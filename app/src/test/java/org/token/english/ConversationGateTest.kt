package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.engine.canOpenLesson
import org.token.english.domain.engine.nextLessonFor
import org.token.english.domain.engine.owedConversation
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Lesson

/**
 * The rule the learner feels: a lesson is not over when its last exercise is
 * answered. Its conversation is where the structure has to be *used*, so the next
 * lesson stays shut until that exchange has been spoken.
 *
 * These are pure-function tests — no repository, no screen — because this is the
 * one place where "may I move on?" is decided.
 */
class ConversationGateTest {

    private fun lesson(id: String, order: Int) =
        Lesson(id, LearningLevel.A1, id, "درس $id", "topic", 5, order)

    private val lessons = listOf(
        lesson("a1.one", 1),
        lesson("a1.two", 2),
        lesson("a1.three", 3),
    )
    private val all = lessons.map { it.id }.toSet()

    @Test
    fun `a lesson that is still being studied owes nothing yet`() {
        val owed = owedConversation(
            allLessons = lessons,
            completedIds = emptySet(),
            dialogueLessonIds = all,
            conversationsDone = emptySet(),
        )

        assertNull(owed)
        assertTrue(canOpenLesson(lessons, emptySet(), all, emptySet(), "a1.one"))
    }

    @Test
    fun `the first completed lesson without its dialogue blocks the next one`() {
        val owed = owedConversation(
            allLessons = lessons,
            completedIds = setOf("a1.one"),
            dialogueLessonIds = all,
            conversationsDone = emptySet(),
        )

        assertEquals("a1.one", owed?.id)
        // The lesson it belongs to stays open — that is where the learner has to go.
        assertTrue(canOpenLesson(lessons, setOf("a1.one"), all, emptySet(), "a1.one"))
        assertFalse(canOpenLesson(lessons, setOf("a1.one"), all, emptySet(), "a1.two"))
    }

    @Test
    fun `finishing the conversation opens the next lesson`() {
        val done = setOf("a1.one")
        val owed = owedConversation(lessons, setOf("a1.one"), all, done)

        assertNull(owed)
        assertTrue(canOpenLesson(lessons, setOf("a1.one"), all, done, "a1.two"))
        // …and the next lesson's own completion will owe its own conversation.
        assertEquals(
            "a1.two",
            owedConversation(lessons, setOf("a1.one", "a1.two"), all, done)?.id,
        )
    }

    @Test
    fun `the oldest owed conversation wins when several lessons are behind`() {
        val owed = owedConversation(
            allLessons = lessons.reversed(),
            completedIds = setOf("a1.one", "a1.two"),
            dialogueLessonIds = all,
            conversationsDone = emptySet(),
        )

        assertEquals("a1.one", owed?.id)
    }

    @Test
    fun `a lesson with no authored dialogue never holds the path back`() {
        val owed = owedConversation(
            allLessons = lessons,
            completedIds = setOf("a1.one"),
            // a1.one ships no conversation: content without a dialogue cannot gate.
            dialogueLessonIds = setOf("a1.two", "a1.three"),
            conversationsDone = emptySet(),
        )

        assertNull(owed)
        assertTrue(canOpenLesson(lessons, setOf("a1.one"), setOf("a1.two"), emptySet(), "a1.two"))
    }

    @Test
    fun `the next lesson still comes from the placed level`() {
        val mixed = listOf(
            Lesson("b1.x", LearningLevel.B1, "x", "درس", "topic", 5, 1),
            Lesson("b2.y", LearningLevel.B2, "y", "درس", "topic", 5, 2),
        )

        assertEquals("b2.y", nextLessonFor(mixed, setOf("b1.x"), LearningLevel.B1)?.id)
    }
}
