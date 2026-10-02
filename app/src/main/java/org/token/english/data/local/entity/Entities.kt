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

/** Learner progress: cursor + completion per lesson. */
@Entity(tableName = "lesson_state")
data class LessonStateEntity(
    @PrimaryKey val lessonId: String,
    val currentIndex: Int,
    val completed: Boolean,
    val completedAt: Long?,
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

/** One row per study day (streaks + daily goal). */
@Entity(tableName = "study_session")
data class StudySessionEntity(
    @PrimaryKey val dateKey: Int,
    val seconds: Long,
)
