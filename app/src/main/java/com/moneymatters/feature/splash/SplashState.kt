package com.moneymatters.feature.splash

enum class SplashNavigationTarget {
    ONBOARDING,
    HOME
}

data class SplashState(
    val isLoading: Boolean = true,
    val isComplete: Boolean = false,
    val navigationTarget: SplashNavigationTarget = SplashNavigationTarget.ONBOARDING,
    val hasProAccess: Boolean = false,
    val reducedMotion: Boolean = false
)
