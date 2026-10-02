package org.token.english.domain.model

/** CEFR levels (methodology spec §2). The offline bundle ships all six (A1–C2, 4 lessons each). */
enum class LearningLevel { A1, A2, B1, B2, C1, C2 }

/**
 * Skill dimensions tracked per learner (methodology spec §19).
 * PRONUNCIATION and FLUENCY were removed: they need speech evaluation, which is
 * suspended while offline (AGENTS.md), and advertising a skill the app cannot
 * train would make mastery/recommendations dishonest. SPEAKING stays only as
 * the suspended exercise placeholder.
 */
enum class Skill {
    VOCABULARY,
    GRAMMAR,
    LISTENING,
    SPEAKING,
    READING,
    WRITING,
}

enum class ThemeMode { SYSTEM, LIGHT, DARK }

data class Lesson(
    val id: String,
    val level: LearningLevel,
    val title: String,
    val titleFa: String,
    val topic: String,
    val estimatedMinutes: Int,
    val order: Int,
    /** Short grammar hint shown in the intro stage and after grammar misses. */
    val grammarTipFa: String? = null,
)

data class LessonState(
    val lessonId: String,
    val currentIndex: Int,
    val completed: Boolean,
    val completedAt: Long?,
)

data class VocabularyItem(
    val id: String,
    val word: String,
    val translation: String,
    val definition: String?,
    val pronunciation: String?,
    val level: LearningLevel,
    val partOfSpeech: String?,
    val examples: List<String>,
    val collocations: List<String>,
    val lessonId: String?,
)

sealed interface Exercise {
    val id: String
    val lessonId: String

    /** Authored skill tag from the content JSON (null → AnswerChecker heuristic). */
    val skill: Skill?

    /** Optional short explanation shown after a wrong answer (content JSON). */
    val explanation: String?

    data class MultipleChoice(
        override val id: String,
        override val lessonId: String,
        val question: String,
        val questionFa: String?,
        val options: List<String>,
        val correctIndex: Int,
        override val skill: Skill? = null,
        override val explanation: String? = null,
    ) : Exercise

    data class FillBlank(
        override val id: String,
        override val lessonId: String,
        val sentence: String,
        val accepted: List<String>,
        override val skill: Skill? = null,
        override val explanation: String? = null,
    ) : Exercise

    data class Translation(
        override val id: String,
        override val lessonId: String,
        val prompt: String,
        val accepted: List<String>,
        /** Word-bank candidates for lighter A1 production; empty = free typing. */
        val bank: List<String> = emptyList(),
        override val skill: Skill? = null,
        override val explanation: String? = null,
    ) : Exercise

    data class Listening(
        override val id: String,
        override val lessonId: String,
        val audioText: String,
        val accepted: List<String>,
        override val skill: Skill? = null,
        override val explanation: String? = null,
    ) : Exercise

    /** Requires speech evaluation (cloud/on-device recognizer) — suspended while offline. */
    data class Speaking(
        override val id: String,
        override val lessonId: String,
        val prompt: String,
        val referenceAnswers: List<String>,
        override val skill: Skill? = null,
        override val explanation: String? = null,
    ) : Exercise
}

/** SRS lifecycle (technical spec §16). */
enum class ReviewState { NEW, LEARNING, REVIEW, RELEARNING, MASTERED }

/** Review grading buttons (technical spec §18). */
enum class ReviewResult { AGAIN, HARD, GOOD, EASY }

enum class ReviewContentType { VOCABULARY, GRAMMAR }

data class ReviewItem(
    val contentId: String,
    val contentType: ReviewContentType,
    val state: ReviewState,
    val dueAt: Long,
    val intervalDays: Int,
    val easeFactor: Float,
    val repetitions: Int,
    val lapses: Int,
    val lastReviewedAt: Long?,
    /** When the card entered the queue (0 = legacy row); feeds the daily new-card cap. */
    val introducedAt: Long = 0L,
)

data class ReviewSchedule(
    val nextReviewAt: Long,
    val intervalDays: Int,
    val state: ReviewState,
)

data class ReviewAttempt(
    val id: Long = 0,
    val contentId: String,
    val timestamp: Long,
    val result: ReviewResult,
    val responseTimeMs: Long,
    val source: String,
)

data class SkillMastery(
    val skill: Skill,
    val mastery: Float,
)

/** Aggregate learning statistics for the Progress screen. */
data class StudyStats(
    val todayStudySeconds: Long,
    /** Consecutive days with at least a meaningful study session (60s+). */
    val streakDays: Int,
    val lessonsCompleted: Int,
    val wordsLearned: Int,
    val reviewsToday: Int,
    /** Share of GOOD/EASY grades over all review grades — HARD and AGAIN count as not-yet-known. */
    val reviewAccuracy: Float,
)

/** What the learner should do today (technical spec §23). */
data class TodayPlan(
    val targetMinutes: Int,
    val todayStudySeconds: Long,
    val dueReviewCount: Int,
    val nextLesson: Lesson?,
    val recommendedSkills: List<Skill>,
)

data class AppSettings(
    val isFirstLaunch: Boolean,
    val level: LearningLevel,
    val themeMode: ThemeMode,
    val dailyGoalMinutes: Int,
    val soundEnabled: Boolean,
    val contentVersion: Int,
    /** Trial start (epoch millis), 0 = not started — see core/billing/TrialClock. */
    val trialStartedAt: Long,
    /** Subscription validity end (epoch millis), 0 = none. */
    val subscriptionUntil: Long,
    /** Daily 19:00 local review reminder (checklist P9); off by default. */
    val dailyReminderEnabled: Boolean = false,
)
