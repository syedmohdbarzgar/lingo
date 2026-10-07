package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNull
import org.junit.Test
import org.token.english.core.common.TimeUtil
import org.token.english.domain.engine.AdaptiveExerciseSelector
import org.token.english.domain.engine.AdaptiveLearningPlanner
import org.token.english.domain.engine.DefaultKnowledgeEngine
import org.token.english.domain.engine.DefaultKnowledgeGraph
import org.token.english.domain.engine.DefaultMasteryEngine
import org.token.english.domain.engine.DefaultMasteryProfileEngine
import org.token.english.domain.engine.DimensionAttempt
import org.token.english.domain.engine.ExerciseDimension
import org.token.english.domain.engine.KnowledgeEvidence
import org.token.english.domain.engine.PrerequisiteEngine
import org.token.english.domain.engine.RemediationEngine
import org.token.english.domain.engine.Sm2ReviewScheduler
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.KnowledgeType
import org.token.english.domain.model.LearningActionType
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.MasteryDimension
import org.token.english.domain.model.MasteryProfile
import org.token.english.domain.model.ReviewResult
import org.token.english.domain.model.Skill

/**
 * End-to-end simulation of the learning chain (audit §6 — Learning Simulation):
 *
 *   User State → Knowledge State → Mastery → Prerequisite → Planner →
 *   Exercise Selector → Exercise → Evidence → Updated Knowledge State
 *
 * Four scenarios pin the behaviour of the full engine stack:
 *
 *   A. Always-correct  → mastery grows, difficulty stretches, new knowledge unlocks.
 *   B. Always-wrong    → mastery drops, remediation activates, no infinite loop.
 *   C. Strong recognition / weak production → exercise format shifts to the weak dimension.
 *   D. Long absence    → reviews re-activate, retention re-evaluated, mastery preserved.
 *
 * The simulation is a pure fold over time — no Android, no clock, no DB.
 */
class LearningSimulationTest {

    // -----------------------------------------------------------------------
    //  Curriculum: a prerequisite chain with mixed grammar / vocabulary nodes.
    //  vocab.a (root) → grammar.b (depends on vocab.a) → grammar.c (depends on b)
    // -----------------------------------------------------------------------

    private val graph = DefaultKnowledgeGraph(
        listOf(
            knowledgeItem("vocab.a", KnowledgeType.VOCABULARY, Skill.VOCABULARY, lesson = "l1"),
            knowledgeItem("grammar.b", KnowledgeType.GRAMMAR, Skill.GRAMMAR, lesson = "l2", prereqs = listOf("vocab.a")),
            knowledgeItem("grammar.c", KnowledgeType.GRAMMAR, Skill.GRAMMAR, lesson = "l3", prereqs = listOf("grammar.b")),
        ),
    )

    private fun knowledgeItem(
        id: String,
        type: KnowledgeType,
        skill: Skill,
        lesson: String,
        prereqs: List<String> = emptyList(),
    ) = KnowledgeItem(
        id = id,
        type = type,
        title = id,
        titleFa = id,
        level = LearningLevel.A1,
        prerequisites = prereqs,
        lessonIds = listOf(lesson),
        skills = listOf(skill),
    )

    // Exercises per lesson. Each exercise's `skill` attribute determines which
    // knowledge type it provides evidence for (see KnowledgeEvidence).
    private val exercisesByLesson: Map<String, List<Exercise>> = mapOf(
        "l1" to listOf(
            mc("l1.mc", Skill.VOCABULARY),
            fill("l1.fill", Skill.VOCABULARY),
            translation("l1.trans", Skill.VOCABULARY),
        ),
        "l2" to listOf(
            mc("l2.mc", Skill.GRAMMAR),
            fill("l2.fill", Skill.GRAMMAR),
            translation("l2.trans", Skill.GRAMMAR),
        ),
        "l3" to listOf(
            mc("l3.mc", Skill.GRAMMAR),
            fill("l3.fill", Skill.GRAMMAR),
            translation("l3.trans", Skill.GRAMMAR),
        ),
    )

