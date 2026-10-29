package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonBlue
import kotlin.math.roundToInt

@Composable
fun VirtualMouseOverlay(
    mousePosition: Offset,
    onMove: (dx: Float, dy: Float, boundsWidth: Float, boundsHeight: Float) -> Unit,
    onClick: () -> Unit,
    onScroll: (dy: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        // Render Animated High-Visibility Cursor
        val cursorOffsetX = mousePosition.x.coerceIn(0f, widthPx)
        val cursorOffsetY = mousePosition.y.coerceIn(0f, heightPx)

        Box(
            modifier = Modifier
                .offset { IntOffset(cursorOffsetX.roundToInt(), cursorOffsetY.roundToInt()) }
                .size(32.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(22f, 22f)
                    lineTo(13f, 24f)
                    lineTo(19f, 36f)
                    lineTo(13f, 38f)
                    lineTo(7f, 26f)
                    lineTo(0f, 32f)
                    close()
                }

                // Cyan Glow Shadow
                drawPath(
                    path = path,
                    color = CyanGlow,
                    style = Stroke(width = 6f)
                )
                // White fill
                drawPath(
                    path = path,
                    color = Color.White,
                    style = Fill
                )
                // Dark edge
                drawPath(
                    path = path,
                    color = Color(0xFF0F172A),
                    style = Stroke(width = 2f)
                )
            }
        }

        // TV Remote On-Screen Precision Control Pad (Docked in Bottom Right)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .shadow(12.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = DarkSurface.copy(alpha = 0.92f),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Sanal Fare",
                    color = CyanGlow,
                    fontSize = 11.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )

                // D-Pad Cross
                // UP
                MouseControlButton(
                    icon = Icons.Default.KeyboardArrowUp,
                    desc = "Yukarı",
                    onClick = { onMove(0f, -2f, widthPx, heightPx) }
                )

                // LEFT - CLICK - RIGHT
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MouseControlButton(
                        icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        desc = "Sol",
                        onClick = { onMove(-2f, 0f, widthPx, heightPx) }
                    )

                    // OK / CLICK CENTER
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(NeonBlue)
                            .clickable { onClick() }
                            .focusable()
                            .testTag("virtual_mouse_click_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = "Tıkla",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    MouseControlButton(
                        icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        desc = "Sağ",
                        onClick = { onMove(2f, 0f, widthPx, heightPx) }
                    )
                }

                // DOWN
                MouseControlButton(
                    icon = Icons.Default.KeyboardArrowDown,
                    desc = "Aşağı",
                    onClick = { onMove(0f, 2f, widthPx, heightPx) }
                )

                // Page Scroll Buttons
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PageScrollButton(
                        icon = Icons.Default.ArrowUpward,
                        label = "Kaydır",
                        onClick = { onScroll(-350) }
                    )
                    PageScrollButton(
                        icon = Icons.Default.ArrowDownward,
                        label = "Kaydır",
                        onClick = { onScroll(350) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MouseControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    desc: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF242C3D))
            .clickable { onClick() }
            .focusable(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = desc,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun PageScrollButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .focusable(),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF1E2638)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = CyanGlow,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
