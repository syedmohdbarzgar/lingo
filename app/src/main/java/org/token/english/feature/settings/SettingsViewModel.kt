package org.token.english.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.token.english.di.AppContainer
import org.token.english.domain.model.AppSettings
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.ThemeMode

data class SettingsUiState(
    val isLoading: Boolean = true,
    val settings: AppSettings? = null,
)

class SettingsViewModel(
    private val container: AppContainer,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            container.settingsRepository.settings.collect {
                _state.value = SettingsUiState(isLoading = false, settings = it)
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { container.settingsRepository.setThemeMode(mode) }
    }

    fun setDailyGoal(minutes: Int) {
        viewModelScope.launch { container.settingsRepository.setDailyGoalMinutes(minutes) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { container.settingsRepository.setSoundEnabled(enabled) }
    }

    /**
     * Persists the reminder flag; scheduling/canceling the alarm follows the
     * flag in EnglishApp's settings collector (single place for both, incl. boot).
     */
    fun setDailyReminderEnabled(enabled: Boolean) {
        viewModelScope.launch { container.settingsRepository.setDailyReminderEnabled(enabled) }
    }

    fun setLevel(level: LearningLevel) {
        viewModelScope.launch { container.settingsRepository.setLevel(level) }
    }

    fun resetProgress(onDone: () -> Unit) {
        viewModelScope.launch {
            container.progressRepository.reset()
            // Knowledge state is learner data and must go with the rest of it.
            container.knowledgeRepository.reset()
            onDone()
        }
    }
}
