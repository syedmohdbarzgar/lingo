package org.token.english.domain.model

/** CEFR levels (methodology spec §2). The bundled curriculum ships all six (A1–C2). */
enum class LearningLevel { A1, A2, B1, B2, C1, C2 }

/**
 * Skill dimensions tracked per learner (methodology spec §19).
 * PRONUNCIATION and FLUENCY were removed: they need speech evaluation, which is
 * suspended until speech evaluation exists (AGENTS.md), and advertising a skill the app cannot
 * train would make mastery/recommendations dishonest. SPEAKING stays because it *is*
 * measured now: a spoken dialogue turn graded by the recogniser credits it
 * (`RecordConversationTurnUseCase`), and its knowledge lands on the lesson's
 * PHONOLOGY nodes.
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
    /**
     * Grammar teaching text. Plain legacy tip, or the multi-section A-8 format
     * (`## قاعده` / `## جدول` / `## مثال‌ها` / `## خطاهای رایج`) — the stored
     * string is unchanged, so no schema or migration is involved (A-8 option 1).
     */
    val grammarTipFa: String? = null,
) {
    /** Sectioned view of [grammarTipFa] for the intro teaching cards. */
    val grammarSections: List<GrammarSection>
        get() = parseGrammarTip(grammarTipFa)

    /** First section as plain text — the short hint shown after a grammar miss. */
    val grammarSummary: String?
        get() = grammarSections.firstOrNull()?.asText()?.takeIf { it.isNotBlank() }
}

data class LessonState(
    val lessonId: String,
    val currentIndex: Int,
    val completed: Boolean,
    val completedAt: Long?,
    /**
     * The guided step the learner stopped on, stored by its stable
     * [LearningStage.id] (not the enum name, so a rename cannot lose a place).
     */
    val stageId: String? = null,
    /** Ids of the steps actually completed — what the progress bar counts. */
    val completedStages: Set<String> = emptySet(),
    /** Ids of the optional steps the learner chose to skip. Never counted as done. */
    val skippedStages: Set<String> = emptySet(),
    /** When each completed step was finished, keyed by [LearningStage.id] (Phase 2). */
    val stageCompletedAt: Map<String, Long> = emptyMap(),
) {
    /** The saved step, resolved; null when nothing was saved or the id is unknown. */
    val stage: LearningStage? get() = stageId?.let(LearningStage::fromId)
}

/**
 * One bilingual example sentence (A-10). The English sentence and its Persian
 * translation are a **pair in one object**, not two parallel lists: aligned
 * arrays drift the moment one side is edited, and a drifted pair shows the wrong
 * translation under the right sentence.
 */
data class VocabularyExample(
    val en: String,
    val fa: String,
)

data class VocabularyItem(
    val id: String,
    val word: String,
    val translation: String,
    val definition: String?,
    val pronunciation: String?,
    val level: LearningLevel,
    val partOfSpeech: String?,
    val examples: List<VocabularyExample>,
    val collocations: List<String>,
    val lessonId: String?,
    /**
     * Authored Persian usage note (checklist A-1). Shown in the review session
     * after a miss — the SRS queue is vocabulary-only, so this is the word's
     * "why is this the answer" text.
     */
    val explanationFa: String? = null,
)

sealed interface Exercise {
    val id: String
    val lessonId: String

    /** Authored skill tag from the content JSON (null → AnswerChecker heuristic). */
    val skill: Skill?

    /** Optional short explanation shown after a wrong answer (content JSON). */
    val explanation: String?

    /**
     * How hard the item is, authored in the bundle: 1 = first encounter, 3 = the
     * hardest thing its lesson asks. The scale is **relative to the lesson's own
     * level**, never across levels — a C2 item at 1 is still C2 content, and an A1
     * item at 3 would contradict the lesson it sits in (the validator refuses that).
     *
     * It exists so selection can change *level* and not only *order*: the adaptive
     * selector serves the easy end while a dimension is still shaky and the hard end
     * once it is consolidated (see `AdaptiveExerciseSelector`).
     */
    val difficulty: Int

