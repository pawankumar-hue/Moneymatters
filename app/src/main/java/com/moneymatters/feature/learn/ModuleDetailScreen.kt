package com.moneymatters.feature.learn

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneymatters.core.designsystem.*
import com.moneymatters.core.designsystem.Canvas3DCoin
import com.moneymatters.core.designsystem.Canvas3DBalanceScale
import com.moneymatters.core.designsystem.Canvas3DDonutChart
import com.moneymatters.core.designsystem.ThreeDIconBadge
import com.moneymatters.core.i18n.AppLanguageManager
import com.moneymatters.data.model.*
import kotlinx.coroutines.launch
import kotlin.math.*

// ─── Cycling highlight colors for **bold text** ─────────────────────────────
private val boldHighlightColors = listOf(
    Color(0xFFFBBF24), // Gold
    Color(0xFF22D3EE), // Cyan
    Color(0xFF34D399), // Emerald
    Color(0xFFF472B6), // Pink
    Color(0xFFA78BFA)  // Purple
)

private fun parseHexColor(hex: String?, fallback: Color = Color(0xFF3B82F6)): Color {
    if (hex.isNullOrBlank()) return fallback
    return try {
        val clean = hex.removePrefix("#")
        val colorInt = android.graphics.Color.parseColor("#$clean")
        Color(colorInt)
    } catch (e: Exception) {
        fallback
    }
}


// ─── Unified Card Data Class used by Pager ─────────────────────────────────
data class UIModuleCard(
    val id: String,
    val topicId: String,
    val topicTitle: String,
    val cardIndex: Int,
    val totalCardsInTopic: Int,
    val overallIndex: Int,
    val totalCardsInModule: Int,
    val title: String,
    val content: String,
    val emoji: String,
    val accentColor: Color,
    val interactiveType: String? = null,
    val choiceData: ChoiceSimData? = null,
    val quizData: CardQuizData? = null,
    val calcData: CardCalcData? = null
)

