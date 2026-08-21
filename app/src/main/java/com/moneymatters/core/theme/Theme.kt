package com.moneymatters.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = SunriseAccentPrimary,
    primaryContainer = SunriseAccentSoft,
    background = SunriseBgBase,
    surface = SunriseBgElevated,
    onPrimary = SunriseBgElevated,
    onBackground = SunriseTextPrimary,
    onSurface = SunriseTextPrimary,
    onSurfaceVariant = SunriseTextSecondary,
    outline = SunriseTextMuted
)

private val DarkColorScheme = darkColorScheme(
    primary = MidnightAccentPrimary,
    primaryContainer = MidnightAccentSoft,
    background = MidnightBgBase,
    surface = MidnightBgElevated,
    onPrimary = MidnightBgElevated,
    onBackground = MidnightTextPrimary,
    onSurface = MidnightTextPrimary,
    onSurfaceVariant = MidnightTextSecondary,
    outline = MidnightTextMuted
)

@Composable
fun MoneyMattersTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