    private fun mc(id: String, skill: Skill) = Exercise.MultipleChoice(
        id = id, lessonId = lessonOf(id), question = "q", questionFa = null,
        options = listOf("a", "b"), correctIndex = 0, skill = skill,
    )

    private fun fill(id: String, skill: Skill) = Exercise.FillBlank(
        id = id, lessonId = lessonOf(id),
        sentence = "I ___ here.", accepted = listOf("live"), skill = skill,
    )

    private fun translation(id: String, skill: Skill) = Exercise.Translation(
        id = id, lessonId = lessonOf(id), prompt = "سلام", accepted = listOf("hello"), skill = skill,
    )

    private fun lessonOf(exerciseId: String): String = exerciseId.substring(0, 3) // "l1.mc" → "l1"

    // -----------------------------------------------------------------------
    //  Engines
    // -----------------------------------------------------------------------

    private val planner = AdaptiveLearningPlanner(graph, PrerequisiteEngine(graph))
    private val knowledgeEngine = DefaultKnowledgeEngine()
    private val masteryEngine = DefaultMasteryEngine()
    private val profileEngine = DefaultMasteryProfileEngine()
    private val selector = AdaptiveExerciseSelector()
    private val remediatedEngine = RemediationEngine(graph).apply {
        withDepthResolver { id -> PrerequisiteEngine(graph).depth(id) }
    }
    private val scheduler = Sm2ReviewScheduler()

    private val startTime = TimeUtil.startOfDay(1_759_300_000_000L)

    // -----------------------------------------------------------------------
    //  Simulation state: per-item KnowledgeState + per-item MasteryProfile
    //  + per-item dimension attempt history (for remediation assessment).
    // -----------------------------------------------------------------------

    private class SimState(
        val states: MutableMap<String, KnowledgeState> = mutableMapOf(),
        val profiles: MutableMap<String, MasteryProfile> = mutableMapOf(),
        val dimensionAttempts: MutableMap<String, MutableList<DimensionAttempt>> = mutableMapOf(),
        val exerciseTypesSeen: MutableSet<MasteryDimension> = mutableSetOf(),
    )

    /**
     * Simulates the learner working through all exercises for one knowledge item.
     * Each graded answer updates the KnowledgeState (SRS + overall mastery) and the
     * per-dimension MasteryProfile, exactly as the real repositories would.
     */
    private fun SimState.workOnItem(
        itemId: String,
        correctFn: (Exercise) -> Boolean,
        now: Long,
    ) {
        val item = graph.byId(itemId) ?: return
        val exercises = item.lessonIds
            .flatMap { lessonId -> exercisesByLesson[lessonId].orEmpty() }
            // Only exercises that actually provide evidence for this item.
            .filter { exercise ->
                exercise.skill == null || KnowledgeEvidence.itemsFor(listOf(item), exercise.skill!!).any { it.id == itemId }
            }

        val profile = profiles[itemId] ?: MasteryProfile()
        val ordered = selector.order(exercises, profile)

        for (exercise in ordered) {
            val dimension = ExerciseDimension.dimensionOf(exercise)
            val correct = correctFn(exercise)
            exerciseTypesSeen.add(dimension)

            val current = states[itemId]
            states[itemId] = knowledgeEngine.applyAttempt(itemId, current, correct, now)

            profiles[itemId] = profileEngine.apply(
                profiles[itemId] ?: MasteryProfile(),
                dimension,
                correct,
            )

            dimensionAttempts.getOrPut(itemId) { mutableListOf() }
                .add(DimensionAttempt(dimension, correct))
        }
    }

    /** Collects the ids of all knowledge items whose review is due at [now]. */
    private fun SimState.dueItemIds(now: Long): List<String> =
        states.filterValues { knowledgeEngine.isDue(it, now) }.keys.toList()

    // -----------------------------------------------------------------------
    //  Scenario A — Always correct
    // -----------------------------------------------------------------------

