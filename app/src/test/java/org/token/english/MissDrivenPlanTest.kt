package org.token.english

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.engine.DefaultLearningPlanner
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.KnowledgeType
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.LessonAttempt
import org.token.english.domain.model.LessonState
import org.token.english.domain.model.Skill
import org.token.english.domain.model.TodayPlan
import org.token.english.domain.usecase.GetMissProfileUseCase
import org.token.english.domain.usecase.GetTodayPlanUseCase

/**
 * The roadmap's adaptive half: the miss mix used to be a report — Progress said which
 * kind of knowing was failing and the plan stayed graph-only, so the learner read the
 * diagnosis on one tab and worked on something else on the next.
 *
 * These cases prove the wiring end to end at the use-case level, over the same fakes the
 * screens use: the mix reaches `AdaptiveLearningPlanner` from `GetTodayPlanUseCase`, it
 * changes the order and the words of the plan, and a profile that cannot be read leaves
 * the graph's plan exactly as it was.
 */
class MissDrivenPlanTest {

    private val lessonId = "l.1"

    private fun lesson(id: String) = Lesson(
        id = id,
        level = LearningLevel.A1,
        title = id,
        titleFa = id,
        topic = id,
        estimatedMinutes = 5,
        order = 1,
    )

    private fun node(id: String, skill: Skill) = KnowledgeItem(
        id = id,
        type = KnowledgeType.GRAMMAR,
        title = id,
        titleFa = "«$id»",
        level = LearningLevel.A1,
        prerequisites = emptyList(),
        lessonIds = listOf(lessonId),
        skills = listOf(skill),
    )

    /** Mid mastery: weak enough to need practice, met, and below the secured gate. */
    private fun practising(itemId: String) = KnowledgeState(
        itemId = itemId,
        mastery = 0.5f,
        exposureCount = 2,
        consecutiveCorrect = 0,
        consecutiveIncorrect = 1,
        intervalDays = 0,
        easeFactor = 2.5f,
        repetitions = 0,
        lapses = 1,
        lastReviewedAt = 0L,
        nextReviewAt = 0L,
    )

    private fun vocabExercise() = Exercise.MultipleChoice(
        id = "ex.v",
        lessonId = lessonId,
        question = "Choose",
        questionFa = null,
        options = listOf("book", "desk"),
        correctIndex = 0,
        skill = Skill.VOCABULARY,
    )

    private suspend fun planOf(
        answers: List<LessonAttempt> = emptyList(),
        readError: Throwable? = null,
    ): TodayPlan {
        val knowledge = FakeKnowledgeRepository(
            listOf(node("a.grammar", Skill.GRAMMAR), node("b.vocab", Skill.VOCABULARY)),
        ).apply {
            statesFlow.value = listOf(practising("a.grammar"), practising("b.vocab"))
        }
        val lessonsRepo = FakeLessonRepository(
            lessons = listOf(lesson(lessonId)),
            states = listOf(LessonState(lessonId, currentIndex = 0, completed = false, completedAt = null)),
        ).apply {
            exercises[lessonId] = listOf(vocabExercise())
            attemptsLog += answers
            this.readError = readError
        }
        val reviews = FakeReviewRepository()
        val useCase = GetTodayPlanUseCase(
            lessons = lessonsRepo,
            reviews = reviews,
            progress = FakeProgressRepository(),
            settingsRepository = FakeSettingsRepository(),
            planner = DefaultLearningPlanner(),
            knowledge = knowledge,
            missProfile = GetMissProfileUseCase(lessonsRepo, reviews),
        )
        return useCase(now = 30 * 3_600_000L)
    }

    private fun vocabularyMisses(count: Int = 5): List<LessonAttempt> = (1..count).map { index ->
        LessonAttempt(
            lessonId = lessonId,
            stageId = "practice",
            exerciseId = "ex.v",
            answer = "desk",
            correct = false,
            at = index.toLong(),
        )
    }

    @Test
    fun `a vocabulary gap reaches the plan, so the day answers the diagnosis`() = runTest {
        val steered = planOf(vocabularyMisses())

        // The grammar node comes first when nothing steers (id tie-break), so this is a
        // change of order and not an artefact of how the nodes were declared.
        assertEquals("b.vocab", steered.actions.first().itemId)
        assertTrue(
            "the plan has to say why this moved up: ${steered.actions.first().reasonFa}",
            steered.actions.first().reasonFa.endsWith("ضعف اخیر: واژگان"),
        )
    }

    @Test
    fun `with no answers at all the plan is exactly the graph's`() = runTest {
        val plain = planOf()

        assertEquals("a.grammar", plain.actions.first().itemId)
        assertTrue(plain.actions.none { it.reasonFa.contains("ضعف اخیر") })
    }

    @Test
    fun `a profile that cannot be read leaves the graph's plan standing`() = runTest {
        // The mix is a hint, not an input the day depends on: a log or exercise read that
        // throws must not turn Home into an error screen over a nudge.
        val degraded = planOf(
            answers = vocabularyMisses(),
            readError = IllegalStateException("the answer log cannot be read"),
        )

        assertEquals("a.grammar", degraded.actions.first().itemId)
        assertTrue(degraded.actions.none { it.reasonFa.contains("ضعف اخیر") })
    }
}
