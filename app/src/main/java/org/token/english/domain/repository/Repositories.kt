package org.token.english.domain.repository

import kotlinx.coroutines.flow.Flow
import org.token.english.domain.model.AppSettings
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.LessonState
import org.token.english.domain.model.ReviewAttempt
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
    suspend fun getLessonState(lessonId: String): LessonState?
    suspend fun completeLesson(lessonId: String, at: Long)
    /** Ensures every vocabulary item of the lesson exists in the SRS queue. */
    suspend fun enqueueLessonVocabulary(lessonId: String, now: Long)
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
    suspend fun reset()
}

interface ProgressRepository {
    fun observeMastery(): Flow<Map<Skill, Float>>
    fun observeStats(): Flow<StudyStats>
    suspend fun applyAttempt(skill: Skill, correct: Boolean, source: String)
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
    suspend fun setContentVersion(version: Int)
}
