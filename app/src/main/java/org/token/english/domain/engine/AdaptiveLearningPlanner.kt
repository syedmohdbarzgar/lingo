package org.token.english.domain.engine

import org.token.english.core.common.persianMeasure
import org.token.english.core.common.persianPercent
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.LearningAction
import org.token.english.domain.model.LearningActionType

/**
 * The engine's verdict for one session: an ordered list of justified actions, plus
 * the budget context the surface needs to render the day (audit §5/§21).
 */
data class LearningDecision(
    val actions: List<LearningAction>,
    val dueReviewCount: Int,
    val targetMinutes: Int,
    val todayStudySeconds: Long,
    /** Sum of every action's [LearningAction.estimatedMinutes]. */
    val estimatedTotalMinutes: Int,
    /** Minutes left in today's target after this plan. */
    val remainingMinutes: Int,
)

/**
 * Decides *what to study now and why* from the learner model (audit §5 — the
 * Learning Planner, "the most important capability of the next stage").
 *
 * Inputs it reasons over: the curriculum graph, the learner's per-node mastery and
 * schedule, the review queue, and the current curriculum focus. Output: an ordered,
 * self-explaining list of [LearningAction]s.
 *
 * The planner is a pure decision function — no Android, no clock, no DB. It never
 * touches a screen; the surface only renders what it returns (technical spec §19).
 * A caller passes the knowledge items of the current target lesson as [focusItems];
 * the planner adds remediation for unmet prerequisites, new units for unseen
 * nodes, practice for shaky ones, and always leads with due reviews.
 */
