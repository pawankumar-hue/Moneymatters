package com.moneymatters.feature.auth

import com.moneymatters.data.auth.PasswordValidationResult
import com.moneymatters.data.auth.UserProfile

data class LoginUiState(
    val emailInput: String = "",
    val passwordInput: String = "",
    val isEmailValid: Boolean = true,
    val isPasswordVisible: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val remainingAttempts: Int = 5,
    val isLockedOut: Boolean = false,
    val lockoutRemainingMinutes: Int = 0
)

data class SignupUiState(
    val nameInput: String = "",
    val emailInput: String = "",
    val passwordInput: String = "",
    val confirmPasswordInput: String = "",
    val isEmailValid: Boolean = true,
    val passwordValidation: PasswordValidationResult = PasswordValidationResult(false, false, false, false, false, false),
    val passwordsMatch: Boolean = true,
    val termsAccepted: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class ForgotPasswordUiState(
    val emailInput: String = "",
    val isEmailValid: Boolean = true,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

data class ProfileSetupUiState(
    val fullName: String = "",
    val college: String = "",
    val selectedIncomeRange: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface AuthNavigationEvent {
    object NavigateToHome : AuthNavigationEvent
    object NavigateToProfileSetup : AuthNavigationEvent
    object NavigateToLogin : AuthNavigationEvent
    object NavigateToSignup : AuthNavigationEvent
    object NavigateToForgotPassword : AuthNavigationEvent
}
