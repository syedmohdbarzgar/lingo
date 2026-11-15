package com.example.phonontts

import android.content.Context
import com.google.mediapipe.tasks.audio.texttospeech.AudioResult
import com.google.mediapipe.tasks.audio.texttospeech.SynthesisOptions
import com.google.mediapipe.tasks.audio.texttospeech.TextToSpeech
import com.google.mediapipe.tasks.audio.texttospeech.TextToSpeechOptions
import com.google.mediapipe.tasks.audio.texttospeech.VoiceInfo
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.Closeable
import java.io.File
import java.io.IOException

/**
 * Phonon 31M on-device speech synthesis through the MediaPipe Text-to-Speech task.
 *
 * API note (verified against `com.google.mediapipe:tasks-audio:1.2.3`, not guessed):
 * the task exposes `TextToSpeech.create(TextToSpeechOptions)`, `listVoices()` (not
 * `availableVoices`), a blocking `synthesize(text, SynthesisOptions)` and a suspending
 * `synthesizeAsync(text, SynthesisOptions)`; `SynthesisOptions` is a Kotlin class with
 * named arguments, not a builder. Results are `AudioResult(samples, sampleRateHz, …)`
 * with mono PCM floats in [-1, 1] at 24 kHz.
 *
 * Phonon is CPU-only (the delegate is set explicitly for documentation, the model
 * ignores it), 87 MB, English (`en-US`), and ships its voices inside the bundle:
 * `freya` is the default, plus the extra conditioning voices the bundle lists.
 */
class PhononTtsManager(context: Context) : Closeable {

    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null

    /** The model is created lazily-but-eagerly on first use; `null` means not loaded. */
    val isInitialized: Boolean get() = tts != null

    /**
     * Loads the bundle. Throws [ModelMissingException] when the 87 MB asset is absent —
     * a state the screen must name, because nothing else in the app can work without it.
     *
     * The task needs a **filesystem path**, not an asset path: passing
     * `setModelAssetPath("phonon_int8.litertlm")` fails on a real device with
     * `"phonon_int8.litertlm/tokenizer.model": No such file or directory`, because the
     * native side opens sibling files next to the bundle. So the asset is copied once to
     * `filesDir` and the copy's absolute path is what the task is given (verified on a
     * Poco X3, arm64; see README — this is the plan's "confirm the real API" rule).
     */
    suspend fun initialize() = withContext(Dispatchers.IO) {
        if (tts != null) return@withContext
        if (!hasModel()) throw ModelMissingException()
        val modelFile = materializeModel()

        val baseOptions = BaseOptions.builder()
            .setModelAssetPath(modelFile.absolutePath)
            .setDelegate(Delegate.CPU)
            .build()

        // A writable cache keeps unpacked weight caches across launches; without it every
        // cold start pays the unpack again.
        // `cacheDir`, not the deprecated `cacheDirectory` alias (the 1.2.3 API keeps
        // both; the string one is the current name).
        val cacheRoot = File(appContext.cacheDir, "phonon-tts").apply { mkdirs() }

        val options = TextToSpeechOptions(
            baseOptions = baseOptions,
            defaultVoiceId = DEFAULT_VOICE_ID,
            speakingRate = 1.0f,
            cacheDir = cacheRoot.absolutePath,
        )
        tts = TextToSpeech.create(options)
    }

    /**
     * The bundle as a real file: copied out of assets on first use and reused afterwards.
     *
     * A size check rather than a version flag, because the asset and the copy are the same
     * bytes: an install that upgraded the APK sees a different length and re-copies. The
     * `.tmp` name makes the copy atomic — a process killed mid-copy leaves the old file (or
     * none), never a truncated model that would fail every launch.
     */
    private fun materializeModel(): File {
        val dir = File(appContext.filesDir, "models").apply { mkdirs() }
        val target = File(dir, MODEL_ASSET)
        val assetSize = assetSize()
        if (target.isFile && target.length() == assetSize && assetSize > 0) return target

        val temp = File(dir, "$MODEL_ASSET.tmp")
        try {
            appContext.assets.open(MODEL_ASSET).use { input ->
                temp.outputStream().use { output -> input.copyTo(output, DEFAULT_BUFFER_SIZE) }
            }
            if (temp.length() != assetSize) {
                throw IOException("copied ${temp.length()} of $assetSize bytes of $MODEL_ASSET")
            }
            if (!temp.renameTo(target)) {
                temp.copyTo(target, overwrite = true)
                temp.delete()
            }
        } finally {
            if (temp.exists()) temp.delete()
        }
        return target
    }

    /** Bytes in the packaged asset, or 0 when it is absent. */
    private fun assetSize(): Long =
        runCatching {
            appContext.assets.openFd(MODEL_ASSET).use { it.length }
        }.getOrElse {
            // An asset opened through a stream (not memory-mapped) has no fd length.
            runCatching {
                appContext.assets.open(MODEL_ASSET).use { input ->
                    var total = 0L
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    while (true) {
                        val read = input.read(buffer)
                        if (read < 0) break
                        total += read
                    }
                    total
                }
            }.getOrDefault(0L)
        }

    /** Voices packaged in the loaded bundle; empty before [initialize]. */
    fun availableVoices(): List<VoiceInfo> = tts?.listVoices().orEmpty()

    /**
     * Synthesizes [text] with [voiceId] (null keeps the bundle's default voice).
     *
     * The blocking call runs on [Dispatchers.Default]: synthesis is a few hundred
     * milliseconds of CPU work and must never touch the main thread. A cancelled caller
     * cancels the engine through [cancel], because an abandoned sentence only wastes
     * battery if it keeps synthesizing.
     */
    suspend fun synthesize(text: String, voiceId: String? = null): AudioResult? =
        withContext(Dispatchers.Default) {
            val engine = tts ?: return@withContext null
            if (voiceId.isNullOrBlank()) {
                engine.synthesize(text)
            } else {
                engine.synthesize(text, SynthesisOptions(voiceId = voiceId))
            }
        }

    /** Stops an in-flight synthesis (barge-in). Safe from any thread. */
    fun cancel() {
        tts?.cancel()
    }

    override fun close() {
        tts?.close()
        tts = null
    }

    private fun hasModel(): Boolean =
        assetSize() > 0L

    class ModelMissingException : IllegalStateException("$MODEL_ASSET is not in assets")

    companion object {
        /** Keep the original filename: the task detects the model type from it. */
        const val MODEL_ASSET = "phonon_int8.litertlm"

        /** Phonon's bundled default voice (en-US). */
        const val DEFAULT_VOICE_ID = "freya"
    }
}
