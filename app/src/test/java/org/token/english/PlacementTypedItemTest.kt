package org.token.english

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.token.english.data.content.ContentParser
import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.Skill
import org.token.english.domain.usecase.AssessPlacementUseCase
import org.token.english.domain.usecase.ScorePlacementUseCase
import org.token.english.feature.onboarding.PlacementEvent
import org.token.english.feature.onboarding.PlacementViewModel
import java.io.File

/**
 * The typed placement item (A-5/A-6 round, bundle 22).
 *
 * The test used to be 100% multiple choice — recognition only. One A1-band MCQ
 * became a `fill_blank` (`placement.t01`, same band slot, same GRAMMAR tag), so
 * these tests pin what must never regress:
 *
 *  1. the parser keeps the typed item (the screen used to filter questions to
 *     `MultipleChoice`, which would silently drop it),
 *  2. it sits in band 0 of the same six-bands-of-six structure the scorer walks,
 *  3. it grades through the SAME AnswerChecker the lessons use,
 *  4. PlacementViewModel records a typed answer like a choice one — correct,
 *     skill-tagged, and the test finishes.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PlacementTypedItemTest {

    private fun contentFile(name: String): File {
        val candidates = listOf(
            File("src/main/assets/content/$name"),
            File("app/src/main/assets/content/$name"),
        )
        return candidates.firstOrNull { it.exists() } ?: error("content file not found: $name")
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun parsed(): List<Exercise> =
        ContentParser.parsePlacementQuestions(contentFile("placement.json").readText())

    @Test
    fun `the bundle keeps all 36 questions with exactly one typed item`() {
        val items = parsed()
        assertEquals("six bands of six must hold across every format", 36, items.size)

        val typed = items.filterNot { it is Exercise.MultipleChoice }
        assertEquals("exactly one typed item is authored", 1, typed.size)
        val item = typed.single()
        assertTrue("the typed item is a fill_blank", item is Exercise.FillBlank)

        // Band 0 (A1): the item replaced an A1 MCQ in the same array slot.
        val band = items.indexOfFirst { it.id == item.id } / ScorePlacementUseCase.DEFAULT_BAND_SIZE
        assertEquals("the typed item must sit in the A1 band", 0, band)

        // The skill tag rides through untouched — the report still counts
        // 11 VOCABULARY / 19 GRAMMAR / 6 READING with this item in place.
        assertEquals(Skill.GRAMMAR, AnswerChecker.skillOf(item))
    }

    @Test
    fun `the typed item grades through the lesson AnswerChecker`() {
        val item = parsed().filterNot { it is Exercise.MultipleChoice }.single() as Exercise.FillBlank

        assertTrue("the authored exemplar must pass", AnswerChecker.isCorrect(item, "drinks"))
        assertFalse(
            "third-person -s is the target — 'drink' must not pass",
            AnswerChecker.isCorrect(item, "drink"),
        )
        // GRAMMAR items never get the almost-correct spelling nudge (A-2b): a
        // missing -s is a wrong answer with an explanation, not a typo.
        assertFalse(item.explanation.isNullOrBlank())
    }

    @Test
    fun `the placement screen records a typed answer like a choice one`() {
        val progress = FakeProgressRepository()
        val lessonRepo = FakeLessonRepository(
            lessons = listOf(
                org.token.english.domain.model.Lesson(
                    ContentParser.PLACEMENT_LESSON_ID,
                    org.token.english.domain.model.LearningLevel.A1,
                    "placement",
                    "تعیین سطح",
                    "placement",
                    10,
                    1,
                ),
            ),
        )
        lessonRepo.exercises[ContentParser.PLACEMENT_LESSON_ID] = listOf(
            Exercise.MultipleChoice(
                id = "placement.test.01",
                lessonId = ContentParser.PLACEMENT_LESSON_ID,
                question = "Pick one",
                questionFa = null,
                options = listOf("right", "wrong"),
                correctIndex = 1,
                skill = Skill.VOCABULARY,
                explanation = "because",
            ),
            Exercise.FillBlank(
                id = "placement.test.t01",
                lessonId = ContentParser.PLACEMENT_LESSON_ID,
                sentence = "Sara ___ tea every morning.",
                accepted = listOf("drinks"),
                skill = Skill.GRAMMAR,
            ),
        )

        val vm = PlacementViewModel(
            contentSeeded = MutableStateFlow(true),
            lessonRepository = lessonRepo,
            assessPlacement = AssessPlacementUseCase(progress),
            settingsRepository = FakeSettingsRepository(),
        )

        assertEquals("both formats load", 2, vm.state.value.questions.size)

        // 1. Choice question — selected by the DISPLAYED correct index (options
        // are shuffled for display, so the authored index is never assumed).
        val mcq = vm.state.value.current as Exercise.MultipleChoice
        vm.onEvent(PlacementEvent.Select(mcq.correctIndex))
        vm.onEvent(PlacementEvent.Next)
        assertTrue("the choice answer is recorded", vm.state.value.answers.last().correct)
        assertEquals(Skill.VOCABULARY, vm.state.value.answers.last().skill)

        // 2. Typed question — type the answer, no option to select.
        val typed = vm.state.value.current
        assertTrue("the typed item is next in order", typed is Exercise.FillBlank)
        vm.onEvent(PlacementEvent.TextAnswerChanged("drinks"))
        vm.onEvent(PlacementEvent.Next)

        assertTrue("the test finishes after the last question", vm.state.value.finished)
        assertEquals(2, vm.state.value.answers.size)
        val record = vm.state.value.answers.last()
        assertTrue("a correct typed answer counts as correct", record.correct)
        assertEquals(Skill.GRAMMAR, record.skill)
        assertEquals("both questions sat in the A1 band", 0, record.level.ordinal)

        // The calibration replay ran through the real progress pipeline (P1-1).
        assertTrue(
            "placement seeds skill mastery",
            progress.masteryFlow.value.isNotEmpty(),
        )
    }
}
