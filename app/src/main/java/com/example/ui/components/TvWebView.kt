package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.BrowserPreferences
import com.example.engine.AdBlockEngine
import com.example.model.BrowserTab
import com.example.viewmodel.BrowserViewModel
import com.example.viewmodel.WebViewCommand
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TvWebView(
    tab: BrowserTab,
    viewModel: BrowserViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val webView = remember(tab.id) {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            isFocusable = true
            isFocusableInTouchMode = true

            settings.apply {
                javaScriptEnabled = viewModel.prefs.isJavaScriptEnabled
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
                displayZoomControls = false
                textZoom = viewModel.prefs.textZoom
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                mediaPlaybackRequiresUserGesture = false

                // Safe browsing & popups
                safeBrowsingEnabled = viewModel.prefs.isSafeBrowsingEnabled
                javaScriptCanOpenWindowsAutomatically = !viewModel.prefs.isPopupsBlocked

                // Incognito vs standard cache
                if (tab.isIncognito) {
                    cacheMode = WebSettings.LOAD_NO_CACHE
                } else {
                    cacheMode = WebSettings.LOAD_DEFAULT
                }

                // Chrome User Agent for TV vs Desktop
                userAgentString = if (tab.isDesktopMode) {
                    BrowserPreferences.DESKTOP_USER_AGENT
                } else {
                    BrowserPreferences.TV_USER_AGENT
                }
            }

            if (tab.isIncognito) {
                CookieManager.getInstance().setAcceptThirdPartyCookies(this, false)
            } else {
                CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
            }
        }
    }

    // Attach custom clients
    LaunchedEffect(webView, tab.id) {
        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                if (viewModel.prefs.isAdBlockEnabled && request != null) {
                    val url = request.url.toString()
                    if (AdBlockEngine.isAdUrl(url)) {
                        viewModel.updateBlockedCount()
                        return AdBlockEngine.createBlockedResponse()
                    }
                }
                return super.shouldInterceptRequest(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                viewModel.updateActiveTabState(
                    url = url,
                    isLoading = true,
                    canGoBack = view?.canGoBack() ?: false,
                    canGoForward = view?.canGoForward() ?: false
                )
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Element hiding injection for visual ad banner cleanup
                if (viewModel.prefs.isAdBlockEnabled) {
                    view?.evaluateJavascript(AdBlockEngine.elementHidingJs, null)
                }

                viewModel.updateActiveTabState(
                    url = url,
                    title = view?.title,
                    isLoading = false,
                    canGoBack = view?.canGoBack() ?: false,
                    canGoForward = view?.canGoForward() ?: false,
                    progress = 100
                )
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                viewModel.updateActiveTabState(
                    progress = newProgress,
                    isLoading = newProgress < 100
                )
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                if (!title.isNullOrBlank()) {
                    viewModel.updateActiveTabState(title = title)
                }
            }

            // HTML5 Fullscreen Video Support
            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                if (view != null && callback != null) {
                    viewModel.onShowCustomView(view, callback)
                }
            }

            override fun onHideCustomView() {
                viewModel.onHideCustomView()
            }
        }
    }

    // Handle initial URL load if tab has URL
    LaunchedEffect(tab.url) {
        if (tab.url.isNotBlank() && tab.url != "about:blank" && tab.url != webView.url) {
            webView.loadUrl(tab.url)
        }
    }

    // Command listener
    LaunchedEffect(viewModel.webCommand) {
        viewModel.webCommand.collectLatest { command ->
            when (command) {
                is WebViewCommand.LoadUrl -> webView.loadUrl(command.url)
                is WebViewCommand.GoBack -> if (webView.canGoBack()) webView.goBack()
                is WebViewCommand.GoForward -> if (webView.canGoForward()) webView.goForward()
                is WebViewCommand.Reload -> webView.reload()
                is WebViewCommand.StopLoading -> webView.stopLoading()
                is WebViewCommand.Scroll -> webView.scrollBy(0, command.dy)
                is WebViewCommand.ClickAt -> {
                    val downTime = SystemClock.uptimeMillis()
                    val eventTime = SystemClock.uptimeMillis() + 50
                    val downEvent = MotionEvent.obtain(
                        downTime,
                        eventTime,
                        MotionEvent.ACTION_DOWN,
                        command.x,
                        command.y,
                        0
                    )
                    val upEvent = MotionEvent.obtain(
                        downTime,
                        eventTime + 50,
                        MotionEvent.ACTION_UP,
                        command.x,
                        command.y,
                        0
                    )
                    webView.dispatchTouchEvent(downEvent)
                    webView.dispatchTouchEvent(upEvent)
                    downEvent.recycle()
                    upEvent.recycle()
                }
            }
        }
    }

    DisposableEffect(tab.id) {
        onDispose {
            if (tab.isIncognito) {
                webView.clearCache(true)
                webView.clearHistory()
                webView.clearFormData()
            }
            webView.destroy()
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color.White)) {
        AndroidView(
            factory = { webView },
            modifier = Modifier.fillMaxSize()
        )
    }
}
