package org.token.english.core.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * Audio abstraction (technical spec §35): UI never talks to a player implementation.
 *
 * The offline MVP speaks English through the on-device Android TTS engine, so
 * listening exercises work with no network and no bundled audio files.
 * A file-based player (bundled mp3s) or cloud TTS can replace this later
 * without touching any screen.
 *
 * Availability and failure contract (checklist B-3):
 *  - [isEnglishAvailable] is `null` while the engine connects, then flips to
 *    `true` (ready) or `false` (no engine / no English voice). When `false` the
 *    UI must fall back to text instead of pretending to play.
 *  - Every [speak] call finishes with [onDone] exactly once — success, failure
 *    or unavailable — so screen state (isPlaying) can never get stuck.
 *    [onError] fires in addition to [onDone] when no sound was produced.
 */
interface AudioPlayer {
    val isSpeaking: Boolean

    /** null = engine still connecting, true = English voice ready, false = unavailable. */
    val isEnglishAvailable: StateFlow<Boolean?>

    fun speak(text: String, onDone: (() -> Unit)? = null, onError: (() -> Unit)? = null)
    fun stop()
    fun release()
}

class TtsAudioPlayer(context: Context) : AudioPlayer {

    private var tts: TextToSpeech? = null
    private var ready = false
    private var pending: String? = null
    private var listener: (() -> Unit)? = null
    private var errorListener: (() -> Unit)? = null

    private val _isEnglishAvailable = MutableStateFlow<Boolean?>(null)
    override val isEnglishAvailable: StateFlow<Boolean?> = _isEnglishAvailable.asStateFlow()

    override var isSpeaking: Boolean = false
        private set

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            val engine = tts
            if (status == TextToSpeech.SUCCESS && engine != null) {
                engine.setSpeechRate(0.95f)
                // B-3: the locale result was ignored before — a device without an
                // English voice silently "played" nothing and the UI hung on
                // "در حال پخش…". Check it and report availability instead.
                val langResult = engine.setLanguage(Locale.US)
                if (langResult == TextToSpeech.LANG_MISSING_DATA ||
                    langResult == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    markUnavailable()
                } else {
                    _isEnglishAvailable.value = true
                    ready = true
                    pending?.let { speakNow(it) }
                    pending = null
                }
            } else {
                markUnavailable()
            }
        }
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                isSpeaking = true
            }

            override fun onDone(utteranceId: String?) {
                finishRequest(failed = false)
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                finishRequest(failed = true)
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                finishRequest(failed = true)
            }
        })
    }

    override fun speak(text: String, onDone: (() -> Unit)?, onError: (() -> Unit)?) {
        if (_isEnglishAvailable.value == false) {
            // No English voice: end the request immediately so callers unstick,
            // and let the UI know no sound was produced.
            onDone?.invoke()
            onError?.invoke()
            return
        }
        listener = onDone
        errorListener = onError
        if (ready) {
            speakNow(text)
        } else {
            pending = text
        }
    }

    private fun speakNow(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utterance-${System.currentTimeMillis()}")
            ?: finishRequest(failed = true)
    }

    /** Engine died between init and speak — treat as a failed request. */
    private fun markUnavailable() {
        _isEnglishAvailable.value = false
        ready = false
        pending = null
        finishRequest(failed = true)
    }

    /** Ends the current request: onDone always, onError additionally on failure. */
    private fun finishRequest(failed: Boolean) {
        isSpeaking = false
        val done = listener
        val error = errorListener
        listener = null
        errorListener = null
        done?.invoke()
        if (failed) error?.invoke()
    }

    override fun stop() {
        listener = null
        errorListener = null
        pending = null
        isSpeaking = false
        tts?.stop()
    }

    override fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        ready = false
        pending = null
        listener = null
        errorListener = null
        _isEnglishAvailable.value = false
    }
}
