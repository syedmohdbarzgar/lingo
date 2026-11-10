package org.token.english

import android.app.Activity
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.token.english.core.audio.AudioPlayer
import org.token.english.core.audio.Speaker
import org.token.english.core.common.TimeUtil
import org.token.english.core.billing.BillingGateway
import org.token.english.core.billing.ProductIds
import org.token.english.core.billing.PurchaseOutcome
import org.token.english.core.billing.Storefront
import org.token.english.core.billing.SubscriptionPlan
import org.token.english.core.billing.TrialAndSubscription
import org.token.english.domain.model.AppSettings
import org.token.english.domain.model.AttemptStats
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.ExerciseFormat
import org.token.english.domain.model.GradedCard
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.LessonAttempt
import org.token.english.domain.model.LessonState
import org.token.english.domain.model.MasteryDimension
import org.token.english.domain.model.ReviewAttempt
import org.token.english.domain.model.ReviewContentType
import org.token.english.domain.model.ReviewItem
import org.token.english.domain.model.Skill
import org.token.english.domain.model.StudyStats
import org.token.english.domain.model.ThemeMode
import org.token.english.domain.model.VocabularyItem
import org.token.english.domain.repository.ConversationRepository
import org.token.english.domain.repository.KnowledgeRepository
import org.token.english.domain.repository.LessonRepository
import org.token.english.domain.repository.ProgressRepository
import org.token.english.domain.repository.ReviewRepository
import org.token.english.domain.repository.SettingsRepository
import org.token.english.domain.repository.VocabularyRepository

// ---------------------------------------------------------------------------
// In-memory fakes for the ViewModel tests (checklist B-6). They implement the
// domain repository interfaces, so a ViewModel can be built with no Android
// context, no Room and no DataStore.
// ---------------------------------------------------------------------------

fun testSettings(
    level: LearningLevel = LearningLevel.A1,
    dailyGoalMinutes: Int = 15,
) = AppSettings(
    isFirstLaunch = false,
    level = level,
    themeMode = ThemeMode.SYSTEM,
    dailyGoalMinutes = dailyGoalMinutes,
    soundEnabled = true,
    contentVersion = 20,
    trialStartedAt = 0L,
    subscriptionUntil = 0L,
)

class FakeSettingsRepository(
    initial: AppSettings = testSettings(),
    trial: TrialAndSubscription = TrialAndSubscription(0L, 0L, 0L, 0L, 0L),
) : SettingsRepository {

    val settingsFlow = MutableStateFlow(initial)
    val trialFlow = MutableStateFlow(trial)

    /** Set to make the next settings read throw, so a load failure can be driven. */
    var failSettingsWith: Throwable? = null

    override val settings: Flow<AppSettings>
        get() = failSettingsWith?.let { error -> flow { throw error } } ?: settingsFlow
    override fun observeTrialAndSubscription(): Flow<TrialAndSubscription> = trialFlow

    override suspend fun completeFirstLaunch() {
        settingsFlow.value = settingsFlow.value.copy(isFirstLaunch = false)
    }

    override suspend fun setLevel(level: LearningLevel) {
        settingsFlow.value = settingsFlow.value.copy(level = level)
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        settingsFlow.value = settingsFlow.value.copy(themeMode = mode)
    }

    override suspend fun setDailyGoalMinutes(minutes: Int) {
        settingsFlow.value = settingsFlow.value.copy(dailyGoalMinutes = minutes)
    }

    override suspend fun setSoundEnabled(enabled: Boolean) {
        settingsFlow.value = settingsFlow.value.copy(soundEnabled = enabled)
    }

    override suspend fun setDailyReminderEnabled(enabled: Boolean) {
        settingsFlow.value = settingsFlow.value.copy(dailyReminderEnabled = enabled)
    }

    override suspend fun setDailyReminderHour(hour: Int) {
        settingsFlow.value = settingsFlow.value.copy(dailyReminderHour = hour.coerceIn(0, 23))
    }

    override suspend fun setContentVersion(version: Int) {
        settingsFlow.value = settingsFlow.value.copy(contentVersion = version)
    }

    override suspend fun setSubscriptionUntil(epochMillis: Long) {
        trialFlow.value = trialFlow.value.copy(subscriptionUntil = epochMillis)
    }
}

