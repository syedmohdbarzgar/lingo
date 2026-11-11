package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.engine.AdaptiveExerciseSelector
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.MasteryDimension
import org.token.english.domain.model.MasteryProfile

/**
 * Audit §6: with recognition strong but recall weak the app must stop serving
 * multiple-choice and ask for recall instead. These tests pin that decision.
 */
class AdaptiveExerciseSelectorTest {

    private val selector = AdaptiveExerciseSelector()

    private fun mc(id: String) = Exercise.MultipleChoice(
        id = id,
        lessonId = "l1",
        question = "q",
        questionFa = null,
        options = listOf("a", "b"),
        correctIndex = 0,
    )

    private fun fill(id: String, difficulty: Int = Exercise.DEFAULT_DIFFICULTY) = Exercise.FillBlank(
        id = id,
        lessonId = "l1",
        sentence = "I ___ here.",
        accepted = listOf("live"),
    ).copy(difficulty = difficulty)

    private fun translation(id: String, difficulty: Int = Exercise.DEFAULT_DIFFICULTY) = Exercise.Translation(
        id = id,
        lessonId = "l1",
        prompt = "من اینجا زندگی می‌کنم",
        accepted = listOf("I live here"),
        difficulty = difficulty,
    )

    private fun profile(vararg pairs: Pair<MasteryDimension, Float>) =
        MasteryProfile(byDimension = pairs.toMap())

    @Test
    fun `a brand new learner sees the lesson exactly as authored`() {
        val exercises = listOf(mc("m1"), fill("f1"), translation("t1"))
        assertEquals(exercises, selector.order(exercises, MasteryProfile()))
    }

    @Test
    fun `strong recognition and weak recall sends recall to the front`() {
        val exercises = listOf(mc("m1"), mc("m2"), fill("f1"))
        val ordered = selector.order(
            exercises,
            profile(MasteryDimension.RECOGNITION to 0.95f, MasteryDimension.RECALL to 0.4f),
        )
        assertEquals(listOf("f1", "m1", "m2"), ordered.map { it.id })
        assertEquals("f1", selector.next(exercises, profile(
            MasteryDimension.RECOGNITION to 0.95f,
            MasteryDimension.RECALL to 0.4f,
        ))?.id)
    }

    @Test
    fun `among weak dimensions the weakest comes first`() {
        val exercises = listOf(mc("m1"), fill("f1"), translation("t1"))
        val ordered = selector.order(
            exercises,
            profile(
                MasteryDimension.RECOGNITION to 0.5f,
                MasteryDimension.RECALL to 0.7f,
                MasteryDimension.APPLICATION to 0.2f,
            ),
        )
        assertEquals(listOf("t1", "m1", "f1"), ordered.map { it.id })
    }

    @Test
    fun `a fully consolidated learner keeps the authored order`() {
        val exercises = listOf(mc("m1"), fill("f1"), translation("t1"))
        val ordered = selector.order(
            exercises,
            profile(
                MasteryDimension.RECOGNITION to 0.9f,
                MasteryDimension.RECALL to 0.95f,
                MasteryDimension.APPLICATION to 0.88f,
            ),
        )
        assertEquals(exercises, ordered)
    }

    @Test
    fun `consolidated formats sink below weak ones but never disappear`() {
        val exercises = listOf(mc("m1"), translation("t1"), mc("m2"))
        val ordered = selector.order(
            exercises,
            profile(MasteryDimension.RECOGNITION to 0.9f, MasteryDimension.APPLICATION to 0.1f),
        )
        // The weak translation leads; the two strong MCQs keep authored order.
        assertEquals(listOf("t1", "m1", "m2"), ordered.map { it.id })
        // Same members, no duplication or loss — it is only a reordering.
        assertEquals(exercises.toSet(), ordered.toSet())
    }

    @Test
    fun `the order is deterministic for equal input`() {
        val exercises = listOf(mc("m1"), fill("f1"), translation("t1"))
        val p = profile(MasteryDimension.RECALL to 0.3f)
        assertEquals(selector.order(exercises, p), selector.order(exercises, p))
    }

    @Test
    fun `an empty lesson has nothing to pick`() {
        assertEquals(null, selector.next(emptyList(), MasteryProfile()))
        assertTrue(selector.order(emptyList(), MasteryProfile()).isEmpty())
    }

    // ---- authored difficulty sets the level (content v66) ----

