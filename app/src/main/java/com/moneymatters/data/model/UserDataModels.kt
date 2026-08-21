package com.moneymatters.data.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class UserProfileData(
    val name: String = "Financial Learner",
    val xp: Int = 120,
    val streakDays: Int = 3,
    val completedModuleIds: Set<Int> = setOf(1),
    val selectedLanguageCode: String = "en",
    val monthlyIncome: Double = 25000.0,
    val unlockedBadges: Set<String> = setOf("FIRST_STEP", "SAVER_NOVICE")
) {
    val level: Int get() = (xp / 200) + 1
    val levelTitle: String get() = when (level) {
        1 -> "Money Rookie"
        2 -> "Smart Saver"
        3 -> "Budget Boss"
        4 -> "Investor Apprentice"
        5 -> "Wealth Builder"
        6 -> "Financial Guru"
        else -> "Financial Maestro"
    }
    val xpInCurrentLevel: Int get() = xp % 200
    val xpForNextLevel: Int get() = 200
}

@Immutable
@Serializable
data class BudgetItem(
    val id: String,
    val title: String,
    val amount: Double,
    val category: BudgetCategory,
    val date: String
)

enum class BudgetCategory(val displayName: String, val recommendedPercentage: Float) {
    NEED("Needs (50%)", 0.50f),
    WANT("Wants (30%)", 0.30f),
    SAVING("Savings & Debt (20%)", 0.20f)
}

@Immutable
@Serializable
data class FinancialGoal(
    val id: String,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val category: String,
    val targetDate: String
)
