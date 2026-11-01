package org.token.english.domain.engine

/**
 * The one line between "known" and "strong", on the same 0f..1f scale as mastery.
 *
 * It lives here, next to the engine that produces mastery values, because three
 * places used to decide "strong" for themselves: the node gate in
 * [DefaultKnowledgeEngine], the daily planner and the exercise selector. Two of them
 * could drift apart without either being wrong on its own — a node marked mastered
 * by one and still "weak" by the other.
 */
const val STRONG_MASTERY = 0.85f

/**
 * Skill mastery model. Deliberately more than "last answer" (technical spec §21):
 * every attempt moves an exponentially-weighted average, so repeated recall and
 * delayed recall both matter, and one lucky guess cannot max out a skill.
 */
interface MasteryEngine {
    /** @param current current mastery 0f..1f, @param correct attempt outcome. */
    fun update(current: Float, correct: Boolean): Float
}

class DefaultMasteryEngine(
    private val learningRate: Float = 0.2f,
) : MasteryEngine {

    override fun update(current: Float, correct: Boolean): Float {
        val score = if (correct) 1f else 0f
        val updated = current * (1f - learningRate) + score * learningRate
        return updated.coerceIn(0f, 1f)
    }
}