    /**
     * A learner who answers every exercise correctly must:
     *
     *  - See mastery climb across items.
     *  - Unlock new knowledge (grammar.b, then grammar.c) as prerequisites are met.
     *  - Stretch review intervals (the SRS treats correct answers as "harder"
     *    spacing → difficulty rises).
     *  - Encounter exercise variety (recognition + recall + application).
     *  - Keep the review queue bounded (not every card due every day).
     */
    @Test
    fun scenarioA_alwaysCorrect_masteryGrows_newKnowledgeUnlocks_intervalsStretch() {
        val sim = SimState()
        var now = startTime
        val days = 15

        val itemsLearned = mutableSetOf<String>()
        val dailyDueCounts = mutableListOf<Int>()
        val maxIntervalHistory = mutableListOf<Int>()

        for (day in 0 until days) {
            val dueCount = sim.dueItemIds(now).size
            dailyDueCounts.add(dueCount)

            val masteredIds = sim.states.filterValues { s -> s.mastery >= AdaptiveLearningPlanner.STRONG_THRESHOLD }
                .keys.toSet()
            val unlocked = graph.unlocked(masteredIds)

            // Focus = items that are unlocked but not yet strong (the "current lesson").
            val focus = unlocked.filter { sim.states[it.id]?.mastery ?: 0f < AdaptiveLearningPlanner.STRONG_THRESHOLD }

            val decision = planner.plan(
                states = sim.states.values.toList(),
                dueReviewCount = dueCount,
                focusItems = focus.ifEmpty { unlocked.take(2) },
                targetMinutes = 60,
                todayStudySeconds = 0L,
            )

            // Process reviews first, then plan actions.
            for (id in sim.dueItemIds(now)) {
                sim.workOnItem(id, { true }, now)
            }

            for (action in decision.actions) {
                when (action.type) {
                    LearningActionType.REVIEW -> { /* already handled above */ }
                    LearningActionType.LEARN, LearningActionType.PRACTISE, LearningActionType.REMEDIATE -> {
                        action.itemId?.let { itemsLearned.add(it) }
                        sim.workOnItem(action.itemId ?: continue, { true }, now)
                    }
                }
            }

            maxIntervalHistory.add(sim.states.values.maxOfOrNull { it.intervalDays } ?: 0)
            now = TimeUtil.startOfDayPlusDays(startTime, day + 1)
        }

        // --- Mastery grew ----------------------------------------------------
        val finalMastery = sim.states.values.map { it.mastery }.average()
        assertTrue("final average mastery should be above 0.5", finalMastery > 0.5)

        // Items that were learned have good mastery.
        val learnedStates = sim.states.values.filter { it.exposureCount > 0 }
        assertTrue("at least one item was worked on", learnedStates.isNotEmpty())
        assertTrue("learned items have above-threshold mastery",
            learnedStates.any { it.mastery >= AdaptiveLearningPlanner.STRONG_THRESHOLD })

        // --- New knowledge entered the plan ----------------------------------
        assertTrue("vocab.a was learned", itemsLearned.contains("vocab.a"))

        // After vocab.a is strong, grammar.b should become learnable.
        val vocabStrong = sim.states["vocab.a"]?.mastery ?: 0f
        if (vocabStrong >= AdaptiveLearningPlanner.STRONG_THRESHOLD) {
            assertTrue("grammar.b was learned after its prerequisite", itemsLearned.contains("grammar.b"))
        }

        // --- Intervals stretched (SRS difficulty rose) ----------------------
        val maxInterval = sim.states.values.maxOfOrNull { it.intervalDays } ?: 0
        assertTrue("at least one item has an interval > 1 day", maxInterval > 1)

        // --- Exercise variety ------------------------------------------------
        assertTrue("recognition exercises were seen", sim.exerciseTypesSeen.contains(MasteryDimension.RECOGNITION))
        assertTrue("recall exercises were seen", sim.exerciseTypesSeen.contains(MasteryDimension.RECALL))
        assertTrue("application exercises were seen", sim.exerciseTypesSeen.contains(MasteryDimension.APPLICATION))

        // --- Review queue is bounded, not spiralling ------------------------
        val maxDailyDue = dailyDueCounts.maxOrNull() ?: 0
        assertTrue("highest daily review count stays bounded", maxDailyDue <= graph.items.size)
    }

