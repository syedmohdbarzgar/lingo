package org.token.english.domain.engine

import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.ExerciseFormat

/**
 * Which exercises the review session asks for a due curriculum node.
 *
 * The node's own schedule says *when* to practise it; this says *with what*, and how
 * much of it one session may spend. Four rules, each a decision rather than a
 * preference:
 *
 * - **Only a format a card can ask.** A review card is answered by tapping, typing or
 *   hearing — the review screen plays a listening item's audio through the same TTS
 *   seam the lesson uses (listening-card pass), so `listening` is askable. A `speaking`
 *   item needs the microphone and a take, so it stays the lesson's and the drill's; a
 *   card that could not be answered would be a dead end on the review screen, which
 *   never asks for the microphone.
 * - **One question per shape the node has not been confirmed in.** Securing a node
 *   needs two formats ([KnowledgeGates.MIN_FORMATS_TO_SECURE]), so a question in a
 *   shape already in the mask proves nothing new: a node asked its authored first item
 *   every session could be reviewed for a year and never add the second shape. This is
 *   the whole reason a card carries an exercise.
 * - **At most [MAX_CARDS_PER_NODE], and a node with nothing missing still gets one.**
 *   Two is what the gate asks for; everything past the second question is the drill's
 *   job, and a review session must not turn into a lesson. A node whose mask is already
 *   complete has no shape to add, so it is asked its authored first — the session
 *   should still touch the node it says is due.
 * - **Authored order inside each rule.** No question is invented, dropped among equals
 *   or reordered by a heuristic, so the same session asks the same questions.
 *
 * Empty when the node has no askable exercise at all — a real answer, not a failure:
 * that node is still served by the focused drill, and a card surface that guessed at one
 * would be asking a question the bundle does not have.
 */
object NodeCardPicker {

    /**
     * How many questions one due node may cost a review session. Two, because
     * [KnowledgeGates.MIN_FORMATS_TO_SECURE] is two: the block's job is to prove the
     * shapes the node is missing, and building mastery is the drill's.
     */
    const val MAX_CARDS_PER_NODE = 2

    /**
     * Formats a review card can ask without a microphone. A listening item is asked by
     * playing it; a take is the one thing the card cannot do.
     */
    val ASKABLE_FORMATS: Set<ExerciseFormat> = setOf(
        ExerciseFormat.MULTIPLE_CHOICE,
        ExerciseFormat.FILL_BLANK,
        ExerciseFormat.WORD_RECALL,
        ExerciseFormat.TRANSLATION,
        ExerciseFormat.SENTENCE_PRODUCTION,
        ExerciseFormat.WRITING_TEXT,
        ExerciseFormat.LISTENING,
    )

    /**
     * The questions a due node's block asks, in the order they are asked.
     *
     * @param confirmedFormats the node's `distinctTypesCorrect` mask — which formats it
     *   has already been proven in.
     */
    fun pickAll(candidates: List<Exercise>, confirmedFormats: Int): List<Exercise> {
        val askable = candidates.filter { AnswerChecker.formatOf(it) in ASKABLE_FORMATS }
        if (askable.isEmpty()) return emptyList()
        // One exercise per missing shape, keeping the authored order; two items of the
        // same shape are one shape, so the first is the one asked.
        val missingShapes = askable
            .filter { AnswerChecker.formatOf(it).bit and confirmedFormats == 0 }
            .distinctBy { AnswerChecker.formatOf(it) }
        if (missingShapes.isEmpty()) return listOf(askable.first())
        return missingShapes.take(MAX_CARDS_PER_NODE)
    }
}
