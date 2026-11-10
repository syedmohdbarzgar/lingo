package org.token.english.di

import android.content.Context
import android.content.Intent
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import java.io.File
import org.token.english.core.audio.AudioPlayer
import org.token.english.core.audio.Speaker
import org.token.english.core.audio.TtsAudioPlayer
import org.token.english.core.common.ContentFailure
import org.token.english.core.common.ContentStatus
import org.token.english.core.common.runCatchingCancellable
import org.token.english.data.content.ContentSeeder
import org.token.english.data.local.AppDatabase
import org.token.english.data.repository.ConversationRepositoryImpl
import org.token.english.data.repository.KnowledgeRepositoryImpl
import org.token.english.data.repository.LessonRepositoryImpl
import org.token.english.data.repository.ProgressRepositoryImpl
import org.token.english.data.repository.ReviewRepositoryImpl
import org.token.english.data.repository.SettingsRepositoryImpl
import org.token.english.data.repository.VocabularyRepositoryImpl
import org.token.english.domain.engine.DefaultLearningPlanner
import org.token.english.domain.engine.DefaultMasteryEngine
import org.token.english.domain.engine.LearningPlanner
import org.token.english.domain.engine.MasteryEngine
import org.token.english.domain.engine.ReviewScheduler
import org.token.english.domain.engine.Sm2ReviewScheduler
import org.token.english.domain.repository.ConversationRepository
import org.token.english.domain.repository.KnowledgeRepository
import org.token.english.domain.repository.LessonRepository
import org.token.english.domain.repository.ProgressRepository
import org.token.english.domain.repository.ReviewRepository
import org.token.english.domain.repository.SettingsRepository
import org.token.english.domain.repository.VocabularyRepository
import org.token.english.domain.usecase.CompleteLessonUseCase
import org.token.english.domain.usecase.GetDueKnowledgeReviewsUseCase
import org.token.english.domain.usecase.GetFocusPlanUseCase
import org.token.english.domain.usecase.GetMissProfileUseCase
import org.token.english.domain.usecase.GetSkillProfilesUseCase
import org.token.english.domain.usecase.GetTodayPlanUseCase
import org.token.english.domain.usecase.AssessPlacementUseCase
import org.token.english.domain.usecase.ScorePlacementUseCase
import org.token.english.domain.usecase.RecordConversationTurnUseCase
import org.token.english.domain.usecase.SubmitExerciseUseCase
import org.token.english.domain.usecase.SubmitReviewUseCase

/**
 * Manual dependency container. Constructor injection at the class level,
 * container-level wiring here — Hilt/KSP-Hilt is deliberately avoided to keep the
 * MVP's build simple under AGP 9 built-in Kotlin (see AGENTS.md).
 */
class AppContainer(context: Context, appScope: CoroutineScope) : Speaker {

    private val appContext: Context = context.applicationContext

    // Engines — declared before any repository/eager property that reads them:
    // `studyStats` is eager and forces `progressRepository` during <init>, whose
    // initializer touches `masteryEngine`; a later-declared `by lazy` delegate is
    // still null at that point (NPE on Lazy.getValue). Keep this block first.
    val masteryEngine: MasteryEngine by lazy { DefaultMasteryEngine() }
    val reviewScheduler: ReviewScheduler by lazy { Sm2ReviewScheduler() }
    val learningPlanner: LearningPlanner by lazy { DefaultLearningPlanner() }

    // Data
    val database: AppDatabase by lazy {
        Room.databaseBuilder(appContext, AppDatabase::class.java, DATABASE_NAME)
            // Every migration must be registered here: Room throws at open time when
            // a path from the installed version is missing, which would crash an
            // upgrading install before the seeder ever runs. Taken as a whole from
            // the database itself rather than listed one by one — the list used to be
            // spelled out and v7→v8 was forgotten, which crashed every upgrading
            // install on the device. `RoomMigrationTest` pins this line.
            .addMigrations(*AppDatabase.ALL_MIGRATIONS.toTypedArray())
            .build()
    }
    val settingsRepository: SettingsRepository by lazy { SettingsRepositoryImpl(appContext) }
    val lessonRepository: LessonRepository by lazy {
        LessonRepositoryImpl(database.contentDao(), database.reviewDao())
    }
    val vocabularyRepository: VocabularyRepository by lazy {
        VocabularyRepositoryImpl(database.contentDao())
    }
    val reviewRepository: ReviewRepository by lazy { ReviewRepositoryImpl(database.reviewDao()) }
    val knowledgeRepository: KnowledgeRepository by lazy {
        KnowledgeRepositoryImpl(database.contentDao(), database.knowledgeDao())
    }
    /** Which lessons' dialogues are finished — the lesson gate reads it. */
    val conversationRepository: ConversationRepository by lazy {
        ConversationRepositoryImpl(database.conversationDao(), appContext)
    }
    val progressRepository: ProgressRepository by lazy {
        ProgressRepositoryImpl(
            contentDao = database.contentDao(),
            reviewDao = database.reviewDao(),
            progressDao = database.progressDao(),
            masteryEngine = masteryEngine,
            database = database,
        )
    }

