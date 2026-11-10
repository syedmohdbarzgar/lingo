package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.engine.DefaultKnowledgeEngine
import org.token.english.domain.engine.KnowledgeEvidence
import org.token.english.domain.engine.KnowledgeGates
import org.token.english.domain.engine.STRONG_MASTERY
import org.token.english.domain.engine.Sm2ReviewScheduler
import org.token.english.domain.model.ExerciseFormat
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

    private val twoFormats = ExerciseFormat.MULTIPLE_CHOICE.bit or ExerciseFormat.FILL_BLANK.bit

    /** 20 hours: exactly the span the gate asks for before a node can be secured. */
    private val span = KnowledgeGates.MIN_RETEST_SPAN_MS

    /**
     * A node with the evidence a secured one has: three in a row, two formats, and a
     * retest [span] after the first correct answer. Tests that need an edge override
     * the one field they care about.
     */
    private fun state(
        itemId: String = "a",
        mastery: Float = 0.5f,
        intervalDays: Int = 0,
        easeFactor: Float = 2.5f,
        repetitions: Int = 0,
        lapses: Int = 0,
        nextReviewAt: Long = 0L,
        firstCorrectAt: Long? = now,
        lastReviewedAt: Long? = firstCorrectAt?.plus(span),
        consecutiveCorrect: Int = KnowledgeGates.MIN_CONSECUTIVE,
        distinctTypesCorrect: Int = twoFormats,
    ) = KnowledgeState(
        itemId = itemId,
        mastery = mastery,
        exposureCount = 1,
        consecutiveCorrect = consecutiveCorrect,
        consecutiveIncorrect = 0,
        intervalDays = intervalDays,
        easeFactor = easeFactor,
        repetitions = repetitions,
        lapses = lapses,
        lastReviewedAt = lastReviewedAt,
        nextReviewAt = nextReviewAt,
        firstCorrectAt = firstCorrectAt,
        distinctTypesCorrect = distinctTypesCorrect,
    )

    @Test
    fun `a first correct answer seeds mastery as started, not known, and schedules tomorrow`() {
        val next = engine.applyAttempt("a", null, correct = true, now = now, format = ExerciseFormat.MULTIPLE_CHOICE)
        // One tap is evidence, not proof: at 1.0 the node looked fully learned and
        // the planner stopped offering what the learner had just met once.
        assertEquals(
            "first hit is 'started'",
            DefaultKnowledgeEngine.FIRST_HIT_MASTERY,
            next.mastery,
            0.0001f,
        )
        assertFalse("and certainly not mastered", engine.isLongTermMastered(next))
        assertEquals("the date it started being known", now, next.firstCorrectAt)
        assertEquals("confirmed in one format so far", 1, next.distinctTypeCount)
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
        assertTrue(
            engine.isLongTermMastered(
                state(
                    mastery = 0.9f,
                    intervalDays = Sm2ReviewScheduler.MASTERED_INTERVAL_DAYS,
                    repetitions = DefaultKnowledgeEngine.MASTERED_MIN_REPETITIONS,
                ),
            ),
        )
        assertFalse(
            engine.isLongTermMastered(
                state(
                    mastery = 0.9f,
                    intervalDays = Sm2ReviewScheduler.MASTERED_INTERVAL_DAYS - 1,
                    repetitions = DefaultKnowledgeEngine.MASTERED_MIN_REPETITIONS,
                ),
            ),
        )
    }

    @Test
    fun `a long interval alone does not make a node mastered`() {
        // The spaced-repetition schedule can space a node far out while the learner
        // has only answered it once; the planner judged the same node "weak" by its
        // mastery, so the two screens disagreed. One gate now covers both.
        assertFalse(
            "three recalls are required before the word mastered is used",
            engine.isLongTermMastered(
                state(
                    mastery = 0.9f,
                    intervalDays = Sm2ReviewScheduler.MASTERED_INTERVAL_DAYS,
                    repetitions = DefaultKnowledgeEngine.MASTERED_MIN_REPETITIONS - 1,
                ),
            ),
        )
        assertFalse(
            "and the mastery has to be strong, not merely scheduled",
            engine.isLongTermMastered(
                state(
                    mastery = STRONG_MASTERY - 0.01f,
                    intervalDays = Sm2ReviewScheduler.MASTERED_INTERVAL_DAYS,
                    repetitions = DefaultKnowledgeEngine.MASTERED_MIN_REPETITIONS,
                ),
            ),
        )
    }

    @Test
    fun `one format is neither secured nor confirmed two ways`() {
        val same = state(
            mastery = 0.9f,
            intervalDays = Sm2ReviewScheduler.MASTERED_INTERVAL_DAYS,
            repetitions = DefaultKnowledgeEngine.MASTERED_MIN_REPETITIONS,
            distinctTypesCorrect = ExerciseFormat.MULTIPLE_CHOICE.bit,
        )
        assertFalse("pattern-matching one shape does not secure a node", engine.isSecured(same))
        assertFalse("nor does it claim long-term mastery", engine.isLongTermMastered(same))
    }

    @Test
    fun `a same-sitting streak from one format is not secured`() {
        // The gate's reason for existing (checklist 3, chapter 1): EWMA 0.2 from 0.4
        // crosses 0.85 after eight hits in a single sitting, without a single delayed
        // recall ever being asked for. Mastery alone must not move the plan on.
        var current: KnowledgeState? = null
        repeat(8) { i ->
            current = engine.applyAttempt("a", current, true, now + i, ExerciseFormat.MULTIPLE_CHOICE)
        }
        assertTrue("the average did cross the bar", current!!.mastery >= STRONG_MASTERY)
        assertFalse("but nothing was retested after a delay", engine.isSecured(current!!))
        assertEquals("and only one shape proved it", 1, current!!.distinctTypeCount)
    }

    @Test
    fun `securing needs the delay as well as the streak and the formats`() {
        val justShort = state(
            mastery = 0.9f,
            firstCorrectAt = now,
            lastReviewedAt = now + KnowledgeGates.MIN_RETEST_SPAN_MS - 1,
        )
        assertFalse("nineteen hours is not a next-day retest", engine.isSecured(justShort))

        val farEnough = state(
            mastery = 0.9f,
            firstCorrectAt = now,
            lastReviewedAt = now + KnowledgeGates.MIN_RETEST_SPAN_MS,
        )
        assertTrue("twenty hours is", engine.isSecured(farEnough))
    }

    @Test
    fun `a broken streak unsecures a node that was secured`() {
        assertTrue(
            "the same node, with its streak intact",
            engine.isSecured(state(mastery = 0.9f)),
        )
        assertFalse(
            "one miss breaks the run of correct answers",
            engine.isSecured(state(mastery = 0.9f, consecutiveCorrect = 2)),
        )
    }

    @Test
    fun `a second format after the delay is what secures the node`() {
        // The whole story in order: a same-sitting streak is not enough, a next-day
        // answer in the same format is still one shape, and only a second format after
        // the delay turns the claim into "secured".
        var current: KnowledgeState? = null
        repeat(8) { i ->
            current = engine.applyAttempt("a", current, true, now + i, ExerciseFormat.MULTIPLE_CHOICE)
        }
        assertFalse("same sitting", engine.isSecured(current!!))

        val dayLater = engine.applyAttempt(
            "a",
            current!!,
            true,
            now + 24 * 3_600_000L,
            ExerciseFormat.MULTIPLE_CHOICE,
        )
        assertFalse("a delayed recall in the same format is still one shape", engine.isSecured(dayLater))

        val transfer = engine.applyAttempt(
            "a",
            dayLater,
            true,
            now + 25 * 3_600_000L,
            ExerciseFormat.FILL_BLANK,
        )
        assertTrue("two shapes, confirmed a day apart", engine.isSecured(transfer))
    }

    @Test
    fun `securing needs a correct answer, not just a strong number`() {
        assertFalse(
            "a mastery value with no recorded hit cannot be secured",
            engine.isSecured(state(mastery = 0.95f, firstCorrectAt = null)),
        )
        assertFalse(
            "and a long-term claim builds on security",
            engine.isLongTermMastered(
                state(
                    mastery = 0.95f,
                    firstCorrectAt = null,
                    intervalDays = Sm2ReviewScheduler.MASTERED_INTERVAL_DAYS,
                    repetitions = DefaultKnowledgeEngine.MASTERED_MIN_REPETITIONS,
                ),
            ),
        )
    }

    @Test
    fun `the two gates are one definition, shared with the planner`() {
        val secured = state(mastery = STRONG_MASTERY)
        assertEquals(engine.isSecured(secured), KnowledgeGates.isSecured(secured))
        assertTrue(KnowledgeGates.isSecured(secured))
    }

    @Test
    fun `a second format confirms the node, the same one twice does not`() {
        var current = engine.applyAttempt("a", null, correct = true, now = now, format = ExerciseFormat.MULTIPLE_CHOICE)
        assertEquals("first format recorded", 1, current.distinctTypeCount)

        current = engine.applyAttempt("a", current, true, now + 1, ExerciseFormat.MULTIPLE_CHOICE)
        assertEquals("answering the same format again is not a new confirmation", 1, current.distinctTypeCount)

        current = engine.applyAttempt("a", current, true, now + 2, ExerciseFormat.FILL_BLANK)
        assertEquals("a different format is", 2, current.distinctTypeCount)
        assertEquals("and the first correct answer keeps its date", now, current.firstCorrectAt)
    }

    @Test
    fun `a miss records no format and moves no first-correct date`() {
        val firstHit = engine.applyAttempt("a", null, true, now, ExerciseFormat.TRANSLATION)
        val afterMiss = engine.applyAttempt("a", firstHit, false, now + 10, ExerciseFormat.LISTENING)
        assertEquals("only correct answers confirm a format", 1, afterMiss.distinctTypeCount)
        assertEquals("the first hit is not rewritten by a later answer", now, afterMiss.firstCorrectAt)
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
        assertTrue(KnowledgeEvidence.itemsForCard(emptyList(), Skill.VOCABULARY).isEmpty())
    }

    @Test
    fun `a card is evidence only for a node of its own type`() {
        val grammar = item("g", KnowledgeType.GRAMMAR, listOf(Skill.GRAMMAR, Skill.VOCABULARY))
        val vocab = item("v", KnowledgeType.VOCABULARY, listOf(Skill.VOCABULARY))
        // The grammar node *lists* VOCABULARY, so the exercise rule would union it in.
        // A card names one word, so only the vocabulary node may move.
        assertEquals(
            listOf("v"),
            KnowledgeEvidence.itemsForCard(listOf(grammar, vocab), Skill.VOCABULARY).map { it.id },
        )
    }

    @Test
    fun `a card never falls back to the lesson's other nodes`() {
        // 35 shipped lessons look like this: words, and only grammar nodes. The
        // exercise rule credits the whole lesson; a card must credit nothing.
        val grammar = item("g", KnowledgeType.GRAMMAR, listOf(Skill.GRAMMAR))
        val discourse = item("d", KnowledgeType.DISCOURSE, listOf(Skill.READING))
        assertTrue(
            "a word is not evidence about the lesson's grammar",
            KnowledgeEvidence.itemsForCard(listOf(grammar, discourse), Skill.VOCABULARY).isEmpty(),
        )
    }

    @Test
    fun `a card ignores a node that merely lists its skill`() {
        // The by-skill axis is what exercises use to reach a differently-typed node.
        // For a card it selects the very nodes this rule excludes: the shipped grammar
        // nodes declare VOCABULARY in `skills`.
        val tagged = item("t", KnowledgeType.DISCOURSE, listOf(Skill.VOCABULARY))
        assertTrue(
            KnowledgeEvidence.itemsForCard(listOf(tagged), Skill.VOCABULARY).isEmpty(),
        )
        // …while the exercise rule still reaches it, so the two cannot be confused.
        assertEquals(
            listOf("t"),
            KnowledgeEvidence.itemsFor(listOf(tagged), Skill.VOCABULARY).map { it.id },
        )
    }
}
