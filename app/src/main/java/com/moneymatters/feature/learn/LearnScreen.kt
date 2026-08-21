package com.moneymatters.feature.learn

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import com.moneymatters.core.designsystem.*
import com.moneymatters.core.i18n.AppLanguageManager

// Per-module accent colors
private val moduleColors = listOf(
    Color(0xFF10B981), Color(0xFF3B82F6), Color(0xFFF59E0B), Color(0xFFEC4899),
    Color(0xFF8B5CF6), Color(0xFF06B6D4), Color(0xFFEF4444), Color(0xFF84CC16),
    Color(0xFFF97316), Color(0xFF6366F1), Color(0xFF14B8A6), Color(0xFFFBBF24),
    Color(0xFF22C55E), Color(0xFFE879F9), Color(0xFF38BDF8), Color(0xFFFF7043),
    Color(0xFF4CAF50), Color(0xFFAB47BC), Color(0xFF26C6DA), Color(0xFFFFB300),
    Color(0xFF7C3AED), Color(0xFF059669), Color(0xFF1D4ED8)
)

private val moduleEmojis = listOf(
    "💰", "📊", "🏦", "💳", "📈", "🏛️", "🛡️", "🧾",
    "🏠", "💸", "🆘", "🎯", "📉", "₿", "🔥", "⚡",
    "🎪", "🧠", "🏅", "🌱", "📋", "🏆", "📜"
)

fun getModuleColor(moduleId: Int): Color =
    moduleColors.getOrElse(moduleId - 1) { PwElectricBlue }

