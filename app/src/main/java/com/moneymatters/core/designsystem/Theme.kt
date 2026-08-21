package com.moneymatters.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Instagram & PW Pitch Black & Surface Palette
val InstagramBlack = Color(0xFF000000)
val InstagramDarkSurface = Color(0xFF121212)
val InstagramElevatedSurface = Color(0xFF1E1E1E)
val InstagramBorderDark = Color(0xFF262626)

// Physics Wallah (PW) Core Colors
val PwElectricBlue = Color(0xFF6356F6)
val PwCyanBlue = Color(0xFF3B82F6)
val PwAmberGold = Color(0xFFF59E0B)
val PwEmeraldGreen = Color(0xFF10B981)

// Backward Compatibility Color Aliases
val DeepObsidianBackground = InstagramBlack
val GlassSurfaceDark = InstagramDarkSurface
val GlassSurfaceCard = InstagramElevatedSurface
val DeepNavyBackground = InstagramBlack
val SurfaceDark = InstagramDarkSurface
val SurfaceCard = InstagramElevatedSurface

// Instagram Brand Colors & Signature Gradient
val InstagramPurple = Color(0xFF833AB4)
val InstagramPink = Color(0xFFE1306C)
val InstagramOrange = Color(0xFFFD1D1D)
val InstagramYellow = Color(0xFFFCAF45)
val InstagramHeartRed = Color(0xFFED4956)

// Fusion Gradients
val InstagramGradient = Brush.horizontalGradient(
    listOf(PwElectricBlue, InstagramPurple, InstagramPink, InstagramYellow)
)

val PwEdtechGradient = Brush.horizontalGradient(
    listOf(PwElectricBlue, PwCyanBlue, PwEmeraldGreen)
)

val EmeraldPrimary = PwEmeraldGreen
val IndigoSecondary = PwElectricBlue
val AmberGold = PwAmberGold

// Text Colors
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFA8A8A8)
val TextMuted = Color(0xFF65676B)

private val DarkColorScheme = darkColorScheme(
    primary = PwElectricBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E1B4B),
    onPrimaryContainer = Color(0xFFC7D2FE),
    secondary = InstagramPink,
    onSecondary = Color.White,
    background = InstagramBlack,
    onBackground = TextPrimary,
    surface = InstagramDarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = InstagramElevatedSurface,
    onSurfaceVariant = TextSecondary
)

@Composable
fun MoneyMattersTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content
    )
}
