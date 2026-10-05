package org.token.english.domain.engine

import org.token.english.domain.model.Exercise
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.MasteryDimension
import org.token.english.domain.model.MasteryProfile
import org.token.english.domain.model.ReviewAttempt
import org.token.english.domain.model.ReviewResult
import org.token.english.domain.model.Skill

/**
 * Targeted remediation engine (audit §5).
 *
 * The planner finds that a prerequisite is weak and produces a REMEDIATE action;
 * this engine then answers the three questions that action is really asking:
 *
 *  1. *Which specific weakness* — not the whole knowledge item, but the precise
 *     sub-concept (or dimension) that is dragging the mastery down. Error isolation
 *     means "Present Simple is at 82% but Third Person -s is at 34%" does NOT
 *     re-teach Present Simple: it targets Third Person -s alone.
 *
 *  2. *Which exercises* — exercises from the item's lessons whose evidence maps
 *     to the specific weak item, ordered weakest-dimension first.
 *
 *  3. *When to stop and reassess* — after a bounded number of attempts the engine
 *     either returns the learner to the main curriculum or escalates.
 *
 * Pure and deterministic: graph + states + exercises in, a plan out. No Android,
 * no clock, no I/O — `now` is always passed in, just like the other engines.
 */
class RemediationEngine(
    private val graph: KnowledgeGraph,
    /** A node below this is considered weak and eligible for error isolation. */
    private val weaknessThreshold: Float = DEFAULT_WEAKNESS_THRESHOLD,
    /** Mastery at or above this after reassessment means the node is fixed. */
    private val reassessThreshold: Float = DEFAULT_REASSESS_THRESHOLD,
    /** Maximum remediation attempts before a hard reassessment is forced. */
    private val maxAttempts: Int = DEFAULT_MAX_ATTEMPTS,
) {

    /**
     * A specific weakness isolated inside a knowledge item — the item itself and
     * the exercise dimension that is weakest on it, so the selector can front-load
     * exactly the format the learner struggles with.
     */
    data class Weakness(
        val itemId: String,
        val titleFa: String,
        val mastery: Float,
        /** Items that depend on this one and are therefore at risk. */
        val blockedDependents: List<String>,
        /** Dimension with the lowest mastery, or null when dimension data is absent. */
        val weakestDimension: MasteryDimension? = null,
        /** How deep the prerequisite chain goes below this item. */
        val depth: Int = 0,
    )

    /**
     * Exercises selected to fix the weakness, plus the bounded retry / reassess
     * policy the session can enforce locally.
     */
    data class Plan(
        val targetId: String,
        val titleFa: String,
        val exercises: List<Exercise>,
        /** Mastery a state must reach to be considered remediated. */
        val reassessThreshold: Float,
        /** Attempts the learner has left before a forced reassessment. */
        val remainingAttempts: Int,
        /** Persian reason the UI can surface: "why these exercises, why now". */
        val reasonFa: String,
    )

    /**
     * Verdict after a cycle of remediation attempts — remediated → return to
     * curriculum; needs-more → keep going; escalation → learner needs help.
     */
    enum class Outcome {
        /** Mastery reached the threshold — return to normal curriculum. */
        REMEDIATED,
        /** Still below threshold, attempts remain. */
        NEEDS_MORE,
        /** Attempts exhausted and still weak — signal for escalation / review. */
        ESCALATION,
    }

    /**
     * The full assessment, carrying the evidence the engine used so the surface
     * can explain the verdict.
     */
    data class Assessment(
        val outcome: Outcome,
        val currentMastery: Float,
        val attemptsMade: Int,
        val correctInRow: Int,
        /** Persian explanation surfaced to the learner. */
        val reasonFa: String,
    )

    // ----------------------------------------------------------- identification

    /**
     * Returns the weakest knowledge items that are below [weaknessThreshold],
     * ordered from weakest to strongest. Only items with at least one exercise
     * in the supplied `exercisesByLesson` are returned — items with no evidence
     * can't be remediated and are skipped.
     *
     * Error isolation: the returned mastery is the item's own, so a weak child
     * (e.g. "Third Person -s" at 0.34) is surfaced even when its parent (e.g.
     * "Present Simple" at 0.82) is strong.
     */
    fun findWeakness(
        states: Map<String, KnowledgeState>,
        exercisesByLesson: Map<String, List<Exercise>>,
    ): Weakness? {
        val candidates = graph.items.mapNotNull { item ->
            val state = states[item.id] ?: return@mapNotNull null
            if (state.mastery >= weaknessThreshold) return@mapNotNull null
            val hasEvidence = item.lessonIds.any { exercisesByLesson[it].orEmpty().isNotEmpty() }
            if (!hasEvidence) return@mapNotNull null
            Weakness(
                itemId = item.id,
                titleFa = item.titleFa,
                mastery = state.mastery,
                blockedDependents = graphItemsDependingOn(item.id),
                weakestDimension = null, // populated by plan() when a profile is available
                depth = depth(item.id),
            )
        }
        return candidates.minByOrNull { it.mastery }
    }

    /** Items that list [itemId] among their prerequisites — i.e. who is blocked. */
    private fun graphItemsDependingOn(itemId: String): List<String> =
        graph.items.filter { itemId in it.prerequisites }.map { it.id }

    // ----------------------------------------------------------- plan

    /**
     * Builds a remediation plan for [itemId]: the exercises in its lessons that
     * provide evidence for it, ordered by dimension weakness when a
     * [MasteryProfile] is supplied so the weakest format comes first.
     */
    fun plan(
        itemId: String,
        exercisesByLesson: Map<String, List<Exercise>>,
        profile: MasteryProfile = MasteryProfile(),
    ): Plan? {
        val item = graph.byId(itemId) ?: return null
        val exercises = item.lessonIds
            .flatMap { lessonId -> exercisesByLesson[lessonId].orEmpty() }
            .filter { exercise ->
                // Keep only exercises whose grading evidence actually maps to this item.
                evidenceCovers(exercise, item)
            }
            .ifEmpty { return null }

        // Order by dimension weakness: weakest first so the learner is asked
        // what they struggle with before what they know.
        val ordered = exercises.sortedWith(
            compareBy(
                { profile.masteryOf(ExerciseDimension.dimensionOf(it)) },
                { it.id },
            ),
        )

        val weakestDim = ordered.firstOrNull()?.let { ExerciseDimension.dimensionOf(it) }
        val remaining = ordered.map { it.id }

        return Plan(
            targetId = itemId,
            titleFa = item.titleFa,
            exercises = ordered,
            reassessThreshold = reassessThreshold,
            remainingAttempts = maxAttempts,
            reasonFa = buildString {
                append("تمرینات هدفمند برای ")
                append(item.titleFa)
                if (weakestDim != null && profile.isEstablished(weakestDim)) {
                    append(" — تمرکز بر ")
                    append(dimensionLabelFa(weakestDim))
                }
            },
        )
    }

    /**
     * Whether one graded answer of [exercise] counts as evidence for [item].
     * Mirrors the attribution rule in [KnowledgeEvidence] but keyed on a single
     * exercise's skill instead of a lesson's full item list.
     */
    private fun evidenceCovers(exercise: Exercise, item: KnowledgeItem): Boolean {
        val exerciseSkill = exercise.skill ?: return true // unknown skill → counts for all
        val typed = KnowledgeEvidence.itemsFor(listOf(item), exerciseSkill)
        return typed.any { it.id == item.id }
    }

    // ----------------------------------------------------------- assess

    /**
     * Decides whether remediation is complete after the learner has made
     * [attemptsMade] attempts. The MasteryProfile is rebuilt from the attempts
     * so the verdict reflects the remediation session, not just the pre-existing
     * mastery score.
     */
    fun assess(
        itemId: String,
        masteryProfile: MasteryProfile,
        attempts: List<DimensionAttempt>,
        attemptsMade: Int,
    ): Assessment {
        // If the learner has never been assessed on any dimension of this item,
        // we can't yet say they're remediated — give them room to try.
        val established = attempts.any { a -> masteryProfile.isEstablished(a.dimension) }
        if (!established && attemptsMade < maxAttempts / 2) {
            return Assessment(
                outcome = Outcome.NEEDS_MORE,
                currentMastery = masteryProfile.overall,
                attemptsMade = attemptsMade,
                correctInRow = consecutiveCorrect(attempts),
                reasonFa = "هنوز داده کافی نیست — ادامه دهید",
            )
        }

        val currentMastery = masteryProfile.overall
        val mastered = currentMastery >= reassessThreshold && attempts.isNotEmpty()

        return when {
            mastered -> Assessment(
                outcome = Outcome.REMEDIATED,
                currentMastery = currentMastery,
                attemptsMade = attemptsMade,
                correctInRow = consecutiveCorrect(attempts),
                reasonFa = "تثبیت شد — بازگشت به درس اصلی",
            )
            attemptsMade >= maxAttempts -> Assessment(
                outcome = Outcome.ESCALATION,
                currentMastery = currentMastery,
                attemptsMade = attemptsMade,
                correctInRow = consecutiveCorrect(attempts),
                reasonFa = "نیاز به مرور بیشتر — این مفهوم را با یک معلم مرور کنید",
            )
            else -> Assessment(
                outcome = Outcome.NEEDS_MORE,
                currentMastery = currentMastery,
                attemptsMade = attemptsMade,
                correctInRow = consecutiveCorrect(attempts),
                reasonFa = "در حال تثبیت — ادامه دهید",
            )
        }
    }

    /**
     * Convenience: assess from raw review attempts (the session log) instead of
     * a pre-built MasteryProfile.
     */
    fun assessFromAttempts(
        itemId: String,
        attempts: List<ReviewAttempt>,
        dimensionOf: (ReviewAttempt) -> MasteryDimension,
    ): Assessment {
        val dimensionAttempts = attempts.map { DimensionAttempt(dimensionOf(it), it.result != ReviewResult.AGAIN) }
        val profile = DefaultMasteryProfileEngine().profileOf(dimensionAttempts)
        return assess(itemId, profile, dimensionAttempts, attempts.size)
    }

    private fun consecutiveCorrect(attempts: List<DimensionAttempt>): Int {
        var count = 0
        for (attempt in attempts.asReversed()) {
            if (!attempt.correct) break
            count++
        }
        return count
    }

    private fun depth(id: String): Int = graphDepth(id)

    /** Delegates to the PrerequisiteEngine's precomputed depth. */
    private lateinit var graphDepth: (String) -> Int

    // ----------------------------------------------------------- wiring

    /**
     * Injects the depth function from [PrerequisiteEngine] so this engine can
     * report dependency depth without duplicating the cycle-safe traversal.
     * Must be called before [plan] if depth is needed.
     */
    fun withDepthResolver(resolver: (String) -> Int) {
        graphDepth = resolver
    }

    // ----------------------------------------------------------- labels

    private fun dimensionLabelFa(dim: MasteryDimension): String = when (dim) {
        MasteryDimension.RECOGNITION -> "شناسایی"
        MasteryDimension.RECALL -> "یادآوری"
        MasteryDimension.COMPREHENSION -> "درک مطلب"
        MasteryDimension.APPLICATION -> "کاربرد"
        MasteryDimension.PRODUCTION -> "تولید"
        MasteryDimension.RETENTION -> "یادماندگی"
    }

    companion object {
        /** Below this, a node is considered weak and worth remediating. */
        const val DEFAULT_WEAKNESS_THRESHOLD = 0.5f
        /** Mastery at or above this means the node is consolidated. */
        const val DEFAULT_REASSESS_THRESHOLD = 0.85f
        /** How many attempts before escalation. */
        const val DEFAULT_MAX_ATTEMPTS = 6
    }
}