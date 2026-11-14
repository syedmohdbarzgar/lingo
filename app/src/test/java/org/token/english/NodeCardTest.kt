package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.engine.NodeCardPicker
import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.KnowledgeType
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Skill
import org.token.english.domain.usecase.GetDueKnowledgeReviewsUseCase
import org.token.english.domain.usecase.GetDueNodeCardsUseCase
import org.token.english.domain.usecase.GetFocusPlanUseCase

/**
 * A due node's own exercise, queued as a review card (graph-card pass).
 *
 * Two halves, and both are decided rules rather than screen behaviour:
 *
 * - `NodeCardPicker` — *which* exercise a card asks, and which it must never ask
 *   (a listening item needs its audio, a speaking item needs the microphone; the
 *   review screen has neither, and a card that cannot be answered is a dead end);
 * - `GetDueNodeCardsUseCase` — the card itself, built from the focused drill's own
 *   exercise subset so the question is one the bundle really asks about that node.
 *
 * The session's half (the card is asked after the words, graded through the lesson's
 * evidence path and ends the session) is pinned in `ReviewViewModelTest`, where the
 * ViewModel harness lives.
 */
class NodeCardTest {

    private val lessonId = "a2.airport"

    private fun mc(id: String) = Exercise.MultipleChoice(
        id = id,
        lessonId = lessonId,
        question = "Choose",
        questionFa = null,
        options = listOf("is", "are"),
        correctIndex = 0,
        skill = Skill.GRAMMAR,
    )

    private fun blank(id: String) = Exercise.FillBlank(
        id = id,
        lessonId = lessonId,
        sentence = "I ______ here.",
        accepted = listOf("live"),
        skill = Skill.GRAMMAR,
    )

    private fun listening(id: String) = Exercise.Listening(
        id = id,
        lessonId = lessonId,
        audioText = "The gate is closed.",
        accepted = listOf("The gate is closed."),
        skill = Skill.LISTENING,
    )

    private fun speaking(id: String) = Exercise.Speaking(
        id = id,
        lessonId = lessonId,
        prompt = "سلام کن",
        referenceAnswers = listOf("Hello"),
        skill = Skill.SPEAKING,
    )

    @Test
    fun `the format the node has not been confirmed in is asked first`() {
        val first = mc("ex.01")
        val second = blank("ex.02")
        // Proven in a choice only: the blank is what adds the second format, so the
        // authored order is not what decides — the evidence the node is missing is.
        val confirmed = AnswerChecker.formatOf(first).bit

        assertEquals(second, NodeCardPicker.pick(listOf(first, second), confirmed))
    }

    @Test
    fun `once every format is confirmed the authored first is asked`() {
        val first = mc("ex.01")
        val second = blank("ex.02")
        val confirmed = AnswerChecker.formatOf(first).bit or AnswerChecker.formatOf(second).bit

        // No format is worth more than another here, so the lesson's own order stands:
        // the same question every session instead of one that jumps around.
        assertEquals(first, NodeCardPicker.pick(listOf(first, second), confirmed))
    }

    @Test
    fun `an exercise the review screen cannot ask is never a card`() {
        // A listening item is its audio and a speaking item needs a take; the review
        // surface has no speaker and no microphone, so neither can be asked there.
        assertNull(NodeCardPicker.pick(listOf(listening("ex.01"), speaking("ex.02")), 0))
        assertNull("nothing to ask is an answer, not a failure", NodeCardPicker.pick(emptyList(), 0))
    }

    private fun Harness.withDueNode(
        itemId: String = "grammar.airport",
        exercises: List<Exercise>,
    ): Harness {
        knowledge.itemsFlow.value = listOf(
            KnowledgeItem(
                id = itemId,
                type = KnowledgeType.GRAMMAR,
                title = "At the airport",
                titleFa = "در فرودگاه",
                level = LearningLevel.A2,
                prerequisites = emptyList(),
                lessonIds = listOf(lessonId),
                skills = listOf(Skill.GRAMMAR),
            ),
        )
        knowledge.statesFlow.value = listOf(
            KnowledgeState(
                itemId = itemId,
                mastery = 0.7f,
                exposureCount = 2,
                consecutiveCorrect = 1,
                consecutiveIncorrect = 0,
                intervalDays = 0,
                easeFactor = 2.5f,
                repetitions = 1,
                lapses = 0,
                lastReviewedAt = 0L,
                nextReviewAt = 0L,
            ),
        )
        lessons.exercises[lessonId] = exercises
        return this
    }

    private class Harness {
        val knowledge = FakeKnowledgeRepository()
        val lessons = FakeLessonRepository()

        fun cards(now: Long = 1_000L): List<org.token.english.domain.model.NodeExerciseCard> {
            val due = GetDueKnowledgeReviewsUseCase(knowledge, GetFocusPlanUseCase(knowledge, lessons))
            return kotlinx.coroutines.runBlocking { GetDueNodeCardsUseCase(due, lessons, knowledge)(now) }
        }
    }

    @Test
    fun `a due node becomes a card carrying one of its own exercises`() {
        val h = Harness().withDueNode(exercises = listOf(mc("ex.01"), blank("ex.02")))

        val cards = h.cards()

        assertEquals(1, cards.size)
        assertEquals("the card belongs to the node whose schedule is due", "grammar.airport", cards.single().itemId)
        assertEquals("and to the lesson that teaches it", lessonId, cards.single().lessonId)
        assertTrue(
            "the question is one of the node's own exercises",
            cards.single().exercise.id in setOf("ex.01", "ex.02"),
        )
    }

    @Test
    fun `a due node with nothing askable is dropped rather than offered dead`() {
        val h = Harness().withDueNode(exercises = listOf(listening("ex.01"), speaking("ex.02")))

        assertTrue(
            "the drill still serves it, but the card surface has no question to ask",
            h.cards().isEmpty(),
        )
    }
}
