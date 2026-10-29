package com.example.model

import java.util.UUID

data class BrowserTab(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Yeni Sekme",
    val url: String = "",
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val isIncognito: Boolean = false,
    val isDesktopMode: Boolean = false
) {
    val isBlankPage: Boolean
        get() = url.isBlank() || url == "about:blank" || url == "agron://home"
}
