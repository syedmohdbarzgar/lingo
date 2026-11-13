package org.token.english.data.repository

import android.content.Context
import android.util.Log
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.token.english.core.common.TimeUtil
import org.token.english.core.common.runCatchingCancellable
import org.token.english.data.content.ContentParser
import org.json.JSONArray
import org.token.english.data.local.dao.ContentDao
import org.token.english.data.local.dao.ConversationDao
import org.token.english.data.local.dao.KnowledgeDao
import org.token.english.data.local.dao.ProgressDao
import org.token.english.data.local.dao.ReviewDao
import org.token.english.data.local.entity.ConversationDoneEntity
import org.token.english.data.local.entity.DimensionMasteryEntity
import org.token.english.data.local.entity.ExerciseEntity
import org.token.english.data.local.entity.KnowledgeItemEntity
import org.token.english.data.local.entity.KnowledgeStateEntity
import org.token.english.data.local.entity.LessonEntity
import org.token.english.data.local.entity.LessonAttemptEntity
import org.token.english.data.local.entity.LessonStateEntity
import org.token.english.data.local.entity.ReviewAttemptEntity
import org.token.english.data.local.entity.ReviewItemEntity
import org.token.english.data.local.entity.VocabularyEntity
import org.token.english.domain.engine.DefaultKnowledgeEngine
import org.token.english.domain.engine.KnowledgeEvidence
import org.token.english.domain.engine.MasteryEngine
import org.token.english.domain.model.AppSettings
import org.token.english.domain.model.AttemptStats
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.ExerciseFormat
import org.token.english.domain.model.GradedCard
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.KnowledgeType
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.LessonAttempt
import org.token.english.domain.model.LessonState
import org.token.english.domain.model.MasteryDimension
import org.token.english.domain.model.ReviewAttempt
import org.token.english.domain.model.ReviewContentType
import org.token.english.domain.model.ReviewItem
import org.token.english.domain.model.ReviewResult
import org.token.english.domain.model.ReviewState
import org.token.english.domain.model.Skill
import org.token.english.domain.model.StudyStats
import org.token.english.domain.model.ThemeMode
import org.token.english.domain.model.VocabularyExample
import org.token.english.domain.model.VocabularyItem
import org.token.english.domain.repository.ConversationRepository
import org.token.english.domain.repository.KnowledgeRepository
import org.token.english.domain.repository.LessonRepository
import org.token.english.domain.repository.ProgressRepository
import org.token.english.domain.repository.ReviewRepository
import org.token.english.domain.repository.SettingsRepository
import org.token.english.domain.repository.VocabularyRepository

// ------------------------------------------------------- Knowledge graph

/**
 * Learner state per knowledge item. Writes are one atomic read-modify-write per
 * affected node, and reads rebuild the node list from the seeded content table so
 * a re-seed can never leave stale curriculum data behind.
 */
