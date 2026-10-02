package org.token.english.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.token.english.data.local.entity.ExerciseEntity
import org.token.english.data.local.entity.LessonEntity
import org.token.english.data.local.entity.LessonStateEntity
import org.token.english.data.local.entity.ReviewAttemptEntity
import org.token.english.data.local.entity.ReviewItemEntity
import org.token.english.data.local.entity.SkillMasteryEntity
import org.token.english.data.local.entity.StudySessionEntity
import org.token.english.data.local.entity.VocabularyEntity

/** One-row aggregate behind [ReviewDao.observeAttemptAggregate]. */
data class AttemptAggregate(val total: Int, val correct: Int)

@Dao
interface ContentDao {
    // Lessons
    @Query("SELECT * FROM lesson ORDER BY orderIndex ASC")
    fun observeLessons(): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lesson WHERE id = :id")
    suspend fun getLesson(id: String): LessonEntity?

    @Query("SELECT * FROM lesson_state")
    fun observeLessonStates(): Flow<List<LessonStateEntity>>

    @Query("SELECT COUNT(*) FROM lesson_state WHERE completed = 1")
    fun observeCompletedLessonCount(): Flow<Int>

    @Query("INSERT OR IGNORE INTO lesson_state (lessonId, currentIndex, completed, completedAt) VALUES (:lessonId, 0, 0, NULL)")
    suspend fun ensureLessonState(lessonId: String)

    @Query("UPDATE lesson_state SET currentIndex = :index WHERE lessonId = :lessonId")
    suspend fun updateLessonIndex(lessonId: String, index: Int)

    @Query("UPDATE lesson_state SET completed = 1, completedAt = :at WHERE lessonId = :lessonId")
    suspend fun markLessonCompleted(lessonId: String, at: Long)

    @Query("SELECT * FROM lesson_state WHERE lessonId = :lessonId")
    suspend fun getLessonState(lessonId: String): LessonStateEntity?

    // Vocabulary
    @Query("SELECT * FROM vocabulary ORDER BY word ASC")
    fun observeVocabulary(): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabulary WHERE lessonId = :lessonId")
    suspend fun getVocabularyByLesson(lessonId: String): List<VocabularyEntity>

    @Query("SELECT * FROM vocabulary WHERE id = :id")
    suspend fun getVocabulary(id: String): VocabularyEntity?

    @Query("SELECT * FROM vocabulary WHERE word LIKE '%' || :query || '%' ESCAPE '\\' OR translation LIKE '%' || :query || '%' ESCAPE '\\'")
    suspend fun searchVocabulary(query: String): List<VocabularyEntity>

    @Query("SELECT COUNT(*) FROM vocabulary")
    suspend fun countVocabulary(): Int

    // Exercises
    @Query("SELECT * FROM exercise WHERE lessonId = :lessonId ORDER BY orderIndex ASC")
    suspend fun getExercises(lessonId: String): List<ExerciseEntity>

    // Content writes (seeding only)

