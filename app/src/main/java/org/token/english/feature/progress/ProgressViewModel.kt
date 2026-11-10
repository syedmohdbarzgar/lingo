package org.token.english.feature.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.token.english.core.common.catchLoadFailure
import org.token.english.core.common.runCatchingCancellable
import org.token.english.domain.engine.LessonMastery
import org.token.english.domain.engine.MissProfile
import org.token.english.domain.engine.SkillProfile
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.LessonState
import org.token.english.domain.model.Skill
import org.token.english.domain.model.StudyStats
import org.token.english.domain.repository.KnowledgeRepository
import org.token.english.domain.repository.LessonRepository
import org.token.english.domain.repository.ProgressRepository
import org.token.english.domain.repository.SettingsRepository
import org.token.english.domain.usecase.GetMissProfileUseCase
import org.token.english.domain.usecase.GetSkillProfilesUseCase

data class ProgressUiState(
    val isLoading: Boolean = true,
    val level: LearningLevel = LearningLevel.A1,
    val stats: StudyStats? = null,
    val mastery: Map<Skill, Float> = emptyMap(),
    /**
     * The three independent learning tracks (P2): each with its mastery, the
     * evidence behind it and an honest status — so a thin track cannot read as a
     * finished one.
     */
    val profiles: List<SkillProfile> = emptyList(),
    val totalLessons: Int = 0,
    /**
     * Completed lessons whose language is also known — no node of theirs needs today's
     * practice. Always a subset of the completed count, never a separate tally of taps.
     */
    val masteredLessons: Int = 0,
    /** Curriculum nodes touched at least once, and how many the bundle ships. */
    val knowledgePractised: Int = 0,
    val knowledgeTotal: Int = 0,
    /** Persian title of the weakest practised node, shown as a study hint. */
    val weakestKnowledge: String? = null,
    /**
     * What the recent answers went wrong about (the error taxonomy). Null until there is
     * enough evidence to say anything, and null again if the read itself fails — that
     * insight is an addition to the screen, never a reason to blank it.
     */
    val missProfile: MissProfile? = null,
    /** A read threw — the screen says so instead of leaving the spinner up. */
    val loadFailed: Boolean = false,
)

