package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.engine.DefaultKnowledgeGraph
import org.token.english.domain.engine.PrerequisiteEngine
import org.token.english.domain.engine.Readiness
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.KnowledgeType
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Skill

/**
 * The prerequisite engine must answer "can this node be learned yet, and if not,
 * what do I fix first?" (audit §4) without ever looking at Android or the clock.
 */
class PrerequisiteEngineTest {

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
    private val engine = PrerequisiteEngine(graph)

    @Test
    fun `a root node is always ready`() {
        val report = engine.report("a", emptyMap())
        assertEquals(Readiness.READY, report.readiness)
        assertTrue(report.unseen.isEmpty())
        assertEquals(0, report.depth)
    }

    @Test
    fun `an unseen prerequisite blocks the dependent node`() {
        val report = engine.report("b", emptyMap())
        assertEquals(Readiness.BLOCKED, report.readiness)
        assertEquals(listOf("a"), report.unseen)
        assertFalse(report.isReady)
    }

    @Test
    fun `a met but weak prerequisite asks for remediation rather than blocking`() {
        val report = engine.report("b", mapOf("a" to state("a", 0.3f)))
        assertEquals(Readiness.NEEDS_REMEDIATION, report.readiness)
        assertEquals(listOf("a"), report.weak)
        assertTrue(report.unseen.isEmpty())
    }

    @Test
    fun `a mastered prerequisite unlocks the node`() {
        val report = engine.report("b", mapOf("a" to state("a", 0.95f)))
        assertEquals(Readiness.READY, report.readiness)
        assertTrue(engine.report("c", mapOf("a" to state("a", 0.95f), "b" to state("b", 0.9f))).isReady)
    }

    @Test
    fun `remediation target prefers an unseen prerequisite then the weakest`() {
        // b has prerequisite a; unseen → a itself is the target.
        assertEquals("a", engine.remediationTarget("b", emptyMap())!!.id)
        // Seen but weak → same target, now because it is weak.
        assertEquals("a", engine.remediationTarget("b", mapOf("a" to state("a", 0.2f)))!!.id)
        // Ready → nothing to remediate.
        assertNull(engine.remediationTarget("b", mapOf("a" to state("a", 0.9f))))
    }

    @Test
    fun `weak prerequisites are ordered weakest first`() {
        val diamond = DefaultKnowledgeGraph(
            listOf(
                item("root", listOf("p1", "p2")),
                item("p1"),
                item("p2"),
            ),
        )
        val report = PrerequisiteEngine(diamond).report(
            "root",
            mapOf("p1" to state("p1", 0.6f), "p2" to state("p2", 0.2f)),
        )
        assertEquals(listOf("p2", "p1"), report.weak)
    }

    @Test
    fun `depth is the longest prerequisite chain, not the node count`() {
        assertEquals(0, engine.depth("a"))
        assertEquals(1, engine.depth("b"))
        assertEquals(2, engine.depth("c"))
        assertEquals(0, engine.depth("d"))
        assertEquals("unknown ids have no depth", 0, engine.depth("ghost"))
    }

    @Test
    fun `a dangling prerequisite reads as unmet instead of unlocking the node`() {
        val broken = DefaultKnowledgeGraph(listOf(item("x", listOf("ghost"))))
        val report = PrerequisiteEngine(broken).report("x", emptyMap())
        assertEquals(Readiness.BLOCKED, report.readiness)
        assertEquals(listOf("ghost"), report.unseen)
        assertNull(PrerequisiteEngine(broken).remediationTarget("x", emptyMap()))
    }

    @Test
    fun `a cyclic graph terminates and reports the node rather than overflowing`() {
        val cyclic = DefaultKnowledgeGraph(
            listOf(item("a", listOf("b")), item("b", listOf("a"))),
        )
        val e = PrerequisiteEngine(cyclic)
        // The call must simply return; depth is bounded by the cycle guard.
        assertTrue(e.depth("a") >= 0)
        assertEquals(Readiness.BLOCKED, e.report("a", emptyMap()).readiness)
    }
}
