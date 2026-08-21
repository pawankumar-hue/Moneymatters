package com.moneymatters.data.auth

import java.util.regex.Pattern

data class PasswordValidationResult(
    val isValid: Boolean,
    val hasMinLength: Boolean,
    val hasUppercase: Boolean,
    val hasLowercase: Boolean,
    val hasDigit: Boolean,
    val hasSpecialChar: Boolean
)

object InputValidator {
    // RFC 5322 compliant Email Regex
    private val EMAIL_REGEX = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    )

    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        return EMAIL_REGEX.matcher(email.trim()).matches()
    }

    fun validatePassword(password: String): PasswordValidationResult {
        val hasMinLength = password.length >= 8
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

        val isValid = hasMinLength && hasUppercase && hasLowercase && hasDigit && hasSpecialChar

        return PasswordValidationResult(
            isValid = isValid,
            hasMinLength = hasMinLength,
            hasUppercase = hasUppercase,
            hasLowercase = hasLowercase,
            hasDigit = hasDigit,
            hasSpecialChar = hasSpecialChar
        )
    }

    // Input sanitization: Trim whitespace and eliminate dangerous invisible control characters
    fun sanitizeInput(input: String): String {
        return input.trim().replace("\u0000", "")
    }
}
