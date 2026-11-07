package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Test
import org.token.english.domain.engine.LessonMastery
import org.token.english.domain.engine.LessonStanding
import org.token.english.domain.model.ExerciseFormat
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.KnowledgeType
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.LessonState
import org.token.english.domain.model.Skill

/**
 * "Completed" and "mastered" are different claims (Phase 1), and the barrier between
 * them is evidence, not a flag.
 *
 * What is pinned here: a lesson that was only opened is not started, one left mid-step
 * is in progress, a finished lesson whose language is unproven is *completed only*, and
 * the mastered count can never exceed the completed count.
 */
class LessonMasteryTest {

    private val hour = 3_600_000L
    private val dayOne = 1_790_000_000_000L
    private val later = dayOne + 21 * hour

    private fun node(id: String, lessonId: String) = KnowledgeItem(
        id = id,
        type = KnowledgeType.GRAMMAR,
        title = "Node $id",
        titleFa = "گره $id",
        level = LearningLevel.A1,
        prerequisites = emptyList(),
        lessonIds = listOf(lessonId),
        skills = listOf(Skill.GRAMMAR),
    )

    private fun state(
        itemId: String,
        mastery: Float = 0.92f,
        consecutiveCorrect: Int = 3,
        formats: Int = ExerciseFormat.FILL_BLANK.bit or ExerciseFormat.TRANSLATION.bit,
        lastReviewedAt: Long? = later,
    ) = KnowledgeState(
        itemId = itemId,
        mastery = mastery,
        exposureCount = 3,
        consecutiveCorrect = consecutiveCorrect,
        consecutiveIncorrect = 0,
        intervalDays = 3,
        easeFactor = 2.5f,
        repetitions = 3,
        lapses = 0,
        lastReviewedAt = lastReviewedAt,
        nextReviewAt = later + hour,
        firstCorrectAt = dayOne,
        distinctTypesCorrect = formats,
    )

    private fun lesson(
        id: String = "a1.articles.lesson-01",
        completed: Boolean = true,
        stageId: String? = null,
        completedStages: Set<String> = emptySet(),
        skippedStages: Set<String> = emptySet(),
    ) = LessonState(
        lessonId = id,
        currentIndex = 0,
        completed = completed,
        completedAt = if (completed) later else null,
        stageId = stageId,
        completedStages = completedStages,
        skippedStages = skippedStages,
    )

    @Test
    fun `a lesson that was never opened is not started`() {
        assertEquals(
            LessonStanding.NOT_STARTED,
            LessonMastery.standing(lesson = null, nodes = listOf(node("g", "a1")), states = emptyMap()),
        )
        assertEquals(
            "a row with nothing saved is still not started",
            LessonStanding.NOT_STARTED,
            LessonMastery.standing(lesson = lesson(completed = false), nodes = listOf(node("g", "a1")), states = emptyMap()),
        )
    }

    @Test
    fun `a lesson left mid-step is in progress, not completed`() {
        assertEquals(
            LessonStanding.IN_PROGRESS,
            LessonMastery.standing(
                lesson = lesson(completed = false, stageId = "practice", completedStages = setOf("intro")),
                nodes = listOf(node("g", "a1")),
                states = emptyMap(),
            ),
        )
    }

    @Test
    fun `a finished lesson whose nodes are unproven is completed, not mastered`() {
        val nodes = listOf(node("g", "a1"))
        // Three answers in one shape is not the two shapes (or the delayed retest) the
        // gate asks for, so the lesson is seen but not known.
        val states = mapOf("g" to state("g", formats = ExerciseFormat.FILL_BLANK.bit))

        assertEquals(
            LessonStanding.COMPLETED,
            LessonMastery.standing(lesson = lesson(), nodes = nodes, states = states),
        )
    }

    @Test
    fun `a finished lesson whose every node needs no practice today is mastered`() {
        val nodes = listOf(node("g", "a1"), node("v", "a1"))
        val states = mapOf("g" to state("g"), "v" to state("v"))

        assertEquals(
            LessonStanding.MASTERED,
            LessonMastery.standing(lesson = lesson(), nodes = nodes, states = states),
        )
    }

    @Test
    fun `one weak node keeps the lesson out of mastered`() {
        val nodes = listOf(node("g", "a1"), node("v", "a1"))
        val states = mapOf("g" to state("g"), "v" to state("v", consecutiveCorrect = 0))

        assertEquals(
            "a lesson is only as mastered as its weakest node",
            LessonStanding.COMPLETED,
            LessonMastery.standing(lesson = lesson(), nodes = nodes, states = states),
        )
    }

    @Test
    fun `a lesson the graph does not know can be finished but never mastered`() {
        assertEquals(
            LessonStanding.COMPLETED,
            LessonMastery.standing(lesson = lesson(), nodes = emptyList(), states = emptyMap()),
        )
    }

    @Test
    fun `mastered lessons are counted only among completed ones`() {
        val nodesByLesson = mapOf(
            "done-and-known" to listOf(node("g1", "done-and-known")),
            "done-not-known" to listOf(node("g2", "done-not-known")),
            "unfinished" to listOf(node("g3", "unfinished")),
        )
        val states = mapOf(
            "g1" to state("g1"),
            "g2" to state("g2", mastery = 0.4f),
            "g3" to state("g3"),
        )

        // The third lesson is fully evidenced but not completed: steps come first, so
        // the number can never be larger than what the completed line shows.
        assertEquals(
            1,
            LessonMastery.masteredCount(
                completed = setOf("done-and-known", "done-not-known"),
                nodesByLesson = nodesByLesson,
                states = states,
            ),
        )
    }
}
