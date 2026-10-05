package org.token.english.domain.engine

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
    /** At or above this, a met node needs no further practice. */
    private val strongThreshold: Float = STRONG_THRESHOLD,
    /** Hard cap on actions so a single day is never an unbounded wall. */
    private val maxActions: Int = MAX_ACTIONS,
) {

    fun plan(
        states: List<KnowledgeState>,
        dueReviewCount: Int,
        focusItems: List<KnowledgeItem>,
        targetMinutes: Int,
        todayStudySeconds: Long,
    ): LearningDecision {
        val byId: Map<String, KnowledgeState> = states.associateBy { it.itemId }
        val actions = mutableListOf<LearningAction>()

        // 1. Spaced repetition is the cheapest retention win, so it always leads.
        if (dueReviewCount > 0) {
            actions += LearningAction(
                type = LearningActionType.REVIEW,
                itemId = null,
                titleFa = "مرور کارت‌های امروز",
                reasonFa = "$dueReviewCount کارت برای مرور آماده است",
                priority = PRIORITY_REVIEW,
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
                        )
                    }
                }

                state == null -> actions += LearningAction(
                    type = LearningActionType.LEARN,
                    itemId = item.id,
                    titleFa = item.titleFa,
                    reasonFa = "واحد جدیدِ درس امروز",
                    priority = PRIORITY_LEARN,
                )

                state.mastery < strongThreshold -> actions += LearningAction(
                    type = LearningActionType.PRACTISE,
                    itemId = item.id,
                    titleFa = item.titleFa,
                    reasonFa = "تسلط فعلی ${percent(state.mastery)} — تمرین بیشتر لازم است",
                    priority = PRIORITY_PRACTISE,
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
                it.type == LearningActionType.PRACTISE
        }
        if (!focusHasUpcomingWork) {
            val masteredIds = byId
                .filterValues { it.mastery >= strongThreshold }
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
                )
            }
        }

        // 4. Deterministic order: urgency first, then id so equal priorities are stable.
        val ordered = actions
            .sortedWith(compareBy({ it.priority }, { it.itemId ?: "" }))
            .take(maxActions)

        return LearningDecision(
            actions = ordered,
            dueReviewCount = dueReviewCount,
            targetMinutes = targetMinutes,
            todayStudySeconds = todayStudySeconds,
        )
    }

    private fun remediationReason(item: KnowledgeItem, report: PrerequisiteReport): String = when {
        report.unseen.isNotEmpty() -> "پیش‌نیاز «${item.titleFa}» هنوز شروع نشده"
        else -> "پیش‌نیاز «${item.titleFa}» هنوز تثبیت نشده"
    }

    private fun percent(mastery: Float): String = "${(mastery * 100).toInt()}٪"

    companion object {
        const val STRONG_THRESHOLD = 0.85f
        const val MAX_ACTIONS = 6

        private const val PRIORITY_REVIEW = 0
        private const val PRIORITY_REMEDIATE = 1
        private const val PRIORITY_LEARN = 2
        private const val PRIORITY_PRACTISE = 3
    }
}
