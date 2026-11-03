package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.LearningStage
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.LessonStagePlan
import org.token.english.domain.model.LessonStageProgress
import org.token.english.domain.model.LessonStageState
import org.token.english.domain.model.Skill
import org.token.english.domain.model.VocabularyItem

/**
 * The guided lesson's backbone (checklist Phase 1): eight ordered stages, derived
 * from the content that already ships, with completion that cannot be faked by
 * opening a screen.
 *
 * These are the rules the UI must not reinvent — which step an exercise belongs
 * to, which steps a lesson actually has, what the progress bar counts, and the
 * difference between "completed" and "skipped".
 */
class LearningStagePlanTest {

    private val lessonId = "a2.test.lesson-01"

    private fun lesson(tip: String = "## قاعده\nمتن") = Lesson(
        id = lessonId,
        level = LearningLevel.A2,
        title = "Test",
        titleFa = "آزمون",
        topic = "Testing",
        estimatedMinutes = 6,
        order = 1,
        grammarTipFa = tip,
    )

    private fun word(id: String) = VocabularyItem(
        id = id,
        word = "word",
        translation = "واژه",
        definition = "def",
        pronunciation = "/w/",
        level = LearningLevel.A2,
        partOfSpeech = "noun",
        examples = emptyList(),
        collocations = emptyList(),
        lessonId = lessonId,
    )

    private fun exercise(id: String) = Exercise.MultipleChoice(
        id = id,
        lessonId = lessonId,
        question = "q",
        questionFa = "سؤال",
        options = listOf("a", "b", "c", "d"),
        correctIndex = 0,
    )

    private fun listening(id: String = "listen") = Exercise.Listening(
        id = id,
        lessonId = lessonId,
        audioText = "hello",
        accepted = listOf("hello"),
        skill = Skill.LISTENING,
    )

    private fun production(id: String = "prod") = Exercise.SentenceProduction(
        id = id,
        lessonId = lessonId,
        prompt = "بگو",
        mustUse = listOf("want", "to"),
        minWords = 4,
        accepted = listOf("i want to go"),
    )

    private fun plan(
        tip: String = "## قاعده\nمتن",
        words: List<VocabularyItem> = listOf(word("w1")),
        exercises: List<Exercise> = listOf(exercise("ex1")),
        hasDialogue: Boolean = true,
    ) = LessonStagePlan.of(lesson(tip), words, exercises, hasDialogue)

    // ------------------------------------------------------------------ ordering

    @Test
    fun `the eight stages are ordered as the learner walks them`() {
        assertEquals(
            listOf(
                LearningStage.CONTEXT, LearningStage.GRAMMAR, LearningStage.VOCABULARY,
                LearningStage.PRACTICE, LearningStage.LISTENING, LearningStage.WRITING,
                LearningStage.SPEAKING, LearningStage.REVIEW,
            ),
            LearningStage.ordered,
        )
        assertEquals(8, LearningStage.total)
        assertEquals(1, LearningStage.CONTEXT.number)
        assertEquals(8, LearningStage.REVIEW.number)
    }

    @Test
    fun `every exercise lands on exactly one stage`() {
        assertEquals(LearningStage.LISTENING, LearningStage.of(listening()))
        assertEquals(LearningStage.WRITING, LearningStage.of(production()))
        assertEquals(
            LearningStage.SPEAKING,
            LearningStage.of(
                Exercise.Speaking(
                    id = "sp", lessonId = lessonId, prompt = "say it",
                    referenceAnswers = listOf("hello"),
                ),
            ),
        )
        // Recognition, blanks, recall and translation are all controlled practice.
        assertEquals(LearningStage.PRACTICE, LearningStage.of(exercise("mc")))
        assertEquals(
            LearningStage.PRACTICE,
            LearningStage.of(
                Exercise.WordRecall(
                    id = "wr", lessonId = lessonId, prompt = "واژه", accepted = listOf("word"),
                ),
            ),
        )
    }

    @Test
    fun `a stable id survives a rename of the enum`() {
        LearningStage.ordered.forEach { stage ->
            assertEquals(stage, LearningStage.fromId(stage.id))
        }
        assertNull(LearningStage.fromId("not-a-stage"))
    }

    // ------------------------------------------------------------------ the plan

    @Test
    fun `the plan only contains stages the lesson can actually run`() {
        val minimal = plan(exercises = listOf(exercise("ex1")), hasDialogue = false)
        assertEquals(
            listOf(
                LearningStage.CONTEXT, LearningStage.GRAMMAR, LearningStage.VOCABULARY,
                LearningStage.PRACTICE, LearningStage.REVIEW,
            ),
            minimal.stages.map { it.stage },
        )
        assertNull(minimal.unitOf(LearningStage.LISTENING))
        assertNull(minimal.unitOf(LearningStage.SPEAKING))
    }

