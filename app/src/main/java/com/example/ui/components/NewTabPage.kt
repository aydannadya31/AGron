package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.BookmarkEntity
import com.example.ui.theme.CinemaBlack
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun NewTabPage(
    speedDials: List<BookmarkEntity>,
    onSelectSpeedDial: (String) -> Unit,
    onVoiceSearch: () -> Unit,
    onSearchSubmit: (String) -> Unit,
    onAddSpeedDial: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        CinemaBlack,
                        Color(0xFF0F1420),
                        CinemaBlack
                    )
                )
            )
    ) {
        // Background Artwork using the user's provided logo
        Image(
            painter = painterResource(id = R.drawable.agron_logo),
            contentDescription = "AGron Arka Plan Logosu",
            modifier = Modifier
                .align(Alignment.Center)
                .size(440.dp)
                .alpha(0.08f),
            contentScale = ContentScale.Fit
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Brand and Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Surface(
                    modifier = Modifier.size(54.dp),
                    shape = CircleShape,
                    color = DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonBlue)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.agron_logo),
                        contentDescription = "AGron Amblem",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "AGron",
                        color = TextPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Android TV Hızlı Web Tarayıcısı",
                        color = CyanGlow,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.width(32.dp))

                // Feature pills
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FeaturePill(icon = Icons.Default.Shield, label = "AdBlock Aktif", color = EmeraldGreen)
                    FeaturePill(icon = Icons.Default.Speed, label = "Turbo Motor", color = NeonBlue)
                    FeaturePill(icon = Icons.Default.Tv, label = "10-Foot TV", color = CyanGlow)
                }
            }

            // Central TV Search Card
            TvSearchInputCard(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSubmit = { onSearchSubmit(searchQuery) },
                onVoiceSearch = onVoiceSearch,
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .padding(bottom = 28.dp)
            )

            // Speed Dials Title & Add Button
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(bottom = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Hızlı Erişim & Sık Kullanılanlar",
                    color = TextSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showAddDialog = true }
                        .focusable()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Hızlı Erişim Ekle",
                        tint = CyanGlow,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Site Ekle",
                        color = CyanGlow,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Speed Dials Grid (Large, 10-foot TV readable cards)
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(speedDials) { item ->
                    SpeedDialCard(
                        title = item.title,
                        url = item.url,
                        onClick = { onSelectSpeedDial(item.url) }
                    )
                }
            }

            // Remote Control Tips
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface.copy(alpha = 0.7f),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    RemoteTip(keyText = "▲ ▼ ◀ ▶", descText = "Kumanda ile Gezin")
                    RemoteTip(keyText = "OK / Seç", descText = "Aç / Tıkla")
                    RemoteTip(keyText = "Fare İkonu", descText = "Sanal Mouse Modu")
                    RemoteTip(keyText = "Geri Tuşu", descText = "Önceki Sayfa")
                }
            }
        }
    }

    if (showAddDialog) {
        AddSpeedDialDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, url ->
                onAddSpeedDial(title, url)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun TvSearchInputCard(
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onVoiceSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(29.dp))
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) NeonBlue else DarkBorder,
                shape = RoundedCornerShape(29.dp)
            ),
        color = DarkSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = if (isFocused) NeonBlue else TextSecondary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { isFocused = it.isFocused }
                    .testTag("home_search_input"),
                placeholder = {
                    Text(
                        text = "Google'da arayın veya web sitesi adresi girin...",
                        color = Color(0xFF94A3B8),
                        fontSize = 15.sp
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            // Voice Search Action
            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable { onVoiceSearch() }
                    .focusable()
                    .testTag("home_voice_search_button"),
                color = if (isFocused) NeonBlue else DarkSurfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Sesli Arama",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SpeedDialCard(
    title: String,
    url: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .height(96.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) NeonBlue else DarkBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .focusable()
            .onFocusChanged { isFocused = it.isFocused }
            .testTag("speed_dial_${title.lowercase()}"),
        color = if (isFocused) DarkSurfaceVariant else DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = if (isFocused) NeonBlue else Color(0xFF1E293B)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val initial = title.firstOrNull()?.uppercaseChar()?.toString() ?: "W"
                    Text(
                        text = initial,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                color = if (isFocused) Color.White else TextPrimary,
                fontSize = 13.sp,
                fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FeaturePill(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun RemoteTip(keyText: String, descText: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFF263248)
        ) {
            Text(
                text = keyText,
                color = CyanGlow,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = descText,
            color = TextSecondary,
            fontSize = 11.sp
        )
    }
}

@Composable
fun AddSpeedDialDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Hızlı Erişim Ekle", color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Site Başlığı (örn: Twitter)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Web Adresi (örn: twitter.com)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && url.isNotBlank()) {
                        onConfirm(title, url)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
            ) {
                Text("Ekle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", color = TextSecondary)
            }
        },
        containerColor = DarkSurface
    )
}
