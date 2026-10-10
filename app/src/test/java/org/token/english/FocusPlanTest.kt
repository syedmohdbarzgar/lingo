package org.token.english

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.KnowledgeType
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Skill
import org.token.english.domain.usecase.GetFocusPlanUseCase

/**
 * `GetFocusPlanUseCase` (checklist B-1): the remediation engine's decision, on
 * top of the in-memory repositories — no Android, no Room, no device.
 *
 * What it proves: the block targets ONE node (error isolation, not a whole
 * subject), it drills only the exercises that are evidence about that node, and
 * it says why. A node with no evidence, or a graph with nothing weak, yields no
 * block at all instead of a meaningless one.
 */
class FocusPlanTest {

    private val lessonId = "a1.test.lesson-01"

    private fun item(
        id: String,
        type: KnowledgeType,
        skills: List<Skill>,
        prerequisites: List<String> = emptyList(),
    ) = KnowledgeItem(
        id = id,
        type = type,
        title = id,
        titleFa = "«$id»",
        level = LearningLevel.A1,
        prerequisites = prerequisites,
        lessonIds = listOf(lessonId),
        skills = skills,
    )

    private fun choice(id: String, skill: Skill) = Exercise.MultipleChoice(
        id = id,
        lessonId = lessonId,
        question = "Choose",
        questionFa = null,
        options = listOf("right", "wrong"),
        correctIndex = 0,
        skill = skill,
    )

    private fun state(itemId: String, mastery: Float) = KnowledgeState(
        itemId = itemId,
        mastery = mastery,
        exposureCount = 2,
        consecutiveCorrect = 0,
        consecutiveIncorrect = 1,
        intervalDays = 0,
        easeFactor = 2.5f,
        repetitions = 0,
        lapses = 0,
        lastReviewedAt = null,
        nextReviewAt = 0L,
    )

    private fun repositories(
        items: List<KnowledgeItem>,
        states: List<KnowledgeState> = emptyList(),
    ): Pair<FakeKnowledgeRepository, FakeLessonRepository> {
        val knowledge = FakeKnowledgeRepository(items).apply { statesFlow.value = states }
        val lessons = FakeLessonRepository()
        lessons.exercises[lessonId] = listOf(
            choice("$lessonId.ex.01", Skill.GRAMMAR),
            choice("$lessonId.ex.02", Skill.VOCABULARY),
        )
        return knowledge to lessons
    }

    private val grammar = item("grammar.test", KnowledgeType.GRAMMAR, listOf(Skill.GRAMMAR))
    private val vocabulary = item("vocab.test", KnowledgeType.VOCABULARY, listOf(Skill.VOCABULARY))

    @Test
    fun `a named node is drilled with only the exercises that are evidence about it`() = runTest {
        val (knowledge, lessons) = repositories(
            items = listOf(grammar, vocabulary),
            states = listOf(state("grammar.test", 0.2f)),
        )
        val plan = GetFocusPlanUseCase(knowledge, lessons)("grammar.test")!!

        assertEquals("grammar.test", plan.itemId)
        assertEquals(lessonId, plan.lessonId)
        assertEquals("«grammar.test»", plan.titleFa)
        assertEquals(0.2f, plan.mastery, 0.0001f)
        assertTrue("the engine explains why", plan.reasonFa.isNotEmpty())
        assertEquals(
            "the vocabulary drill is not evidence about the grammar node",
            setOf("$lessonId.ex.01"),
            plan.exerciseIds,
        )
    }

    @Test
    fun `a node the learner never studied is taught, not drilled`() = runTest {
        val (knowledge, lessons) = repositories(listOf(grammar, vocabulary))
        assertNull(GetFocusPlanUseCase(knowledge, lessons)("grammar.test"))
    }

    @Test
    fun `with no node the weakest one that has evidence is isolated`() = runTest {
        val (knowledge, lessons) = repositories(
            items = listOf(grammar, vocabulary),
            states = listOf(state("grammar.test", 0.9f), state("vocab.test", 0.3f)),
        )
        val plan = GetFocusPlanUseCase(knowledge, lessons)(null)!!

        assertEquals("vocab.test", plan.itemId)
        assertEquals(0.3f, plan.mastery, 0.0001f)
        assertEquals(setOf("$lessonId.ex.02"), plan.exerciseIds)
    }

    @Test
    fun `it reports what the weak node is blocking`() = runTest {
        val blocked = item("grammar.advanced", KnowledgeType.GRAMMAR, listOf(Skill.GRAMMAR), listOf("grammar.test"))
        val (knowledge, lessons) = repositories(
            items = listOf(grammar, blocked),
            states = listOf(state("grammar.test", 0.2f)),
        )
        val plan = GetFocusPlanUseCase(knowledge, lessons)(null)!!

        assertEquals("grammar.test", plan.itemId)
        assertEquals("one node stays locked behind it", 1, plan.blockedCount)
    }

    @Test
    fun `nothing weak enough yields no block`() = runTest {
        val (knowledge, lessons) = repositories(
            items = listOf(grammar),
            states = listOf(state("grammar.test", 0.9f)),
        )
        assertNull(GetFocusPlanUseCase(knowledge, lessons)(null))
    }

    @Test
    fun `an empty curriculum yields no block`() = runTest {
        val (knowledge, lessons) = repositories(items = emptyList())
        assertNull(GetFocusPlanUseCase(knowledge, lessons)(null))
    }
}
