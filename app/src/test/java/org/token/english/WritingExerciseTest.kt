package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.ExerciseFormat
import org.token.english.domain.model.LearningStage
import org.token.english.domain.model.MasteryDimension
import org.json.JSONObject
import org.token.english.data.content.ContentParser
import org.token.english.domain.engine.ExerciseDimension
import org.token.english.domain.model.Skill

/**
 * The `writing` exercise type — a short connected text.
 *
 * What is pinned here is the difference from a produced sentence, because that is
 * the whole reason the type exists: anchors **anywhere** (a paragraph's connective
 * opens the second sentence), a word floor, and a **sentence** floor. An offline
 * grader cannot judge prose, so it must not pretend to; it checks the shape that was
 * asked for and nothing else.
 */
class WritingExerciseTest {

    private fun writing(
        mustUse: List<String> = listOf("has not been", "refund"),
        minWords: Int = 14,
        minSentences: Int = 3,
        accepted: List<String> = listOf(
            "The order has not been delivered yet. I have contacted support twice. I would like a full refund.",
        ),
    ) = Exercise.Writing(
        id = "b2.test.wr.01",
        lessonId = "b2.problems.lesson-01",
        prompt = "یک شکایت رسمی کوتاه بنویس",
        mustUse = mustUse,
        minWords = minWords,
        minSentences = minSentences,
        accepted = accepted,
        skill = Skill.WRITING,
    )

    @Test
    fun `the exemplar passes the bar the learner is held to`() {
        val item = writing()

        assertTrue(AnswerChecker.isCorrect(item, item.accepted.first()))
        assertEquals(AnswerChecker.Verdict.CORRECT, AnswerChecker.grade(item, item.accepted.first()))
    }

    @Test
    fun `an answer that is new wording but uses every anchor and floor passes`() {
        // The point of a text is that the learner writes their own: this shares no
        // sentence with the exemplar, only the structure and the lesson's words.
        val answer = "My parcel has not been delivered yet. I wrote to the shop twice. Please send me a full refund."

        assertTrue(AnswerChecker.isCorrect(writing(), answer))
    }

    @Test
    fun `a single long sentence does not pass as a text`() {
        // Every anchor and every word the floor asks for, in one run-on — the shape
        // that was asked for is two or three sentences, so this is not the answer.
        val answer = "my parcel has not been delivered yet and i wrote twice and i want a refund"

        assertFalse(AnswerChecker.isCorrect(writing(), answer))
    }

    @Test
    fun `too few words is not a text`() {
        val answer = "It has not been delivered. Refund please."

        assertFalse(AnswerChecker.isCorrect(writing(minWords = 14), answer))
    }

    @Test
    fun `a missing anchor fails even when the text is long enough`() {
        val answer = "The parcel is late again. I wrote twice and nobody answered. Send my money back now please."

        assertFalse("no 'has not been', no 'refund'", AnswerChecker.isCorrect(writing(), answer))
    }

    @Test
    fun `anchor order is not part of the contract, but presence is`() {
        // The refund request opens the text here; a paragraph's structure may sit in
        // any sentence, so the grader must accept it (unlike a produced sentence,
        // where the order *is* the structure under test).
        val answer = "I would like a refund. The order has not been delivered. I have written twice."

        assertTrue(AnswerChecker.isCorrect(writing(), answer))
    }

    @Test
    fun `a two-word anchor is a phrase, not two loose words`() {
        // `was collected` is the passive the item is about. A text that says «the
        // survey was useful» and then «we collected the forms» contains both words and
        // neither construction — flattening an anchor into independent words made that
        // a pass, which is the whole gap this closes.
        val item = writing(mustUse = listOf("was collected"), minWords = 8, minSentences = 2)

        assertFalse(
            "the passive never appears",
            AnswerChecker.isCorrect(item, "The survey was useful. We collected the forms in May."),
        )
        assertTrue(
            "the same words, adjacent, are the structure",
            AnswerChecker.isCorrect(item, "The data was collected in May. It comes from three offices."),
        )
    }