    companion object {
        /** Neither authored nor unavoidable: what an item is when nobody said. */
        const val DEFAULT_DIFFICULTY = 2

        /** The authored scale: 1 = first encounter … 3 = hardest in its lesson. */
        val DIFFICULTY_RANGE = 1..3
    }

    data class MultipleChoice(
        override val id: String,
        override val lessonId: String,
        val question: String,
        val questionFa: String?,
        /**
         * Optional English reading text shown above the question (checklist A-5).
         * Comprehension items carry a short passage here (3–5 sentences);
         * "choose the meaning" items carry the single sentence under test. Null
         * for ordinary one-line questions. Always English — the UI renders it LTR.
         */
        val passage: String? = null,
        val options: List<String>,
        val correctIndex: Int,
        override val skill: Skill? = null,
        override val explanation: String? = null,
        override val difficulty: Int = DEFAULT_DIFFICULTY,
    ) : Exercise

    data class FillBlank(
        override val id: String,
        override val lessonId: String,
        val sentence: String,
        val accepted: List<String>,
        override val skill: Skill? = null,
        override val explanation: String? = null,
        override val difficulty: Int = DEFAULT_DIFFICULTY,
    ) : Exercise

    /**
     * Recall one English word from its Persian meaning, with **no options to
     * recognize** (checklist P2). This is the cheapest exercise that demands
     * retrieval instead of recognition, which makes it the vocabulary-side
     * evidence for [MasteryDimension.RECALL] — multiple choice can only ever show
     * that the learner recognises a word they may not be able to produce.
     */
    data class WordRecall(
        override val id: String,
        override val lessonId: String,
        /** Persian cue: the meaning the learner must name in English. */
        val prompt: String,
        /** Accepted English forms, inflections welcome (`block`, `blocks`). */
        val accepted: List<String>,
        override val skill: Skill? = null,
        override val explanation: String? = null,
        override val difficulty: Int = DEFAULT_DIFFICULTY,
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
        override val difficulty: Int = DEFAULT_DIFFICULTY,
    ) : Exercise

    /**
     * Produce a whole English sentence from a Persian cue, typing it with no bank
     * to pick from (checklist P2). This is the written counterpart of
     * [Speaking] and therefore the second source of [MasteryDimension.PRODUCTION]
     * evidence — the one that works without a speech recognizer.
     *
     * Grading deliberately does not demand one memorised wording: an answer counts
     * when it matches an exemplar in [accepted] **or** uses every [mustUse] anchor
     * in order and still reaches [minWords] words, so a learner who composes their
     * own correct sentence is not failed for phrasing it differently.
     */
    data class SentenceProduction(
        override val id: String,
        override val lessonId: String,
        /** Persian cue: what to say. */
        val prompt: String,
        /**
         * Anchor words/structures the sentence must use, **in this order** — the
         * authored skeleton of the target form (e.g. `["didn't", "yesterday"]`).
         * Matched whole-word and contraction-expanded against the typed answer.
         */
        val mustUse: List<String>,
        /** Word floor the author sets, so a two-word fragment cannot pass. */
        val minWords: Int,
        /** Exemplar answers — also the sentence shown after a miss. */
        val accepted: List<String>,
        /** Optional Persian nudge shown under the cue (the structure to use). */
        val hintFa: String? = null,
        override val skill: Skill? = null,
        override val explanation: String? = null,
        override val difficulty: Int = DEFAULT_DIFFICULTY,
    ) : Exercise

    /**
     * Write a short connected text — two or three sentences — from a Persian
     * situation, using the lesson's own structures.
     *
     * This is deliberately **not** [SentenceProduction] with a bigger word floor.
     * A produced sentence is graded on anchors **in order**, because the order is
     * the structure under test (`didn't … yesterday`). A text is graded on anchors
     * *anywhere*, a word floor **and** a sentence floor, because what is being
     * asked for is connected writing: an answer that uses every anchor in one run-on
     * sentence has not written a text. Both stay offline and deterministic — no
     * model judges whether the prose is good, and the authored [accepted] text is the
     * reference shown after a miss.
     */
    data class Writing(
        override val id: String,
        override val lessonId: String,
        /** Persian cue: what to write about. */
        val prompt: String,
        /**
         * Structures or words the text must contain — **anywhere**, in any order,
         * because a connective that opens the second sentence is still used. Each
         * anchor is matched whole-word, contraction-expanded and dialect-folded.
         */
        val mustUse: List<String>,
        /** Word floor, so two fragments cannot pass as a text. */
        val minWords: Int,
        /** Sentence floor: how many finished sentences the answer must contain. */
        val minSentences: Int,
        /** One exemplar text — also what is shown after a miss. */
        val accepted: List<String>,
        /** Optional Persian nudge (the structure to reach for). */
        val hintFa: String? = null,
        override val skill: Skill? = null,
        override val explanation: String? = null,
        override val difficulty: Int = DEFAULT_DIFFICULTY,
    ) : Exercise

