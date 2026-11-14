package org.token.english

import org.junit.Assert.assertEquals
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
 * A due node's own exercises, queued as review cards.
 *
 * Two halves, and both are decided rules rather than screen behaviour:
 *
 * - `NodeCardPicker` — *which* exercises a node's block asks (one per shape the node has
 *   not been confirmed in, at most two), and which it must never ask (a listening item
 *   needs its audio, a speaking item needs the microphone; the review screen has
 *   neither, and a card that cannot be answered is a dead end);
 * - `GetDueNodeCardsUseCase` — the cards themselves, built from the focused drill's own
 *   exercise subset so every question is one the bundle really asks about that node.
 *
 * The session's half (the block is asked after the words, each answer is graded through
 * the lesson's evidence path, and the session ends after the last card) is pinned in
 * `ReviewViewModelTest`, where the ViewModel harness lives.
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

    private fun translation(id: String) = Exercise.Translation(
        id = id,
        lessonId = lessonId,
        prompt = "من اینجا زندگی می‌کنم.",
        accepted = listOf("I live here."),
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
    fun `a missing shape is asked instead of the authored first`() {
        val first = mc("ex.01")
        val second = blank("ex.02")
        // Proven in a choice only: the blank is the shape that adds the second format,
        // so the authored order is not what decides — the shape the node is missing is.
        val confirmed = AnswerChecker.formatOf(first).bit

        assertEquals(listOf(second), NodeCardPicker.pickAll(listOf(first, second), confirmed))
    }

    @Test
    fun `two missing shapes are both asked, and a third waits for the drill`() {
        // Nothing is confirmed, so every shape is missing — but the block is bounded by
        // what the gate needs (two), not by how many exercises the lesson ships.
        val picks = NodeCardPicker.pickAll(
            listOf(mc("ex.01"), blank("ex.02"), translation("ex.03")),
            confirmedFormats = 0,
        )

        assertEquals(NodeCardPicker.MAX_CARDS_PER_NODE, picks.size)
        assertEquals("the authored order decides among the missing shapes", listOf("ex.01", "ex.02"), picks.map { it.id })
    }

    @Test
    fun `a fully confirmed node is asked one authored question`() {
        val first = mc("ex.01")
        val second = blank("ex.02")
        val confirmed = AnswerChecker.formatOf(first).bit or AnswerChecker.formatOf(second).bit

        // No shape left to add, so there is nothing a second question would prove — the
        // session still touches the node, with the authored first.
        assertEquals(listOf(first), NodeCardPicker.pickAll(listOf(first, second), confirmed))
    }

    @Test
    fun `a listening item is asked, a speaking one is not`() {
        // listening-card pass: the card plays a listening item's audio through the same
        // TTS seam the lesson uses, so the sound is askable there. A take is not — the
        // review screen has no microphone.
        val heard = listening("ex.01")
        assertEquals(listOf(heard), NodeCardPicker.pickAll(listOf(heard), 0))

        assertTrue(
            "a speaking item needs a take the card cannot record",
            NodeCardPicker.pickAll(listOf(speaking("ex.02")), 0).isEmpty(),
        )
        assertTrue("nothing to ask is an answer, not a failure", NodeCardPicker.pickAll(emptyList(), 0).isEmpty())
    }

    @Test
    fun `a node missing only the listening shape is asked its listening item`() {
        val first = mc("ex.01")
        val second = blank("ex.02")
        val heard = listening("ex.03")
        val confirmed = AnswerChecker.formatOf(first).bit or AnswerChecker.formatOf(second).bit

        // Choice and blank are proven; the sound is the one shape left. Before the
        // listening-card pass this node could not add it by review at all.
        assertEquals(listOf(heard), NodeCardPicker.pickAll(listOf(first, second, heard), confirmed))
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
    fun `a due node becomes one card per missing shape, all of its own lesson`() {
        val h = Harness().withDueNode(exercises = listOf(blank("ex.01"), translation("ex.02")))

        val cards = h.cards()

        assertEquals("two shapes are missing, so the block is two questions", 2, cards.size)
        assertEquals(listOf("ex.01", "ex.02"), cards.map { it.exercise.id })
        assertTrue(
            "every card belongs to the node whose schedule is due, in its own lesson",
            cards.all { it.itemId == "grammar.airport" && it.lessonId == lessonId },
        )
    }

    @Test
    fun `a due node with nothing askable is dropped rather than offered dead`() {
        val h = Harness().withDueNode(exercises = listOf(speaking("ex.01"), speaking("ex.02")))

        assertTrue(
            "the drill still serves it, but the card surface has no question to ask",
            h.cards().isEmpty(),
        )
    }
}
