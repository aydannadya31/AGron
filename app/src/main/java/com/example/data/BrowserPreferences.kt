package com.example.data

import android.content.Context
import android.content.SharedPreferences

enum class SearchEngine(val title: String, val searchUrl: String) {
    GOOGLE("Google", "https://www.google.com/search?q="),
    YANDEX("Yandex", "https://yandex.com.tr/search/?text="),
    DUCKDUCKGO("DuckDuckGo", "https://duckduckgo.com/?q="),
    BING("Bing", "https://www.bing.com/search?q=")
}

enum class UserAgentMode(val title: String) {
    TV("Android TV"),
    DESKTOP("Masaüstü (Chrome)"),
    MOBILE("Mobil")
}

class BrowserPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("agron_prefs", Context.MODE_PRIVATE)

    var searchEngine: SearchEngine
        get() {
            val name = prefs.getString("search_engine", SearchEngine.GOOGLE.name)
            return try {
                SearchEngine.valueOf(name ?: SearchEngine.GOOGLE.name)
            } catch (_: Exception) {
                SearchEngine.GOOGLE
            }
        }
        set(value) = prefs.edit().putString("search_engine", value.name).apply()

    var userAgentMode: UserAgentMode
        get() {
            val name = prefs.getString("user_agent_mode", UserAgentMode.TV.name)
            return try {
                UserAgentMode.valueOf(name ?: UserAgentMode.TV.name)
            } catch (_: Exception) {
                UserAgentMode.TV
            }
        }
        set(value) = prefs.edit().putString("user_agent_mode", value.name).apply()

    var isAdBlockEnabled: Boolean
        get() = prefs.getBoolean("ad_block_enabled", true)
        set(value) = prefs.edit().putBoolean("ad_block_enabled", value).apply()

    var isPopupsBlocked: Boolean
        get() = prefs.getBoolean("popups_blocked", true)
        set(value) = prefs.edit().putBoolean("popups_blocked", value).apply()

    var isJavaScriptEnabled: Boolean
        get() = prefs.getBoolean("javascript_enabled", true)
        set(value) = prefs.edit().putBoolean("javascript_enabled", value).apply()

    var isDarkTheme: Boolean
        get() = prefs.getBoolean("dark_theme", true)
        set(value) = prefs.edit().putBoolean("dark_theme", value).apply()

    var textZoom: Int
        get() = prefs.getInt("text_zoom", 110)
        set(value) = prefs.edit().putInt("text_zoom", value).apply()

    var isSafeBrowsingEnabled: Boolean
        get() = prefs.getBoolean("safe_browsing_enabled", true)
        set(value) = prefs.edit().putBoolean("safe_browsing_enabled", value).apply()

    var isVirtualMouseEnabled: Boolean
        get() = prefs.getBoolean("virtual_mouse_enabled", false)
        set(value) = prefs.edit().putBoolean("virtual_mouse_enabled", value).apply()

    companion object {
        const val DESKTOP_USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36"
        const val TV_USER_AGENT =
            "Mozilla/5.0 (Linux; Android 14; BRAVIA 4K Build/URT1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36 CrKey/1.56.500000"
    }
}
