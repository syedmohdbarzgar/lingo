package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.engine.AdaptiveExerciseSelector
import org.token.english.domain.engine.DefaultKnowledgeGraph
import org.token.english.domain.engine.DefaultMasteryProfileEngine
import org.token.english.domain.engine.DimensionAttempt
import org.token.english.domain.engine.ExerciseDimension
import org.token.english.domain.engine.PrerequisiteEngine
import org.token.english.domain.engine.RemediationEngine
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.KnowledgeType
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.MasteryDimension
import org.token.english.domain.model.MasteryProfile
import org.token.english.domain.model.ReviewResult
import org.token.english.domain.model.Skill

/**
 * The remediation engine must not re-teach a whole knowledge item when only one
 * sub-concept is weak (audit §5 — "Error isolation"). These tests pin:
 *
 *  - Weak Knowledge detection: only items below the threshold are candidates.
 *  - Error isolation: a weak child is surfaced even when its parent is strong.
 *  - Targeted remediation: exercises are ordered weakest-dimension first.
 *  - Reassessment: the verdict flips to REMEDIATED when mastery recovers.
 *  - Bounded retry: exhausted attempts escalate instead of looping forever.
 *  - Depth wiring: withDepthResolver populates the dependency depth.
 */
class RemediationEngineTest {

