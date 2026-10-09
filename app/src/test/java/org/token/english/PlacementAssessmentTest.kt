package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.engine.PlacementAnswer
import org.token.english.domain.engine.PlacementAssessmentEngine
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Skill
import org.token.english.domain.usecase.ScorePlacementUseCase

/**
 * The placement test used to keep only a single CEFR level (P1-1). These tests pin
 * the skill-assessment contract: the overall level must stay identical to the
 * existing band scorer (one scoring rule in the app), and the per-skill read must
 * be an honest count — no fabricated per-skill CEFR band.
 */
class PlacementAssessmentTest {

    private val engine = PlacementAssessmentEngine()

    private fun answer(skill: Skill, correct: Boolean, level: LearningLevel = LearningLevel.A1) =
        PlacementAnswer(level = level, skill = skill, correct = correct)

    /** Six bands of six, all correct — the same shape a real full placement run has. */
    @Test
    fun `all correct answers reach C2 and agree with the band scorer`() {
        val answers = (1..36).map { answer(Skill.GRAMMAR, correct = true) }
        val assessment = engine(answers)

        assertEquals(LearningLevel.C2, assessment.level)
        assertEquals(ScorePlacementUseCase()(answers.map { it.correct }), assessment.level)
        assertEquals(36, assessment.answered)
    }

    @Test
    fun `a failed first band floors the level at A1`() {
        // A1 band: 1/6 correct — fails the 2/3 band bar, so the walk stops.
        val answers = List(6) { answer(Skill.GRAMMAR, correct = it == 0) } +
            List(30) { answer(Skill.GRAMMAR, correct = true) }
        assertEquals(LearningLevel.A1, engine(answers).level)
    }

    @Test
    fun `the per-skill read counts the questions of each skill`() {
        val answers = listOf(
            answer(Skill.GRAMMAR, true),
            answer(Skill.GRAMMAR, true),
            answer(Skill.GRAMMAR, false),
            answer(Skill.VOCABULARY, true),
            answer(Skill.VOCABULARY, false),
        )
        val assessment = engine(answers)

        // `Skill` declaration order, and only measured skills appear.
        assertEquals(listOf(Skill.VOCABULARY, Skill.GRAMMAR), assessment.bySkill.map { it.skill })
        val grammar = assessment.reportFor(Skill.GRAMMAR)!!
        assertEquals(3, grammar.asked)
        assertEquals(2, grammar.correct)
        val vocabulary = assessment.reportFor(Skill.VOCABULARY)!!
        assertEquals(2, vocabulary.asked)
        assertEquals(1, vocabulary.correct)
        assertNull("LISTENING was never measured, so it must not be reported", assessment.reportFor(Skill.LISTENING))
    }

    @Test
    fun `focus lists weak skills weakest first and strengths the rest`() {
        val answers = listOf(
            answer(Skill.GRAMMAR, false),
            answer(Skill.GRAMMAR, false),
            answer(Skill.GRAMMAR, false),
            answer(Skill.VOCABULARY, true),
            answer(Skill.VOCABULARY, true),
            answer(Skill.VOCABULARY, true),
            answer(Skill.VOCABULARY, true),
        )
        val assessment = engine(answers)

        assertEquals(listOf(Skill.VOCABULARY), assessment.strengths)
        assertEquals(listOf(Skill.GRAMMAR), assessment.focusSkills)
    }

    @Test
    fun `a skill at or above the strength threshold is not a weakness`() {
        // 3/4 = 0.75 → exactly the threshold counts as a strength.
        val answers = listOf(
            answer(Skill.GRAMMAR, true),
            answer(Skill.GRAMMAR, true),
            answer(Skill.GRAMMAR, true),
            answer(Skill.GRAMMAR, false),
        )
        val assessment = engine(answers)
        assertFalse(assessment.reportFor(Skill.GRAMMAR)!!.isWeak)
        assertTrue(assessment.focusSkills.isEmpty())
    }

    @Test
    fun `an empty test is a harmless A1 with no skills`() {
        val assessment = engine(emptyList())
        assertEquals(LearningLevel.A1, assessment.level)
        assertEquals(0, assessment.answered)
        assertTrue(assessment.bySkill.isEmpty())
        assertTrue(assessment.strengths.isEmpty())
        assertTrue(assessment.focusSkills.isEmpty())
    }
}
