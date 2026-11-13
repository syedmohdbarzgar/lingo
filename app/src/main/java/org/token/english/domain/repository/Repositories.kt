package org.token.english.domain.repository

import kotlinx.coroutines.flow.Flow
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

interface LessonRepository {
    fun observeLessons(): Flow<List<Lesson>>
    fun observeLessonStates(): Flow<List<LessonState>>
    suspend fun getLesson(lessonId: String): Lesson?
    suspend fun getExercises(lessonId: String): List<Exercise>
    suspend fun getVocabulary(lessonId: String): List<VocabularyItem>
    suspend fun saveLessonIndex(lessonId: String, index: Int)

    /**
     * Persists where the guided lesson stands: the active step and the steps
     * already completed or skipped. Called on every step change, so an
     * interrupted lesson resumes on the same step (checklist Phase 4).
     */
    suspend fun saveLessonStage(
        lessonId: String,
        stageId: String,
        completedStages: Set<String>,
        skippedStages: Set<String>,
        /** Completion time per completed stage id (Phase 2); absent = never stamped. */
        completedAt: Map<String, Long> = emptyMap(),
    )

    /**
     * Appends one answered question to the lesson's history (Phase 2). Append-only by
     * design: a retry is a new row, so the record cannot quietly rewrite what happened.
     */
    suspend fun recordAttempt(attempt: LessonAttempt)

    /** The recorded answers of one lesson, oldest first — what a reopened step resumes from. */
    suspend fun attempts(lessonId: String): List<LessonAttempt>

    /**
     * The newest answers across every lesson, capped at [limit] — the miss taxonomy's
     * window. Read only for that classification; the per-lesson history above stays the
     * read a session needs.
     */
    suspend fun recentAttempts(limit: Int): List<LessonAttempt>

    suspend fun getLessonState(lessonId: String): LessonState?
    suspend fun completeLesson(lessonId: String, at: Long)
    /** Ensures every vocabulary item of the lesson exists in the SRS queue. */
    suspend fun enqueueLessonVocabulary(lessonId: String, now: Long)

    /** Lessons that ship an authored dialogue (a `speaking` item in the bundle). */
    suspend fun lessonIdsWithDialogue(): Set<String>
}

/**
 * The learner's finished conversations, one flag per lesson.
 *
 * Conversation is the last step of a lesson: the exercises prove the learner
 * recognises the structure, the dialogue proves they can *use* it in a reply. The
 * lesson path reads this set to keep the next lesson shut until the current
 * lesson's conversation has been practised end to end.
 */
interface ConversationRepository {
    /** Lesson ids whose dialogue has been completed. */
    fun observeCompleted(): Flow<Set<String>>

    /** Marks the lesson's dialogue as practised. Idempotent. */
    suspend fun markCompleted(lessonId: String)

    /**
     * Forgets every finished dialogue — the reset path's half of «پاک‌سازی پیشرفت».
     *
     * This set is learning data like any other (Room v10, table `conversation_done`),
     * so a wipe has to clear it with the lessons it unlocks: a reset that left it
     * behind kept every dialogue open while the learner model, the review queue and
     * the lesson states were gone, and the app disagreed with its own reset.
     */
    suspend fun reset()
}

interface VocabularyRepository {
    fun observeAll(): Flow<List<VocabularyItem>>
    suspend fun search(query: String): List<VocabularyItem>
    suspend fun get(id: String): VocabularyItem?
    suspend fun count(): Int
}

interface ReviewRepository {
    fun observeDueCount(now: Long): Flow<Int>
    suspend fun getDue(now: Long, limit: Int): List<ReviewItem>

    /** COUNT(*) of due cards — cheap; use instead of getDue(Int.MAX_VALUE).size. */
    suspend fun countDue(now: Long): Int
    suspend fun getItem(contentId: String): ReviewItem?
    suspend fun schedule(item: ReviewItem)
    suspend fun recordAttempt(attempt: ReviewAttempt)
    suspend fun getAttempts(contentId: String): List<ReviewAttempt>

    /**
     * Review-log totals per reviewed content type (checklist v8 P2). The SRS log is
     * the practice that never touches the curriculum graph, so a skill track has to
     * ask for it separately — otherwise a learner whose work is mostly review shows
     * a well-built vocabulary number with no evidence behind it.
     */
    suspend fun attemptStatsByType(): Map<ReviewContentType, AttemptStats>

    /**
     * The last [limit] grades, newest first, each with the kind of content it reviewed
     * (null when the item has left the queue). Review is where most of a mature
     * learner's practice happens, so a miss profile built only from lessons would
     * describe the first week and then stop changing.
     */
    suspend fun recentCards(limit: Int): List<GradedCard>

    suspend fun reset()
}

/**
 * Learner state per curriculum node (audit §5/§19/§20).
 *
 * The content half (which nodes a lesson teaches) is a graph read; the progress
 * half is written as answers arrive. Both live here because callers always want
 * them together: "which nodes did this lesson just give evidence about?".
 */
interface KnowledgeRepository {
    /** State of every practised node; a node missing from the list is unpractised. */
    fun observeStates(): Flow<List<KnowledgeState>>

    fun observePractisedCount(): Flow<Int>

    suspend fun getState(itemId: String): KnowledgeState?

    /** Curriculum nodes a lesson teaches (authored graph, not learner data). */
    suspend fun itemsForLesson(lessonId: String): List<KnowledgeItem>

    /** The whole curriculum graph, for coverage counts and per-node labels. */
    suspend fun allItems(): List<KnowledgeItem>

