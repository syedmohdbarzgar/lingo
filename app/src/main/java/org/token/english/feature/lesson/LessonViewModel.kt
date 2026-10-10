package org.token.english.feature.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.token.english.core.audio.AudioPlayer
import org.token.english.core.audio.Speaker
import org.token.english.domain.engine.AdaptiveExerciseSelector
import org.token.english.domain.engine.DefaultMasteryProfileEngine
import org.token.english.domain.engine.ExerciseDimension
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.MasteryDimension
import org.token.english.domain.model.MasteryProfile
import org.token.english.domain.model.VocabularyItem
import org.token.english.domain.model.shuffledForDisplay
import org.token.english.domain.repository.LessonRepository
import org.token.english.domain.repository.ProgressRepository
import org.token.english.domain.usecase.CompleteLessonUseCase
import org.token.english.domain.usecase.ExerciseOutcome
import org.token.english.domain.usecase.FocusPlan
import org.token.english.domain.usecase.GetFocusPlanUseCase
import org.token.english.domain.usecase.SubmitExerciseUseCase
import kotlin.random.Random

enum class LessonStage { INTRO, EXERCISES }

data class LessonUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val lesson: Lesson? = null,
    val exercises: List<Exercise> = emptyList(),
    val vocabulary: List<VocabularyItem> = emptyList(),
    /** Intro first (vocab, examples, grammar tip), then the exercises (P4). */
    val stage: LessonStage = LessonStage.INTRO,
    val currentIndex: Int = 0,
    val selectedOption: Int? = null,
    val textAnswer: String = "",
    val outcome: ExerciseOutcome? = null,
    val correctCount: Int = 0,
    /** Exercises missed at least once — marked in the summary (P4). */
    val weakAnswers: List<String> = emptyList(),
    /** Attempts made so far (a missed exercise may be asked again). */
    val askedCount: Int = 0,
    /** Exercises still waiting in the session queue (excludes the current one). */
    val remainingCount: Int = 0,
    /** True when the just-answered exercise will be asked again later (a miss). */
    val repeatsCurrent: Boolean = false,
    val completed: Boolean = false,
    val isPlaying: Boolean = false,
    /** No English TTS voice on this device — the listening card falls back to text (B-3). */
    val audioUnavailable: Boolean = false,
    /** The last playback attempt failed (engine error) — show a retry hint (B-3). */
    val audioFailed: Boolean = false,
    /** Focused practice: the engine's decision this session obeys (B-1), else null. */
    val focus: FocusBanner? = null,
    /**
     * The kind of knowing this session's own answers showed to be weakest, or
     * `null` when nothing weak has been observed yet (B-1). The UI labels it.
     */
    val weakestDimension: MasteryDimension? = null,
) {
    val currentExercise: Exercise? get() = exercises.getOrNull(currentIndex)
    val answeredCorrectly: Boolean? get() = outcome?.correct
    val totalExercises: Int get() = exercises.size

    /** Attempts total: done + pending + current — grows when a miss is re-queued. */
    val visibleTotal: Int get() = askedCount + remainingCount + 1

    val progress: Float
        get() = if (visibleTotal <= 0) 0f else askedCount.toFloat() / visibleTotal
}

/**
 * The engine's decision for a focused session (checklist B-1): what is being
 * drilled, why now, and what has to be reached for it to count as fixed. The
 * screen renders this; it never decides any of it (technical spec §19).
 */
data class FocusBanner(
    val titleFa: String,
    val reasonFa: String,
    val masteryPercent: Int,
    val targetPercent: Int,
)

sealed interface LessonEvent {
    data class SelectOption(val index: Int) : LessonEvent
    data class TextAnswerChanged(val value: String) : LessonEvent
    data object Submit : LessonEvent
    data object Next : LessonEvent
    data object ReplayAudio : LessonEvent
    data object StartExercises : LessonEvent
    data class SpeakText(val text: String) : LessonEvent
}