    data class Listening(
        override val id: String,
        override val lessonId: String,
        val audioText: String,
        val accepted: List<String>,
        override val skill: Skill? = null,
        override val explanation: String? = null,
        override val difficulty: Int = DEFAULT_DIFFICULTY,
    ) : Exercise

    /** Speaking practice, graded on-device by `ConversationScorer` (Vosk + the
     *  offline model). */
    data class Speaking(
        override val id: String,
        override val lessonId: String,
        val prompt: String,
        val referenceAnswers: List<String>,
        /**
         * The partner's turn, in English: the question the device asks before the
         * learner answers, spoken by TTS. Present on a dialogue item; absent on a
         * plain "say this sentence back" item, where the reference answer is what
         * the learner repeats.
         *
         * It lives in content, not in Kotlin, because what a conversation is about
         * is a lesson decision: the exchange has to practise the lesson's own
         * structure (book a table / ask directions / disagree politely).
         */
        val question: String? = null,
        /** Persian gloss of [question] — the learner must understand what was asked. */
        val questionFa: String? = null,
        /**
         * The situation this exchange happens in, in Persian ("در فرودگاه — باجهٔ
         * تحویل بار"). The practice screen opens with it so the learner knows who
         * they are talking to and why; it is authored per lesson, never invented
         * in the UI.
         */
        val sceneFa: String? = null,
        /**
         * The partner's role in Persian ("مأمور فرودگاه"). Every turn of a lesson's
         * dialogue carries it, so one turn can never be answered without knowing
         * whose question it is.
         */
        val partnerFa: String? = null,
        override val skill: Skill? = null,
        override val explanation: String? = null,
        override val difficulty: Int = DEFAULT_DIFFICULTY,
    ) : Exercise
}

/**
 * The exercise format an answer came *through* — the "distinct types" in the learner
 * model (checklist 2.1). A node's mastery can be strong after one format the learner
 * has learned to pattern-match; answering it in a second format is transfer, and that
 * is a different claim. Ordered (ordinal = bit) because [KnowledgeState] records the
 * formats seen as a bitmask, so the same format is never counted twice.
 */
enum class ExerciseFormat {
    MULTIPLE_CHOICE,
    FILL_BLANK,
    WORD_RECALL,
    TRANSLATION,
    SENTENCE_PRODUCTION,
    LISTENING,
    SPEAKING,

    /**
     * A connected text, which is a different ask from a produced sentence: the
     * format mask exists to say a node was confirmed "in more than one way", and
     * writing three sentences is another way than writing one.
     *
     * Appended, never inserted: [bit] is `1 shl ordinal` and the masks are persisted
     * in `knowledge_state`, so a new constant in the middle would silently re-label
     * every stored format.
     */
    WRITING_TEXT,
    ;

    /** This format's bit inside [KnowledgeState.distinctTypesCorrect]. */
    val bit: Int get() = 1 shl ordinal
}

/** SRS lifecycle (technical spec §16). */
enum class ReviewState { NEW, LEARNING, REVIEW, RELEARNING, MASTERED }

/** Review grading buttons (technical spec §18). */
enum class ReviewResult { AGAIN, HARD, GOOD, EASY }

/**
 * The grades that claim the card is known: GOOD and EASY. HARD and AGAIN are
 * "not-yet-known", which is how the skill tracks and the miss taxonomy both read the
 * log — one definition, so a grade cannot be a hit in one place and a miss in another.
 */
