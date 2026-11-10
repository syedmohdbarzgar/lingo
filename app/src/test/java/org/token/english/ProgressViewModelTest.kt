package org.token.english

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.token.english.domain.engine.SkillStatus
import org.token.english.domain.model.AttemptStats
import org.token.english.domain.model.ExerciseFormat
import org.token.english.domain.model.GradedCard
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.KnowledgeType
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.LessonState
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.LessonAttempt
import org.token.english.domain.model.ReviewContentType
import org.token.english.domain.model.ReviewResult
import org.token.english.domain.model.Skill
import org.token.english.domain.engine.MissCategory
import org.token.english.domain.usecase.GetMissProfileUseCase
import org.token.english.domain.usecase.GetSkillProfilesUseCase
import org.token.english.feature.progress.ProgressViewModel

/**
 * The Progress screen's three tracks (checklist v8 P2), driven through the real
 * ViewModel on in-memory fakes: no Android, no Room, no device.
 *
 * The screen used to list six `Skill.entries` bars — including `SPEAKING`, which the
 * app cannot train at all, so it could only ever read "شروع نشده". What is asserted
 * here is the replacement: three tracks, each carrying the evidence behind its
 * number, and review practice counted into the track it exercises.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProgressViewModelTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun node(id: String, vararg skills: Skill) = KnowledgeItem(
        id = id,
        type = KnowledgeType.GRAMMAR,
        title = id,
        titleFa = "«$id»",
        level = LearningLevel.A1,
        prerequisites = emptyList(),
        lessonIds = listOf("l.$id"),
        skills = skills.toList(),
    )

    private fun state(itemId: String, mastery: Float, attempts: Int, misses: Int) = KnowledgeState(
        itemId = itemId,
        mastery = mastery,
        exposureCount = attempts,
        consecutiveCorrect = 0,
        consecutiveIncorrect = misses,
        intervalDays = 0,
        easeFactor = 2.5f,
        repetitions = 0,
        lapses = misses,
        lastReviewedAt = null,
        nextReviewAt = 0L,
    )

    private fun viewModel(
        items: List<KnowledgeItem>,
        states: List<KnowledgeState>,
        mastery: Map<Skill, Float> = emptyMap(),
        reviewStats: Map<ReviewContentType, AttemptStats> = emptyMap(),
        lessons: List<LessonState> = emptyList(),
        answers: List<LessonAttempt> = emptyList(),
        exercises: Map<String, List<Exercise>> = emptyMap(),
        cards: List<GradedCard> = emptyList(),
    ): ProgressViewModel {
        val knowledge = FakeKnowledgeRepository(items).apply { statesFlow.value = states }
        val progress = FakeProgressRepository().apply { masteryFlow.value = mastery }
        val reviews = FakeReviewRepository().apply {
            attemptStats = reviewStats
            gradedCards += cards
        }
        val lessonsRepo = FakeLessonRepository().apply {
            statesFlow.value = lessons
            attemptsLog += answers
            exercises.forEach { (lessonId, list) -> this.exercises[lessonId] = list }
        }
        return ProgressViewModel(
            settingsRepository = FakeSettingsRepository(),
            lessonRepository = lessonsRepo,
            progressRepository = progress,
            knowledgeRepository = knowledge,
            getSkillProfiles = GetSkillProfilesUseCase(knowledge, progress, reviews),
            getMissProfile = GetMissProfileUseCase(lessonsRepo, reviews),
            studyStats = progress.statsFlow,
        )
    }

    @Test
    fun `the profile is exactly the three tracks, never the six-skill list`() = runTest {
        val vm = viewModel(
            items = listOf(node("g1", Skill.GRAMMAR)),
            states = emptyList(),
        )
        assertEquals(
            listOf(Skill.GRAMMAR, Skill.VOCABULARY, Skill.READING),
            vm.state.value.profiles.map { it.skill },
        )
        assertTrue(
            "speaking is not a track the app can train",
            vm.state.value.profiles.none { it.skill == Skill.SPEAKING },
        )
        // Nothing done yet, so every track reports the honest empty state rather than
        // a 0% that would read as "answered everything wrong".
        assertTrue(vm.state.value.profiles.all { it.status == SkillStatus.NOT_STARTED })
        assertTrue(vm.state.value.profiles.all { it.accuracy == null })
    }

    @Test
    fun `each track reaches the state with its own evidence`() = runTest {
        val vm = viewModel(
            items = listOf(node("g1", Skill.GRAMMAR), node("r1", Skill.READING)),
            states = listOf(
                state("g1", 0.9f, attempts = 8, misses = 1),
                state("r1", 0.4f, attempts = 2, misses = 1),
            ),
            mastery = mapOf(Skill.GRAMMAR to 0.9f, Skill.READING to 0.4f),
        )

        val grammar = vm.state.value.profiles.first { it.skill == Skill.GRAMMAR }
        val reading = vm.state.value.profiles.first { it.skill == Skill.READING }
        val vocabulary = vm.state.value.profiles.first { it.skill == Skill.VOCABULARY }

        assertEquals(8, grammar.evidence)
        assertEquals(SkillStatus.CONSOLIDATED, grammar.status)
        assertEquals(2, reading.evidence)
        assertEquals(SkillStatus.BUILDING, reading.status)
        assertEquals("no vocabulary work yet", 0, vocabulary.evidence)
        assertNull(vocabulary.accuracy)
    }

    @Test
    fun `a completed lesson is only mastered once its own nodes are secured`() = runTest {
        val items = listOf(node("g1", Skill.GRAMMAR))
        val finished = LessonState(
            lessonId = "l.g1",
            currentIndex = 0,
            completed = true,
            completedAt = 1_790_000_000_000L,
        )
        val taught = state("g1", 0.4f, attempts = 2, misses = 0)
        // Secured: strong, a broken-free streak, two shapes, and a delayed retest — the
        // same bar the planner uses, so the screen cannot be more generous than the plan.
        val secured = taught.copy(
            mastery = 0.92f,
            consecutiveCorrect = 3,
            repetitions = 3,
            intervalDays = 3,
            lastReviewedAt = 1_790_086_000_000L,
            firstCorrectAt = 1_790_000_000_000L,
            distinctTypesCorrect = ExerciseFormat.FILL_BLANK.bit or ExerciseFormat.TRANSLATION.bit,
        )

        assertEquals(
            "finished steps alone are not mastery",
            0,
            viewModel(items, listOf(taught), lessons = listOf(finished)).state.value.masteredLessons,
        )
        assertEquals(
            1,
            viewModel(items, listOf(secured), lessons = listOf(finished)).state.value.masteredLessons,
        )
        assertEquals(
            "evidence without the finished steps is not a mastered lesson",
            0,
            viewModel(items, listOf(secured), lessons = listOf(finished.copy(completed = false)))
                .state.value.masteredLessons,
        )
    }

    @Test
    fun `recent misses reach the state classified by what they asked about`() = runTest {
        // The error taxonomy: the screen has to say *what* goes wrong, not only how much
        // is known. Five answers is the evidence bar; below it the card stays hidden.
        val lessonId = "a1.miss.lesson-01"
        val vocab = Exercise.MultipleChoice(
            id = "v.1",
            lessonId = lessonId,
            question = "Choose",
            questionFa = null,
            options = listOf("book", "desk"),
            correctIndex = 0,
            skill = Skill.VOCABULARY,
        )
        val grammar = Exercise.MultipleChoice(
            id = "g.1",
            lessonId = lessonId,
            question = "Choose",
            questionFa = null,
            options = listOf("is", "are"),
            correctIndex = 0,
            skill = Skill.GRAMMAR,
        )
        fun answer(exercise: Exercise, correct: Boolean, at: Long) = LessonAttempt(
            lessonId = lessonId,
            stageId = "practice",
            exerciseId = exercise.id,
            answer = if (correct) "ok" else "wrong",
            correct = correct,
            at = at,
        )

        val answers = listOf(
            answer(vocab, true, 1),
            answer(vocab, false, 2),
            answer(grammar, false, 3),
            answer(grammar, false, 4),
        )
        val vm = viewModel(
            items = emptyList(),
            states = emptyList(),
            answers = answers,
            exercises = mapOf(lessonId to listOf(vocab, grammar)),
        )
        assertNull("four answers is not a pattern yet", vm.state.value.missProfile)

        val withFive = viewModel(
            items = emptyList(),
            states = emptyList(),
            answers = answers + answer(vocab, true, 5),
            exercises = mapOf(lessonId to listOf(vocab, grammar)),
        )
        val profile = withFive.state.value.missProfile!!

        assertEquals(5, profile.attempts)
        assertEquals("one vocabulary and two grammar answers went wrong", 3, profile.misses)
        assertEquals(MissCategory.GRAMMAR, profile.weakest?.category)
    }

    @Test
    fun `review cards feed the miss mix, so a review-only week still moves it`() = runTest {
        // A mature learner's practice is mostly the SRS queue, which never touches a
        // lesson; a profile that only read lessons would freeze after the first week.
        val vm = viewModel(
            items = emptyList(),
            states = emptyList(),
            cards = (1..5).map { index ->
                GradedCard(
                    result = if (index <= 3) ReviewResult.AGAIN else ReviewResult.GOOD,
                    type = ReviewContentType.VOCABULARY,
                    at = index.toLong(),
                )
            },
        )
        val profile = vm.state.value.missProfile!!

        assertEquals(5, profile.attempts)
        assertEquals("HARD and AGAIN are not hits", 3, profile.misses)
        assertEquals(MissCategory.VOCABULARY, profile.weakest?.category)
    }

    @Test
    fun `lessons and cards share one window, so neither source crowds the other out`() = runTest {
        val lessonId = "a1.test.lesson-01"
        val grammar = Exercise.MultipleChoice(
            id = "g.1",
            lessonId = lessonId,
            question = "Choose",
            questionFa = null,
            options = listOf("is", "are"),
            correctIndex = 0,
            skill = Skill.GRAMMAR,
        )
        val lessonAnswers = (1..4).map { at ->
            LessonAttempt(
                lessonId = lessonId,
                stageId = "practice",
                exerciseId = grammar.id,
                answer = "wrong",
                correct = false,
                at = at.toLong(),
            )
        }
        val vm = viewModel(
            items = emptyList(),
            states = emptyList(),
            answers = lessonAnswers,
            exercises = mapOf(lessonId to listOf(grammar)),
            cards = listOf(
                GradedCard(ReviewResult.GOOD, ReviewContentType.VOCABULARY, 10),
                GradedCard(ReviewResult.GOOD, ReviewContentType.VOCABULARY, 11),
            ),
        )
        val profile = vm.state.value.missProfile!!

        assertEquals("four lesson answers and two cards are one pool", 6, profile.attempts)
        assertEquals(
            listOf(MissCategory.VOCABULARY, MissCategory.GRAMMAR),
            profile.categories.map { it.category },
        )
        assertEquals(MissCategory.GRAMMAR, profile.weakest?.category)
    }

    @Test
    fun `a card whose item left the queue is dropped instead of guessed into a category`() = runTest {
        val vm = viewModel(
            items = emptyList(),
            states = emptyList(),
            cards = (1..5).map { index ->
                GradedCard(result = ReviewResult.AGAIN, type = null, at = index.toLong())
            },
        )

        assertNull("five unclassifiable grades are not a pattern", vm.state.value.missProfile)
    }

    @Test
    fun `review practice is counted into the vocabulary track`() = runTest {
        // The SRS log is where most vocabulary work happens and it never touches the
        // curriculum graph, so a track that ignored it would report a high number with
        // no evidence behind it.
        val vm = viewModel(
            items = listOf(node("v1", Skill.VOCABULARY)),
            states = emptyList(),
            mastery = mapOf(Skill.VOCABULARY to 0.8f),
            reviewStats = mapOf(ReviewContentType.VOCABULARY to AttemptStats(attempts = 10, misses = 2)),
        )
        val vocabulary = vm.state.value.profiles.first { it.skill == Skill.VOCABULARY }

        assertEquals(10, vocabulary.evidence)
        assertEquals(80, vocabulary.accuracyPercent)
        assertEquals(SkillStatus.CONSOLIDATED, vocabulary.status)
        assertEquals(
            "grammar gets nothing from a vocabulary review",
            0,
            vm.state.value.profiles.first { it.skill == Skill.GRAMMAR }.evidence,
        )
    }
}
