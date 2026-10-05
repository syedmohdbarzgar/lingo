package org.token.english.domain.model

/** CEFR levels (methodology spec §2). The bundled curriculum ships all six (A1–C2). */
enum class LearningLevel { A1, A2, B1, B2, C1, C2 }

/**
 * Skill dimensions tracked per learner (methodology spec §19).
 * PRONUNCIATION and FLUENCY were removed: they need speech evaluation, which is
 * suspended until speech evaluation exists (AGENTS.md), and advertising a skill the app cannot
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

    /** Requires speech evaluation (on-device or cloud recognizer) — not implemented yet. */
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

/**
 * What kind of knowledge a graph node represents. Deliberately not the same axis
 * as [Skill]: "inversion" is GRAMMAR knowledge that trains the GRAMMAR and
 * WRITING skill meters, so type and skills are separate.
 */
enum class KnowledgeType { GRAMMAR, VOCABULARY, DISCOURSE, PHONOLOGY }

/**
 * One learnable unit — the thing mastery and spaced repetition should eventually
 * be about, instead of a raw exercise or an anonymous skill bucket (audit §4/§19).
 *
 * A knowledge item is taught by one or more lessons and assessed by the exercises
 * of those lessons, so a single item collects evidence from many exercise formats
 * (multiple choice, fill blank, translation, listening).
 *
 * `prerequisites` are ids of items to learn first — these are the curriculum graph
 * edges that [org.token.english.domain.engine.KnowledgeGraph] reasons over.
 */
data class KnowledgeItem(
    val id: String,
    val type: KnowledgeType,
    val title: String,
    val titleFa: String,
    val level: LearningLevel,
    val prerequisites: List<String>,
    val lessonIds: List<String>,
    val skills: List<Skill>,
)

/**
 * Per-knowledge-item learner state (audit §20). This is the learner model: how
 * well each curriculum node is known, how often it has been practised, and when
 * it should come back for review.
 *
 * `mastery` is the exponentially-weighted 0..1 estimate; `intervalDays`,
 * `easeFactor` and `nextReviewAt` are the SM-2 schedule, shared with the review
 * queue so "known" means the same thing everywhere.
 */
data class KnowledgeState(
    val itemId: String,
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

/**
 * The kind of study move a planner can ask for (audit §5).
 *
 * Ordered by urgency in [org.token.english.domain.engine.AdaptiveLearningPlanner]:
 * a due review beats fixing a prerequisite, which beats a brand-new unit.
 */
enum class LearningActionType {
    /** Spaced-repetition cards are due right now. */
    REVIEW,

    /** A prerequisite is unseen or too weak — study it before the dependent node. */
    REMEDIATE,

    /** A brand-new, unlocked curriculum node. */
    LEARN,

    /** A node the learner has met but not yet consolidated. */
    PRACTISE,
}

/**
 * One ordered "do this now" the engine can justify. The UI only renders it; it
 * never decides what it says (technical spec §19). `reasonFa` is authored by the
 * engine so every recommendation carries its own explanation.
 */
data class LearningAction(
    val type: LearningActionType,
    /** The curriculum node to act on, or `null` for an undirected review block. */
    val itemId: String?,
    val titleFa: String,
    val reasonFa: String,
    /** Lower runs first. See [LearningActionType]. */
    val priority: Int,
    /** Rough minutes this action needs, so the surface can budget the day (audit §1.1). */
    val estimatedMinutes: Int = 0,
    /** The mastery dimension this action targets, or null when not applicable. */
    val dimension: MasteryDimension? = null,
)

/**
 * The kinds of knowing a single mastery number hides (audit §2). A learner can
 * recognise a word (pick it from options) and still fail to produce it from
 * memory; collapsing both into one score is what lets the app serve recognition
 * exercises forever (audit §6).
 *
 * Ordered from least to most demanding, so comparisons and tie-breaks are
 * deterministic and a weaker dimension can be named meaningfully.
 */
enum class MasteryDimension {
    /** Choosing among options — the easiest evidence. */
    RECOGNITION,

    /** Producing a known form from a cue (fill the blank). */
    RECALL,

    /** Understanding meaning in real time (listening). */
    COMPREHENSION,

    /** Applying a rule to build a new utterance (translation). */
    APPLICATION,

    /** Free production without a scaffold (speaking). */
    PRODUCTION,

    /** Durability of the above over a delay — carried by the SRS interval, not one answer. */
    RETENTION,
}

/**
 * Mastery broken down by [MasteryDimension] (audit §2). A dimension the learner
 * has never been assessed on is simply absent — distinct from a practised-and-
 * failed 0f. Reads default absent to 0f so "not yet known" ranks as a weakness,
 * while [isEstablished] still distinguishes the two.
 */
data class MasteryProfile(
    val byDimension: Map<MasteryDimension, Float> = emptyMap(),
) {
    /** Practised mastery for [dimension], or 0f when never assessed. */
    fun masteryOf(dimension: MasteryDimension): Float = byDimension[dimension] ?: 0f

    /** True once at least one graded answer has moved this dimension. */
    fun isEstablished(dimension: MasteryDimension): Boolean = byDimension.containsKey(dimension)

    /** Mean of the practised dimensions; 0f when nothing has been assessed yet. */
    val overall: Float
        get() = if (byDimension.isEmpty()) 0f else byDimension.values.average().toFloat()

    /**
     * The least-known dimension among [candidates], deterministic on ties
     * (declaration order). `null` when the candidate set is empty.
     */
    fun weakestOf(candidates: Collection<MasteryDimension>): MasteryDimension? =
        candidates.minWithOrNull(compareBy({ masteryOf(it) }, { it.ordinal }))
}

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
