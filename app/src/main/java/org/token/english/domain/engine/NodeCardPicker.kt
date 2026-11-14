package org.token.english.domain.engine

import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.ExerciseFormat

/**
 * Which exercise the review session asks for a due curriculum node (graph-card pass).
 *
 * The node's own schedule says *when* to practise it; this says *with what*. Three rules,
 * and each one is a decision rather than a preference:
 *
 * - **Only a format a card can ask.** A review card is answered by tapping or typing.
 *   A `listening` item needs its audio played and a `speaking` item needs the
 *   microphone and a take, so both are the lesson's to ask (the focused drill still
 *   serves them); a card that could not be answered would be a dead end on the review
 *   screen, which never asks for the microphone.
 * - **The format the node has not been confirmed in, if there is one.** Securing a node
 *   needs two formats ([KnowledgeGates.MIN_FORMATS_TO_SECURE]), so of a node's
 *   exercises the one that adds a format is worth more than another pass over a shape
 *   already in the mask — this is what a card could never do before.
 * - **Otherwise the authored first.** Among equals the lesson's own order decides, so
 *   the pick is stable from one session to the next instead of jumping around, and no
 *   question is invented or reordered by a heuristic.
 *
 * Null when the node has no askable exercise at all — a real answer, not a failure:
 * that node is still served by the focused drill, and a card surface that guessed at
 * one would be asking a question the bundle does not have.
 */
object NodeCardPicker {

    /** Formats a review card can ask without a speaker or a microphone. */
    val ASKABLE_FORMATS: Set<ExerciseFormat> = setOf(
        ExerciseFormat.MULTIPLE_CHOICE,
        ExerciseFormat.FILL_BLANK,
        ExerciseFormat.WORD_RECALL,
        ExerciseFormat.TRANSLATION,
        ExerciseFormat.SENTENCE_PRODUCTION,
        ExerciseFormat.WRITING_TEXT,
    )

    /**
     * @param confirmedFormats the node's `distinctTypesCorrect` mask — which formats it
     *   has already been proven in.
     */
    fun pick(candidates: List<Exercise>, confirmedFormats: Int): Exercise? {
        val askable = candidates.filter { AnswerChecker.formatOf(it) in ASKABLE_FORMATS }
        if (askable.isEmpty()) return null
        return askable.firstOrNull { exercise ->
            AnswerChecker.formatOf(exercise).bit and confirmedFormats == 0
        } ?: askable.first()
    }
}
