package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.engine.ClassifiedAnswer
import org.token.english.domain.engine.MissCategory
import org.token.english.domain.engine.MissTaxonomy
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.GradedCard
import org.token.english.domain.model.LessonAttempt
import org.token.english.domain.model.ReviewContentType
import org.token.english.domain.model.ReviewResult
import org.token.english.domain.model.Skill

/**
 * The error taxonomy (roadmap): a miss is classified by the skill its exercise was
 * authored for, never by a guess about why the learner chose an answer.
 *
 * What this proves: every exercise shape lands in exactly one category, the fold counts
 * attempts and misses per category over the window, content that is no longer shipped is
 * skipped rather than classified, and the "weakest" label follows miss *count* so a
 * one-of-two fluke cannot outrank a real pattern.
 */
class MissTaxonomyTest {

    private val lessonId = "a1.test.lesson-01"

    private fun choice(id: String, skill: Skill?) = Exercise.MultipleChoice(
        id = id,
        lessonId = lessonId,
        question = "Choose",
        questionFa = null,
        options = listOf("right", "wrong"),
        correctIndex = 0,
        skill = skill,
    )

    private fun listening(id: String) = Exercise.Listening(
        id = id,
        lessonId = lessonId,
        audioText = "Listen to me",
        accepted = listOf("listen to me"),
        skill = Skill.LISTENING,
    )

    private fun production(id: String) = Exercise.Translation(
        id = id,
        lessonId = lessonId,
        prompt = "من اینجا هستم",
        accepted = listOf("I am here"),
        skill = Skill.WRITING,
    )

    private fun attempt(exercise: Exercise, correct: Boolean, at: Long) = LessonAttempt(
        lessonId = exercise.lessonId,
        stageId = "practice",
        exerciseId = exercise.id,
        answer = if (correct) "right" else "wrong",
        correct = correct,
        at = at,
    )

    @Test
    fun `every skill maps to one category, and production is not grammar`() {
        assertEquals(MissCategory.VOCABULARY, MissTaxonomy.categoryOf(Skill.VOCABULARY))
        assertEquals(MissCategory.GRAMMAR, MissTaxonomy.categoryOf(Skill.GRAMMAR))
        assertEquals(MissCategory.COMPREHENSION, MissTaxonomy.categoryOf(Skill.READING))
        assertEquals(MissCategory.COMPREHENSION, MissTaxonomy.categoryOf(Skill.LISTENING))
        // Building or saying a sentence is production: a miss there is not evidence that
        // the rule is unknown, so it must not be folded into grammar.
        assertEquals(MissCategory.PRODUCTION, MissTaxonomy.categoryOf(Skill.WRITING))
        assertEquals(MissCategory.PRODUCTION, MissTaxonomy.categoryOf(Skill.SPEAKING))
    }

    @Test
    fun `the profile counts each category and names the one carrying the most misses`() {
        val vocab = listOf(choice("v.1", Skill.VOCABULARY), choice("v.2", Skill.VOCABULARY), choice("v.3", Skill.VOCABULARY))
        val grammar = listOf(choice("g.1", Skill.GRAMMAR), choice("g.2", Skill.GRAMMAR))
        val exercises = (vocab + grammar).associateBy { it.id }
        val attempts = listOf(
            attempt(vocab[0], correct = false, at = 1),
            attempt(vocab[1], correct = true, at = 2),
            attempt(vocab[2], correct = true, at = 3),
            attempt(grammar[0], correct = false, at = 4),
            attempt(grammar[1], correct = false, at = 5),
        )

        val profile = MissTaxonomy.profile(MissTaxonomy.classify(attempts, exercises))!!

        assertEquals(5, profile.attempts)
        assertEquals(3, profile.misses)
        assertEquals(listOf(MissCategory.VOCABULARY, MissCategory.GRAMMAR), profile.categories.map { it.category })
        val vocabulary = profile.categories.first { it.category == MissCategory.VOCABULARY }
        assertEquals(3, vocabulary.attempts)
        assertEquals(1, vocabulary.misses)
        assertEquals(2f / 3f, vocabulary.accuracy!!, 0.0001f)
        assertEquals(
            "two of two grammar items missed outweighs one of three vocabulary items",
            MissCategory.GRAMMAR,
            profile.weakest?.category,
        )
    }

    @Test
    fun `a written sentence lands in production, not in the grammar bucket`() {
        val written = production("p.1")
        val grammar = choice("g.1", Skill.GRAMMAR)
        val exercises = mapOf(written.id to written, grammar.id to grammar)
        val attempts = (1..4).map { attempt(written, correct = false, at = it.toLong()) } +
            attempt(grammar, correct = true, at = 5)

        val profile = MissTaxonomy.profile(MissTaxonomy.classify(attempts, exercises))!!

        assertEquals(
            listOf(MissCategory.GRAMMAR, MissCategory.PRODUCTION),
            profile.categories.map { it.category },
        )
        assertEquals(MissCategory.PRODUCTION, profile.weakest?.category)
    }