class KnowledgeRepositoryImpl(
    private val contentDao: ContentDao,
    private val knowledgeDao: KnowledgeDao,
    private val engine: DefaultKnowledgeEngine = DefaultKnowledgeEngine(),
) : KnowledgeRepository {

    override fun observeStates(): Flow<List<KnowledgeState>> =
        knowledgeDao.observeStates().map { rows -> rows.map { it.toDomain() } }

    override fun observePractisedCount(): Flow<Int> =
        knowledgeDao.observeStates().map { it.size }

    override suspend fun getState(itemId: String): KnowledgeState? =
        knowledgeDao.getState(itemId)?.toDomain()

    override suspend fun itemsForLesson(lessonId: String): List<KnowledgeItem> =
        allItems().filter { lessonId in it.lessonIds }

    override suspend fun allItems(): List<KnowledgeItem> =
        contentDao.getKnowledgeItems().map { it.toDomain() }

    override suspend fun recordAttempt(
        lessonId: String,
        skill: Skill,
        format: ExerciseFormat,
        correct: Boolean,
        now: Long,
    ) {
        // The caller has already claimed this exercise for the day; anything written
        // here is new evidence by construction (see ProgressRepository.claimAnswer).
        KnowledgeEvidence.itemsFor(itemsForLesson(lessonId), skill).forEach { item ->
            knowledgeDao.updateAtomic(item.id) { existing ->
                engine.applyAttempt(item.id, existing?.toDomain(), correct, now, format).toEntity()
            }
        }
    }

    override suspend fun recordRecall(
        lessonId: String?,
        skill: Skill,
        correct: Boolean,
        now: Long,
        cardId: String?,
    ) {
        // No lesson, no attribution: the repository never invents nodes for a card it
        // cannot place (the same rule as an unknown lesson in `recordAttempt`).
        val lesson = lessonId ?: return
        // The card's own word, so a lesson with two vocabulary nodes credits the one
        // that teaches it. A card id the content no longer ships resolves to null and
        // falls back to "the lesson's node of this type", which is exact when unique.
        val word = cardId?.let { contentDao.getVocabulary(it)?.word }
        KnowledgeEvidence.itemsForCard(itemsForLesson(lesson), skill, word).forEach { item ->
            knowledgeDao.updateAtomic(item.id) { existing ->
                // format = null on purpose: review is recall, not a transfer claim (see
                // the contract in `KnowledgeRepository.recordRecall`).
                engine.applyAttempt(item.id, existing?.toDomain(), correct, now, format = null).toEntity()
            }
        }
    }

    override suspend fun reset() = knowledgeDao.clear()
}

private fun KnowledgeStateEntity.toDomain(): KnowledgeState = KnowledgeState(
    itemId = itemId,
    mastery = mastery,
    exposureCount = exposureCount,
    consecutiveCorrect = consecutiveCorrect,
    consecutiveIncorrect = consecutiveIncorrect,
    intervalDays = intervalDays,
    easeFactor = easeFactor,
    repetitions = repetitions,
    lapses = lapses,
    lastReviewedAt = lastReviewedAt,
    nextReviewAt = nextReviewAt,
    firstCorrectAt = firstCorrectAt,
    distinctTypesCorrect = distinctTypesCorrect,
)

private fun KnowledgeState.toEntity(): KnowledgeStateEntity = KnowledgeStateEntity(
    itemId = itemId,
    mastery = mastery,
    exposureCount = exposureCount,
    consecutiveCorrect = consecutiveCorrect,
    consecutiveIncorrect = consecutiveIncorrect,
    intervalDays = intervalDays,
    easeFactor = easeFactor,
    repetitions = repetitions,
    lapses = lapses,
    lastReviewedAt = lastReviewedAt,
    nextReviewAt = nextReviewAt,
    firstCorrectAt = firstCorrectAt,
    distinctTypesCorrect = distinctTypesCorrect,
)

private fun KnowledgeItemEntity.toDomain(): KnowledgeItem = KnowledgeItem(
    id = id,
    type = KnowledgeType.valueOf(type.uppercase()),
    title = title,
    titleFa = titleFa,
    level = LearningLevel.valueOf(level),
    prerequisites = prerequisitesJson.toStringList(),
    lessonIds = lessonIdsJson.toStringList(),
    skills = skillsJson.toStringList()
        .mapNotNull { runCatching { Skill.valueOf(it.uppercase()) }.getOrNull() },
    words = wordsJson.toStringList(),
)

/** Tolerates a malformed cell rather than crashing the whole graph. */
private fun String.toStringList(): List<String> = runCatching {
    val array = JSONArray(this)
    (0 until array.length()).map { array.getString(it) }
}.getOrDefault(emptyList())

// ---------------------------------------------------------------- Lessons

/** Max cards introduced (made due) per calendar day — daily new-card cap (P5). */
private const val NEW_CARDS_PER_DAY = 20

/** Seconds of study required before a day counts toward the streak (P7). */
private const val MIN_STREAK_STUDY_SECONDS = 60L

