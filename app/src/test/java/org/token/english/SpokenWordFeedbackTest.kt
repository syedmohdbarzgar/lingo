package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.engine.ConversationScorer
import org.token.english.domain.engine.SpokenWordFeedback

/**
 * Word-level feedback on a spoken take.
 *
 * The verdict says whether the sentence landed; this says *which* word did not, so
 * the two have to agree on what "the same word" means — same normalisation, same
 * contraction expansion, same dialect and homophone fold, same one-edit tolerance.
 * These cases pin that agreement as much as the marking itself.
 */
class SpokenWordFeedbackTest {

    @Test
    fun `a take that says every word marks every word heard`() {
        val feedback = SpokenWordFeedback.of(
            "I'm going to visit my family.",
            "I'm going to visit my family.",
        )

        assertEquals(0, feedback.missed.size)
        assertTrue(feedback.marks.all { it.heard })
        // The mark list follows the model sentence, so a screen can print it as is.
        assertEquals(listOf("i", "am", "going", "to", "visit", "my", "family"), feedback.marks.map { it.word })
    }

    @Test
    fun `the dropped content word is the one named`() {
        // The learner said the sentence with one word missing: the verdict is
        // already «near», and the only actionable part is *which* word.
        val short = SpokenWordFeedback.of("i am going to visit my", "I'm going to visit my family.")
        assertEquals(listOf("family"), short.missed)
        assertTrue(short.marks.single { it.word == "family" }.let { !it.heard })
    }

    @Test
    fun `a dropped article is not a miss to act on`() {
        // The scorer forgives a missing article, so the feedback must not report one
        // — otherwise the same take would be «correct» and «missing a word» at once.
        val feedback = SpokenWordFeedback.of(
            "bank is on the left",
            "The bank is on the left.",
        )

        assertEquals(emptyList<String>(), feedback.missed)
        // The marks themselves are still truthful per word — the dropped article is
        // one mark, it is just not what the learner is asked to fix.
        assertTrue(feedback.marks.any { it.word == "the" && !it.heard })
        assertTrue(feedback.marks.filter { it.word == "bank" }.all { it.heard })
    }

    @Test
    fun `order is forgiven, exactly as the verdict forgives it`() {
        // Said every word in the learner's own order: the scorer calls that near,
        // not a miss, so no word may be reported as unheard because of position.
        val feedback = SpokenWordFeedback.of(
            "my family i am going to visit",
            "I'm going to visit my family.",
        )

        assertEquals(emptyList<String>(), feedback.missed)
    }

    @Test
    fun `a contraction and its expansion are the same word`() {
        val feedback = SpokenWordFeedback.of("i am ready", "I'm ready.")

        assertEquals(emptyList<String>(), feedback.missed)
    }

    @Test
    fun `a homophone and a dialect spelling still count as heard`() {
        val plural = SpokenWordFeedback.of("there are two dogs", "There are two dogs.")
        assertEquals(emptyList<String>(), plural.missed)

        // The bundle teaches British spelling; the recogniser may return American.
        val dialect = SpokenWordFeedback.of("my favourite colour is blue", "My favourite colour is blue.")
        assertEquals(emptyList<String>(), dialect.missed)
        val flipped = SpokenWordFeedback.of("my favorite color is blue", "My favourite colour is blue.")
        assertEquals(emptyList<String>(), flipped.missed)
    }

    @Test
    fun `one utterance satisfies one word, not two`() {
        // "very very good" said once is one "very": consuming the heard word is what
        // keeps the list honest instead of letting a repeated model word look met.
        val feedback = SpokenWordFeedback.of("very good", "very very good")

        assertEquals(listOf("very"), feedback.missed)
        assertEquals(2, feedback.marks.count { it.word == "very" })
        assertEquals(1, feedback.marks.count { it.word == "very" && it.heard })
    }

    @Test
    fun `a take with no words marks every content word unheard`() {
        val feedback = SpokenWordFeedback.of("", "I'm going to visit my family.")

        // The screen decides whether to show it — an unheard take offers the mic
        // again rather than a critique — but the marking itself is complete.
        // `to` is a stop word, so it is marked but not reported — the same rule
        // the verdict uses when it counts content words.
        assertEquals(listOf("i", "am", "going", "visit", "my", "family"), feedback.missed)
        assertTrue(feedback.marks.none { it.heard })
    }

    @Test
    fun `the scorer names the same target the feedback marks`() {
        // Feedback is only meaningful about the sentence the verdict was formed on:
        // a turn authored with two accepted replies must mark the one that matched.
        val targets = listOf(
            "Yes, a table for two, please.",
            "A table for two, thanks.",
        )

        assertEquals("A table for two, thanks.", ConversationScorer.bestTarget("a table for two", targets))
        val feedback = SpokenWordFeedback.of(
            "a table for two",
            ConversationScorer.bestTarget("a table for two", targets).orEmpty(),
        )
        assertEquals(emptyList<String>(), feedback.missed)
    }
}
