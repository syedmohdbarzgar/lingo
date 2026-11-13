package org.token.english.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** Bundled curriculum content (seeded from the assets content directory). */
@Entity(tableName = "lesson")
data class LessonEntity(
    @PrimaryKey val id: String,
    val level: String,
    val title: String,
    val titleFa: String,
    val topic: String,
    val estimatedMinutes: Int,
    val orderIndex: Int,
    val grammarTipFa: String?,
)

@Entity(tableName = "vocabulary")
data class VocabularyEntity(
    @PrimaryKey val id: String,
    val word: String,
    val translation: String,
    val definition: String?,
    val pronunciation: String?,
    val level: String,
    val partOfSpeech: String?,
    /** JSON arrays kept as text — small, authored content, no query needs. */
    val examplesJson: String,
    val collocationsJson: String,
    val lessonId: String?,
    /** Authored Persian usage note, shown after a missed review card (A-1). */
    val explanationFa: String? = null,
)

@Entity(tableName = "exercise")
data class ExerciseEntity(
    @PrimaryKey val id: String,
    val lessonId: String,
    val type: String,
    val orderIndex: Int,
    /** Full authored JSON payload; parsed into the domain Exercise by ContentParser. */
    val payloadJson: String,
)

/**
 * Curriculum knowledge graph node (seeded from assets/content/knowledge.json).
 * String lists are kept as JSON text — authored content with no query needs,
 * exactly like vocabulary examples and collocations.
 */
@Entity(tableName = "knowledge_item")
data class KnowledgeItemEntity(
    @PrimaryKey val id: String,
    val type: String,
    val title: String,
    val titleFa: String,
    val level: String,
    val prerequisitesJson: String,
    val lessonIdsJson: String,
    val skillsJson: String,
    /**
     * Headwords this node teaches (v11). `'[]'` for a node that teaches no word —
     * every grammar and discourse node — which is also the column's migration
     * default, so an upgrading install reads a node that claims nothing until the
     * re-seed writes the authored list.
     */
    val wordsJson: String,
)

/**
 * Learner state per knowledge item (audit §20). Unlike the content tables this is
 * progress, so it survives re-seeding and is cleared only by an explicit reset.
 */
@Entity(tableName = "knowledge_state")
data class KnowledgeStateEntity(
    @PrimaryKey val itemId: String,
    val mastery: Float,
    val exposureCount: Int,
    val consecutiveCorrect: Int,
    val consecutiveIncorrect: Int,
    val intervalDays: Int,
    val easeFactor: Float,
    val repetitions: Int,
    val lapses: Int,
    val lastReviewedAt: Long?,
    val nextReviewAt: Long,
    /** When the node was first answered right; NULL until then (checklist 2.1, Room v7). */
    val firstCorrectAt: Long? = null,
    /**
     * Bitmask of the exercise formats that produced a correct answer (Room v7). A
     * mask, not a counter: [org.token.english.domain.model.KnowledgeState]
     * reports the number of set bits, and the same format answered twice must not
     * count as two confirmations.
     */
    @ColumnInfo(defaultValue = "0") val distinctTypesCorrect: Int = 0,
)

/** Learner progress: cursor + completion per lesson, plus the guided step. */
@Entity(tableName = "lesson_state")
data class LessonStateEntity(
    @PrimaryKey val lessonId: String,
    val currentIndex: Int,
    val completed: Boolean,
    val completedAt: Long?,
    /**
     * The guided lesson's saved step (its stable id, e.g. `practice`) and the
     * steps already completed or skipped, comma-separated (Room v8). Empty for
     * a lesson that has not been opened since the redesign, which resumes at the
     * first step instead of inventing a position.
     */
    @ColumnInfo(defaultValue = "") val stage: String = "",
    @ColumnInfo(defaultValue = "") val completedStages: String = "",
    @ColumnInfo(defaultValue = "") val skippedStages: String = "",
    /**
     * When each step was completed, as `stageId=millis` pairs (Room v9, Phase 2).
     * Empty for a lesson finished before the column existed — no invented times.
     */
    @ColumnInfo(defaultValue = "") val stageCompletedAt: String = "",
)

