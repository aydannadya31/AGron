package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BrowserPreferences
import com.example.data.SearchEngine
import com.example.data.UserAgentMode
import com.example.ui.theme.CinemaBlack
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsDialog(
    prefs: BrowserPreferences,
    blockedAdsCount: Int,
    onClearAllData: () -> Unit,
    onDismiss: () -> Unit
) {
    var searchEngine by remember { mutableStateOf(prefs.searchEngine) }
    var userAgentMode by remember { mutableStateOf(prefs.userAgentMode) }
    var adBlockEnabled by remember { mutableStateOf(prefs.isAdBlockEnabled) }
    var popupsBlocked by remember { mutableStateOf(prefs.isPopupsBlocked) }
    var safeBrowsing by remember { mutableStateOf(prefs.isSafeBrowsingEnabled) }
    var javascriptEnabled by remember { mutableStateOf(prefs.isJavaScriptEnabled) }
    var darkTheme by remember { mutableStateOf(prefs.isDarkTheme) }
    var textZoom by remember { mutableStateOf(prefs.textZoom) }

    var showClearConfirmation by remember { mutableStateOf(false) }
    var clearSuccessMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = NeonBlue,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "AGron Tarayıcı Ayarları",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section 1: Arama ve Görünüm
                SectionHeader("Arama Motoru ve Görünüm")

                // Search engine selector
                SettingSelectorRow(
                    icon = Icons.Default.Search,
                    title = "Varsayılan Arama Motoru",
                    subtitle = searchEngine.title,
                    options = SearchEngine.values().map { it.title },
                    selectedIndex = SearchEngine.values().indexOf(searchEngine),
                    onSelect = { idx ->
                        searchEngine = SearchEngine.values()[idx]
                        prefs.searchEngine = searchEngine
                    }
                )

                // User agent selector
                SettingSelectorRow(
                    icon = Icons.Default.DesktopWindows,
                    title = "Varsayılan Sayfa Modu",
                    subtitle = userAgentMode.title,
                    options = UserAgentMode.values().map { it.title },
                    selectedIndex = UserAgentMode.values().indexOf(userAgentMode),
                    onSelect = { idx ->
                        userAgentMode = UserAgentMode.values()[idx]
                        prefs.userAgentMode = userAgentMode
                    }
                )

                // Text zoom selector for TV
                SettingSelectorRow(
                    icon = Icons.Default.FormatSize,
                    title = "TV Metin Yakınlaştırma",
                    subtitle = "%$textZoom",
                    options = listOf("%100", "%110", "%125", "%150"),
                    selectedIndex = when (textZoom) {
                        100 -> 0
                        110 -> 1
                        125 -> 2
                        else -> 3
                    },
                    onSelect = { idx ->
                        textZoom = when (idx) {
                            0 -> 100
                            1 -> 110
                            2 -> 125
                            else -> 150
                        }
                        prefs.textZoom = textZoom
                    }
                )

                // Dark Mode Switch
                SettingToggleRow(
                    icon = Icons.Default.Brightness4,
                    title = "Karanlık Tema (Sinema Modu)",
                    subtitle = "TV panelleri için göz yormayan derin antrasit tema",
                    isChecked = darkTheme,
                    onCheckedChange = {
                        darkTheme = it
                        prefs.isDarkTheme = it
                    }
                )

                HorizontalDivider(color = DarkBorder, thickness = 1.dp)

                // Section 2: Güvenlik ve Reklam Engelleyici
                SectionHeader("Güvenlik ve Reklam Engelleyici")

                // AdBlock Switch with live counter
                SettingToggleRow(
                    icon = Icons.Default.Shield,
                    title = "Entegre Reklam Engelleyici",
                    subtitle = "Web sitelerindeki pop-up ve reklamları bloke eder (Engellenen: $blockedAdsCount)",
                    isChecked = adBlockEnabled,
                    accentColor = EmeraldGreen,
                    onCheckedChange = {
                        adBlockEnabled = it
                        prefs.isAdBlockEnabled = it
                    }
                )

                // Popup Blocker
                SettingToggleRow(
                    icon = Icons.Default.Block,
                    title = "Açılır Pencere (Pop-up) Koruması",
                    subtitle = "İstenmeyen yeni pencerelerin açılmasını önler",
                    isChecked = popupsBlocked,
                    onCheckedChange = {
                        popupsBlocked = it
                        prefs.isPopupsBlocked = it
                    }
                )

                // Safe browsing
                SettingToggleRow(
                    icon = Icons.Default.Security,
                    title = "Güvenli Gezinti",
                    subtitle = "Zararlı ve sahte sitelere karşı otomatik koruma",
                    isChecked = safeBrowsing,
                    onCheckedChange = {
                        safeBrowsing = it
                        prefs.isSafeBrowsingEnabled = it
                    }
                )

                // JavaScript
                SettingToggleRow(
                    icon = Icons.Default.Code,
                    title = "JavaScript Desteği",
                    subtitle = "Modern interaktif siteler ve video oynatıcılar için gereklidir",
                    isChecked = javascriptEnabled,
                    onCheckedChange = {
                        javascriptEnabled = it
                        prefs.isJavaScriptEnabled = it
                    }
                )

                HorizontalDivider(color = DarkBorder, thickness = 1.dp)

                // Section 3: Veri Temizleme
                SectionHeader("Gizlilik ve Veri Temizliği")

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showClearConfirmation = true }
                        .focusable(),
                    color = CoralRed.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CoralRed.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = null,
                            tint = CoralRed,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Tüm Gezinti Verilerini Temizle",
                                color = CoralRed,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Çerezler, önbellek, web depolama alanı ve geçmiş tamamen silinir.",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                if (clearSuccessMessage != null) {
                    Text(
                        text = clearSuccessMessage ?: "",
                        color = EmeraldGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
            ) {
                Text("Tamam")
            }
        },
        containerColor = CinemaBlack,
        modifier = Modifier.fillMaxWidth(0.85f)
    )

    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearConfirmation = false },
            title = { Text("Verileri Temizle?", color = TextPrimary) },
            text = {
                Text(
                    "Tüm web geçmişiniz, kayıtlı çerezler ve geçici tarayıcı önbelleği silinecek. Bu işlem geri alınamaz.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllData()
                        showClearConfirmation = false
                        clearSuccessMessage = "Gezinti verileri başarıyla temizlendi."
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralRed)
                ) {
                    Text("Evet, Temizle")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmation = false }) {
                    Text("İptal", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        color = CyanGlow,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
    )
}

@Composable
fun SettingToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: Color = NeonBlue
) {
    var isFocused by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) NeonBlue else DarkBorder,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onCheckedChange(!isChecked) }
            .focusable()
            .onFocusChanged { isFocused = it.isFocused },
        color = if (isFocused) DarkSurfaceVariant else DarkSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isChecked) accentColor else TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = accentColor,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color(0xFF1E293B)
                )
            )
        }
    }
}

@Composable
fun SettingSelectorRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) NeonBlue else DarkBorder,
                shape = RoundedCornerShape(10.dp)
            )
            .focusable()
            .onFocusChanged { isFocused = it.isFocused },
        color = if (isFocused) DarkSurfaceVariant else DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NeonBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Seçili: $subtitle",
                        color = CyanGlow,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                options.forEachIndexed { index, option ->
                    val isSelected = index == selectedIndex
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onSelect(index) }
                            .focusable(),
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) NeonBlue else Color(0xFF1E2638),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) CyanGlow else DarkBorder
                        )
                    ) {
                        Text(
                            text = option,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(vertical = 6.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
