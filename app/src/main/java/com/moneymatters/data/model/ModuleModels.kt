package com.moneymatters.data.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ModuleIndex(
    val languages: Map<String, String>,
    val categories: List<String>,
    val modules: List<ModuleSummary>
)

@Immutable
@Serializable
data class ModuleSummary(
    val id: Int,
    val title: String,
    val category: String,
    val language: String,
    val readTimeMinutes: Int,
    val xpReward: Int,
    val filename: String
)

@Immutable
@Serializable
data class ModuleDetail(
    val id: Int,
    val title: String,
    val category: String,
    val language: String,
    val readTimeMinutes: Int,
    val wordCount: Int,
    val xpReward: Int,
    val chapters: List<String>,
    val content: String
)

@Immutable
@Serializable
data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)

enum class ModuleCategory(val displayName: String) {
    FOUNDATIONS("Foundations"),
    BUDGETING("Budgeting & Saving"),
    BANKING("Banking & Credit"),
    INVESTING("Investing & Markets"),
    TAX_GOVT("Tax & Govt Schemes"),
    REAL_WORLD("Real World & Career")
}

@Immutable
@Serializable
data class TopicData(
    val id: String = "",
    val title: String = "",
    val emoji: String = "📚",
    val color: String = "#3B82F6",
    val description: String = "",
    val cards: List<CardItemData> = emptyList()
)

@Immutable
@Serializable
data class CardItemData(
    val id: String = "",
    val topicId: String = "",
    val topicTitle: String = "",
    val cardIndex: Int = 1,
    val totalCardsInTopic: Int = 1,
    val title: String = "",
    val content: String = "",
    val imagePrompt: String? = null,
    val color: String = "#3B82F6",
    val emoji: String = "💡",
    val interactiveType: String? = null,
    val choiceData: ChoiceSimData? = null,
    val quizData: CardQuizData? = null,
    val calcData: CardCalcData? = null
)

@Immutable
@Serializable
data class ChoiceSimData(
    val scenario: String = "",
    val choices: List<ChoiceOptionData> = emptyList()
)

@Immutable
@Serializable
data class ChoiceOptionData(
    val text: String = "",
    val isCorrect: Boolean = false,
    val consequence: String = ""
)

@Immutable
@Serializable
data class CardQuizData(
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswerIndex: Int = 0,
    val explanation: String = ""
)

@Immutable
@Serializable
data class CardCalcData(
    val calcType: String = "compounding",
    val formula: String = "none",
    val inputs: List<CalcInputData> = emptyList()
)

@Immutable
@Serializable
data class CalcInputData(
    val label: String = "",
    val min: Float = 0f,
    val max: Float = 100f,
    val defaultValue: Float = 10f,
    val step: Float = 1f,
    val unit: String = ""
)

