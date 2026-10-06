package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.engine.DefaultKnowledgeEngine
import org.token.english.domain.engine.KnowledgeEvidence
import org.token.english.domain.engine.Sm2ReviewScheduler
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.KnowledgeType
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Skill

/**
 * The learner model: one graded answer must move a knowledge item's mastery and
 * its review schedule in a predictable way, and every answer must land on the
 * right nodes (audit §5/§19/§20).
 */
class KnowledgeEngineTest {

    private val engine = DefaultKnowledgeEngine()
    private val now = 1_700_000_000_000L

    private fun item(
        id: String,
        type: KnowledgeType = KnowledgeType.GRAMMAR,
        skills: List<Skill> = listOf(Skill.GRAMMAR),
    ) = KnowledgeItem(
        id = id,
        type = type,
        title = id,
        titleFa = id,
        level = LearningLevel.A1,
        prerequisites = emptyList(),
        lessonIds = listOf("l1"),
        skills = skills,
    )

    private fun state(
        itemId: String = "a",
        mastery: Float = 0.5f,
        intervalDays: Int = 0,
        easeFactor: Float = 2.5f,
        repetitions: Int = 0,
        lapses: Int = 0,
        nextReviewAt: Long = 0L,
    ) = KnowledgeState(
        itemId = itemId,
        mastery = mastery,
        exposureCount = 1,
        consecutiveCorrect = 0,
        consecutiveIncorrect = 0,
        intervalDays = intervalDays,
        easeFactor = easeFactor,
        repetitions = repetitions,
        lapses = lapses,
        lastReviewedAt = null,
        nextReviewAt = nextReviewAt,
    )

    @Test
    fun `a first correct answer seeds mastery high and schedules tomorrow`() {
        val next = engine.applyAttempt("a", null, correct = true, now = now)
        assertEquals("first hit counts as known, not half-known", 1f, next.mastery, 0.0001f)
        assertEquals(1, next.exposureCount)
        assertEquals(1, next.intervalDays)
        assertEquals(1, next.repetitions)
        assertEquals(0, next.lapses)
        assertEquals(1, next.consecutiveCorrect)
        assertEquals(0, next.consecutiveIncorrect)
        assertEquals("ease is untouched by a correct answer", 2.5f, next.easeFactor, 0.0001f)
        assertEquals(now, next.lastReviewedAt)
        assertTrue("due in the future", next.nextReviewAt > now)
    }

    @Test
    fun `a first wrong answer seeds mastery low and comes back the same session`() {
        val next = engine.applyAttempt("a", null, correct = false, now = now)
        assertEquals("a first miss must not look half-learned", 0f, next.mastery, 0.0001f)
        assertEquals(0, next.intervalDays)
        assertEquals(0, next.repetitions)
        assertEquals(1, next.lapses)
        assertEquals(1, next.consecutiveIncorrect)
        assertEquals("ease drops on a lapse", 2.3f, next.easeFactor, 0.0001f)
        assertEquals("AGAIN re-queues within the session", now + 10 * 60 * 1000L, next.nextReviewAt)
    }

    @Test
    fun `mastery moves with the shared engine rather than jumping`() {
        val afterWrong = engine.applyAttempt("a", null, correct = false, now = now)
        val afterRecovery = engine.applyAttempt("a", afterWrong, correct = true, now = now + 1000)
        // EWMA with learning rate 0.2: 0 * 0.8 + 1 * 0.2
        assertEquals(0.2f, afterRecovery.mastery, 0.0001f)
        assertTrue("recovery is gradual", afterRecovery.mastery < 1f)
    }

    @Test
    fun `repeated correct answers stretch the interval`() {
        var current = engine.applyAttempt("a", null, correct = true, now = now)
        val first = current.intervalDays
        current = engine.applyAttempt("a", current, correct = true, now = now)
        assertTrue("second hit must wait longer than the first", current.intervalDays > first)
        val second = current.intervalDays
        current = engine.applyAttempt("a", current, correct = true, now = now)
        assertTrue("interval keeps growing", current.intervalDays > second)
    }

    @Test
    fun `a miss resets the correct streak but keeps the node alive`() {
        var current = engine.applyAttempt("a", null, correct = true, now = now)
        current = engine.applyAttempt("a", current, correct = true, now = now)
        assertEquals(2, current.consecutiveCorrect)
        current = engine.applyAttempt("a", current, correct = false, now = now)
        assertEquals("streak broken", 0, current.consecutiveCorrect)
        assertEquals(1, current.consecutiveIncorrect)
        assertTrue("the node still exists with its ease", current.easeFactor > 0f)
    }

