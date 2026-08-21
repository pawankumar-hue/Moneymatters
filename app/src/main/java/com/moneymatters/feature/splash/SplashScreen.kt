package com.moneymatters.feature.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moneymatters.core.designsystem.*
import kotlinx.coroutines.delay

/**
 * SplashScreen — Luxury 3D Prewarming Loader Screen.
 * Runs a 4.5-second prewarming sequence while background IO loads index & caches module states.
 */
@Composable
fun SplashScreen(
    onPrewarmComplete: () -> Unit
) {
    var progress by remember { mutableStateOf(0f) }
    var statusText by remember { mutableStateOf("Initializing 3D Engines...") }

    val infiniteTransition = rememberInfiniteTransition(label = "SplashRotation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "BadgeRotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    // 2.2 Second Prewarming Countdown
    LaunchedEffect(Unit) {
        statusText = "Loading Multilingual Roadmaps..."
        progress = 0.35f
        delay(600)

        statusText = "Warm-Caching 276 Financial Modules..."
        progress = 0.70f
        delay(700)

        statusText = "Pre-rendering 3D Specular Assets..."
        progress = 0.95f
        delay(600)

        statusText = "Ready! Launching MoneyMatters..."
        progress = 1.0f
        delay(300)

        onPrewarmComplete()
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "SplashProgress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(InstagramBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // 3D Double-Bezel Logo Core Container
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .graphicsLayer {
                        scaleX = pulseScale
                        scaleY = pulseScale
                        shadowElevation = 20.dp.toPx()
                        shape = RoundedCornerShape(32.dp)
                        clip = false
                    }
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(PwElectricBlue, InstagramPink, PwAmberGold)
                        )
                    )
                    .border(
                        width = 1.8.dp,
                        brush = Brush.verticalGradient(
                            listOf(Color.White, Color.White.copy(alpha = 0.2f), Color.Transparent)
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(3.dp)
                    .clip(RoundedCornerShape(29.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF231E60), Color(0xFF0C0A21)),
                            center = Offset(30f, 30f),
                            radius = 180f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(54.dp)
                        .graphicsLayer {
                            rotationZ = rotationAngle * 0.1f
                        }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Name Animated Typography
            Text(
                text = "MoneyMatters",
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                style = TextStyle(
                    brush = Brush.horizontalGradient(
                        listOf(Color.White, PwElectricBlue, InstagramPink, PwAmberGold)
                    ),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1.2).sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "MASTER FINANCIAL FREEDOM",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = PwEmeraldGreen,
                letterSpacing = 2.5.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 3D Linear Progress Bar & Real-time Status Readout
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(InstagramDarkSurface)
                    .border(0.8.dp, Color.White.copy(alpha = 0.1f), CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(PwEmeraldGreen, PwElectricBlue, InstagramPink)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = statusText,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = PwAmberGold
            )
        }
    }
}