class FakeLessonRepository(
    lessons: List<Lesson> = emptyList(),
    states: List<LessonState> = emptyList(),
) : LessonRepository {

    val lessonsFlow = MutableStateFlow(lessons)
    val statesFlow = MutableStateFlow(states)
    val exercises: MutableMap<String, List<Exercise>> = mutableMapOf()
    val vocabulary: MutableMap<String, List<VocabularyItem>> = mutableMapOf()
    val savedIndex = mutableMapOf<String, Int>()
    val completed = mutableSetOf<String>()
    /** Guided-step writes: active step id, completed ids and skipped ids per lesson. */
    val savedStages = mutableMapOf<String, String>()
    val savedCompleted = mutableMapOf<String, Set<String>>()
    val savedSkipped = mutableMapOf<String, Set<String>>()
    /** Completion stamps written by the session, per lesson (Phase 2). */
    val savedCompletedAt = mutableMapOf<String, Map<String, Long>>()
    /** The append-only answer log (Phase 2), in write order. */
    val attemptsLog = mutableListOf<LessonAttempt>()

    override fun observeLessons(): Flow<List<Lesson>> = lessonsFlow
    override fun observeLessonStates(): Flow<List<LessonState>> = statesFlow

    /**
     * When set, the lesson reads throw — a read that cannot reach the disk (a corrupt
     * file, a migration the installed build cannot walk). Clearing it models a retry
     * that works, which is what the lesson and placement error states offer.
     */
    var readError: Throwable? = null

    override suspend fun getLesson(lessonId: String): Lesson? {
        readError?.let { throw it }
        return lessonsFlow.value.firstOrNull { it.id == lessonId }
    }

    override suspend fun getExercises(lessonId: String): List<Exercise> {
        readError?.let { throw it }
        return exercises[lessonId].orEmpty()
    }

    override suspend fun getVocabulary(lessonId: String): List<VocabularyItem> = vocabulary[lessonId].orEmpty()

    override suspend fun saveLessonIndex(lessonId: String, index: Int) {
        savedIndex[lessonId] = index
        statesFlow.value = statesFlow.value.filterNot { it.lessonId == lessonId } +
            LessonState(lessonId, index, completed.contains(lessonId), null)
    }

    override suspend fun saveLessonStage(
        lessonId: String,
        stageId: String,
        completedStages: Set<String>,
        skippedStages: Set<String>,
        completedAt: Map<String, Long>,
    ) {
        savedStages[lessonId] = stageId
        savedCompleted[lessonId] = completedStages
        savedSkipped[lessonId] = skippedStages
        savedCompletedAt[lessonId] = completedAt
    }

    override suspend fun recordAttempt(attempt: LessonAttempt) {
        attemptsLog += attempt
    }

    override suspend fun attempts(lessonId: String): List<LessonAttempt> =
        attemptsLog.filter { it.lessonId == lessonId }

    /** The whole log, newest first — the miss taxonomy's window. */
    override suspend fun recentAttempts(limit: Int): List<LessonAttempt> =
        attemptsLog.sortedByDescending { it.at }.take(limit)

    /** The guided-step writes are part of the saved state, so a restart sees them. */
    override suspend fun getLessonState(lessonId: String): LessonState? {
        val base = statesFlow.value.firstOrNull { it.lessonId == lessonId }
        val stage = savedStages[lessonId] ?: return base
        return LessonState(
            lessonId = lessonId,
            currentIndex = savedIndex[lessonId] ?: base?.currentIndex ?: 0,
            completed = base?.completed ?: completed.contains(lessonId),
            completedAt = base?.completedAt,
            stageId = stage,
            completedStages = savedCompleted[lessonId].orEmpty(),
            skippedStages = savedSkipped[lessonId].orEmpty(),
            stageCompletedAt = savedCompletedAt[lessonId].orEmpty(),
        )
    }

    override suspend fun completeLesson(lessonId: String, at: Long) {
        completed += lessonId
    }

    override suspend fun enqueueLessonVocabulary(lessonId: String, now: Long) = Unit

    /** Lessons whose fake exercise list contains a spoken turn. */
    override suspend fun lessonIdsWithDialogue(): Set<String> =
        exercises.filterValues { list -> list.any { it is Exercise.Speaking } }.keys
}

