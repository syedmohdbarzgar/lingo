package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.engine.AdaptiveLearningPlanner
import org.token.english.domain.engine.DefaultKnowledgeGraph
import org.token.english.domain.engine.PrerequisiteEngine
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.KnowledgeType
import org.token.english.domain.model.LearningActionType
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Skill

/**
 * The planner is the product (audit §5/§21): it turns learner state into an ordered,
 * justified "study this now". These tests cover the four simulated learners from
 * audit §18 so the *decisions* are proven before any surface renders them.
 */
class AdaptiveLearningPlannerTest {

    private fun item(id: String, prerequisites: List<String> = emptyList()) = KnowledgeItem(
        id = id,
        type = KnowledgeType.GRAMMAR,
        title = id,
        titleFa = id,
        level = LearningLevel.A1,
        prerequisites = prerequisites,
        lessonIds = listOf("l1"),
        skills = listOf(Skill.GRAMMAR),
    )

    private fun state(itemId: String, mastery: Float) = KnowledgeState(
        itemId = itemId,
        mastery = mastery,
        exposureCount = 1,
        consecutiveCorrect = 0,
        consecutiveIncorrect = 0,
        intervalDays = 0,
        easeFactor = 2.5f,
        repetitions = 0,
        lapses = 0,
        lastReviewedAt = null,
        nextReviewAt = 0L,
    )

    // a (root) → b → c, d independent root
    private val graph = DefaultKnowledgeGraph(
        listOf(item("c", listOf("b")), item("a"), item("b", listOf("a")), item("d")),
    )
    private val planner = AdaptiveLearningPlanner(graph, PrerequisiteEngine(graph))

    private fun plan(
        states: List<KnowledgeState> = emptyList(),
        due: Int = 0,
        focus: List<KnowledgeItem> = emptyList(),
    ) = planner.plan(
        states = states,
        dueReviewCount = due,
        focusItems = focus,
        targetMinutes = 15,
        todayStudySeconds = 0L,
    )

    // --- scenario 4: good knowledge, long absence ------------------------------

    @Test
    fun `due reviews always lead the day`() {
        val decision = plan(
            states = listOf(state("a", 0.9f)),
            due = 12,
            focus = listOf(item("b", listOf("a"))),
        )
        assertEquals(LearningActionType.REVIEW, decision.actions.first().type)
        assertEquals(12, decision.dueReviewCount)
        assertTrue(decision.actions.first().reasonFa.contains("12"))
    }

    @Test
    fun `no due cards means no review block`() {
        val decision = plan(due = 0, focus = listOf(item("a")))
        assertTrue(decision.actions.none { it.type == LearningActionType.REVIEW })
    }

    // --- scenario 1: always correct -------------------------------------------

    @Test
    fun `a fully strong focus lesson opens a new unlocked unit`() {
        val decision = plan(
            states = listOf(state("a", 0.9f), state("b", 0.9f), state("c", 0.9f)),
            focus = listOf(item("c", listOf("b"))),
        )
        // c needs no practice, so the planner advances to the next unlocked root.
        assertEquals(listOf(LearningActionType.LEARN), decision.actions.map { it.type })
        assertEquals("d", decision.actions.first().itemId)
    }

    @Test
    fun `an unseen focus node is simply learned`() {
        val decision = plan(states = listOf(state("a", 0.9f)), focus = listOf(item("b", listOf("a"))))
        assertEquals(listOf(LearningActionType.LEARN), decision.actions.map { it.type })
        assertEquals("b", decision.actions.first().itemId)
    }

    // --- scenario 2: always wrong → prerequisite check + remediation ----------

    @Test
    fun `a weak prerequisite is remediated before the dependent node`() {
        val decision = plan(
            states = listOf(state("a", 0.2f)),
            focus = listOf(item("b", listOf("a"))),
        )
        assertEquals(listOf(LearningActionType.REMEDIATE), decision.actions.map { it.type })
        assertEquals("a", decision.actions.first().itemId)
        assertTrue("the reason names the dependent node", decision.actions.first().reasonFa.contains("b"))
    }

    @Test
    fun `an unseen prerequisite is started before the dependent node`() {
        val decision = plan(states = emptyList(), focus = listOf(item("b", listOf("a"))))
        assertEquals(LearningActionType.REMEDIATE, decision.actions.first().type)
        assertEquals("a", decision.actions.first().itemId)
    }

    @Test
    fun `remediation outranks a new unit in the same plan`() {
        val decision = plan(
            states = listOf(state("a", 0.2f)),
            focus = listOf(item("b", listOf("a")), item("d")),
        )
        val ordered = decision.actions.map { it.type }
        assertEquals(
            "remediate first, then learn",
            listOf(LearningActionType.REMEDIATE, LearningActionType.LEARN),
            ordered,
        )
    }

    // --- scenario 3: good recognition, shaky consolidation --------------------

    @Test
    fun `a met but not strong focus node is practised`() {
        val decision = plan(
            states = listOf(state("a", 0.9f), state("b", 0.55f)),
            focus = listOf(item("b", listOf("a"))),
        )
        assertEquals(listOf(LearningActionType.PRACTISE), decision.actions.map { it.type })
        assertEquals("b", decision.actions.first().itemId)
    }

    // --- determinism, caps and empties ----------------------------------------

    @Test
    fun `the plan is deterministic and never exceeds the action cap`() {
        val states = List(10) { state("n$it", 0.1f) }
        val focus = List(10) { item("n$it") }
        val a = plan(states = states, due = 3, focus = focus)
        val b = plan(states = states, due = 3, focus = focus)
        assertEquals(a, b)
        assertTrue(a.actions.size <= AdaptiveLearningPlanner.MAX_ACTIONS)
    }

    @Test
    fun `a brand new learner is offered the first unlocked root`() {
        val decision = plan()
        assertEquals(listOf(LearningActionType.LEARN), decision.actions.map { it.type })
        assertEquals("a", decision.actions.first().itemId)
        assertEquals(0, decision.dueReviewCount)
    }

    @Test
    fun `the same prerequisite is not remediated twice for two dependents`() {
        val shared = DefaultKnowledgeGraph(
            listOf(item("p"), item("x", listOf("p")), item("y", listOf("p"))),
        )
        val p = AdaptiveLearningPlanner(shared, PrerequisiteEngine(shared))
        val decision = p.plan(
            states = emptyList(),
            dueReviewCount = 0,
            focusItems = listOf(item("x", listOf("p")), item("y", listOf("p"))),
            targetMinutes = 15,
            todayStudySeconds = 0L,
        )
        assertFalse(decision.actions.isEmpty())
        assertEquals(
            "one remediation for the shared prerequisite",
            1,
            decision.actions.count { it.type == LearningActionType.REMEDIATE },
        )
    }
}