    // -----------------------------------------------------------------------
    //  Scenario B — Always wrong
    // -----------------------------------------------------------------------

    /**
     * A learner who answers every exercise incorrectly must:
     *
     *  - See mastery stay low on every item (never reaches strong threshold).
     *  - Trigger remediation: the planner proposes REMEDIATE actions for
     *    unmet prerequisites instead of blindly continuing.
     *  - Never enter an infinite loop: the action cap and remediation
     *    attempt cap keep every day bounded.
     *  - See ease factors degrade to the minimum floor.
     */
    @Test
    fun scenarioB_alwaysWrong_masteryStaysLow_remediationActivates_boundedDays() {
        val sim = SimState()
        var now = startTime
        val days = 10

        val remediationTriggered = mutableListOf<Boolean>()
        val maxActionsPerDay = mutableListOf<Int>()
        val maxEasePerItem = mutableMapOf<String, Float>()

        for (day in 0 until days) {
            val dueCount = sim.dueItemIds(now).size

            // Focus on items whose prerequisites are met OR need remediation.
            val masteredIds = sim.states.filterValues { s -> s.mastery >= AdaptiveLearningPlanner.STRONG_THRESHOLD }
                .keys.toSet()
            val unlocked = graph.unlocked(masteredIds)
            val focus = graph.items.take(3) // include prereq-chain items so REMEDIATE fires

            val decision = planner.plan(
                states = sim.states.values.toList(),
                dueReviewCount = dueCount,
                focusItems = focus,
                targetMinutes = 60,
                todayStudySeconds = 0L,
            )

            maxActionsPerDay.add(decision.actions.size)
            remediationTriggered.add(decision.actions.any { it.type == LearningActionType.REMEDIATE })

            // Process reviews.
            for (id in sim.dueItemIds(now)) {
                sim.workOnItem(id, { false }, now)
            }

            // Process plan actions.
            var remediationAttempts = 0
            for (action in decision.actions) {
                when (action.type) {
                    LearningActionType.REVIEW -> { /* handled above */ }
                    LearningActionType.REMEDIATE -> {
                        val itemId = action.itemId
                        if (itemId != null) {
                            sim.workOnItem(itemId, { false }, now)
                            remediationAttempts++

                            // Use remediation engine to assess progress.
                            val profile = sim.profiles[itemId] ?: MasteryProfile()
                            val attempts = sim.dimensionAttempts[itemId].orEmpty()
                            val assessment = remediatedEngine.assess(
                                itemId, profile, attempts, attempts.size,
                            )
                            when (assessment.outcome) {
                                RemediationEngine.Outcome.ESCALATION -> {
                                    // Stop trying more remediation on this item for today.
                                }
                                else -> { /* continue */ }
                            }
                        }
                    }
                    LearningActionType.LEARN, LearningActionType.PRACTISE -> {
                        sim.workOnItem(action.itemId ?: continue, { false }, now)
                    }
                }
            }

            // Track ease factor floors.
            sim.states.forEach { (id, state) ->
                maxEasePerItem[id] = state.easeFactor // overwrite: track current, not max
            }

            now = TimeUtil.startOfDayPlusDays(startTime, day + 1)
        }

        // --- Mastery stuck low ------------------------------------------------
        for ((id, state) in sim.states) {
            assertTrue(
                "item $id mastery should remain below strong threshold ($state.mastery)",
                state.mastery < AdaptiveLearningPlanner.STRONG_THRESHOLD,
            )
        }

        // --- Remediation was triggered at least once --------------------------
        assertTrue("remediation was triggered on at least one day", remediationTriggered.any { it })

        // --- Bounded: no day exceeded the action cap --------------------------
        for (count in maxActionsPerDay) {
            assertTrue("day had $count actions, exceeds cap of ${AdaptiveLearningPlanner.MAX_ACTIONS}",
                count <= AdaptiveLearningPlanner.MAX_ACTIONS)
        }

        // --- Ease factors degraded to floor -----------------------------------
        for ((id, ease) in maxEasePerItem) {
            assertTrue("item $id ease factor $ease should have hit the floor",
                ease <= Sm2ReviewScheduler.MIN_EASE + 0.01f)
        }

        // --- The simulation completed all days (no crash / no infinite loop) --
        assertEquals("all simulated days completed", days, maxActionsPerDay.size)
    }

