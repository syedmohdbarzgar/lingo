package org.token.english.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.token.english.di.AppContainer
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.LessonState
import org.token.english.domain.model.Skill
import org.token.english.domain.model.StudyStats
import org.token.english.domain.model.TodayPlan

data class HomeUiState(
    val isLoading: Boolean = true,
    val greeting: String = "",
    val level: LearningLevel = LearningLevel.A1,
    val plan: TodayPlan? = null,
    val stats: StudyStats? = null,
    val mastery: Map<Skill, Float> = emptyMap(),
    val access: org.token.english.core.billing.AccessLevel = org.token.english.core.billing.AccessLevel.TRIAL,
    val trialRemainingMillis: Long = 0L,
)

class HomeViewModel(
    private val container: AppContainer,
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
    private var access: org.token.english.core.billing.AccessLevel =
        org.token.english.core.billing.AccessLevel.TRIAL
    private var trialRemainingMillis: Long = 0L
    private var trialAndSubscription: org.token.english.core.billing.TrialAndSubscription? = null

    init {
        viewModelScope.launch {
            container.settingsRepository.observeTrialAndSubscription().collect { ts ->
                trialAndSubscription = ts
                recomputeAccess()
            }
        }
        viewModelScope.launch {
            // Trial remaining decays with wall time, but the flow only emits on
            // data changes — tick so the banner and lock state stay honest.
            while (true) {
                kotlinx.coroutines.delay(ACCESS_TICK_MS)
                if (trialAndSubscription != null) recomputeAccess()
            }
        }
        viewModelScope.launch {
            container.settingsRepository.settings.collect {
                level = it.level
                goalMinutes = it.dailyGoalMinutes
                rebuild()
            }
        }
        viewModelScope.launch {
            container.lessonRepository.observeLessons().collect {
                lessons = it
                rebuild()
            }
        }
        viewModelScope.launch {
            container.lessonRepository.observeLessonStates().collect {
                states = it
                rebuild()
            }
        }
        viewModelScope.launch {
            container.progressRepository.observeMastery().collect {
                mastery = it
                rebuild()
            }
        }
        viewModelScope.launch {
            // Shared aggregate pipeline (P7) — same stream Progress shows.
            container.studyStats.collect {
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
            delay(DUE_TICK_MS)
        }
    }.flatMapLatest { now -> container.reviewRepository.observeDueCount(now) }

    private fun recomputeAccess() {
        val ts = trialAndSubscription ?: return
        val now = System.currentTimeMillis()
        val state = ts.trialClockState()
        val elapsed = android.os.SystemClock.elapsedRealtime()
        trialRemainingMillis = org.token.english.core.billing.TrialClock.remainingMs(state, now, elapsed)
        access = org.token.english.core.billing.EntitlementPolicy.level(
            now = now,
            trialRemainingMs = trialRemainingMillis,
            subscriptionUntil = ts.subscriptionUntil,
        )
        rebuild()
    }

    private fun rebuild() {
        val completedIds = states.filter { it.completed }.map { it.lessonId }.toSet()
        val nextLesson = org.token.english.domain.engine.nextLessonFor(lessons, completedIds, level)

        val plan = container.learningPlanner.createPlan(
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
            stats = stats,
            mastery = mastery,
            access = access,
            trialRemainingMillis = trialRemainingMillis,
        )
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
