package org.token.english

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.data.content.ContentParser
import org.token.english.domain.engine.DefaultKnowledgeGraph
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeType
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Skill
import java.io.File

/**
 * The curriculum graph is the spine the planner and (next) the scheduler hang
 * off, so its invariants are tested twice:
 *
 * 1. Against small hand-written graphs — ordering, unlock logic, cycles.
 * 2. Against the real bundled `knowledge.json` — no cycle, no prerequisite
 *    pointing at a missing item, and every lesson taught by something.
 */
class KnowledgeGraphTest {

    private fun item(
        id: String,
        prerequisites: List<String> = emptyList(),
        lessons: List<String> = listOf("l1"),
        level: LearningLevel = LearningLevel.A1,
    ) = KnowledgeItem(
        id = id,
        type = KnowledgeType.GRAMMAR,
        title = id,
        titleFa = id,
        level = level,
        prerequisites = prerequisites,
        lessonIds = lessons,
        skills = listOf(Skill.GRAMMAR),
    )

    private fun contentFile(name: String): File {
        val candidates = listOf(
            File("src/main/assets/content/$name"),
            File("app/src/main/assets/content/$name"),
        )
        return candidates.firstOrNull { it.exists() } ?: error("content file not found: $name")
    }

    @Test
    fun `topological order places every prerequisite before its dependent`() {
        val graph = DefaultKnowledgeGraph(
            listOf(
                item("c", listOf("b")),
                item("a"),
                item("b", listOf("a")),
            ),
        )
        val order = graph.topologicalOrder().map { it.id }
        assertEquals("parents come first", listOf("a", "b", "c"), order)
        assertTrue("no cycle reported", graph.cyclicIds().isEmpty())
    }

    @Test
    fun `ordering is deterministic regardless of input order`() {
        val items = listOf(item("z", listOf("m")), item("a"), item("m", listOf("a")))
        val first = DefaultKnowledgeGraph(items).topologicalOrder().map { it.id }
        val shuffled = DefaultKnowledgeGraph(items.reversed()).topologicalOrder().map { it.id }
        assertEquals(first, shuffled)
    }

    @Test
    fun `a cycle is detected but no item is dropped`() {
        val graph = DefaultKnowledgeGraph(
            listOf(
                item("a", listOf("b")),
                item("b", listOf("a")),
                item("free"),
            ),
        )
        assertEquals("both nodes of the loop are reported", listOf("a", "b"), graph.cyclicIds())
        assertEquals("nothing is lost", 3, graph.topologicalOrder().size)
        assertEquals("the acyclic node is still ordered first", "free", graph.topologicalOrder().first().id)
    }

    @Test
    fun `a self-referencing prerequisite counts as a cycle`() {
        val graph = DefaultKnowledgeGraph(listOf(item("loop", listOf("loop"))))
        assertEquals(listOf("loop"), graph.cyclicIds())
    }

    @Test
    fun `unlocked returns only items whose prerequisites are mastered`() {
        val graph = DefaultKnowledgeGraph(
            listOf(
                item("a"),
                item("b", listOf("a")),
                item("c", listOf("b")),
            ),
        )
        assertEquals(listOf("a"), graph.unlocked(emptySet()).map { it.id })
        assertEquals("b unlocks once a is mastered", listOf("b"), graph.unlocked(setOf("a")).map { it.id })
        assertEquals(
            "already-mastered items are not offered again",
            listOf("c"),
            graph.unlocked(setOf("a", "b")).map { it.id },
        )
        assertTrue("everything mastered leaves nothing to unlock", graph.unlocked(setOf("a", "b", "c")).isEmpty())
    }

    @Test
    fun `missing prerequisites are reported instead of crashing`() {
        val graph = DefaultKnowledgeGraph(listOf(item("a", listOf("ghost"))))
        assertEquals(listOf("a -> ghost"), graph.danglingPrerequisites())
        assertFalse("an unmet prerequisite is not met", graph.prerequisitesMet("a", emptySet()))
        assertFalse("unknown ids never claim to be ready", graph.prerequisitesMet("nope", emptySet()))
    }

    @Test
    fun `bundled knowledge graph is acyclic with resolvable prerequisites`() {
        val graph = DefaultKnowledgeGraph(ContentParser.parseKnowledge(contentFile("knowledge.json").readText()))
        assertTrue("no circular prerequisites: ${graph.cyclicIds()}", graph.cyclicIds().isEmpty())
        assertTrue("no dangling prerequisites: ${graph.danglingPrerequisites()}", graph.danglingPrerequisites().isEmpty())
        assertEquals("ordering keeps every item", graph.items.size, graph.topologicalOrder().size)
    }

    @Test
    fun `every bundled lesson is taught by at least one knowledge item`() {
        val lessons = JSONObject(contentFile("lessons.json").readText())
            .getJSONArray("lessons")
            .let { array -> (0 until array.length()).map { array.getJSONObject(it).getString("id") } }
        val graph = DefaultKnowledgeGraph(ContentParser.parseKnowledge(contentFile("knowledge.json").readText()))

        val taught = graph.items.flatMap { it.lessonIds }.toSet()
        val orphans = lessons.filterNot { it in taught }
        assertTrue("lessons with no knowledge item: $orphans", orphans.isEmpty())

        val unknown = taught.filterNot { it in lessons.toSet() }
        assertTrue("knowledge items pointing at unknown lessons: $unknown", unknown.isEmpty())
    }
}