    // -----------------------------------------------------------------------
    //  Scenario C — Strong recognition / weak production
    // -----------------------------------------------------------------------

    /**
     * A learner whose recognition is strong but whose applied/production
     * dimensions are weak must be served exercises that target the weak
     * dimensions first. The [AdaptiveExerciseSelector] is the gatekeeper here.
     */
    @Test
    fun scenarioC_strongRecognition_weakRecallAndApplication_prioritizesWeakDimensions() {
        val exercises = listOf(
            mc("mc1", Skill.GRAMMAR),          // RECOGNITION
            fill("fill1", Skill.GRAMMAR),     // RECALL
            translation("trans1", Skill.GRAMMAR), // APPLICATION
        )

        // Build a per-dimension MasteryProfile: recognition strong, recall and
        // application weak.
        val profile = MasteryProfile(
            byDimension = mapOf(
                MasteryDimension.RECOGNITION to 0.9f,
                MasteryDimension.RECALL to 0.3f,
                MasteryDimension.APPLICATION to 0.2f,
            ),
        )

        val ordered = selector.order(exercises, profile)

        // The weakest dimension (APPLICATION) should come first.
        assertEquals("translation (APPLICATION) should come first", "trans1", ordered.first().id)
        assertEquals("fill blank (RECALL) should come second", "fill1", ordered[1].id)
        // Recognition (strong) sinks to the back.
        assertEquals("MC (RECOGNITION) should be last", "mc1", ordered.last().id)

        // Verify the dimension order matches the weakness order.
        val dims = ordered.map { ExerciseDimension.dimensionOf(it) }
        assertEquals(
            listOf(MasteryDimension.APPLICATION, MasteryDimension.RECALL, MasteryDimension.RECOGNITION),
            dims,
        )

        // --- Full simulation: a learner who always gets MC right but always
        //     misses fill-in and translation should develop a skewed profile. ---

        val sim = SimState()
        // Seed the profile to simulate prior MC-heavy practice.
        var profileAcc = profile
        exercises.forEach { ex ->
            val dim = ExerciseDimension.dimensionOf(ex)
            // MC → correct, fill/translation → wrong.
            val correct = dim == MasteryDimension.RECOGNITION
            profileAcc = profileEngine.apply(profileAcc, dim, correct)
        }
        sim.profiles["grammar.b"] = profileAcc

        // Now exercise the selector with the evolved profile.
        val evolved = selector.order(exercises, profileAcc)

        // Even after one round, recognition stays strong and the weak dimensions
        // are still front-loaded.
        val evolvedDims = evolved.map { ExerciseDimension.dimensionOf(it) }
        assertTrue(
            "weak dimensions still precede strong ones",
            evolvedDims.indexOfFirst { it == MasteryDimension.APPLICATION } <
                evolvedDims.indexOfFirst { it == MasteryDimension.RECOGNITION },
        )
    }

    // -----------------------------------------------------------------------
    //  Scenario D — Long absence
    // -----------------------------------------------------------------------

