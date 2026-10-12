package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.engine.ExerciseDimension
import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.MasteryDimension
import org.token.english.domain.model.Skill

/**
 * The two P2 exercise shapes: no-option **word recall** and whole-sentence
 * **production**.
 *
 * Both exist to close a hole in the mastery model rather than to add variety for
 * its own sake: before them, every vocabulary item was multiple choice, so the app
 * could only ever observe RECOGNITION, and the only "produce something" item was
 * `translation`, which 21 of 41 shipped with a word bank to pick from. These tests
 * pin what a right answer means for each shape, and — for production — what the
 * structural credit must *not* accept.
 */
class ProductionRecallTest {

    private val lessonId = "a1.test.lesson-01"

    private fun recall(
        prompt: String = "مادر",
        accepted: List<String> = listOf("mother"),
        skill: Skill? = null,
    ) = Exercise.WordRecall(
        id = "test.recall",
        lessonId = lessonId,
        prompt = prompt,
        accepted = accepted,
        skill = skill,
    )

    private fun production(
        mustUse: List<String> = listOf("didn't", "yesterday"),
        minWords: Int = 5,
        accepted: List<String> = listOf("she didn't go to school yesterday"),
        skill: Skill? = null,
    ) = Exercise.SentenceProduction(
        id = "test.production",
        lessonId = lessonId,
        prompt = "او دیروز به مدرسه نرفت.",
        mustUse = mustUse,
        minWords = minWords,
        accepted = accepted,
        skill = skill,
    )

    // ------------------------------------------------------------------ dimensions

    @Test
    fun `the new shapes evidence retrieval and production`() {
        assertEquals(MasteryDimension.RECALL, ExerciseDimension.dimensionOf(recall()))
        assertEquals(MasteryDimension.PRODUCTION, ExerciseDimension.dimensionOf(production()))
    }

    @Test
    fun `production is the typed second source of production evidence`() {
        // Speaking also maps here and stays suspended (no recognizer) — the point is
        // that production is no longer observable only through an unimplemented item.
        val speaking = Exercise.Speaking(
            id = "test.speaking",
            lessonId = lessonId,
            prompt = "Say hello",
            referenceAnswers = listOf("hello"),
        )
        assertEquals(
            ExerciseDimension.dimensionOf(speaking),
            ExerciseDimension.dimensionOf(production()),
        )
    }

    @Test
    fun `skill falls back to vocabulary for recall and writing for production`() {
        assertEquals(Skill.VOCABULARY, AnswerChecker.skillOf(recall()))
        assertEquals(Skill.WRITING, AnswerChecker.skillOf(production()))
        // An authored tag always beats the fallback (P4/P6), whichever direction it goes.
        assertEquals(Skill.GRAMMAR, AnswerChecker.skillOf(production(skill = Skill.GRAMMAR)))
        assertEquals(Skill.READING, AnswerChecker.skillOf(recall(skill = Skill.READING)))
    }

    // ------------------------------------------------------------------ recall

    @Test
    fun `recall grades the word, not the option the learner recognises`() {
        val exercise = recall(accepted = listOf("mother", "mum"))
        assertTrue(AnswerChecker.isCorrect(exercise, "mother"))
        assertTrue(AnswerChecker.isCorrect(exercise, "  Mother  "))
        assertTrue(AnswerChecker.isCorrect(exercise, "MUM"))
        assertFalse(AnswerChecker.isCorrect(exercise, "father"))
        assertFalse(AnswerChecker.isCorrect(exercise, ""))
    }

    @Test
    fun `a near-miss on a recalled word is a spelling hint, not a pass`() {
        // A-2 parity: the same "one edit away" signal the review session shows.
        assertEquals(AnswerChecker.Verdict.ALMOST, AnswerChecker.grade(recall(), "mothr"))
        assertFalse(AnswerChecker.isCorrect(recall(), "mothr"))
        assertEquals("mother", AnswerChecker.correctAnswerText(recall()))
    }

    // ------------------------------------------------------------------ production

    @Test
    fun `an exemplar answer is correct`() {
        val exercise = production()
        assertTrue(AnswerChecker.isCorrect(exercise, "She didn't go to school yesterday."))
    }

