package com.moneymatters.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymatters.data.auth.AuthRepository
import com.moneymatters.data.auth.InputValidator
import com.moneymatters.data.auth.RateLimiter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val rateLimiter: RateLimiter
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _signupState = MutableStateFlow(SignupUiState())
    val signupState: StateFlow<SignupUiState> = _signupState.asStateFlow()

    private val _forgotPasswordState = MutableStateFlow(ForgotPasswordUiState())
    val forgotPasswordState: StateFlow<ForgotPasswordUiState> = _forgotPasswordState.asStateFlow()

    private val _profileSetupState = MutableStateFlow(ProfileSetupUiState())
    val profileSetupState: StateFlow<ProfileSetupUiState> = _profileSetupState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<AuthNavigationEvent>()
    val navigationEvent: SharedFlow<AuthNavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        updateRateLimitStatus()
    }

    private fun updateRateLimitStatus() {
        val status = rateLimiter.checkLoginRateLimit()
        _loginState.update {
            it.copy(
                isLockedOut = status.isLockedOut,
                remainingAttempts = status.remainingAttempts,
                lockoutRemainingMinutes = (status.lockOutRemainingMillis / 60000).toInt()
            )
        }
    }

    // --- LOGIN ---
    fun onLoginEmailChange(email: String) {
        val sanitized = InputValidator.sanitizeInput(email)
        _loginState.update {
            it.copy(
                emailInput = sanitized,
                isEmailValid = InputValidator.isValidEmail(sanitized),
                errorMessage = null
            )
        }
    }

    fun onLoginPasswordChange(password: String) {
        _loginState.update { it.copy(passwordInput = password, errorMessage = null, passwordError = null, generalError = null) }
    }

    fun toggleLoginPasswordVisibility() {
        _loginState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun submitLogin() = loginWithEmail()

    fun loginWithEmail() {
        val state = _loginState.value
        if (!state.isEmailValid || state.emailInput.isBlank() || state.passwordInput.isBlank()) {
            _loginState.update { it.copy(errorMessage = "Please enter valid email and password.") }
            return
        }

        viewModelScope.launch {
            _loginState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.loginWithEmail(state.emailInput, state.passwordInput)

            result.onSuccess { profile ->
                _loginState.update { it.copy(isLoading = false) }
                if (profile.isOnboardingComplete) {
                    _navigationEvent.emit(AuthNavigationEvent.NavigateToHome)
                } else {
                    _navigationEvent.emit(AuthNavigationEvent.NavigateToProfileSetup)
                }
            }.onFailure { exception ->
                updateRateLimitStatus()
                _loginState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.localizedMessage ?: "Login failed"
                    )
                }
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _loginState.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.loginWithGoogle(idToken)
                .onSuccess { profile ->
                    _loginState.update { it.copy(isLoading = false) }
                    if (profile.isOnboardingComplete) {
                        _navigationEvent.emit(AuthNavigationEvent.NavigateToHome)
                    } else {
                        _profileSetupState.update { it.copy(fullName = profile.name) }
                        _navigationEvent.emit(AuthNavigationEvent.NavigateToProfileSetup)
                    }
                }
                .onFailure { error ->
                    val userFriendlyMsg = if (error.message?.contains("Locked out") == true) {
                        error.localizedMessage
                    } else {
                        "Invalid email address or password. Please try again."
                    }
                    _loginState.update {
                        it.copy(isLoading = false, generalError = userFriendlyMsg)
                    }
                }
        }
    }

    fun loginAnonymously() {
        viewModelScope.launch {
            _loginState.update { it.copy(isLoading = true) }
            authRepository.loginAnonymously()
                .onSuccess {
                    _loginState.update { it.copy(isLoading = false) }
                    _navigationEvent.emit(AuthNavigationEvent.NavigateToHome)
                }
                .onFailure { error ->
                    _loginState.update {
                        it.copy(isLoading = false, errorMessage = error.localizedMessage)
                    }
                }
        }
    }

    // --- SIGNUP ---
    fun onSignupNameChange(name: String) {
        _signupState.update { it.copy(nameInput = InputValidator.sanitizeInput(name)) }
    }

    fun onSignupEmailChange(email: String) {
        val sanitized = InputValidator.sanitizeInput(email)
        _signupState.update {
            it.copy(
                emailInput = sanitized,
                isEmailValid = InputValidator.isValidEmail(sanitized)
            )
        }
    }

    fun onSignupPasswordChange(password: String) {
        val validation = InputValidator.validatePassword(password)
        _signupState.update {
            it.copy(
                passwordInput = password,
                passwordValidation = validation,
                passwordsMatch = password == it.confirmPasswordInput
            )
        }
    }

    fun onSignupConfirmPasswordChange(confirmPassword: String) {
        _signupState.update {
            it.copy(
                confirmPasswordInput = confirmPassword,
                passwordsMatch = it.passwordInput == confirmPassword
            )
        }
    }

    fun onTermsAcceptedChange(accepted: Boolean) {
        _signupState.update { it.copy(termsAccepted = accepted) }
    }

    fun signupWithEmail() {
        val state = _signupState.value
        if (!state.termsAccepted) {
            _signupState.update { it.copy(errorMessage = "Please accept Terms & Conditions") }
            return
        }
        if (!state.passwordValidation.isValid || !state.passwordsMatch) {
            _signupState.update { it.copy(errorMessage = "Please fulfill all password requirements") }
            return
        }

        viewModelScope.launch {
            _signupState.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.signupWithEmail(state.nameInput, state.emailInput, state.passwordInput)
                .onSuccess { profile ->
                    _signupState.update { it.copy(isLoading = false) }
                    _profileSetupState.update { it.copy(fullName = profile.name) }
                    _navigationEvent.emit(AuthNavigationEvent.NavigateToProfileSetup)
                }
                .onFailure { error ->
                    _signupState.update {
                        it.copy(isLoading = false, errorMessage = error.localizedMessage)
                    }
                }
        }
    }

    // --- FORGOT PASSWORD ---
    fun onForgotPasswordEmailChange(email: String) {
        val sanitized = InputValidator.sanitizeInput(email)
        _forgotPasswordState.update {
            it.copy(
                emailInput = sanitized,
                isEmailValid = InputValidator.isValidEmail(sanitized)
            )
        }
    }

    fun sendPasswordResetEmail() {
        val email = _forgotPasswordState.value.emailInput
        viewModelScope.launch {
            _forgotPasswordState.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.sendPasswordResetEmail(email)
                .onSuccess {
                    _forgotPasswordState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { error ->
                    _forgotPasswordState.update {
                        it.copy(isLoading = false, errorMessage = error.localizedMessage)
                    }
                }
        }
    }

    // --- PROFILE SETUP ---
    fun onCollegeChange(college: String) {
        _profileSetupState.update { it.copy(college = InputValidator.sanitizeInput(college)) }
    }

    fun onIncomeRangeSelect(range: String) {
        _profileSetupState.update { it.copy(selectedIncomeRange = range) }
    }

    fun saveProfileSetup() {
        val state = _profileSetupState.value
        viewModelScope.launch {
            _profileSetupState.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.saveProfileSetup(state.college, state.selectedIncomeRange)
                .onSuccess {
                    _profileSetupState.update { it.copy(isLoading = false) }
                    _navigationEvent.emit(AuthNavigationEvent.NavigateToHome)
                }
                .onFailure { error ->
                    _profileSetupState.update {
                        it.copy(isLoading = false, errorMessage = error.localizedMessage)
                    }
                }
        }
    }
}
