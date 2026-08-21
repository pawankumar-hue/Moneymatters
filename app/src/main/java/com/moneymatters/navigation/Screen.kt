package com.moneymatters.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector
) {
    // Only Module-focused screens
    object Splash : Screen("splash", "Splash", Icons.Default.Home, Icons.Outlined.Home)
    object Home : Screen("home", "Home", Icons.Default.Home, Icons.Outlined.Home)
    object Learn : Screen("learn", "Modules", Icons.Default.AutoStories, Icons.Outlined.AutoStories)
    object Profile : Screen("profile", "Progress", Icons.Default.BarChart, Icons.Outlined.BarChart)

    object ModuleDetail : Screen("module_detail/{moduleId}", "Module", Icons.Default.AutoStories, Icons.Outlined.AutoStories) {
        fun createRoute(moduleId: Int) = "module_detail/$moduleId"
    }
}

// Minimal bottom nav focused purely on Modules & Progress
val bottomNavItems = listOf(
    Screen.Home,
    Screen.Learn,
    Screen.Profile
)
