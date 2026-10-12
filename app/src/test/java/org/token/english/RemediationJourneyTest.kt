package org.token.english

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.data.content.ContentParser
import org.token.english.domain.engine.DefaultKnowledgeEngine
import org.token.english.domain.engine.DefaultKnowledgeGraph
import org.token.english.domain.engine.KnowledgeEvidence
import org.token.english.domain.engine.RemediationEngine
import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.MasteryDimension
import org.token.english.domain.model.ReviewAttempt
import org.token.english.domain.model.ReviewResult
import org.token.english.domain.model.Skill
import org.token.english.domain.repository.KnowledgeRepository
import org.token.english.domain.usecase.GetFocusPlanUseCase
import org.token.english.domain.usecase.SubmitExerciseUseCase
import java.io.File

/**
 * The remediation layer, evaluated on the **real bundled curriculum** (checklist
 * v8 item 6: "evaluate RemediationEngine with real user scenarios").
 *
 * [RemediationEngineTest] and [FocusPlanTest] pin the engine's rules on hand-built
 * one-lesson graphs. This file asks the different question the checklist is really
 * about: does the engine's decision stay meaningful when it runs over the 89 real
 * knowledge nodes, the 48 real lessons and the 465 real exercises — with a learner
 * who actually answers the shipped items wrong?
 *
 * What it establishes:
 *
 *  - a learner who misses one lesson's grammar drills is offered a block for exactly
 *    that node, run in that node's own lesson, and the block holds the lesson's
 *    evidence-bearing exercises (not the whole lesson's variety);
 *  - error isolation holds on real data: a GRAMMAR round moves the grammar node and
 *    leaves the lesson's PHONOLOGY node untouched;
 *  - the block reports how much the weak node is holding back (its real dependents);
 *  - a never-studied node is taught, not drilled — no block until there is evidence;
 *  - answering the block correctly closes it, and the two thresholds involved
 *    (`GetFocusPlanUseCase`'s weakness bar vs `RemediationEngine`'s reassess bar) do
 *    not agree — see the note on the third test;
 *  - the session verdict escalates a stuck learner and clears a fixed one.
 */
class RemediationJourneyTest {

    private val now = 1_759_300_000_000L

    private fun contentFile(name: String): File {
        val candidates = listOf(
            File("src/main/assets/content/$name"),
            File("app/src/main/assets/content/$name"),
        )
        return candidates.firstOrNull { it.exists() } ?: error("content file not found: $name")
    }

    private val lessons: List<Lesson> = ContentParser.parseLessons(contentFile("lessons.json").readText())
    private val knowledgeItems: List<KnowledgeItem> =
        ContentParser.parseKnowledge(contentFile("knowledge.json").readText())
    private val exercisesJson = contentFile("exercises.json").readText()
    private val exercisesByLesson: Map<String, List<Exercise>> =
        lessons.associate { it.id to ContentParser.parseExercises(exercisesJson, it.id).map { it.second } }

    private fun newApp() = App(lessons, knowledgeItems, exercisesByLesson)

    // ------------------------------------------------------------------ fakes

    /**
     * The knowledge repository on top of the production [DefaultKnowledgeEngine], so
     * a graded answer moves the real learner model (first miss seeds 0, then EWMA) —
     * exactly like the Room-backed implementation, minus Room.
     */
    private class JourneyKnowledge(private val items: List<KnowledgeItem>) : KnowledgeRepository {
        private val statesFlow = MutableStateFlow<List<KnowledgeState>>(emptyList())
        private val engine = DefaultKnowledgeEngine()

        override fun observeStates(): Flow<List<KnowledgeState>> = statesFlow
        override fun observePractisedCount(): Flow<Int> = statesFlow.map { it.size }
        override suspend fun getState(itemId: String): KnowledgeState? =
            statesFlow.value.firstOrNull { it.itemId == itemId }

        override suspend fun itemsForLesson(lessonId: String): List<KnowledgeItem> =
            items.filter { lessonId in it.lessonIds }

        override suspend fun allItems(): List<KnowledgeItem> = items