class LessonRepositoryImpl(
    private val contentDao: ContentDao,
    private val reviewDao: ReviewDao,
) : LessonRepository {

    override fun observeLessons(): Flow<List<Lesson>> =
        contentDao.observeLessons().map { list -> list.map { it.toDomain() } }

    override fun observeLessonStates(): Flow<List<LessonState>> =
        contentDao.observeLessonStates().map { list -> list.map { it.toDomain() } }

    override suspend fun getLesson(lessonId: String): Lesson? =
        contentDao.getLesson(lessonId)?.toDomain()

    override suspend fun getExercises(lessonId: String): List<Exercise> =
        contentDao.getExercises(lessonId).mapNotNull {
            ContentParser.exerciseFromPayload(it.payloadJson, lessonId)
        }

    override suspend fun getVocabulary(lessonId: String): List<VocabularyItem> =
        contentDao.getVocabularyByLesson(lessonId).map { it.toDomain() }

    override suspend fun saveLessonIndex(lessonId: String, index: Int) {
        contentDao.ensureLessonState(lessonId)
        contentDao.updateLessonIndex(lessonId, index)
    }

    override suspend fun saveLessonStage(
        lessonId: String,
        stageId: String,
        completedStages: Set<String>,
        skippedStages: Set<String>,
        completedAt: Map<String, Long>,
    ) {
        contentDao.ensureLessonState(lessonId)
        contentDao.updateLessonStage(
            lessonId = lessonId,
            stage = stageId,
            completed = completedStages.sorted().joinToString(","),
            skipped = skippedStages.sorted().joinToString(","),
            // `/` cannot appear in a stage id or a millis value, so the pairs stay
            // unambiguous without a JSON dependency for two columns.
            completedAt = completedAt.entries
                .sortedBy { it.key }
                .joinToString(",") { (stage, at) -> "$stage=$at" },
        )
    }

    override suspend fun recordAttempt(attempt: LessonAttempt) {
        contentDao.insertLessonAttempt(
            LessonAttemptEntity(
                lessonId = attempt.lessonId,
                stageId = attempt.stageId,
                exerciseId = attempt.exerciseId,
                answer = attempt.answer,
                correct = attempt.correct,
                at = attempt.at,
            ),
        )
    }

    override suspend fun attempts(lessonId: String): List<LessonAttempt> =
        contentDao.getLessonAttempts(lessonId).map {
            LessonAttempt(
                lessonId = it.lessonId,
                stageId = it.stageId,
                exerciseId = it.exerciseId,
                answer = it.answer,
                correct = it.correct,
                at = it.at,
            )
        }

    override suspend fun recentAttempts(limit: Int): List<LessonAttempt> =
        contentDao.getRecentLessonAttempts(limit).map {
            LessonAttempt(
                lessonId = it.lessonId,
                stageId = it.stageId,
                exerciseId = it.exerciseId,
                answer = it.answer,
                correct = it.correct,
                at = it.at,
            )
        }

    override suspend fun getLessonState(lessonId: String): LessonState? =
        contentDao.getLessonState(lessonId)?.toDomain()

    override suspend fun completeLesson(lessonId: String, at: Long) {
        contentDao.ensureLessonState(lessonId)
        contentDao.markLessonCompleted(lessonId, at)
    }

    override suspend fun lessonIdsWithDialogue(): Set<String> =
        contentDao.lessonIdsWithDialogue().toSet()

    override suspend fun enqueueLessonVocabulary(lessonId: String, now: Long) {
        // Daily new-card cap: at most NEW_CARDS_PER_DAY cards become due per
        // calendar day; the overflow waits for tomorrow instead of flooding the
        // first review session with the whole lesson at once (checklist P5).
        val dayStart = TimeUtil.startOfDay(now)
        val tomorrowStart = TimeUtil.startOfDayPlusDays(now, 1)
        var introducedToday = reviewDao.countIntroducedSince(dayStart)
        contentDao.getVocabularyByLesson(lessonId).forEach { vocab ->
            if (reviewDao.getItem(vocab.id) != null) return@forEach
            val withinCap = introducedToday < NEW_CARDS_PER_DAY
            reviewDao.upsertItem(
                ReviewItemEntity(
                    contentId = vocab.id,
                    contentType = ReviewContentType.VOCABULARY.name,
                    state = ReviewState.NEW.name,
                    dueAt = if (withinCap) now else tomorrowStart,
                    intervalDays = 0,
                    easeFactor = org.token.english.domain.engine.Sm2ReviewScheduler.DEFAULT_EASE,
                    repetitions = 0,
                    lapses = 0,
                    lastReviewedAt = null,
                    introducedAt = now,
                ),
            )
            if (withinCap) introducedToday++
        }
    }
}