/**
 * Lesson session state (checklist B-6): the repositories, use cases, audio seam
 * and foreground signal it actually uses, instead of the whole container.
 */
class LessonViewModel(
    private val lessonId: String,
    private val contentSeeded: StateFlow<Boolean>,
    private val lessonRepository: LessonRepository,
    private val progressRepository: ProgressRepository,
    private val submitExercise: SubmitExerciseUseCase,
    private val completeLesson: CompleteLessonUseCase,
    private val audioPlayer: AudioPlayer,
    private val speaker: Speaker,
    private val isAppInForeground: () -> Boolean,
    /** Focused practice: the node whose block this session should run (B-1). */
    private val getFocusPlan: GetFocusPlanUseCase,
    private val focusItemId: String? = null,
    /** Study-time tick; tests pass 0 to disable it. */
    private val studyTickMillis: Long = STUDY_TICK_MILLIS,
) : ViewModel() {

    private val _state = MutableStateFlow(LessonUiState())
    val state: StateFlow<LessonUiState> = _state.asStateFlow()

    private var finished: Boolean = false

    /** Blocks a second submit() while the first grade is still in flight (double-tap). */
    private var submitting: Boolean = false

    /** Indices still to show this session; the head has already been removed. */
    private val pending = ArrayDeque<Int>()

    /** Exercises already re-asked once — a miss is repeated at most one time. */
    private val requeued = mutableSetOf<String>()

    /** Per-dimension mastery of this session's own answers (audit §2/§6). */
    private var profile: MasteryProfile = MasteryProfile()
    private val profileEngine = DefaultMasteryProfileEngine()
    private val selector = AdaptiveExerciseSelector()

    init {
        if (studyTickMillis > 0) {
            viewModelScope.launch {
                // Study time is credited in fixed ticks while the app is visible —
                // time spent backgrounded never counts toward the daily goal (P7).
                while (true) {
                    kotlinx.coroutines.delay(studyTickMillis)
                    creditStudyTime()
                }
            }
        }
        viewModelScope.launch {
            // B-3: watch the TTS engine — when no English voice is available the
            // listening card switches to its text fallback instead of hanging.
            audioPlayer.isEnglishAvailable.collect { available ->
                _state.update { s ->
                    when (available) {
                        false -> s.copy(audioUnavailable = true, isPlaying = false)
                        true -> if (s.audioUnavailable) s.copy(audioUnavailable = false) else s
                        null -> s
                    }
                }
            }
        }
        viewModelScope.launch {
            // Hold the screen until the curriculum bundle is in Room — a fresh
            // install seeds in the background and must not show "lesson missing".
            contentSeeded.first { it }
            val lesson = lessonRepository.getLesson(lessonId)
            // Display-time shuffles: option order and word-bank order derive from
            // the exercise id — stable per question, never gameable (P1/P4).
            val exercises = lessonRepository.getExercises(lessonId).map { exercise ->
                when {
                    exercise is Exercise.MultipleChoice -> exercise.shuffledForDisplay()
                    exercise is Exercise.Translation && exercise.bank.isNotEmpty() ->
                        exercise.copy(bank = exercise.bank.shuffled(Random(exercise.id.hashCode())))

                    else -> exercise
                }
            }
            // Focused practice (checklist B-1): when the learner arrived from the
            // engine's weak-spot card, the remediation engine decides which of the
            // lesson's exercises are evidence about that node and the session shows
            // only those, in the authored order. A subset, not a reshuffle.
            val focus = focusItemId?.let { runCatching { getFocusPlan(it) }.getOrNull() }
            val focusedIds = focus?.exerciseIds.orEmpty()
            val sessionExercises = exercises.filter { it.id in focusedIds }.ifEmpty { exercises }
            if (lesson == null || sessionExercises.isEmpty()) {
                _state.update {
                    it.copy(isLoading = false, error = "درس در دسترس نیست. محتوای ذخیره‌شده را بررسی کنید.")
                }
                return@launch
            }
            val vocabulary = lessonRepository.getVocabulary(lessonId)
            val saved = lessonRepository.getLessonState(lessonId)
            // A focused block is short and always starts at its beginning: the saved
            // lesson cursor points into the full lesson, not into this subset.
            val startIndex = when {
                focus != null -> 0
                saved?.completed == true -> 0
                else -> (saved?.currentIndex ?: 0)
            }
            val index = startIndex.coerceIn(0, sessionExercises.lastIndex.coerceAtLeast(0))
            pending.addAll(index until sessionExercises.size)
            val firstIndex = pending.removeFirstOrNull() ?: index
            // A resumed lesson (cursor > 0) has already shown the intro, and a
            // focused block is a drill rather than a first teach — it starts on the
            // exercises the engine picked.
            val stage = if (focus == null && index == 0 && vocabulary.isNotEmpty()) {
                LessonStage.INTRO
            } else {
                LessonStage.EXERCISES
            }
            _state.update {
                it.copy(
                    isLoading = false,
                    lesson = lesson,
                    exercises = sessionExercises,
                    vocabulary = vocabulary,
                    stage = stage,
                    currentIndex = firstIndex,
                    remainingCount = pending.size,
                    focus = focus?.let { plan ->
                        FocusBanner(
                            titleFa = plan.titleFa,
                            reasonFa = plan.reasonFa,
                            masteryPercent = (plan.mastery * 100).toInt(),
                            targetPercent = (plan.reassessThreshold * 100).toInt(),
                        )
                    },
                )
            }
            if (stage == LessonStage.EXERCISES && sessionExercises[firstIndex] is Exercise.Listening) playAudio()
        }
    }

    fun onEvent(event: LessonEvent) {
        when (event) {
            is LessonEvent.SelectOption -> _state.update {
                if (it.outcome != null) it else it.copy(selectedOption = event.index)
            }

            is LessonEvent.TextAnswerChanged -> _state.update {
                if (it.outcome != null) it else it.copy(textAnswer = event.value)
            }

            LessonEvent.Submit -> submit()
            LessonEvent.Next -> next()
            LessonEvent.ReplayAudio -> playAudio()
            LessonEvent.StartExercises -> startExercises()
            is LessonEvent.SpeakText -> speaker.speak(
                text = event.text,
                onDone = { _state.update { it.copy(isPlaying = false) } },
            )
        }
    }

    private fun startExercises() {
        _state.update { it.copy(stage = LessonStage.EXERCISES) }
        // Listening IS the exercise — play it the moment the exercises begin.
        if (_state.value.currentExercise is Exercise.Listening) playAudio()
    }

    private fun submit() {
        val s = _state.value
        if (s.outcome != null || submitting) return
        val exercise = s.currentExercise ?: return
        val answer = when (exercise) {
            is Exercise.MultipleChoice -> exercise.options.getOrNull(s.selectedOption ?: return) ?: return
            else -> s.textAnswer
        }
        if (answer.isBlank()) return
        submitting = true
        viewModelScope.launch {
            try {
                val outcome = submitExercise(exercise, answer, System.currentTimeMillis())
                // The answer is evidence about a *kind of knowing* too (audit §2):
                // folding it keeps the session's own weakness visible and lets the
                // selector steer the rest of the session with it (B-1).
                val updatedProfile = profileEngine.apply(
                    profile,
                    ExerciseDimension.dimensionOf(exercise),
                    outcome.correct,
                )
                profile = updatedProfile
                _state.update {
                    it.copy(
                        outcome = outcome,
                        weakestDimension = weakestDimensionOf(updatedProfile, it.exercises),
                        correctCount = if (outcome.correct) it.correctCount + 1 else it.correctCount,
                        // Misses are marked so the summary can point them out (P4).
                        weakAnswers = if (outcome.correct) {
                            it.weakAnswers
                        } else {
                            (it.weakAnswers + outcome.correctAnswer).distinct()
                        },
                        askedCount = it.askedCount + 1,
                        // Not yet re-queued (that happens in next()) — used for the
                        // final-button label so "پایان درس" never lies.
                        repeatsCurrent = !outcome.correct && exercise.id !in requeued,
                    )
                }
            } finally {
                submitting = false
            }
        }
    }

    private fun next() {
        val s = _state.value
        if (s.outcome == null) return
        // Wrong answer → ask the SAME exercise again later in this session (P4),
        // but only once so the session is guaranteed to end.
        val current = s.currentExercise
        if (s.answeredCorrectly == false && current != null && requeued.add(current.id)) {
            pending.addLast(s.currentIndex)
        }
        val nextIndex = pickNextIndex(s)
        if (nextIndex == null) {
            finishLesson()
            return
        }
        _state.update {
            it.copy(
                currentIndex = nextIndex,
                selectedOption = null,
                textAnswer = "",
                outcome = null,
                remainingCount = pending.size,
                repeatsCurrent = false,
                audioFailed = false,
            )
        }
        viewModelScope.launch {
            lessonRepository.saveLessonIndex(lessonId, nextIndex)
        }
        // Check the NEW exercise (the update above already advanced currentIndex).
        if (s.exercises.getOrNull(nextIndex) is Exercise.Listening) playAudio()
    }

    /**
     * Picks which queued exercise comes next (checklist B-1). The authored order
     * stands until the learner's own answers justify a change: the
     * [AdaptiveExerciseSelector] front-loads the dimension this session has shown
     * to be weakest, so after shaky recognition the fill-in-the-blanks (recall)
     * lead instead of more multiple choice. With no evidence yet the sort is
     * stable, so a fresh lesson runs exactly as authored — and a miss still comes
     * back later in the session, because a failed dimension ties with an untouched
     * one (0.0 vs 0f) and the tie is broken by queue position.
     */
    private fun pickNextIndex(s: LessonUiState): Int? {
        val candidates = pending.toList()
        if (candidates.size <= 1) return pending.removeFirstOrNull()
        val chosen = selector.order(candidates.map { s.exercises[it] }, profile).firstOrNull()
            ?: return pending.removeFirstOrNull()
        val index = candidates.first { s.exercises[it].id == chosen.id }
        pending.remove(index)
        return index
    }

    /**
     * The kind of knowing the session has shown to be weakest, or `null`. An
     * untouched dimension is unseen rather than weak, and a consolidated one is no
     * weakness at all (checklist B-1) — only an observed gap is named.
     */
    private fun weakestDimensionOf(profile: MasteryProfile, exercises: List<Exercise>): MasteryDimension? {
        if (profile.byDimension.isEmpty()) return null
        val weakest = profile.weakestOf(exercises.map { ExerciseDimension.dimensionOf(it) }.distinct())
            ?: return null
        val observed = profile.isEstablished(weakest) &&
            profile.masteryOf(weakest) < AdaptiveExerciseSelector.STRONG_THRESHOLD
        return if (observed) weakest else null
    }

    private fun finishLesson() {
        if (finished) return
        finished = true
        viewModelScope.launch {
            completeLesson(lessonId, System.currentTimeMillis())
            _state.update { it.copy(completed = true) }
        }
    }

    private fun playAudio() {
        val exercise = _state.value.currentExercise as? Exercise.Listening ?: return
        // No English voice → the card already shows the text fallback; do not
        // pretend to play (isPlaying would hang on "در حال پخش…").
        if (audioPlayer.isEnglishAvailable.value == false) return
        _state.update { it.copy(isPlaying = true, audioFailed = false) }
        speaker.speak(
            text = exercise.audioText,
            onDone = { _state.update { it.copy(isPlaying = false) } },
            onError = { _state.update { it.copy(isPlaying = false, audioFailed = true) } },
        )
    }

    private suspend fun creditStudyTime() {
        if (!isAppInForeground()) return
        progressRepository.addStudySeconds(studyTickMillis / 1000)
    }

    override fun onCleared() {
        audioPlayer.stop()
    }

    private companion object {
        const val STUDY_TICK_MILLIS = 30_000L
    }
}
