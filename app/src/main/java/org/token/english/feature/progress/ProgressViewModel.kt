package org.token.english.feature.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Skill
import org.token.english.domain.model.StudyStats
import org.token.english.domain.repository.KnowledgeRepository
import org.token.english.domain.repository.LessonRepository
import org.token.english.domain.repository.ProgressRepository
import org.token.english.domain.repository.SettingsRepository

data class ProgressUiState(
    val isLoading: Boolean = true,
    val level: LearningLevel = LearningLevel.A1,
    val stats: StudyStats? = null,
    val mastery: Map<Skill, Float> = emptyMap(),
    val totalLessons: Int = 0,
    /** Curriculum nodes touched at least once, and how many the bundle ships. */
    val knowledgePractised: Int = 0,
    val knowledgeTotal: Int = 0,
    /** Persian title of the weakest practised node, shown as a study hint. */
    val weakestKnowledge: String? = null,
)

/** Progress screen state (checklist B-6): narrow, testable dependencies. */
class ProgressViewModel(
    private val settingsRepository: SettingsRepository,
    private val lessonRepository: LessonRepository,
    private val progressRepository: ProgressRepository,
    private val knowledgeRepository: KnowledgeRepository,
    private val studyStats: StateFlow<StudyStats>,
) : ViewModel() {

    private val _state = MutableStateFlow(ProgressUiState())
    val state: StateFlow<ProgressUiState> = _state.asStateFlow()

    private var totalLessons: Int = 0
    private var level: LearningLevel = LearningLevel.A1
    private var knowledgeStates: List<KnowledgeState> = emptyList()
    private var knowledgeTitles: Map<String, String> = emptyMap()
    private var knowledgeTotal: Int = 0

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect {
                level = it.level
                rebuild()
            }
        }
        viewModelScope.launch {
            // Shared aggregate pipeline (P7) — same stream Home shows.
            studyStats.collect {
                _state.value = _state.value.copy(isLoading = false, stats = it, mastery = _state.value.mastery)
                rebuild()
            }
        }
        viewModelScope.launch {
            progressRepository.observeMastery().collect {
                _state.value = _state.value.copy(mastery = it)
                rebuild()
            }
        }
        viewModelScope.launch {
            lessonRepository.observeLessons().collect {
                totalLessons = it.size
                rebuild()
            }
        }
        // Curriculum labels are authored content, read once — the graph is 70-odd
        // rows and never changes during a session.
        viewModelScope.launch {
            val items = knowledgeRepository.allItems()
            knowledgeTitles = items.associate { it.id to it.titleFa }
            knowledgeTotal = items.size
            rebuild()
        }
        viewModelScope.launch {
            knowledgeRepository.observeStates().collect {
                knowledgeStates = it
                rebuild()
            }
        }
    }

    private fun rebuild() {
        val stats = _state.value.stats
        val weakest = knowledgeStates
            .minByOrNull { it.mastery }
            ?.let { knowledgeTitles[it.itemId] }
        _state.value = ProgressUiState(
            isLoading = false,
            level = level,
            stats = stats,
            mastery = _state.value.mastery,
            totalLessons = totalLessons,
            knowledgePractised = knowledgeStates.size,
            knowledgeTotal = knowledgeTotal,
            weakestKnowledge = weakest,
        )
    }
}
