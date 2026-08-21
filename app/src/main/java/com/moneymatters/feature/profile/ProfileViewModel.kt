package com.moneymatters.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymatters.data.model.UserProfileData
import com.moneymatters.data.repository.ModuleRepository
import com.moneymatters.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val userProfile: UserProfileData = UserProfileData(),
    val availableLanguages: Map<String, String> = emptyMap()
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPrefsRepository: UserPreferencesRepository,
    private val moduleRepository: ModuleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val index = moduleRepository.getModuleIndex()
            _uiState.update { it.copy(availableLanguages = index.languages) }

            userPrefsRepository.userProfile.collect { profile ->
                _uiState.update { it.copy(userProfile = profile) }
            }
        }
    }

    fun selectLanguage(langCode: String) {
        viewModelScope.launch {
            userPrefsRepository.updateLanguage(langCode)
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            userPrefsRepository.resetProgress()
        }
    }
}
