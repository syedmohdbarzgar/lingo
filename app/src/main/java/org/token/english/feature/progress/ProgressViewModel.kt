package org.token.english.feature.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.token.english.di.AppContainer
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Skill
import org.token.english.domain.model.StudyStats

data class ProgressUiState(
    val isLoading: Boolean = true,
    val level: LearningLevel = LearningLevel.A1,
    val stats: StudyStats? = null,
    val mastery: Map<Skill, Float> = emptyMap(),
    val totalLessons: Int = 0,
)

class ProgressViewModel(
    private val container: AppContainer,
) : ViewModel() {

    private val _state = MutableStateFlow(ProgressUiState())
    val state: StateFlow<ProgressUiState> = _state.asStateFlow()

    private var totalLessons: Int = 0
    private var level: LearningLevel = LearningLevel.A1

    init {
        viewModelScope.launch {
            container.settingsRepository.settings.collect {
                level = it.level
                rebuild()
            }
        }
        viewModelScope.launch {
            // Shared aggregate pipeline (P7) — same stream Home shows.
            container.studyStats.collect {
                _state.value = _state.value.copy(isLoading = false, stats = it, mastery = _state.value.mastery)
                rebuild()
            }
        }
        viewModelScope.launch {
            container.progressRepository.observeMastery().collect {
                _state.value = _state.value.copy(mastery = it)
                rebuild()
            }
        }
        viewModelScope.launch {
            container.lessonRepository.observeLessons().collect {
                totalLessons = it.size
                rebuild()
            }
        }
    }

    private fun rebuild() {
        val stats = _state.value.stats
        _state.value = ProgressUiState(
            isLoading = false,
            level = level,
            stats = stats,
            mastery = _state.value.mastery,
            totalLessons = totalLessons,
        )
    }
}
