package com.moneymatters.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            "secure_auth_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    companion object {
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_LAST_ACTIVITY = "last_activity_timestamp"
        private const val KEY_USER_ID = "user_id"
        private const val INACTIVITY_LIMIT_MILLIS = 30L * 24 * 60 * 60 * 1000 // 30 days
        private const val INACTIVITY_WARNING_MILLIS = 25L * 24 * 60 * 60 * 1000 // 25 days
    }

    fun saveTokens(jwtToken: String, refreshToken: String, userId: String) {
        encryptedPrefs.edit()
            .putString(KEY_JWT_TOKEN, jwtToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .putString(KEY_USER_ID, userId)
            .putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis())
            .apply()
    }

    fun getJwtToken(): String? = encryptedPrefs.getString(KEY_JWT_TOKEN, null)

    fun updateLastActivity() {
        encryptedPrefs.edit()
            .putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis())
            .apply()
    }

    fun isSessionExpired(): Boolean {
        val lastActivity = encryptedPrefs.getLong(KEY_LAST_ACTIVITY, 0L)
        if (lastActivity == 0L) return false
        return (System.currentTimeMillis() - lastActivity) > INACTIVITY_LIMIT_MILLIS
    }

    fun isSessionNearExpiry(): Boolean {
        val lastActivity = encryptedPrefs.getLong(KEY_LAST_ACTIVITY, 0L)
        if (lastActivity == 0L) return false
        val diff = System.currentTimeMillis() - lastActivity
        return diff in INACTIVITY_WARNING_MILLIS..INACTIVITY_LIMIT_MILLIS
    }

    fun clearSession() {
        encryptedPrefs.edit().clear().apply()
    }
}
