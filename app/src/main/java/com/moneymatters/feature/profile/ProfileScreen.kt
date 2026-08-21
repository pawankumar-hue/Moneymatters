package com.moneymatters.feature.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.graphicsLayer
import com.moneymatters.core.audio.SoundManager
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moneymatters.core.designsystem.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneymatters.core.designsystem.*
import com.moneymatters.core.i18n.AppLanguageManager

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val profile = uiState.userProfile
    val langCode = profile.selectedLanguageCode
    var showLangDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    @OptIn(ExperimentalFoundationApi::class)
    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(InstagramBlack)
        ) {
        // HEADER
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = AppLanguageManager.getString("my_progress", langCode),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        letterSpacing = (-0.4).sp
                    )
                    Text(
                        text = "${AppLanguageManager.getString("level", langCode)} ${profile.level} · ${profile.levelTitle}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(InstagramElevatedSurface)
                        .border(0.8.dp, InstagramBorderDark, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            HorizontalDivider(color = InstagramBorderDark)
        }

        // AVATAR + 3-STAT COLUMNS
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(PwElectricBlue, InstagramPink, PwAmberGold))
                        )
                        .padding(2.5.dp)
                        .clip(CircleShape)
                        .background(InstagramBlack)
                        .padding(2.5.dp)
                        .clip(CircleShape)
                        .background(InstagramElevatedSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = profile.name.take(1).uppercase(),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                }

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProgressStatColumn("${profile.completedModuleIds.size}", AppLanguageManager.getString("completed", langCode))
                    ProgressStatColumn("${profile.xp}", AppLanguageManager.getString("total_xp", langCode))
                    ProgressStatColumn("${profile.streakDays}", AppLanguageManager.getString("day_streak", langCode))
                }
            }
        }

        // BIO
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = profile.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${profile.levelTitle} · ${AppLanguageManager.getString("level", langCode)} ${profile.level}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PwElectricBlue
                )
                Text(
                    text = "23 ${AppLanguageManager.getString("financial_modules", langCode)}",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${AppLanguageManager.getString("content_language", langCode)}: ${AppLanguageManager.supportedLanguages[langCode] ?: langCode}",
                    fontSize = 12.sp,
                    color = PwElectricBlue,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // ╔══════════════════════════════════════════════════════════════╗
        // ║  USER PREFERENCES & PROJECT SETTINGS HUB — 3D Double-Bezel ║
        // ╚══════════════════════════════════════════════════════════════╝
        item {
            var isAudioMuted by remember { mutableStateOf(SoundManager.isMuted()) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .graphicsLayer {
                        shadowElevation = 10.dp.toPx()
                        shape = RoundedCornerShape(22.dp)
                        clip = false
                    }
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF272730), Color(0xFF16161D))
                        )
                    )
                    .border(
                        width = 1.2.dp,
                        brush = Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.2f), Color.Transparent)
                        ),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .padding(2.5.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(InstagramDarkSurface)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "PROJECT & USER SETTINGS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = PwElectricBlue,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Setting Row 1: Language Selection
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showLangDialog = true }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ThreeDIconBadge(
                                icon = Icons.Outlined.Translate,
                                badgeSize = 38.dp,
                                iconSize = 18.dp,
                                primaryColor = PwElectricBlue,
                                secondaryColor = InstagramPink,
                                elevation = 4.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = AppLanguageManager.getString("switch_language", langCode),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = AppLanguageManager.supportedLanguages[langCode] ?: langCode,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }

                    HorizontalDivider(color = InstagramBorderDark.copy(alpha = 0.6f), modifier = Modifier.padding(vertical = 4.dp))

                    // Setting Row 2: Haptic Audio Effects Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                isAudioMuted = SoundManager.toggleMute()
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ThreeDIconBadge(
                                icon = if (isAudioMuted) Icons.Outlined.VolumeOff else Icons.Outlined.VolumeUp,
                                badgeSize = 38.dp,
                                iconSize = 18.dp,
                                primaryColor = if (isAudioMuted) Color(0xFFEF4444) else PwEmeraldGreen,
                                secondaryColor = Color.Black,
                                elevation = 4.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Haptic Sound Effects",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (isAudioMuted) "Audio Muted 🔇" else "Audio Enabled 🔊",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        Switch(
                            checked = !isAudioMuted,
                            onCheckedChange = {
                                isAudioMuted = SoundManager.toggleMute()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PwEmeraldGreen,
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = InstagramElevatedSurface
                            )
                        )
                    }

                    HorizontalDivider(color = InstagramBorderDark.copy(alpha = 0.6f), modifier = Modifier.padding(vertical = 4.dp))

                    // Setting Row 3: Reset Progress
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showResetDialog = true }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ThreeDIconBadge(
                                icon = Icons.Outlined.Refresh,
                                badgeSize = 38.dp,
                                iconSize = 18.dp,
                                primaryColor = PwAmberGold,
                                secondaryColor = Color(0xFFF59E0B),
                                elevation = 4.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = AppLanguageManager.getString("reset_progress", langCode),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Clear XP, streak & completion history",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // ACHIEVEMENTS
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = AppLanguageManager.getString("achievements", langCode),
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                letterSpacing = (-0.3).sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val badges = listOf(
                    Triple("First Step", Icons.Default.Star, PwElectricBlue),
                    Triple("Saver", Icons.Default.Savings, PwEmeraldGreen),
                    Triple("SIP Starter", Icons.Default.TrendingUp, PwAmberGold),
                    Triple("Tax Saver", Icons.Default.AccountBalance, InstagramPink),
                    Triple("Streak 7", Icons.Default.LocalFireDepartment, InstagramHeartRed)
                )
                items(badges, key = { it.first }) { (label, icon, color) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ThreeDIconBadge(
                            icon = icon,
                            badgeSize = 60.dp,
                            iconSize = 26.dp,
                            shape = CircleShape,
                            primaryColor = color,
                            secondaryColor = color.copy(alpha = 0.5f),
                            elevation = 8.dp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = label, fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = InstagramBorderDark)
        }

        // COMPLETED MODULES GRID
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppLanguageManager.getString("completed_modules", langCode),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                PwBatchBadge(text = "${profile.completedModuleIds.size}/23")
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(profile.completedModuleIds.toList().chunked(3), key = { it.first() }) { rowIds ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowIds.forEach { modId ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(InstagramDarkSurface)
                            .border(0.8.dp, InstagramBorderDark, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = PwEmeraldGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "M$modId", fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            Text(text = AppLanguageManager.getString("completed", langCode), fontSize = 10.sp, color = PwEmeraldGreen)
                        }
                    }
                }
                repeat(3 - rowIds.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }

    // Language Picker Dialog
    if (showLangDialog) {
        AlertDialog(
            onDismissRequest = { showLangDialog = false },
            title = { Text(AppLanguageManager.getString("select_language", langCode), color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.height(340.dp)) {
                    items(AppLanguageManager.supportedLanguages.toList()) { (code, name) ->
                        val isSelected = profile.selectedLanguageCode == code
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectLanguage(code)
                                    showLangDialog = false
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = name,
                                fontSize = 15.sp,
                                color = if (isSelected) PwElectricBlue else TextPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            if (isSelected) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = PwElectricBlue)
                            }
                        }
                        HorizontalDivider(color = InstagramBorderDark)
                    }
                }
            },
            confirmButton = {},
            containerColor = InstagramDarkSurface
        )
    }

    // Reset Progress Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Progress?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to reset all completed modules, total XP, and streak? This action cannot be undone.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetProgress()
                        showResetDialog = false
                    }
                ) {
                    Text("Reset Everything", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = TextPrimary)
                }
            },
            containerColor = InstagramDarkSurface
        )
    }
}
}

@Composable
fun ProgressStatColumn(number: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = number, fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
        Text(text = label, fontSize = 11.sp, color = TextSecondary)
    }
}
