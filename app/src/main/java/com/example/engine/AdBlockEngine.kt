package com.example.engine

import android.webkit.WebResourceResponse
import java.io.ByteArrayInputStream
import java.net.URI
import java.util.concurrent.atomic.AtomicInteger

object AdBlockEngine {
    private val blockedCount = AtomicInteger(0)

    private val adHosts = hashSetOf(
        "doubleclick.net",
        "googlesyndication.com",
        "adservice.google.com",
        "google-analytics.com",
        "analytics.google.com",
        "popads.net",
        "popcash.net",
        "criteo.com",
        "criteo.net",
        "taboola.com",
        "outbrain.com",
        "adnxs.com",
        "adroll.com",
        "scorecardresearch.com",
        "moatads.com",
        "advertising.com",
        "adform.net",
        "rubiconproject.com",
        "pubmatic.com",
        "bidswitch.net",
        "smartadserver.com",
        "zedo.com",
        "adcolony.com",
        "applovin.com",
        "inmobi.com",
        "chartboost.com",
        "admob.com",
        "exponential.com",
        "revcontent.com",
        "mgid.com",
        "propellerads.com",
        "exoclick.com",
        "trafficjunky.com",
        "adblade.com",
        "adsterra.com",
        "amazon-adsystem.com",
        "ad-delivery.net",
        "adtech.de",
        "casalemedia.com",
        "openx.net",
        "serving-sys.com",
        "yieldmo.com",
        "quantserve.com",
        "hotjar.com",
        "yandex.ru/ads",
        "an.yandex.ru"
    )

    private val adKeywords = listOf(
        "/ads.js",
        "/pagead/",
        "/adsense/",
        "googleads",
        "ad_banner",
        "adserver",
        "/banners/",
        "popunder",
        "tracker.js"
    )

    fun isAdUrl(url: String): Boolean {
        return try {
            val lowercaseUrl = url.lowercase()
            val host = try {
                URI(url).host?.lowercase()
            } catch (_: Exception) {
                null
            } ?: ""

            // Check known hostnames or subdomains
            for (adHost in adHosts) {
                if (host == adHost || host.endsWith(".$adHost") ||
                    lowercaseUrl.contains("://$adHost") ||
                    lowercaseUrl.contains(".$adHost/")
                ) {
                    blockedCount.incrementAndGet()
                    return true
                }
            }

            // Check path keywords
            for (keyword in adKeywords) {
                if (lowercaseUrl.contains(keyword)) {
                    blockedCount.incrementAndGet()
                    return true
                }
            }

            false
        } catch (_: Exception) {
            false
        }
    }

    fun createBlockedResponse(): WebResourceResponse {
        return WebResourceResponse(
            "text/plain",
            "UTF-8",
            ByteArrayInputStream(ByteArray(0))
        )
    }

    fun getBlockedCount(): Int = blockedCount.get()

    fun resetCount() {
        blockedCount.set(0)
    }

    // CSS snippet injected into pages to remove collapsed ad elements and banners
    val elementHidingJs: String = """
        (function() {
            try {
                var css = '.adsbygoogle, [id^="google_ads_"], .ad-container, .advertisement, [class*="ad-banner"], [id*="ad-banner"], .banner-ad, [class*="ad-wrapper"], [id*="ad_slot"], iframe[src*="doubleclick"] { display: none !important; height: 0 !important; visibility: hidden !important; }';
                var style = document.createElement('style');
                style.type = 'text/css';
                style.appendChild(document.createTextNode(css));
                (document.head || document.documentElement).appendChild(style);
            } catch(e) {}
        })();
    """.trimIndent()
}