// ------------------------------------------------------------- Vocabulary

class VocabularyRepositoryImpl(
    private val contentDao: ContentDao,
) : VocabularyRepository {

    override fun observeAll(): Flow<List<VocabularyItem>> =
        contentDao.observeVocabulary().map { list -> list.map { it.toDomain() } }

    override suspend fun search(query: String): List<VocabularyItem> {
        // %, _ and \ are LIKE wildcards — escape them so a learner typing "c++"
        // or "100%" gets a literal search (checklist P7).
        val trimmed = query.trim()
        val rows = contentDao.searchVocabulary(escapeLike(trimmed))
        return rows.map { it.toDomain() }
    }

    private fun escapeLike(input: String): String {
        if (input.isEmpty()) return ""
        return input
            .replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_")
    }

    override suspend fun get(id: String): VocabularyItem? = contentDao.getVocabulary(id)?.toDomain()

    override suspend fun count(): Int = contentDao.countVocabulary()
}

// ----------------------------------------------------------------- Review

class ReviewRepositoryImpl(
    private val reviewDao: ReviewDao,
) : ReviewRepository {

    override fun observeDueCount(now: Long): Flow<Int> = reviewDao.observeDueCount(now)

    override suspend fun getDue(now: Long, limit: Int): List<ReviewItem> =
        reviewDao.getDue(now, limit).map { it.toDomain() }

    override suspend fun countDue(now: Long): Int = reviewDao.countDue(now)

    override suspend fun getItem(contentId: String): ReviewItem? =
        reviewDao.getItem(contentId)?.toDomain()

    override suspend fun schedule(item: ReviewItem) = reviewDao.upsertItem(item.toEntity())

    override suspend fun recordAttempt(attempt: ReviewAttempt) =
        reviewDao.insertAttempt(attempt.toEntity())

    override suspend fun getAttempts(contentId: String): List<ReviewAttempt> =
        reviewDao.getAttempts(contentId).map { it.toDomain() }

    override suspend fun attemptStatsByType(): Map<ReviewContentType, AttemptStats> =
        reviewDao.attemptStatsByContentType().mapNotNull { row ->
            // An unknown legacy value counts for no track rather than a guessed one.
            val type = ReviewContentType.entries.firstOrNull { it.name == row.contentType }
                ?: return@mapNotNull null
            type to AttemptStats(attempts = row.total, misses = row.total - row.correct)
        }.toMap()

    override suspend fun recentCards(limit: Int): List<GradedCard> =
        reviewDao.recentGrades(limit).mapNotNull { row ->
            // A grade this build cannot name (a legacy result string) is dropped: the
            // taxonomy classifies known grades or says nothing, never guesses.
            val result = ReviewResult.entries.firstOrNull { it.name == row.result }
                ?: return@mapNotNull null
            GradedCard(
                result = result,
                type = ReviewContentType.entries.firstOrNull { it.name == row.contentType },
                at = row.timestamp,
            )
        }

    override suspend fun reset() {
        reviewDao.clearItems()
        reviewDao.clearAttempts()
    }
}

// --------------------------------------------------------------- Progress