    @Test
    fun `an original sentence is accepted when it uses the target structure`() {
        // The whole point of structural credit: the learner composed their own
        // sentence, so failing them for not matching the author's wording would
        // punish exactly the behaviour the exercise is for. It still has to reuse
        // most of the target's words (checklist v9 B-10), which a real paraphrase of
        // the authored exemplar does.
        val exercise = production(
            mustUse = listOf("usually", "breakfast"),
            minWords = 5,
            accepted = listOf("i usually have breakfast at seven"),
        )
        assertTrue(AnswerChecker.isCorrect(exercise, "We usually have breakfast at eight."))
        assertTrue(AnswerChecker.isCorrect(exercise, "They usually have breakfast together at work."))
    }

    @Test
    fun `a filler sentence that merely carries the anchors is not credited`() {
        // Checklist v9 B-10: anchors-in-order plus a word floor alone let this pass.
        // It reuses almost none of the target sentence, so it is not production.
        val exercise = production(
            mustUse = listOf("usually", "breakfast"),
            minWords = 5,
            accepted = listOf("i usually have breakfast at seven"),
        )
        assertFalse(
            "carrying the anchor words is not the same as producing the target",
            AnswerChecker.isCorrect(exercise, "usually breakfast five words here"),
        )
    }

    @Test
    fun `a paraphrase that keeps the target's words is still credited`() {
        // The gate must not over-reject: a sentence that keeps most of the exemplar's
        // vocabulary and the anchors in order passes even when the wording differs.
        val exercise = production(
            mustUse = listOf("usually", "breakfast"),
            minWords = 5,
            accepted = listOf("i usually have breakfast at seven"),
        )
        assertTrue(AnswerChecker.isCorrect(exercise, "we usually have breakfast at eight"))
    }

    @Test
    fun `anchors must appear in the authored order`() {
        val exercise = production(
            mustUse = listOf("didn't", "yesterday"),
            minWords = 4,
            accepted = listOf("i didn't go out yesterday"),
        )
        assertFalse(
            "containing both anchor words is not the same structure",
            AnswerChecker.isCorrect(exercise, "Yesterday I didn't go out."),
        )
        assertTrue(AnswerChecker.isCorrect(exercise, "I didn't go out yesterday."))
    }

    @Test
    fun `a fragment below the word floor does not pass on anchors alone`() {
        val exercise = production(mustUse = listOf("didn't", "go"), minWords = 5)
        assertFalse(AnswerChecker.isCorrect(exercise, "I didn't go."))
        assertTrue(AnswerChecker.isCorrect(exercise, "I didn't go to school yesterday."))
    }

    @Test
    fun `a missing anchor fails even at the right length`() {
        val exercise = production(mustUse = listOf("usually", "breakfast"), minWords = 5)
        assertFalse(AnswerChecker.isCorrect(exercise, "I usually get up at seven in the morning."))
    }

    @Test
    fun `an authored contraction is satisfied by the expanded form`() {
        // AnswerChecker already treats `I'm` ≡ `I am`; the graded anchor has to
        // follow the same rule or a learner typing the full form is failed.
        val exercise = production(
            mustUse = listOf("didn't", "yesterday"),
            minWords = 4,
            accepted = listOf("i didn't go out yesterday"),
        )
        assertTrue(AnswerChecker.isCorrect(exercise, "I did not go out yesterday."))
    }

    @Test
    fun `production never reports a spelling near-miss`() {
        // ALMOST means "check the spelling of the word"; a sentence is not a word,
        // and the near-miss rule is documented as single-word only.
        val exercise = production()
        assertEquals(AnswerChecker.Verdict.WRONG, AnswerChecker.grade(exercise, "she didnt go skool yesterday"))
        assertEquals(AnswerChecker.Verdict.CORRECT, AnswerChecker.grade(exercise, "She didn't go to school yesterday"))
        assertEquals("she didn't go to school yesterday", AnswerChecker.correctAnswerText(exercise))
    }

    @Test
    fun `an empty anchor list cannot be satisfied by any sentence`() {
        // validateContent rejects an anchorless item, but a malformed payload must
        // still not let a random sentence through.
        val exercise = production(mustUse = emptyList(), minWords = 3)
        assertFalse(AnswerChecker.isCorrect(exercise, "anything at all goes here"))
    }
}
