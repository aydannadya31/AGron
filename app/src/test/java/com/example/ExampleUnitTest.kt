package com.example

import com.example.engine.AdBlockEngine
import com.example.model.BrowserTab
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testAdBlockEngineDetection() {
        assertTrue(AdBlockEngine.isAdUrl("https://doubleclick.net/ad.js"))
        assertTrue(AdBlockEngine.isAdUrl("https://pagead2.googlesyndication.com/pagead/show_ads.js"))
        assertTrue(AdBlockEngine.isAdUrl("https://popads.net/serve"))
        assertFalse(AdBlockEngine.isAdUrl("https://www.google.com/search?q=kotlin"))
        assertFalse(AdBlockEngine.isAdUrl("https://tr.wikipedia.org/wiki/Ana_Sayfa"))
    }

    @Test
    fun testBrowserTabBlankPage() {
        val blankTab = BrowserTab(url = "")
        assertTrue(blankTab.isBlankPage)

        val homeTab = BrowserTab(url = "about:blank")
        assertTrue(homeTab.isBlankPage)

        val webTab = BrowserTab(url = "https://www.google.com")
        assertFalse(webTab.isBlankPage)
    }

    @Test
    fun testTabInitialState() {
        val tab = BrowserTab(title = "Yeni Sekme", isIncognito = true)
        assertTrue(tab.isIncognito)
        assertEquals("Yeni Sekme", tab.title)
        assertFalse(tab.canGoBack)
        assertFalse(tab.canGoForward)
    }
}