    @Test
    fun `ease never falls below the scheduler floor`() {
        var current: KnowledgeState? = null
        repeat(20) { current = engine.applyAttempt("a", current, correct = false, now = now) }
        assertEquals(Sm2ReviewScheduler.MIN_EASE, current!!.easeFactor, 0.0001f)
        assertEquals(20, current!!.exposureCount)
    }

    @Test
    fun `mastery is only claimed once the interval clears the threshold`() {
        assertTrue(engine.isMastered(state(intervalDays = Sm2ReviewScheduler.MASTERED_INTERVAL_DAYS)))
        assertFalse(engine.isMastered(state(intervalDays = Sm2ReviewScheduler.MASTERED_INTERVAL_DAYS - 1)))
    }

    @Test
    fun `due is decided by the scheduled date`() {
        assertTrue(engine.isDue(state(nextReviewAt = now), now = now))
        assertFalse(engine.isDue(state(nextReviewAt = now + 1), now = now))
    }

    @Test
    fun `the same inputs always produce the same state`() {
        val a = engine.applyAttempt("a", null, correct = true, now = now)
        val b = engine.applyAttempt("a", null, correct = true, now = now)
        assertEquals(a, b)
        assertNotEquals("different items stay distinct", a.itemId, engine.applyAttempt("b", null, true, now).itemId)
    }

    @Test
    fun `evidence prefers nodes of the matching knowledge type`() {
        val grammar = item("g", KnowledgeType.GRAMMAR)
        val vocab = item("v", KnowledgeType.VOCABULARY, listOf(Skill.VOCABULARY))
        val chosen = KnowledgeEvidence.itemsFor(listOf(grammar, vocab), Skill.GRAMMAR)
        assertEquals(listOf("g"), chosen.map { it.id })
    }

    @Test
    fun `listening counts as vocabulary evidence`() {
        assertEquals(KnowledgeType.VOCABULARY, KnowledgeEvidence.knowledgeTypeFor(Skill.LISTENING))
        assertEquals(KnowledgeType.DISCOURSE, KnowledgeEvidence.knowledgeTypeFor(Skill.WRITING))
        assertEquals(KnowledgeType.GRAMMAR, KnowledgeEvidence.knowledgeTypeFor(Skill.GRAMMAR))
    }

    @Test
    fun `evidence falls back to the matching skill then to the whole lesson`() {
        val vocab = item("v", KnowledgeType.VOCABULARY, listOf(Skill.VOCABULARY))
        // No DISCOURSE node, but the node lists READING → the by-skill pass picks it.
        val reading = item("r", KnowledgeType.VOCABULARY, listOf(Skill.VOCABULARY, Skill.READING))
        assertEquals(
            listOf("r"),
            KnowledgeEvidence.itemsFor(listOf(vocab, reading), Skill.READING).map { it.id },
        )
        // Nothing claims WRITING → the answer still counts for the lesson's nodes.
        assertEquals(
            listOf("v", "r"),
            KnowledgeEvidence.itemsFor(listOf(vocab, reading), Skill.WRITING).map { it.id },
        )
    }

    @Test
    fun `skill-tagged nodes are credited even when a type match exists`() {
        val vocab = item("v", KnowledgeType.VOCABULARY, listOf(Skill.VOCABULARY))
        val phonology = item("p", KnowledgeType.PHONOLOGY, listOf(Skill.LISTENING))
        val grammar = item("g", KnowledgeType.GRAMMAR, listOf(Skill.GRAMMAR))
        // A-7: listening's default type is VOCABULARY, so the phonology node only
        // collects evidence through its LISTENING tag — the rule must union both
        // axes or the node stays at zero mastery forever.
        assertEquals(
            listOf("v", "p"),
            KnowledgeEvidence.itemsFor(listOf(vocab, phonology), Skill.LISTENING).map { it.id },
        )
        // A node that matches neither axis is still excluded.
        assertEquals(
            listOf("v", "p"),
            KnowledgeEvidence.itemsFor(listOf(vocab, phonology, grammar), Skill.LISTENING).map { it.id },
        )
        // Speaking is phonology's own evidence axis (suspended feature, same rule).
        assertEquals(
            listOf("p"),
            KnowledgeEvidence.itemsFor(listOf(vocab, phonology), Skill.SPEAKING).map { it.id },
        )
    }

    @Test
    fun `a lesson with no knowledge items attributes nothing instead of failing`() {
        assertTrue(KnowledgeEvidence.itemsFor(emptyList(), Skill.GRAMMAR).isEmpty())
    }
}
