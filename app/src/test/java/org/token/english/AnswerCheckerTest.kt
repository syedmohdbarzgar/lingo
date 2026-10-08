package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.Skill

class AnswerCheckerTest {

    private fun fillBlank(vararg accepted: String) = Exercise.FillBlank(
        id = "x",
        lessonId = "l",
        sentence = "I'm happy.",
        accepted = accepted.toList(),
    )

    /**
     * A vocabulary-tagged blank. The near-miss hint is a *spelling* signal, so it
     * only applies where the learner is spelling a word (A-2b) — a plain blank
     * falls back to the GRAMMAR heuristic and never reaches ALMOST.
     */
    private fun vocabBlank(vararg accepted: String) = Exercise.FillBlank(
        id = "x",
        lessonId = "l",
        sentence = "I'm happy.",
        accepted = accepted.toList(),
        skill = Skill.VOCABULARY,
    )

    @Test
    fun `curly apostrophe equals straight apostrophe`() {
        assertEquals("i'm", AnswerChecker.normalize("I\u2019m"))
        assertTrue(AnswerChecker.isCorrect(fillBlank("I'm"), "I\u2019m"))
    }

    @Test
    fun `contractions equal their expanded form in both directions`() {
        assertTrue(AnswerChecker.isCorrect(fillBlank("I am happy"), "I'm happy"))
        assertTrue(AnswerChecker.isCorrect(fillBlank("I'm happy"), "I am happy"))
        assertTrue(AnswerChecker.isCorrect(fillBlank("do not stop"), "don't stop"))
        assertTrue(AnswerChecker.isCorrect(fillBlank("it is fine"), "it's fine"))
    }

    @Test
    fun `arabic yeh and kaf normalize to persian forms`() {
        // كتاب — kaf U+0643 instead of Persian U+06A9 (Arabic keyboard layout).
        val arabicKaf = "\u0643\u062a\u0627\u0628"
        val persianKaf = "\u06a9\u062a\u0627\u0628"
        assertTrue(AnswerChecker.isCorrect(fillBlank(persianKaf), arabicKaf))
        assertEquals(AnswerChecker.normalize(persianKaf), AnswerChecker.normalize(arabicKaf))

        // میروم — yeh U+064A instead of Persian U+06CC.
        val arabicYeh = "\u0645\u064a\u0631\u0648\u0645"
        val persianYeh = "\u0645\u06cc\u0631\u0648\u0645"
        assertTrue(AnswerChecker.matchesAny(listOf(persianYeh), arabicYeh))
        assertEquals(AnswerChecker.normalize(persianYeh), AnswerChecker.normalize(arabicYeh))
    }

    @Test
    fun `matchesAny rejects blanks and wrong answers`() {
        assertFalse(AnswerChecker.matchesAny(listOf("hello"), ""))
        assertFalse(AnswerChecker.matchesAny(listOf("hello"), "   "))
        assertFalse(AnswerChecker.matchesAny(listOf("hello"), "goodbye"))
    }

    @Test
    fun `multiple choice still compares against the correct option only`() {
        val exercise = Exercise.MultipleChoice(
            id = "x",
            lessonId = "l",
            question = "q",
            questionFa = null,
            options = listOf("alpha", "beta"),
            correctIndex = 1,
        )
        assertTrue(AnswerChecker.isCorrect(exercise, "Beta!"))
        assertFalse(AnswerChecker.isCorrect(exercise, "alpha"))
    }

    // --- A-2: digit normalization -------------------------------------------

    @Test
    fun `digits match their word form in both directions`() {
        assertTrue(AnswerChecker.isCorrect(fillBlank("three"), "3"))
        assertTrue(AnswerChecker.isCorrect(fillBlank("3"), "three"))
        assertTrue(AnswerChecker.isCorrect(fillBlank("twenty"), "20"))
        // Digits inside a sentence normalize too.
        assertTrue(
            AnswerChecker.matchesAny(listOf("i have three books"), "I have 3 books."),
        )
        // Words that merely start with digits stay untouched.
        assertFalse(AnswerChecker.isCorrect(fillBlank("1st"), "1"))
        // Out-of-range numbers keep their digits (no map entry).
        assertTrue(AnswerChecker.matchesAny(listOf("100"), "100"))
    }

    /**
     * A-2b latent collision: the old normalizer stripped every dot and comma, so a
     * decimal collapsed into a whole number (`3.5` → `35`). Nothing in the bundle
     * has a price or a time yet, but the first price lesson would silently grade
     * `3.5` as `35`.
     */
    @Test
    fun `a decimal point between digits is never swallowed`() {
        assertEquals("3.5", AnswerChecker.normalize("3.5"))
        assertFalse(AnswerChecker.matchesAny(listOf("35"), "3.5"))
        assertFalse(AnswerChecker.matchesAny(listOf("3.5"), "35"))
        // A sentence-final dot is still punctuation: `3.` ≡ `three`.
        assertEquals("three", AnswerChecker.normalize("3."))
        assertTrue(AnswerChecker.matchesAny(listOf("three"), "3."))
        // Ordinals stay glued to their letters (never `thirtyth`).
        assertEquals("20th", AnswerChecker.normalize("20th"))
        assertEquals("three", AnswerChecker.normalize("3"))
    }

    // --- A-2: near-miss (almost) verdict -------------------------------------

