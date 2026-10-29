package com.example.viewmodel

import android.app.Application
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebStorage
import android.webkit.WebView
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BrowserPreferences
import com.example.data.SearchEngine
import com.example.data.UserAgentMode
import com.example.data.model.BookmarkEntity
import com.example.data.model.HistoryEntity
import com.example.engine.AdBlockEngine
import com.example.model.BrowserTab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class WebViewCommand {
    data class LoadUrl(val url: String) : WebViewCommand()
    object GoBack : WebViewCommand()
    object GoForward : WebViewCommand()
    object Reload : WebViewCommand()
    object StopLoading : WebViewCommand()
    data class Scroll(val dy: Int) : WebViewCommand()
    data class ClickAt(val x: Float, val y: Float) : WebViewCommand()
}

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

    val prefs = BrowserPreferences(application)
    private val database = AppDatabase.getInstance(application)
    private val bookmarksDao = database.bookmarksDao()
    private val historyDao = database.historyDao()

    val bookmarks = bookmarksDao.getAllBookmarks().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val speedDials = bookmarksDao.getSpeedDials().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val history = historyDao.getAllHistory().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Tabs
    private val initialTab = BrowserTab(url = "")
    private val _tabs = MutableStateFlow<List<BrowserTab>>(listOf(initialTab))
    val tabs: StateFlow<List<BrowserTab>> = _tabs.asStateFlow()

    private val _activeTabId = MutableStateFlow(initialTab.id)
    val activeTabId: StateFlow<String> = _activeTabId.asStateFlow()

    // Fullscreen Video container
    private val _fullscreenCustomView = MutableStateFlow<View?>(null)
    val fullscreenCustomView: StateFlow<View?> = _fullscreenCustomView.asStateFlow()
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null

    // Virtual Mouse State for TV Remotes
    private val _isVirtualMouseEnabled = MutableStateFlow(prefs.isVirtualMouseEnabled)
    val isVirtualMouseEnabled: StateFlow<Boolean> = _isVirtualMouseEnabled.asStateFlow()

    private val _mousePosition = MutableStateFlow(Offset(960f, 540f)) // Center of 1080p TV
    val mousePosition: StateFlow<Offset> = _mousePosition.asStateFlow()

    // WebView commands
    private val _webCommand = MutableSharedFlow<WebViewCommand>(extraBufferCapacity = 8)
    val webCommand: SharedFlow<WebViewCommand> = _webCommand.asSharedFlow()

    // Settings & Live state
    private val _blockedAdsCount = MutableStateFlow(AdBlockEngine.getBlockedCount())
    val blockedAdsCount: StateFlow<Int> = _blockedAdsCount.asStateFlow()

    val activeTab: BrowserTab
        get() = _tabs.value.find { it.id == _activeTabId.value } ?: _tabs.value.first()

    fun updateBlockedCount() {
        _blockedAdsCount.value = AdBlockEngine.getBlockedCount()
    }

    // --- Tab Management ---

    fun openNewTab(url: String = "", isIncognito: Boolean = false) {
        val newTab = BrowserTab(url = url, isIncognito = isIncognito)
        _tabs.value = _tabs.value + newTab
        _activeTabId.value = newTab.id
    }

    fun selectTab(tabId: String) {
        _activeTabId.value = tabId
    }

    fun closeTab(tabId: String) {
        val currentTabs = _tabs.value
        val tabToClose = currentTabs.find { it.id == tabId }

        // If closing an incognito tab, clear session caches as requested
        if (tabToClose?.isIncognito == true) {
            clearIncognitoData()
        }

        if (currentTabs.size <= 1) {
            // Keep at least one tab open
            val freshTab = BrowserTab(url = "")
            _tabs.value = listOf(freshTab)
            _activeTabId.value = freshTab.id
            return
        }

        val newTabs = currentTabs.filter { it.id != tabId }
        _tabs.value = newTabs
        if (_activeTabId.value == tabId) {
            _activeTabId.value = newTabs.last().id
        }
    }

    fun closeAllTabs() {
        val hasIncognito = _tabs.value.any { it.isIncognito }
        if (hasIncognito) {
            clearIncognitoData()
        }
        val freshTab = BrowserTab(url = "")
        _tabs.value = listOf(freshTab)
        _activeTabId.value = freshTab.id
    }

    fun updateActiveTabState(
        url: String? = null,
        title: String? = null,
        canGoBack: Boolean? = null,
        canGoForward: Boolean? = null,
        isLoading: Boolean? = null,
        progress: Int? = null
    ) {
        val currentId = _activeTabId.value
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == currentId) {
                tab.copy(
                    url = url ?: tab.url,
                    title = title ?: tab.title,
                    canGoBack = canGoBack ?: tab.canGoBack,
                    canGoForward = canGoForward ?: tab.canGoForward,
                    isLoading = isLoading ?: tab.isLoading,
                    progress = progress ?: tab.progress
                )
            } else {
                tab
            }
        }

        // Save history if not incognito and page finished loading
        if (url != null && !url.startsWith("data:") && !url.isBlank() && url != "about:blank") {
            val currentTab = activeTab
            if (!currentTab.isIncognito && isLoading == false) {
                viewModelScope.launch(Dispatchers.IO) {
                    historyDao.insert(
                        HistoryEntity(
                            title = title ?: tabTitleFromUrl(url),
                            url = url
                        )
                    )
                }
            }
        }
    }

    fun toggleDesktopMode() {
        val currentId = _activeTabId.value
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == currentId) {
                tab.copy(isDesktopMode = !tab.isDesktopMode)
            } else {
                tab
            }
        }
        _webCommand.tryEmit(WebViewCommand.Reload)
    }

    // --- Navigation ---

    fun loadUrlOrSearch(input: String) {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return

        val finalUrl = when {
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
            else -> prefs.searchEngine.searchUrl + java.net.URLEncoder.encode(trimmed, "UTF-8")
        }

        updateActiveTabState(url = finalUrl, title = tabTitleFromUrl(finalUrl), isLoading = true)
        _webCommand.tryEmit(WebViewCommand.LoadUrl(finalUrl))
    }

    fun goHome() {
        updateActiveTabState(url = "", title = "Yeni Sekme", isLoading = false)
        _webCommand.tryEmit(WebViewCommand.LoadUrl("about:blank"))
    }

    fun goBack() {
        _webCommand.tryEmit(WebViewCommand.GoBack)
    }

    fun goForward() {
        _webCommand.tryEmit(WebViewCommand.GoForward)
    }

    fun reload() {
        _webCommand.tryEmit(WebViewCommand.Reload)
    }

    fun stopLoading() {
        _webCommand.tryEmit(WebViewCommand.StopLoading)
    }

    // --- Virtual Mouse Pointer ---

    fun toggleVirtualMouse() {
        val next = !_isVirtualMouseEnabled.value
        _isVirtualMouseEnabled.value = next
        prefs.isVirtualMouseEnabled = next
    }

    fun moveVirtualMouse(dx: Float, dy: Float, boundsWidth: Float, boundsHeight: Float) {
        val current = _mousePosition.value
        val speed = 28f // Responsive D-pad cursor speed
        val newX = (current.x + dx * speed).coerceIn(0f, boundsWidth.coerceAtLeast(100f))
        val newY = (current.y + dy * speed).coerceIn(0f, boundsHeight.coerceAtLeast(100f))
        _mousePosition.value = Offset(newX, newY)
    }

    fun performVirtualMouseClick() {
        val pos = _mousePosition.value
        _webCommand.tryEmit(WebViewCommand.ClickAt(pos.x, pos.y))
    }

    fun scrollPage(dy: Int) {
        _webCommand.tryEmit(WebViewCommand.Scroll(dy))
    }

    // --- Bookmarks & History ---

    fun toggleBookmarkCurrentPage() {
        val currentTab = activeTab
        if (currentTab.isBlankPage) return

        viewModelScope.launch(Dispatchers.IO) {
            val exists = bookmarksDao.isBookmarked(currentTab.url)
            if (exists) {
                bookmarksDao.deleteByUrl(currentTab.url)
            } else {
                bookmarksDao.insert(
                    BookmarkEntity(
                        title = currentTab.title.ifBlank { tabTitleFromUrl(currentTab.url) },
                        url = currentTab.url,
                        isSpeedDial = false
                    )
                )
            }
        }
    }

    fun addSpeedDial(title: String, url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            bookmarksDao.insert(
                BookmarkEntity(
                    title = title,
                    url = if (!url.startsWith("http")) "https://$url" else url,
                    isSpeedDial = true
                )
            )
        }
    }

    fun deleteBookmark(bookmark: BookmarkEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            bookmarksDao.delete(bookmark)
        }
    }

    fun clearBrowsingHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            historyDao.clearAll()
        }
    }

    fun clearAllBrowsingData(context: android.content.Context) {
        viewModelScope.launch(Dispatchers.IO) {
            historyDao.clearAll()
            withContext(Dispatchers.Main) {
                try {
                    CookieManager.getInstance().removeAllCookies(null)
                    CookieManager.getInstance().flush()
                    WebStorage.getInstance().deleteAllData()
                    WebView(context).clearCache(true)
                } catch (_: Exception) {}
            }
            AdBlockEngine.resetCount()
            updateBlockedCount()
        }
    }

    private fun clearIncognitoData() {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                CookieManager.getInstance().removeSessionCookies(null)
                CookieManager.getInstance().flush()
            } catch (_: Exception) {}
        }
    }

    // --- Fullscreen Video Callbacks ---

    fun onShowCustomView(view: View, callback: WebChromeClient.CustomViewCallback) {
        customViewCallback?.onCustomViewHidden()
        customViewCallback = callback
        _fullscreenCustomView.value = view
    }

    fun onHideCustomView() {
        customViewCallback?.onCustomViewHidden()
        customViewCallback = null
        _fullscreenCustomView.value = null
    }

    private fun tabTitleFromUrl(url: String): String {
        return try {
            val host = android.net.Uri.parse(url).host
            host?.removePrefix("www.") ?: url
        } catch (_: Exception) {
            url
        }
    }
}