        override suspend fun recordAttempt(lessonId: String, skill: Skill, correct: Boolean, now: Long) {
            val lessonItems = itemsForLesson(lessonId)
            val byId = statesFlow.value.associateBy { it.itemId }
            KnowledgeEvidence.itemsFor(lessonItems, skill).forEach { item ->
                val updated = engine.applyAttempt(item.id, byId[item.id], correct, now)
                statesFlow.value = statesFlow.value.filterNot { it.itemId == item.id } + updated
            }
        }

        override suspend fun reset() {
            statesFlow.value = emptyList()
        }
    }

    /** One wired-up app slice: real content in, the production use cases on top. */
    private class App(
        allLessons: List<Lesson>,
        allKnowledge: List<KnowledgeItem>,
        allExercises: Map<String, List<Exercise>>,
    ) {
        val knowledge = JourneyKnowledge(allKnowledge)
        val lessons = FakeLessonRepository(allLessons).apply {
            allExercises.forEach { (lessonId, list) -> exercises[lessonId] = list }
        }
        val submit = SubmitExerciseUseCase(FakeProgressRepository(), knowledge)
        val focusPlan = GetFocusPlanUseCase(knowledge, lessons)
    }

    // ------------------------------------------------------------------ helpers

    /** A genuinely wrong answer for any shipped exercise type. */
    private fun wrongAnswer(exercise: Exercise): String = when (exercise) {
        is Exercise.MultipleChoice -> exercise.options.withIndex()
            .first { it.index != exercise.correctIndex && it.value.isNotBlank() }.value
        is Exercise.FillBlank -> "zzz-wrong"
        is Exercise.WordRecall -> "zzz-wrong"
        is Exercise.Translation -> "zzz-wrong"
        is Exercise.SentenceProduction -> "zzz-wrong"
        is Exercise.Listening -> "zzz-wrong"
        is Exercise.Speaking -> "zzz-wrong"
    }

    private fun correctAnswer(exercise: Exercise): String = when (exercise) {
        is Exercise.MultipleChoice -> exercise.options[exercise.correctIndex]
        is Exercise.FillBlank -> exercise.accepted.first()
        is Exercise.WordRecall -> exercise.accepted.first()
        is Exercise.Translation -> exercise.accepted.first()
        is Exercise.SentenceProduction -> exercise.accepted.first()
        is Exercise.Listening -> exercise.accepted.first()
        is Exercise.Speaking -> exercise.referenceAnswers.first()
    }

    private fun titleFaOf(itemId: String): String = knowledgeItems.first { it.id == itemId }.titleFa

    /** The lesson's exercises that a GRAMMAR round actually grades as grammar. */
    private fun grammarExercises(lessonId: String): List<Exercise> =
        exercisesByLesson.getValue(lessonId).filter { AnswerChecker.skillOf(it) == Skill.GRAMMAR }

    // ------------------------------------------------------------------ scenarios

