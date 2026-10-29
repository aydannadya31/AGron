package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrowserTab
import com.example.ui.theme.CinemaBlack
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TabsDrawer(
    tabs: List<BrowserTab>,
    activeTabId: String,
    onSelectTab: (String) -> Unit,
    onCloseTab: (String) -> Unit,
    onNewTab: () -> Unit,
    onNewIncognitoTab: () -> Unit,
    onCloseAllTabs: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Açık Sekmeler (${tabs.size})",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onNewTab,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Yeni Sekme", fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = onNewIncognitoTab,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanGlow)
                    ) {
                        Icon(Icons.Default.VisibilityOff, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Gizli Sekme", fontSize = 13.sp)
                    }

                    IconButton(onClick = onCloseAllTabs) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Tümünü Kapat",
                            tint = CoralRed
                        )
                    }
                }
            }
        },
        text = {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(tabs) { tab ->
                    TabThumbnailCard(
                        tab = tab,
                        isActive = tab.id == activeTabId,
                        onSelect = { onSelectTab(tab.id) },
                        onClose = { onCloseTab(tab.id) }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant)
            ) {
                Text("Kapat")
            }
        },
        containerColor = CinemaBlack,
        modifier = Modifier.fillMaxWidth(0.9f)
    )
}

@Composable
fun TabThumbnailCard(
    tab: BrowserTab,
    isActive: Boolean,
    onSelect: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .width(220.dp)
            .height(130.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (isFocused || isActive) 2.dp else 1.dp,
                color = when {
                    isActive -> NeonBlue
                    isFocused -> CyanGlow
                    else -> DarkBorder
                },
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onSelect)
            .focusable()
            .onFocusChanged { isFocused = it.isFocused }
            .testTag("tab_item_${tab.id}"),
        color = if (tab.isIncognito) Color(0xFF1B1B2B) else DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (tab.isIncognito) Icons.Default.VisibilityOff else Icons.Default.Web,
                        contentDescription = null,
                        tint = if (tab.isIncognito) CyanGlow else NeonBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (tab.isIncognito) "Gizli Sekme" else "Web Sekmesi",
                        color = if (tab.isIncognito) CyanGlow else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Sekmeyi Kapat",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = tab.title.ifBlank { "Yeni Sekme" },
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (tab.isBlankPage) "Ana Sayfa" else tab.url,
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
