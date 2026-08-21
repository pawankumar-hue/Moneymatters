package com.moneymatters.data.repository

import android.content.Context
import com.moneymatters.data.model.ModuleDetail
import com.moneymatters.data.model.ModuleIndex
import com.moneymatters.data.model.ModuleSummary
import com.moneymatters.data.model.QuizQuestion
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModuleRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }
    private var cachedIndex: ModuleIndex? = null
    private val cachedDetails = ConcurrentHashMap<String, ModuleDetail>()

    suspend fun getModuleIndex(): ModuleIndex = withContext(Dispatchers.IO) {
        cachedIndex?.let { return@withContext it }
        try {
            val jsonString = context.assets.open("modules/index.json").bufferedReader().use { it.readText() }
            val parsed = json.decodeFromString<ModuleIndex>(jsonString)
            cachedIndex = parsed
            parsed
        } catch (e: Exception) {
            ModuleIndex(emptyMap(), emptyList(), emptyList())
        }
    }

    suspend fun prewarmCache() = withContext(Dispatchers.IO) {
        val index = getModuleIndex()
        // Pre-warm index filtering for all supported languages
        listOf("en", "hi", "hinglish", "bn", "te", "mr", "ta", "ur", "gu", "kn", "ml", "pa").forEach { lang ->
            index.modules.filter { it.language == lang }
        }
    }

    suspend fun getModulesForLanguage(langCode: String): List<ModuleSummary> = withContext(Dispatchers.IO) {
        val index = getModuleIndex()
        index.modules.filter { it.language == langCode }
    }

    suspend fun getModuleDetail(langCode: String, moduleId: Int): ModuleDetail? = withContext(Dispatchers.IO) {
        val cacheKey = "$langCode-$moduleId"
        cachedDetails[cacheKey]?.let { return@withContext it }

        try {
            val assetPath = "modules/$langCode/module_$moduleId.json"
            val jsonString = context.assets.open(assetPath).bufferedReader().use { it.readText() }
            val parsed = json.decodeFromString<ModuleDetail>(jsonString)
            cachedDetails[cacheKey] = parsed
            parsed
        } catch (e: Exception) {
            try {
                val fallbackPath = "modules/en/module_$moduleId.json"
                val jsonString = context.assets.open(fallbackPath).bufferedReader().use { it.readText() }
                val fallbackParsed = json.decodeFromString<ModuleDetail>(jsonString)
                cachedDetails[cacheKey] = fallbackParsed
                fallbackParsed
            } catch (ex: Exception) {
                null
            }
        }
    }

    private val cachedTopicCards = ConcurrentHashMap<Int, List<com.moneymatters.data.model.TopicData>>()

    suspend fun getModuleTopicCards(moduleId: Int): List<com.moneymatters.data.model.TopicData> = withContext(Dispatchers.IO) {
        cachedTopicCards[moduleId]?.let { return@withContext it }

        try {
            val assetPath = "module_cards/module_$moduleId.json"
            val jsonString = context.assets.open(assetPath).bufferedReader().use { it.readText() }
            val parsed = json.decodeFromString<List<com.moneymatters.data.model.TopicData>>(jsonString)
            if (parsed.isNotEmpty()) {
                cachedTopicCards[moduleId] = parsed
                return@withContext parsed
            }
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }


    suspend fun getQuizForModule(moduleId: Int): List<QuizQuestion> = withContext(Dispatchers.IO) {
        when (moduleId) {
            1 -> listOf(
                QuizQuestion(
                    id = 1,
                    question = "What main problem did money solve over the ancient Barter System?",
                    options = listOf(
                        "High taxation rate",
                        "Double coincidence of wants",
                        "Lack of gold coins",
                        "Government inflation control"
                    ),
                    correctAnswerIndex = 1,
                    explanation = "Money solved the 'double coincidence of wants' where both trading parties had to want each other's goods."
                ),
                QuizQuestion(
                    id = 2,
                    question = "Which type of income requires one-time effort but generates repeated earnings?",
                    options = listOf("Active Income", "Passive Income", "Salary Income", "Daily Wages"),
                    correctAnswerIndex = 1,
                    explanation = "Passive income (e.g. YouTube ad revenue, dividend stocks, digital products) continues generating returns after initial work."
                )
            )
            2 -> listOf(
                QuizQuestion(
                    id = 1,
                    question = "In the 50/30/20 Budgeting Rule, what does the 20% represent?",
                    options = listOf("Wants & Dining out", "Rent & Utilities", "Savings & Investments/Debt Payoff", "Entertainment"),
                    correctAnswerIndex = 2,
                    explanation = "20% of net income should be allocated towards future savings, investments, and emergency fund reserves."
                )
            )
            3 -> listOf(
                QuizQuestion(
                    id = 1,
                    question = "What is the recommended size of an Emergency Fund for a student or professional?",
                    options = listOf("1 month expenses", "3 to 6 months of living expenses", "10 years income", "₹5,000 fixed"),
                    correctAnswerIndex = 1,
                    explanation = "An emergency fund should cover at least 3 to 6 months of essential living expenses in liquid accounts."
                )
            )
            else -> listOf(
                QuizQuestion(
                    id = 1,
                    question = "Why is compounding interest considered the 8th wonder of the world in finance?",
                    options = listOf(
                        "It pays fixed interest only once",
                        "It earns interest on both principal and previously accumulated interest",
                        "It is guaranteed by law",
                        "It requires zero capital"
                    ),
                    correctAnswerIndex = 1,
                    explanation = "Compounding grows wealth exponentially over time because returns earn their own returns."
                )
            )
        }
    }
}
