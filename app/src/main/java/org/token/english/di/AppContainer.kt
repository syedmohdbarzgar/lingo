package org.token.english.di

import android.content.Context
import android.os.SystemClock
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import org.token.english.core.audio.AudioPlayer
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
import org.token.english.domain.usecase.GetTodayPlanUseCase
import org.token.english.domain.usecase.ScorePlacementUseCase
import org.token.english.domain.usecase.SubmitExerciseUseCase
import org.token.english.domain.usecase.SubmitReviewUseCase

/**
 * Manual dependency container. Constructor injection at the class level,
 * container-level wiring here — Hilt/KSP-Hilt is deliberately avoided to keep the
 * MVP's build simple under AGP 9 built-in Kotlin (see AGENTS.md).
 */
class AppContainer(context: Context, appScope: CoroutineScope) {

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

    // Store billing — implementation comes from the active product flavor
    // (src/bazaar, src/myket or src/googlePlay). See AGENTS.md §4.
    val billing: org.token.english.core.billing.BillingGateway by lazy {
        org.token.english.billing.PlatformBillingGateway(appContext, settingsRepository)
    }

    /** Starts the trial if needed, then asks the store about the subscription. */
    suspend fun refreshEntitlements() {
        org.token.english.core.common.runCatchingCancellable {
            settingsRepository.ensureTrialStarted(
                now = System.currentTimeMillis(),
                elapsedRealtime = SystemClock.elapsedRealtime(),
            )
            if (billing.initialize()) billing.checkSubscription()
        }.onFailure { e ->
            android.util.Log.w("AppContainer", "entitlement refresh failed", e)
        }
    }

    /**
     * Advances the persisted trial checkpoint using real elapsed time (P3):
     * called once a minute while the process lives, so a reboot or a moved wall
     * clock can steal at most a minute of trial time.
     */
    suspend fun persistTrialProgress() {
        org.token.english.core.common.runCatchingCancellable {
            val ts = settingsRepository.observeTrialAndSubscription().first()
            val state = ts.trialClockState()
            if (!org.token.english.core.billing.TrialClock.isStarted(state)) {
                return@runCatchingCancellable
            }
            val advanced = org.token.english.core.billing.TrialClock.advance(
                state,
                System.currentTimeMillis(),
                SystemClock.elapsedRealtime(),
            )
            if (advanced != state) {
                settingsRepository.saveTrialCheckpoint(
                    consumedMs = advanced.consumedMs,
                    lastWallMs = advanced.lastWallMs,
                    lastElapsedMs = advanced.lastElapsedMs,
                )
            }
        }
    }

    /** Mirrors the sound setting; kept hot by [org.token.english.EnglishApp]. */
    @Volatile
    var soundEnabled: Boolean = true

    /** Speaks only when the user has sound enabled in settings. */
    fun speak(text: String, onDone: (() -> Unit)? = null) {
        if (soundEnabled) {
            audioPlayer.speak(text, onDone)
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
        GetTodayPlanUseCase(lessonRepository, reviewRepository, progressRepository, settingsRepository, learningPlanner)
    }
    val scorePlacement: ScorePlacementUseCase by lazy { ScorePlacementUseCase() }
}