    private fun mc(id: String, difficulty: Int) = mc(id).copy(difficulty = difficulty)

    @Test
    fun `a shaky dimension is served from its easy end`() {
        // Three recognition items in the authored order hard → easy, and a weak
        // recognition meter: the level has to come down, not just the format change.
        val exercises = listOf(mc("hard", 3), mc("middle", 2), mc("easy", 1))
        val ordered = selector.order(exercises, profile(MasteryDimension.RECOGNITION to 0.4f))

        assertEquals(listOf("easy", "middle", "hard"), ordered.map { it.id })
        assertEquals("easy", selector.next(exercises, profile(MasteryDimension.RECOGNITION to 0.4f))?.id)
    }

    @Test
    fun `a consolidated dimension is served from its hard end`() {
        // The mirror rule: the same items, a meter above the strong bar, so the level
        // rises instead of the learner re-tapping what they already know.
        val exercises = listOf(mc("easy", 1), mc("hard", 3), mc("middle", 2))
        val ordered = selector.order(exercises, profile(MasteryDimension.RECOGNITION to 0.95f))

        assertEquals(listOf("hard", "middle", "easy"), ordered.map { it.id })
    }

    // ---- "no evidence" is not "zero" (P0 evidence boundary) ----

    private fun sentence(id: String, difficulty: Int = Exercise.DEFAULT_DIFFICULTY) = Exercise.SentenceProduction(
        id = id,
        lessonId = "l1",
        prompt = "بگو چه شد.",
        mustUse = listOf("happened"),
        minWords = 2,
        accepted = listOf("It happened."),
        difficulty = difficulty,
    )

    @Test
    fun `a never-assessed dimension is not promoted over a proven gap`() {
        // Recognition consolidated, recall observed at 0.4, production never asked.
        // Reading the number alone called production 0f and pushed it to the very
        // front — the learner's one observed gap lost first place to material nobody
        // has looked at. Absence of evidence must not win that tie.
        val exercises = listOf(mc("m1"), sentence("s1"), fill("f1"))
        val profile = profile(
            MasteryDimension.RECOGNITION to 0.95f,
            MasteryDimension.RECALL to 0.4f,
        )

        val ordered = selector.order(exercises, profile)

        assertEquals(listOf("f1", "s1", "m1"), ordered.map { it.id })
        assertEquals("f1", selector.next(exercises, profile)?.id)
    }

    @Test
    fun `a proven zero with real evidence is weak, and gets the easy end`() {
        // A wrong answer establishes the dimension at 0f — the same number an unseen
        // dimension reads. Here it is the weakest thing the session knows, so it leads,
        // and it leads from its easy end instead of the authored (hard-first) order.
        val exercises = listOf(fill("hard", 3), sentence("s1"), fill("easy", 1))
        val profile = profile(MasteryDimension.RECALL to 0f)

        val ordered = selector.order(exercises, profile)

        assertEquals(0f, profile.masteryOf(MasteryDimension.RECALL), 0f)
        assertTrue(profile.isEstablished(MasteryDimension.RECALL))
        assertEquals(listOf("easy", "hard", "s1"), ordered.map { it.id })
    }

    @Test
    fun `unseen dimensions keep authored order among themselves while a gap leads`() {
        // Two never-assessed formats and one observed gap: the gap leads, and the two
        // unseen items stay in the order the author wrote them — no difficulty key is
        // invented for material with no evidence.
        val exercises = listOf(sentence("s3", 3), fill("f1"), sentence("s1", 1), mc("m1"))
        val ordered = selector.order(exercises, profile(MasteryDimension.RECALL to 0.2f))

        assertEquals(listOf("f1", "s3", "s1", "m1"), ordered.map { it.id })
        assertEquals(exercises.toSet(), ordered.toSet())
        assertEquals(exercises.size, ordered.size)
    }

    @Test
    fun `no evidence keeps the authored order whatever the difficulty says`() {
        // "No evidence" is not "weak": a fresh install must see the lesson as written,
        // or the first screen of every lesson would be decided by a number nobody
        // earned. The three items differ in difficulty on purpose.
        val exercises = listOf(mc("hard", 3), mc("easy", 1), mc("middle", 2))

        assertEquals(exercises, selector.order(exercises, MasteryProfile()))
    }