class ProgressRepositoryImpl(
    private val contentDao: ContentDao,
    private val reviewDao: ReviewDao,
    private val progressDao: ProgressDao,
    private val masteryEngine: MasteryEngine,
    /**
     * The database itself, for the one call that spans a whole answer. The DAOs
     * cannot open a transaction across each other, and an answer is written by
     * three of them (checklist 0.1).
     */
    private val database: RoomDatabase,
) : ProgressRepository {

    override fun observeMastery(): Flow<Map<Skill, Float>> =
        progressDao.observeMastery().map { rows ->
            rows.mapNotNull { row ->
                runCatching { Skill.valueOf(row.skill) to row.mastery }.getOrNull()
            }.toMap()
        }

    /**
     * Stats straight from SQL aggregates (COUNT/SUM) — no more loading every
     * review row into memory (checklist P7). "Today" boundaries are computed by
     * SQLite at query time, so counts refresh whenever the tables change.
     * reviewAccuracy counts only GOOD/EASY as correct (defined in StudyStats).
     */
    override fun observeStats(): Flow<StudyStats> {
        val study = combine(
            progressDao.observeTodaySeconds(),
            progressDao.observeSessions(),
        ) { seconds, sessions -> (seconds ?: 0L) to sessions }
        return combine(
            reviewDao.observeAttemptCountToday(),
            reviewDao.observeAttemptAggregate(),
            contentDao.observeCompletedLessonCount(),
            reviewDao.observeWordsLearnedCount(),
            study,
        ) { reviewsToday, attempts, completed, words, (todaySeconds, sessions) ->
            val now = System.currentTimeMillis()
            StudyStats(
                todayStudySeconds = todaySeconds,
                // A day only counts toward the streak after a meaningful session
                // (a few seconds of app-open time must not build a streak, P7).
                streakDays = TimeUtil.streakDays(
                    sessions.filter { it.seconds >= MIN_STREAK_STUDY_SECONDS }.map { it.dateKey },
                    now,
                ),
                lessonsCompleted = completed,
                wordsLearned = words,
                reviewsToday = reviewsToday,
                reviewAccuracy = if (attempts.total == 0) {
                    0f
                } else {
                    attempts.correct.toFloat() / attempts.total
                },
            )
        }
    }

    override suspend fun applyAttempt(skill: Skill, correct: Boolean, source: String) {
        // Read + update + write happen inside one Room transaction (checklist P2),
        // and the FIRST observation seeds the value instead of starting every
        // skill at 0 (checklist P6): a skill answered correctly starts at 1.0 and
        // an EWMA carries it from there.
        progressDao.updateMasteryAtomic(skill.name) { current ->
            val seed = if (correct) 1f else 0f
            masteryEngine.update(current ?: seed, correct)
        }
    }

    override suspend fun claimAnswer(
        lessonId: String,
        exerciseId: String,
        correct: Boolean,
        now: Long,
    ): Boolean = progressDao.claimAttempt(
        exerciseId = exerciseId,
        lessonId = lessonId,
        dayKey = TimeUtil.dateKey(now),
        correct = correct,
        at = now,
    )

    /**
     * Room's own transaction, so a crash (or an exception) inside [block] rolls back
     * every row it wrote. Nested calls join the outer transaction instead of opening
     * a second one, which is why the DAO-level `@Transaction` helpers ([claimAnswer],
     * `updateMasteryAtomic`) work unchanged inside it.
     */
    override suspend fun <T> inTransaction(block: suspend () -> T): T =
        database.withTransaction { block() }

    override suspend fun setDimensionMastery(dimension: MasteryDimension, mastery: Float) {
        progressDao.upsertDimension(DimensionMasteryEntity(dimension.name, mastery))
    }

    override suspend fun getDimensionMastery(): Map<MasteryDimension, Float> =
        progressDao.getDimensions().mapNotNull { row ->
            // A row naming a dimension this build does not know (a downgrade, or a
            // future enum constant) must not crash the seed — same guard style as
            // observeMastery's skill valueOf.
            runCatching { MasteryDimension.valueOf(row.dimension) to row.mastery }.getOrNull()
        }.toMap()

    override suspend fun addStudySeconds(seconds: Long) {
        if (seconds <= 0) return
        val key = TimeUtil.dateKey(System.currentTimeMillis())
        val existing = progressDao.getSession(key)?.seconds ?: 0L
        progressDao.upsertSession(
            org.token.english.data.local.entity.StudySessionEntity(dateKey = key, seconds = existing + seconds),
        )
    }

    override suspend fun reset() {
        progressDao.clearMastery()
        progressDao.clearDimensions()
        progressDao.clearSessions()
        // A reset is a fresh learner: yesterday's answers must not come back as claims
        // on the exercises of the new attempt, and the recorded history of answers
        // (Phase 2) must not be read back as "this step was already answered".
        progressDao.clearAttempts()
        contentDao.clearLessonAttempts()
        reviewDao.clearItems()
        reviewDao.clearAttempts()
        contentDao.clearLessonStates()
    }
}