    @Test
    fun `a lesson without a grammar tip has no grammar step`() {
        val p = plan(tip = "")
        assertNull(p.unitOf(LearningStage.GRAMMAR))
    }

    @Test
    fun `a lesson without vocabulary has no vocabulary step`() {
        assertNull(plan(words = emptyList()).unitOf(LearningStage.VOCABULARY))
    }

    @Test
    fun `exercises are grouped into their stage, in authored order`() {
        val p = plan(
            exercises = listOf(
                exercise("ex1"), listening("l1"), production("p1"), exercise("ex2"), listening("l2"),
            ),
            hasDialogue = false,
        )
        assertEquals(listOf("ex1", "ex2"), p.unitOf(LearningStage.PRACTICE)?.exercises?.map { it.id })
        assertEquals(listOf("l1", "l2"), p.unitOf(LearningStage.LISTENING)?.exercises?.map { it.id })
        assertEquals(listOf("p1"), p.unitOf(LearningStage.WRITING)?.exercises?.map { it.id })
    }

    @Test
    fun `teaching stages carry no exercises, practice stages do`() {
        val p = plan()
        assertTrue(p.unitOf(LearningStage.CONTEXT)!!.isTeaching)
        assertTrue(p.unitOf(LearningStage.GRAMMAR)!!.isTeaching)
        assertTrue(p.unitOf(LearningStage.VOCABULARY)!!.isTeaching)
        assertFalse(p.unitOf(LearningStage.PRACTICE)!!.isTeaching)
    }

    @Test
    fun `next and previous follow the plan, not the enum`() {
        val p = plan(hasDialogue = false)
        assertEquals(LearningStage.GRAMMAR, p.next(LearningStage.CONTEXT))
        assertEquals(LearningStage.VOCABULARY, p.next(LearningStage.GRAMMAR))
        assertEquals(LearningStage.PRACTICE, p.next(LearningStage.VOCABULARY))
        // No listening stage in this lesson, so practice leads straight to review.
        assertEquals(LearningStage.REVIEW, p.next(LearningStage.PRACTICE))
        assertNull(p.next(LearningStage.REVIEW))
        assertEquals(LearningStage.PRACTICE, p.previous(LearningStage.REVIEW))
        assertNull(p.previous(LearningStage.CONTEXT))
    }

    // ------------------------------------------------------------------ progress

    @Test
    fun `progress counts completed required stages only`() {
        var state = LessonStageState(plan())
        val required = state.requiredCount
        assertEquals(0, state.percent)

        state = state.withCompleted(LearningStage.CONTEXT)
        assertEquals(100 / required, state.percent)

        // Skipping the optional listening stage does not move the bar.
        state = state.withSkipped(LearningStage.LISTENING)
        assertTrue(state.isSkipped(LearningStage.LISTENING))
        assertFalse(state.isCompleted(LearningStage.LISTENING))
        assertEquals(100 / required, state.percent)
    }

    @Test
    fun `a skipped stage is not mastery`() {
        val state = LessonStageState(plan()).withSkipped(LearningStage.LISTENING)
        val entry = state.progress.first { it.stage == LearningStage.LISTENING }
        assertEquals(LessonStageProgress(LearningStage.LISTENING, skipped = true), entry)
        assertFalse(entry.countsAsDone)
    }

    @Test
    fun `opening a stage is not completing it`() {
        val state = LessonStageState(plan()).withActive(LearningStage.VOCABULARY)
        assertEquals(LearningStage.VOCABULARY, state.active)
        assertEquals(0, state.percent)
    }

    @Test
    fun `a stage behind the active one stays reachable, one ahead does not`() {
        val p = plan()
        val state = LessonStageState(p).withActive(LearningStage.VOCABULARY)
        assertTrue(state.isAccessible(LearningStage.CONTEXT))
        assertTrue(state.isAccessible(LearningStage.VOCABULARY))
        assertFalse(state.isAccessible(LearningStage.PRACTICE))
    }

    @Test
    fun `the lesson is finished only when every stage is completed or skipped`() {
        var state = LessonStageState(plan())
        LearningStage.ordered.forEach { stage ->
            if (state.plan.unitOf(stage) != null) {
                state = if (stage.required) state.withCompleted(stage) else state.withSkipped(stage)
            }
        }
        assertTrue(state.isFinished)
        assertEquals(100, state.percent)
    }
}
