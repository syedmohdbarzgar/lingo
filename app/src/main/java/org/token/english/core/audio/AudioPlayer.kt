package org.token.english.core.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

/**
 * Audio abstraction (technical spec §35): UI never talks to a player implementation.
 *
 * The offline MVP speaks English through the on-device Android TTS engine, so
 * listening exercises work with no network and no bundled audio files.
 * A file-based player (bundled mp3s) or cloud TTS can replace this later
 * without touching any screen.
 */
interface AudioPlayer {
    val isSpeaking: Boolean
    fun speak(text: String, onDone: (() -> Unit)? = null)
    fun stop()
    fun release()
}

class TtsAudioPlayer(context: Context) : AudioPlayer {

    private var tts: TextToSpeech? = null
    private var ready = false
    private var pending: String? = null
    private var listener: (() -> Unit)? = null

    override var isSpeaking: Boolean = false
        private set

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setSpeechRate(0.95f)
                ready = true
                pending?.let { speakNow(it) }
                pending = null
            }
        }
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                isSpeaking = true
            }

            override fun onDone(utteranceId: String?) {
                isSpeaking = false
                listener?.invoke()
                listener = null
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                isSpeaking = false
                listener = null
            }
        })
    }

    override fun speak(text: String, onDone: (() -> Unit)?) {
        listener = onDone
        if (ready) {
            speakNow(text)
        } else {
            pending = text
        }
    }

    private fun speakNow(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utterance-${System.currentTimeMillis()}")
    }

    override fun stop() {
        listener = null
        pending = null
        isSpeaking = false
        tts?.stop()
    }

    override fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        ready = false
    }
}