    @Test
    fun `one edit away from a long word is almost, not correct`() {
        val ex = vocabBlank("welcome")
        assertEquals(AnswerChecker.Verdict.ALMOST, AnswerChecker.grade(ex, "welcom"))
        assertEquals(AnswerChecker.Verdict.ALMOST, AnswerChecker.grade(ex, "welcoe"))
        assertEquals(AnswerChecker.Verdict.CORRECT, AnswerChecker.grade(ex, "Welcome!"))
        assertEquals(AnswerChecker.Verdict.WRONG, AnswerChecker.grade(ex, "goodbye"))
        // "almost" is never an accept — isCorrect must stay false.
        assertFalse(AnswerChecker.isCorrect(ex, "welcom"))
    }

    @Test
    fun `near-miss ignores short words where one edit changes the word`() {
        // car/cat are 3 letters — one edit must NOT count as almost.
        assertEquals(AnswerChecker.Verdict.WRONG, AnswerChecker.grade(vocabBlank("car"), "cat"))
        // Two edits are wrong even on long words (h→y substitution + w insertion).
        assertEquals(AnswerChecker.Verdict.WRONG, AnswerChecker.grade(vocabBlank("hello"), "yellow"))
    }

    /**
     * A-2b: a grammar item must never claim the learner made a spelling slip. The
     * canonical pair is `listen` / `listens` — one letter apart, but the error is a
     * missing third-person `-s`, so "check the spelling" sends them looking in the
     * wrong place. The item's own explanation says what the form should have been.
     */
    @Test
    fun `a grammar blank never reports a near-miss spelling`() {
        val thirdPerson = Exercise.FillBlank(
            id = "x",
            lessonId = "l",
            sentence = "She ______ to music every evening.",
            accepted = listOf("listens"),
        )
        assertEquals(Skill.GRAMMAR, AnswerChecker.skillOf(thirdPerson))
        assertEquals(AnswerChecker.Verdict.WRONG, AnswerChecker.grade(thirdPerson, "listen"))
        // ...while the same edit on a *vocabulary* target is still a spelling hint.
        assertEquals(
            AnswerChecker.Verdict.ALMOST,
            AnswerChecker.grade(vocabBlank("listens"), "listen"),
        )

        // A grammar target is judged as a form: the correct form is still correct.
        assertEquals(AnswerChecker.Verdict.CORRECT, AnswerChecker.grade(thirdPerson, "Listens."))
    }

    @Test
    fun `list-based grade matches exercise-based rules for review production`() {
        // The review session grades a bare word (no Exercise) — same verdicts.
        assertEquals(AnswerChecker.Verdict.CORRECT, AnswerChecker.grade(listOf("welcome"), "Welcome!"))
        assertEquals(AnswerChecker.Verdict.ALMOST, AnswerChecker.grade(listOf("welcome"), "welcom"))
        assertEquals(AnswerChecker.Verdict.WRONG, AnswerChecker.grade(listOf("welcome"), "hi"))
        // Digits and multi-word phrases behave like the lesson path.
        assertEquals(AnswerChecker.Verdict.CORRECT, AnswerChecker.grade(listOf("three"), "3"))
        assertEquals(AnswerChecker.Verdict.WRONG, AnswerChecker.grade(listOf("look forward"), "lok forward"))
    }

    @Test
    fun `near-miss never fires for multiple choice or multi-word answers`() {
        val mc = Exercise.MultipleChoice(
            id = "x", lessonId = "l", question = "q", questionFa = null,
            options = listOf("welcome", "goodbye"), correctIndex = 0,
        )
        // Options are picked, not typed — no almost state in MC.
        assertEquals(AnswerChecker.Verdict.WRONG, AnswerChecker.grade(mc, "welcom"))

        // Sentence-length answers stay exact: edit distance over a phrase
        // would bless sloppy paraphrases as "almost".
        val translation = Exercise.Translation(
            id = "x", lessonId = "l", prompt = "سلام", accepted = listOf("hello world"),
        )
        assertEquals(AnswerChecker.Verdict.WRONG, AnswerChecker.grade(translation, "hello wrld"))
    }

    @Test
    fun `skill tagging wins over the heuristic`() {
        val tagged = Exercise.MultipleChoice(
            id = "x",
            lessonId = "l",
            question = "What does \"apple\" mean?",
            questionFa = null,
            options = listOf("\u0628\u0627\u0628", "\u0645\u0648\u0632"),
            correctIndex = 0,
            skill = Skill.READING,
        )
        assertEquals(Skill.READING, AnswerChecker.skillOf(tagged))
    }

    @Test
    fun `heuristic classifies persian-option mc as vocabulary and blanks as grammar`() {
        val vocabMc = Exercise.MultipleChoice(
            id = "x",
            lessonId = "l",
            question = "What does \"apple\" mean?",
            questionFa = null,
            options = listOf("\u0633\u06cc\u0628", "\u0645\u0648\u0632"),
            correctIndex = 0,
        )
        assertEquals(Skill.VOCABULARY, AnswerChecker.skillOf(vocabMc))

        val grammarMc = Exercise.MultipleChoice(
            id = "x",
            lessonId = "l",
            question = "She ____ to work.",
            questionFa = null,
            options = listOf("go", "goes"),
            correctIndex = 1,
        )
        assertEquals(Skill.GRAMMAR, AnswerChecker.skillOf(grammarMc))

        assertEquals(
            Skill.WRITING,
            AnswerChecker.skillOf(
                Exercise.Translation(id = "x", lessonId = "l", prompt = "\u0633\u0644\u0627\u0645", accepted = listOf("hello")),
            ),
        )
    }
}
