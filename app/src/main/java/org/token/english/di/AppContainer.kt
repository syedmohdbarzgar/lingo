package org.token.english.di

import android.content.Context
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
import org.token.english.core.audio.AudioPlayer
import org.token.english.core.audio.Speaker
import org.token.english.core.audio.TtsAudioPlayer
import org.token.english.data.content.ContentSeeder
import org.token.english.data.local.AppDatabase
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
import org.token.english.domain.repository.KnowledgeRepository
import org.token.english.domain.repository.LessonRepository
import org.token.english.domain.repository.ProgressRepository
import org.token.english.domain.repository.ReviewRepository
import org.token.english.domain.repository.SettingsRepository
import org.token.english.domain.repository.VocabularyRepository
import org.token.english.domain.usecase.CompleteLessonUseCase
import org.token.english.domain.usecase.GetFocusPlanUseCase
import org.token.english.domain.usecase.GetSkillProfilesUseCase
import org.token.english.domain.usecase.GetTodayPlanUseCase
import org.token.english.domain.usecase.AssessPlacementUseCase
import org.token.english.domain.usecase.ScorePlacementUseCase
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
        Room.databaseBuilder(appContext, AppDatabase::class.java, "english.db")
            // Every migration must be listed here: Room throws at open time when a
            // path from the installed version is missing, which would crash an
            // upgrading install before the seeder ever runs.
            .addMigrations(
                AppDatabase.MIGRATION_1_2,
                AppDatabase.MIGRATION_2_3,
                AppDatabase.MIGRATION_3_4,
                AppDatabase.MIGRATION_4_5,
                AppDatabase.MIGRATION_5_6,
            )
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
    val progressRepository: ProgressRepository by lazy {
        ProgressRepositoryImpl(
            contentDao = database.contentDao(),
            reviewDao = database.reviewDao(),
            progressDao = database.progressDao(),
            masteryEngine = masteryEngine,
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

    // Content
    private val _contentSeeded = MutableStateFlow(false)

    /** True once this process finished writing the curriculum bundle to Room. */
    val contentSeeded: StateFlow<Boolean> = _contentSeeded.asStateFlow()

    fun markContentSeeded() {
        _contentSeeded.value = true
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
    val completeLesson: CompleteLessonUseCase by lazy {
        CompleteLessonUseCase(lessonRepository, progressRepository)
    }
    val submitReview: SubmitReviewUseCase by lazy {
        SubmitReviewUseCase(reviewRepository, reviewScheduler, progressRepository)
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
     * The three independent skill tracks (P2): mastery plus the evidence behind it,
     * combined from the learner model and the review log. Home and Progress both read
     * it so neither can define a track its own way.
     */
    val getSkillProfiles: GetSkillProfilesUseCase by lazy {
        GetSkillProfilesUseCase(knowledgeRepository, progressRepository, reviewRepository)
    }

    val scorePlacement: ScorePlacementUseCase by lazy { ScorePlacementUseCase() }

    /** Placement as a skill assessment (P1-1): scores *and* calibrates mastery. */
    val assessPlacement: AssessPlacementUseCase by lazy {
        AssessPlacementUseCase(progressRepository)
    }
}
