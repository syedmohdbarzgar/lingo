package org.token.english.feature.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.token.english.di.AppContainer
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.VocabularyItem
import org.token.english.domain.model.shuffledForDisplay
import org.token.english.domain.usecase.ExerciseOutcome
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
) {
    val currentExercise: Exercise? get() = exercises.getOrNull(currentIndex)
    val answeredCorrectly: Boolean? get() = outcome?.correct
    val totalExercises: Int get() = exercises.size

    /** Attempts total: done + pending + current — grows when a miss is re-queued. */
    val visibleTotal: Int get() = askedCount + remainingCount + 1

    val progress: Float
        get() = if (visibleTotal <= 0) 0f else askedCount.toFloat() / visibleTotal
}

sealed interface LessonEvent {
    data class SelectOption(val index: Int) : LessonEvent
    data class TextAnswerChanged(val value: String) : LessonEvent
    data object Submit : LessonEvent
    data object Next : LessonEvent
    data object ReplayAudio : LessonEvent
    data object StartExercises : LessonEvent
    data class SpeakText(val text: String) : LessonEvent
}

class LessonViewModel(
    private val container: AppContainer,
    private val lessonId: String,
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

    init {
        viewModelScope.launch {
            // Study time is credited in fixed ticks while the app is visible —
            // time spent backgrounded never counts toward the daily goal (P7).
            while (true) {
                kotlinx.coroutines.delay(STUDY_TICK_MILLIS)
                creditStudyTime()
            }
        }
        viewModelScope.launch {
            // B-3: watch the TTS engine — when no English voice is available the
            // listening card switches to its text fallback instead of hanging.
            container.audioPlayer.isEnglishAvailable.collect { available ->
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
            container.contentSeeded.first { it }
            val lesson = container.lessonRepository.getLesson(lessonId)
            // Display-time shuffles: option order and word-bank order derive from
            // the exercise id — stable per question, never gameable (P1/P4).
            val exercises = container.lessonRepository.getExercises(lessonId).map { exercise ->
                when {
                    exercise is Exercise.MultipleChoice -> exercise.shuffledForDisplay()
                    exercise is Exercise.Translation && exercise.bank.isNotEmpty() ->
                        exercise.copy(bank = exercise.bank.shuffled(Random(exercise.id.hashCode())))

                    else -> exercise
                }
            }
            if (lesson == null || exercises.isEmpty()) {
                _state.update {
                    it.copy(isLoading = false, error = "درس در دسترس نیست. محتوای ذخیره‌شده را بررسی کنید.")
                }
                return@launch
            }
            val vocabulary = container.lessonRepository.getVocabulary(lessonId)
            val saved = container.lessonRepository.getLessonState(lessonId)
            val startIndex = if (saved?.completed == true) 0 else (saved?.currentIndex ?: 0)
            val index = startIndex.coerceIn(0, exercises.lastIndex.coerceAtLeast(0))
            pending.addAll(index until exercises.size)
            val firstIndex = pending.removeFirstOrNull() ?: index
            // A resumed lesson (cursor > 0) has already shown the intro.
            val stage = if (index == 0 && vocabulary.isNotEmpty()) LessonStage.INTRO else LessonStage.EXERCISES
            _state.update {
                it.copy(
                    isLoading = false,
                    lesson = lesson,
                    exercises = exercises,
                    vocabulary = vocabulary,
                    stage = stage,
                    currentIndex = firstIndex,
                    remainingCount = pending.size,
                )
            }
            if (stage == LessonStage.EXERCISES && exercises[firstIndex] is Exercise.Listening) playAudio()
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
            is LessonEvent.SpeakText -> container.speak(
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
                val outcome = container.submitExercise(exercise, answer, System.currentTimeMillis())
                _state.update {
                    it.copy(
                        outcome = outcome,
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
        val nextIndex = pending.removeFirstOrNull()
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
            container.lessonRepository.saveLessonIndex(lessonId, nextIndex)
        }
        // Check the NEW exercise (the update above already advanced currentIndex).
        if (s.exercises.getOrNull(nextIndex) is Exercise.Listening) playAudio()
    }

    private fun finishLesson() {
        if (finished) return
        finished = true
        viewModelScope.launch {
            container.completeLesson(lessonId, System.currentTimeMillis())
            _state.update { it.copy(completed = true) }
        }
    }

    private fun playAudio() {
        val exercise = _state.value.currentExercise as? Exercise.Listening ?: return
        // No English voice → the card already shows the text fallback; do not
        // pretend to play (isPlaying would hang on "در حال پخش…").
        if (container.audioPlayer.isEnglishAvailable.value == false) return
        _state.update { it.copy(isPlaying = true, audioFailed = false) }
        container.speak(
            text = exercise.audioText,
            onDone = { _state.update { it.copy(isPlaying = false) } },
            onError = { _state.update { it.copy(isPlaying = false, audioFailed = true) } },
        )
    }

    private suspend fun creditStudyTime() {
        if (!container.isAppInForeground) return
        container.progressRepository.addStudySeconds(STUDY_TICK_MILLIS / 1000)
    }

    override fun onCleared() {
        container.audioPlayer.stop()
    }

    private companion object {
        const val STUDY_TICK_MILLIS = 30_000L
    }
}