/**
 * Finished conversations, in memory. The gate reads the same set the practice
 * screen writes, so a test can walk a dialogue and see the next lesson open.
 */
class FakeConversationRepository(
    initial: Set<String> = emptySet(),
) : ConversationRepository {

    val doneFlow = MutableStateFlow(initial)

    override fun observeCompleted(): Flow<Set<String>> = doneFlow

    override suspend fun markCompleted(lessonId: String) {
        doneFlow.value = doneFlow.value + lessonId
    }

    override suspend fun reset() {
        doneFlow.value = emptySet()
    }
}

class FakeVocabularyRepository(
    initial: List<VocabularyItem> = emptyList(),
) : VocabularyRepository {

    val allFlow = MutableStateFlow(initial)

    override fun observeAll(): Flow<List<VocabularyItem>> = allFlow

    override suspend fun search(query: String): List<VocabularyItem> =
        allFlow.value.filter { it.word.contains(query, ignoreCase = true) }

    override suspend fun get(id: String): VocabularyItem? = allFlow.value.firstOrNull { it.id == id }

    override suspend fun count(): Int = allFlow.value.size
}

class FakeReviewRepository : ReviewRepository {

    val dueFlow = MutableStateFlow<List<ReviewItem>>(emptyList())
    val scheduled = mutableListOf<ReviewItem>()
    val attempts = mutableListOf<ReviewAttempt>()

    /** Review-log totals the skill-profile use case folds into a track (P2). */
    var attemptStats: Map<ReviewContentType, AttemptStats> = emptyMap()

    /** Set to make the next queue read throw, so a load failure can be driven. */
    var failDueWith: Throwable? = null

    override fun observeDueCount(now: Long): Flow<Int> = dueFlow.map { it.size }

    override suspend fun getDue(now: Long, limit: Int): List<ReviewItem> {
        failDueWith?.let { throw it }
        return dueFlow.value.take(limit)
    }

    override suspend fun countDue(now: Long): Int = dueFlow.value.size

    override suspend fun getItem(contentId: String): ReviewItem? =
        dueFlow.value.firstOrNull { it.contentId == contentId }

    override suspend fun schedule(item: ReviewItem) {
        scheduled += item
        dueFlow.value = dueFlow.value.filterNot { it.contentId == item.contentId } + item
    }

    override suspend fun recordAttempt(attempt: ReviewAttempt) {
        attempts += attempt
    }

    override suspend fun getAttempts(contentId: String): List<ReviewAttempt> =
        attempts.filter { it.contentId == contentId }

    override suspend fun attemptStatsByType(): Map<ReviewContentType, AttemptStats> = attemptStats

    /** Grades the miss taxonomy reads, newest first like the DAO's query. */
    val gradedCards = mutableListOf<GradedCard>()

    override suspend fun recentCards(limit: Int): List<GradedCard> =
        gradedCards.sortedByDescending { it.at }.take(limit)

    override suspend fun reset() {
        dueFlow.value = emptyList()
    }
}

class FakeProgressRepository : ProgressRepository {

    val masteryFlow = MutableStateFlow<Map<Skill, Float>>(emptyMap())
    val statsFlow = MutableStateFlow(StudyStats(0L, 0, 0, 0, 0, 0f))
    val attempts = mutableListOf<Pair<Skill, Boolean>>()
    var studySeconds: Long = 0L

    /** Cross-session dimension rows (checklist B-1, Room v6): seed + write log. */
    val dimensionFlow = MutableStateFlow<Map<MasteryDimension, Float>>(emptyMap())
    val dimensionWrites = mutableListOf<Pair<MasteryDimension, Float>>()

    override fun observeMastery(): Flow<Map<Skill, Float>> = masteryFlow
    override fun observeStats(): Flow<StudyStats> = statsFlow

    /** exercise id → (day key, correct) claimed, mirroring the v9 `question_attempt` row. */
    val claimed = mutableMapOf<String, Pair<Int, Boolean>>()

    /** How many times a whole answer was submitted as one transaction (checklist 0.1). */
    var transactions = 0

    /**
     * When set, [inTransaction] refuses to open: nothing inside runs and the caller
     * sees the failure, exactly like a transaction that could not be written. The
     * fake keeps no partial rows, because a real one would have rolled them back.
     */
    var transactionError: Throwable? = null

