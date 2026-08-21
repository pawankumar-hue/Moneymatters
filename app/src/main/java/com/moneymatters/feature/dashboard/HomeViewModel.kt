package com.moneymatters.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymatters.data.model.ModuleSummary
import com.moneymatters.data.model.UserProfileData
import com.moneymatters.data.repository.ModuleRepository
import com.moneymatters.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val userProfile: UserProfileData = UserProfileData(),
    val nextRecommendedModule: ModuleSummary? = null,
    val dailyTip: String = "Pay Yourself First — Set up auto-debit for savings the day income arrives.",
    val modules: List<ModuleSummary> = emptyList(),
    val completedModuleIds: Set<Int> = emptySet(),
    val userLevel: Int = 1,
    val levelTitle: String = "Money Novice",
    val totalXp: Int = 0,
    val streakDays: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPrefsRepository: UserPreferencesRepository,
    private val moduleRepository: ModuleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            moduleRepository.prewarmCache()
        }
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            userPrefsRepository.userProfile.collect { profile ->
                val modules = moduleRepository.getModulesForLanguage(profile.selectedLanguageCode)
                val nextModId = (profile.completedModuleIds.maxOrNull() ?: 0) + 1
                val nextMod = modules.find { it.id == nextModId } ?: modules.firstOrNull()

                _uiState.update {
                    it.copy(
                        userProfile = profile,
                        nextRecommendedModule = nextMod,
                        modules = modules,
                        completedModuleIds = profile.completedModuleIds,
                        userLevel = profile.level,
                        levelTitle = profile.levelTitle,
                        totalXp = profile.xp,
                        streakDays = profile.streakDays,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun selectLanguage(langCode: String) {
        viewModelScope.launch {
            userPrefsRepository.updateLanguage(langCode)
        }
    }
}
