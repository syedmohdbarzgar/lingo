package org.token.english.domain.engine

import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.LessonState

/**
 * Where one lesson stands in the learner model.
 *
 * The app used to have a single word for two different facts: a lesson was
 * "completed" the moment its required steps had been through once, and the Progress
 * screen showed that under «درس تمام‌شده» — which reads like "learned". Steps done
 * and language known are not the same claim, and a learner who tapped through a
 * dialogue has no evidence about the grammar it taught. So the two are kept apart
 * here, and every surface reads the same two-value answer instead of inventing its
 * own bar from a float.
 */
enum class LessonStanding {
    /** Opened and left, or never opened: nothing has been finished. */
    NOT_STARTED,

    /** Steps under way — some completed, the lesson itself not. */
    IN_PROGRESS,

    /** Every required step is done: the learner has *seen* the lesson. */
    COMPLETED,

    /**
     * Completed **and** no knowledge node the lesson teaches needs today's practice
     * ([KnowledgeGates.isSecured]).
     *
     * Deliberately the planner's bar, not the 30-day retention claim: a lesson whose
     * language survived a delayed retest in two shapes is done *for the learner now*,
     * and that is the distinction the screen can act on. The long-term claim stays
     * [KnowledgeGates.isLongTermMastered] and is never inferred from "completed".
     */
    MASTERED,
}

/**
 * Maps lesson state + the evidence in [KnowledgeState] onto [LessonStanding].
 *
 * Pure: lesson states, the curriculum graph and node states are all passed in, so the
 * screen can compute this from what it already observes and a JVM test can hold it to
 * one definition.
 */
object LessonMastery {

    fun standing(
        lesson: LessonState?,
        nodes: List<KnowledgeItem>,
        states: Map<String, KnowledgeState>,
    ): LessonStanding {
        val completed = lesson?.completed == true
        if (!completed) {
            val touched = lesson != null &&
                (lesson.stageId != null || lesson.completedStages.isNotEmpty() || lesson.skippedStages.isNotEmpty())
            return if (touched) LessonStanding.IN_PROGRESS else LessonStanding.NOT_STARTED
        }
        // A lesson the graph does not know can be finished but never mastered: there is
        // no authored language to be evidenced about.
        if (nodes.isEmpty()) return LessonStanding.COMPLETED
        val secured = nodes.all { node -> states[node.id]?.let(KnowledgeGates::isSecured) == true }
        return if (secured) LessonStanding.MASTERED else LessonStanding.COMPLETED
    }

    /**
     * How many completed lessons are also mastered.
     *
     * `completed` is passed as the set of lesson ids the lesson table already marks
     * done, so this never counts a lesson the learner has not finished — the number is
     * always a subset of what «درس تمام‌شده» shows.
     */
    fun masteredCount(
        completed: Set<String>,
        nodesByLesson: Map<String, List<KnowledgeItem>>,
        states: Map<String, KnowledgeState>,
    ): Int = completed.count { lessonId ->
        val nodes = nodesByLesson[lessonId].orEmpty()
        nodes.isNotEmpty() && nodes.all { node -> states[node.id]?.let(KnowledgeGates::isSecured) == true }
    }
}