/** Spaced-repetition state per content item (technical spec §15). */
@Entity(tableName = "review_item")
data class ReviewItemEntity(
    @PrimaryKey val contentId: String,
    val contentType: String,
    val state: String,
    val dueAt: Long,
    val intervalDays: Int,
    val easeFactor: Float,
    val repetitions: Int,
    val lapses: Int,
    val lastReviewedAt: Long?,
    /** When the card entered the queue — powers the daily new-card cap (P5). */
    @ColumnInfo(defaultValue = "0") val introducedAt: Long,
)

/** Event log of review grades — never overwritten (technical spec §45). */
@Entity(tableName = "review_attempt")
data class ReviewAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contentId: String,
    val timestamp: Long,
    val result: String,
    val responseTimeMs: Long,
    val source: String,
)

@Entity(tableName = "skill_mastery")
data class SkillMasteryEntity(
    @PrimaryKey val skill: String,
    val mastery: Float,
)

/**
 * Learner state per mastery **dimension** (checklist B-1, Room v6): the fold of
 * graded answers *across* sessions, so a returning learner's session seeds its
 * `MasteryProfile` from observed evidence instead of starting every kind of
 * knowing as unseen ("the selector only adapts once the learner has answered
 * inside that session" was the debt). One row per `MasteryDimension`; the
 * presence of a row is what makes a dimension established
 * (`MasteryProfile.isEstablished`). Learner state like `knowledge_state`:
 * survives re-seeding, cleared only by an explicit reset.
 */
@Entity(tableName = "dimension_mastery")
data class DimensionMasteryEntity(
    @PrimaryKey val dimension: String,
    val mastery: Float,
)

/**
 * The day's answer for one exercise (Phase 1, Room v9).
 *
 * Exists for one question the model could not answer before: *has this question
 * already moved the learner today?* Re-answering the item that was just answered —
 * from a retry button, or by re-running a lesson — used to push mastery and the streak
 * again, so the bar measured taps. One row per exercise is enough for that question;
 * the history of every attempt is [LessonAttemptEntity], and this row does not try to
 * be it.
 */
@Entity(tableName = "question_attempt")
data class QuestionAttemptEntity(
    @PrimaryKey val exerciseId: String,
    val lessonId: String,
    /** Compact day id, e.g. 20261001 — the same id `study_session` keys on. */
    val dayKey: Int,
    val correct: Boolean,
    val at: Long,
)

/**
 * One answered lesson question, append-only (Phase 2, Room v9).
 *
 * [QuestionAttemptEntity] above is the *claim* — "has this exercise already moved the
 * model today" — and it is overwritten in place. This row is the history: every answer
 * in every step, what the learner actually wrote or picked, and when. Two things read
 * it: a reopened step resumes on the first question it has no answer for
 * ([org.token.english.domain.model.StageResume]), and the session keeps the learner's
 * own answers across a process death instead of only the cursor.
 */
@Entity(tableName = "lesson_attempt")
data class LessonAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lessonId: String,
    val stageId: String,
    val exerciseId: String,
    val answer: String,
    val correct: Boolean,
    val at: Long,
)

/**
 * A lesson whose dialogue the learner has finished (Room v10).
 *
 * This used to live in the settings preferences file, and that was the bug: Auto Backup
 * and device-to-device transfer exclude `datastore/` — correctly, because the same file
 * carries the subscription mirror — so after a restore the lesson was still completed,
 * its answers were still in `lesson_attempt` and its spoken turns still claimed, while
 * the "this dialogue is done" mark was gone. The app then owed a dialogue the learner
 * had already practised and kept the next lesson shut. A progress fact belongs in the
 * table that holds the rest of the progress, not in the store that is excluded.
 *
 * One row per lesson and no timestamp: the mark's only question is "has this dialogue
 * been practised", and an imported row would have no honest date to carry.
 */
@Entity(tableName = "conversation_done")
data class ConversationDoneEntity(
    @PrimaryKey val lessonId: String,
)

/** One row per study day (streaks + daily goal). */
@Entity(tableName = "study_session")
data class StudySessionEntity(
    @PrimaryKey val dateKey: Int,
    val seconds: Long,
)