fun getModuleEmoji(moduleId: Int): String =
    moduleEmojis.getOrElse(moduleId - 1) { "📚" }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    onModuleClick: (Int) -> Unit,
    viewModel: LearnViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val langCode = uiState.selectedLanguage
    var searchQuery by remember { mutableStateOf("") }

    val filteredModules = remember(uiState.modules, uiState.selectedCategory, searchQuery) {
        uiState.modules.filter { mod ->
            (uiState.selectedCategory == null || mod.category == uiState.selectedCategory) &&
            (searchQuery.isEmpty() || mod.title.contains(searchQuery, ignoreCase = true))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(InstagramBlack)
    ) {
        // HEADER WITH GRADIENT TYPOGRAPHY (no infinite transition — saves continuous recomposition)
        val headerTextGradient = remember {
            Brush.linearGradient(
                colors = listOf(
                    Color(0xFF38BDF8), // Cyan
                    Color(0xFF3B82F6), // Electric Blue
                    Color(0xFF8B5CF6), // Purple
                    Color(0xFFEC4899), // Pink
                    Color(0xFFFBBF24), // Amber Gold
                    Color(0xFF10B981)  // Emerald
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(InstagramBlack)
                .padding(top = 14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = AppLanguageManager.getString("financial_journey", langCode),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        style = TextStyle(
                            brush = headerTextGradient,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "🗺️", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(InstagramDarkSurface)
                    .border(0.8.dp, InstagramBorderDark, RoundedCornerShape(14.dp))
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(AppLanguageManager.getString("search_placeholder", langCode), color = TextSecondary, fontSize = 12.sp)
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Outlined.Search, contentDescription = null, tint = TextSecondary)
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3D Category filter chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp)
            ) {
                item {
                    CategoryChip3D(
                        selected = uiState.selectedCategory == null,
                        onClick = { viewModel.selectCategory(null) },
                        label = AppLanguageManager.getString("all_modules", langCode),
                        emoji = "✨"
                    )
                }
                items(uiState.categories, key = { it }) { cat ->
                    CategoryChip3D(
                        selected = uiState.selectedCategory == cat,
                        onClick = { viewModel.selectCategory(cat) },
                        label = cat,
                        emoji = getCategoryEmoji(cat)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = InstagramBorderDark)
        }


        // 2-COLUMN MODULE CARD GRID — 3D Website-Style with Staggered Entrance
        // Estimated cards per module: readTimeMinutes / 2 (avg 2 min per card)
        @OptIn(ExperimentalFoundationApi::class)
        CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
            itemsIndexed(filteredModules, key = { _, mod -> mod.id }) { index, mod ->
                val isCompleted = uiState.userProfile.completedModuleIds.contains(mod.id)
                val accentColor = remember(mod.id) { getModuleColor(mod.id) }
                val emoji = remember(mod.id) { getModuleEmoji(mod.id) }
                // Determine lock state: first module always unlocked, then sequential unlock
                val isLocked = mod.id > 1 && !uiState.userProfile.completedModuleIds.contains(mod.id - 1) && !isCompleted
                val cardCount = remember(mod.readTimeMinutes) { maxOf(4, mod.readTimeMinutes / 2) }
                val progressPercent = if (isCompleted) 100 else 0

                WebsiteModuleCard(
                    moduleId = mod.id,
                    title = mod.title,
                    category = mod.category,
                    cardCount = cardCount,
                    readTimeMinutes = mod.readTimeMinutes,
                    xpReward = mod.xpReward,
                    isCompleted = isCompleted,
                    isLocked = isLocked,
                    progressPercent = progressPercent,
                    progress = if (isCompleted) 1f else 0f,
                    accentColor = accentColor,
                    emoji = emoji,
                    entranceIndex = index,
                    langCode = langCode,
                    onClick = { if (!isLocked) onModuleClick(mod.id) }
                )
            }
        }
    }
}


}

/**
 * WebsiteModuleCard — 3D Double-Bezel Module Card
 * Mirrors the reference website's ModuleCard component with:
 * - 3 states: Locked (grayscale+lock icon), In-Progress (accent% badge), Completed (green Done badge)
 * - Staggered entrance animation (entranceIndex * 60ms delay)
 * - Spring tap scale feedback (press = 0.94f scale, release = 1f)
 * - Animated SVG-style progress arc ring (Canvas drawArc with sweep gradient)
 * - Footer: 📖 N Cards | 🕐 N min matching reference website exactly
 * - 3D outer bevel gradient shell with inner specular surface
 */
@Composable
fun WebsiteModuleCard(
    moduleId: Int,
    title: String,
    category: String,
    cardCount: Int,
    readTimeMinutes: Int,
    xpReward: Int,
    isCompleted: Boolean,
    isLocked: Boolean,
    progressPercent: Int,
    progress: Float,
    accentColor: Color,
    emoji: String,
    entranceIndex: Int = 0,
    langCode: String = "en",
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val springScale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessHigh),
        label = "spring_scale_$moduleId"
    )

    // ── Pre-cached Brushes (GPU shader objects) ──
    val effectiveAccent = if (isLocked) Color(0xFF52525B) else accentColor
    val cardBackground = remember(isLocked) {
        if (isLocked)
            Brush.verticalGradient(listOf(Color(0xFF111115), Color(0xFF0A0A0D)))
        else
            Brush.verticalGradient(listOf(Color(0xFF1E1E26), Color(0xFF111118)))
    }
    val bezelGradient = remember(isLocked) {
        Brush.verticalGradient(
            listOf(
                if (isLocked) Color.White.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.18f),
                Color.Transparent
            )
        )
    }
    val sweepGradient = remember(effectiveAccent) {
        Brush.sweepGradient(listOf(effectiveAccent.copy(alpha = 0.4f), effectiveAccent, effectiveAccent.copy(alpha = 0.4f)))
    }
    val cardShape = remember { RoundedCornerShape(20.dp) }
    val innerShape = remember { RoundedCornerShape(18.dp) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = springScale
                scaleY = springScale
                shadowElevation = if (isLocked) 2.dp.toPx() else 6.dp.toPx()
                shape = cardShape
                clip = false
            }
    ) {
        // ── Outer 3D Bevel Shell ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(cardBackground)
                .border(width = 1.2.dp, brush = bezelGradient, shape = cardShape)
                .padding(1.5.dp)
                .clip(innerShape)
                // Ambient glow blob (top-right)
                .drawBehind {
                    if (!isLocked) {
                        drawCircle(
                            color = effectiveAccent.copy(alpha = 0.10f),
                            radius = size.width * 0.85f,
                            center = center.copy(x = size.width, y = 0f)
                        )
                        drawCircle(
                            color = effectiveAccent.copy(alpha = 0.04f),
                            radius = size.width * 0.6f,
                            center = center.copy(x = 0f, y = size.height)
                        )
                    }
                }
                .background(if (isLocked) Color(0xFF0C0C0F) else Color(0xFF13131A))
                .pointerInput(isLocked, onClick) {
                    detectTapGestures(
                        onPress = {
                            if (!isLocked) {
                                isPressed = true
                                tryAwaitRelease()
                                isPressed = false
                            }
                        },
                        onTap = { onClick() }
                    )
                }
                .then(if (isLocked) Modifier.graphicsLayer { alpha = 0.45f } else Modifier)
                .padding(14.dp)
        ) {
            Column {
                // ── TOP ROW: Progress Ring + Emoji | State Badge ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Progress Ring with Emoji Inside
                    Box(
                        modifier = Modifier.size(54.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 2.8.dp.toPx()
                            val pad = strokeWidth / 2 + 3.dp.toPx()
                            val diam = size.minDimension - pad * 2
                            // Track ring
                            drawArc(
                                color = Color.White.copy(alpha = if (isLocked) 0.03f else 0.06f),
                                startAngle = -90f, sweepAngle = 360f, useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                                topLeft = androidx.compose.ui.geometry.Offset(pad, pad),
                                size = androidx.compose.ui.geometry.Size(diam, diam)
                            )
                            // Progress arc
                            if (progress > 0f) {
                                drawArc(
                                    brush = sweepGradient,
                                    startAngle = -90f, sweepAngle = 360f * progress, useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                                    topLeft = androidx.compose.ui.geometry.Offset(pad, pad),
                                    size = androidx.compose.ui.geometry.Size(diam, diam)
                                )
                            }
                        }
                        // Emoji tile inside ring
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isLocked) Color.White.copy(alpha = 0.03f)
                                    else effectiveAccent.copy(alpha = 0.14f)
                                )
                                .border(
                                    1.dp,
                                    if (isLocked) Color.White.copy(alpha = 0.06f)
                                    else effectiveAccent.copy(alpha = 0.25f),
                                    RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLocked) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFF52525B),
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(text = emoji, fontSize = 17.sp)
                            }
                        }
                    }

                    // State Badge — Done / X% / Locked
                    when {
                        isCompleted -> {
                            // ✅ DONE badge
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(PwEmeraldGreen.copy(alpha = 0.15f))
                                    .border(1.dp, PwEmeraldGreen.copy(alpha = 0.4f), CircleShape)
                                    .padding(horizontal = 7.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = PwEmeraldGreen,
                                        modifier = Modifier.size(9.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "DONE",
                                        fontSize = 8.sp, fontWeight = FontWeight.Black,
                                        color = PwEmeraldGreen, letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }
                        isLocked -> {
                            // 🔒 LOCKED badge
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.04f))
                                    .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
                                    .padding(horizontal = 7.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color(0xFF71717A),
                                        modifier = Modifier.size(9.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "LOCKED",
                                        fontSize = 8.sp, fontWeight = FontWeight.Black,
                                        color = Color(0xFF71717A), letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }
                        progressPercent > 0 -> {
                            // 🟡 IN PROGRESS badge
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(PwAmberGold.copy(alpha = 0.12f))
                                    .border(1.dp, PwAmberGold.copy(alpha = 0.35f), CircleShape)
                                    .padding(horizontal = 7.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$progressPercent%",
                                    fontSize = 8.sp, fontWeight = FontWeight.Black,
                                    color = PwAmberGold, letterSpacing = 0.5.sp
                                )
                            }
                        }
                        else -> {}
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ── MODULE ID Label ──
                Text(
                    text = "MODULE $moduleId",
                    fontSize = 8.sp, fontWeight = FontWeight.Black,
                    color = if (isLocked) Color(0xFF52525B) else effectiveAccent,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(3.dp))

                // ── Title ──
                Text(
                    text = title.removePrefix("Module $moduleId: "),
                    fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                    color = if (isLocked) Color(0xFF52525B) else TextPrimary,
                    letterSpacing = (-0.2).sp,
                    lineHeight = 17.sp,
                    maxLines = 2, overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                // ── Category ──
                Text(
                    text = category,
                    fontSize = 10.sp,
                    color = if (isLocked) Color(0xFF3F3F46) else TextSecondary,
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )

            }
        }
    }
}