    // ---- the whole mastery × difficulty matrix (P1) ----
    //
    // The four cells of weak/consolidated × easy/hard, each asserted on its own so no
    // cell can drift while another keeps the suite green. The items are authored
    // interleaved on purpose: an implementation that only reversed the list would pass
    // one cell and fail the other.

    private fun orderOf(exercises: List<Exercise>, mastery: Float): List<String> =
        selector.order(exercises, profile(MasteryDimension.RECOGNITION to mastery)).map { it.id }

    private val easy = mc("easy", 1)
    private val hard = mc("hard", 3)
    private val interleaved = listOf(mc("easy-1", 1), mc("hard-1", 3), mc("easy-2", 1), mc("hard-2", 3))

    @Test
    fun `low mastery with an easy item available leads with the easy end`() {
        assertEquals(
            listOf("easy-1", "easy-2", "hard-1", "hard-2"),
            selector.order(interleaved, profile(MasteryDimension.RECOGNITION to 0.2f)).map { it.id },
        )
    }

    @Test
    fun `low mastery with only hard items still serves them, in authored order`() {
        // The weak end is not a filter: a lesson whose items are all hard still gets its
        // lesson, and the tie between two equally hard items stays the author's order.
        val onlyHard = listOf(mc("hard-b", 3), mc("hard-a", 3))
        assertEquals(listOf("hard-b", "hard-a"), orderOf(onlyHard, 0.3f))
    }

    @Test
    fun `high mastery with a hard item available leads with the hard end`() {
        assertEquals(
            listOf("hard-1", "hard-2", "easy-1", "easy-2"),
            selector.order(interleaved, profile(MasteryDimension.RECOGNITION to 0.95f)).map { it.id },
        )
    }

    @Test
    fun `high mastery with only easy items still serves them, in authored order`() {
        // Nor is "consolidated" a filter: the hard end leads when it exists, and when it
        // does not the authored order stands rather than a reverse nobody asked for.
        val onlyEasy = listOf(mc("easy-b", 1), mc("easy-a", 1))
        assertEquals(listOf("easy-b", "easy-a"), orderOf(onlyEasy, 0.95f))
    }

    @Test
    fun `mixed dimensions order by evidence band, then by level inside each band`() {
        // Recognition consolidated, recall weak, both with an easy and a hard item: the
        // weak band leads from its easy end, the consolidated band follows from its hard
        // end, and neither band's items jump into the other's order.
        val exercises = listOf(
            mc("m-hard", 3),
            fill("f-hard", 3),
            mc("m-easy", 1),
            fill("f-easy", 1),
        )
        val ordered = selector.order(
            exercises,
            profile(MasteryDimension.RECOGNITION to 0.9f, MasteryDimension.RECALL to 0.3f),
        )

        assertEquals(listOf("f-easy", "f-hard", "m-hard", "m-easy"), ordered.map { it.id })
    }

    @Test
    fun `no evidence in some dimensions never invents an order for them`() {
        // One consolidated dimension, one observable but unassessed: the unseen items
        // keep their authored position and gain no direction from a number they do not
        // have, while the consolidated one sinks with its hard end first.
        val exercises = listOf(
            mc("m-easy", 1),
            translation("t-hard", 3),
            mc("m-hard", 3),
        )
        val ordered = selector.order(exercises, profile(MasteryDimension.RECOGNITION to 0.95f))

        assertEquals(listOf("t-hard", "m-hard", "m-easy"), ordered.map { it.id })
    }

    @Test
    fun `every cell of the matrix keeps every exercise exactly once`() {
        // The one property that must hold whatever the meters say: a lesson is reordered,
        // never shortened, and no item is served twice.
        listOf(0f, 0.2f, 0.5f, 0.84f, 0.85f, 0.95f, 1f).forEach { mastery ->
            val ordered = selector.order(interleaved, profile(MasteryDimension.RECOGNITION to mastery))
            assertEquals("mastery $mastery dropped or duplicated an item", interleaved.size, ordered.size)
            assertEquals("mastery $mastery changed the item set", interleaved.toSet(), ordered.toSet())
        }
    }

    @Test
    fun `both ends of the scale are reachable from one list`() {
        // The point of the axis: the same two items, one meter, two directions.
        assertEquals("easy", selector.next(listOf(easy, hard), profile(MasteryDimension.RECOGNITION to 0.2f))?.id)
        assertEquals("hard", selector.next(listOf(easy, hard), profile(MasteryDimension.RECOGNITION to 0.95f))?.id)
    }
}