    /**
     * Replaces all bundled content in ONE transaction — a crash mid-seed can
     * never leave the learner with a half-empty or mixed-version bundle.
     */
    @Transaction
    suspend fun replaceContent(
        lessons: List<LessonEntity>,
        vocabulary: List<VocabularyEntity>,
        exercises: List<ExerciseEntity>,
    ) {
        clearExercises()
        clearVocabulary()
        clearLessons()
        insertLessons(lessons)
        insertVocabulary(vocabulary)
        insertExercises(exercises)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(items: List<LessonEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabulary(items: List<VocabularyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(items: List<ExerciseEntity>)

    @Query("DELETE FROM lesson")
    suspend fun clearLessons()

    @Query("DELETE FROM vocabulary")
    suspend fun clearVocabulary()

    @Query("DELETE FROM exercise")
    suspend fun clearExercises()

    @Query("DELETE FROM lesson_state")
    suspend fun clearLessonStates()
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM review_item WHERE dueAt <= :now ORDER BY dueAt ASC")
    fun observeDue(now: Long): Flow<List<ReviewItemEntity>>

    @Query("SELECT COUNT(*) FROM review_item WHERE dueAt <= :now")
    fun observeDueCount(now: Long): Flow<Int>

    @Query("SELECT * FROM review_item WHERE dueAt <= :now ORDER BY dueAt ASC LIMIT :limit")
    suspend fun getDue(now: Long, limit: Int): List<ReviewItemEntity>

    @Query("SELECT COUNT(*) FROM review_item WHERE dueAt <= :now")
    suspend fun countDue(now: Long): Int

    @Query("SELECT COUNT(*) FROM review_item WHERE introducedAt >= :since")
    suspend fun countIntroducedSince(since: Long): Int

    @Query("SELECT * FROM review_item WHERE contentId = :contentId")
    suspend fun getItem(contentId: String): ReviewItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertItem(item: ReviewItemEntity)

    @Insert
    suspend fun insertAttempt(attempt: ReviewAttemptEntity)

    @Query("SELECT * FROM review_attempt WHERE contentId = :contentId ORDER BY timestamp ASC")
    suspend fun getAttempts(contentId: String): List<ReviewAttemptEntity>

    @Query("SELECT * FROM review_attempt")
    fun observeAttempts(): Flow<List<ReviewAttemptEntity>>

    /** Grades received today — `now` is evaluated by SQLite at query time. */
    @Query(
        "SELECT COUNT(*) FROM review_attempt WHERE timestamp >= " +
            "CAST(strftime('%s','now','localtime','start of day') AS INTEGER) * 1000",
    )
    fun observeAttemptCountToday(): Flow<Int>

    /**
     * Accuracy aggregate in one query: correct = GOOD/EASY (HARD and AGAIN are
     * deliberately NOT counted as correct — checklist P5; see StudyStats).
     */
    @Query(
        "SELECT COUNT(*) AS total, " +
            "COALESCE(SUM(CASE WHEN result IN ('GOOD','EASY') THEN 1 ELSE 0 END), 0) AS correct " +
            "FROM review_attempt",
    )
    fun observeAttemptAggregate(): Flow<AttemptAggregate>

    @Query("SELECT COUNT(*) FROM review_item WHERE lastReviewedAt IS NOT NULL")
    fun observeWordsLearnedCount(): Flow<Int>

    @Query("SELECT * FROM review_item")
    fun observeItems(): Flow<List<ReviewItemEntity>>

    @Query("DELETE FROM review_item")
    suspend fun clearItems()

    @Query("DELETE FROM review_attempt")
    suspend fun clearAttempts()
}

@Dao
interface ProgressDao {
    @Query("SELECT * FROM skill_mastery")
    fun observeMastery(): Flow<List<SkillMasteryEntity>>

    @Query("SELECT * FROM skill_mastery WHERE skill = :skill")
    suspend fun getMastery(skill: String): SkillMasteryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMastery(item: SkillMasteryEntity)

    /**
     * Atomic read-modify-write of one skill's mastery: the value handed to
     * [compute] is the row as it exists inside this transaction (`null` = no row
     * yet — the caller decides the seed), so two concurrent attempts can never
     * interleave between read and write.
     */
    @Transaction
    suspend fun updateMasteryAtomic(skill: String, compute: (Float?) -> Float) {
        val current = getMastery(skill)?.mastery
        upsertMastery(SkillMasteryEntity(skill, compute(current)))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSession(item: StudySessionEntity)

    @Query("SELECT * FROM study_session WHERE dateKey = :dateKey")
    suspend fun getSession(dateKey: Int): StudySessionEntity?

    @Query("SELECT * FROM study_session ORDER BY dateKey DESC")
    fun observeSessions(): Flow<List<StudySessionEntity>>

    @Query("SELECT dateKey FROM study_session ORDER BY dateKey DESC")
    fun observeSessionKeys(): Flow<List<Int>>

    /** Today's study seconds — day id computed by SQLite at query time. */
    @Query("SELECT seconds FROM study_session WHERE dateKey = CAST(strftime('%Y%m%d','now','localtime') AS INTEGER)")
    fun observeTodaySeconds(): Flow<Long?>

    @Query("DELETE FROM skill_mastery")
    suspend fun clearMastery()

    @Query("DELETE FROM study_session")
    suspend fun clearSessions()
}
