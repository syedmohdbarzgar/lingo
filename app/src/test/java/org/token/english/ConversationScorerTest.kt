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
    fun `the recogniser's unknown marker is not a word the learner said`() {
        // Vosk answers a constrained grammar with `[unk]` for anything outside it.
        // Stripping punctuation off that token left the word "unk" behind, which
        // counted as content the learner had said: on a device take of
        // "[unk] i am going to visit my family" the sentence was marked down for a
        // word nobody spoke, and the placeholder itself was shown under «شما گفتید».
        assertEquals(
            "i am going to visit my family",
            ConversationScorer.normalize("[unk] I'm going to visit my family."),
        )
        assertEquals(
            SpeakingVerdict.CORRECT,
            ConversationScorer.assess(
                spoken = "[unk] [unk] i am going to visit my family",
                target = "I'm going to visit my family.",
            ),
        )
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
    fun `a restarted phrase is still the target said back`() {
        // Speaking practice is where false starts happen: the learner begins again
        // after the first "go straight". The keywords are all there, in the
        // authored order — the repetition is not a different sentence.
        val verdict = ConversationScorer.assess(
            spoken = "go straight go straight and turn left at the traffic light",
            target = "go straight and turn left at the traffic light",
        )
        assertEquals(SpeakingVerdict.CORRECT, verdict)
    }

    @Test
    fun `a contraction in the take matches the expanded target`() {
        // Both are the same sentence said the same way; only the writing differs.
        // Comparing them literally docked a correct answer for style (and the
        // recogniser is free to return either form).
        val verdict = ConversationScorer.assess(
            spoken = "I'm going to visit my family",
            target = "I am going to visit my family.",
        )
        assertEquals(SpeakingVerdict.CORRECT, verdict)
    }

    @Test
    fun `normalize expands contractions and unifies apostrophes`() {
        // Curly apostrophes arrive from the speech model as often as the ASCII one.
        assertEquals("i am fine", ConversationScorer.normalize("I\u2019m fine!"))
        assertEquals("do not worry", ConversationScorer.normalize("Don't worry."))
    }

    @Test
    fun `a hyphenated word is heard as the two words the recogniser knows`() {
        // "twenty-five" is written with a hyphen; the lexicon holds twenty and five,
        // never "twentyfive" — the merged token used to vanish from the grammar.
        val grammar = ConversationScorer.speechGrammar(listOf("Twenty-five minutes."))
        assertTrue("the merged token would be unknown to the recogniser: $grammar", grammar.contains("\"twenty five minutes\""))
        assertEquals(
            SpeakingVerdict.CORRECT,
            ConversationScorer.assess("twenty five minutes", "Twenty-five minutes."),
        )
        assertEquals(
            SpeakingVerdict.CORRECT,
            ConversationScorer.assess("the decision making process is slow", "The decision-making process is slow."),
        )
    }

    @Test
    fun `quotes around a word are not part of the word`() {
        // C2 nuance turns quote the word under discussion: 'May' / 'shall'.
        val grammar = ConversationScorer.speechGrammar(listOf("'May' is right."))
        assertTrue("a quote mark leaked into the grammar: $grammar", grammar.contains("\"may is right\""))
        assertEquals(
            SpeakingVerdict.CORRECT,
            ConversationScorer.assess("may is right", "'May' is right."),
        )
    }

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

    // ---- the bundle's other accepted answers ---------------------------------

    @Test
    fun `the best of the authored answers decides the verdict`() {
        // Only the first answer used to be graded, so a learner who said an answer
        // the bundle itself lists was told they were wrong.
        assertEquals(
            SpeakingVerdict.CORRECT,
            ConversationScorer.assessBest(
                spoken = "just water",
                targets = listOf("A table for two, please.", "Just water, thank you."),
            ),
        )
    }

    @Test
    fun `a short authored answer is correct on its own`() {
        assertEquals(
            SpeakingVerdict.CORRECT,
            ConversationScorer.assessBest(
                spoken = "yes please",
                targets = listOf("Yes, please. A table for two.", "Yes, please."),
            ),
        )
    }

    @Test
    fun `a turn with no accepted answer is a retry, not a crash`() {
        assertEquals(SpeakingVerdict.RETRY, ConversationScorer.assessBest("anything", emptyList()))
    }

    // ---- what the recogniser is allowed to get wrong -------------------------

    @Test
    fun `a homophone from the recogniser does not lose a correct take`() {
        // The model writes "to" for "two"; both sides are folded before comparing.
        assertEquals(
            SpeakingVerdict.CORRECT,
            ConversationScorer.assess("a table for to", "A table for two, please."),
        )
        assertEquals(
            SpeakingVerdict.CORRECT,
            ConversationScorer.assess("nice to meet you to", "Nice to meet you too."),
        )
    }

    @Test
    fun `a one-letter recognition slip is forgiven on a long word`() {
        // "pharmcy" for "pharmacy": one missing letter on a long word. (Two
        // edits — "farmacy" — is a different word as far as this rule goes.)
        assertEquals(
            SpeakingVerdict.CORRECT,
            ConversationScorer.assess(
                "the bank is on the left next to the pharmcy",
                "The bank is on the left, next to the pharmacy.",
            ),
        )
    }

    @Test
    fun `politeness is not content - just water answers just water thank you`() {
        assertEquals(
            SpeakingVerdict.CORRECT,
            ConversationScorer.assessBest(
                spoken = "just water",
                targets = listOf("A table for two, please.", "Just water, thank you."),
            ),
        )
    }

    @Test
    fun `a short word must match exactly - fuzzy matching cannot invent it`() {
        assertTrue(ConversationScorer.sameWord("left", "left"))
        assertTrue(!ConversationScorer.sameWord("left", "right"))
        assertTrue(!ConversationScorer.sameWord("to", "too"))
    }

    // ---- the partner's own line is not an answer ------------------------------

    @Test
    fun `a take that repeats the partner's question is not graded as an answer`() {
        // The recogniser can pick the device up through the speaker; the echo used
        // to land on "near", which told the learner they had almost answered.
        assertEquals(
            SpeakingVerdict.RETRY,
            ConversationScorer.assessBest(
                spoken = "nice to meet you",
                targets = listOf("Nice to meet you too."),
                partnerLine = "Nice to meet you.",
            ),
        )
    }

    @Test
    fun `the real answer survives the echo rule even when it shares the question's words`() {
        assertEquals(
            SpeakingVerdict.CORRECT,
            ConversationScorer.assessBest(
                spoken = "nice to meet you too",
                targets = listOf("Nice to meet you too."),
                partnerLine = "Nice to meet you.",
            ),
        )
    }

    // ---- the recogniser's allowed phrases ------------------------------------

    @Test
    fun `the speech grammar lists every accepted answer and keeps unknown open`() {
        val grammar = ConversationScorer.speechGrammar(
            listOf("I'm fine, thank you.", "Fine thanks.", "I'm fine, thank you."),
        )
        assertTrue("grammar is not a JSON array: $grammar", grammar.startsWith("[") && grammar.endsWith("]"))
        assertTrue("contractions are not expanded for the recogniser: $grammar", grammar.contains("\"i am fine thank you\""))
        assertTrue("second answer missing: $grammar", grammar.contains("\"fine thanks\""))
        assertTrue("duplicate answer not removed: $grammar", grammar.split("fine thanks").size == 2)
        assertTrue("unknown token missing: $grammar", grammar.contains("\"[unk]\""))
    }

    // ---- neither variety of English is a wrong answer (P1-4) ------------------

    @Test
    fun `a take in the other spelling of English is still the target`() {
        // The bundle teaches British English, so a learner who *says* the American
        // spelling — or a recogniser that returns it — used to be marked down for it.
        assertEquals(
            SpeakingVerdict.CORRECT,
            ConversationScorer.assess(
                spoken = "my favorite color is gray",
                target = "My favourite colour is grey.",
            ),
        )
        assertEquals(
            SpeakingVerdict.CORRECT,
            ConversationScorer.assess(
                spoken = "i organised the neighbourhood party",
                target = "I organized the neighborhood party.",
            ),
        )
    }

    // ---- which accepted answer the take is judged against --------------------

    @Test
    fun `the closer of two near misses is the one reported`() {
        // Both replies are "near" for this take, and the old rule kept whichever the
        // bundle listed first — so the missed-word list named a sentence the learner
        // had not tried to say. Coverage and similarity decide between equals.
        val targets = listOf(
            "Yes, a table for two please.",
            "A table for two, please. We are in a hurry.",
        )
        val spoken = "a table for two in a hurry"

        assertEquals("both are near", SpeakingVerdict.NEAR, ConversationScorer.assessBest(spoken, targets))
        assertEquals("the second one is what was said", targets[1], ConversationScorer.bestTarget(spoken, targets))
    }

    @Test
    fun `a retry names the reply it was closer to, not the first one listed`() {
        // Neither answer matches, but one shares two of its words with the take. Word
        // feedback may only come from the answer the verdict was formed on, so this
        // must pick the second one even though both are retries.
        val targets = listOf(
            "No, I do not need anything.",
            "Yes, I have a reservation.",
        )
        val spoken = "yes i need a bag"

        assertEquals(SpeakingVerdict.RETRY, ConversationScorer.assessBest(spoken, targets))
        assertEquals(targets[1], ConversationScorer.bestTarget(spoken, targets))
    }

    @Test
    fun `a better verdict is never traded for better coverage`() {
        // The verdict is the first key: an answer said back wins over a nearer-sounding
        // partial one, whatever order the bundle lists them in.
        val targets = listOf(
            "It is on the left.",
            "It is on the left near the bank.",
        )

        assertEquals("It is on the left.", ConversationScorer.bestTarget("it is on the left", targets))
    }

    @Test
    fun `the fold is a grading rule, so the grammar keeps the authored spelling`() {
        // What the learner is asked to say stays as authored: only the comparison
        // folds. Handing the recogniser the US spelling instead would change what
        // the turn sounds like, not just how it is graded.
        val grammar = ConversationScorer.speechGrammar(listOf("My favourite colour is grey."))
        assertTrue("authored spelling lost: $grammar", grammar.contains("\"my favourite colour is grey\""))
    }
}
