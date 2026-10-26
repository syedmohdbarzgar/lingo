package org.token.english.core.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

/**
 * The learner's own voice, kept entirely on the device (speaking practice).
 *
 * The app never uploads audio and never recognises speech: the exercise is
 * "hear the model, say it, hear yourself, compare". This seam is what the
 * screen and the ViewModel talk to; recording and playback are Android
 * details behind it, so the feature stays testable and the UI stays dumb.
 *
 * Failure contract (same spirit as [AudioPlayer], checklist B-3): every call
 * either reaches the state it promises or reports [error] — no silent no-op
 * that leaves a "recording…" label on screen forever.
 */
interface VoiceNote {
    /** A recording is in progress — the mic is open. */
    val isRecording: StateFlow<Boolean>

    /** A take exists on disk, so it can be played and compared. */
    val hasTake: StateFlow<Boolean>

    /** The learner's take is playing right now. */
    val isPlaying: StateFlow<Boolean>

    /** The last action failed (no microphone permission, recorder unavailable). */
    val error: StateFlow<Boolean>

    /** Opens the mic. Returns false when recording could not start. */
    fun startRecording(): Boolean

    /** Closes the mic and keeps the take. */
    fun stopRecording()

    /** Plays the last take back. */
    fun playTake()

    /** Stops take playback (leaving the screen, starting a new recording). */
    fun stopPlayback()

    /** Mic permission is granted — the card offers recording instead of only listening. */
    fun hasMicrophonePermission(): Boolean

    fun release()
}

class AndroidVoiceNote(private val context: Context) : VoiceNote {

    private val _isRecording = MutableStateFlow(false)
    override val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _hasTake = MutableStateFlow(false)
    override val hasTake: StateFlow<Boolean> = _hasTake.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _error = MutableStateFlow(false)
    override val error: StateFlow<Boolean> = _error.asStateFlow()

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    /** Cache-dir file: a take is practice, not user data — it never leaves the app. */
    private val takeFile: File get() = File(context.cacheDir, TAKE_FILE)

    override fun hasMicrophonePermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED

    override fun startRecording(): Boolean {
        if (_isRecording.value) return true
        if (!hasMicrophonePermission()) {
            _error.value = true
            return false
        }
        stopPlayback()
        // A new take replaces the old one: comparing against a stale recording
        // would be comparing against the wrong sentence.
        takeFile.delete()
        _hasTake.value = false
        return try {
            @Suppress("DEPRECATION") // MediaRecorder(Context) needs API 31; minSdk is 24.
            val created = MediaRecorder()
            created.setAudioSource(MediaRecorder.AudioSource.MIC)
            created.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            created.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            created.setAudioEncodingBitRate(64_000)
            created.setAudioSamplingRate(44_100)
            created.setOutputFile(takeFile.absolutePath)
            created.prepare()
            created.start()
            recorder = created
            _error.value = false
            _isRecording.value = true
            true
        } catch (t: Throwable) {
            // A recorder that fails to start must not leave the mic open or the
            // card stuck on "recording".
            releaseRecorder()
            _isRecording.value = false
            _error.value = true
            false
        }
    }

    override fun stopRecording() {
        if (!_isRecording.value) return
        try {
            recorder?.stop()
        } catch (t: Throwable) {
            // stop() throws when the take is too short to contain audio: the
            // take is discarded rather than played back as an empty file.
            takeFile.delete()
        } finally {
            releaseRecorder()
        }
        _isRecording.value = false
        _hasTake.value = takeFile.exists() && takeFile.length() > 0
    }

    override fun playTake() {
        if (!_hasTake.value || _isRecording.value) return
        stopPlayback()
        try {
            player = MediaPlayer().apply {
                setDataSource(takeFile.absolutePath)
                setOnCompletionListener {
                    _isPlaying.value = false
                    releasePlayer()
                }
                prepare()
                start()
            }
            _isPlaying.value = true
            _error.value = false
        } catch (t: Throwable) {
            releasePlayer()
            _isPlaying.value = false
            _error.value = true
        }
    }

    override fun stopPlayback() {
        if (!_isPlaying.value) return
        releasePlayer()
        _isPlaying.value = false
    }

    override fun release() {
        if (_isRecording.value) stopRecording()
        stopPlayback()
    }

    private fun releaseRecorder() {
        try {
            recorder?.reset()
            recorder?.release()
        } catch (t: Throwable) {
            // Already released / never prepared — nothing left to clean up.
        }
        recorder = null
    }

    private fun releasePlayer() {
        try {
            player?.stop()
            player?.release()
        } catch (t: Throwable) {
            // Already stopped — release is best effort here too.
        }
        player = null
    }

    private companion object {
        const val TAKE_FILE = "voice_take.m4a"
    }
}