    /**
     * Shared stats stream (checklist P7): one hot pipeline feeds Home and
     * Progress instead of each screen running its own aggregate query set.
     * While-subscribed keeps it idle when no screen is looking.
     */
    val studyStats: StateFlow<org.token.english.domain.model.StudyStats> =
        progressRepository.observeStats()
            .stateIn(
                scope = appScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = org.token.english.domain.model.StudyStats(0L, 0, 0, 0, 0, 0f),
            )

    /** True while any activity is visible — gates study-time crediting (P7). */
    val isAppInForeground: Boolean
        get() = (appContext as? org.token.english.EnglishApp)?.isAppInForeground ?: true

    // Audio (on-device TTS — no network needed)
    val audioPlayer: AudioPlayer by lazy { TtsAudioPlayer(appContext) }

    // Speaking practice (record + play back the learner's own take — local only)
    val voiceNote: org.token.english.core.audio.VoiceNote by lazy {
        org.token.english.core.audio.AndroidVoiceNote(appContext)
    }

    // Conversation practice: on-device Vosk model + recogniser. One instance for
    // the process (a second copy would hold a second native model), released when
    // the practice screen goes away. See feature/conversation/VoskManager.kt.
    val voskManager: org.token.english.feature.conversation.VoskManager by lazy {
        org.token.english.feature.conversation.VoskManager(appContext)
    }

    /** Live recognition state for the practice screen; published by the service. */
    val conversationSpeech: StateFlow<org.token.english.feature.conversation.SpeechState> =
        org.token.english.feature.conversation.SpeechBus.state

    /** Starts the mic foreground service — the container is where the Context lives. */
    fun startConversationRecognition() =
        org.token.english.feature.conversation.SpeechRecognitionService.start(appContext)

    /** Stops the take and dismisses its notification. Safe when nothing is running. */
    fun stopConversationRecognition() =
        org.token.english.feature.conversation.SpeechRecognitionService.stop(appContext)

    // Free-access companion app (org.token.zaribar): while it is installed *and
    // signed by the expected key* the subscription is free — see CompanionApp.
    // Re-checked on every foreground and whenever a paywall/home surface opens, so
    // the grant can never outlive the install that justifies it.
    val companionDetector: org.token.english.core.billing.CompanionDetector by lazy {
        org.token.english.core.billing.PackageManagerCompanionDetector(appContext)
    }
    private val _companionStatus =
        MutableStateFlow(org.token.english.core.billing.CompanionStatus.ABSENT)

    /** Latest companion lookup (N-3): absent, installed-but-untrusted, or trusted. */
    val companionStatus: StateFlow<org.token.english.core.billing.CompanionStatus> =
        _companionStatus.asStateFlow()

