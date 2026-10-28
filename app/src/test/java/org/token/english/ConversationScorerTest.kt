package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.engine.ConversationScorer
import org.token.english.domain.engine.SpeakingVerdict

/**
 * Conversation practice grading — the pure engine behind «درست / نزدیک / تلاش دوباره».
 *
 * Every case here is a real thing a learner does: says the sentence back, says it
 * with a stumble, says a different sentence, or says nothing. The thresholds are
 * product decisions, so they are pinned here rather than left to drift in the UI.
 */
class ConversationScorerTest {

    // ---- CORRECT: the learner said the target back ---------------------------

    @Test
    fun `an identical sentence is correct regardless of case and punctuation`() {
        val verdict = ConversationScorer.assess(
            spoken = "Go straight and turn left at the traffic light!",
            target = "go straight and turn left at the traffic light",
        )
        assertEquals(SpeakingVerdict.CORRECT, verdict)
    }

    @Test
    fun `extra filler words still count as correct`() {
        // A learner adding "um" should not be failed for a hesitation.
        val verdict = ConversationScorer.assess(
            spoken = "um go straight and turn left at the traffic light okay",
            target = "go straight and turn left at the traffic light",
        )
        assertEquals(SpeakingVerdict.CORRECT, verdict)
    }

    @Test
    fun `a real word change drops it out of correct`() {
        val verdict = ConversationScorer.assess(
            spoken = "go straight and turn right at the traffic light",
            target = "go straight and turn left at the traffic light",
        )
        // right/left is one substitution in a long sentence: near, not correct.
        assertEquals(SpeakingVerdict.NEAR, verdict)
    }

    // ---- NEAR: a stumble, not a different sentence ---------------------------

    @Test
    fun `a misspoken middle word lands on near`() {
        // "leff" is not a content word of the target, so coverage drops to 3/4
        // even though the take is 97% similar — the misspelling is what shows.
        val verdict = ConversationScorer.assess(
            spoken = "the bank is on the leff next to the pharmacy",
            target = "the bank is on the left next to the pharmacy",
        )
        assertEquals(SpeakingVerdict.NEAR, verdict)
    }

    @Test
    fun `dropping the article the still counts as correct`() {
        // "the" is a stop word: every content word survives and the take is 91%
        // similar, so this is the sentence said back — not a stumble to flag.
        val verdict = ConversationScorer.assess(
            spoken = "bank is on the left next to pharmacy",
            target = "the bank is on the left next to the pharmacy",
        )
        assertEquals(SpeakingVerdict.CORRECT, verdict)
    }

    @Test
    fun `dropping a content word lands on near`() {
        // "pharmacy" is content: 3 of 4 content words remain — still unmistakably
        // the same sentence, but not the whole one, so near.
        val verdict = ConversationScorer.assess(
            spoken = "the bank is on the left next to the",
            target = "the bank is on the left next to the pharmacy",
        )
        assertEquals(SpeakingVerdict.NEAR, verdict)
    }

    @Test
    fun `reordered content words still land on near`() {
        // The keyword signal is whole-word, so word order alone is a stumble.
        val verdict = ConversationScorer.assess(
            spoken = "next to the pharmacy the bank is on the left",
            target = "the bank is on the left next to the pharmacy",
        )
        assertEquals(SpeakingVerdict.NEAR, verdict)
    }

    @Test
    fun `similarity alone can reach near without every keyword`() {
        // 8/10 words right but a key word missing: the edit distance is close
        // enough that a learner should see "near", not a hard fail.
        val verdict = ConversationScorer.assess(
            spoken = "go straight and turn left at the traffic park",
            target = "go straight and turn left at the traffic light",
        )
        assertEquals(SpeakingVerdict.NEAR, verdict)
    }

    // ---- RETRY: the wrong sentence, or nothing -------------------------------

    @Test
    fun `an unrelated sentence is a retry`() {
        val verdict = ConversationScorer.assess(
            spoken = "i would like a cup of coffee please",
            target = "the bank is on the left next to the pharmacy",
        )
        assertEquals(SpeakingVerdict.RETRY, verdict)
    }

    @Test
    fun `an empty take is a retry, never a crash`() {
        assertEquals(SpeakingVerdict.RETRY, ConversationScorer.assess("", "go straight"))
    }

    @Test
    fun `a whitespace only take is a retry`() {
        assertEquals(SpeakingVerdict.RETRY, ConversationScorer.assess("   ", "go straight"))
    }

    @Test
    fun `noise with no keyword overlap is a retry not near`() {
        // Guards the substring bug: "the" inside "other" must not count as a hit.
        val verdict = ConversationScorer.assess(
            spoken = "others bother rather",
            target = "the bank is on the left",
        )
        assertEquals(SpeakingVerdict.RETRY, verdict)
    }

    @Test
    fun `an empty target is always correct so a missing item cannot block`() {
        assertEquals(SpeakingVerdict.CORRECT, ConversationScorer.assess("anything", ""))
    }

    // ---- similarity ----------------------------------------------------------

    @Test
    fun `similarity is one for identical strings and zero for empty versus text`() {
        assertEquals(1.0, ConversationScorer.similarity("go", "go"), 0.0001)
        assertEquals(0.0, ConversationScorer.similarity("", "go straight"), 0.0001)
    }

    @Test
    fun `similarity is symmetric`() {
        val a = "turn left at the light"
        val b = "turn left at night"
        assertEquals(
            ConversationScorer.similarity(a, b),
            ConversationScorer.similarity(b, a),
            0.0001,
        )
    }

    @Test
    fun `similarity charges one edit as one over the longer length`() {
        // "left" -> "leff": distance 1, longest 23 chars → 1 - 1/23.
        val expected = 1.0 - 1.0 / "go straight and turn left at the traffic light".length
        val actual = ConversationScorer.similarity(
            "go straight and turn leff at the traffic light",
            "go straight and turn left at the traffic light",
        )
        assertEquals(expected, actual, 0.0001)
    }

    // ---- normalize / contentWords -------------------------------------------

    @Test
    fun `normalize strips punctuation and collapses whitespace`() {
        assertEquals(
            "bank is on the left next to the pharmacy",
            ConversationScorer.normalize("  Bank is on the left, next to the pharmacy!! "),
        )
    }

    @Test
    fun `content words drop stop words`() {
        assertEquals(
            listOf("bank", "left", "pharmacy"),
            ConversationScorer.contentWords("the bank is on the left near the pharmacy"),
        )
    }

    @Test
    fun `content words of a stop word only target is empty not a crash`() {
        assertTrue(ConversationScorer.contentWords("the and or").isEmpty())
    }
}
