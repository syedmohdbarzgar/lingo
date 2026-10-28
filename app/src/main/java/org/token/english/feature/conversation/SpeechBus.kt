package org.token.english.feature.conversation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
 * Live recognition state, published by [SpeechRecognitionService] and observed by
 * the ViewModel. A process-wide holder on purpose: the service outlives the
 * screen (that is the point of a foreground service), so binding a service would
 * only add a second channel for the same three values.
 *
 * [partial] updates while the learner speaks; [finalText] lands once per
 * utterance and is what the scorer reads. [isListening] is the single source of
 * truth for the mic button.
 */
object SpeechBus {

    private val _state = MutableStateFlow(SpeechState())
    val state: StateFlow<SpeechState> = _state.asStateFlow()

    /** Called from the service's capture thread — StateFlow is thread-safe. */
    internal fun publish(update: (SpeechState) -> SpeechState) {
        _state.value = update(_state.value)
    }

    /** Screen left the feature: never leave a mic button looking live. */
    internal fun reset() {
        _state.value = SpeechState()
    }
}

data class SpeechState(
    val isListening: Boolean = false,
    /** Model still being copied/loaded — button shows a spinner, not a mic. */
    val isPreparing: Boolean = false,
    /** Text recognised so far, refreshed on every Vosk partial. */
    val partial: String = "",
    /** The completed utterance — cleared when the next take starts. */
    val finalText: String = "",
    val error: SpeechError = SpeechError.NONE,
)
