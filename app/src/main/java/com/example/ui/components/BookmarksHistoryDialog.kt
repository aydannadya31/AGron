package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.BookmarkEntity
import com.example.data.model.HistoryEntity
import com.example.ui.theme.CinemaBlack
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BookmarksHistoryDialog(
    bookmarks: List<BookmarkEntity>,
    history: List<HistoryEntity>,
    onOpenUrl: (String) -> Unit,
    onDeleteBookmark: (BookmarkEntity) -> Unit,
    onClearHistory: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = NeonBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = NeonBlue
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Yer İmleri (${bookmarks.size})", fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Geçmiş (${history.size})", fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
            ) {
                if (selectedTab == 0) {
                    // Bookmarks View
                    if (bookmarks.isEmpty()) {
                        EmptyListNotice("Henüz kayıtlı yer imi yok. Web gezinirken yıldız butonuna basarak ekleyebilirsiniz.")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(bookmarks) { item ->
                                BookmarkListItem(
                                    item = item,
                                    onOpen = { onOpenUrl(item.url); onDismiss() },
                                    onDelete = { onDeleteBookmark(item) }
                                )
                            }
                        }
                    }
                } else {
                    // History View with Quick Clear button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Son Gezinti Geçmişi",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )

                        if (history.isNotEmpty()) {
                            Button(
                                onClick = onClearHistory,
                                colors = ButtonDefaults.buttonColors(containerColor = CoralRed.copy(alpha = 0.2f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CoralRed),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = CoralRed, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Geçmişi Hızlıca Temizle", color = CoralRed, fontSize = 12.sp)
                            }
                        }
                    }

                    if (history.isEmpty()) {
                        EmptyListNotice("Gezinti geçmişi boş.")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(history) { item ->
                                HistoryListItem(
                                    item = item,
                                    onOpen = { onOpenUrl(item.url); onDismiss() }
                                )
                            }
                        }
                    }
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
        modifier = Modifier.fillMaxWidth(0.85f)
    )
}

@Composable
fun BookmarkListItem(
    item: BookmarkEntity,
    onOpen: () -> Unit,
    onDelete: () -> Unit
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
            .clickable(onClick = onOpen)
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
                imageVector = Icons.Default.Bookmark,
                contentDescription = null,
                tint = CyanGlow,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.url,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Sil",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun HistoryListItem(
    item: HistoryEntity,
    onOpen: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val formattedDate = remember(item.timestamp) {
        SimpleDateFormat("HH:mm - dd.MM", Locale.getDefault()).format(Date(item.timestamp))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) NeonBlue else DarkBorder,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onOpen)
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
                imageVector = Icons.Default.OpenInBrowser,
                contentDescription = null,
                tint = NeonBlue,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.url,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = formattedDate,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun EmptyListNotice(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = TextSecondary,
            fontSize = 13.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(24.dp)
        )
    }
}