// --------------------------------------------------------------- Settings

private val Context.dataStore by preferencesDataStore(name = "app_settings")

/**
 * Lesson ids whose conversation has been finished (newline-separated). Shared by
 * the settings store's file and the conversation repository, which live next to
 * each other on purpose: it is one small set written once per dialogue.
 */
private val CONVERSATIONS_DONE = stringPreferencesKey("conversations_done")

class SettingsRepositoryImpl(
    private val context: Context,
) : SettingsRepository {

    private object Keys {
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch_done")
        val LEVEL = stringPreferencesKey("level")
        val THEME = stringPreferencesKey("theme_mode")
        val DAILY_GOAL = intPreferencesKey("daily_goal_minutes")
        val SOUND = booleanPreferencesKey("sound_enabled")
        val DAILY_REMINDER = booleanPreferencesKey("daily_reminder_enabled")
        val REMINDER_HOUR = intPreferencesKey("daily_reminder_hour")
        val CONTENT_VERSION = intPreferencesKey("content_version")
        val TRIAL_STARTED = longPreferencesKey("trial_started_at")
        val TRIAL_CONSUMED = longPreferencesKey("trial_consumed_ms")
        val TRIAL_LAST_WALL = longPreferencesKey("trial_last_wall_ms")
        val TRIAL_LAST_ELAPSED = longPreferencesKey("trial_last_elapsed_ms")
        val SUBSCRIPTION_UNTIL = longPreferencesKey("subscription_until")
    }

    override val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            isFirstLaunch = (prefs[Keys.FIRST_LAUNCH] ?: false).not(),
            level = prefs[Keys.LEVEL]?.let { runCatching { LearningLevel.valueOf(it) }.getOrNull() }
                ?: LearningLevel.A1,
            themeMode = prefs[Keys.THEME]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
                ?: ThemeMode.SYSTEM,
            dailyGoalMinutes = prefs[Keys.DAILY_GOAL] ?: 15,
            soundEnabled = prefs[Keys.SOUND] ?: true,
            dailyReminderEnabled = prefs[Keys.DAILY_REMINDER] ?: false,
            // A stored value outside 0–23 is a corrupted record, not an hour: fall
            // back to the shipped default rather than hand `Calendar` a value it
            // would silently roll into another day.
            dailyReminderHour = prefs[Keys.REMINDER_HOUR]
                ?.takeIf { it in 0..23 }
                ?: AppSettings.DEFAULT_REMINDER_HOUR,
            contentVersion = prefs[Keys.CONTENT_VERSION] ?: 0,
            trialStartedAt = prefs[Keys.TRIAL_STARTED] ?: 0L,
            subscriptionUntil = prefs[Keys.SUBSCRIPTION_UNTIL] ?: 0L,
        )
    }

    override fun observeTrialAndSubscription(): Flow<org.token.english.core.billing.TrialAndSubscription> =
        context.dataStore.data.map { prefs ->
            org.token.english.core.billing.TrialAndSubscription(
                trialStartedAt = prefs[Keys.TRIAL_STARTED] ?: 0L,
                subscriptionUntil = prefs[Keys.SUBSCRIPTION_UNTIL] ?: 0L,
                trialConsumedMs = prefs[Keys.TRIAL_CONSUMED] ?: 0L,
                trialLastWallMs = prefs[Keys.TRIAL_LAST_WALL] ?: 0L,
                trialLastElapsedMs = prefs[Keys.TRIAL_LAST_ELAPSED] ?: 0L,
            )
        }

    override suspend fun setSubscriptionUntil(epochMillis: Long) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SUBSCRIPTION_UNTIL] = epochMillis
        }
    }

    override suspend fun completeFirstLaunch() {
        context.dataStore.edit { it[Keys.FIRST_LAUNCH] = true }
    }

    override suspend fun setLevel(level: LearningLevel) {
        context.dataStore.edit { it[Keys.LEVEL] = level.name }
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME] = mode.name }
    }

    override suspend fun setDailyGoalMinutes(minutes: Int) {
        context.dataStore.edit { it[Keys.DAILY_GOAL] = minutes }
    }

    override suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SOUND] = enabled }
    }

    override suspend fun setDailyReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DAILY_REMINDER] = enabled }
    }

    override suspend fun setDailyReminderHour(hour: Int) {
        context.dataStore.edit { it[Keys.REMINDER_HOUR] = hour.coerceIn(0, 23) }
    }

    override suspend fun setContentVersion(version: Int) {
        context.dataStore.edit { it[Keys.CONTENT_VERSION] = version }
    }
}

