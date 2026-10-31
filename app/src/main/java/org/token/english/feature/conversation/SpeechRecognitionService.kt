package org.token.english.feature.conversation

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.token.english.R
import org.vosk.Recognizer
import java.util.concurrent.atomic.AtomicInteger

/**
 * Owns the microphone and the on-device recogniser while conversation practice is
 * running, so a screen-off take is not cut short (the learner says a whole
 * sentence with the phone against their ear).
 *
 * Design notes:
 *  - **One capture thread.** AudioRecord + Vosk are blocking native calls; a
 *    dedicated thread with a plain loop beats a coroutine per read: no dispatch
 *    overhead in the hot path, and the thread is interruptible in one place.
 *  - **`microphone` foreground type is passed to [ServiceCompat.startForeground]**
 *    *and* declared in the manifest — API 34+ needs both, and drops the type
 *    silently if only one is present.
 *  - The service never scores. It publishes text; the domain engine decides what
 *    it means. Keeping grading out of a Service is what makes it unit-testable.
 */
class SpeechRecognitionService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile
    private var captureThread: Thread? = null

    @Volatile
    private var keepRecording = false

    /**
     * Identifies the take the service is allowed to open the mic for; 0 means none.
     *
     * Both a START and a STOP can be in flight at once — the screen calls
     * [start] again for «تلاش دوباره» while the first take is still copying the
     * model — and the plain `keepRecording` boolean could not tell them apart: a
     * STOP during the load set it false, then the older START set it true again
     * and the mic opened after the service had already been told to stop. Each
     * START takes a number and a STOP invalidates it, so a take that was abandoned
     * while loading can never capture, and never takes ownership from a newer one.
     */
    private val takeSequence = AtomicInteger(0)

    @Volatile
    private var activeTake = 0

    private var audioRecord: AudioRecord? = null
    private var recognizer: Recognizer? = null

    private val vosk: VoskManager get() = (application as org.token.english.EnglishApp).container.voskManager

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Foreground FIRST, whatever the action. Android requires
        // startForeground() shortly after startForegroundService(), and this
        // service's own start is not instantaneous — the first take also copies
        // ~40 MB of model out of assets. Two crashes proved it on a real device
        // (Android 12): loading before entering the foreground, and a STOP that
        // overtook the START and tore the service down while the system was still
        // waiting. Satisfying the contract here covers every action, including the
        // STOP that arrives after a START the user already abandoned.
        val foreground = enterForeground()
        when (intent?.action) {
            ACTION_START -> if (foreground) startRecording() else return START_NOT_STICKY
            ACTION_STOP -> stopRecordingAndSelf()
            else -> stopRecordingAndSelf()
        }
        // Never restarted by the system after a kill: a practice take interrupted
        // by the OS is over; the screen shows "تلاش دوباره" and the learner retries.
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        stopRecording()
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun startRecording() {
        // Numbered before the early return so a START that arrives while a take is
        // already capturing still invalidates nothing: it is simply ignored, and
        // the take on the mic keeps its number.
        val take = takeSequence.incrementAndGet()
        if (captureThread != null) return
        activeTake = take

        serviceScope.launch {
            SpeechBus.publish { it.copy(isPreparing = true, error = SpeechError.NONE) }
            val loaded = vosk.ensureModelLoaded()
            if (loaded.isFailure) {
                val error = if (assetsMissing(loaded.exceptionOrNull())) {
                    SpeechError.MODEL_MISSING
                } else {
                    SpeechError.ASSET_COPY_FAILED
                }
                SpeechBus.publish { it.copy(isPreparing = false, error = error) }
                stopForegroundAndSelf()
                return@launch
            }
            val model = vosk.modelOrNull()
            if (model == null) {
                SpeechBus.publish { it.copy(isPreparing = false, error = SpeechError.MODEL_MISSING) }
                stopForegroundAndSelf()
                return@launch
            }
            // The model load is the long part of a first take, so it is the window
            // in which «پایان ضبط» / leaving the screen arrives most often. When that
            // happened, this take is dead: it must not open the microphone, and it
            // must not tear the service down for whatever START came after it.
            // Silent: either a STOP already published the reset, or a newer take
            // owns the screen state and must not have its spinner cleared here.
            if (!isCurrentTake(take)) return@launch
            // A Recognizer holds per-utterance state, so it is built per take and
            // released with it; the heavy Model stays cached in VoskManager.
            //
            // The grammar is the turn's own accepted answers: the small English
            // model is open-vocabulary, and on a Persian accent it turns "for" into
            // "four" and "pharmacy" into "farmers see". Constraining the search to
            // the sentences the turn can accept removes most of that, and `[unk]`
            // (added by ConversationScorer.speechGrammar) keeps a genuinely
            // different take from being forced into one of them.
            val grammar = SpeechBus.grammar
            val rec = try {
                if (grammar.isNullOrBlank()) {
                    Recognizer(model, VoskManager.SAMPLE_RATE)
                } else {
                    Recognizer(model, VoskManager.SAMPLE_RATE, grammar)
                }
            } catch (t: Throwable) {
                SpeechBus.publish { it.copy(isPreparing = false, error = SpeechError.INTERRUPTED) }
                stopForegroundAndSelf()
                return@launch
            }
            if (!isCurrentTake(take)) {
                closeQuietly(rec)
                return@launch
            }
            beginCapture(rec, take)
        }
    }

    /** Whether [take] is the one still allowed to run. */
    private fun isCurrentTake(take: Int): Boolean = take != 0 && take == activeTake

    /**
     * Opens the mic for [take]. Runs after the model load (a background
     * coroutine), not on the main thread: the foreground state was already
     * established by [onStartCommand], which is where Android requires it.
     */
    private fun beginCapture(rec: Recognizer, take: Int) {
        // Ask before touching AudioRecord: the permission can be revoked between
        // the screen's check and this call (and the OS would throw inside the
        // constructor). The learner gets the same "blocked" copy either way.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            rec.close()
            SpeechBus.publish { it.copy(isPreparing = false, error = SpeechError.PERMISSION_DENIED) }
            stopForegroundAndSelf()
            return
        }

        val minBuffer = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        )
        if (minBuffer == AudioRecord.ERROR || minBuffer == AudioRecord.ERROR_BAD_VALUE) {
            rec.close()
            SpeechBus.publish { it.copy(isPreparing = false, error = SpeechError.NO_MICROPHONE) }
            stopForegroundAndSelf()
            return
        }

        // VOICE_RECOGNITION, not MIC: that source turns on the platform's speech
        // processing (noise suppression, AGC) and keeps the capture mono/unprocessed
        // in the way recognisers expect. With plain MIC the same sentence came back
        // with words missing, which is half of why learners had to repeat themselves.
        // It is not implemented everywhere, so an uninitialised stream falls back to
        // MIC instead of failing the take.
        val record = openAudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, minBuffer)
            ?: openAudioRecord(MediaRecorder.AudioSource.MIC, minBuffer)
        if (record == null) {
            rec.close()
            SpeechBus.publish { it.copy(isPreparing = false, error = SpeechError.NO_MICROPHONE) }
            stopForegroundAndSelf()
            return
        }
        // A STOP or a newer START can land between the permission check above and
        // this point; without the re-check that take would capture with the service
        // already shutting down, and the microphone would look live forever.
        if (!isCurrentTake(take)) {
            record.release()
            closeQuietly(rec)
            return
        }

        audioRecord = record
        recognizer = rec
        keepRecording = true

        // Already foreground since onStartCommand (see enterForeground); refresh
        // the text now that the mic is genuinely live.
        updateNotification()

        SpeechBus.publish { it.copy(isListening = true, isPreparing = false, partial = "", finalText = "") }

        captureThread = Thread({
            runCaptureLoop(record, rec, take)
        }, "vosk-capture").also { it.start() }
    }

    /** An initialised [AudioRecord] for [source], or null — the caller tries the next source. */
    private fun openAudioRecord(source: Int, minBuffer: Int): AudioRecord? {
        // Re-checked here even though the take already checked: the permission can be
        // revoked between the screen and this call, and the constructor throws without
        // it. Keeping the check inside the method that opens the recorder is also what
        // lets a reader (and lint) see the guard next to the call it protects.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }
        val record = try {
            AudioRecord(
                source,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                // 2× min: a full buffer is a ~100 ms take, small enough for low
                // partial latency and large enough not to underrun.
                minBuffer * 2,
            )
        } catch (t: Throwable) {
            return null
        }
        if (record.state != AudioRecord.STATE_INITIALIZED) {
            record.release()
            return null
        }
        return record
    }

    /**
     * The capture loop: read PCM → feed Vosk → publish partials, and the take's
     * transcript when Vosk detects an utterance boundary (silence). Runs on its
     * own thread; every exit path funnels through [finishCapture].
     *
     * The take keeps its OWN transcript. Vosk finalises an utterance at every
     * pause and the result JSON then holds only that segment — the one before it
     * is gone. Publishing that JSON straight to the screen (the first version)
     * meant a learner who breathed mid-sentence watched the first half disappear
     * and had to start over, which on a long sentence makes a correct answer
     * almost impossible. So every segment is appended here and the screen always
     * sees the whole take.
     */
    private fun runCaptureLoop(record: AudioRecord, rec: Recognizer, take: Int) {
        val transcript = StringBuilder()
        val buffer = ShortArray(BUFFER_SAMPLES)
        // End-of-take detection. The learner had to tap «پایان ضبط» to finish a
        // sentence, which is exactly the moment a speaking screen should not
        // demand: a learner who pauses to find the next word either stopped too
        // early (half a sentence graded) or kept the take open and collected the
        // room's noise. After [SILENCE_END_MS] of quiet, with nothing still in
        // flight, the take ends on its own. The window is deliberately long: a
        // pause for thought is not the end of the sentence, and an auto-end that
        // fires mid-sentence brings back the «اگر مکث کنم جمله‌ام می‌شکند» report
        // this service already had to fix once (see [runCaptureLoop]).
        var heardSomething = false
        var lastSoundAt = 0L
        try {
            record.startRecording()
            while (keepRecording && isCurrentTake(take)) {
                val read = record.read(buffer, 0, buffer.size)
                if (read <= 0) {
                    // ERROR_INVALID_OPERATION / ERROR_DEAD_OBJECT: the recorder
                    // was yanked out from under us — surface it, don't spin. Not
                    // when the learner ended the take: `read` reports the same
                    // negative codes for a stream stopped on purpose.
                    if (keepRecording) SpeechBus.publish { it.copy(error = SpeechError.INTERRUPTED) }
                    break
                }
                val bytes = ByteArray(read * 2)
                for (i in 0 until read) {
                    bytes[i * 2] = (buffer[i].toInt() and 0xFF).toByte()
                    bytes[i * 2 + 1] = (buffer[i].toInt() shr 8).toByte()
                }
                // Vosk's Java API exposes these as getResult()/getPartialResult()/
                // getFinalResult(); Kotlin sees the getters as properties, so the
                // bare `result()` form does not resolve.
                // What Vosk has not settled yet. A take ends on silence only when
                // nothing is in flight — a phrase still being recognised is not a
                // pause.
                var inFlight = ""
                if (rec.acceptWaveForm(bytes, bytes.size)) {
                    val segment = parseText(rec.getResult())
                    appendSegment(transcript, segment)
                    if (segment.isNotBlank()) {
                        heardSomething = true
                        lastSoundAt = SystemClock.elapsedRealtime()
                    }
                    SpeechBus.publish { it.copy(finalText = transcript.toString(), partial = "") }
                } else {
                    val partial = parsePartial(rec.getPartialResult())
                    inFlight = partial
                    if (partial.isNotBlank()) {
                        heardSomething = true
                        lastSoundAt = SystemClock.elapsedRealtime()
                    }
                    SpeechBus.publish { it.copy(partial = partial) }
                }
                // Only a take that has already recognised something can end by
                // silence: a learner who has not spoken yet keeps an open mic
                // instead of losing the take before they start.
                if (heardSomething &&
                    inFlight.isBlank() &&
                    SystemClock.elapsedRealtime() - lastSoundAt >= SILENCE_END_MS
                ) {
                    keepRecording = false
                    break
                }
            }
            // Flush whatever was still in the buffer as a final utterance. This
            // runs on EVERY exit, not only on an unexpected one: the common path
            // is the learner tapping «پایان ضبط» mid-sentence, and that last
            // in-flight utterance used to be dropped because the loop had already
            // left on `keepRecording == false`.
            appendSegment(transcript, parseText(rec.getFinalResult()))
            SpeechBus.publish { it.copy(finalText = transcript.toString(), partial = "") }
        } catch (t: Throwable) {
            // A take the learner ended is not a failure; only a recorder that
            // died underneath a take still meant to be running is.
            if (keepRecording) SpeechBus.publish { it.copy(error = SpeechError.INTERRUPTED) }
        } finally {
            finishCapture(record, rec)
        }
    }

    /** A recogniser that never opened must not keep the native model alive. */
    private fun closeQuietly(rec: Recognizer) {
        try {
            rec.close()
        } catch (t: Throwable) {
            // Already closed.
        }
    }

    /** Joins the take's segments with single spaces, skipping empty ones. */
    private fun appendSegment(transcript: StringBuilder, segment: String) {
        if (segment.isBlank()) return
        if (transcript.isNotEmpty()) transcript.append(' ')
        transcript.append(segment)
    }

    /** Main-thread cleanup after the capture thread is done. */
    private fun finishCapture(record: AudioRecord, rec: Recognizer) {
        cleanup(record, rec)
        // The loop owns its thread, so it also clears the field: a stale, finished
        // thread left there made [startRecording] believe a take was still running
        // and refuse the next one.
        if (captureThread === Thread.currentThread()) captureThread = null
        try {
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        } catch (t: Throwable) {
            // Already stopped — nothing to detach.
        }
        stopSelf()
    }

    private fun stopRecordingAndSelf() {
        stopRecording()
        stopForegroundAndSelf()
    }

    /**
     * Posts the ongoing notification and enters the foreground state, which must
     * happen before the (possibly slow) model load. Returns false when the type
     * could not be granted (e.g. a device that refuses the microphone type).
     */
    private fun enterForeground(): Boolean = try {
        // The type is what grants mic access in the background on API 34+.
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            buildNotification(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
            } else {
                0
            },
        )
        true
    } catch (t: Throwable) {
        // Never silent: a refused foreground type is exactly the kind of failure
        // that otherwise reaches the user as "the mic does nothing".
        Log.e(TAG, "could not enter the foreground state", t)
        SpeechBus.publish { it.copy(isPreparing = false, error = SpeechError.INTERRUPTED) }
        stopSelf()
        false
    }

    private fun updateNotification() {
        try {
            getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, buildNotification())
        } catch (t: Throwable) {
            // The notification is a courtesy; the take does not depend on it.
        }
    }

    /** One exit path for every failure: drop the notification, then the service. */
    private fun stopForegroundAndSelf() {
        try {
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        } catch (t: Throwable) {
            // Never entered the foreground state — nothing to detach.
        }
        stopSelf()
    }

    private fun stopRecording() {
        // Invalidates a take still copying/loading the model, so it cannot open the
        // mic after this stop, and leaves no take current for the next START.
        activeTake = 0
        keepRecording = false
        // A take stopped while it was still preparing never reaches cleanup(), so
        // without this the screen keeps showing the loading spinner of a take the
        // learner already cancelled.
        SpeechBus.publish { it.copy(isPreparing = false) }
        // The loop releases its own record/recorder on exit; joining here would
        // block the main thread, so just ask and let finishCapture run.
        captureThread?.interrupt()
        captureThread = null
    }

    private fun cleanup(record: AudioRecord, rec: Recognizer) {
        try {
            record.stop()
        } catch (t: Throwable) {
            // Never started — fine.
        }
        record.release()
        try {
            rec.close()
        } catch (t: Throwable) {
            // Already closed.
        }
        audioRecord = null
        recognizer = null
        SpeechBus.publish { it.copy(isListening = false, isPreparing = false) }
    }

    /** Vosk result JSON: `{"text": "go straight and turn left"}`. */
    private fun parseText(json: String): String = try {
        JSONObject(json).optString("text").trim()
    } catch (t: Throwable) {
        ""
    }

    /** Vosk partial JSON: `{"partial": "go straight and"}`. */
    private fun parsePartial(json: String): String = try {
        JSONObject(json).optString("partial").trim()
    } catch (t: Throwable) {
        ""
    }

    private fun assetsMissing(t: Throwable?): Boolean =
        t is java.io.FileNotFoundException || t?.message?.contains("fetch_vosk_model") == true

    private fun buildNotification(): Notification {
        val manager = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notif_channel_speech),
                NotificationManager.IMPORTANCE_LOW, // silent: no sound while the learner speaks
            )
            manager.createNotificationChannel(channel)
        }
        val stopIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, SpeechRecognitionService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mic)
            .setContentTitle(getString(R.string.notif_speech_title))
            .setContentText(getString(R.string.notif_speech_text))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(0, getString(R.string.notif_speech_stop), stopIntent)
            .build()
    }

    companion object {
        const val ACTION_START = "org.token.english.action.START_SPEECH"
        const val ACTION_STOP = "org.token.english.action.STOP_SPEECH"
        private const val CHANNEL_ID = "speech_practice"
        private const val TAG = "SpeechRecognition"
        private const val NOTIFICATION_ID = 4711
        const val SAMPLE_RATE = 16_000
        private const val BUFFER_SAMPLES = 1600 // 100 ms at 16 kHz mono

        /**
         * Quiet time after the last recognised sound that ends a take by itself.
         * Comfortably longer than the three-second thinking pause the device
         * checklist says must survive: ending a take mid-sentence grades half of
         * what the learner meant to say.
         */
        private const val SILENCE_END_MS = 4_000L

        /**
         * Start (or restart) a take. Safe to call repeatedly — the service dedupes.
         * `ContextCompat` handles minSdk 24: `startForegroundService` only exists
         * from API 26, and below that a plain start is the correct behavior.
         */
        fun start(context: Context) {
            val intent = Intent(context, SpeechRecognitionService::class.java).setAction(ACTION_START)
            ContextCompat.startForegroundService(context, intent)
        }

        /** Stop the take and dismiss the notification. Safe when not running. */
        fun stop(context: Context) {
            val intent = Intent(context, SpeechRecognitionService::class.java).setAction(ACTION_STOP)
            context.startService(intent)
        }
    }
}
