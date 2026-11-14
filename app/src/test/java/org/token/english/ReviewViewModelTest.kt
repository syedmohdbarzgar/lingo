package org.token.english

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.token.english.domain.engine.Sm2ReviewScheduler
import org.token.english.domain.engine.StudyTimePolicy
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.ExerciseFormat
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.KnowledgeType
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.ReviewContentType
import org.token.english.domain.model.ReviewItem
import org.token.english.domain.model.ReviewResult
import org.token.english.domain.model.ReviewState
import org.token.english.domain.model.Skill
import org.token.english.domain.model.VocabularyItem
import org.token.english.domain.usecase.GetDueKnowledgeReviewsUseCase
import org.token.english.domain.usecase.GetDueNodeCardsUseCase
import org.token.english.domain.usecase.GetFocusPlanUseCase
import org.token.english.domain.usecase.SubmitExerciseUseCase
import org.token.english.domain.usecase.SubmitReviewUseCase
import org.token.english.feature.review.ReviewEvent
import org.token.english.feature.review.ReviewPhase
import org.token.english.feature.review.ReviewViewModel

/**
 * ReviewViewModel tests (checklist B-6): the SRS write on a grade and the
 * in-session re-queue of an AGAIN card.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ReviewViewModelTest {

    private val contentId = "a1.test.word.hello"

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val card = ReviewItem(
        contentId = contentId,
        contentType = ReviewContentType.VOCABULARY,
        state = ReviewState.NEW,
        dueAt = 0L,
        intervalDays = 0,
        easeFactor = Sm2ReviewScheduler.DEFAULT_EASE,
        repetitions = 0,
        lapses = 0,
        lastReviewedAt = null,
    )

    private val word = VocabularyItem(
        id = contentId,
        word = "hello",
        translation = "سلام",
        definition = null,
        pronunciation = null,
        level = LearningLevel.A1,
        partOfSpeech = "interjection",
        examples = emptyList(),
        collocations = emptyList(),
        lessonId = "a1.test.lesson-01",
    )

    private class Harness {
        val reviews = FakeReviewRepository()
        val vocabulary = FakeVocabularyRepository()
        val progress = FakeProgressRepository()
        val knowledge = FakeKnowledgeRepository()
        val lessons = FakeLessonRepository()
        val audio = FakeAudioPlayer()
        val speaker = RecordingSpeaker()

        fun build(foreground: () -> Boolean = { true }): ReviewViewModel {
            val due = GetDueKnowledgeReviewsUseCase(knowledge, GetFocusPlanUseCase(knowledge, lessons))
            return ReviewViewModel(
                reviewRepository = reviews,
                vocabularyRepository = vocabulary,
                progressRepository = progress,
                submitReview = SubmitReviewUseCase(reviews, Sm2ReviewScheduler(), progress, knowledge),
                getDueKnowledgeReviews = due,
                getDueNodeCards = GetDueNodeCardsUseCase(due, lessons, knowledge),
                submitExercise = SubmitExerciseUseCase(progress, knowledge),
                audioPlayer = audio,
                speaker = speaker,
                isAppInForeground = foreground,
            )
        }
    }

    private fun Harness.withDueCard(): Harness {
        reviews.dueFlow.value = listOf(card)
        vocabulary.allFlow.value = listOf(word)
        return this
    }

    /** One curriculum node whose own schedule is due, with a drillable exercise. */
    private fun Harness.withDueNode(
        itemId: String = "grammar.test",
        exercises: List<Exercise>? = null,
    ): Harness {
        val lessonId = word.lessonId!!
        knowledge.itemsFlow.value = listOf(
            KnowledgeItem(
                id = itemId,
                type = KnowledgeType.GRAMMAR,
                title = "Verb be",
                titleFa = "فعل be",
                level = LearningLevel.A1,
                prerequisites = emptyList(),
                lessonIds = listOf(lessonId),
                skills = listOf(Skill.GRAMMAR),
            ),
        )
        knowledge.statesFlow.value = listOf(
            KnowledgeState(
                itemId = itemId,
                mastery = 0.7f,
                exposureCount = 2,
                consecutiveCorrect = 1,
                consecutiveIncorrect = 0,
                intervalDays = 0,
                easeFactor = 2.5f,
                repetitions = 1,
                lapses = 0,
                lastReviewedAt = 0L,
                nextReviewAt = 0L,
            ),
        )
        lessons.exercises[lessonId] = exercises ?: listOf(
            Exercise.MultipleChoice(
                id = "a1.test.ex.01",
                lessonId = lessonId,
                question = "Choose",
                questionFa = null,
                options = listOf("is", "are"),
                correctIndex = 0,
                skill = Skill.GRAMMAR,
            ),
        )
        return this
    }

    @Test
    fun `grading a card writes the schedule and finishes the session`() {
        val h = Harness().withDueCard()
        val vm = h.build()
        assertEquals(1, vm.state.value.queue.size)

        vm.onEvent(ReviewEvent.Start)
        vm.onEvent(ReviewEvent.Reveal)
        vm.onEvent(ReviewEvent.Grade(ReviewResult.GOOD))

        assertEquals(ReviewPhase.DONE, vm.state.value.phase)
        assertEquals(1, h.reviews.attempts.size)
        assertEquals(ReviewResult.GOOD, h.reviews.attempts.first().result)
        assertTrue("the next schedule is persisted", h.reviews.scheduled.isNotEmpty())
        assertTrue("a GOOD grade moves the vocabulary meter", h.progress.attempts.contains(Skill.VOCABULARY to true))
        assertEquals(
            "the grade reaches the graph through the word's own lesson",
            listOf(Triple(word.lessonId, Skill.VOCABULARY, true)),
            h.knowledge.recalled,
        )
    }

    private fun vocabularyNode(itemId: String, lessonId: String) = KnowledgeItem(
        id = itemId,
        type = KnowledgeType.VOCABULARY,
        title = "Airport",
        titleFa = "فرودگاه",
        level = LearningLevel.A2,
        prerequisites = emptyList(),
        lessonIds = listOf(lessonId),
        skills = listOf(Skill.VOCABULARY, Skill.LISTENING),
    )

    @Test
    fun `a card from a lesson that gained its vocabulary node in v65 reaches the tab and is credited to it`() {
        // a2.airport is one of the 35 lessons that shipped words while owning only grammar
        // nodes; v65 gave each of them a VOCABULARY node titled by its own lesson. This is
        // the tab's half of that promise: the card is in the queue the learner sees, and
        // its grade is attributed to the word's own lesson (which is what resolves to that
        // node — `KnowledgeEngineTest` pins the same-type rule, `EvidenceTransactionTest`
        // the rolled-back write on real Room).
        val h = Harness()
        val airportWord = word.copy(lessonId = "a2.airport")
        h.reviews.dueFlow.value = listOf(card)
        h.vocabulary.allFlow.value = listOf(airportWord)
        h.knowledge.itemsFlow.value = listOf(vocabularyNode("vocab.airport", "a2.airport"))

        val vm = h.build()

        assertEquals(
            "the card of a v65 lesson is on the review screen, not stranded in the graph",
            listOf(airportWord.id),
            vm.state.value.queue.map { it.contentId },
        )

        vm.onEvent(ReviewEvent.Start)
        vm.onEvent(ReviewEvent.Reveal)
        vm.onEvent(ReviewEvent.Grade(ReviewResult.GOOD))

        assertEquals(
            "the grade is credited to that lesson, where its vocabulary node lives",
            listOf(Triple("a2.airport", Skill.VOCABULARY, true)),
            h.knowledge.recalled,
        )
    }

    @Test
    fun `a due curriculum node is offered beside the cards, not instead of them`() {
        // review→graph pass: the node's own schedule is read too, so a due node whose
        // exercises exist is served by the focused drill — while the word cards on
        // screen stay exactly what they were.
        val h = Harness().withDueCard().withDueNode()

        val vm = h.build()

        assertEquals(listOf("grammar.test"), vm.state.value.dueReviews.map { it.itemId })
        assertEquals("the row opens the drill in the node's own lesson", word.lessonId, vm.state.value.dueReviews.single().lessonId)
        assertEquals("the cards are still there", 1, vm.state.value.queue.size)
    }

    @Test
    fun `a resumed summary re-reads both queues`() {
        // Both reads only ran in `init`; work finished elsewhere (the focused drill, a
        // card graded on another tab) left the screen showing what was already done.
        val h = Harness()
        val vm = h.build()
        assertTrue("nothing due at first", vm.state.value.queue.isEmpty())
        assertTrue(vm.state.value.dueReviews.isEmpty())

        h.withDueCard().withDueNode()
        vm.onScreenResumed()

        assertEquals("the cards are read again", 1, vm.state.value.queue.size)
        assertEquals(listOf("grammar.test"), vm.state.value.dueReviews.map { it.itemId })
    }

    @Test
    fun `resuming mid-session never resets the card the learner is on`() {
        val h = Harness().withDueCard()
        val vm = h.build()
        vm.onEvent(ReviewEvent.Start)
        vm.onEvent(ReviewEvent.Reveal)

        // The disk says otherwise — a completed drill, an empty queue — but the session
        // on screen is the learner's, so a resume must not touch it.
        h.reviews.dueFlow.value = emptyList()
        h.withDueNode()
        vm.onScreenResumed()

        assertEquals(ReviewPhase.SESSION, vm.state.value.phase)
        assertTrue("the revealed card stays revealed", vm.state.value.revealed)
        assertEquals(1, vm.state.value.queue.size)
        assertTrue("the graph half is not grafted onto a running session", vm.state.value.dueReviews.isEmpty())
    }

    @Test
    fun `the finished session returns to the due list instead of stranding it`() {
        val h = Harness().withDueCard().withDueNode()
        val vm = h.build()

        vm.onEvent(ReviewEvent.Start)
        vm.onEvent(ReviewEvent.Reveal)
        vm.onEvent(ReviewEvent.Grade(ReviewResult.GOOD))
        // The word cards are done but the session is not: the graph's own question is
        // next (graph-card pass), and answering it is what ends the session.
        assertEquals(ReviewPhase.SESSION, vm.state.value.phase)
        assertTrue(vm.state.value.askingNode)
        val exercise = vm.state.value.currentNodeCard!!.exercise as Exercise.MultipleChoice
        vm.onEvent(ReviewEvent.SelectNodeOption(exercise.correctIndex))
        vm.onEvent(ReviewEvent.SubmitNode)
        vm.onEvent(ReviewEvent.NextNode)
        assertEquals(ReviewPhase.DONE, vm.state.value.phase)
        assertEquals("the node is still owed after the cards", 1, vm.state.value.dueReviews.size)

        vm.onEvent(ReviewEvent.ShowDue)

        assertEquals(ReviewPhase.SUMMARY, vm.state.value.phase)
        assertEquals(listOf("grammar.test"), vm.state.value.dueReviews.map { it.itemId })
    }

    @Test
    fun `an AGAIN card comes back later in the same session`() {
        val h = Harness().withDueCard()
        val vm = h.build()

        vm.onEvent(ReviewEvent.Start)
        vm.onEvent(ReviewEvent.Reveal)
        vm.onEvent(ReviewEvent.Grade(ReviewResult.AGAIN))

        assertEquals("the card is re-queued", 2, vm.state.value.queue.size)
        assertEquals(1, vm.state.value.againCount)
        assertEquals(ReviewPhase.SESSION, vm.state.value.phase)
        assertTrue("an AGAIN grade does not credit mastery", h.progress.attempts.contains(Skill.VOCABULARY to false))
    }

    @Test
    fun `study time follows interactions - an idle screen stops crediting`() {
        val h = Harness().withDueCard()
        val vm = h.build()
        val t0 = System.currentTimeMillis()

        vm.onEvent(ReviewEvent.Start)
        vm.onStudyTick(nowMs = t0 + 5_000)
        assertEquals("a recently active session credits a tick", StudyTimePolicy.TICK_SECONDS, h.progress.studySeconds)

        vm.onStudyTick(nowMs = t0 + StudyTimePolicy.IDLE_CAP_MILLIS + 60_000)
        assertEquals("an abandoned screen credits nothing more", StudyTimePolicy.TICK_SECONDS, h.progress.studySeconds)

        vm.onEvent(ReviewEvent.Reveal)
        vm.onStudyTick(nowMs = System.currentTimeMillis())
        assertEquals("acting again reopens the window", 2 * StudyTimePolicy.TICK_SECONDS, h.progress.studySeconds)
    }

    @Test
    fun `no study time is credited while the app is backgrounded`() {
        val h = Harness().withDueCard()
        val vm = h.build(foreground = { false })

        vm.onStudyTick(nowMs = System.currentTimeMillis())
        assertEquals(0L, h.progress.studySeconds)
    }

    @Test
    fun `a due node's own exercise is asked after the word cards, and its answer is evidence`() {
        // graph-card pass: the session no longer ends with the last word. A node whose
        // schedule is due is asked its own exercise — the question the lesson would ask
        // — so the format mask can finally be confirmed by review practice.
        val h = Harness().withDueCard().withDueNode()
        val vm = h.build()
        assertEquals(1, vm.state.value.nodeCards.size)

        vm.onEvent(ReviewEvent.Start)
        vm.onEvent(ReviewEvent.Reveal)
        vm.onEvent(ReviewEvent.Grade(ReviewResult.GOOD))

        assertEquals("the session goes on to the graph's question", ReviewPhase.SESSION, vm.state.value.phase)
        assertTrue(vm.state.value.askingNode)

        val exercise = vm.state.value.currentNodeCard!!.exercise as Exercise.MultipleChoice
        vm.onEvent(ReviewEvent.SelectNodeOption(exercise.correctIndex))
        vm.onEvent(ReviewEvent.SubmitNode)

        assertTrue("an answer is graded, not self-assessed", vm.state.value.nodeOutcome!!.correct)
        assertEquals(
            "the node is credited through the lesson's own answer path",
            listOf(word.lessonId to true),
            h.knowledge.recorded,
        )
        assertEquals(
            "the format the card asked in is confirmed — what a word card may never do",
            listOf(Skill.GRAMMAR to ExerciseFormat.MULTIPLE_CHOICE),
            h.knowledge.recordedSkills,
        )
        assertTrue(
            "the day's claim is taken, so repeating the answer is not new evidence",
            h.progress.claimed.containsKey(exercise.id),
        )

        vm.onEvent(ReviewEvent.NextNode)

        assertEquals(ReviewPhase.DONE, vm.state.value.phase)
        assertEquals("both halves count as reviewed", 2, vm.state.value.gradedCount)
    }

    @Test
    fun `a wrong node answer names the authored answer and still takes the next question`() {
        // No word cards at all: with only a due node the graph is the whole session, and
        // the node card is the first question rather than the second.
        val h = Harness().withDueNode()
        val vm = h.build()

        vm.onEvent(ReviewEvent.Start)
        val exercise = vm.state.value.currentNodeCard!!.exercise as Exercise.MultipleChoice
        vm.onEvent(ReviewEvent.SelectNodeOption((exercise.correctIndex + 1) % exercise.options.size))
        vm.onEvent(ReviewEvent.SubmitNode)

        val outcome = vm.state.value.nodeOutcome!!
        assertFalse(outcome.correct)
        assertEquals(
            "the banner has a real answer to name, not a generic miss",
            exercise.options[exercise.correctIndex],
            outcome.correctAnswer,
        )
        assertEquals("a miss is still written to the graph", listOf(word.lessonId to false), h.knowledge.recorded)

        vm.onEvent(ReviewEvent.NextNode)
        assertEquals(ReviewPhase.DONE, vm.state.value.phase)
    }

    @Test
    fun `a node missing two shapes is asked twice, in the authored order`() {
        // A node whose schedule keeps coming round was asked its choice item every time
        // (one card per node), so the second format the gate needs was never proven.
        // The block asks one question per missing shape, at most two — and both are
        // questions of the same due node, not a second node.
        val lessonId = word.lessonId!!
        val h = Harness().withDueNode(
            exercises = listOf(
                Exercise.FillBlank(
                    id = "a1.test.ex.01",
                    lessonId = lessonId,
                    sentence = "I ______ here.",
                    accepted = listOf("live"),
                    skill = Skill.GRAMMAR,
                ),
                Exercise.Translation(
                    id = "a1.test.ex.02",
                    lessonId = lessonId,
                    prompt = "من اینجا زندگی می‌کنم.",
                    accepted = listOf("I live here."),
                    skill = Skill.GRAMMAR,
                ),
            ),
        )
        val vm = h.build()
        assertEquals(
            "one card per missing shape, in the order the lesson authored them",
            listOf("a1.test.ex.01", "a1.test.ex.02"),
            vm.state.value.nodeCards.map { it.exercise.id },
        )

        vm.onEvent(ReviewEvent.Start)
        assertEquals("a1.test.ex.01", vm.state.value.currentNodeCard!!.exercise.id)
        vm.onEvent(ReviewEvent.AnswerChanged("live"))
        vm.onEvent(ReviewEvent.SubmitNode)
        assertTrue(vm.state.value.nodeOutcome!!.correct)

        vm.onEvent(ReviewEvent.NextNode)

        assertEquals("the node's second shape is still to be proven", ReviewPhase.SESSION, vm.state.value.phase)
        assertTrue(vm.state.value.askingNode)
        assertEquals("a1.test.ex.02", vm.state.value.currentNodeCard!!.exercise.id)

        vm.onEvent(ReviewEvent.AnswerChanged("I live here."))
        vm.onEvent(ReviewEvent.SubmitNode)
        assertTrue(vm.state.value.nodeOutcome!!.correct)
        vm.onEvent(ReviewEvent.NextNode)
        assertEquals("only after the last question does the session end", ReviewPhase.DONE, vm.state.value.phase)
    }
}