    override suspend fun <T> inTransaction(block: suspend () -> T): T {
        transactions++
        transactionError?.let { throw it }
        return block()
    }

    override suspend fun claimAnswer(
        lessonId: String,
        exerciseId: String,
        correct: Boolean,
        now: Long,
    ): Boolean {
        val dayKey = TimeUtil.dateKey(now)
        val existing = claimed[exerciseId]
        if (existing != null && existing.first == dayKey && (existing.second || !correct)) return false
        claimed[exerciseId] = dayKey to correct
        return true
    }

    override suspend fun applyAttempt(skill: Skill, correct: Boolean, source: String) {
        attempts += skill to correct
        val current = masteryFlow.value.toMutableMap()
        val previous = current[skill] ?: 0f
        val target = if (correct) 1f else 0f
        current[skill] = previous + (target - previous) * 0.3f
        masteryFlow.value = current
    }

    override suspend fun getDimensionMastery(): Map<MasteryDimension, Float> = dimensionFlow.value

    /** When set, the dimension fold fails — session input, not evidence (debt.md). */
    var dimensionError: Throwable? = null

    override suspend fun setDimensionMastery(dimension: MasteryDimension, mastery: Float) {
        dimensionError?.let { throw it }
        dimensionWrites += dimension to mastery
        dimensionFlow.value = dimensionFlow.value + (dimension to mastery)
    }

    override suspend fun addStudySeconds(seconds: Long) {
        studySeconds += seconds
    }

    override suspend fun reset() {
        masteryFlow.value = emptyMap()
        dimensionFlow.value = emptyMap()
        dimensionWrites.clear()
    }
}

class FakeKnowledgeRepository(
    items: List<KnowledgeItem> = emptyList(),
) : KnowledgeRepository {

    val itemsFlow = MutableStateFlow(items)
    val statesFlow = MutableStateFlow<List<KnowledgeState>>(emptyList())
    val recorded = mutableListOf<Pair<String, Boolean>>()

    /** Which track (skill) and shape each answer was attributed to. */
    val recordedSkills = mutableListOf<Pair<Skill, ExerciseFormat>>()

    /**
     * When set, [allItems] suspends on it — lets a test cancel a ViewModel while a
     * use case is mid-lookup (checklist B-9: cancellation must not be swallowed).
     */
    var allItemsGate: CompletableDeferred<Unit>? = null

    override fun observeStates(): Flow<List<KnowledgeState>> = statesFlow
    override fun observePractisedCount(): Flow<Int> = statesFlow.map { it.size }
    override suspend fun getState(itemId: String): KnowledgeState? = statesFlow.value.firstOrNull { it.itemId == itemId }
    override suspend fun itemsForLesson(lessonId: String): List<KnowledgeItem> =
        itemsFlow.value.filter { lessonId in it.lessonIds }

    override suspend fun allItems(): List<KnowledgeItem> {
        allItemsGate?.await()
        return itemsFlow.value
    }

    override suspend fun recordAttempt(
        lessonId: String,
        skill: Skill,
        format: ExerciseFormat,
        correct: Boolean,
        now: Long,
    ) {
        recorded += lessonId to correct
        recordedSkills += skill to format
    }

    /** Review grades that reached the graph: (lesson, skill, correct). */
    val recalled = mutableListOf<Triple<String?, Skill, Boolean>>()

    override suspend fun recordRecall(
        lessonId: String?,
        skill: Skill,
        correct: Boolean,
        now: Long,
    ) {
        recalled += Triple(lessonId, skill, correct)
    }

    override suspend fun reset() {
        statesFlow.value = emptyList()
    }
}

/** Audio player that reports a working English voice and finishes instantly. */
class FakeAudioPlayer(available: Boolean? = true) : AudioPlayer {

    override val isEnglishAvailable = MutableStateFlow(available)
    override var isSpeaking: Boolean = false
    var stopped: Boolean = false

    override fun speak(text: String, onDone: (() -> Unit)?, onError: (() -> Unit)?) {
        onDone?.invoke()
    }

    override fun stop() {
        stopped = true
    }

    override fun release() = Unit
}

/**
 * Store double (checklist v8 item 3): the marketplace is whatever the test says
 * it is — reachable or not, priced or not, purchase succeeding, cancelled or
 * failing — so every paywall path is reachable without a real store account.
 */
