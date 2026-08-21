package com.moneymatters.feature.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneymatters.R
import com.moneymatters.core.audio.SoundManager
import com.moneymatters.core.designsystem.*
import com.moneymatters.core.i18n.AppLanguageManager
import com.moneymatters.data.model.ModuleSummary
import com.moneymatters.feature.learn.getModuleColor
import com.moneymatters.feature.learn.getModuleEmoji

@Composable
fun HomeScreen(
    onNavigateToModule: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val langCode = uiState.userProfile.selectedLanguageCode
    var isMuted by remember { mutableStateOf(SoundManager.isMuted()) }

    val quotes = remember {
        listOf(
            "Invest your money, secure your future! 🚀",
            "Every rupee is a soldier — deploy it wisely! ⚔️",
            "Understand the magic of compounding and grow wealthy! ✨",
            "Avoiding debt = the first step to financial freedom 🛡️",
            "Save consistently, set up auto-debits! 🐷"
        )
    }
    val todayQuote = remember(uiState.streakDays) {
        quotes[uiState.streakDays % quotes.size]
    }

    // Next uncompleted active module for "Continue Learning" spot
    val activeModule = remember(uiState.modules, uiState.completedModuleIds) {
        uiState.modules.firstOrNull { !uiState.completedModuleIds.contains(it.id) } ?: uiState.modules.firstOrNull()
    }

    val healthScore = remember(uiState.completedModuleIds, uiState.totalXp, uiState.streakDays) {
        val moduleScore = (uiState.completedModuleIds.size.toFloat() / 23f) * 60f
        val xpScore = (uiState.totalXp.toFloat() / 1000f) * 25f
        val streakScore = (uiState.streakDays.toFloat() / 30f) * 15f
        (moduleScore + xpScore + streakScore).toInt().coerceIn(10, 100)
    }

    @OptIn(ExperimentalFoundationApi::class)
    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(InstagramBlack),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
        // ╔══════════════════════════════════════════════════════════════╗
        // ║  1. NAVBAR HEADER — Redesigned Motion Typography Title Bar  ║
        // ╚══════════════════════════════════════════════════════════════╝
        item {
            val textGradient = remember {
                Brush.linearGradient(
                    colors = listOf(
                        Color.White,
                        PwElectricBlue,
                        PwEmeraldGreen
                    )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        renderEffect = null
                    }
                    .background(InstagramBlack)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Animated Animated Typography Title (No Dot)
                    Text(
                        text = AppLanguageManager.getString("app_name", langCode),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        style = TextStyle(
                            brush = textGradient,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.8).sp
                        )
                    )

                    // Sleek Streak & XP Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            PwAmberGold.copy(alpha = 0.2f),
                                            InstagramPink.copy(alpha = 0.2f)
                                        )
                                    )
                                )
                                .border(
                                    1.dp,
                                    Brush.horizontalGradient(listOf(PwAmberGold, InstagramPink)),
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🔥", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${uiState.userProfile.streakDays}d",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PwAmberGold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(PwEmeraldGreen.copy(alpha = 0.18f))
                                .border(1.dp, PwEmeraldGreen.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "⚡", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${uiState.userProfile.xp} XP",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PwEmeraldGreen
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(color = InstagramBorderDark.copy(alpha = 0.6f))
            }
        }

        // ╔══════════════════════════════════════════════════════════════╗
        // ║  2. HERO GREETING PANEL — 3D Double-Bezel Architecture     ║
        // ╚══════════════════════════════════════════════════════════════╝
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .graphicsLayer {
                        shadowElevation = 12.dp.toPx()
                        shape = RoundedCornerShape(24.dp)
                        clip = false
                    }
                    .clip(RoundedCornerShape(24.dp))
                    // Outer Bevel Shell
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF2C2C34),
                                Color(0xFF18181F),
                                Color(0xFF101014)
                            )
                        )
                    )
                    .border(
                        width = 1.2.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.25f),
                                Color.White.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(2.5.dp)
                    .clip(RoundedCornerShape(22.dp))
                    // Inner Specular Refraction Core
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF1E1E28),
                                Color(0xFF12121A)
                            ),
                            center = Offset(40f, 40f),
                            radius = 400f
                        )
                    )
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(PwEmeraldGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "WELCOME BACK 👋",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = PwEmeraldGreen,
                                letterSpacing = 1.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Champion! 🔥",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            style = TextStyle(
                                brush = Brush.horizontalGradient(
                                    listOf(Color.White, Color(0xFFE2E8F0))
                                ),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.6).sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // 3D Financial Health Gauge Badge Tile
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .graphicsLayer {
                                shadowElevation = 10.dp.toPx()
                                shape = RoundedCornerShape(18.dp)
                                clip = false
                            }
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        PwEmeraldGreen.copy(alpha = 0.25f),
                                        Color(0xFF064E3B).copy(alpha = 0.6f)
                                    )
                                )
                            )
                            .border(
                                width = 1.2.dp,
                                brush = Brush.verticalGradient(
                                    listOf(
                                        PwEmeraldGreen,
                                        PwEmeraldGreen.copy(alpha = 0.3f)
                                    )
                                ),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .padding(2.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFF065F46), Color(0xFF022C22)),
                                    center = Offset(20f, 20f),
                                    radius = 120f
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$healthScore",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = PwEmeraldGreen
                            )
                            Text(
                                text = if (healthScore >= 70) "Fit 💪" else "Growing 🚀",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // ╔══════════════════════════════════════════════════════════════╗
        // ║  4. 4 STAT CARDS GRID (2x2)                                 ║
        // ╚══════════════════════════════════════════════════════════════╝
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WebsiteStatCard(
                        emoji = "💰",
                        label = "TOTAL XP",
                        value = "${uiState.totalXp} XP",
                        accent = PwAmberGold,
                        sub = if (uiState.totalXp >= 200) "Big saver energy 🚀" else "Keep earning! ⚡",
                        modifier = Modifier.weight(1f)
                    )
                    WebsiteStatCard(
                        emoji = "🔥",
                        label = "STREAK",
                        value = "${uiState.streakDays} days",
                        accent = Color(0xFFEF4444),
                        sub = "Keep it going! 💪",
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WebsiteStatCard(
                        emoji = "📚",
                        label = "MODULES",
                        value = "${uiState.completedModuleIds.size}/23",
                        accent = PwEmeraldGreen,
                        sub = "${((uiState.completedModuleIds.size.toFloat() / 23f) * 100).toInt()}% completed",
                        modifier = Modifier.weight(1f)
                    )
                    WebsiteStatCard(
                        emoji = "🏆",
                        label = "LEVEL",
                        value = "Lvl ${uiState.userLevel}",
                        accent = PwElectricBlue,
                        sub = uiState.levelTitle,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // ╔══════════════════════════════════════════════════════════════╗
        // ║  5. CONTINUE LEARNING SPOTLIGHT CARD                        ║
        // ╚══════════════════════════════════════════════════════════════╝
        if (activeModule != null) {
            item {
                val activeAccent = remember(activeModule.id) { getModuleColor(activeModule.id) }
                val activeEmoji = remember(activeModule.id) { getModuleEmoji(activeModule.id) }
                val isCompleted = uiState.completedModuleIds.contains(activeModule.id)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .graphicsLayer { clip = true }
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1E1E1E), Color(0xFF141414))
                            )
                        )
                        .border(1.dp, activeAccent.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                        .clickable {
                            SoundManager.playNodeTapSound()
                            onNavigateToModule(activeModule.id)
                        }
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ThreeDIconBadge(
                                    emoji = activeEmoji,
                                    badgeSize = 48.dp,
                                    primaryColor = activeAccent,
                                    secondaryColor = activeAccent.copy(alpha = 0.6f),
                                    elevation = 8.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "CONTINUE LEARNING",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = activeAccent,
                                        letterSpacing = 1.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = activeModule.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LinearProgressIndicator(
                                progress = { if (isCompleted) 1f else 0.15f },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(CircleShape),
                                color = activeAccent,
                                trackColor = InstagramElevatedSurface
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = {
                                    SoundManager.playNodeTapSound()
                                    onNavigateToModule(activeModule.id)
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = activeAccent),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text(
                                    text = if (isCompleted) "Review" else "Start",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ╔══════════════════════════════════════════════════════════════╗
        // ║  6. GAMIFIED ZIG-ZAG ROAD LEARNING MAP                      ║
        // ╚══════════════════════════════════════════════════════════════╝
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Static glow — no infinite transition needed for a small status dot
                val animatedGlowAlpha = 0.7f

                val progressPercentage = remember(uiState.completedModuleIds.size, uiState.modules.size) {
                    if (uiState.modules.isNotEmpty()) {
                        (uiState.completedModuleIds.size.toFloat() / uiState.modules.size.toFloat() * 100).toInt()
                    } else 0
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "FINANCIAL ROAD MAP",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                style = TextStyle(
                                    brush = Brush.horizontalGradient(
                                        listOf(
                                            Color.White,
                                            PwEmeraldGreen,
                                            PwElectricBlue
                                        )
                                    ),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = (-0.4).sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "🗺️", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(PwEmeraldGreen.copy(alpha = animatedGlowAlpha))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${uiState.completedModuleIds.size} of 23 Modules Mastered",
                                fontSize = 12.sp,
                                color = PwEmeraldGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                }


                Spacer(modifier = Modifier.height(10.dp))

                // Smooth Linear Progress Bar Track
                val animatedProgress by animateFloatAsState(
                    targetValue = if (uiState.modules.isNotEmpty()) uiState.completedModuleIds.size.toFloat() / uiState.modules.size.toFloat() else 0f,
                    animationSpec = tween(1000, easing = FastOutSlowInEasing),
                    label = "ProgressBarAnimation"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(InstagramBorderDark)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(PwEmeraldGreen, PwElectricBlue)
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Gamified Winding Road Component
                ZigZagRoadMap(
                    modules = uiState.modules,
                    completedIds = uiState.completedModuleIds,
                    onNodeClick = { mod ->
                        SoundManager.playNodeTapSound()
                        onNavigateToModule(mod.id)
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
}

@Composable
fun ZigZagRoadMap(
    modules: List<ModuleSummary>,
    completedIds: Set<Int>,
    onNodeClick: (ModuleSummary) -> Unit
) {
    var selectedModuleId by remember { mutableStateOf<Int?>(null) }

    // ── 3. AMBIENT ANIMATIONS (Slow, Living & Calm - 60FPS Performance) ──
    // Static ambient values — eliminates 3 infinite transition coroutines that cause continuous recomposition
    val activeGlowScale = 1.04f
    val activeGlowAlpha = 0.22f
    val particleDriftY = 0f

    val nodeOffsets = remember {
        listOf(
            0.5f,   // Module 1 (Center)
            0.35f,  // Module 2 (Left)
            0.28f,  // Module 3 (Far Left)
            0.42f,  // Module 4 (Center-Left)
            0.68f,  // Module 5 (Right)
            0.72f,  // Module 6 (Far Right)
            0.62f,  // Module 7 (Center-Right)
            0.38f,  // Module 8 (Left)
            0.28f,  // Module 9 (Far Left)
            0.5f,   // Module 10 (Center)
            0.72f,  // Module 11 (Far Right)
            0.65f,  // Module 12 (Right)
            0.42f,  // Module 13 (Center-Left)
            0.28f,  // Module 14 (Far Left)
            0.5f,   // Module 15 (Center)
            0.72f,  // Module 16 (Right)
            0.62f,  // Module 17 (Center-Right)
            0.35f,  // Module 18 (Left)
            0.5f,   // Module 19 (Center)
            0.68f,  // Module 20 (Right)
            0.5f,   // Module 21 (Center)
            0.35f,  // Module 22 (Left)
            0.5f    // Module 23 (Center Finish)
        )
    }

    // ── 1. COLOR & TONE: Mid-dark tone range (#12141A -> #1C1F2A) with Gradient Border ──
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF12141A),
                        Color(0xFF171A24),
                        Color(0xFF1C1F2A)
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        Color(0xFF38BDF8).copy(alpha = 0.35f),
                        Color(0xFF10B981).copy(alpha = 0.45f),
                        Color(0xFFF59E0B).copy(alpha = 0.30f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(vertical = 24.dp, horizontal = 8.dp)
    ) {
        // ── 4. TECHNICAL IMPLEMENTATION: VectorDrawable SVG Base Layer ──
        Image(
            painter = painterResource(id = R.drawable.roadmap_vector_bg),
            contentDescription = "Gamified Level Path SVG Vector Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        // Layer Vignette Overlay for Contrast & Readability
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF12141A).copy(alpha = 0.50f),
                            Color(0xFF171A24).copy(alpha = 0.30f),
                            Color(0xFF1C1F2A).copy(alpha = 0.60f)
                        )
                    )
                )
        )

        // ── 2 & 4. DYNAMIC CANVAS LAYER (Floating Bokeh, Active Gold Glow, Floating Path Glow) ──
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height((modules.size * 90).dp)
        ) {
            val canvasWidth = size.width
            val rowHeight = 90.dp.toPx()

            // Layer A: Bokeh Light Particles Slow Drift (5-10% Opacity)
            val driftPx = particleDriftY.dp.toPx()
            drawCircle(
                color = Color(0xFF38BDF8).copy(alpha = 0.08f),
                radius = 65.dp.toPx(),
                center = Offset(canvasWidth * 0.82f, rowHeight * 1.5f + driftPx)
            )
            drawCircle(
                color = Color(0xFF10B981).copy(alpha = 0.09f),
                radius = 75.dp.toPx(),
                center = Offset(canvasWidth * 0.18f, rowHeight * 4.2f - driftPx)
            )
            drawCircle(
                color = Color(0xFFF59E0B).copy(alpha = 0.08f),
                radius = 70.dp.toPx(),
                center = Offset(canvasWidth * 0.78f, rowHeight * 7.5f + driftPx)
            )

            // Layer B: Completed Nodes Teal/Green Ambient Glow & Active Warm Amber Radial Glow
            modules.forEachIndexed { index, mod ->
                val isCompleted = completedIds.contains(mod.id)
                val isCurrent = !isCompleted && (index == 0 || completedIds.contains(mod.id - 1))
                val ratio = nodeOffsets.getOrElse(index) { 0.5f }
                val nodeX = ratio * canvasWidth
                val nodeY = index * rowHeight + rowHeight / 2f

                if (isCurrent) {
                    // Active Node (M3): Warm Amber/Gold Soft Radial Glow (~15-30% opacity, 2.5s pulse)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFF59E0B).copy(alpha = activeGlowAlpha),
                                Color(0xFFD97706).copy(alpha = activeGlowAlpha * 0.5f),
                                Color.Transparent
                            ),
                            center = Offset(nodeX, nodeY),
                            radius = 95.dp.toPx() * activeGlowScale
                        ),
                        center = Offset(nodeX, nodeY),
                        radius = 95.dp.toPx() * activeGlowScale
                    )
                } else if (isCompleted) {
                    // Completed Nodes (M1, M2): Subtle Teal/Green Ambient Glow (~15% opacity)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF10B981).copy(alpha = 0.18f),
                                Color(0xFF059669).copy(alpha = 0.06f),
                                Color.Transparent
                            ),
                            center = Offset(nodeX, nodeY),
                            radius = 70.dp.toPx()
                        ),
                        center = Offset(nodeX, nodeY),
                        radius = 70.dp.toPx()
                    )
                }
            }

            // Layer C: Curved Path Line with Inner Under-Shadow & Glowing Energy Strokes
            val path = Path()
            for (i in modules.indices) {
                val ratio = nodeOffsets.getOrElse(i) { 0.5f }
                val x = ratio * canvasWidth
                val y = i * rowHeight + rowHeight / 2f

                if (i == 0) {
                    path.moveTo(x, y)
                } else {
                    val prevRatio = nodeOffsets.getOrElse(i - 1) { 0.5f }
                    val prevX = prevRatio * canvasWidth
                    val prevY = (i - 1) * rowHeight + rowHeight / 2f
                    val midY = (prevY + y) / 2f

                    path.cubicTo(prevX, midY, x, midY, x, y)
                }
            }

            // Path Base Inner Shadow Track
            drawPath(
                path = path,
                color = Color(0xFF272A38),
                style = Stroke(
                    width = 6.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 14f), 0f)
                )
            )

            // Active Completed Path Line with Floating Energy Glow
            val completedCount = completedIds.size
            if (completedCount > 0) {
                val activePath = Path()
                val activeLimit = completedCount.coerceAtMost(modules.size)

                for (i in 0 until activeLimit) {
                    val ratio = nodeOffsets.getOrElse(i) { 0.5f }
                    val x = ratio * canvasWidth
                    val y = i * rowHeight + rowHeight / 2f

                    if (i == 0) {
                        activePath.moveTo(x, y)
                    } else {
                        val prevRatio = nodeOffsets.getOrElse(i - 1) { 0.5f }
                        val prevX = prevRatio * canvasWidth
                        val prevY = (i - 1) * rowHeight + rowHeight / 2f
                        val midY = (prevY + y) / 2f

                        activePath.cubicTo(prevX, midY, x, midY, x, y)
                    }
                }

                // 1. Wide Ambient Energy Under-Glow
                drawPath(
                    path = activePath,
                    color = Color(0xFF38BDF8).copy(alpha = 0.18f),
                    style = Stroke(width = 26.dp.toPx(), cap = StrokeCap.Round)
                )
                // 2. Mid Glow Energy Stroke
                drawPath(
                    path = activePath,
                    color = Color(0xFF10B981).copy(alpha = 0.40f),
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
                // 3. Core Solid Active Energy Path
                drawPath(
                    path = activePath,
                    color = Color(0xFF10B981),
                    style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        // 2. Node Circles & Floating Banners with Interactive Scale & Pulse Effects
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            modules.forEachIndexed { index, mod ->
                val isCompleted = completedIds.contains(mod.id)
                val isCurrent = !isCompleted && (index == 0 || completedIds.contains(mod.id - 1))
                val ratio = nodeOffsets.getOrElse(index) { 0.5f }
                val accentColor = getModuleColor(mod.id)
                val emoji = getModuleEmoji(mod.id)
                val isSelected = selectedModuleId == mod.id

                val animatedNodeScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.15f else 1.0f,
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                    label = "NodeScale"
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        // Node Circle Positioned horizontally based on offset ratio
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(ratio)
                                .align(Alignment.CenterStart),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                // Motion Pulse Glow Aura behind Active / Completed Node
                                if (isCurrent || isCompleted) {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .graphicsLayer {
                                                val s = if (isCurrent) activeGlowScale else 1.05f
                                                scaleX = s
                                                scaleY = s
                                            }
                                            .clip(CircleShape)
                                            .background(
                                                Brush.radialGradient(
                                                    listOf(
                                                        if (isCompleted) PwEmeraldGreen.copy(alpha = 0.5f) else PwElectricBlue.copy(alpha = activeGlowAlpha),
                                                        if (isCompleted) Color(0xFF047857).copy(alpha = 0.25f) else InstagramPink.copy(alpha = activeGlowAlpha * 0.5f),
                                                        Color.Transparent
                                                    )
                                                )
                                            )
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .graphicsLayer {
                                            scaleX = animatedNodeScale
                                            scaleY = animatedNodeScale
                                            shadowElevation = if (isCurrent || isCompleted) 16.dp.toPx() else 4.dp.toPx()
                                            shape = CircleShape
                                            clip = false
                                        }
                                        .clip(CircleShape)
                                        // 3D Outer Bevel Enclosure
                                        .background(
                                            when {
                                                isCompleted -> Brush.verticalGradient(
                                                    listOf(
                                                        Color(0xFF34D399),
                                                        PwEmeraldGreen,
                                                        Color(0xFF047857)
                                                    )
                                                )
                                                isCurrent -> Brush.verticalGradient(
                                                    listOf(
                                                        Color(0xFF818CF8),
                                                        PwElectricBlue,
                                                        InstagramPink,
                                                        PwAmberGold
                                                    )
                                                )
                                                else -> Brush.verticalGradient(
                                                    listOf(
                                                        Color(0xFF3F3F46),
                                                        Color(0xFF27272A),
                                                        Color(0xFF18181B)
                                                    )
                                                )
                                            }
                                        )
                                        .border(
                                            width = if (isCurrent) 3.dp else 1.5.dp,
                                            brush = when {
                                                isCompleted -> Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.8f), PwEmeraldGreen))
                                                isCurrent -> Brush.verticalGradient(listOf(Color.White, PwAmberGold))
                                                else -> Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.15f), Color.Transparent))
                                            },
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            SoundManager.playNodeTapSound()
                                            selectedModuleId = if (isSelected) null else mod.id
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Inner 3D Specular Highlight Core Box
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(3.5.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isCompleted -> Brush.radialGradient(
                                                        colors = listOf(Color(0xFF10B981), Color(0xFF065F46)),
                                                        center = Offset(30f, 30f),
                                                        radius = 120f
                                                    )
                                                    isCurrent -> Brush.radialGradient(
                                                        colors = listOf(Color(0xFF6356F6), Color(0xFF1E1B4B)),
                                                        center = Offset(30f, 30f),
                                                        radius = 120f
                                                    )
                                                    else -> Brush.radialGradient(
                                                        colors = listOf(Color(0xFF27272A), Color(0xFF09090B)),
                                                        center = Offset(30f, 30f),
                                                        radius = 120f
                                                    )
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            if (isCompleted) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = Color(0xFFFDE047),
                                                    modifier = Modifier.size(26.dp)
                                                )
                                            } else if (isCurrent) {
                                                Text(
                                                    text = emoji,
                                                    fontSize = 24.sp,
                                                    modifier = Modifier.graphicsLayer {
                                                        translationY = -2f
                                                    }
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = null,
                                                    tint = TextSecondary.copy(alpha = 0.7f),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            Text(
                                                text = "M${mod.id}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (isCompleted || isCurrent) Color.White else TextSecondary
                                            )
                                        }
                                    }

                                    // 3D Floating Status Badge Cap (Positioned cleanly above node)
                                    if (isCurrent) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopCenter)
                                                .offset(y = (-24).dp)
                                                .graphicsLayer {
                                                    shadowElevation = 8.dp.toPx()
                                                    shape = RoundedCornerShape(8.dp)
                                                    clip = false
                                                }
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    Brush.horizontalGradient(
                                                        listOf(PwAmberGold, Color(0xFFF59E0B))
                                                    )
                                                )
                                                .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 7.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "CURRENT",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.Black
                                            )
                                        }
                                    } else if (isCompleted) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopCenter)
                                                .offset(y = (-22).dp)
                                                .graphicsLayer {
                                                    shadowElevation = 6.dp.toPx()
                                                    shape = RoundedCornerShape(8.dp)
                                                    clip = false
                                                }
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    Brush.horizontalGradient(
                                                        listOf(PwEmeraldGreen, Color(0xFF047857))
                                                    )
                                                )
                                                .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "+50 XP",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Expanded Floating Interactive Node Detail Tooltip Card with Pop-in animation
                AnimatedVisibility(
                    visible = isSelected,
                    enter = fadeIn(tween(200)) + expandVertically(spring(stiffness = Spring.StiffnessMedium)),
                    exit = fadeOut(tween(150)) + shrinkVertically(spring(stiffness = Spring.StiffnessHigh))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        InstagramElevatedSurface,
                                        Color(0xFF1E1E24)
                                    )
                                )
                            )
                            .border(1.5.dp, accentColor, RoundedCornerShape(18.dp))
                            .clickable { onNodeClick(mod) }
                    ) {
                        Column {
                            // 3D Generated Artwork Header for Module Card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(95.dp)
                                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.roadmap_vector_bg),
                                    contentDescription = "M${mod.id} SVG Vector",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(
                                                    Color.Transparent,
                                                    Color(0xFF1E1E24).copy(alpha = 0.95f)
                                                )
                                            )
                                        )
                                )
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = emoji,
                                        fontSize = 18.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "MODULE ${mod.id}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = mod.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "⏱️ ${mod.readTimeMinutes} min read",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "⚡ +${mod.xpReward} XP",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PwAmberGold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Button(
                                    onClick = { onNodeClick(mod) },
                                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text(if (isCompleted) "REVIEW" else "START", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WebsiteStatCard(
    emoji: String,
    label: String,
    value: String,
    accent: Color,
    sub: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .graphicsLayer {
                shadowElevation = 8.dp.toPx()
                shape = RoundedCornerShape(20.dp)
                clip = false
            }
            .clip(RoundedCornerShape(20.dp))
            .background(InstagramDarkSurface)
            .border(1.2.dp, Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.15f), accent.copy(alpha = 0.2f))), RoundedCornerShape(20.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = label,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = TextSecondary,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = value,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        letterSpacing = (-0.3).sp
                    )
                }
                ThreeDIconBadge(
                    emoji = emoji,
                    badgeSize = 40.dp,
                    primaryColor = accent,
                    secondaryColor = accent.copy(alpha = 0.5f),
                    elevation = 6.dp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = sub,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = accent,
                maxLines = 1
            )
        }
    }
}
