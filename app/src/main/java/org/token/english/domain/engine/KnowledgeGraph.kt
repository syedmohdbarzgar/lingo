package org.token.english.domain.engine

import org.token.english.domain.model.KnowledgeItem

/**
 * Pure view over the curriculum graph (audit §4/§6 — "prerequisites").
 *
 * The graph answers the question the planner and (later) the scheduler need:
 * given what the learner has already mastered, which knowledge items are ready
 * to be learned next, and in what order must they be introduced?
 *
 * Nothing here touches Android, Room or Compose: the UI and the repositories
 * must never learn how the curriculum is ordered (technical spec §19).
 */
interface KnowledgeGraph {
    /** All items, in a deterministic order (sorted by id) — never null, never nulls inside. */
    val items: List<KnowledgeItem>

    fun byId(id: String): KnowledgeItem?

    /** True when every prerequisite of [id] is in [masteredIds]. */
    fun prerequisitesMet(id: String, masteredIds: Set<String>): Boolean

    /**
     * Items whose prerequisites are all satisfied and that are not themselves in
     * [masteredIds] — i.e. what is unlocked right now. Deterministic order.
     */
    fun unlocked(masteredIds: Set<String>): List<KnowledgeItem>

    /**
     * Prerequisites-before-dependents ordering. Items that sit on a cycle cannot
     * be ordered and are appended at the end (sorted by id) rather than dropped,
     * so a broken graph degrades instead of losing content.
     */
    fun topologicalOrder(): List<KnowledgeItem>

    /** Ids that participate in at least one dependency cycle (should be empty). */
    fun cyclicIds(): List<String>

    /** "itemId -> prerequisiteId" for prerequisites pointing at unknown items. */
    fun danglingPrerequisites(): List<String>
}

/**
 * Immutable, deterministic implementation. All work is done once in the
 * constructor so repeated queries are cheap and stable.
 */
class DefaultKnowledgeGraph(source: List<KnowledgeItem>) : KnowledgeGraph {

    override val items: List<KnowledgeItem> = source.sortedBy { it.id }

    private val index: Map<String, KnowledgeItem> = items.associateBy { it.id }

    /** Violations are precomputed once; a malformed graph still works. */
    private val cyclic: List<String> = items.filter { participatesInCycle(it.id) }.map { it.id }

    private val dangling: List<String> = items
        .flatMap { item -> item.prerequisites.filterNot { it in index }.map { "${item.id} -> $it" } }
        .sorted()

    override fun byId(id: String): KnowledgeItem? = index[id]

    override fun prerequisitesMet(id: String, masteredIds: Set<String>): Boolean {
        val item = index[id] ?: return false
        return item.prerequisites.all { it in masteredIds }
    }

    override fun unlocked(masteredIds: Set<String>): List<KnowledgeItem> = items.filter { item ->
        item.id !in masteredIds && item.prerequisites.all { it in masteredIds }
    }

    override fun topologicalOrder(): List<KnowledgeItem> {
        val remaining = items.toMutableList()
        val ordered = mutableListOf<KnowledgeItem>()
        val placed = mutableSetOf<String>()
        while (remaining.isNotEmpty()) {
            // `remaining` preserves the sorted order, so `ready` is deterministic.
            val ready = remaining.filter { item ->
                item.prerequisites.filter { it in index }.all { it in placed }
            }
            if (ready.isEmpty()) break // a cycle: everything left depends on itself
            ordered += ready
            placed += ready.map { it.id }
            remaining.removeAll(ready.toSet())
        }
        ordered += remaining.sortedBy { it.id }
        return ordered
    }

    override fun cyclicIds(): List<String> = cyclic

    override fun danglingPrerequisites(): List<String> = dangling

    /** Depth-first walk from [id]'s prerequisites back to see if it reaches itself. */
    private fun participatesInCycle(id: String): Boolean {
        val item = index[id] ?: return false
        val pending = ArrayDeque(item.prerequisites.filter { it in index })
        val visited = mutableSetOf<String>()
        while (pending.isNotEmpty()) {
            val current = pending.removeLast()
            if (current == id) return true
            if (!visited.add(current)) continue
            index[current]?.prerequisites?.filter { it in index }?.forEach { pending += it }
        }
        return false
    }
}
