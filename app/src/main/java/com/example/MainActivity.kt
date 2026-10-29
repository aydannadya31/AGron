package com.example

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.components.BookmarksHistoryDialog
import com.example.ui.components.NewTabPage
import com.example.ui.components.SettingsDialog
import com.example.ui.components.TabsDrawer
import com.example.ui.components.TvTopBar
import com.example.ui.components.TvWebView
import com.example.ui.components.VirtualMouseOverlay
import com.example.ui.theme.AGronTheme
import com.example.ui.theme.CinemaBlack
import com.example.viewmodel.BrowserViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BrowserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme = viewModel.prefs.isDarkTheme

            AGronTheme(darkTheme = isDarkTheme) {
                MainBrowserScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainBrowserScreen(viewModel: BrowserViewModel) {
    val context = LocalContext.current
    val tabs by viewModel.tabs.collectAsState()
    val activeTabId by viewModel.activeTabId.collectAsState()
    val fullscreenView by viewModel.fullscreenCustomView.collectAsState()
    val isVirtualMouseActive by viewModel.isVirtualMouseEnabled.collectAsState()
    val mousePosition by viewModel.mousePosition.collectAsState()
    val blockedAdsCount by viewModel.blockedAdsCount.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val speedDials by viewModel.speedDials.collectAsState()
    val history by viewModel.history.collectAsState()

    val activeTab = remember(tabs, activeTabId) {
        tabs.find { it.id == activeTabId } ?: tabs.first()
    }

    val isCurrentUrlBookmarked = remember(bookmarks, activeTab.url) {
        bookmarks.any { it.url == activeTab.url }
    }

    // Modal dialogs state
    var showTabsDrawer by remember { mutableStateOf(false) }
    var showBookmarksDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Voice search launcher
    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val query = matches?.firstOrNull()
            if (!query.isNullOrBlank()) {
                viewModel.loadUrlOrSearch(query)
            }
        }
    }

    fun launchVoiceSearch() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
            )
            putExtra(RecognizerIntent.EXTRA_PROMPT, "AGron Sesli Arama: Aramak istediğiniz şeyi söyleyin...")
        }
        try {
            voiceLauncher.launch(intent)
        } catch (_: Exception) {
            Toast.makeText(
                context,
                "Bu cihazda ses tanıma servisi bulunamadı. Lütfen arama kutusuna yazınız.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // TV Remote Back Button navigation handler
    BackHandler {
        when {
            fullscreenView != null -> {
                viewModel.onHideCustomView()
            }
            showTabsDrawer -> showTabsDrawer = false
            showBookmarksDialog -> showBookmarksDialog = false
            showSettingsDialog -> showSettingsDialog = false
            activeTab.canGoBack -> {
                viewModel.goBack()
            }
            !activeTab.isBlankPage -> {
                viewModel.goHome()
            }
            else -> {
                (context as? Activity)?.finish()
            }
        }
    }

    // If HTML5 video requests fullscreen, show full-bleed video view
    if (fullscreenView != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AndroidView(
                factory = {
                    FrameLayout(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        (fullscreenView?.parent as? ViewGroup)?.removeView(fullscreenView)
                        addView(fullscreenView)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        return
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val screenWidthPx = with(density) { maxWidth.toPx() }
        val screenHeightPx = with(density) { maxHeight.toPx() }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(CinemaBlack)
                .onKeyEvent { keyEvent ->
                    if (keyEvent.type == KeyEventType.KeyDown) {
                        when (keyEvent.key) {
                            Key.Menu -> {
                                showSettingsDialog = true
                                true
                            }
                            Key.PageUp -> {
                                viewModel.scrollPage(-400)
                                true
                            }
                            Key.PageDown -> {
                                viewModel.scrollPage(400)
                                true
                            }
                            // Virtual mouse D-pad navigation when mouse is active
                            Key.DirectionUp -> {
                                if (isVirtualMouseActive) {
                                    viewModel.moveVirtualMouse(0f, -1.5f, screenWidthPx, screenHeightPx)
                                    true
                                } else false
                            }
                            Key.DirectionDown -> {
                                if (isVirtualMouseActive) {
                                    viewModel.moveVirtualMouse(0f, 1.5f, screenWidthPx, screenHeightPx)
                                    true
                                } else false
                            }
                            Key.DirectionLeft -> {
                                if (isVirtualMouseActive) {
                                    viewModel.moveVirtualMouse(-1.5f, 0f, screenWidthPx, screenHeightPx)
                                    true
                                } else false
                            }
                            Key.DirectionRight -> {
                                if (isVirtualMouseActive) {
                                    viewModel.moveVirtualMouse(1.5f, 0f, screenWidthPx, screenHeightPx)
                                    true
                                } else false
                            }
                            Key.DirectionCenter, Key.Enter -> {
                                if (isVirtualMouseActive) {
                                    viewModel.performVirtualMouseClick()
                                    true
                                } else false
                            }
                            else -> false
                        }
                    } else false
                },
            containerColor = CinemaBlack,
            topBar = {
                TvTopBar(
                    activeTab = activeTab,
                    tabCount = tabs.size,
                    blockedAdsCount = blockedAdsCount,
                    isBookmarked = isCurrentUrlBookmarked,
                    isVirtualMouseActive = isVirtualMouseActive,
                    onUrlSubmit = { viewModel.loadUrlOrSearch(it) },
                    onGoBack = { viewModel.goBack() },
                    onGoForward = { viewModel.goForward() },
                    onRefresh = {
                        if (activeTab.isLoading) viewModel.stopLoading() else viewModel.reload()
                    },
                    onHome = { viewModel.goHome() },
                    onVoiceSearch = { launchVoiceSearch() },
                    onToggleBookmark = { viewModel.toggleBookmarkCurrentPage() },
                    onToggleVirtualMouse = { viewModel.toggleVirtualMouse() },
                    onToggleDesktopMode = { viewModel.toggleDesktopMode() },
                    onOpenTabs = { showTabsDrawer = true },
                    onOpenBookmarks = { showBookmarksDialog = true },
                    onOpenSettings = { showSettingsDialog = true }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (activeTab.isBlankPage) {
                    NewTabPage(
                        speedDials = speedDials,
                        onSelectSpeedDial = { viewModel.loadUrlOrSearch(it) },
                        onVoiceSearch = { launchVoiceSearch() },
                        onSearchSubmit = { viewModel.loadUrlOrSearch(it) },
                        onAddSpeedDial = { title, url -> viewModel.addSpeedDial(title, url) }
                    )
                } else {
                    TvWebView(
                        tab = activeTab,
                        viewModel = viewModel
                    )
                }

                // Virtual Mouse overlay cursor and remote control pad
                if (isVirtualMouseActive) {
                    VirtualMouseOverlay(
                        mousePosition = mousePosition,
                        onMove = { dx, dy, w, h -> viewModel.moveVirtualMouse(dx, dy, w, h) },
                        onClick = { viewModel.performVirtualMouseClick() },
                        onScroll = { viewModel.scrollPage(it) }
                    )
                }
            }
        }

        // Modals
        if (showTabsDrawer) {
            TabsDrawer(
                tabs = tabs,
                activeTabId = activeTabId,
                onSelectTab = {
                    viewModel.selectTab(it)
                    showTabsDrawer = false
                },
                onCloseTab = { viewModel.closeTab(it) },
                onNewTab = {
                    viewModel.openNewTab()
                    showTabsDrawer = false
                },
                onNewIncognitoTab = {
                    viewModel.openNewTab(isIncognito = true)
                    showTabsDrawer = false
                },
                onCloseAllTabs = {
                    viewModel.closeAllTabs()
                    showTabsDrawer = false
                },
                onDismiss = { showTabsDrawer = false }
            )
        }

        if (showBookmarksDialog) {
            BookmarksHistoryDialog(
                bookmarks = bookmarks,
                history = history,
                onOpenUrl = { viewModel.loadUrlOrSearch(it) },
                onDeleteBookmark = { viewModel.deleteBookmark(it) },
                onClearHistory = { viewModel.clearBrowsingHistory() },
                onDismiss = { showBookmarksDialog = false }
            )
        }

        if (showSettingsDialog) {
            SettingsDialog(
                prefs = viewModel.prefs,
                blockedAdsCount = blockedAdsCount,
                onClearAllData = { viewModel.clearAllBrowsingData(context) },
                onDismiss = { showSettingsDialog = false }
            )
        }
    }
}