    @Test
    fun `a real grammar weakness is isolated to one node and drilled in its own lesson`() = runTest {
        val app = newApp()
        val lessonId = "a1.articles.lesson-01"
        val nodeId = "grammar.articles-a-an-the"

        val drills = grammarExercises(lessonId)
        assertTrue("the lesson ships the A-8 grammar-practice floor", drills.size >= 6)

        // A real learner round: every grammar item in the lesson answered wrong.
        drills.forEachIndexed { index, exercise ->
            val outcome = app.submit(exercise, wrongAnswer(exercise), now + index)
            assertFalse("the answer is graded wrong", outcome.correct)
        }

        val state = app.knowledge.getState(nodeId)
        assertNotNull("the misses reached the real learner model", state)
        assertEquals("a first miss seeds the node at zero", 0f, state!!.mastery, 0.0001f)
        assertEquals("one exposure per graded item", drills.size, state.exposureCount)
        assertTrue(
            "and it sits below the engine's weakness threshold",
            state.mastery < RemediationEngine.DEFAULT_WEAKNESS_THRESHOLD,
        )

        // Error isolation on real content: the lesson also teaches a PHONOLOGY node,
        // which only listens for LISTENING evidence — grammar misses must not move it.
        assertNull(
            "a grammar round does not touch the lesson's pronunciation node",
            app.knowledge.getState("phonology.vowel-length"),
        )

        val plan = app.focusPlan()
        assertNotNull("the engine offers a block for the weak node", plan)
        assertEquals(nodeId, plan!!.itemId)
        assertEquals("the block runs in the lesson that teaches the node", lessonId, plan.lessonId)
        assertEquals(0f, plan.mastery, 0.0001f)
        assertTrue("the engine explains why in Persian", plan.reasonFa.contains(titleFaOf(nodeId)))

        // The block holds the lesson's evidence-bearing exercises — each of them, so the
        // learner drills the point rather than one arbitrary item.
        val drilledIds = drills.map { it.id }.toSet()
        assertTrue("every grammar item of the lesson is drilled", plan.exerciseIds.containsAll(drilledIds))
        assertTrue(
            "and nothing outside the lesson leaks in",
            plan.exerciseIds.all { it.startsWith("a1.articles.ex.") },
        )
        // An exercise that authors no skill counts as evidence for every node (the
        // documented `skill ?: return true` rule in `plan()`), so the lesson's untagged
        // listening item joins the grammar block. Pinned here so the behaviour is a
        // decision, not an accident.
        assertTrue(
            "an untagged exercise counts as evidence for every node",
            "a1.articles.ex.07" in plan.exerciseIds,
        )

        assertEquals(RemediationEngine.DEFAULT_REASSESS_THRESHOLD, plan.reassessThreshold, 0.0001f)
        assertEquals(RemediationEngine.DEFAULT_MAX_ATTEMPTS, plan.remainingAttempts)
        assertEquals("the articles node blocks nothing behind it", 0, plan.blockedCount)
    }

    @Test
    fun `a node the learner never studied is taught, not drilled`() = runTest {
        val app = newApp()

        assertNull("nothing is weak before there is any evidence", app.focusPlan())
        assertNull(
            "naming a node with no state yields no block either",
            app.focusPlan("grammar.articles-a-an-the"),
        )
    }

    @Test
    fun `a foundational node's block reports what it is holding back`() = runTest {
        val app = newApp()
        val lessonId = "a1.daily-routine.lesson-01"

        val drills = grammarExercises(lessonId)
        drills.forEachIndexed { index, exercise ->
            app.submit(exercise, wrongAnswer(exercise), now + index)
        }

        // The lesson teaches two grammar points and a GRAMMAR round moves both, so both
        // are weak — but each keeps its own block and its own blocked-dependents count.
        val presentSimple = app.knowledge.getState("grammar.present-simple-affirmative")
        val thirdPerson = app.knowledge.getState("grammar.third-person-s")
        assertNotNull(presentSimple)
        assertNotNull(thirdPerson)
        assertTrue(presentSimple!!.mastery < RemediationEngine.DEFAULT_WEAKNESS_THRESHOLD)
        assertTrue(thirdPerson!!.mastery < RemediationEngine.DEFAULT_WEAKNESS_THRESHOLD)

        val foundational = app.focusPlan("grammar.present-simple-affirmative")
        assertNotNull(foundational)
        assertEquals(
            "Present Simple is a prerequisite of thirteen other nodes",
            13,
            foundational!!.blockedCount,
        )
        assertEquals(lessonId, foundational.lessonId)

        val leaf = app.focusPlan("grammar.third-person-s")
        assertNotNull(leaf)
        assertEquals("Third Person -s holds back two", 2, leaf!!.blockedCount)

        // With no target the weakest node wins; on a real tie (both at zero) the choice
        // is deterministic because the graph sorts by id.
        val isolated = app.focusPlan()
        assertEquals("grammar.present-simple-affirmative", isolated!!.itemId)
        assertEquals(lessonId, isolated.lessonId)
    }