// ─── 3D Category Filter Chip Composable ──────────────────────────────────────
fun getCategoryEmoji(category: String?): String = when (category) {
    "Foundations" -> "🧱"
    "Budgeting & Saving" -> "📊"
    "Banking & Credit" -> "🏛️"
    "Investing & Markets" -> "📈"
    "Tax & Govt Schemes" -> "📝"
    "Real World & Career" -> "💼"
    else -> "✨"
}

@Composable
fun CategoryChip3D(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    emoji: String
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessHigh),
        label = "chip_scale"
    )

    val chipShape = remember { RoundedCornerShape(22.dp) }
    val activeGradient = remember {
        Brush.horizontalGradient(listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6)))
    }
    val activeBorder = remember {
        Brush.horizontalGradient(listOf(Color(0xFF93C5FD), Color(0xFFC4B5FD)))
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(chipShape)
            .background(
                if (selected) activeGradient
                else Brush.verticalGradient(listOf(Color(0xFF1C1C2A), Color(0xFF12121E)))
            )
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                brush = if (selected) activeBorder else Brush.verticalGradient(listOf(Color.White.copy(0.12f), Color.Transparent)),
                shape = chipShape
            )
            .drawBehind {
                if (selected) {
                    drawCircle(
                        color = Color(0xFF8B5CF6).copy(alpha = 0.35f),
                        radius = size.width * 0.75f,
                        center = center
                    )
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            }
            .padding(horizontal = 14.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Black else FontWeight.Medium,
                color = if (selected) Color.White else TextSecondary,
                maxLines = 1
            )
        }
    }
}