    @Test
    fun `an anchor's words in the other order are not the anchor`() {
        val item = writing(mustUse = listOf("not only"), minWords = 8, minSentences = 2)

        assertFalse(
            "`only` then `not` is a different sentence",
            AnswerChecker.isCorrect(item, "The study only proves one thing. It does not prove a link."),
        )
        assertTrue(
            "the inversion, as written",
            AnswerChecker.isCorrect(
                item,
                "Not only does the study fail to prove a link. Its sample was also far too small.",
            ),
        )
    }

    @Test
    fun `an auxiliary alone no longer stands in for the tense`() {
        // `have` was satisfied by possession and `had` by a plain past tense, so the two
        // items that test a perfect tense asked for a word rather than a structure.
        val perfect = writing(mustUse = listOf("have travelled"), minWords = 8, minSentences = 2)

        assertFalse(
            "a family text does not answer a travel-experience item",
            AnswerChecker.isCorrect(perfect, "I have a small family. My sister is a student."),
        )
        assertTrue(
            "the present perfect, as the exemplar writes it",
            AnswerChecker.isCorrect(
                perfect,
                "I have travelled abroad twice. My most memorable journey was in Turkey.",
            ),
        )
    }

    @Test
    fun `a missing final full stop still counts as a sentence`() {
        // Forgetting the last period is punctuation, not a missing sentence.
        val answer = "The order has not been delivered. I have written twice. I would like a refund"

        assertEquals(3, AnswerChecker.sentencesIn(answer))
        assertTrue(AnswerChecker.isCorrect(writing(), answer))
    }

    @Test
    fun `a contraction on the learner's side still meets the anchor`() {
        val answer = "The parcel hasn't been delivered yet. I wrote to the shop twice. Please send me a refund."

        assertTrue(AnswerChecker.isCorrect(writing(), answer))
    }

    @Test
    fun `a text is never a spelling near-miss`() {
        // ALMOST is a single-word signal; a paragraph is not a typo.
        assertFalse(AnswerChecker.isAlmost(writing(), "teh order has not been delivered. i wrote twice. refund"))
    }

    @Test
    fun `a text is its own evidence format and production dimension`() {
        val item = writing()

        assertEquals(ExerciseFormat.WRITING_TEXT, AnswerChecker.formatOf(item))
        assertEquals(Skill.WRITING, AnswerChecker.skillOf(item))
        assertEquals(MasteryDimension.PRODUCTION, ExerciseDimension.dimensionOf(item))
        // The same step as a produced sentence: both are written production.
        assertEquals(LearningStage.WRITING, LearningStage.of(item))
    }

    @Test
    fun `the bundle's own json parses into a writing item`() {
        // A type the parser does not know is dropped at seed time and the lesson
        // silently loses the step, so the two lists have to stay in step.
        val json = JSONObject(
            """
            {"id": "l1.ex.02", "lessonId": "l1", "type": "writing", "order": 6, "difficulty": 3,
             "prompt": "بنویس", "hintFa": "با have شروع کن",
             "mustUse": ["have"], "minWords": 8, "minSentences": 2,
             "accepted": ["I have a brother. He is a nurse."], "skill": "WRITING",
             "explanationFa": "توضیح"}
            """.trimIndent(),
        )

        val parsed = ContentParser.parseExercise(json, "l1") as Exercise.Writing

        assertEquals(listOf("have"), parsed.mustUse)
        assertEquals(8, parsed.minWords)
        assertEquals(2, parsed.minSentences)
        assertEquals("با have شروع کن", parsed.hintFa)
        assertEquals(3, parsed.difficulty)
        assertEquals(Skill.WRITING, parsed.skill)
    }

    @Test
    fun `the exemplar is the reference shown after a miss`() {
        val item = writing()

        assertEquals(item.accepted.first(), AnswerChecker.correctAnswerText(item))
    }
}