    @Test
    fun `correct practice clears the block, but the two thresholds do not agree`() = runTest {
        val app = newApp()
        val lessonId = "a1.articles.lesson-01"
        val nodeId = "grammar.articles-a-an-the"
        val drills = grammarExercises(lessonId)

        drills.forEachIndexed { index, exercise ->
            app.submit(exercise, wrongAnswer(exercise), now + index)
        }
        assertNotNull("the block exists to begin with", app.focusPlan())

        // Remediate by answering the block's own items correctly, one item per attempt.
        var answered = 0
        while (app.focusPlan() != null && answered < 40) {
            val exercise = drills[answered % drills.size]
            app.submit(exercise, correctAnswer(exercise), now + 10_000 + answered)
            answered++
        }

        val mastery = app.knowledge.getState(nodeId)!!.mastery
        assertTrue("a handful of correct answers is enough", answered in 1..10)
        assertTrue(
            "the node crossed the weakness threshold the plan watches ($mastery)",
            mastery >= RemediationEngine.DEFAULT_WEAKNESS_THRESHOLD,
        )
        assertTrue(
            "…yet it is still under the engine's own reassess bar ($mastery)",
            mastery < RemediationEngine.DEFAULT_REASSESS_THRESHOLD,
        )
        assertNull("the block is gone", app.focusPlan())
        assertNull("and naming the node no longer resurrects it", app.focusPlan(nodeId))

        // Evaluation note (checklist v8 item 6): the two bars are not the same bar.
        // `GetFocusPlanUseCase` drops the block at DEFAULT_WEAKNESS_THRESHOLD (0.5,
        // the node's EWMA mastery), while `RemediationEngine.assess` only calls the
        // node REMEDIATED at DEFAULT_REASSESS_THRESHOLD (0.85, the *session* profile's
        // mean — a separate model that never sees the node's stored mastery). So a
        // learner can be handed back to the curriculum at 0.59 while the engine would
        // still say NEEDS_MORE for the same evidence. Demonstrating that divergence on
        // the real graph is the point of this assertion; the two numbers are asserted
        // to differ so a future change that unifies them is caught here.
        assertTrue(RemediationEngine.DEFAULT_WEAKNESS_THRESHOLD < RemediationEngine.DEFAULT_REASSESS_THRESHOLD)
    }

    @Test
    fun `the session verdict escalates a stuck learner and clears a fixed one`() = runTest {
        val engine = RemediationEngine(DefaultKnowledgeGraph(knowledgeItems))
        val nodeId = "grammar.present-simple-affirmative"

        fun attempt(correct: Boolean) = ReviewAttempt(
            contentId = nodeId,
            timestamp = now,
            result = if (correct) ReviewResult.GOOD else ReviewResult.AGAIN,
            responseTimeMs = 800,
            source = "remediation",
        )

        val dimensionOf = { _: ReviewAttempt -> MasteryDimension.RECOGNITION }

        // Six misses with no correct answer: the cap is reached while still weak.
        val stuck = engine.assessFromAttempts(nodeId, List(6) { attempt(false) }, dimensionOf)
        assertEquals(RemediationEngine.Outcome.ESCALATION, stuck.outcome)
        assertEquals(0, stuck.correctInRow)
        assertEquals(6, stuck.attemptsMade)
        assertTrue("the escalation tells the learner what to do", stuck.reasonFa.isNotBlank())

        // A half-way run keeps going: four misses then one hit is 0.2 — under the bar
        // and under the cap.
        val halfway = engine.assessFromAttempts(
            nodeId,
            List(4) { attempt(false) } + listOf(attempt(true)),
            dimensionOf,
        )
        assertEquals(RemediationEngine.Outcome.NEEDS_MORE, halfway.outcome)
        assertTrue(halfway.currentMastery < RemediationEngine.DEFAULT_REASSESS_THRESHOLD)

        // A hit at the reassess bar clears it. Note the seeding: the session profile is
        // built from the session's own evidence and seeds an untouched dimension at 1f
        // on the first correct answer, so a session whose *first* answer is correct is
        // remediated immediately regardless of how weak the stored node was — the
        // second half of the divergence noted above.
        val fixed = engine.assessFromAttempts(nodeId, listOf(attempt(true)), dimensionOf)
        assertEquals(RemediationEngine.Outcome.REMEDIATED, fixed.outcome)
        assertEquals(1, fixed.correctInRow)
    }
}
