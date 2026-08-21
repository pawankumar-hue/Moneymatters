package com.moneymatters.core.designsystem

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * PW Batch Badge Component (Physics Wallah EdTech Tag)
 */
@Composable
fun PwBatchBadge(text: String = "TARGET 2026 BATCH", color: Color = PwElectricBlue) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.2f))
            .border(0.8.dp, color, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            letterSpacing = 0.5.sp
        )
    }
}

/**
 * Instagram Story Ring Circle Component with PW Gradient Accent.
 */
@Composable
fun InstagramStoryRing(
    title: String,
    icon: ImageVector = Icons.Default.Person,
    hasUnseenStory: Boolean = true,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .graphicsLayer {
                    shadowElevation = 8.dp.toPx()
                    shape = RoundedCornerShape(22.dp)
                    clip = false
                }
                .clip(RoundedCornerShape(22.dp))
                .background(
                    if (hasUnseenStory) InstagramGradient
                    else Brush.linearGradient(listOf(InstagramBorderDark, InstagramBorderDark))
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.8f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(2.5.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(InstagramBlack)
                .padding(2.5.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(InstagramElevatedSurface, Color(0xFF0D0D11)),
                            center = Offset(20f, 20f),
                            radius = 100f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            color = TextPrimary,
            maxLines = 1
        )
    }
}

/**
 * PW × Instagram Hybrid Feed Card Architecture.
 */
@Composable
fun InstagramFeedCard(
    username: String,
    userCategory: String,
    title: String,
    likesCount: Int = 1420,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    var isBookmarked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(InstagramBlack)
            .border(0.8.dp, InstagramBorderDark, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
    ) {
        // Header Row: Avatar, Username, PW Batch Tag, Options menu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(InstagramGradient)
                        .padding(1.5.dp)
                        .clip(CircleShape)
                        .background(InstagramBlack)
                        .padding(1.5.dp)
                        .clip(CircleShape)
                        .background(InstagramElevatedSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = username.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = username,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = PwElectricBlue,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = userCategory,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                PwBatchBadge()
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "Options",
                    tint = TextPrimary
                )
            }
        }

        // Post Main Content Body
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(InstagramDarkSurface)
                .clickable(enabled = onClick != null) { onClick?.invoke() }
                .padding(16.dp)
        ) {
            Column {
                if (title.isNotBlank()) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                content()
            }
        }

        // App-Relevant Action Bar — Save Lesson, Share, Open
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                // Save lesson bookmark
                IconButton(
                    onClick = { isBookmarked = !isBookmarked },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save Lesson",
                        tint = if (isBookmarked) PwElectricBlue else TextPrimary
                    )
                }

                // Share module
                IconButton(onClick = { }, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share Module",
                        tint = TextPrimary
                    )
                }
            }

            // XP reward chip on the right
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(PwElectricBlue.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "+${likesCount / 120} XP on completion",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = PwElectricBlue
                )
            }
        }

        Column(modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 12.dp)) {
            Text(
                text = "$likesCount students enrolled this module",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TextPrimary
            )
        }
    }
}

/**
 * Instagram Stories Top Segmented Progress Bar.
 */
@Composable
fun InstagramSegmentedProgressBar(
    totalSegments: Int,
    currentSegmentIndex: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (i in 0 until totalSegments) {
            val isCompleted = i < currentSegmentIndex
            val isCurrent = i == currentSegmentIndex

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted || isCurrent -> PwElectricBlue
                            else -> TextPrimary.copy(alpha = 0.3f)
                        }
                    )
            )
        }
    }
}

/**
 * PW × Instagram Fusion Action Pill Button.
 */
@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradient: List<Color> = listOf(PwElectricBlue, InstagramPink, PwAmberGold),
    icon: ImageVector = Icons.Default.ChevronRight,
    enabled: Boolean = true
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        color = Color.Transparent,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(CircleShape)
            .background(Brush.horizontalGradient(gradient))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = InstagramDarkSurface,
    borderColor: Color = InstagramBorderDark,
    outerRadius: Dp = 16.dp,
    innerRadius: Dp = 12.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    var mod = modifier
        .clip(RoundedCornerShape(outerRadius))
        .background(backgroundColor)
        .border(0.8.dp, borderColor, RoundedCornerShape(outerRadius))

    if (onClick != null) {
        mod = mod.clickable { onClick() }
    }

    Column(
        modifier = mod.padding(innerRadius),
        content = content
    )
}

@Composable
fun EyebrowTag(text: String, color: Color = PwElectricBlue) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f))
            .border(0.8.dp, color.copy(alpha = 0.4f), CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun XPBadge(xp: Int) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(PwEdtechGradient)
            .padding(horizontal = 12.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Default.Star, contentDescription = "XP", tint = Color.White, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "$xp XP", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
fun StreakBadge(streakDays: Int) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(InstagramHeartRed)
            .padding(horizontal = 12.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Default.LocalFireDepartment, contentDescription = "Streak", tint = Color.White, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "$streakDays Days", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String? = null) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        if (subtitle != null) {
            Text(text = subtitle, fontSize = 12.sp, color = TextSecondary)
        }
    }
}