/**
 * Finished conversations (Room v10).
 *
 * These used to live in the settings preferences file. That file is excluded from
 * Auto Backup because it also carries the subscription mirror, so the mark — which
 * is learning data, not settings — vanished on a restore while the completed lesson
 * it unlocks survived: the gate then demanded a dialogue already spoken, and the
 * next lesson stayed shut. Progress belongs in the progress table, and the mark is
 * written inside the final turn's own transaction (see
 * [org.token.english.domain.usecase.RecordConversationTurnUseCase]).
 *
 * The legacy preference key is read **once** and then removed: an install that
 * upgrades keeps every dialogue it has already finished instead of being sent back
 * through them, and the settings file stops being a second, silent source of truth.
 */
class ConversationRepositoryImpl(
    private val dao: ConversationDao,
    private val context: Context,
) : ConversationRepository {

    /** Single-flight, so two collectors of the stream cannot both import. */
    private val importLock = Mutex()

    @Volatile
    private var legacyImported = false

    override fun observeCompleted(): Flow<Set<String>> = flow {
        importLegacyOnce()
        emitAll(dao.observeDone().map { it.toSet() })
    }

    override suspend fun markCompleted(lessonId: String) {
        if (lessonId.isBlank()) return
        dao.insert(ConversationDoneEntity(lessonId))
    }

    /**
     * Room's own delete: an empty table is "no dialogue finished", nothing left to
     * re-read.
     *
     * The legacy key goes with the rows. A wipe that left it behind would be undone by
     * the next read of the stream, which imports whatever the old preference set still
     * holds — progress the learner had just asked to delete.
     */
    override suspend fun reset() {
        dao.clear()
        runCatchingCancellable {
            context.dataStore.edit { it.remove(CONVERSATIONS_DONE) }
        }
        legacyImported = true
    }

    /**
     * Imports the old preference set into Room the first time the stream is read.
     *
     * Idempotent by construction: the rows are `INSERT OR IGNORE`, so a crash after
     * the write but before the key is removed simply repeats it next launch. A failed
     * import leaves the flag unset (it retries) and never breaks the caller: the
     * stream still emits what Room holds, which is the authority from now on.
     */
    private suspend fun importLegacyOnce() {
        if (legacyImported) return
        importLock.withLock {
            if (legacyImported) return
            val done = runCatchingCancellable {
                val raw = context.dataStore.data.first()[CONVERSATIONS_DONE]
                val ids = parseDoneLessonIds(raw)
                if (ids.isEmpty()) {
                    true
                } else {
                    dao.insertAll(ids.map(::ConversationDoneEntity))
                    // Written first, then cleared: the mark now lives in the table that
                    // backup restores with the rest of the progress.
                    context.dataStore.edit { it.remove(CONVERSATIONS_DONE) }
                    true
                }
            }
            if (done.isSuccess) {
                legacyImported = true
            } else {
                Log.w("ConversationRepository", "legacy dialogue import failed", done.exceptionOrNull())
            }
        }
    }
}

/** One lesson id per line; blank lines are skipped so a stray write never counts. */
private fun parseDoneLessonIds(raw: String?): Set<String> =
    raw.orEmpty().split('\n').map { it.trim() }.filter { it.isNotEmpty() }.toSet()

// ----------------------------------------------------------------- Mappers