    private fun item(
        id: String,
        prerequisites: List<String> = emptyList(),
        lessons: List<String> = listOf("l1"),
        skills: List<Skill> = listOf(Skill.GRAMMAR),
    ) = KnowledgeItem(
        id = id,
        type = KnowledgeType.GRAMMAR,
        title = id,
        titleFa = id,
        level = LearningLevel.A1,
        prerequisites = prerequisites,
        lessonIds = lessons,
        skills = skills,
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

    private fun mc(id: String, skill: Skill? = Skill.GRAMMAR) = Exercise.MultipleChoice(
        id = id, lessonId = "l1", question = "q", questionFa = null,
        options = listOf("a", "b"), correctIndex = 0, skill = skill,
    )

    private fun fill(id: String, skill: Skill? = Skill.GRAMMAR) = Exercise.FillBlank(
        id = id, lessonId = "l1", sentence = "I ___ here.",
        accepted = listOf("am"), skill = skill,
    )

    private fun translation(id: String, skill: Skill? = Skill.GRAMMAR) = Exercise.Translation(
        id = id, lessonId = "l1", prompt = "سلام",
        accepted = listOf("hello"), skill = skill,
    )

    // --- graph: a (root) → b (depends on a), c independent root ----------------

    private val graph = DefaultKnowledgeGraph(
        listOf(item("a"), item("b", listOf("a")), item("c")),
    )
    private val engine = RemediationEngine(graph)

    private fun exercisesFor(lessonId: String, exercises: List<Exercise>): Map<String, List<Exercise>> =
        mapOf(lessonId to exercises)

    @Test
    fun `findWeakness returns the weakest item below the threshold`() {
        val states = mapOf(
            "a" to state("a", 0.3f),
            "b" to state("b", 0.6f),
        )
        engine.withDepthResolver { id -> PrerequisiteEngine(graph).depth(id) }
        val exercises = exercisesFor("l1", listOf(mc("e1")))

        val weakness = engine.findWeakness(states, exercises)!!
        assertEquals("a", weakness.itemId)
        assertEquals(0.3f, weakness.mastery, 0.0001f)
    }

    @Test
    fun `findWeakness ignores items at or above the threshold`() {
        val states = mapOf(
            "a" to state("a", 0.6f),  // exactly at DEFAULT_WEAKNESS_THRESHOLD
            "b" to state("b", 0.9f),
        )
        engine.withDepthResolver { id -> PrerequisiteEngine(graph).depth(id) }
        val exercises = exercisesFor("l1", listOf(mc("e1")))

        assertNull(engine.findWeakness(states, exercises))
    }

    @Test
    fun `findWeakness skips items with no exercises`() {
        val states = mapOf("a" to state("a", 0.2f))
        engine.withDepthResolver { id -> PrerequisiteEngine(graph).depth(id) }

        assertNull(engine.findWeakness(states, emptyMap()))
    }

    @Test
    fun `findWeakness reports blocked dependents`() {
        // a is weak, b depends on a, c is independent.
        val states = mapOf("a" to state("a", 0.2f))
        engine.withDepthResolver { id -> PrerequisiteEngine(graph).depth(id) }
        val exercises = exercisesFor("l1", listOf(mc("e1")))

        val weakness = engine.findWeakness(states, exercises)!!
        assertTrue("b depends on a, so b is a blocked dependent", weakness.blockedDependents.contains("b"))
        assertFalse("c does not depend on a", weakness.blockedDependents.contains("c"))
    }

    @Test
    fun `error isolation surfaces a weak child even when its parent is strong`() {
        // Graph: parent (root) → child. Parent mastered, child weak.
        val localGraph = DefaultKnowledgeGraph(
            listOf(item("parent"), item("child", listOf("parent"))),
        )
        val localEngine = RemediationEngine(localGraph)
        localEngine.withDepthResolver { id -> PrerequisiteEngine(localGraph).depth(id) }

        val states = mapOf(
            "child" to state("child", 0.4f),
            // parent has no state → treated as strong enough by being unreported
        )
        val exercises = exercisesFor("l1", listOf(mc("e1")))

        val weakness = localEngine.findWeakness(states, exercises)!!
        assertEquals("child", weakness.itemId)
        assertEquals(0.4f, weakness.mastery, 0.0001f)
    }

    // --- plan ---------------------------------------------------------------

    @Test
    fun `plan returns exercises that provide evidence for the target item`() {
        val exercises = listOf(mc("mc1"), fill("fill1"), translation("trans1"))
        val byLesson = exercisesFor("l1", exercises)

        val plan = engine.plan("a", byLesson, MasteryProfile())!!
        assertEquals("a", plan.targetId)
        assertEquals(exercises.toSet(), plan.exercises.toSet())
        assertTrue("plan has a Persian reason", plan.reasonFa.isNotEmpty())
        assertEquals(RemediationEngine.DEFAULT_REASSESS_THRESHOLD, plan.reassessThreshold, 0.0001f)
        assertEquals(RemediationEngine.DEFAULT_MAX_ATTEMPTS, plan.remainingAttempts)
    }

    @Test
    fun `plan orders exercises weakest-dimension first`() {
        val exercises = listOf(
            mc("mc1"),       // RECOGNITION
            fill("fill1"),   // RECALL
            translation("trans1"), // APPLICATION
        )
        val byLesson = exercisesFor("l1", exercises)

        // RECOGNITION is strong, APPLICATION is weakest → translation first.
        val profile = MasteryProfile(
            byDimension = mapOf(
                MasteryDimension.RECOGNITION to 0.9f,
                MasteryDimension.RECALL to 0.5f,
                MasteryDimension.APPLICATION to 0.2f,
            ),
        )
        val plan = engine.plan("a", byLesson, profile)!!
        assertEquals("trans1", plan.exercises.first().id)
        assertEquals("mc1", plan.exercises.last().id)
    }

    @Test
    fun `plan returns null when no exercises provide evidence`() {
        val emptyLessons = emptyMap<String, List<Exercise>>()
        assertNull(engine.plan("a", emptyLessons, MasteryProfile()))
    }

    @Test
    fun `plan exercises are a permutation — never duplicates or drops`() {
        val exercises = listOf(mc("m1"), fill("f1"), translation("t1"), mc("m2"))
        val byLesson = exercisesFor("l1", exercises)
        val plan = engine.plan("a", byLesson, MasteryProfile())!!
        assertEquals(exercises.toSet(), plan.exercises.toSet())
        assertEquals(exercises.size, plan.exercises.size)
    }

    // --- assess -------------------------------------------------------------

    @Test
    fun `assess returns REMEDIATED when mastery clears the threshold`() {
        val profile = MasteryProfile(
            byDimension = mapOf(MasteryDimension.RECOGNITION to 0.9f),
        )
        val attempts = listOf(DimensionAttempt(MasteryDimension.RECOGNITION, correct = true))
        val assessment = engine.assess("a", profile, attempts, attemptsMade = 3)

        assertEquals(RemediationEngine.Outcome.REMEDIATED, assessment.outcome)
        assertEquals(0.9f, assessment.currentMastery, 0.0001f)
        assertEquals(3, assessment.attemptsMade)
        assertTrue(assessment.reasonFa.contains("بازگشت به درس اصلی"))
    }

    @Test
    fun `assess returns NEEDS_MORE when mastery is low and attempts remain`() {
        val profile = MasteryProfile(
            byDimension = mapOf(MasteryDimension.RECALL to 0.3f),
        )
        val attempts = listOf(
            DimensionAttempt(MasteryDimension.RECALL, correct = true),
            DimensionAttempt(MasteryDimension.RECALL, correct = false),
        )
        val assessment = engine.assess("a", profile, attempts, attemptsMade = 2)

        assertEquals(RemediationEngine.Outcome.NEEDS_MORE, assessment.outcome)
        assertEquals(2, assessment.attemptsMade)
    }

    @Test
    fun `assess returns ESCALATION when attempts exhausted and mastery is still low`() {
        val profile = MasteryProfile(
            byDimension = mapOf(MasteryDimension.RECALL to 0.3f),
        )
        val attempts = List(RemediationEngine.DEFAULT_MAX_ATTEMPTS) {
            DimensionAttempt(MasteryDimension.RECALL, correct = it % 2 == 0)
        }
        val assessment = engine.assess("a", profile, attempts, attemptsMade = RemediationEngine.DEFAULT_MAX_ATTEMPTS)

        assertEquals(RemediationEngine.Outcome.ESCALATION, assessment.outcome)
        assertTrue(assessment.reasonFa.contains("مرور بیشتر"))
    }

    @Test
    fun `assess gives early attempts room when no dimension is established`() {
        // A fresh profile with one wrong attempt — not enough data yet.
        val profile = MasteryProfile()
        val attempts = listOf(DimensionAttempt(MasteryDimension.RECOGNITION, correct = false))

        // Below maxAttempts/2 → NEEDS_MORE even though mastery is 0f.
        val assessment = engine.assess("a", profile, attempts, attemptsMade = 1)
        assertEquals(RemediationEngine.Outcome.NEEDS_MORE, assessment.outcome)
    }

    @Test
    fun `assess tracks consecutive correct at the end of the attempt list`() {
        val profile = MasteryProfile(
            byDimension = mapOf(MasteryDimension.RECOGNITION to 0.9f),
        )
        val attempts = listOf(
            DimensionAttempt(MasteryDimension.RECOGNITION, correct = false),
            DimensionAttempt(MasteryDimension.RECOGNITION, correct = false),
            DimensionAttempt(MasteryDimension.RECOGNITION, correct = true),
            DimensionAttempt(MasteryDimension.RECOGNITION, correct = true),
        )
        val assessment = engine.assess("a", profile, attempts, attemptsMade = 4)
        assertEquals(2, assessment.correctInRow)
    }

    // --- assessFromAttempts -------------------------------------------------

    @Test
    fun `assessFromAttempts infers dimension from a lambda`() {
        val attempts = listOf(
            org.token.english.domain.model.ReviewAttempt(
                contentId = "a", timestamp = 0L, result = ReviewResult.GOOD,
                responseTimeMs = 0L, source = "remediation",
            ),
        )
        val dimensionOf: (org.token.english.domain.model.ReviewAttempt) -> MasteryDimension = { MasteryDimension.RECOGNITION }

        val assessment = engine.assessFromAttempts("a", attempts, dimensionOf)
        // A single correct attempt seeds recognition mastery to 1.0f (>= 0.85 threshold),
        // so the item is remediated — confirms the lambda inferred the dimension correctly.
        assertEquals(RemediationEngine.Outcome.REMEDIATED, assessment.outcome)
        assertEquals(1.0f, assessment.currentMastery, 0.0001f)
    }

    // --- depth wiring -------------------------------------------------------

    @Test
    fun `withDepthResolver populates the dependency depth field`() {
        engine.withDepthResolver { id -> PrerequisiteEngine(graph).depth(id) }
        val states = mapOf("a" to state("a", 0.2f))
        val exercises = exercisesFor("l1", listOf(mc("e1")))

        val weakness = engine.findWeakness(states, exercises)!!
        // a is a root → depth 0.
        assertEquals(0, weakness.depth)

        // b depends on a → depth 1.
        val states2 = mapOf("b" to state("b", 0.2f))
        val weakness2 = engine.findWeakness(states2, exercises)!!
        assertEquals(1, weakness2.depth)
    }

    // --- integration with AdaptiveExerciseSelector --------------------------

    @Test
    fun `plan exercises can be fed to the selector for ordering`() {
        val exercises = listOf(mc("mc1"), fill("fill1"), translation("trans1"))
        val byLesson = exercisesFor("l1", exercises)

        val plan = engine.plan("a", byLesson, MasteryProfile())!!
        val selector = AdaptiveExerciseSelector()

        // The selector re-orders within the remedial set, but all exercises survive.
        val ordered = selector.order(plan.exercises, MasteryProfile())
        assertEquals(exercises.toSet(), ordered.toSet())
    }

    // --- reasonFa with dimension -------------------------------------------

    @Test
    fun `plan reasonFa mentions the weakest dimension when established`() {
        val exercises = listOf(fill("fill1"), translation("trans1"))
        val byLesson = exercisesFor("l1", exercises)

        // APPLICATION is established and weaker than RECALL → mentioned in reason.
        val profile = MasteryProfile(
            byDimension = mapOf(
                MasteryDimension.RECALL to 0.8f,
                MasteryDimension.APPLICATION to 0.3f,
            ),
        )
        val plan = engine.plan("a", byLesson, profile)!!
        assertTrue("reason mentions the weak dimension label", plan.reasonFa.contains("کاربرد"))
    }

    @Test
    fun `plan reasonFa omits the dimension when it is not established`() {
        val exercises = listOf(fill("fill1"), translation("trans1"))
        val byLesson = exercisesFor("l1", exercises)

        // PRODUCTION is not established → dimension label should not appear.
        val profile = MasteryProfile(
            byDimension = mapOf(
                MasteryDimension.RECALL to 0.5f,
                MasteryDimension.APPLICATION to 0.5f,
            ),
        )
        val plan = engine.plan("a", byLesson, profile)!!
        assertFalse("reason does not mention unestablished dimension", plan.reasonFa.contains("تولید"))
    }
}
