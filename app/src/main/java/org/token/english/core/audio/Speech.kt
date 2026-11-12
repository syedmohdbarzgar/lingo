package org.token.english.core.audio

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.token.english.domain.engine.ConversationScorer
import org.token.english.domain.engine.SpeakingVerdict

/** Why a take could not be produced. Every case has Persian copy on the screen. */
enum class SpeechError {
    NONE,

    /** No mic hardware, or AudioRecord could not be created. */
    NO_MICROPHONE,

    /** RECORD_AUDIO was refused (or revoked while listening). */
    PERMISSION_DENIED,

    /** The bundled model is absent — assets were never fetched. */
    MODEL_MISSING,

    /** Copying the model out of assets failed (storage full, corrupt asset). */
    ASSET_COPY_FAILED,

    /** The recorder died mid-take (device state change, hardware grab). */
    INTERRUPTED,
}

/**
 * Live recognition state, published by `SpeechRecognitionService` and observed by
 * the ViewModel. A process-wide holder on purpose: the service outlives the
 * screen (that is the point of a foreground service), so binding a service would
 * only add a second channel for the same three values.
 *
 * [partial] updates while the learner speaks; [finalText] lands once per
 * utterance and is what the scorer reads. [isListening] is the single source of
 * truth for the mic button.
 *
 * It lives in `core/audio` because **two** screens speak through it: the
 * dialogue ([feature/conversation]) and the lesson's own `speaking` item
 * ([feature/lesson]). A shared seam belongs below both features; before that it
 * sat inside the conversation feature, which the lesson had no business importing.
 */
object SpeechBus {

    private val _state = MutableStateFlow(SpeechState())
    val state: StateFlow<SpeechState> = _state.asStateFlow()

    /**
     * The allowed phrases of the turn being practised, as the JSON array Vosk
     * wants — or null for an open vocabulary. Set by the ViewModel before a take
     * starts, read by the service when it builds its recogniser: the service
     * outlives the screen and must not reach into the lesson itself.
     */
    @Volatile
    var grammar: String? = null

    /**
     * Every reply the current turn accepts, as authored. Set by the ViewModel next
     * to [grammar] and read by the service for exactly one decision: a take whose
     * transcript already *is* an accepted answer can close soon after the last word
     * instead of waiting out the whole silence window (see
     * `SpeechRecognitionService.runCaptureLoop`). The service still does not grade —
     * [acceptedAnswer] asks [ConversationScorer], it does not form its own verdict.
     */
    @Volatile
    var acceptedAnswers: List<String> = emptyList()

    /**
     * The current turn is A1: the take waits longer before closing on silence
     * (`EndOfTakePolicy.HESITANT_SILENCE_MS`). A beginner pausing to find the next
     * word is not finished, and a four-second cut reads as a speed test.
     */
    @Volatile
    var hesitant: Boolean = false

    /**
     * True when [spoken] already reads back as one of the turn's accepted replies.
     * The `[unk]` marker and a repeated partner line are not answers, so they stay
     * [SpeakingVerdict.RETRY] and the take keeps its full silence window.
     */
    internal fun acceptedAnswer(spoken: String): Boolean =
        spoken.isNotBlank() &&
            acceptedAnswers.isNotEmpty() &&
            ConversationScorer.assessBest(spoken, acceptedAnswers) == SpeakingVerdict.CORRECT

    /**
     * Called from the service's capture thread, the main thread and coroutines.
     * `update` retries against the latest value; assigning `value` from several
     * threads dropped updates (a lost `isListening = false` left a take ungraded
     * and the mic looking live).
     */
    internal fun publish(update: (SpeechState) -> SpeechState) {
        _state.update(update)
    }

    /** Screen left the feature: never leave a mic button looking live. */
    internal fun reset() {
        _state.value = SpeechState()
        grammar = null
        acceptedAnswers = emptyList()
        hesitant = false
    }
}

data class SpeechState(
    val isListening: Boolean = false,

    /** Model still being copied/loaded — button shows a spinner, not a mic. */
    val isPreparing: Boolean = false,

    /** Text recognised so far, refreshed on every Vosk partial. */
    val partial: String = "",

    /**
     * The take's transcript: every segment Vosk finalised so far, joined, and
     * cleared when the next take starts. It accumulates across pauses on purpose —
     * Vosk ends an utterance at each silence and its result JSON then holds only
     * that segment, so publishing the JSON alone would erase everything said before
     * the pause (see `SpeechRecognitionService`).
     */
    val finalText: String = "",
    val error: SpeechError = SpeechError.NONE,
)
