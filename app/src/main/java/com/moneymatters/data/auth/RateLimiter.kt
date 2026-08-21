package com.moneymatters.data.auth

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

data class RateLimitStatus(
    val isLockedOut: Boolean,
    val remainingAttempts: Int,
    val lockOutRemainingMillis: Long = 0L,
    val backoffDelayMillis: Long = 0L
)

@Singleton
class RateLimiter @Inject constructor() {
    private var failedAttempts = 0
    private var lastAttemptTimestamp = 0L
    private var lockoutTimestamp = 0L

    private val MAX_ATTEMPTS = 5
    private val LOCKOUT_DURATION_MILLIS = 30 * 60 * 1000L // 30 mins
    private val ATTEMPT_WINDOW_MILLIS = 15 * 60 * 1000L  // 15 mins
    private val RESET_PASSWORD_LIMIT_MILLIS = 5 * 60 * 1000L // 5 mins

    private var lastPasswordResetTimestamp = 0L

    @Synchronized
    fun checkLoginRateLimit(): RateLimitStatus {
        val now = System.currentTimeMillis()

        // Check if locked out
        if (lockoutTimestamp > 0 && now < lockoutTimestamp) {
            val remainingLockout = lockoutTimestamp - now
            return RateLimitStatus(
                isLockedOut = true,
                remainingAttempts = 0,
                lockOutRemainingMillis = remainingLockout
            )
        }

        // Reset window if 15 mins passed since last attempt
        if (lastAttemptTimestamp > 0 && (now - lastAttemptTimestamp > ATTEMPT_WINDOW_MILLIS)) {
            failedAttempts = 0
            lockoutTimestamp = 0L
        }

        val remaining = (MAX_ATTEMPTS - failedAttempts).coerceAtLeast(0)
        return RateLimitStatus(
            isLockedOut = false,
            remainingAttempts = remaining
        )
    }

    @Synchronized
    fun recordFailedAttempt(): RateLimitStatus {
        val now = System.currentTimeMillis()
        lastAttemptTimestamp = now
        failedAttempts++

        if (failedAttempts >= MAX_ATTEMPTS) {
            lockoutTimestamp = now + LOCKOUT_DURATION_MILLIS
            return RateLimitStatus(
                isLockedOut = true,
                remainingAttempts = 0,
                lockOutRemainingMillis = LOCKOUT_DURATION_MILLIS
            )
        }

        // Exponential backoff: 2s -> 4s -> 8s -> 16s -> 32s
        val backoff = (2.0.pow(failedAttempts.toDouble())).toLong() * 1000L
        val remaining = (MAX_ATTEMPTS - failedAttempts).coerceAtLeast(0)

        return RateLimitStatus(
            isLockedOut = false,
            remainingAttempts = remaining,
            backoffDelayMillis = backoff
        )
    }

    @Synchronized
    fun resetAttempts() {
        failedAttempts = 0
        lastAttemptTimestamp = 0L
        lockoutTimestamp = 0L
    }

    @Synchronized
    fun canRequestPasswordReset(): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastPasswordResetTimestamp < RESET_PASSWORD_LIMIT_MILLIS) {
            return false
        }
        lastPasswordResetTimestamp = now
        return true
    }
}