    /**
     * True once the companion is installed **and** trusted — the entitlement
     * check reads this, never the raw install (N-1/N-3: a Zaribar under a
     * foreign key earns nothing).
     */
    val companionInstalled: StateFlow<Boolean> = _companionStatus
        .map { it == org.token.english.core.billing.CompanionStatus.TRUSTED }
        .stateIn(
            scope = appScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    /** PackageManager lookup; never throws. Cheap enough to re-run on resume. */
    suspend fun refreshCompanionInstalled(): Boolean = withContext(Dispatchers.IO) {
        val status = runCatching {
            when {
                !companionDetector.isInstalled() ->
                    org.token.english.core.billing.CompanionStatus.ABSENT

                companionDetector.isTrusted() ->
                    org.token.english.core.billing.CompanionStatus.TRUSTED

                else -> org.token.english.core.billing.CompanionStatus.UNTRUSTED
            }
        }.getOrDefault(org.token.english.core.billing.CompanionStatus.ABSENT)
        _companionStatus.value = status
        status == org.token.english.core.billing.CompanionStatus.TRUSTED
    }

    // Store billing — implementation comes from the active product flavor
    // (src/bazaar, src/myket or src/googlePlay). See AGENTS.md §4.
    val billing: org.token.english.core.billing.BillingGateway by lazy {
        org.token.english.billing.PlatformBillingGateway(appContext, settingsRepository)
    }

    /** Asks the store about the subscription (payment is the only online surface). */
    suspend fun refreshEntitlements() {
        org.token.english.core.common.runCatchingCancellable {
            if (billing.initialize()) billing.checkSubscription()
        }.onFailure { e ->
            android.util.Log.w("AppContainer", "entitlement refresh failed", e)
        }
    }

    /** Mirrors the sound setting; kept hot by [org.token.english.EnglishApp]. */
    @Volatile
    var soundEnabled: Boolean = true

    /**
     * Speaks only when the user has sound enabled in settings.
     * [onDone] always fires when the request ends; [onError] additionally fires
     * when no sound was produced (engine unavailable or playback failed) — a
     * mute by user choice is not an error (checklist B-3).
     */
    override fun speak(text: String, onDone: (() -> Unit)?, onError: (() -> Unit)?) {
        if (soundEnabled) {
            audioPlayer.speak(text, onDone, onError)
        } else {
            onDone?.invoke()
        }
    }

    override fun stop() {
        audioPlayer.stop()
    }

    // Content
    private val _contentStatus = MutableStateFlow(ContentStatus.LOADING)

    /**
     * Where the bundle write stands: still running, written, or failed. Home renders
     * [ContentStatus.FAILED] as an error card with a retry, so a fresh install can
     * never open on an empty screen with no explanation.
     */
    val contentStatus: StateFlow<ContentStatus> = _contentStatus.asStateFlow()

    private val _contentFailure = MutableStateFlow<ContentFailure?>(null)

    /**
     * Why the last write failed, when it did — `null` while it succeeded or is running.
     *
     * The screens need the difference: a retry is the right answer to a bad asset, and
     * a lie when Room cannot open the database at all. Only the second one offers the
     * rebuild, and a rebuild is not something to offer on a guess.
     */
    val contentFailure: StateFlow<ContentFailure?> = _contentFailure.asStateFlow()

    /**
     * True once the write has finished — successful or not. Screens and the splash
     * wait on this so a fresh install never paints "lesson missing" mid-write; the
     * outcome itself is [contentStatus].
     */
    val contentSeeded: StateFlow<Boolean> = _contentStatus
        .map { it != ContentStatus.LOADING }
        .stateIn(appScope, SharingStarted.Eagerly, false)

    /**
     * Writes the curriculum bundle into Room and records the outcome. Never throws:
     * a failure is state ([ContentStatus.FAILED]) that the UI can show and retry,
     * not a crash in a background coroutine.
     */
    suspend fun seedContent(): Boolean {
        _contentStatus.value = ContentStatus.LOADING
        val result = runCatchingCancellable { contentSeeder.ensureSeeded() }
        result.exceptionOrNull()?.let { android.util.Log.e("AppContainer", "content seeding failed", it) }
        _contentFailure.value = if (result.isSuccess) null else classifyFailure()
        _contentStatus.value = if (result.isSuccess) ContentStatus.READY else ContentStatus.FAILED
        return result.isSuccess
    }

    /**
     * Asks the database itself, instead of reading the exception: an asset that failed to
     * parse and a file that is not a database both arrive here as a throwable, and only
     * one of them is worth a rebuild. Opening it is the question.
     */
    private suspend fun classifyFailure(): ContentFailure = withContext(Dispatchers.IO) {
        val openable = runCatching { database.openHelper.writableDatabase }.isSuccess
        if (openable) ContentFailure.OTHER else ContentFailure.DATABASE_UNREADABLE
    }

    /**
     * Moves an unreadable `english.db` out of the way so the next start can build a fresh
     * one — the only way out of [ContentFailure.DATABASE_UNREADABLE], where every retry
     * fails the same way.
     *
     * Nothing is deleted: the damaged file (with its `-wal` and `-shm` companions) is
     * renamed into `noBackupFilesDir/corrupt-database/`, a directory Auto Backup and
     * device transfer both ignore, so it neither rides along in a restore nor counts
     * against the backup quota — and support can still pull it with `run-as`.
     *
     * The repository graph holds DAOs from this database, so the caller **must** restart
     * the process afterwards; until then every read would still hit the closed handle.
     *
     * @return true when a file was moved (the caller restarts), false when there was
     *   nothing to move — in which case the screen says the rebuild did not happen
     *   rather than pretending it did.
     */
    suspend fun repairUnreadableDatabase(): Boolean = withContext(Dispatchers.IO) {
        val moved = runCatchingCancellable {
            // Closing a database whose open failed throws on some paths and not on
            // others; neither outcome is allowed to block the move.
            runCatching { database.close() }
            val stamp = System.currentTimeMillis()
            val aside = File(appContext.noBackupFilesDir, CORRUPT_DATABASE_DIR).apply { mkdirs() }
            val base = appContext.getDatabasePath(DATABASE_NAME).absolutePath
            var any = false
            listOf("", "-wal", "-shm").forEach { suffix ->
                val from = File(base + suffix)
                if (from.exists()) {
                    any = from.renameTo(File(aside, "$DATABASE_NAME.$stamp$suffix")) || any
                }
            }
            any
        }
        if (moved.isFailure) {
            android.util.Log.e("AppContainer", "database repair could not move the file", moved.exceptionOrNull())
        }
        moved.getOrDefault(false)
    }

    /**
     * The repair's second half: starts the app again from its launcher and exits this
     * process.
     *
     * Every repository in the graph holds DAOs bound to the database object that was
     * just closed and moved aside, so rebuilding the graph inside a live process would
     * mean re-creating the container underneath ViewModels that already captured it.
     * A relaunch is the honest equivalent, and it is also what the confirmation on
     * Home tells the learner will happen — so the promise and the behaviour match even
     * on an OEM that defers the start.
     *
     * @return false when there is nothing to relaunch (no launcher activity, e.g. a
     *   build without one). The file has already been moved by then, so the caller must
     *   not pretend either that nothing happened or that the app will come back by
     *   itself: the screen says the rebuild is done and asks the learner to reopen the
     *   app. Returning true means the relaunch was queued and this process is going.
     */
    fun restartProcess(): Boolean {
        val launch = appContext.packageManager.getLaunchIntentForPackage(appContext.packageName)
        if (launch == null) {
            android.util.Log.w("AppContainer", "no launcher intent after the database repair")
            return false
        }
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        appContext.startActivity(launch)
        // Queued with the system first, then this process goes: killing it before the
        // request would leave the learner staring at the launcher.
        android.os.Process.killProcess(android.os.Process.myPid())
        return true
    }

    val contentSeeder: ContentSeeder by lazy {
        ContentSeeder(
            dao = database.contentDao(),
            readAsset = { path -> appContext.assets.open(path).bufferedReader().use { it.readText() } },
            settingsRepository = settingsRepository,
        )
    }

    // Use cases
    val submitExercise: SubmitExerciseUseCase by lazy {
        SubmitExerciseUseCase(progressRepository, knowledgeRepository)
    }

    /**
     * Writes a graded conversation turn to its own track — SPEAKING when it was
     * spoken, WRITING when it was typed. One seam, so the screen cannot credit the
     * wrong skill.
     */
    val recordConversationTurn: RecordConversationTurnUseCase by lazy {
        RecordConversationTurnUseCase(
            progress = progressRepository,
            knowledge = knowledgeRepository,
            conversations = conversationRepository,
        )
    }
    val completeLesson: CompleteLessonUseCase by lazy {
        CompleteLessonUseCase(lessonRepository, progressRepository)
    }
    val submitReview: SubmitReviewUseCase by lazy {            SubmitReviewUseCase(
                reviewRepository,
                reviewScheduler,
                progressRepository,
                knowledgeRepository,
            )
    }
    val getTodayPlan: GetTodayPlanUseCase by lazy {
        GetTodayPlanUseCase(
            lessons = lessonRepository,
            reviews = reviewRepository,
            progress = progressRepository,
            settingsRepository = settingsRepository,
            planner = learningPlanner,
            knowledge = knowledgeRepository,
        )
    }
    /**
     * The remediation engine's weak-spot decision (checklist B-1): Home renders it
     * as a card and the focused lesson session obeys its exercise subset.
     */
    val getFocusPlan: GetFocusPlanUseCase by lazy {
        GetFocusPlanUseCase(knowledgeRepository, lessonRepository)
    }

    /**
     * The review screen's graph half (review→graph pass): which curriculum nodes are
     * due, via the focused drill that will serve them — so the row it shows and the
     * session it opens are the same decision.
     */
    val getDueKnowledgeReviews: GetDueKnowledgeReviewsUseCase by lazy {
        GetDueKnowledgeReviewsUseCase(knowledgeRepository, getFocusPlan)
    }

    /**
     * The three independent skill tracks (P2): mastery plus the evidence behind it,
     * combined from the learner model and the review log. Home and Progress both read
     * it so neither can define a track its own way.
     */
    val getSkillProfiles: GetSkillProfilesUseCase by lazy {
        GetSkillProfilesUseCase(knowledgeRepository, progressRepository, reviewRepository)
    }

    /**
     * The error taxonomy's read (roadmap): recent misses classified by the kind of
     * knowing that failed, so Progress can say what to fix rather than only how much
     * is known.
     */
    val getMissProfile: GetMissProfileUseCase by lazy {
        GetMissProfileUseCase(lessonRepository, reviewRepository)
    }

    val scorePlacement: ScorePlacementUseCase by lazy { ScorePlacementUseCase() }

    /** Placement as a skill assessment (P1-1): scores *and* calibrates mastery. */
    val assessPlacement: AssessPlacementUseCase by lazy {
        AssessPlacementUseCase(progressRepository)
    }

    companion object {
        /** The one file the learner's progress lives in — and the one a repair moves. */
        const val DATABASE_NAME = "english.db"

        /** Where an unreadable database is kept after a repair (never backed up). */
        const val CORRUPT_DATABASE_DIR = "corrupt-database"
    }
}
