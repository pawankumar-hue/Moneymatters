package com.moneymatters.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ThreeDIconBadge — Agency-level 3D Icon & Emoji Enclosure.
 * Renders flat icons/emojis with 3D bevel depth, radial specular highlights, and drop shadows.
 */
@Composable
fun ThreeDIconBadge(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    emoji: String? = null,
    badgeSize: Dp = 44.dp,
    iconSize: Dp = 22.dp,
    shape: Shape = RoundedCornerShape(14.dp),
    primaryColor: Color = PwElectricBlue,
    secondaryColor: Color = InstagramPink,
    iconTint: Color = Color.White,
    elevation: Dp = 6.dp
) {
    Box(
        modifier = modifier
            .size(badgeSize)
            .graphicsLayer {
                shadowElevation = elevation.toPx()
                this.shape = shape
                clip = false
            }
            .clip(shape)
            // 1. 3D Outer Bevel Shell
            .background(
                Brush.verticalGradient(
                    listOf(
                        primaryColor,
                        secondaryColor,
                        primaryColor.copy(alpha = 0.8f)
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.85f),
                        Color.White.copy(alpha = 0.2f),
                        Color.Transparent
                    )
                ),
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        // 2. 3D Specular Highlight Core Box
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.5.dp)
                .clip(shape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.9f),
                            Color(0xFF0F0F14)
                        ),
                        center = Offset(20f, 20f),
                        radius = 90f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (emoji != null) {
                Text(
                    text = emoji,
                    fontSize = (badgeSize.value * 0.45f).sp,
                    modifier = Modifier.graphicsLayer {
                        translationY = -1f
                    }
                )
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}
