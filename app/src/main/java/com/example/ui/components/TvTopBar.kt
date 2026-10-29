package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrowserTab
import com.example.ui.theme.CinemaBlack
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonBlue

@Composable
fun TvTopBar(
    activeTab: BrowserTab,
    tabCount: Int,
    blockedAdsCount: Int,
    isBookmarked: Boolean,
    isVirtualMouseActive: Boolean,
    onUrlSubmit: (String) -> Unit,
    onGoBack: () -> Unit,
    onGoForward: () -> Unit,
    onRefresh: () -> Unit,
    onHome: () -> Unit,
    onVoiceSearch: () -> Unit,
    onToggleBookmark: () -> Unit,
    onToggleVirtualMouse: () -> Unit,
    onToggleDesktopMode: () -> Unit,
    onOpenTabs: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var urlText by remember(activeTab.url) {
        mutableStateOf(if (activeTab.isBlankPage) "" else activeTab.url)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CinemaBlack)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Home Button
            TvIconButton(
                icon = Icons.Default.Home,
                contentDescription = "Ana Sayfa",
                testTag = "home_button",
                onClick = onHome
            )

            // Back Button
            TvIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Geri",
                enabled = activeTab.canGoBack,
                testTag = "back_button",
                onClick = onGoBack
            )

            // Forward Button
            TvIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "İleri",
                enabled = activeTab.canGoForward,
                testTag = "forward_button",
                onClick = onGoForward
            )

            // Refresh / Stop Button
            TvIconButton(
                icon = if (activeTab.isLoading) Icons.Default.Close else Icons.Default.Refresh,
                contentDescription = if (activeTab.isLoading) "Durdur" else "Yenile",
                testTag = "refresh_button",
                onClick = onRefresh
            )

            // URL & Search Box (Central Address Bar)
            TvAddressBar(
                urlText = urlText,
                onUrlChange = { urlText = it },
                onSubmit = { onUrlSubmit(urlText) },
                isIncognito = activeTab.isIncognito,
                onVoiceSearch = onVoiceSearch,
                modifier = Modifier.weight(1f)
            )

            // AdBlock Shield Indicator with live counter
            TvBadgeIconButton(
                icon = Icons.Default.Security,
                contentDescription = "Reklam Engelleyici",
                badgeText = if (blockedAdsCount > 0) blockedAdsCount.toString() else null,
                badgeColor = EmeraldGreen,
                tint = if (blockedAdsCount > 0) EmeraldGreen else Color.Gray,
                testTag = "adblock_shield_button",
                onClick = onOpenSettings
            )

            // Virtual Mouse Toggle Button
            TvIconButton(
                icon = Icons.Default.Mouse,
                contentDescription = "Sanal Fare Modu",
                highlighted = isVirtualMouseActive,
                testTag = "virtual_mouse_button",
                onClick = onToggleVirtualMouse
            )

            // Desktop / TV Mode Toggle
            TvIconButton(
                icon = if (activeTab.isDesktopMode) Icons.Default.Computer else Icons.Default.Tv,
                contentDescription = if (activeTab.isDesktopMode) "Masaüstü Modu" else "TV Modu",
                highlighted = activeTab.isDesktopMode,
                testTag = "desktop_mode_button",
                onClick = onToggleDesktopMode
            )

            // Bookmark Toggle
            TvIconButton(
                icon = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                contentDescription = "Yer İmi",
                highlighted = isBookmarked,
                testTag = "bookmark_toggle_button",
                onClick = onToggleBookmark
            )

            // Bookmarks & History List
            TvIconButton(
                icon = Icons.Default.BookmarkBorder,
                contentDescription = "Yer İmleri ve Geçmiş",
                testTag = "bookmarks_dialog_button",
                onClick = onOpenBookmarks
            )

            // Tabs Button
            TvBadgeIconButton(
                icon = Icons.Default.Tab,
                contentDescription = "Sekmeler",
                badgeText = tabCount.toString(),
                badgeColor = NeonBlue,
                testTag = "tabs_button",
                onClick = onOpenTabs
            )

            // Settings Button
            TvIconButton(
                icon = Icons.Default.Settings,
                contentDescription = "Ayarlar",
                testTag = "settings_button",
                onClick = onOpenSettings
            )
        }

        // Web Page Loading Progress Bar
        AnimatedVisibility(visible = activeTab.isLoading) {
            LinearProgressIndicator(
                progress = { activeTab.progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .padding(top = 4.dp),
                color = CyanGlow,
                trackColor = DarkSurfaceVariant
            )
        }
    }
}

@Composable
fun TvAddressBar(
    urlText: String,
    onUrlChange: (String) -> Unit,
    onSubmit: () -> Unit,
    isIncognito: Boolean,
    onVoiceSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) NeonBlue else DarkBorder,
                shape = RoundedCornerShape(24.dp)
            ),
        color = if (isIncognito) Color(0xFF1E1B2E) else DarkSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isIncognito) {
                Icon(
                    imageVector = Icons.Default.VisibilityOff,
                    contentDescription = "Gizli Mod",
                    tint = CyanGlow,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            BasicTextField(
                value = urlText,
                onValueChange = onUrlChange,
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { isFocused = it.isFocused }
                    .testTag("url_input_field"),
                singleLine = true,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal
                ),
                cursorBrush = SolidColor(NeonBlue),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = { onSubmit() }),
                decorationBox = { innerTextField ->
                    if (urlText.isEmpty()) {
                        Text(
                            text = if (isIncognito) "Gizli arama veya web adresi..." else "Arama yapın veya web adresi girin (örn: google.com)...",
                            color = Color(0xFF94A3B8),
                            fontSize = 14.sp
                        )
                    }
                    innerTextField()
                }
            )

            // Voice Search Button inside the address bar
            IconButton(
                onClick = onVoiceSearch,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("voice_search_bar_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Sesli Arama",
                    tint = if (isFocused) CyanGlow else Color.White
                )
            }
        }
    }
}

@Composable
fun TvIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    highlighted: Boolean = false,
    testTag: String = ""
) {
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    isFocused -> NeonBlue.copy(alpha = 0.25f)
                    highlighted -> DarkSurfaceVariant
                    else -> Color.Transparent
                }
            )
            .border(
                width = if (isFocused) 2.dp else if (highlighted) 1.dp else 0.dp,
                color = if (isFocused) NeonBlue else if (highlighted) CyanGlow else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .focusable(enabled = enabled)
            .onFocusChanged { isFocused = it.isFocused }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = when {
                !enabled -> Color.DarkGray
                isFocused -> Color.White
                highlighted -> CyanGlow
                else -> Color(0xFFCBD5E1)
            },
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun TvBadgeIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    badgeText: String?,
    badgeColor: Color,
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFFCBD5E1),
    testTag: String = ""
) {
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isFocused) NeonBlue.copy(alpha = 0.25f) else Color.Transparent)
            .border(
                width = if (isFocused) 2.dp else 0.dp,
                color = if (isFocused) NeonBlue else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .focusable()
            .onFocusChanged { isFocused = it.isFocused }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        BadgedBox(
            badge = {
                if (!badgeText.isNullOrEmpty()) {
                    Badge(
                        containerColor = badgeColor,
                        contentColor = Color.White
                    ) {
                        Text(text = badgeText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (isFocused) Color.White else tint,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