val ReviewResult.isSuccess: Boolean get() = this == ReviewResult.GOOD || this == ReviewResult.EASY

enum class ReviewContentType { VOCABULARY, GRAMMAR }

/**
 * One graded card with the kind of content it reviewed — the review log's half of the
 * miss taxonomy. [type] is null when the item it graded is no longer in the queue (a
 * word the bundle dropped): the grade happened, but there is nothing left to classify.
 */
data class GradedCard(
    val result: ReviewResult,
    val type: ReviewContentType?,
    val at: Long,
)

/**
 * The skill a graded review exercises. One definition, shared by the code that
 * *writes* the grade ([SubmitReviewUseCase]) and the code that *counts* it into a
 * skill track (checklist v8 P2) — two copies of this mapping would drift and make
 * the review log credit a different track than the one the mastery moved.
 */
fun ReviewContentType.skill(): Skill = when (this) {
    ReviewContentType.VOCABULARY -> Skill.VOCABULARY
    ReviewContentType.GRAMMAR -> Skill.GRAMMAR
}

/** Review-log totals for one content type: how many grades, how many were misses. */
data class AttemptStats(
    val attempts: Int,
    val misses: Int,
)

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
    /**
     * When the node was first answered **right** (checklist 2.1) — the timestamp
     * that makes "strong" a traceable claim rather than a bare number. `null`
     * until the first correct answer, and on rows that predate Room v7 until their
     * next correct answer (no invented dates, see [org.token.english.data.local.AppDatabase]).
     */
    val firstCorrectAt: Long? = null,
    /**
     * A **bitmask** of the [ExerciseFormat]s that have produced a correct answer
     * (checklist 2.1): `format.bit` is OR-ed in on a hit, so answering the same
     * format five times still counts once. A mask rather than a counter because the
     * question is *which* formats confirmed the node, and a count cannot answer it.
     * Read the number of formats with [distinctTypeCount].
     */
    val distinctTypesCorrect: Int = 0,
) {
    /** How many distinct formats have confirmed this node so far. */
    val distinctTypeCount: Int get() = distinctTypesCorrect.countOneBits()
}

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
    /**
     * The ordered, self-explaining actions the adaptive planner decided on
     * (checklist B-1). The UI renders them; it never decides their order or why
     * they were chosen (technical spec §19). Empty until the curriculum graph is
     * seeded, so every surface still works from the fields above.
     */
    val actions: List<LearningAction> = emptyList(),
)

/**
 * The kind of study move a planner can ask for (audit §5).
 *
 * Ordered by urgency in [org.token.english.domain.engine.AdaptiveLearningPlanner]:
 * a due review beats fixing a prerequisite, which beats a retest, which beats a
 * brand-new unit.
 */
enum class LearningActionType {
    /** Spaced-repetition cards are due right now. */
    REVIEW,

    /** A prerequisite is unseen or too weak — study it before the dependent node. */
    REMEDIATE,

    /**
     * A node that is strong but not yet secured: it was last answered long enough ago
     * to be retested, and it has not been recalled after a delay in enough formats
     * (checklist 3, chapter 1). The move is a short focused session on that node, not
     * another full pass at its lesson.
     */
    RETEST,

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
    /**
     * The lesson a focused session on this node would run in, when the engine knows it.
     * Only [LearningActionType.RETEST] fills it: opening the action has to land on the
     * node's own exercises rather than on whatever lesson the day happens to point at.
     */
    val lessonId: String? = null,
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
    /** Legacy trial start (epoch millis); the trial was removed and nothing grants access from it. */
    val trialStartedAt: Long,
    /** Subscription validity end (epoch millis), 0 = none. */
    val subscriptionUntil: Long,
    /** Daily local review reminder (checklist P9); off by default. */
    val dailyReminderEnabled: Boolean = false,
    /**
     * Hour of the daily reminder, 0–23 local (checklist B-4). 19 is the shipped
     * default: an evening nudge, after the day's work rather than before it.
     */
    val dailyReminderHour: Int = 19,
) {
    companion object {
        /** The hour the reminder ships with, and the fallback for a bad stored value. */
        const val DEFAULT_REMINDER_HOUR = 19
    }
}