class FakeBillingGateway(
    override val storefront: Storefront = Storefront.BAZAAR,
    /** What `initialize()` answers (a store app that is missing/unusable). */
    var storeReachable: Boolean = true,
    var plans: List<SubscriptionPlan> = listOf(
        SubscriptionPlan(ProductIds.MONTHLY, "اشتراک ماهانه", "یک ماه", "۱۰۰٬۰۰۰ تومان", monthly = true),
        SubscriptionPlan(ProductIds.YEARLY, "اشتراک سالانه", "یک سال", "۵۰۰٬۰۰۰ تومان", monthly = false),
    ),
    var purchaseOutcome: PurchaseOutcome = PurchaseOutcome.Success,
    var subscriptionActive: Boolean = false,
    /** When set, every call throws it — how a store failure reaches the UI. */
    var throwOnCall: Throwable? = null,
) : BillingGateway {

    var initializeCalls = 0
    var checkCalls = 0
    val purchased = mutableListOf<SubscriptionPlan>()

    private fun crashIfAsked() {
        throwOnCall?.let { throw it }
    }

    override suspend fun initialize(): Boolean {
        initializeCalls++
        crashIfAsked()
        return storeReachable
    }

    override suspend fun fetchPlans(): List<SubscriptionPlan> {
        crashIfAsked()
        return plans
    }

    override suspend fun purchase(activity: Activity, plan: SubscriptionPlan): PurchaseOutcome {
        purchased += plan
        crashIfAsked()
        return purchaseOutcome
    }

    override suspend fun checkSubscription(): Boolean {
        checkCalls++
        crashIfAsked()
        return subscriptionActive
    }

    override suspend fun subscriptionUntil(): Long = if (subscriptionActive) Long.MAX_VALUE else 0L
}

/**
 * In-memory [VoiceNote]: records which actions the ViewModel asked for and keeps
 * the same state contract as the real recorder (mic open, take on disk, playing).
 * No file, no microphone — the speaking flow is tested without any device audio.
 */
class FakeVoiceNote(
    var permission: Boolean = true,
) : org.token.english.core.audio.VoiceNote {

    /** How many times each action was asked for — the silence rules are asserted on it. */
    var stopPlaybackCalls = 0
        private set
    var stopRecordingCalls = 0
        private set

    private val _isRecording = MutableStateFlow(false)
    override val isRecording: StateFlow<Boolean> = _isRecording

    private val _hasTake = MutableStateFlow(false)
    override val hasTake: StateFlow<Boolean> = _hasTake

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _error = MutableStateFlow(false)
    override val error: StateFlow<Boolean> = _error

    val started = mutableListOf<Int>()
    var released = false
        private set

    /** True once the reset path asked for the take to be deleted. */
    var discarded = false
        private set

    override fun hasMicrophonePermission(): Boolean = permission

    override fun startRecording(): Boolean {
        if (!permission) {
            _error.value = true
            return false
        }
        started += 1
        _error.value = false
        _isRecording.value = true
        return true
    }

    override fun stopRecording() {
        stopRecordingCalls += 1
        if (_isRecording.value) {
            _isRecording.value = false
            _hasTake.value = true
        }
    }

    override fun playTake() {
        if (_hasTake.value && !_isRecording.value) _isPlaying.value = true
    }

    override fun stopPlayback() {
        stopPlaybackCalls += 1
        _isPlaying.value = false
    }

    override fun discardTake() {
        discarded = true
        _hasTake.value = false
    }

    override fun release() {
        released = true
    }
}

/** Records what a ViewModel asked to speak; never throws. */
class RecordingSpeaker(
    /**
     * False models a line that is still playing: the completion callback is kept
     * until [finishSpeaking], which is how a test holds the mic shut.
     */
    private val finishImmediately: Boolean = true,
) : Speaker {
    val spoken = mutableListOf<String>()
    var stops = 0

    private var pendingDone: (() -> Unit)? = null

    override fun speak(text: String, onDone: (() -> Unit)?, onError: (() -> Unit)?) {
        spoken += text
        if (finishImmediately) onDone?.invoke() else pendingDone = onDone
    }

    /** Ends the line the way the TTS engine would. */
    fun finishSpeaking() {
        pendingDone?.invoke()
        pendingDone = null
    }

    override fun stop() {
        stops++
        pendingDone = null
    }
}