// ─── Content Splitter: converts raw markdown text into sub-concept cards ───
fun splitContentIntoSubConceptCards(
    content: String,
    chapters: List<String>,
    moduleId: Int
): List<UIModuleCard> {
    val cards = mutableListOf<UIModuleCard>()
    val accents = listOf(
        Color(0xFF3B82F6), Color(0xFF8B5CF6), Color(0xFF10B981),
        Color(0xFFF59E0B), Color(0xFFEF4444), Color(0xFF06B6D4)
    )

    // Split text by chapter headings or subheadings
    val rawSections = content.split(Regex("""(?m)^(?:##|###|\d+\.\d+)\s+""")).filter { it.isNotBlank() }
    val sections = if (rawSections.size >= 3) rawSections else content.split("\n\n").filter { it.length > 50 }

    val totalCount = sections.size.coerceAtLeast(1)

    sections.forEachIndexed { idx, sec ->
        val lines = sec.trim().lines()
        val heading = lines.firstOrNull()?.replace("#", "")?.trim() ?: "Concept ${idx + 1}"
        val body = if (lines.size > 1) lines.drop(1).joinToString("\n").trim() else sec.trim()
        val accent = accents[idx % accents.size]

        cards.add(
            UIModuleCard(
                id = "$moduleId-${idx + 1}",
                topicId = "$moduleId-ch",
                topicTitle = if (chapters.isNotEmpty()) chapters[idx % chapters.size] else "Module $moduleId",
                cardIndex = idx + 1,
                totalCardsInTopic = totalCount,
                overallIndex = idx + 1,
                totalCardsInModule = totalCount,
                title = heading,
                content = body,
                emoji = if (heading.contains("Money")) "💰" else if (heading.contains("Income")) "💵" else "📚",
                accentColor = accent
            )
        )
    }

    return cards.ifEmpty {
        listOf(
            UIModuleCard(
                id = "$moduleId-1",
                topicId = "$moduleId-ch",
                topicTitle = "Module $moduleId",
                cardIndex = 1,
                totalCardsInTopic = 1,
                overallIndex = 1,
                totalCardsInModule = 1,
                title = "Module $moduleId Overview",
                content = content,
                emoji = "📚",
                accentColor = accents[0]
            )
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  ModuleDetailScreen — Full-Screen Swipeable Card Reader
// ══════════════════════════════════════════════════════════════════════════════
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ModuleDetailScreen(
    moduleId: Int,
    onBack: () -> Unit,
    viewModel: LearnViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val langCode = uiState.selectedLanguage
    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(moduleId, langCode) {
        viewModel.loadModuleDetail(moduleId)
    }

    val detail = uiState.currentModuleDetail
    val topicDataList = uiState.currentTopicCards

    // Flatten topic cards if structured JSON cards exist
    val uiCards = remember(topicDataList, detail) {
        if (topicDataList.isNotEmpty()) {
            val allCards = topicDataList.flatMap { topic -> topic.cards }
            val totalInModule = allCards.size
            allCards.mapIndexed { idx, card ->
                UIModuleCard(
                    id = card.id.ifEmpty { "$moduleId-$idx" },
                    topicId = card.topicId,
                    topicTitle = card.topicTitle.ifEmpty { "Module $moduleId" },
                    cardIndex = card.cardIndex,
                    totalCardsInTopic = card.totalCardsInTopic,
                    overallIndex = idx + 1,
                    totalCardsInModule = totalInModule,
                    title = card.title,
                    content = card.content,
                    emoji = card.emoji.ifEmpty { "💡" },
                    accentColor = parseHexColor(card.color),
                    interactiveType = card.interactiveType,
                    choiceData = card.choiceData,
                    quizData = card.quizData,
                    calcData = card.calcData
                )
            }
        } else if (detail != null) {
            splitContentIntoSubConceptCards(detail.content, detail.chapters, detail.id)
        } else {
            emptyList()
        }
    }

    val pageCount = uiCards.size.coerceAtLeast(1)
    val pagerState = rememberPagerState(pageCount = { pageCount })
    var showChapterSheet by remember { mutableStateOf(false) }

    // Haptic feedback on page change
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage > 0) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Scaffold(
        containerColor = Color(0xFF080810),
        topBar = {
            CardReaderTopBar(
                moduleId = moduleId,
                currentCard = uiCards.getOrNull(pagerState.currentPage),
                onBack = onBack,
                onShowToc = { showChapterSheet = true }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading || detail == null || uiCards.isEmpty()) {
            CardLoadingSpinner(modifier = Modifier.padding(paddingValues))
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                // Main Swipeable Pager — One full-screen card per swipe
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    pageSpacing = 16.dp,
                    beyondBoundsPageCount = 1
                ) { pageIndex ->
                    val card = uiCards.getOrNull(pageIndex) ?: return@HorizontalPager
                    val rawOffset = ((pageIndex - pagerState.currentPage) +
                            pagerState.currentPageOffsetFraction).coerceIn(-1f, 1f)

                    SingleCardView(
                        card = card,
                        pageOffset = rawOffset,
                        isActive = pagerState.currentPage == pageIndex,
                        moduleQuiz = if (pageIndex == pageCount - 1) uiState.currentQuiz.firstOrNull() else null,
                        langCode = langCode,
                        onModuleComplete = {
                            viewModel.completeCurrentModule(detail.id, detail.xpReward)
                            onBack()
                        }
                    )
                }

                // Bottom Sticky Navigation Controls
                CardBottomNavControls(
                    currentPage = pagerState.currentPage,
                    totalPages = pageCount,
                    currentCard = uiCards.getOrNull(pagerState.currentPage),
                    onPrev = {
                        if (pagerState.currentPage > 0) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    pagerState.currentPage - 1,
                                    animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)
                                )
                            }
                        }
                    },
                    onNext = {
                        if (pagerState.currentPage < pageCount - 1) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    pagerState.currentPage + 1,
                                    animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)
                                )
                            }
                        } else {
                            viewModel.completeCurrentModule(detail.id, detail.xpReward)
                            onBack()
                        }
                    }
                )
            }
        }
    }

    // Index Modal Dialog Grouped by Topic
    if (showChapterSheet && detail != null) {
        AlertDialog(
            onDismissRequest = { showChapterSheet = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.List, contentDescription = null, tint = PwElectricBlue, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Index", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Spacer(Modifier.width(6.dp))
                    Text("(${uiCards.size} Cards)", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Normal)
                }
            },
            text = {
                Column(modifier = Modifier.heightIn(max = 380.dp).verticalScroll(rememberScrollState())) {
                    val groupedTopics = remember(uiCards) {
                        uiCards.groupBy { it.topicTitle }
                    }

                    var globalCardIndex = 0
                    groupedTopics.entries.forEachIndexed { tIdx, (topicTitle, topicCards) ->
                        // Topic Header Banner
                        val topicAccent = topicCards.firstOrNull()?.accentColor ?: PwElectricBlue
                        Spacer(Modifier.height(if (tIdx == 0) 0.dp else 12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(topicAccent.copy(alpha = 0.12f))
                                .border(1.dp, topicAccent.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📌", fontSize = 11.sp)
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = topicTitle.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = topicAccent,
                                    letterSpacing = 1.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        Spacer(Modifier.height(6.dp))

                        // Topic Cards List
                        topicCards.forEach { card ->
                            val idx = globalCardIndex
                            globalCardIndex++
                            val isActive = pagerState.currentPage == idx

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isActive) topicAccent.copy(alpha = 0.16f) else Color.Transparent)
                                    .clickable {
                                        showChapterSheet = false
                                        coroutineScope.launch { pagerState.animateScrollToPage(idx) }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier.size(24.dp).clip(CircleShape)
                                            .background(if (isActive) topicAccent else InstagramElevatedSurface),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${idx + 1}", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                            color = if (isActive) Color.White else TextSecondary)
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        text = "${card.emoji} ${card.title}",
                                        fontSize = 12.sp,
                                        color = if (isActive) topicAccent else TextPrimary,
                                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                if (isActive) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = topicAccent, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            containerColor = Color(0xFF0E0E18)
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  SINGLE CARD VIEW — Full page card container with 3D bezel & animations
// ══════════════════════════════════════════════════════════════════════════════
@Composable
private fun SingleCardView(
    card: UIModuleCard,
    pageOffset: Float,
    isActive: Boolean,
    moduleQuiz: QuizQuestion?,
    langCode: String,
    onModuleComplete: () -> Unit
) {
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(card.id) { appeared = true }

    val entranceAlpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(500, 100, FastOutSlowInEasing),
        label = "alpha_$card.id"
    )
    val titleY by animateFloatAsState(
        targetValue = if (appeared) 0f else 35f,
        animationSpec = tween(600, 150, FastOutSlowInEasing),
        label = "title_$card.id"
    )

    // Static ambient values — eliminates infinite transition coroutines for non-essential decorative animations
    val arcRot = 0f
    val particle1Y = 0.5f
    val particle2Y = 0.5f

    // Progress sweep for this card in topic
    val progressFraction = card.cardIndex.toFloat() / card.totalCardsInTopic.coerceAtLeast(1).toFloat()
    val progressSweep by animateFloatAsState(
        targetValue = if (appeared) progressFraction * 360f else 0f,
        animationSpec = tween(1000, 200, FastOutSlowInEasing),
        label = "sweep"
    )

    // Pre-cached brush for arc ring (prevents per-frame GPU shader object allocation)
    val arcSweepBrush = remember(card.accentColor) {
        Brush.sweepGradient(listOf(Color.Transparent, card.accentColor.copy(0.4f), card.accentColor.copy(0.7f), Color.Transparent))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                alpha = (1f - abs(pageOffset) * 0.55f).coerceIn(0f, 1f)
                scaleX = (1f - abs(pageOffset) * 0.07f).coerceIn(0.92f, 1f)
                scaleY = (1f - abs(pageOffset) * 0.07f).coerceIn(0.92f, 1f)
            }
            .drawBehind {
                // Background ambient glow
                drawCircle(card.accentColor.copy(alpha = 0.07f), size.width * 0.85f, Offset(size.width, 0f))
                drawCircle(card.accentColor.copy(alpha = 0.04f), size.width * 0.6f, Offset(0f, size.height))

                // Top-Right SVG Arc Ring
                val arcR = size.width * 0.3f
                val arcC = Offset(size.width - 24.dp.toPx(), 24.dp.toPx())
                rotate(arcRot, pivot = arcC) {
                    drawArc(
                        brush = arcSweepBrush,
                        startAngle = 0f, sweepAngle = 210f, useCenter = false,
                        topLeft = Offset(arcC.x - arcR, arcC.y - arcR),
                        size = Size(arcR * 2f, arcR * 2f),
                        style = Stroke(1.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Floating ambient particles
                drawCircle(card.accentColor.copy(0.3f), 3.dp.toPx(), Offset(size.width * 0.09f, size.height * 0.25f + particle1Y * 12f))
                drawCircle(card.accentColor.copy(0.18f), 5.dp.toPx(), Offset(size.width * 0.88f, size.height * 0.48f + particle2Y * 16f))
            }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
            Spacer(Modifier.height(12.dp))

            // ── Top Header Row: Emoji Badge ──────
            Row(
                modifier = Modifier.fillMaxWidth().graphicsLayer { alpha = entranceAlpha },
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 3D double-bezel emoji icon box
                Box(
                    modifier = Modifier.size(50.dp)
                        .drawBehind {
                            drawCircle(card.accentColor.copy(0.2f), size.minDimension / 2f + 5.dp.toPx())
                            drawCircle(card.accentColor.copy(0.45f), size.minDimension / 2f, style = Stroke(1.5.dp.toPx()))
                        }
                        .clip(CircleShape).background(card.accentColor.copy(0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(card.emoji, fontSize = 24.sp, textAlign = TextAlign.Center)
                }
            }

            Spacer(Modifier.height(14.dp))

            // ── Topic Eyebrow + Card Title ─────────────────────────────────
            Column(modifier = Modifier.graphicsLayer { alpha = entranceAlpha; translationY = titleY }) {
                Text(
                    text = card.topicTitle.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = card.accentColor,
                    letterSpacing = 1.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = card.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    lineHeight = 24.sp,
                    letterSpacing = (-0.3).sp
                )
            }

            Spacer(Modifier.height(10.dp))

            // Glowing divider line
            Box(
                modifier = Modifier.fillMaxWidth().height(1.5.dp)
                    .background(Brush.horizontalGradient(listOf(card.accentColor, card.accentColor.copy(0f))))
            )

            Spacer(Modifier.height(14.dp))

            // ── Scrollable Rich Content Body + Interactive Cards ─────────
            Column(
                modifier = Modifier.weight(1f)
                    .verticalScroll(rememberScrollState())
                    .graphicsLayer { alpha = entranceAlpha }
            ) {
                // Render Rich Markdown Blocks (Dialogues, Tables, Bold terms, Missions)
                RichCardContentRenderer(
                    content = card.content,
                    topicColor = card.accentColor,
                    isVisible = appeared
                )

                // ── Render Custom Lightweight 3D Canvas Elements per Card Title ─────
                when {
                    card.title.contains("Barter", ignoreCase = true) || card.title.contains("Needs vs Wants", ignoreCase = true) -> {
                        Spacer(Modifier.height(12.dp))
                        Canvas3DBalanceScale(tiltAngle = if (card.title.contains("Want", ignoreCase = true)) 12f else -8f, accentColor = card.accentColor)
                    }
                    card.title.contains("Inflation", ignoreCase = true) || card.title.contains("Income", ignoreCase = true) || card.title.contains("Tracking", ignoreCase = true) -> {
                        Spacer(Modifier.height(12.dp))
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Canvas3DCoin(coinColor = card.accentColor, secondaryColor = PwAmberGold)
                        }
                    }
                    card.title.contains("Budget", ignoreCase = true) || card.title.contains("Rule", ignoreCase = true) || card.title.contains("Template", ignoreCase = true) || card.title.contains("Buffer", ignoreCase = true) -> {
                        Spacer(Modifier.height(12.dp))
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Canvas3DDonutChart()
                        }
                    }
                }

                // ── Render Interactive Component per card ─────────────────
                when (card.interactiveType) {
                    "choice_sim" -> {
                        card.choiceData?.let { cData ->
                            Spacer(Modifier.height(16.dp))
                            InteractiveChoiceSimCard(choiceData = cData, topicColor = card.accentColor)
                        }
                    }
                    "quiz" -> {
                        card.quizData?.let { qData ->
                            Spacer(Modifier.height(16.dp))
                            InteractiveCardQuiz(quizData = qData, topicColor = card.accentColor)
                        }
                    }
                    "calculator", "calc" -> {
                        card.calcData?.let { calcData ->
                            Spacer(Modifier.height(16.dp))
                            InteractiveCardCalc(calcData = calcData, topicColor = card.accentColor)
                        }
                    }
                }

                // If last card, show Module Quiz / Completion Button
                if (card.overallIndex == card.totalCardsInModule && moduleQuiz != null) {
                    Spacer(Modifier.height(24.dp))
                    ModuleFinalQuizBlock(
                        quiz = moduleQuiz,
                        langCode = langCode,
                        onComplete = onModuleComplete
                    )
                }

                Spacer(Modifier.height(90.dp))
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  RICH CONTENT RENDERER — Parsed dialogues, bold text, lists, missions, tables
// ══════════════════════════════════════════════════════════════════════════════
@Composable
fun RichCardContentRenderer(
    content: String,
    topicColor: Color,
    isVisible: Boolean
) {
    val lines = remember(content) { content.lines() }
    val checkedState = remember { mutableStateMapOf<String, Boolean>() }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        var i = 0
        var blockIdx = 0

        while (i < lines.size) {
            val line = lines[i]

            // 1. Dialogue Chat Bubble (`Priya: ...` vs `Bhaiya: ...`)
            if ((line.contains(": *") || (line.contains("**") && line.contains(":"))) && line.contains(":")) {
                val match = Regex("""^([\w\s]+):\s*(.*)$""").find(line)
                if (match != null) {
                    val speaker = match.groupValues[1].trim()
                    val dialogText = match.groupValues[2].trim()
                    val isMentor = speaker.contains("Bhai", ignoreCase = true) || speaker.contains("Mentor", ignoreCase = true)

                    ChatBubbleBlock(
                        speaker = speaker,
                        text = dialogText,
                        isMentor = isMentor,
                        topicColor = topicColor
                    )
                    i++
                    blockIdx++
                    continue
                }
            }

            // 2. Mission / Alert Box (`🚨 MISSION` / `🚨 ALERT`)
            if (line.contains("🚨") && (line.contains("MISSION") || line.contains("ALERT"))) {
                val missionTitle = line.trim()
                i++
                val missionItems = mutableListOf<String>()
                while (i < lines.size && lines[i].trim().isNotEmpty() && !lines[i].contains("🚨")) {
                    missionItems.add(lines[i].trim())
                    i++
                }

                MissionBoxBlock(
                    title = missionTitle,
                    items = missionItems,
                    topicColor = topicColor,
                    checkedState = checkedState,
                    blockId = "mission_$blockIdx"
                )
                blockIdx++
                continue
            }

            // 3. Markdown Table (`| col | col |`)
            if (line.trim().startsWith("|")) {
                val tableLines = mutableListOf<String>()
                while (i < lines.size && (lines[i].trim().startsWith("|") || lines[i].trim().isEmpty())) {
                    if (lines[i].trim().startsWith("|")) tableLines.add(lines[i].trim())
                    i++
                }
                val rowLines = tableLines.filter { !it.matches(Regex("""^\|\s*[-:]+\s*(\|\s*[-:]+\s*)*\|?$""")) }
                if (rowLines.isNotEmpty()) {
                    val rows = rowLines.map { r -> r.split("|").drop(1).dropLast(1).map { cell -> cell.trim() } }
                    RenderModernTable(rows = rows, topicColor = topicColor)
                }
                blockIdx++
                continue
            }

            // 4. Empty line
            if (line.trim().isEmpty()) {
                i++
                continue
            }

            // 5. Uppercase tracked section header
            if (line.trim().matches(Regex("""^[A-Z0-9\s()—–\-:]{5,}$""")) && !line.contains("₹")) {
                Text(
                    text = line.trim(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = topicColor,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
                i++
                blockIdx++
                continue
            }

            // 6. Bullet List (`- item` or `• item`)
            if (line.trim().matches(Regex("""^[-•*✅❌]\s.*"""))) {
                val items = mutableListOf<String>()
                while (i < lines.size && lines[i].trim().matches(Regex("""^[-•*✅❌]\s.*"""))) {
                    items.add(lines[i].trim().replace(Regex("""^[-•*✅❌]\s"""), ""))
                    i++
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items.forEach { item ->
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier.padding(top = 7.dp, end = 8.dp)
                                    .size(6.dp).clip(CircleShape).background(topicColor)
                            )
                            Text(
                                text = parseInlineRichText(item, topicColor),
                                fontSize = 14.sp,
                                color = TextPrimary,
                                lineHeight = 21.sp
                            )
                        }
                    }
                }
                blockIdx++
                continue
            }

            // 7. Numbered List (`1. item` or `1) item`)
            if (line.trim().matches(Regex("""^\d+[.)]\s.*"""))) {
                val items = mutableListOf<String>()
                while (i < lines.size && lines[i].trim().matches(Regex("""^\d+[.)]\s.*"""))) {
                    items.add(lines[i].trim().replace(Regex("""^\d+[.)]\s"""), ""))
                    i++
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items.forEachIndexed { idx, item ->
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier.size(20.dp).clip(CircleShape)
                                    .background(topicColor.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${idx + 1}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = topicColor
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = parseInlineRichText(item, topicColor),
                                fontSize = 14.sp,
                                color = TextPrimary,
                                lineHeight = 21.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                blockIdx++
                continue
            }

            // 8. Standard paragraph with inline formatting
            Text(
                text = parseInlineRichText(line, topicColor),
                fontSize = 14.sp,
                color = TextPrimary,
                lineHeight = 22.sp
            )
            i++
            blockIdx++
        }
    }
}

private val inlineFormatRegex = Regex("""\*\*([^*]+)\*\*|\*([^*]+)\*|`([^`]+)`|(₹[\d,./+\-% ]+)""")

// ── Inline Rich Text Parser (`**bold**`, `*italic*`, `₹amount`, `` `code` ``) ─
@Composable
private fun parseInlineRichText(text: String, topicColor: Color): androidx.compose.ui.text.AnnotatedString {
    return remember(text, topicColor) {
        buildAnnotatedString {
            var last = 0
            var colorIdx = 0

            inlineFormatRegex.findAll(text).forEach { m ->
                if (m.range.first > last) {
                    append(text.substring(last, m.range.first))
                }

                val boldMatch = m.groups[1]?.value
                val italicMatch = m.groups[2]?.value
                val codeMatch = m.groups[3]?.value
                val currencyMatch = m.groups[4]?.value

                when {
                    boldMatch != null -> {
                        val color = boldHighlightColors[colorIdx % boldHighlightColors.size]
                        colorIdx++
                        pushStyle(SpanStyle(fontWeight = FontWeight.Black, color = color))
                        append(boldMatch)
                        pop()
                    }
                    italicMatch != null -> {
                        pushStyle(SpanStyle(fontStyle = FontStyle.Italic, color = TextSecondary))
                        append(italicMatch)
                        pop()
                    }
                    codeMatch != null -> {
                        pushStyle(SpanStyle(fontWeight = FontWeight.Bold, color = PwCyanBlue))
                        append("`$codeMatch`")
                        pop()
                    }
                    currencyMatch != null -> {
                        pushStyle(SpanStyle(fontWeight = FontWeight.Bold, color = PwAmberGold))
                        append(currencyMatch)
                        pop()
                    }
                }
                last = m.range.last + 1
            }

            if (last < text.length) {
                append(text.substring(last))
            }
        }
    }
}

// ── Chat Bubble Component ──────────────────────────────────────────────────
@Composable
private fun ChatBubbleBlock(
    speaker: String,
    text: String,
    isMentor: Boolean,
    topicColor: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalAlignment = if (isMentor) Alignment.End else Alignment.Start
    ) {
        Text(
            text = speaker.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
        Box(
            modifier = Modifier
                .widthIn(max = 290.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isMentor) 16.dp else 2.dp,
                        bottomEnd = if (isMentor) 2.dp else 16.dp
                    )
                )
                .background(
                    if (isMentor) topicColor.copy(alpha = 0.85f)
                    else InstagramElevatedSurface
                )
                .border(
                    1.dp,
                    if (isMentor) topicColor else InstagramBorderDark,
                    RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = parseInlineRichText(text, if (isMentor) Color.Black else topicColor),
                fontSize = 14.sp,
                color = if (isMentor) Color.Black else TextPrimary,
                fontWeight = FontWeight.Medium,
                lineHeight = 20.sp
            )
        }
    }
}

// ── Mission / Alert Box Component ──────────────────────────────────────────
@Composable
private fun MissionBoxBlock(
    title: String,
    items: List<String>,
    topicColor: Color,
    checkedState: androidx.compose.runtime.snapshots.SnapshotStateMap<String, Boolean>,
    blockId: String
) {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(topicColor.copy(alpha = 0.06f))
            .border(1.5.dp, topicColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = topicColor, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = title.uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = topicColor,
                    letterSpacing = 1.sp
                )
            }
            Spacer(Modifier.height(10.dp))
            items.forEachIndexed { itemIdx, item ->
                val isCheckbox = item.startsWith("[ ]") || item.startsWith("[]")
                val cleanText = item.replace(Regex("""^\[\s*\]\s*"""), "")
                val itemId = "${blockId}_$itemIdx"
                val isChecked = checkedState[itemId] == true

                if (isCheckbox) {
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable {
                            checkedState[itemId] = !isChecked
                        }.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(18.dp).clip(RoundedCornerShape(4.dp))
                                .background(if (isChecked) PwEmeraldGreen else InstagramElevatedSurface)
                                .border(1.dp, if (isChecked) PwEmeraldGreen else InstagramBorderDark, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isChecked) Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = parseInlineRichText(cleanText, topicColor),
                            fontSize = 13.sp,
                            color = if (isChecked) TextSecondary else TextPrimary,
                            lineHeight = 19.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    Text(
                        text = parseInlineRichText(item, topicColor),
                        fontSize = 13.sp,
                        color = TextPrimary,
                        lineHeight = 19.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}

// ── Modern Table Component ─────────────────────────────────────────────────
@Composable
private fun RenderModernTable(rows: List<List<String>>, topicColor: Color) {
    if (rows.isEmpty()) return
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF10101C))
            .border(1.dp, InstagramBorderDark, RoundedCornerShape(12.dp))
    ) {
        rows.forEachIndexed { rIdx, row ->
            val isHeader = rIdx == 0
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(if (isHeader) topicColor.copy(alpha = 0.15f) else if (rIdx % 2 == 0) Color(0xFF141422) else Color.Transparent)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                row.forEachIndexed { cIdx, cell ->
                    Text(
                        text = parseInlineRichText(cell, topicColor),
                        fontSize = if (isHeader) 11.sp else 12.sp,
                        fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
                        color = if (isHeader) topicColor else TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            if (rIdx < rows.size - 1) {
                HorizontalDivider(color = InstagramBorderDark.copy(alpha = 0.4f))
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  INTERACTIVE CHOICE SIMULATION CARD (`choice_sim`)
// ══════════════════════════════════════════════════════════════════════════════
@Composable
private fun InteractiveChoiceSimCard(choiceData: ChoiceSimData, topicColor: Color) {
    var selectedIdx by remember { mutableStateOf<Int?>(null) }

    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF121220))
            .border(1.dp, topicColor.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "SCENARIO CHOICE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = topicColor,
                letterSpacing = 1.5.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "\"${choiceData.scenario}\"",
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                color = TextPrimary,
                lineHeight = 21.sp
            )
            Spacer(Modifier.height(14.dp))

            choiceData.choices.forEachIndexed { idx, choice ->
                val isSelected = selectedIdx == idx
                val isCorrect = choice.isCorrect

                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when {
                                isSelected && isCorrect -> PwEmeraldGreen.copy(alpha = 0.15f)
                                isSelected && !isCorrect -> Color.Red.copy(alpha = 0.15f)
                                else -> InstagramElevatedSurface
                            }
                        )
                        .border(
                            1.dp,
                            when {
                                isSelected && isCorrect -> PwEmeraldGreen
                                isSelected && !isCorrect -> Color.Red
                                else -> InstagramBorderDark
                            },
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedIdx = idx }
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = choice.text,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (isSelected) {
                            Spacer(Modifier.height(8.dp))
                            HorizontalDivider(color = InstagramBorderDark)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = if (isCorrect) "Sahi Choice! ✅" else "Khatra! ❌",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrect) PwEmeraldGreen else Color.Red
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = choice.consequence,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  INTERACTIVE CARD QUIZ (`quiz`)
// ══════════════════════════════════════════════════════════════════════════════
@Composable
private fun InteractiveCardQuiz(quizData: CardQuizData, topicColor: Color) {
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var submitted by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF121220))
            .border(1.dp, PwAmberGold.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column {
            Text("QUICK QUIZ", fontSize = 10.sp, fontWeight = FontWeight.Black, color = PwAmberGold, letterSpacing = 1.5.sp)
            Spacer(Modifier.height(6.dp))
            Text(quizData.question, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary, lineHeight = 22.sp)
            Spacer(Modifier.height(14.dp))

            quizData.options.forEachIndexed { idx, option ->
                val isSelected = selectedOption == idx
                val isCorrect = idx == quizData.correctAnswerIndex

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when {
                                submitted && isCorrect -> PwEmeraldGreen.copy(alpha = 0.15f)
                                submitted && isSelected && !isCorrect -> Color.Red.copy(alpha = 0.15f)
                                isSelected -> topicColor.copy(alpha = 0.15f)
                                else -> InstagramElevatedSurface
                            }
                        )
                        .border(
                            1.dp,
                            when {
                                submitted && isCorrect -> PwEmeraldGreen
                                submitted && isSelected && !isCorrect -> Color.Red
                                isSelected -> topicColor
                                else -> InstagramBorderDark
                            },
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            if (!submitted) selectedOption = idx
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(24.dp).clip(CircleShape)
                            .background(if (isSelected) topicColor else InstagramElevatedSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(('A' + idx).toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else TextSecondary)
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(option, fontSize = 13.sp, color = TextPrimary)
                }
            }

            if (selectedOption != null && !submitted) {
                Spacer(Modifier.height(12.dp))
                PillButton(text = "Submit Answer", onClick = { submitted = true }, gradient = listOf(topicColor, PwAmberGold))
            }

            if (submitted) {
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(PwEmeraldGreen.copy(alpha = 0.1f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Bhai, simple hai: ${quizData.explanation}",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  INTERACTIVE CARD CALCULATOR (`calc` / `calculator`)
// ══════════════════════════════════════════════════════════════════════════════
@Composable
private fun InteractiveCardCalc(calcData: CardCalcData, topicColor: Color) {
    val inputValues = remember(calcData) {
        mutableStateMapOf<String, Float>().apply {
            calcData.inputs.forEach { input ->
                put(input.label, input.defaultValue)
            }
        }
    }

    // Dynamic Live Calculation
    val resultText = remember(inputValues.toMap()) {
        when (calcData.calcType) {
            "compounding" -> {
                val p = inputValues.values.firstOrNull() ?: 1000f
                val r = 0.12f // 12% returns
                val t = 5f
                val f = p * (1 + r).pow(t)
                "₹${f.toInt()}"
            }
            "rule72" -> {
                val r = inputValues.values.firstOrNull() ?: 12f
                val yrs = if (r > 0) 72f / r else 0f
                "%.1f Years".format(yrs)
            }
            "inflation" -> {
                val p = inputValues.values.firstOrNull() ?: 100f
                val r = 0.06f
                val t = 5f
                val f = p / (1 + r).pow(t)
                "₹${f.toInt()}"
            }
            else -> {
                val p = inputValues.values.firstOrNull() ?: 5000f
                "₹${p.toInt()}"
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF121220))
            .border(1.dp, topicColor.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column {
            Text("INTERACTIVE CALCULATOR", fontSize = 10.sp, fontWeight = FontWeight.Black, color = topicColor, letterSpacing = 1.5.sp)
            Spacer(Modifier.height(12.dp))

            calcData.inputs.forEach { input ->
                val currentVal = inputValues[input.label] ?: input.defaultValue
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(input.label, fontSize = 12.sp, color = TextSecondary)
                        Text(
                            text = "${if (input.unit == "₹") "₹" else ""}${currentVal.toInt()}${if (input.unit != "₹") " ${input.unit}" else ""}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Slider(
                        value = currentVal,
                        onValueChange = { inputValues[input.label] = it },
                        valueRange = input.min..input.max,
                        steps = 20,
                        colors = SliderDefaults.colors(thumbColor = topicColor, activeTrackColor = topicColor)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(topicColor.copy(alpha = 0.12f))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("CALCULATED VALUE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = topicColor, letterSpacing = 1.2.sp)
                    Text(resultText, fontSize = 26.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  FINAL MODULE QUIZ BLOCK
// ══════════════════════════════════════════════════════════════════════════════
@Composable
private fun ModuleFinalQuizBlock(
    quiz: QuizQuestion,
    langCode: String,
    onComplete: () -> Unit
) {
    var selectedIdx by remember { mutableStateOf<Int?>(null) }
    var submitted by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF12121E))
            .border(1.dp, PwAmberGold.copy(0.35f), RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            EyebrowTag("MODULE FINAL KNOWLEDGE CHECK", PwAmberGold)
            Spacer(Modifier.height(12.dp))
            Text(quiz.question, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, lineHeight = 24.sp)
            Spacer(Modifier.height(16.dp))

            quiz.options.forEachIndexed { index, option ->
                val isSelected = selectedIdx == index
                val isCorrect = index == quiz.correctAnswerIndex

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when {
                                submitted && isCorrect -> PwEmeraldGreen.copy(0.15f)
                                submitted && isSelected && !isCorrect -> Color.Red.copy(0.15f)
                                isSelected -> PwElectricBlue.copy(0.15f)
                                else -> InstagramElevatedSurface
                            }
                        )
                        .border(
                            1.dp,
                            when {
                                submitted && isCorrect -> PwEmeraldGreen
                                isSelected -> PwElectricBlue
                                else -> InstagramBorderDark
                            },
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { if (!submitted) selectedIdx = index }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(24.dp).clip(CircleShape)
                            .background(if (isSelected) PwElectricBlue else InstagramElevatedSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(('A' + index).toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else TextSecondary)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(option, fontSize = 14.sp, color = TextPrimary)
                }
            }

            if (selectedIdx != null && !submitted) {
                Spacer(Modifier.height(16.dp))
                PillButton("Submit Module Quiz", onClick = { submitted = true }, gradient = listOf(PwElectricBlue, InstagramPink, PwAmberGold))
            }

            if (submitted) {
                Spacer(Modifier.height(14.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(PwEmeraldGreen.copy(0.1f)).border(1.dp, PwEmeraldGreen.copy(0.5f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PwEmeraldGreen, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Explanation", fontWeight = FontWeight.Bold, color = PwEmeraldGreen, fontSize = 13.sp)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(quiz.explanation, fontSize = 13.sp, color = TextPrimary, lineHeight = 20.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                PillButton("Complete Module 🚀", onClick = onComplete, gradient = listOf(PwElectricBlue, PwCyanBlue, PwEmeraldGreen))
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  TOP BAR & DOTS & BOTTOM NAV CONTROLS
// ══════════════════════════════════════════════════════════════════════════════
@Composable
private fun CardReaderTopBar(
    moduleId: Int,
    currentCard: UIModuleCard?,
    onBack: () -> Unit,
    onShowToc: () -> Unit
) {
    val accent = currentCard?.accentColor ?: PwElectricBlue
    val animAccent by animateColorAsState(targetValue = accent, animationSpec = tween(400), label = "topbar_accent")

    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFF080810))
            .drawBehind {
                drawLine(animAccent.copy(alpha = 0.3f), Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx())
            }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(34.dp).clip(CircleShape).background(InstagramElevatedSurface).clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextPrimary, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(12.dp))
            Text("Module $moduleId", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
        }

        Box(
            modifier = Modifier.size(34.dp).clip(CircleShape).background(InstagramElevatedSurface)
                .border(0.8.dp, animAccent.copy(alpha = 0.4f), CircleShape).clickable { onShowToc() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.List, contentDescription = "TOC", tint = animAccent, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun CardBottomNavControls(
    currentPage: Int,
    totalPages: Int,
    currentCard: UIModuleCard?,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    val accent = currentCard?.accentColor ?: PwElectricBlue
    val animAccent by animateColorAsState(targetValue = accent, animationSpec = tween(400), label = "nav_accent")
    val isLast = currentPage == totalPages - 1

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF080810))
            .drawBehind { drawLine(animAccent.copy(0.25f), Offset(0f, 0f), Offset(size.width, 0f), 1.dp.toPx()) }
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Slim Prev button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (currentPage > 0) Color(0xFF161622) else Color.Transparent)
                .border(1.dp, if (currentPage > 0) Color.White.copy(0.12f) else Color.Transparent, RoundedCornerShape(20.dp))
                .clickable(enabled = currentPage > 0) { onPrev() }
                .padding(horizontal = 14.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (currentPage > 0) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(5.dp))
                    Text("Prev", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                } else {
                    Spacer(Modifier.width(36.dp))
                }
            }
        }

        // Slim Next / Finish button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.horizontalGradient(
                        if (isLast) listOf(PwElectricBlue, InstagramPink)
                        else listOf(animAccent.copy(0.85f), animAccent)
                    )
                )
                .border(1.dp, Color.White.copy(0.25f), RoundedCornerShape(20.dp))
                .clickable { onNext() }
                .padding(horizontal = 16.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(if (isLast) "Finish" else "Next", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Black)
                Spacer(Modifier.width(5.dp))
                Icon(if (isLast) Icons.Default.CheckCircle else Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
            }
        }
    }
}

@Composable
private fun CardLoadingSpinner(modifier: Modifier = Modifier) {
    val inf = rememberInfiniteTransition("load")
    val rot by inf.animateFloat(0f, 360f, infiniteRepeatable(tween(900, easing = LinearEasing)), "lr")
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Canvas(modifier = Modifier.size(56.dp)) {
                val r = size.minDimension / 2f - 4.dp.toPx()
                drawCircle(PwElectricBlue.copy(0.15f), r, style = Stroke(3.dp.toPx()))
                rotate(rot) {
                    drawArc(
                        brush = Brush.sweepGradient(listOf(Color.Transparent, PwElectricBlue)),
                        startAngle = 0f, sweepAngle = 240f, useCenter = false,
                        topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                        size = Size(r * 2f, r * 2f),
                        style = Stroke(3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
            Text("Loading module cards...", color = TextSecondary, fontSize = 14.sp)
        }
    }
}
