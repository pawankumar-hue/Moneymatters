package com.moneymatters.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymatters.core.data.SettingsStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val settingsStorage: SettingsStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashState())
    val uiState: StateFlow<SplashState> = _uiState.asStateFlow()

    init {
        checkStatusAndNavigate()
    }

    private fun checkStatusAndNavigate() {
        viewModelScope.launch {
            val isOnboardingCompleted = settingsStorage.isOnboardingCompleted().first()
            val isReducedMotion = settingsStorage.isReducedMotionEnabled().first()

            val target = if (isOnboardingCompleted) {
                SplashNavigationTarget.HOME
            } else {
                SplashNavigationTarget.ONBOARDING
            }

            _uiState.update {
                it.copy(
                    navigationTarget = target,
                    reducedMotion = isReducedMotion
                )
            }

            val holdDuration = if (isReducedMotion) 1500L else 3000L
            delay(holdDuration)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isComplete = true
                )
            }
        }
    }
}