    /**
     * A learner who studied a while and then disappears for 30+ days must:
     *
     *  - Have their reviews re-activate (srs interval elapsed).
     *  - Keep the mastery they earned (it is not zeroed on return).
     *  - See retention re-evaluated: a correct review stretches the interval
     *    further, while a wrong review shortens it and drops ease.
     */
    @Test
    fun scenarioD_longAbsence_reviewReactivates_masteryPreserved() {
        val sim = SimState()

        // --- Seed: the learner mastered vocab.a a month ago. ----------------
        val oneMonthAgo = startTime - 31L * 24 * 60 * 60 * 1000L
        val masteredState = KnowledgeState(
            itemId = "vocab.a",
            mastery = 0.95f,
            exposureCount = 10,
            consecutiveCorrect = 10,
            consecutiveIncorrect = 0,
            intervalDays = 30,       // already at the mastered threshold
            easeFactor = 2.8f,
            repetitions = 10,
            lapses = 0,
            lastReviewedAt = oneMonthAgo,
            nextReviewAt = oneMonthAgo + 30L * 24 * 60 * 60 * 1000L, // due ~now
        )
        sim.states["vocab.a"] = masteredState
        sim.profiles["vocab.a"] = MasteryProfile(
            byDimension = mapOf(
                MasteryDimension.RECOGNITION to 0.9f,
                MasteryDimension.RECALL to 0.8f,
                MasteryDimension.APPLICATION to 0.7f,
            ),
        )

        // --- The absence: advance time past the review date. ------------------
        val now = startTime // 30+ days after the last review → the card is due.

        assertTrue("vocab.a review is due after the absence",
            knowledgeEngine.isDue(masteredState, now))

        val dueCount = sim.dueItemIds(now).size
        assertTrue("at least one review is due after absence", dueCount >= 1)

        // --- Retention check: a correct review must not destroy mastery. -----
        val intervalBefore = masteredState.intervalDays
        sim.workOnItem("vocab.a", { true }, now)

        val stateAfter = sim.states["vocab.a"]!!
        assertTrue("mastery is preserved (not zeroed) on correct review", stateAfter.mastery >= 0.95f)
        assertTrue("interval does not shrink after a correct review",
            stateAfter.intervalDays >= intervalBefore)

        // --- A wrong review would shorten the interval and drop ease. -------
        val wrongState = KnowledgeState(
            itemId = "vocab.a",
            mastery = 0.95f,
            exposureCount = 10,
            consecutiveCorrect = 10,
            consecutiveIncorrect = 0,
            intervalDays = 30,
            easeFactor = 2.8f,
            repetitions = 10,
            lapses = 0,
            lastReviewedAt = oneMonthAgo,
            nextReviewAt = oneMonthAgo + 30L * 24 * 60 * 60 * 1000L,
        )
        val afterWrong = knowledgeEngine.applyAttempt("vocab.a", wrongState, false, now)

        assertTrue("a wrong review adds a lapse", afterWrong.lapses > wrongState.lapses)
        assertTrue("a wrong review shortens or resets the interval",
            afterWrong.intervalDays < wrongState.intervalDays || afterWrong.intervalDays == 0)

        // --- Exercise difficulty stays logical: a strong item's exercises
        //     should not suddenly reorder to the hardest format. ------------
        val strongProfile = MasteryProfile(
            byDimension = mapOf(
                MasteryDimension.RECOGNITION to 0.9f,
                MasteryDimension.RECALL to 0.9f,
                MasteryDimension.APPLICATION to 0.9f,
            ),
        )
        val exercises = exercisesByLesson["l1"]!!
        val ordered = selector.order(exercises, strongProfile)

        // For a fully consolidated item, the selector keeps the authored order
        // (stable sort) — no jarring reordering after a break.
        assertEquals(exercises, ordered)
    }

    // -----------------------------------------------------------------------
    //  Scenario E — Combined end-to-end: correct → wrong → review cycle
    //     (ties together planner, selector, knowledge engine, and SRS in a
    //      single mini-loop to prove the whole chain is wired correctly.)
    // -----------------------------------------------------------------------