class AdaptiveLearningPlanner(
    private val graph: KnowledgeGraph,
    private val prerequisites: PrerequisiteEngine,
    /** Hard cap on actions so a single day is never an unbounded wall. */
    private val maxActions: Int = MAX_ACTIONS,
) {

    /**
     * Builds today's learning plan.
     *
     * @param estimatedMinutesByItem Optional map of knowledge-item id to its lesson's
     *   estimated duration in minutes. When a key is absent the planner falls back to
     *   [DEFAULT_ITEM_MINUTES]. This lets the surface pass real lesson durations
     *   while the planner stays pure and testable with small hand-written graphs.
     */
    fun plan(
        states: List<KnowledgeState>,
        dueReviewCount: Int,
        focusItems: List<KnowledgeItem>,
        targetMinutes: Int,
        todayStudySeconds: Long,
        /**
         * The current time, used for exactly one decision: whether a strong-but-
         * unsecured node has had its retest delay yet. Everything else in the planner
         * reads the stored state, so the parameter is a clock, not a second source of
         * truth — and it has no default, because a wrong default would silently decide
         * the retest question for every caller.
         */
        now: Long,
        estimatedMinutesByItem: Map<String, Int> = emptyMap(),
        /**
         * The recent miss mix, when the caller has one (roadmap: the error taxonomy's
         * adaptive half). Optional because it changes only *which* of the day's equally
         * urgent actions comes first and what one of them says — never what exists.
         */
        missProfile: MissProfile? = null,
    ): LearningDecision {
        val byId: Map<String, KnowledgeState> = states.associateBy { it.itemId }
        val actions = mutableListOf<LearningAction>()

        // 1. Spaced repetition is the cheapest retention win, so it always leads.
        if (dueReviewCount > 0) {
            actions += LearningAction(
                type = LearningActionType.REVIEW,
                itemId = null,
                titleFa = "مرور کارت‌های امروز",
                reasonFa = "${persianMeasure(dueReviewCount, "کارت")} برای مرور آماده است",
                priority = PRIORITY_REVIEW,
                estimatedMinutes = estimateReviewMinutes(dueReviewCount),
            )
        }

        // 2. Turn the focus lesson into actions, one per node.
        for (item in focusItems) {
            if (actions.any { it.itemId == item.id }) continue
            val report = prerequisites.report(item.id, byId)
            val state = byId[item.id]
            when {
                !report.isReady -> {
                    val target = prerequisites.remediationTarget(item.id, byId)
                    if (target != null && actions.none { it.itemId == target.id }) {
                        actions += LearningAction(
                            type = LearningActionType.REMEDIATE,
                            itemId = target.id,
                            titleFa = target.titleFa,
                            reasonFa = remediationReason(item, report),
                            priority = PRIORITY_REMEDIATE,
                            estimatedMinutes = estimatedMinutesByItem[target.id] ?: DEFAULT_ITEM_MINUTES,
                        )
                    }
                }

                state == null -> actions += LearningAction(
                    type = LearningActionType.LEARN,
                    itemId = item.id,
                    titleFa = item.titleFa,
                    reasonFa = "واحد جدیدِ درس امروز",
                    priority = PRIORITY_LEARN,
                    estimatedMinutes = estimatedMinutesByItem[item.id] ?: DEFAULT_ITEM_MINUTES,
                )

                // One bar, shared with the learner model (checklist 2.1): the planner
                // asks the gate, it does not compare floats of its own, so a node the
                // gate calls secured cannot be "weak" here.
                //
                // A strong node the gate has not secured is one of two things
                // (checklist 3, chapter 1):
                //  - it was answered moments ago and still owes us a delayed recall.
                //    Asking for more today would just re-run the drill it already
                //    passed, so the plan says nothing about it until the delay is up;
                //  - the delay has passed, so a short focused retest is the action.
                //    `firstCorrectAt != null` keeps the branch honest: a stored number
                //    with no recorded answer behind it is not "awaiting a retest", it
                //    is a number we cannot trust — that node keeps its practice slot.
                state.mastery >= STRONG_MASTERY &&
                    state.firstCorrectAt != null &&
                    !KnowledgeGates.isSecured(state) -> {
                    if (KnowledgeGates.hasWaitedForRetest(state, now)) {
                        actions += LearningAction(
                            type = LearningActionType.RETEST,
                            itemId = item.id,
                            titleFa = item.titleFa,
                            reasonFa = "بازآزمایی برای تثبیت",
                            priority = PRIORITY_RETEST,
                            estimatedMinutes = RETEST_MINUTES,
                            lessonId = item.lessonIds.firstOrNull(),
                        )
                    }
                }

                !KnowledgeGates.isSecured(state) -> actions += LearningAction(
                    type = LearningActionType.PRACTISE,
                    itemId = item.id,
                    titleFa = item.titleFa,
                    reasonFa = "تسلط فعلی ${percent(state.mastery)} — تمرین بیشتر لازم است",
                    priority = PRIORITY_PRACTISE,
                    estimatedMinutes = estimatedMinutesByItem[item.id] ?: DEFAULT_ITEM_MINUTES,
                )
            }
        }

        // 3. Only if the focus lesson produced nothing actionable of its own (every
        //    node already met and strong) do we open the next unlocked unit, so a
        //    strong learner always keeps moving forward. Remediation/practice work
        //    on the focus lesson must not be diluted by an extra new unit.
        val focusHasUpcomingWork = actions.any {
            it.type == LearningActionType.LEARN ||
                it.type == LearningActionType.REMEDIATE ||
                it.type == LearningActionType.RETEST ||
                it.type == LearningActionType.PRACTISE
        }
        if (!focusHasUpcomingWork) {
            val masteredIds = byId
                .filterValues { KnowledgeGates.isSecured(it) }
                .keys
            val next = graph.unlocked(masteredIds)
                .firstOrNull { candidate ->
                    byId[candidate.id] == null && actions.none { it.itemId == candidate.id }
                }
            if (next != null) {
                actions += LearningAction(
                    type = LearningActionType.LEARN,
                    itemId = next.id,
                    titleFa = next.titleFa,
                    reasonFa = "واحد بعدیِ بازشده",
                    priority = PRIORITY_LEARN,
                    estimatedMinutes = estimatedMinutesByItem[next.id] ?: DEFAULT_ITEM_MINUTES,
                )
            }
        }

        // 4. Deterministic order: urgency first, then the miss mix, then id so equal
        //    priorities are stable.
        //
        //    The mix is the only input here that is about the answers that went *wrong*
        //    rather than about what is known. It is allowed to do exactly two things:
        //    order the day *within one priority* (a vocabulary gap puts the vocabulary
        //    node first among equally urgent work) and add one sentence to the reason of
        //    the action it promoted. It never invents an action, drops one, or changes a
        //    priority — urgency is still the graph's, because a miss count is a window's
        //    worth of evidence and a prerequisite is a fact. A mix whose weakest
        //    category has no miss at all (everything answered right) steers nothing:
        //    "weakest" of a clean window is not a weakness.
        val weakestMiss = MissTaxonomy.weakestCategory(missProfile)
        val missRank: Map<String, Int> = MissTaxonomy.rank(graph.items, weakestMiss)
        val sorted = actions.sortedWith(
            compareBy(
                { it.priority },
                { missRank[it.itemId] ?: 1 },
                { it.itemId ?: "" },
            ),
        )

        // 5. Budget enforcement: keep actions in priority order until the daily time
        //    budget or the action cap is exhausted. todayStudySeconds already spent
        //    this calendar day eats into the budget first.
        //
        //    Two rules keep the plan honest (checklist B-2):
        //    - Review is NEVER dropped for lack of budget. Retention decays silently
        //      while postponed, so a big review block must not lose its slot to a
        //      cheaper lesson further down the list; its estimate is instead capped
        //      at half the day's target so a 300-card backlog cannot swallow the day.
        //    - Everything else stops at the FIRST action that does not fit, rather
        //      than skipping it for a smaller later one: the plan is a sequence, and
        //      reordering it silently would misrepresent what to do next.
        //
        //    Time accounting (P0): seconds already studied are rounded UP to whole
        //    minutes, because a partially elapsed minute is time the learner has
        //    already spent — rounding down would hand the plan more budget than the
        //    day actually has.
        val spentMinutes = ((todayStudySeconds + 59) / 60).toInt()
        val availableMinutes = (targetMinutes - spentMinutes).coerceAtLeast(0)
        val candidates = sorted.take(maxActions)
        //    Review is capped at half the TARGET (not the remaining budget), which
        //    is what makes the whole plan provably never exceed the user's chosen
        //    target: review ≤ target/2, and every later action is bounded by the
        //    remaining budget, so the total can never pass the target.
        val reviewCap = targetMinutes / 2
        val budgeted = mutableListOf<LearningAction>()
        var usedMinutes = 0

        candidates.firstOrNull { it.type == LearningActionType.REVIEW }?.let { review ->
            val cost = minOf(review.estimatedMinutes, reviewCap)
            budgeted += review.copy(estimatedMinutes = cost)
            usedMinutes += cost
        }
        for (action in candidates) {
            if (action.type == LearningActionType.REVIEW) continue
            if (usedMinutes + action.estimatedMinutes > availableMinutes) break
            usedMinutes += action.estimatedMinutes
            budgeted += action
        }

        return LearningDecision(
            actions = withMissReason(budgeted, weakestMiss, missRank),
            dueReviewCount = dueReviewCount,
            targetMinutes = targetMinutes,
            todayStudySeconds = todayStudySeconds,
            estimatedTotalMinutes = budgeted.sumOf { it.estimatedMinutes },
            remainingMinutes = (availableMinutes - usedMinutes).coerceAtLeast(0),
        )
    }

    /**
     * Names the gap on the first action that answers it, and on nothing else.
     *
     * One sentence, not a badge on every row: the plan is already a list of reasons, and
     * a line repeated on five cards tells the learner nothing about where to start.
     */
    private fun withMissReason(
        actions: List<LearningAction>,
        weakest: MissCategory?,
        missRank: Map<String, Int>,
    ): List<LearningAction> {
        if (weakest == null) return actions
        val index = actions.indexOfFirst { action ->
            action.itemId != null && missRank[action.itemId] == 0
        }
        if (index < 0) return actions
        val promoted = actions[index]
        return actions.toMutableList().also { list ->
            list[index] = promoted.copy(reasonFa = promoted.reasonFa + MissTaxonomy.gapSuffix(weakest))
        }
    }

    private fun remediationReason(item: KnowledgeItem, report: PrerequisiteReport): String = when {
        report.unseen.isNotEmpty() -> "پیش‌نیاز «${item.titleFa}» هنوز شروع نشده"
        else -> "پیش‌نیاز «${item.titleFa}» هنوز تثبیت نشده"
    }

    private fun percent(mastery: Float): String = persianPercent((mastery * 100).toInt())

    private fun estimateReviewMinutes(count: Int): Int =
        (count * MINUTES_PER_REVIEW_CARD).coerceAtLeast(MIN_REVIEW_MINUTES)

    companion object {
        /**
         * The mastery number behind [KnowledgeGates.isSecured]: one [STRONG_MASTERY],
         * not a second threshold. Kept public because the metrics and the simulation
         * tests measure mastery against the same line the planner uses.
         */
        const val STRONG_THRESHOLD = STRONG_MASTERY
        const val MAX_ACTIONS = 6

        /** Default minutes for a single new/practise/remediate item when no lesson duration is known. */
        const val DEFAULT_ITEM_MINUTES = 5

        /** A retest is one short pass over the node's own evidence, not a full lesson. */
        const val RETEST_MINUTES = 3

        /** Minutes assumed per due review card. */
        const val MINUTES_PER_REVIEW_CARD = 1

        /** Even a handful of review cards gets a short block. */
        const val MIN_REVIEW_MINUTES = 5

        private const val PRIORITY_REVIEW = 0
        private const val PRIORITY_REMEDIATE = 1

        /** Between a broken prerequisite and a new unit: finish what is nearly known. */
        private const val PRIORITY_RETEST = 2
        private const val PRIORITY_LEARN = 3
        private const val PRIORITY_PRACTISE = 4
    }
}
