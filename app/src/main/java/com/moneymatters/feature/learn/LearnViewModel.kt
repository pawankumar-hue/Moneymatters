package com.moneymatters.feature.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymatters.data.model.ModuleDetail
import com.moneymatters.data.model.ModuleIndex
import com.moneymatters.data.model.ModuleSummary
import com.moneymatters.data.model.QuizQuestion
import com.moneymatters.data.model.UserProfileData
import com.moneymatters.data.repository.ModuleRepository
import com.moneymatters.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LearnUiState(
    val selectedLanguage: String = "en",
    val availableLanguages: Map<String, String> = emptyMap(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val modules: List<ModuleSummary> = emptyList(),
    val userProfile: UserProfileData = UserProfileData(),
    val currentModuleDetail: ModuleDetail? = null,
    val currentTopicCards: List<com.moneymatters.data.model.TopicData> = emptyList(),
    val currentQuiz: List<QuizQuestion> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class LearnViewModel @Inject constructor(
    private val moduleRepository: ModuleRepository,
    private val userPrefsRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearnUiState())
    val uiState: StateFlow<LearnUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val index = moduleRepository.getModuleIndex()
            _uiState.update {
                it.copy(
                    availableLanguages = index.languages,
                    categories = index.categories
                )
            }

            userPrefsRepository.userProfile.collect { profile ->
                val lang = profile.selectedLanguageCode
                val modules = moduleRepository.getModulesForLanguage(lang)
                _uiState.update {
                    it.copy(
                        userProfile = profile,
                        selectedLanguage = lang,
                        modules = modules
                    )
                }
            }
        }
    }

    fun selectLanguage(langCode: String) {
        viewModelScope.launch {
            userPrefsRepository.updateLanguage(langCode)
            val modules = moduleRepository.getModulesForLanguage(langCode)
            _uiState.update {
                it.copy(
                    selectedLanguage = langCode,
                    modules = modules
                )
            }
        }
    }

    fun selectCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun loadModuleDetail(moduleId: Int) {
        viewModelScope.launch {
            val lang = _uiState.value.selectedLanguage
            val detail = moduleRepository.getModuleDetail(lang, moduleId)
            val topicCards = moduleRepository.getModuleTopicCards(moduleId)
            val quiz = moduleRepository.getQuizForModule(moduleId)
            _uiState.update {
                it.copy(
                    currentModuleDetail = detail,
                    currentTopicCards = topicCards,
                    currentQuiz = quiz,
                    isLoading = false
                )
            }
        }
    }


    fun completeCurrentModule(moduleId: Int, xp: Int) {
        viewModelScope.launch {
            userPrefsRepository.completeModule(moduleId, xp)
        }
    }
}