    @Test
    fun `a category with no answers is absent instead of reading as clean`() {
        val exercises = listOf(choice("v.1", Skill.VOCABULARY), listening("l.1"))
        val attempts = (1..5).map { index ->
            attempt(if (index % 2 == 0) exercises[0] else exercises[1], correct = index != 1, at = index.toLong())
        }

        val profile = MissTaxonomy.profile(MissTaxonomy.classify(attempts, exercises.associateBy { it.id }))!!

        assertEquals(
            "grammar and production were never answered, so they are not 100 %",
            listOf(MissCategory.VOCABULARY, MissCategory.COMPREHENSION),
            profile.categories.map { it.category },
        )
        assertEquals(MissCategory.COMPREHENSION, profile.weakest?.category)
    }

    @Test
    fun `answers to content the bundle no longer ships are skipped, not guessed`() {
        val known = choice("v.1", Skill.VOCABULARY)
        val stale = choice("v.gone", Skill.VOCABULARY)
        val attempts = (1..4).map { attempt(known, correct = true, at = it.toLong()) } +
            (5..6).map { attempt(stale, correct = false, at = it.toLong()) }

        assertNull(
            "four classifiable answers are below the evidence bar — the stale pair cannot pad it",
            MissTaxonomy.profile(MissTaxonomy.classify(attempts, mapOf(known.id to known))),
        )
        assertEquals(
            "and once enough real answers exist, only they are counted",
            5,
            MissTaxonomy.profile(
                MissTaxonomy.classify(attempts + attempt(known, true, 7), mapOf(known.id to known)),
            )!!.attempts,
        )
    }

    @Test
    fun `the evidence bar is attempts, not misses`() {
        val exercise = choice("v.1", Skill.VOCABULARY)
        val clean = (1..5).map { attempt(exercise, correct = true, at = it.toLong()) }

        val profile = MissTaxonomy.profile(MissTaxonomy.classify(clean, mapOf(exercise.id to exercise)))!!

        assertEquals("a clean window still reports the mix, with zero misses", 0, profile.misses)
        assertNull(
            "but one answer is not a pattern",
            MissTaxonomy.profile(MissTaxonomy.classify(clean.take(1), mapOf(exercise.id to exercise))),
        )
    }

    // ---------------------------------------------------------------- review cards

    private fun card(result: ReviewResult, type: ReviewContentType?, at: Long) =
        GradedCard(result = result, type = type, at = at)

    @Test
    fun `a card can only speak for vocabulary or grammar`() {
        // A recall card carries its item's content type and nothing finer. It is not a
        // guess: a word card really does test a word, and a rule card a rule.
        assertEquals(MissCategory.VOCABULARY, MissTaxonomy.categoryOf(ReviewContentType.VOCABULARY))
        assertEquals(MissCategory.GRAMMAR, MissTaxonomy.categoryOf(ReviewContentType.GRAMMAR))
    }

    @Test
    fun `good and easy are hits, hard and again are not`() {
        val cards = listOf(
            card(ReviewResult.GOOD, ReviewContentType.VOCABULARY, 1),
            card(ReviewResult.EASY, ReviewContentType.VOCABULARY, 2),
            card(ReviewResult.HARD, ReviewContentType.GRAMMAR, 3),
            card(ReviewResult.AGAIN, ReviewContentType.GRAMMAR, 4),
            card(ReviewResult.GOOD, ReviewContentType.GRAMMAR, 5),
        )

        val profile = MissTaxonomy.profile(MissTaxonomy.classify(cards))!!

        assertEquals(5, profile.attempts)
        assertEquals("the grammar card graded HARD and the one graded AGAIN", 2, profile.misses)
        assertEquals(
            listOf(MissCategory.VOCABULARY, MissCategory.GRAMMAR),
            profile.categories.map { it.category },
        )
        assertEquals(MissCategory.GRAMMAR, profile.weakest?.category)
    }

    @Test
    fun `the mix steering is one rule, and a clean window steers nothing`() {
        // The day's plan and the review screen's due list both ask this function, so a
        // change here moves both — that is the point of it living in the taxonomy rather
        // than in two comparators.
        val clean = MissTaxonomy.profile(
            (1..5).map { ClassifiedAnswer(MissCategory.VOCABULARY, correct = true, at = it.toLong()) },
        )
        assertNull("five right answers are not a weakness", MissTaxonomy.weakestCategory(clean))
        assertTrue("nothing steers, so there is no key to sort by", MissTaxonomy.rank(emptyList(), null).isEmpty())

        val gap = MissTaxonomy.profile(
            (1..5).map { ClassifiedAnswer(MissCategory.GRAMMAR, correct = false, at = it.toLong()) },
        )
        assertEquals(MissCategory.GRAMMAR, MissTaxonomy.weakestCategory(gap))
    }

    @Test
    fun `a card whose item left the queue is dropped instead of guessed`() {
        val cards = (1..6).map { card(ReviewResult.AGAIN, type = null, at = it.toLong()) }

        assertTrue("nothing to classify", MissTaxonomy.classify(cards).isEmpty())
        assertNull(
            "six unclassifiable grades cannot clear the evidence bar",
            MissTaxonomy.profile(MissTaxonomy.classify(cards)),
        )
    }
}
