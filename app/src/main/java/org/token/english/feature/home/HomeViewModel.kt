package org.token.english.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.token.english.core.billing.AccessLevel
import org.token.english.core.billing.AccessReason
import org.token.english.core.billing.EntitlementPolicy
import org.token.english.core.billing.TrialAndSubscription
import org.token.english.core.billing.TrialClock
import org.token.english.core.common.runCatchingCancellable
import org.token.english.domain.engine.LearningPlanner
import org.token.english.domain.engine.nextLessonFor
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.LessonState
import org.token.english.domain.model.LearningActionType
import org.token.english.domain.model.Skill
import org.token.english.domain.model.StudyStats
import org.token.english.domain.model.TodayPlan
import org.token.english.domain.repository.LessonRepository
import org.token.english.domain.repository.ProgressRepository
import org.token.english.domain.repository.ReviewRepository
import org.token.english.domain.repository.SettingsRepository
import org.token.english.domain.usecase.FocusPlan
import org.token.english.domain.usecase.GetFocusPlanUseCase
import org.token.english.domain.usecase.GetTodayPlanUseCase

data class HomeUiState(
    val isLoading: Boolean = true,
    val greeting: String = "",
    val level: LearningLevel = LearningLevel.A1,
    val plan: TodayPlan? = null,
    /**
     * The remediation engine's weak-spot decision (checklist B-1): the node to
     * drill, why, and where. `null` when nothing is weak enough to be worth a
     * focused block. The screen renders it; it never picks the node.
     */
    val focus: FocusPlan? = null,
    val stats: StudyStats? = null,
    val mastery: Map<Skill, Float> = emptyMap(),
    /** True once every lesson at the learner's current level is completed. */
    val levelComplete: Boolean = false,
    /** The next level above the current one that still has pending lessons. */
    val nextLevel: LearningLevel? = null,
    val access: AccessLevel = AccessLevel.TRIAL,
    val trialRemainingMillis: Long = 0L,
    /** Why access is held — a free companion grant is not a paid subscription. */
    val accessReason: AccessReason = AccessReason.NONE,
    /** True while the free-access companion app (org.token.zaribar) is installed. */
    val companionInstalled: Boolean = false,
)

/**
 * Home screen state. Takes exactly what it reads (checklist B-6) — the
 * repositories it observes, the shared stats stream, the planner it asks for a
 * day, and the companion-access signals it follows — never the whole container.
 */
