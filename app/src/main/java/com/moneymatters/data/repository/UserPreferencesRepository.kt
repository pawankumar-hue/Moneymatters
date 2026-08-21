package com.moneymatters.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.moneymatters.data.model.UserProfileData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val USER_NAME = stringPreferencesKey("user_name")
        val XP = intPreferencesKey("xp")
        val STREAK_DAYS = intPreferencesKey("streak_days")
        val COMPLETED_MODULES = stringSetPreferencesKey("completed_modules")
        val SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
        val MONTHLY_INCOME = doublePreferencesKey("monthly_income")
        val UNLOCKED_BADGES = stringSetPreferencesKey("unlocked_badges")
    }

    val userProfile: Flow<UserProfileData> = context.dataStore.data.map { prefs ->
        val completedStrSet = prefs[Keys.COMPLETED_MODULES] ?: setOf("1")
        val completedIds = completedStrSet.mapNotNull { it.toIntOrNull() }.toSet()
        UserProfileData(
            name = prefs[Keys.USER_NAME] ?: "Financial Learner",
            xp = prefs[Keys.XP] ?: 120,
            streakDays = prefs[Keys.STREAK_DAYS] ?: 3,
            completedModuleIds = completedIds,
            selectedLanguageCode = prefs[Keys.SELECTED_LANGUAGE] ?: "en",
            monthlyIncome = prefs[Keys.MONTHLY_INCOME] ?: 25000.0,
            unlockedBadges = prefs[Keys.UNLOCKED_BADGES] ?: setOf("FIRST_STEP", "SAVER_NOVICE")
        )
    }.flowOn(Dispatchers.IO)

    suspend fun updateLanguage(langCode: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SELECTED_LANGUAGE] = langCode
        }
    }

    suspend fun completeModule(moduleId: Int, xpEarned: Int) {
        context.dataStore.edit { prefs ->
            val currentCompleted = prefs[Keys.COMPLETED_MODULES] ?: setOf()
            val newCompleted = currentCompleted + moduleId.toString()
            prefs[Keys.COMPLETED_MODULES] = newCompleted

            val currentXp = prefs[Keys.XP] ?: 120
            prefs[Keys.XP] = currentXp + xpEarned
        }
    }

    suspend fun resetProgress() {
        context.dataStore.edit { prefs ->
            prefs[Keys.XP] = 0
            prefs[Keys.STREAK_DAYS] = 1
            prefs[Keys.COMPLETED_MODULES] = setOf()
            prefs[Keys.UNLOCKED_BADGES] = setOf()
        }
    }

    suspend fun updateMonthlyIncome(income: Double) {
        context.dataStore.edit { prefs ->
            prefs[Keys.MONTHLY_INCOME] = income
        }
    }

    suspend fun addXp(amount: Int) {
        context.dataStore.edit { prefs ->
            val currentXp = prefs[Keys.XP] ?: 120
            prefs[Keys.XP] = currentXp + amount
        }
    }
}
