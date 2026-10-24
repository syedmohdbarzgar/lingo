package org.token.english.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.token.english.data.local.entity.DimensionMasteryEntity
import org.token.english.data.local.entity.ExerciseEntity
import org.token.english.data.local.entity.KnowledgeItemEntity
import org.token.english.data.local.entity.KnowledgeStateEntity
import org.token.english.data.local.entity.LessonEntity
import org.token.english.data.local.entity.LessonStateEntity
import org.token.english.data.local.entity.ReviewAttemptEntity
import org.token.english.data.local.entity.ReviewItemEntity
import org.token.english.data.local.entity.SkillMasteryEntity
import org.token.english.data.local.entity.StudySessionEntity
import org.token.english.data.local.entity.VocabularyEntity

/** One-row aggregate behind [ReviewDao.observeAttemptAggregate]. */
data class AttemptAggregate(val total: Int, val correct: Int)

/**
 * Review totals per reviewed content type (P2 skill tracks). The skill a grade
 * exercised lives on the *item*, not the attempt row, so the count joins the two
 * rather than guessing which track a grade belongs to.
 */
data class AttemptsByContentType(val contentType: String, val total: Int, val correct: Int)

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

    // Knowledge graph
    @Query("SELECT * FROM knowledge_item ORDER BY id ASC")
    suspend fun getKnowledgeItems(): List<KnowledgeItemEntity>

    @Query("SELECT COUNT(*) FROM knowledge_item")
    suspend fun countKnowledgeItems(): Int

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
        knowledge: List<KnowledgeItemEntity>,
    ) {
        clearExercises()
        clearVocabulary()
        clearKnowledgeItems()
        clearLessons()
        insertLessons(lessons)
        insertVocabulary(vocabulary)
        insertExercises(exercises)
        insertKnowledgeItems(knowledge)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(items: List<LessonEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabulary(items: List<VocabularyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(items: List<ExerciseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKnowledgeItems(items: List<KnowledgeItemEntity>)

    @Query("DELETE FROM lesson")
    suspend fun clearLessons()

    @Query("DELETE FROM vocabulary")
    suspend fun clearVocabulary()

    @Query("DELETE FROM exercise")
    suspend fun clearExercises()

    @Query("DELETE FROM knowledge_item")
    suspend fun clearKnowledgeItems()

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

    /**
     * Grades per reviewed content type, using the same "correct = GOOD/EASY" rule as
     * [observeAttemptAggregate] so the track accuracy and the review accuracy cannot
     * disagree about what a correct answer is.
     */
    @Query(
        "SELECT i.contentType AS contentType, COUNT(*) AS total, " +
            "COALESCE(SUM(CASE WHEN a.result IN ('GOOD','EASY') THEN 1 ELSE 0 END), 0) AS correct " +
            "FROM review_attempt a INNER JOIN review_item i ON i.contentId = a.contentId " +
            "GROUP BY i.contentType",
    )
    suspend fun attemptStatsByContentType(): List<AttemptsByContentType>

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

    // Cross-session mastery per dimension (checklist B-1, Room v6): the rows a
    // new session seeds its MasteryProfile from.
    @Query("SELECT * FROM dimension_mastery")
    suspend fun getDimensions(): List<DimensionMasteryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDimension(item: DimensionMasteryEntity)

    @Query("DELETE FROM dimension_mastery")
    suspend fun clearDimensions()

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

@Dao
interface KnowledgeDao {
    @Query("SELECT * FROM knowledge_state")
    fun observeStates(): Flow<List<KnowledgeStateEntity>>

    @Query("SELECT * FROM knowledge_state WHERE itemId = :itemId")
    suspend fun getState(itemId: String): KnowledgeStateEntity?

    @Query("SELECT COUNT(*) FROM knowledge_state")
    suspend fun countPractised(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: KnowledgeStateEntity)

    /**
     * Atomic read-modify-write of one node's state — same pattern as skill
     * mastery: the value handed to [compute] is the row as it exists inside this
     * transaction, so two answers can never interleave between read and write.
     */
    @Transaction
    suspend fun updateAtomic(itemId: String, compute: (KnowledgeStateEntity?) -> KnowledgeStateEntity) {
        upsert(compute(getState(itemId)))
    }

    @Query("DELETE FROM knowledge_state")
    suspend fun clear()
}
