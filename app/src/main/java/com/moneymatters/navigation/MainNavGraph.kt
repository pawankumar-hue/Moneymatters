package com.moneymatters.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.moneymatters.core.designsystem.*
import com.moneymatters.core.i18n.AppLanguageManager
import com.moneymatters.feature.dashboard.HomeScreen
import com.moneymatters.feature.learn.LearnScreen
import com.moneymatters.feature.learn.ModuleDetailScreen
import com.moneymatters.feature.profile.ProfileScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavGraph(
    navController: NavHostController = rememberNavController(),
    currentLanguageCode: String = "en"
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Column {
                    HorizontalDivider(
                        color = InstagramBorderDark,
                        thickness = 0.8.dp
                    )
                    NavigationBar(
                        containerColor = InstagramBlack,
                        contentColor = TextPrimary,
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(58.dp)
                    ) {
                        bottomNavItems.forEach { item ->
                            val selected = currentRoute == item.route
                            val localizedTitle = when (item) {
                                Screen.Home -> AppLanguageManager.getString("home", currentLanguageCode)
                                Screen.Learn -> AppLanguageManager.getString("modules", currentLanguageCode)
                                Screen.Profile -> AppLanguageManager.getString("progress", currentLanguageCode)
                                else -> item.title
                            }

                            val animatedScale by animateFloatAsState(
                                targetValue = if (selected) 1.12f else 1.0f,
                                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                                label = "TabScale"
                            )

                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    if (currentRoute != item.route) {
                                        navController.navigate(item.route) {
                                            popUpTo(Screen.Home.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = {
                                    Box(
                                        modifier = Modifier
                                            .graphicsLayer {
                                                scaleX = animatedScale
                                                scaleY = animatedScale
                                                if (selected) {
                                                    shadowElevation = 8.dp.toPx()
                                                    shape = RoundedCornerShape(12.dp)
                                                    clip = false
                                                }
                                            }
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                brush = if (selected) {
                                                    Brush.verticalGradient(
                                                        listOf(
                                                            PwElectricBlue.copy(alpha = 0.35f),
                                                            InstagramPink.copy(alpha = 0.2f)
                                                        )
                                                    )
                                                } else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .border(
                                                width = if (selected) 1.dp else 0.dp,
                                                brush = if (selected) {
                                                    Brush.verticalGradient(
                                                        listOf(
                                                            Color.White.copy(alpha = 0.8f),
                                                            PwElectricBlue.copy(alpha = 0.4f)
                                                        )
                                                    )
                                                } else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .padding(horizontal = 12.dp, vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (selected) item.activeIcon else item.inactiveIcon,
                                            contentDescription = localizedTitle,
                                            tint = if (selected) Color.White else TextSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = localizedTitle,
                                        fontSize = 10.sp,
                                        fontWeight = if (selected) FontWeight.Black else FontWeight.Normal,
                                        color = if (selected) PwElectricBlue else TextSecondary
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            }
        },
        containerColor = InstagramBlack
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(paddingValues),
            enterTransition = { fadeIn(animationSpec = tween(180)) },
            exitTransition = { fadeOut(animationSpec = tween(180)) },
            popEnterTransition = { fadeIn(animationSpec = tween(180)) },
            popExitTransition = { fadeOut(animationSpec = tween(180)) }
        ) {
            composable(
                route = Screen.Splash.route,
                exitTransition = { fadeOut(animationSpec = tween(220)) }
            ) {
                com.moneymatters.feature.splash.SplashScreen(
                    onPrewarmComplete = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToModule = { modId ->
                        navController.navigate(Screen.ModuleDetail.createRoute(modId))
                    }
                )
            }

            composable(Screen.Learn.route) {
                LearnScreen(
                    onModuleClick = { modId ->
                        navController.navigate(Screen.ModuleDetail.createRoute(modId))
                    }
                )
            }

            composable(
                route = Screen.ModuleDetail.route,
                arguments = listOf(navArgument("moduleId") { type = NavType.IntType }),
                enterTransition = {
                    slideInVertically(initialOffsetY = { it / 3 }, animationSpec = tween(220)) + fadeIn(animationSpec = tween(220))
                },
                exitTransition = {
                    slideOutVertically(targetOffsetY = { it / 3 }, animationSpec = tween(220)) + fadeOut(animationSpec = tween(220))
                },
                popExitTransition = {
                    slideOutVertically(targetOffsetY = { it / 3 }, animationSpec = tween(220)) + fadeOut(animationSpec = tween(220))
                }
            ) { backStackEntry ->
                val moduleId = backStackEntry.arguments?.getInt("moduleId") ?: 1
                ModuleDetailScreen(
                    moduleId = moduleId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen()
            }
        }
    }
}
