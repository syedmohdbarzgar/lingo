package org.token.english.domain.engine

import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.GradedCard
import org.token.english.domain.model.LessonAttempt
import org.token.english.domain.model.ReviewContentType
import org.token.english.domain.model.Skill
import org.token.english.domain.model.isSuccess

/**
 * What a miss is *about*, as far as the app can honestly tell (roadmap: the error
 * taxonomy).
 *
 * The learner model already says how much is known — per skill and per curriculum node.
 * This answers a different question: of the answers that went wrong, which kind of
 * knowing failed? A vocabulary gap is fixed by words; a grammar gap by structure; a
 * comprehension gap by reading and listening; a production gap by writing or saying
 * something, not by recognising it.
 *
 * The categories are derived from the exercise's own skill ([AnswerChecker.skillOf]), so
 * the label is authored evidence plus one mapping — never a guess about *why* the learner
 * chose an answer, which the app cannot observe.
 */
enum class MissCategory { VOCABULARY, GRAMMAR, COMPREHENSION, PRODUCTION }

/** Attempts and misses of one category, over the window the profile was built from. */
data class MissCategoryEvidence(
    val category: MissCategory,
    val attempts: Int,
    val misses: Int,
) {
    /** 0..1, or null when the window holds no attempt in this category. */
    val accuracy: Float? get() = if (attempts == 0) null else (attempts - misses) / attempts.toFloat()
}

/**
 * One answer the window can classify: what it was about, whether it was right, and
 * when. Lessons and review cards become the same shape here so the profile has one
 * fold and the two sources cannot disagree about what a miss is.
 */
data class ClassifiedAnswer(
    val category: MissCategory,
    val correct: Boolean,
    val at: Long,
)

/** The miss mix over one window of answers. */
data class MissProfile(
    /** Only categories the window actually holds an attempt for, in taxonomy order. */
    val categories: List<MissCategoryEvidence>,
) {
    val attempts: Int get() = categories.sumOf { it.attempts }
    val misses: Int get() = categories.sumOf { it.misses }

    /**
     * The category carrying the most misses — what the learner should work on first.
     *
     * Miss *count* rather than rate: a category answered twice with one miss is a 50 %
     * rate on no evidence, and naming it over a category with nine misses out of forty
     * would send the learner to the wrong place. A tie keeps the taxonomy order, so the
     * label cannot change between two identical reads.
     */
    val weakest: MissCategoryEvidence? get() = categories.maxByOrNull { it.misses }
}

/**
 * Classifies answered lesson questions into [MissCategory] and folds them into a
 * [MissProfile].
 *
 * Deliberately a window, not the whole log: the profile is "what your recent answers
 * say", so it should move as the learner improves.
 *
 * Two sources feed it. Lesson answers are classified by their exercise's skill, which
 * is authored evidence. Review cards are classified by the item's content type — the
 * only thing a card carries — so a card can say vocabulary or grammar but never
 * comprehension or production. That is a coarse label, not a wrong one: a recall card
 * genuinely tests a word or a rule, and the alternative (keeping review out) meant the
 * profile stopped moving once a learner's practice moved to review, which is exactly
 * what a mature learner's week looks like. The limit is recorded in `docs/debt.md`.
 */
object MissTaxonomy {

    /** Answers needed before the mix is worth showing: one miss proves nothing. */
    const val MIN_ATTEMPTS = 5

    /** How many recent answers the profile reads. A session's worth, not the archive. */
    const val WINDOW = 200

    /**
     * Which category an answered skill speaks for.
     *
     * `WRITING` and `SPEAKING` are [MissCategory.PRODUCTION] rather than grammar: the
     * exercises that carry them ask the learner to build or say a sentence, so a miss is
     * a failure to produce, not evidence that a rule is unknown.
     */
    fun categoryOf(skill: Skill): MissCategory = when (skill) {
        Skill.VOCABULARY -> MissCategory.VOCABULARY
        Skill.GRAMMAR -> MissCategory.GRAMMAR
        Skill.READING, Skill.LISTENING -> MissCategory.COMPREHENSION
        Skill.WRITING, Skill.SPEAKING -> MissCategory.PRODUCTION
    }

    /**
     * Which category a review card speaks for. A card carries its item's content type
     * and nothing finer, so these are the only two answers it can give.
     */
    fun categoryOf(type: ReviewContentType): MissCategory = when (type) {
        ReviewContentType.VOCABULARY -> MissCategory.VOCABULARY
        ReviewContentType.GRAMMAR -> MissCategory.GRAMMAR
    }

    /**
     * @param attempts the recent answer log, in any order (counts are order-free).
     * @param exercisesById the authored exercises, for the skill each answer was about.
     *   An id the bundle no longer ships is skipped: a classification of content that
     *   is gone would outlive the item it describes.
     */
    fun classify(
        attempts: List<LessonAttempt>,
        exercisesById: Map<String, Exercise>,
    ): List<ClassifiedAnswer> = attempts.mapNotNull { attempt ->
        exercisesById[attempt.exerciseId]?.let { exercise ->
            ClassifiedAnswer(
                category = categoryOf(AnswerChecker.skillOf(exercise)),
                correct = attempt.correct,
                at = attempt.at,
            )
        }
    }

    /**
     * A card graded GOOD or EASY is a hit; HARD and AGAIN are not — the same rule the
     * skill tracks read from the log. A card whose item left the queue is skipped: the
     * grade happened, but nothing is left to say what it trained.
     */
    fun classify(cards: List<GradedCard>): List<ClassifiedAnswer> = cards.mapNotNull { card ->
        card.type?.let { type ->
            ClassifiedAnswer(
                category = categoryOf(type),
                correct = card.result.isSuccess,
                at = card.at,
            )
        }
    }

    /**
     * Folds classified answers into the mix. `null` below [minAttempts]: one miss proves
     * nothing, and a bar that moves on two answers would teach the learner to distrust it.
     */
    fun profile(
        answers: List<ClassifiedAnswer>,
        minAttempts: Int = MIN_ATTEMPTS,
    ): MissProfile? {
        if (answers.size < minAttempts) return null
        val categories = MissCategory.entries.mapNotNull { category ->
            val answersOf = answers.filter { it.category == category }
            if (answersOf.isEmpty()) {
                null
            } else {
                MissCategoryEvidence(
                    category = category,
                    attempts = answersOf.size,
                    misses = answersOf.count { !it.correct },
                )
            }
        }
        return MissProfile(categories)
    }
}