class HomeViewModel(
    private val settingsRepository: SettingsRepository,
    private val lessonRepository: LessonRepository,
    private val progressRepository: ProgressRepository,
    private val reviewRepository: ReviewRepository,
    private val studyStats: StateFlow<StudyStats>,
    private val planner: LearningPlanner,
    private val getTodayPlan: GetTodayPlanUseCase,
    private val getFocusPlan: GetFocusPlanUseCase,
    private val companionInstalled: StateFlow<Boolean>,
    private val refreshCompanion: suspend () -> Boolean,
    /** Wall-clock tick for the trial countdown; tests pass 0 to keep it still. */
    private val accessTickMillis: Long = ACCESS_TICK_MS,
    /** Re-check of the due count; tests pass 0 to read it once. */
    private val dueTickMillis: Long = DUE_TICK_MS,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private var level: LearningLevel = LearningLevel.A1
    private var goalMinutes: Int = 15
    private var lessons: List<Lesson> = emptyList()
    private var states: List<LessonState> = emptyList()
    private var mastery: Map<Skill, Float> = emptyMap()
    private var stats: StudyStats? = null
    private var dueCount: Int = 0
    private var access: AccessLevel = AccessLevel.TRIAL
    private var accessReason: AccessReason = AccessReason.NONE
    private var companionIsInstalled: Boolean = false
    private var trialRemainingMillis: Long = 0L
    private var trialAndSubscription: TrialAndSubscription? = null

    init {
        viewModelScope.launch {
            settingsRepository.observeTrialAndSubscription().collect { ts ->
                trialAndSubscription = ts
                recomputeAccess()
            }
        }
        viewModelScope.launch {
            // Free companion grant: re-read the install state (the user may have
            // installed/removed zaribar since the process started) and follow it.
            refreshCompanion()
            companionInstalled.collect { installed ->
                companionIsInstalled = installed
                recomputeAccess()
            }
        }
        if (accessTickMillis > 0) {
            viewModelScope.launch {
                // Trial remaining decays with wall time, but the flow only emits on
                // data changes — tick so the banner and lock state stay honest.
                while (true) {
                    delay(accessTickMillis)
                    if (trialAndSubscription != null) recomputeAccess()
                }
            }
        }
        viewModelScope.launch {
            settingsRepository.settings.collect {
                level = it.level
                goalMinutes = it.dailyGoalMinutes
                rebuild()
            }
        }
        viewModelScope.launch {
            lessonRepository.observeLessons().collect {
                lessons = it
                rebuild()
            }
        }
        viewModelScope.launch {
            lessonRepository.observeLessonStates().collect {
                states = it
                rebuild()
            }
        }
        viewModelScope.launch {
            progressRepository.observeMastery().collect {
                mastery = it
                rebuild()
            }
        }
        viewModelScope.launch {
            // Shared aggregate pipeline (P7) — same stream Progress shows.
            studyStats.collect {
                stats = it
                rebuild()
            }
        }
        viewModelScope.launch {
            // Room only re-emits when review_item changes; `now` must be refreshed
            // too, otherwise newly-due cards stay invisible until the next write
            // (checklist: fixed-now → minute ticker).
            dueCountFlow().collect {
                dueCount = it
                rebuild()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun dueCountFlow() = flow {
        while (true) {
            emit(System.currentTimeMillis())
            if (dueTickMillis <= 0) break
            delay(dueTickMillis)
        }
    }.flatMapLatest { now -> reviewRepository.observeDueCount(now) }

    private fun recomputeAccess() {
        val ts = trialAndSubscription ?: return
        val now = System.currentTimeMillis()
        val state = ts.trialClockState()
        val elapsed = android.os.SystemClock.elapsedRealtime()
        trialRemainingMillis = TrialClock.remainingMs(state, now, elapsed)
        val entitlement = EntitlementPolicy.entitlement(
            now = now,
            trialRemainingMs = trialRemainingMillis,
            subscriptionUntil = ts.subscriptionUntil,
            companionAppInstalled = companionIsInstalled,
        )
        access = entitlement.level
        accessReason = entitlement.reason
        rebuild()
    }

    private fun rebuild() {
        val completedIds = states.filter { it.completed }.map { it.lessonId }.toSet()
        val nextLesson = nextLessonFor(lessons, completedIds, level)

        // Level progression: a learner who has finished every lesson at their
        // current level needs a clear way up, not just the next lesson card.
        val currentLevelPending = lessons.any { it.level == level && it.id !in completedIds }
        val nextLevel = LearningLevel.entries
            .filter { it.ordinal > level.ordinal }
            .firstOrNull { lvl -> lessons.any { it.level == lvl && it.id !in completedIds } }
        val levelComplete = lessons.isNotEmpty() && !currentLevelPending

        // Instant, from cached data — the card never waits on a query.
        val plan = planner.createPlan(
            dueReviewCount = dueCount,
            nextLesson = nextLesson,
            masteryBySkill = mastery,
            targetMinutes = goalMinutes,
            todayStudySeconds = stats?.todayStudySeconds ?: 0L,
        )
        _state.value = HomeUiState(
            isLoading = false,
            greeting = greeting(),
            level = level,
            plan = plan,
            // The engine-driven plan and weak spot are re-fetched below; keep the
            // previous ones visible until the new decision arrives, so a stats tick
            // does not make the cards blink out.
            focus = _state.value.focus,
            stats = stats,
            mastery = mastery,
            access = access,
            accessReason = accessReason,
            companionInstalled = companionIsInstalled,
            trialRemainingMillis = trialRemainingMillis,
            levelComplete = levelComplete,
            nextLevel = nextLevel,
        )
        // …then enrich it with the adaptive planner's ordered, justified actions
        // (checklist B-1), which need a suspend read of the knowledge graph.
        refreshAdaptivePlan()
    }

    private var planJob: Job? = null

    /**
     * Asks `GetTodayPlanUseCase` for the full plan. The engine owns the ordering
     * and the reasons; this ViewModel only stores what it returns.
     */
    private fun refreshAdaptivePlan() {
        planJob?.cancel()
        planJob = viewModelScope.launch {
            // runCatchingCancellable, not runCatching (checklist B-9): the plain
            // version also catches CancellationException, so a cancelled screen
            // would keep querying and write state after its scope died.
            val full = runCatchingCancellable { getTodayPlan(System.currentTimeMillis()) }.getOrNull()
                ?: return@launch
            _state.update { current -> current.copy(plan = full) }
            // …and ask the remediation engine whether there is a specific node
            // worth drilling (checklist B-1). The daily plan's own remediation
            // action names the node; with none the engine picks the weakest one that
            // has evidence (error isolation — not "the whole subject").
            val weakSpot = full.actions
                .firstOrNull { it.type == LearningActionType.REMEDIATE }
                ?.itemId
            val focus = runCatchingCancellable { getFocusPlan(weakSpot) }.getOrNull()
            _state.update { current -> current.copy(focus = focus) }
        }
    }

    /** Moves the learner up to the next level that still has lessons to do. */
    fun advanceLevel() {
        val target = _state.value.nextLevel ?: return
        viewModelScope.launch { settingsRepository.setLevel(target) }
    }

    private fun greeting(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "صبح بخیر"
            hour < 18 -> "عصر بخیر"
            else -> "شب بخیر"
        }
    }

    private companion object {
        const val DUE_TICK_MS = 60_000L
        const val ACCESS_TICK_MS = 60_000L
    }
}
