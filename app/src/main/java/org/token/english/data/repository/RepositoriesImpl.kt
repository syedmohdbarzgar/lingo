package org.token.english.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.token.english.core.common.TimeUtil
import org.token.english.data.content.ContentParser
import org.json.JSONArray
import org.token.english.data.local.dao.ContentDao
import org.token.english.data.local.dao.KnowledgeDao
import org.token.english.data.local.dao.ProgressDao
import org.token.english.data.local.dao.ReviewDao
import org.token.english.data.local.entity.ExerciseEntity
import org.token.english.data.local.entity.KnowledgeItemEntity
import org.token.english.data.local.entity.KnowledgeStateEntity
import org.token.english.data.local.entity.LessonEntity
import org.token.english.data.local.entity.LessonStateEntity
import org.token.english.data.local.entity.ReviewAttemptEntity
import org.token.english.data.local.entity.ReviewItemEntity
import org.token.english.data.local.entity.VocabularyEntity
import org.token.english.domain.engine.DefaultKnowledgeEngine
import org.token.english.domain.engine.KnowledgeEvidence
import org.token.english.domain.engine.MasteryEngine
import org.token.english.domain.model.AppSettings
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.KnowledgeType
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.LessonState
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

    override suspend fun recordAttempt(lessonId: String, skill: Skill, correct: Boolean, now: Long) {
        KnowledgeEvidence.itemsFor(itemsForLesson(lessonId), skill).forEach { item ->
            knowledgeDao.updateAtomic(item.id) { existing ->
                engine.applyAttempt(item.id, existing?.toDomain(), correct, now).toEntity()
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

    override suspend fun getLessonState(lessonId: String): LessonState? =
        contentDao.getLessonState(lessonId)?.toDomain()

    override suspend fun completeLesson(lessonId: String, at: Long) {
        contentDao.ensureLessonState(lessonId)
        contentDao.markLessonCompleted(lessonId, at)
    }

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
        progressDao.clearSessions()
        reviewDao.clearItems()
        reviewDao.clearAttempts()
        contentDao.clearLessonStates()
    }
}

// --------------------------------------------------------------- Settings

private val Context.dataStore by preferencesDataStore(name = "app_settings")

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

    override suspend fun ensureTrialStarted(now: Long, elapsedRealtime: Long) {
        context.dataStore.edit { prefs ->
            if (prefs[Keys.TRIAL_STARTED] == null) {
                prefs[Keys.TRIAL_STARTED] = now
                prefs[Keys.TRIAL_CONSUMED] = 0L
                prefs[Keys.TRIAL_LAST_WALL] = now
                prefs[Keys.TRIAL_LAST_ELAPSED] = elapsedRealtime
            } else {
                val state = org.token.english.core.billing.TrialClockState(
                    startedAt = prefs[Keys.TRIAL_STARTED] ?: 0L,
                    consumedMs = prefs[Keys.TRIAL_CONSUMED] ?: 0L,
                    lastWallMs = prefs[Keys.TRIAL_LAST_WALL] ?: 0L,
                    lastElapsedMs = prefs[Keys.TRIAL_LAST_ELAPSED] ?: 0L,
                )
                // Pre-checkpoint installs (or fresh data) get a baseline now, so
                // consumption starts being verified from this run onwards.
                val advanced = if (state.lastWallMs <= 0L) {
                    state.copy(lastWallMs = now, lastElapsedMs = elapsedRealtime)
                } else {
                    org.token.english.core.billing.TrialClock.advance(state, now, elapsedRealtime)
                }
                prefs[Keys.TRIAL_CONSUMED] = advanced.consumedMs
                prefs[Keys.TRIAL_LAST_WALL] = advanced.lastWallMs
                prefs[Keys.TRIAL_LAST_ELAPSED] = advanced.lastElapsedMs
            }
        }
    }

    override suspend fun saveTrialCheckpoint(consumedMs: Long, lastWallMs: Long, lastElapsedMs: Long) {
        context.dataStore.edit { prefs ->
            if (prefs[Keys.TRIAL_STARTED] != null) {
                prefs[Keys.TRIAL_CONSUMED] = consumedMs
                prefs[Keys.TRIAL_LAST_WALL] = lastWallMs
                prefs[Keys.TRIAL_LAST_ELAPSED] = lastElapsedMs
            }
        }
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

    override suspend fun setContentVersion(version: Int) {
        context.dataStore.edit { it[Keys.CONTENT_VERSION] = version }
    }
}

// ----------------------------------------------------------------- Mappers

private fun LessonEntity.toDomain() = LearningLevel.valueOf(level).let {
    Lesson(id, it, title, titleFa, topic, estimatedMinutes, orderIndex, grammarTipFa)
}

private fun LessonStateEntity.toDomain() = LessonState(lessonId, currentIndex, completed, completedAt)

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