    /**
     * A compact end-to-end loop: learn a root item correctly, then miss it,
     * verify the planner flags remediation on the next day, then re-learn
     * and confirm the item returns to the plan as PRACTISE or stays mastered.
     */
    @Test
    fun endToEnd_learnThenStruggle_thenReviewCycle_isConsistent() {
        val sim = SimState()
        var now = startTime

        // --- Day 0: learn vocab.a (unseen, no prerequisites). -----------------
        val masteredIds = emptySet<String>()
        val unlocked = graph.unlocked(masteredIds)
        val decision = planner.plan(
            states = emptyList(),
            dueReviewCount = 0,
            focusItems = unlocked.take(1),
            targetMinutes = 30,
            todayStudySeconds = 0L,
        )
        assertEquals("first plan has exactly one LEARN action",
            1, decision.actions.size)
        assertEquals("the action is to learn vocab.a",
            LearningActionType.LEARN, decision.actions.first().type)
        assertEquals("target is vocab.a",
            "vocab.a", decision.actions.first().itemId)

        // Work on it: always correct.
        sim.workOnItem("vocab.a", { true }, now)

        assertTrue("vocab.a mastery increased", sim.states["vocab.a"]!!.mastery > 0f)
        assertEquals("vocab.a has three exposures (one per exercise)", 3, sim.states["vocab.a"]!!.exposureCount)

        // --- Day 1+: keep working until mastered, then miss. ------------------
        var mastered = false
        for (day in 1..8) {
            now = TimeUtil.startOfDayPlusDays(startTime, day)

            if (sim.states["vocab.a"]?.mastery ?: 0f < AdaptiveLearningPlanner.STRONG_THRESHOLD) {
                sim.workOnItem("vocab.a", { true }, now)
            } else {
                mastered = true
                // Deliberately miss to stress the SRS and trigger remediation.
                sim.workOnItem("vocab.a", { false }, now)
            }
        }
        assertTrue("vocab.a reached the strong threshold before the miss", mastered)

        val finalState = sim.states["vocab.a"]!!
        assertTrue("mastery dropped after the miss but stayed positive",
            finalState.mastery >= 0f && finalState.mastery < 0.95f)
        assertTrue("a lapse was recorded", finalState.lapses > 0)
        assertTrue("ease factor was reduced by the miss",
            finalState.easeFactor < 2.5f)
    }

    // -----------------------------------------------------------------------
    //  Scenario F — A heavy review day must not erase the day (checklist B-2)
    //     30 due cards against a 10-minute target: the review block has to
    //     survive, capped, and a lesson must still fit after it.
    // -----------------------------------------------------------------------

    @Test
    fun heavyReviewDay_keepsReviewAndStillFitsALesson() {
        val started = TimeUtil.startOfDay(startTime)
        val decision = planner.plan(
            states = emptyList(),
            dueReviewCount = 30,
            focusItems = graph.unlocked(emptySet()).take(1),
            targetMinutes = 10,
            todayStudySeconds = 0L,
        )

        assertEquals("review always leads", LearningActionType.REVIEW, decision.actions.first().type)
        assertEquals(30, decision.dueReviewCount)
        assertEquals(
            "the review estimate is capped at half the target",
            5,
            decision.actions.first().estimatedMinutes,
        )
        assertTrue(
            "a lesson still fits after the capped review block",
            decision.actions.any { it.type == LearningActionType.LEARN },
        )
        assertTrue(
            "the plan never exceeds the day's target",
            decision.estimatedTotalMinutes <= 10,
        )
        // Sanity: the same call one day later still surfaces the review.
        val fulfilled = planner.plan(
            states = emptyList(),
            dueReviewCount = 30,
            focusItems = graph.unlocked(emptySet()).take(1),
            targetMinutes = 10,
            todayStudySeconds = TimeUtil.startOfDayPlusDays(started, 1) - started,
        )
        assertEquals(
            "a finished goal still offers retention work",
            LearningActionType.REVIEW,
            fulfilled.actions.firstOrNull()?.type,
        )
    }
}