    /**
     * Records one graded answer against every knowledge item it is evidence
     * about. Never throws for an unknown lesson (e.g. the placement test) — there
     * is simply nothing to attribute the answer to.
     *
     * @param format the exercise shape the answer came through — the learner model
     *   records which formats confirmed a node (checklist 2.1), so it can tell
     *   "answered this shape five times" from "proven two ways".
     *
     * Whether the answer is *new* evidence is not decided here: the caller claims the
     * exercise for the day first ([ProgressRepository.claimAnswer]) and only writes
     * what it claimed. Splitting it that way keeps one rule for every caller, and the
     * rule lives where the exercise id is known.
     */
    suspend fun recordAttempt(
        lessonId: String,
        skill: Skill,
        format: ExerciseFormat,
        correct: Boolean,
        now: Long,
    )

    /**
     * Records one graded **review card** against the nodes it is evidence about.
     *
     * Narrower than [org.token.english.domain.engine.KnowledgeEvidence.itemsFor] on
     * purpose: a card is one word, so it may only move its lesson's node of its own
     * knowledge type and never the lesson's unrelated nodes — not even with a by-skill
     * pass, because 35 lessons once had words but no VOCABULARY node while their grammar
     * nodes list VOCABULARY in their `skills` — before this rule those cards credited
     * their grammar nodes, and v65 shipped the missing vocabulary nodes.
     *
     * Deliberately no [ExerciseFormat]: a card is recall of a word, not a *transfer*
     * claim, and the format mask is exactly that claim ([KnowledgeGates]
     * `MIN_FORMATS_TO_SECURE`). Letting a card set a bit would buy the second format
     * without ever answering in it — the gate would say "proven two ways" about one way.
     * Review therefore moves mastery and the node's own schedule, and leaves the
     * transfer evidence to the lesson.
     *
     * Where a lesson owns more than one node of the card's type, the card credits only
     * the node that names its word (bundle v82): the word is looked up from [cardId],
     * so a card from `c1.phrasal-idioms` moves `vocab.phrasal-verbs` or
     * `vocab.idioms-and-figurative` depending on what was actually asked, never both.
     *
     * @param lessonId the lesson that taught the reviewed content (a card from a lesson
     *   that no longer exists, or one opened before the bundle landed, has none — then
     *   there is nothing to attribute and nothing is written).
     * @param cardId the reviewed content id, resolved to its headword. Null when the
     *   caller has no card identity: the card then credits the lesson's nodes of its
     *   type, which is exact whenever there is only one.
     */
    suspend fun recordRecall(
        lessonId: String?,
        skill: Skill,
        correct: Boolean,
        now: Long,
        cardId: String? = null,
    )

    suspend fun reset()
}

interface ProgressRepository {
    fun observeMastery(): Flow<Map<Skill, Float>>
    fun observeStats(): Flow<StudyStats>
    suspend fun applyAttempt(skill: Skill, correct: Boolean, source: String)

    /**
     * Claims today's evidence for one exercise, before anything is written from it.
     *
     * The learner model is a function of *questions answered*, not of taps: answering
     * the item that was just answered correctly — from a retry, or by re-running a
     * lesson the same day — used to push the EWMA and the streak again, so seven taps
     * of one item crossed the "strong" bar. The claim is stored per exercise and day
     * (Room v9), so it survives the process and cannot be re-earned by restarting.
     *
     * The one exception is a correction: a miss that is followed by a hit on the same
     * exercise and day counts, because fixing it is real evidence.
     *
     * @return true when this answer is the day's first for this question, or turns
     *   today's miss into a hit. `false` means nothing should be written from it.
     */
    suspend fun claimAnswer(lessonId: String, exerciseId: String, correct: Boolean, now: Long): Boolean

    /**
     * Runs [block] as **one database transaction**: either every write inside it
     * lands or none of it does (checklist 0.1).
     *
     * One graded answer is four rows — the day's claim, the skill meter, the
     * curriculum nodes and the answer's own history — and writing them as separate
     * statements meant a process death between two of them left the model
     * disagreeing with itself: evidence credited for a question the history has no
     * answer for, or an answer on record that never moved the model. Wrapping the
     * whole answer turns a crash into a no-op instead of a partial fact.
     *
     * A repeated answer inside the block is harmless: [claimAnswer] refuses it, so
     * nothing it guards is written twice.
     */
    suspend fun <T> inTransaction(block: suspend () -> T): T

    /**
     * Per-dimension mastery folded across sessions (checklist B-1, Room v6) —
     * what a new lesson session seeds its `MasteryProfile` from, so the exercise
     * selector can steer the FIRST exercise instead of waiting for an in-session
     * answer. Dimensions a learner has never answered are absent, never 0.
     */
    suspend fun getDimensionMastery(): Map<MasteryDimension, Float>

    /** Persists one dimension's folded value right after the answer that moved it. */
    suspend fun setDimensionMastery(dimension: MasteryDimension, mastery: Float)

    suspend fun addStudySeconds(seconds: Long)
    suspend fun reset()
}

interface SettingsRepository : org.token.english.core.billing.SubscriptionStore {
    val settings: Flow<AppSettings>
    suspend fun completeFirstLaunch()
    suspend fun setLevel(level: LearningLevel)
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setDailyGoalMinutes(minutes: Int)
    suspend fun setSoundEnabled(enabled: Boolean)
    /** Toggles the daily review reminder; the caller also (un)schedules the alarm. */
    suspend fun setDailyReminderEnabled(enabled: Boolean)
    /**
     * The hour (0–23 local) the reminder fires at (checklist B-4). The caller also
     * reschedules, so a changed hour must reach the alarm, not just the record.
     * Out-of-range values are clamped by the implementation.
     */
    suspend fun setDailyReminderHour(hour: Int)
    suspend fun setContentVersion(version: Int)
}
