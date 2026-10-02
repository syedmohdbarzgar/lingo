package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.engine.DefaultLearningPlanner
import org.token.english.domain.engine.DefaultMasteryEngine
import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.Skill

class DomainEngineTest {

    private val engine = DefaultMasteryEngine()

    @Test
    fun `correct attempt raises mastery and wrong attempt lowers it`() {
        val up = engine.update(0.5f, correct = true)
        assertTrue(up > 0.5f)
        val down = engine.update(up, correct = false)
        assertTrue(down < up)
    }

    @Test
    fun `mastery stays within bounds`() {
        var value = 0f
        repeat(50) { value = engine.update(value, correct = true) }
        assertTrue(value <= 1f)
        repeat(50) { value = engine.update(value, correct = false) }
        assertTrue(value >= 0f)
    }

    @Test
    fun `answer checking is case and punctuation tolerant`() {
        val exercise = Exercise.FillBlank(
            id = "x",
            lessonId = "l",
            sentence = "I usually drink coffee.",
            accepted = listOf("drink"),
        )
        assertTrue(AnswerChecker.isCorrect(exercise, "  Drink! "))
        assertFalse(AnswerChecker.isCorrect(exercise, "eats"))
    }

    @Test
    fun `answer checking normalizes persian text`() {
        val exercise = Exercise.Translation(
            id = "x",
            lessonId = "l",
            prompt = "من معمولاً صبح بیدار می‌شوم.",
            accepted = listOf("من معمولاً صبح بیدار میشوم"),
        )
        // ZWNJ vs plain ي/ک handling: same string after normalization
        assertTrue(AnswerChecker.isCorrect(exercise, "من معمولاً صبح بیدار می‌شوم"))
    }

    @Test
    fun `multiple choice compares against the correct option`() {
        val exercise = Exercise.MultipleChoice(
            id = "x",
            lessonId = "l",
            question = "q",
            questionFa = null,
            options = listOf("a", "b", "c"),
            correctIndex = 1,
        )
        assertTrue(AnswerChecker.isCorrect(exercise, "B"))
        assertFalse(AnswerChecker.isCorrect(exercise, "A"))
    }

    @Test
    fun `planner recommends the weakest skills up to the limit`() {
        val planner = DefaultLearningPlanner(maxRecommendedSkills = 2)
        val plan = planner.createPlan(
            dueReviewCount = 5,
            nextLesson = Lesson(
                id = "a1.x", level = LearningLevel.A1, title = "T", titleFa = "ت",
                topic = "Basics", estimatedMinutes = 10, order = 1,
            ),
            masteryBySkill = mapOf(
                Skill.LISTENING to 0.2f,
                Skill.SPEAKING to 0.3f,
                Skill.VOCABULARY to 0.9f,
            ),
            targetMinutes = 15,
            todayStudySeconds = 0,
        )
        assertEquals(5, plan.dueReviewCount)
        assertEquals(2, plan.recommendedSkills.size)
        assertEquals(Skill.LISTENING, plan.recommendedSkills.first())
        assertTrue(plan.recommendedSkills.none { it == Skill.VOCABULARY })
        assertEquals("a1.x", plan.nextLesson?.id)
    }
}