private fun LessonEntity.toDomain() = LearningLevel.valueOf(level).let {
    Lesson(id, it, title, titleFa, topic, estimatedMinutes, orderIndex, grammarTipFa)
}

private fun LessonStateEntity.toDomain() = LessonState(
    lessonId = lessonId,
    currentIndex = currentIndex,
    completed = completed,
    completedAt = completedAt,
    stageId = stage.takeIf { it.isNotBlank() },
    completedStages = completedStages.split(',').filter { it.isNotBlank() }.toSet(),
    skippedStages = skippedStages.split(',').filter { it.isNotBlank() }.toSet(),
    // A pair written by a newer build with an id this one does not know is dropped,
    // not guessed: the step list is the authority on which ids exist.
    stageCompletedAt = stageCompletedAt.split(',')
        .mapNotNull { pair ->
            val parts = pair.split('=')
            val at = parts.getOrNull(1)?.toLongOrNull() ?: return@mapNotNull null
            parts.first().takeIf { it.isNotBlank() }?.let { it to at }
        }
        .toMap(),
)

private fun VocabularyEntity.toDomain() = VocabularyItem(
    id = id,
    word = word,
    translation = translation,
    definition = definition,
    pronunciation = pronunciation,
    level = runCatching { LearningLevel.valueOf(level) }.getOrDefault(LearningLevel.A1),
    partOfSpeech = partOfSpeech,
    examples = jsonExamples(examplesJson),
    collocations = jsonArrayToList(collocationsJson),
    lessonId = lessonId,
    explanationFa = explanationFa,
)

private fun ReviewItemEntity.toDomain() = ReviewItem(
    contentId = contentId,
    contentType = runCatching { ReviewContentType.valueOf(contentType) }.getOrDefault(ReviewContentType.VOCABULARY),
    state = runCatching { ReviewState.valueOf(state) }.getOrDefault(ReviewState.NEW),
    dueAt = dueAt,
    intervalDays = intervalDays,
    easeFactor = easeFactor,
    repetitions = repetitions,
    lapses = lapses,
    lastReviewedAt = lastReviewedAt,
    introducedAt = introducedAt,
)

private fun ReviewItem.toEntity() = ReviewItemEntity(
    contentId = contentId,
    contentType = contentType.name,
    state = state.name,
    dueAt = dueAt,
    intervalDays = intervalDays,
    easeFactor = easeFactor,
    repetitions = repetitions,
    lapses = lapses,
    lastReviewedAt = lastReviewedAt,
    introducedAt = introducedAt,
)

private fun ReviewAttemptEntity.toDomain() = ReviewAttempt(
    id = id,
    contentId = contentId,
    timestamp = timestamp,
    result = runCatching { ReviewResult.valueOf(result) }.getOrDefault(ReviewResult.AGAIN),
    responseTimeMs = responseTimeMs,
    source = source,
)

private fun ReviewAttempt.toEntity() = ReviewAttemptEntity(
    contentId = contentId,
    timestamp = timestamp,
    result = result.name,
    responseTimeMs = responseTimeMs,
    source = source,
)

private fun jsonArrayToList(json: String): List<String> = runCatching {
    val arr = org.json.JSONArray(json)
    (0 until arr.length()).map { arr.getString(it) }
}.getOrDefault(emptyList())

/**
 * Reads the stored bilingual examples (A-10). The stored array is objects
 * (`{en, fa}`) as written by [org.token.english.data.content.ContentSeeder], but a
 * database still holding the legacy `["sentence"]` array must keep loading — the
 * version-gated re-seed replaces it on the next launch, and until then the
 * sentence is better shown than dropped.
 */
private fun jsonExamples(json: String): List<VocabularyExample> = runCatching {
    val arr = org.json.JSONArray(json)
    (0 until arr.length()).mapNotNull { i ->
        when (val entry = arr.opt(i)) {
            is org.json.JSONObject -> entry.optString("en").takeIf { it.isNotBlank() }
                ?.let { VocabularyExample(it, entry.optString("fa")) }
            is String -> entry.takeIf { it.isNotBlank() }?.let { VocabularyExample(it, "") }
            else -> null
        }
    }
}.getOrDefault(emptyList())