/** Progress screen state (checklist B-6): narrow, testable dependencies. */
class ProgressViewModel(
    private val settingsRepository: SettingsRepository,
    private val lessonRepository: LessonRepository,
    private val progressRepository: ProgressRepository,
    private val knowledgeRepository: KnowledgeRepository,
    private val getSkillProfiles: GetSkillProfilesUseCase,
    private val getMissProfile: GetMissProfileUseCase,
    private val studyStats: StateFlow<StudyStats>,
) : ViewModel() {

    private val _state = MutableStateFlow(ProgressUiState())
    val state: StateFlow<ProgressUiState> = _state.asStateFlow()

    private var totalLessons: Int = 0
    private var level: LearningLevel = LearningLevel.A1
    private var profiles: List<SkillProfile> = emptyList()
    private var missProfile: MissProfile? = null
    private var knowledgeStates: List<KnowledgeState> = emptyList()
    private var knowledgeTitles: Map<String, String> = emptyMap()
    private var knowledgeTotal: Int = 0
    /** The graph itself, for the lesson→nodes mapping the mastered count asks for. */
    private var knowledgeItems: List<KnowledgeItem> = emptyList()
    private var lessonStates: List<LessonState> = emptyList()

    /** Declared before `init` — Kotlin initialises properties in source order. */
    private var loadJobs: List<Job> = emptyList()

    init {
        startLoading()
    }

    /**
     * Starts the reads; [retry] re-runs this. Every collector is guarded, because a
     * Room failure used to end the collector quietly and leave the spinner up: only
     * the stats stream ever cleared `isLoading`.
     */
    private fun startLoading() {
        loadJobs.forEach { it.cancel() }
        loadJobs = listOf(
            viewModelScope.launch {
                settingsRepository.settings.catchLoadFailure(::onLoadFailure).collect {
                    level = it.level
                    rebuild()
                }
            },
            viewModelScope.launch {
                // Shared aggregate pipeline (P7) — same stream Home shows.
                studyStats.catchLoadFailure(::onLoadFailure).collect {
                    _state.value = _state.value.copy(isLoading = false, stats = it, mastery = _state.value.mastery)
                    rebuild()
                }
            },
            viewModelScope.launch {
                progressRepository.observeMastery().catchLoadFailure(::onLoadFailure).collect {
                    _state.value = _state.value.copy(mastery = it)
                    rebuild()
                    // A grade moves a track's mastery and its evidence together, so the
                    // profile is rebuilt from the same signal (P2).
                    refreshProfiles()
                }
            },
            viewModelScope.launch {
                lessonRepository.observeLessons().catchLoadFailure(::onLoadFailure).collect {
                    totalLessons = it.size
                    rebuild()
                }
            },
            // Which lessons are finished — the half of the standing that is not
            // evidence. Without it "completed" and "mastered" could only ever agree.
            viewModelScope.launch {
                lessonRepository.observeLessonStates().catchLoadFailure(::onLoadFailure).collect {
                    lessonStates = it
                    rebuild()
                }
            },
            // Curriculum labels are authored content, read once — the graph is 70-odd
            // rows and never changes during a session.
            viewModelScope.launch {
                val items = runCatchingCancellable { knowledgeRepository.allItems() }
                    .getOrElse { error ->
                        onLoadFailure(error)
                        return@launch
                    }
                knowledgeTitles = items.associate { it.id to it.titleFa }
                knowledgeItems = items
                knowledgeTotal = items.size
                rebuild()
            },
            viewModelScope.launch {
                knowledgeRepository.observeStates().catchLoadFailure(::onLoadFailure).collect {
                    knowledgeStates = it
                    rebuild()
                    // Lesson answers are the other half of a track's evidence.
                    refreshProfiles()
                }
            },
        )
    }

    private fun onLoadFailure(error: Throwable) {
        android.util.Log.w("ProgressViewModel", "progress read failed", error)
        _state.update { it.copy(isLoading = false, loadFailed = true) }
    }

    /** The error state's action: re-subscribe to the reads (the data is local). */
    fun retry() {
        _state.update { it.copy(isLoading = true, loadFailed = false) }
        startLoading()
    }

    /**
     * Recomputes the three tracks off the flow (checklist B-6: the screen keeps
     * narrow dependencies; the combination of sources lives in the use case).
     */
    private fun refreshProfiles() {
        viewModelScope.launch {
            val loaded = runCatchingCancellable { getSkillProfiles() }
                .getOrElse { error ->
                    onLoadFailure(error)
                    return@launch
                }
            profiles = loaded
            // Same signal, second question: the mastery writes are exactly when the
            // answer log changes, so the miss mix is recomputed here rather than on a
            // stream of its own. Its failure is contained — see the state property.
            missProfile = runCatchingCancellable { getMissProfile() }.getOrElse { error ->
                android.util.Log.w("ProgressViewModel", "miss profile read failed", error)
                missProfile
            }
            rebuild()
        }
    }

    private fun rebuild() {
        val stats = _state.value.stats
        val weakest = knowledgeStates
            .minByOrNull { it.mastery }
            ?.let { knowledgeTitles[it.itemId] }
        val completedIds = lessonStates.filter { it.completed }.mapTo(mutableSetOf()) { it.lessonId }
        val nodesByLesson = knowledgeItems
            .flatMap { item -> item.lessonIds.map { it to item } }
            .groupBy({ it.first }, { it.second })
        val mastered = LessonMastery.masteredCount(
            completed = completedIds,
            nodesByLesson = nodesByLesson,
            states = knowledgeStates.associateBy { it.itemId },
        )
        _state.value = ProgressUiState(
            isLoading = false,
            level = level,
            stats = stats,
            mastery = _state.value.mastery,
            profiles = profiles,
            totalLessons = totalLessons,
            masteredLessons = mastered,
            knowledgePractised = knowledgeStates.size,
            knowledgeTotal = knowledgeTotal,
            weakestKnowledge = weakest,
            missProfile = missProfile,
            loadFailed = _state.value.loadFailed,
        )
    }
}
