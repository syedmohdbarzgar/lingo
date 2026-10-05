package org.token.english.domain.engine

import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState

/**
 * Whether a node is safe to learn yet (audit §4 — "Prerequisite mastered? YES → learn,
 * NO → remediate").
 */
enum class Readiness {
    /** Every prerequisite is known well enough. */
    READY,

    /** At least one prerequisite has been met but is still below the mastery threshold. */
    NEEDS_REMEDIATION,

    /** At least one prerequisite has never been practised (or does not resolve). */
    BLOCKED,
}

/**
 * Why a node is or is not ready, and which of its prerequisites to fix first
 * (audit §4: "Missing prerequisite detection", "Weak prerequisite detection",
 * "Dependency depth").
 *
 * Pure data: the graph is immutable, so this is the stable answer for a given
 * learner state and can be cached or logged.
 */
data class PrerequisiteReport(
    val itemId: String,
    val readiness: Readiness,
    /** Prerequisites that were never practised (in graph order). */
    val unseen: List<String>,
    /** Prerequisites that were practised but are still weak, weakest first. */
    val weak: List<String>,
    /** Longest prerequisite chain below this node (0 = a root). */
    val depth: Int,
) {
    val isReady: Boolean get() = readiness == Readiness.READY
}

/**
 * Turns the raw curriculum graph into an actionable readiness verdict for one
 * learner (audit §4). The graph itself only says "who depends on whom"; this
 * engine says "so what should happen now", using the mastery the learner model
 * already tracks.
 *
 * Pure and deterministic: no Android, no clock, no I/O. Depth is precomputed once
 * so repeated [report] calls are cheap, and a cycle (which [KnowledgeGraph]
 * already flags) degrades to a bounded depth instead of looping forever.
 */
class PrerequisiteEngine(
    private val graph: KnowledgeGraph,
    /** Mastery at or above this counts as "known enough" to unlock a dependent node. */
    private val masteryThreshold: Float = DEFAULT_MASTERY_THRESHOLD,
) {

    private val depth: Map<String, Int> = computeDepths()

    fun report(itemId: String, states: Map<String, KnowledgeState>): PrerequisiteReport {
        val item = graph.byId(itemId)
            ?: return PrerequisiteReport(itemId, Readiness.READY, emptyList(), emptyList(), 0)

        val unseen = mutableListOf<String>()
        val weak = mutableListOf<Pair<String, Float>>()
        for (prerequisite in item.prerequisites) {
            val state = states[prerequisite]
            when {
                // A dangling prereq is unrunnable, so it reads as unmet rather than
                // silently unlocking the node.
                graph.byId(prerequisite) == null || state == null -> unseen += prerequisite
                state.mastery < masteryThreshold -> weak += prerequisite to state.mastery
            }
        }

        val readiness = when {
            unseen.isNotEmpty() -> Readiness.BLOCKED
            weak.isNotEmpty() -> Readiness.NEEDS_REMEDIATION
            else -> Readiness.READY
        }
        return PrerequisiteReport(
            itemId = itemId,
            readiness = readiness,
            unseen = unseen,
            weak = weak.sortedBy { it.second }.map { it.first },
            depth = depth[itemId] ?: 0,
        )
    }

    /**
     * The single prerequisite worth studying before [itemId]: an unseen one first
     * (it must be introduced at all), otherwise the weakest met one. `null` when
     * nothing blocks the node or the blockers do not resolve to real items.
     */
    fun remediationTarget(itemId: String, states: Map<String, KnowledgeState>): KnowledgeItem? {
        val report = report(itemId, states)
        val candidateId = report.unseen.firstOrNull() ?: report.weak.firstOrNull() ?: return null
        return graph.byId(candidateId)
    }

    /** Longest prerequisite chain below [id]; 0 for a root or an unknown id. */
    fun depth(id: String): Int = depth[id] ?: 0

    private fun computeDepths(): Map<String, Int> {
        val cache = HashMap<String, Int>()
        for (item in graph.items) depthOf(item.id, HashSet(), cache)
        return cache
    }

    private fun depthOf(id: String, path: MutableSet<String>, cache: MutableMap<String, Int>): Int {
        cache[id]?.let { return it }
        val item = graph.byId(id) ?: return 0
        // Guard against a cycle: an id already on this walk contributes no depth.
        if (!path.add(id)) return 0
        val prerequisites = item.prerequisites.filter { graph.byId(it) != null }
        val result = if (prerequisites.isEmpty()) {
            0
        } else {
            1 + prerequisites.maxOf { depthOf(it, path, cache) }
        }
        path.remove(id)
        cache[id] = result
        return result
    }

    companion object {
        /** Below this, a node is treated as not yet consolidated. */
        const val DEFAULT_MASTERY_THRESHOLD = 0.7f
    }
}
