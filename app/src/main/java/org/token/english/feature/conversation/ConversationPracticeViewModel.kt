package org.token.english.feature.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.token.english.core.audio.Speaker
import org.token.english.domain.engine.ConversationScorer
import org.token.english.domain.engine.SpeakingVerdict
import org.token.english.domain.model.Exercise
import org.token.english.domain.repository.LessonRepository

/** One practice target: a Persian cue and the English sentence to say back. */
data class PracticeItem(
    val id: String,
    val promptFa: String,
    val target: String,
)

data class ConversationUiState(
    val isLoading: Boolean = true,
    val items: List<PracticeItem> = emptyList(),
    val index: Int = 0,
    val isListening: Boolean = false,
    /** Model being copied/loaded on the first take. */
    val isPreparing: Boolean = false,
    /** Live text while the learner speaks. */
    val partial: String = "",
    /** The completed take. */
    val finalText: String = "",
    /** Set once the take is graded; cleared when a new take starts. */
    val verdict: SpeakingVerdict? = null,
    val error: SpeechError = SpeechError.NONE,
) {
    val current: PracticeItem? get() = items.getOrNull(index)

    /** No speaking item exists in the bundle yet — an empty state, not an error. */
    val isEmpty: Boolean get() = !isLoading && items.isEmpty()

    val isFinished: Boolean get() = !isLoading && items.isNotEmpty() && index >= items.size

    val progressLabel: String get() =
        if (items.isEmpty()) "" else "${(index + 1).coerceAtMost(items.size)} / ${items.size}"
}

/**
 * Conversation practice: shows a target sentence, listens for the learner saying
 * it, and grades the take with [ConversationScorer].
 *
 * The screen owns the microphone *permission* (a Compose launcher is the only
 * sane place to ask); this class owns everything after that. The recogniser runs
 * in [SpeechRecognitionService], so its state arrives through [SpeechBus] — the
 * ViewModel never touches Vosk, AudioRecord or a Context, and a unit test can
 * drive a whole practice with a fake flow and a recording [Speaker].
 */
class ConversationPracticeViewModel(
    private val lessonRepository: LessonRepository,
    private val speaker: Speaker,
    /** Flipped once the JSON bundle has reached Room; targets read after it. */
    private val contentSeeded: StateFlow<Boolean>,
    speechState: StateFlow<SpeechState>,
    private val startRecognition: () -> Unit,
    private val stopRecognition: () -> Unit,
) : ViewModel() {

    private val _state = MutableStateFlow(ConversationUiState())
    val state: StateFlow<ConversationUiState> = _state.asStateFlow()

    /** Last take already graded, so a repeated utterance is not graded twice. */
    private var lastAssessed: String? = null

    init {
        loadTargets()
        viewModelScope.launch {
            speechState.collect { s ->
                _state.update {
                    it.copy(
                        isListening = s.isListening,
                        isPreparing = s.isPreparing,
                        partial = s.partial,
                        finalText = s.finalText,
                        // A blank take means the previous sentence is over: leaving
                        // its verdict up would show feedback for audio that is no
                        // longer the one on screen (the learner is mid-retry).
                        verdict = if (s.finalText.isBlank()) null else it.verdict,
                        error = s.error,
                    )
                }
                if (s.finalText.isBlank()) {
                    // A new take is open again: the same sentence may come next,
                    // and it must be graded, not skipped as a duplicate.
                    lastAssessed = null
                } else if (s.finalText != lastAssessed) {
                    lastAssessed = s.finalText
                    val target = _state.value.current?.target.orEmpty()
                    _state.update {
                        it.copy(verdict = ConversationScorer.assess(s.finalText, target))
                    }
                }
            }
        }
    }

    /** Every `speaking` item in the bundle, in lesson order. */
    private fun loadTargets() {
        viewModelScope.launch {
            // The seeder runs in the background at app start. Reading the table
            // before it lands returns an empty lesson list, which would show the
            // empty state for a bundle that has targets — and nothing re-emits,
            // because the read below takes a single snapshot.
            contentSeeded.first { it }
            // `first()` takes one emission and closes the flow — a bare `collect`
            // would keep the lesson observation alive for the screen's lifetime.
            val lessons = lessonRepository.observeLessons().first().sortedBy { it.order }

            val items = buildList {
                for (lesson in lessons) {
                    lessonRepository.getExercises(lesson.id)
                        .filterIsInstance<Exercise.Speaking>()
                        .sortedBy { it.id }
                        .forEach { ex ->
                            ex.referenceAnswers.firstOrNull()?.let { target ->
                                add(PracticeItem(id = ex.id, promptFa = ex.prompt, target = target))
                            }
                        }
                }
            }
            _state.update { it.copy(isLoading = false, items = items) }
        }
    }

    fun onEvent(event: ConversationEvent) {
        when (event) {
            ConversationEvent.StartTake -> startTake()
            ConversationEvent.StopTake -> stopRecognition()
            ConversationEvent.Retry -> {
                clearTake()
                startTake()
            }
            ConversationEvent.Next -> {
                clearTake()
                _state.update { it.copy(index = it.index + 1) }
            }
            ConversationEvent.SpeakModel -> {
                _state.value.current?.let { speaker.speak(it.target) }
            }
        }
    }

    private fun startTake() {
        if (_state.value.current == null) return
        // Deliberately NOT `clearTake()`: that stops the recogniser, and a stop
        // racing the start is what tore the foreground service down before it
        // could call startForeground() (process crash, Android 12). A new take
        // only needs the UI state reset; the previous take is already over.
        resetTake()
        startRecognition()
    }

    /** A take that is not on screen must not stay on screen as if it happened. */
    private fun clearTake() {
        stopRecognition()
        SpeechBus.reset()
        resetTake()
    }

    private fun resetTake() {
        lastAssessed = null
        _state.update { it.copy(partial = "", finalText = "", verdict = null, error = SpeechError.NONE) }
    }

    override fun onCleared() {
        // Leaving the feature ends the practice: never keep a mic service (or a
        // TTS request) alive behind a screen the learner already left.
        stopRecognition()
        SpeechBus.reset()
        super.onCleared()
    }
}

/** What the screen can ask for. Mirrors the Event convention of the other features. */
sealed interface ConversationEvent {
    data object StartTake : ConversationEvent
    data object StopTake : ConversationEvent
    data object Retry : ConversationEvent
    data object Next : ConversationEvent
    data object SpeakModel : ConversationEvent
}
