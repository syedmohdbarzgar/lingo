package org.token.english.feature.conversation

import android.content.Context
import org.vosk.Model
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.token.english.core.common.runCatchingCancellable
import java.io.File
import java.io.IOException
import java.util.UUID

/**
 * Owns the on-device Vosk [Model] for conversation practice.
 *
 * The model (~40 MB) ships in `assets/vosk/` and is copied to internal storage on
 * first use, so recognition runs with no network at all — the offline rule that
 * docs/connectivity.md pins for the learning core. Two process-wide guarantees:
 *
 *  - the model is loaded **once** ([ensureModelLoaded] is a no-op after success),
 *    because loading it again would hold a second copy of ~300 MB of native
 *    memory for nothing;
 *  - the copy is **marked complete with a `uuid` file**, so an interrupted copy
 *    (app killed mid-install-of-model) is detected and redone instead of leaving
 *    a half-written graph that fails at recognition time.
 *
 * Everything here is blocking I/O and native calls — never call it on the main
 * thread; [ensureModelLoaded] is a suspend function that hops to [Dispatchers.IO].
 */
class VoskManager(private val context: Context) {

    /** Non-null once the model is ready — the only state callers need to branch on. */
    @Volatile
    private var model: Model? = null

    val isLoaded: Boolean get() = model != null

    /**
     * Copies the model out of assets if needed, then loads it. Idempotent and
     * thread-safe: two callers racing the first practice both wait for the same
     * load instead of loading twice.
     */
    suspend fun ensureModelLoaded(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatchingCancellable {
            model?.let { return@runCatchingCancellable }
            synchronized(this) {
                if (model != null) return@synchronized
                val dir = materializeModel()
                model = Model(dir.absolutePath)
            }
        }
    }

    /** The loaded model, or null when [ensureModelLoaded] has not succeeded. */
    fun modelOrNull(): Model? = model

    /**
     * Frees the native model. The caller (the ViewModel on screen exit) decides
     * when practice is over; Vosk's native memory is not reclaimed by GC promptly
     * enough to leave it to chance. `Model` is AutoCloseable — close(), not
     * delete() — and Vosk itself guards against a double close.
     */
    fun release() {
        synchronized(this) {
            model?.close()
            model = null
        }
    }

    /**
     * Copies `assets/vosk/<MODEL_ASSET>` to `filesDir/vosk/<MODEL_ASSET>` unless a
     * previous copy already completed (signalled by the `uuid` marker file, the
     * same convention Vosk's own Android demo uses).
     */
    private fun materializeModel(): File {
        val dest = File(context.filesDir, "$ASSET_ROOT/$MODEL_ASSET")
        val marker = File(dest, UUID_MARKER)
        if (marker.exists()) return dest

        val assetDir = "$ASSET_ROOT/$MODEL_ASSET"
        val entries = context.assets.list(assetDir)
            ?: throw IOException("model assets are missing — run scripts/fetch_vosk_model.sh")

        dest.mkdirs()
        for (name in entries) {
            val from = "$assetDir/$name"
            val to = File(dest, name)
            // `list()` returns null for a plain file, so test the array directly.
            val children = context.assets.list(from)
            if (children != null && children.isNotEmpty()) {
                copyAssetDir(from, to) // model sub-directories (am/, graph/, …)
            } else {
                context.assets.open(from).use { input ->
                    to.outputStream().use { output -> input.copyTo(output) }
                }
            }
        }
        // Written LAST: its presence certifies the whole tree landed intact.
        marker.writeText(UUID.randomUUID().toString())
        return dest
    }

    private fun copyAssetDir(from: String, to: File) {
        to.mkdirs()
        for (name in context.assets.list(from).orEmpty()) {
            val src = "$from/$name"
            val dst = File(to, name)
            val children = context.assets.list(src)
            if (children != null && children.isNotEmpty()) {
                copyAssetDir(src, dst)
            } else {
                context.assets.open(src).use { input ->
                    dst.outputStream().use { output -> input.copyTo(output) }
                }
            }
        }
    }

    companion object {
        /**
         * The bundled model directory. English, not Persian: every practice target
         * in this app is an English sentence, and a Persian model cannot transcribe
         * English — the comparison would fail for every learner. To support Persian
         * targets later, ship that model beside this one and choose per target.
         */
        const val MODEL_ASSET = "vosk-model-small-en-us-0.15"

        /** `assets/vosk/…` in the APK, `filesDir/vosk/…` after extraction. */
        const val ASSET_ROOT = "vosk"

        /** Presence of this file inside the extracted model dir = copy completed. */
        const val UUID_MARKER = "uuid"

        /** 16 kHz is the sample rate every Vosk model is trained on. */
        const val SAMPLE_RATE = 16_000f
    }
}
