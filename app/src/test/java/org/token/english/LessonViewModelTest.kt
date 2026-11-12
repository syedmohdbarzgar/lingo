package org.token.english

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.token.english.core.common.ContentStatus
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.KnowledgeType
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.MasteryDimension
import org.token.english.domain.model.MasteryProfile
import org.token.english.domain.model.Lesson
import org.token.english.domain.engine.AdaptiveExerciseSelector
import org.token.english.domain.engine.DefaultMasteryProfileEngine
import org.token.english.core.audio.SpeechError
import org.token.english.core.audio.SpeechState
import org.token.english.domain.engine.SpeakingVerdict
import org.token.english.domain.engine.StudyTimePolicy
import org.token.english.domain.model.Skill
import org.token.english.domain.usecase.CompleteLessonUseCase
import org.token.english.domain.usecase.GetFocusPlanUseCase
import org.token.english.domain.usecase.SubmitExerciseUseCase
import org.token.english.domain.model.LearningStage
import org.token.english.feature.lesson.LessonEvent
import org.token.english.feature.lesson.LessonViewModel

/**
 * LessonViewModel tests (checklist B-6): the requeue-once-on-a-miss rule and the
 * TTS-unavailable fallback, both driven through in-memory fakes — plus the two
 * failure paths of P1-7: a read that throws must become a state with a retry
 * (never a spinner nobody can leave) and a failed dimension fold must not take
 * the learner's verdict down with it.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LessonViewModelTest {

    private val lessonId = "a1.test.lesson-01"

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun choice(id: String = "$lessonId.ex.01", skill: Skill = Skill.VOCABULARY) =
        Exercise.MultipleChoice(
            id = id,
            lessonId = lessonId,
            question = "Choose",
            questionFa = null,
            options = listOf("right", "wrong"),
            correctIndex = 0,
            skill = skill,
            explanation = "because",
        )

    private fun knowledgeState(itemId: String, mastery: Float) = KnowledgeState(
        itemId = itemId,
        mastery = mastery,
        exposureCount = 3,
        consecutiveCorrect = 0,
        consecutiveIncorrect = 2,
        intervalDays = 0,
        easeFactor = 2.5f,
        repetitions = 0,
        lapses = 0,
        lastReviewedAt = null,
        nextReviewAt = 0L,
    )

    /** A "say this sentence back" item: no partner question, so the model is the target. */
    private fun speaking(id: String, model: String) = Exercise.Speaking(
        id = id,
        lessonId = lessonId,
        prompt = "جمله را بگو",
        referenceAnswers = listOf(model),
        skill = Skill.SPEAKING,
    )

    private fun blank(id: String) = Exercise.FillBlank(
        id = id,
        lessonId = lessonId,
        sentence = "I ___ here.",
        accepted = listOf("am"),
        skill = Skill.GRAMMAR,
    )

    private inner class Harness(
        available: Boolean? = true,
        /** The offline recogniser, faked: a take is published as the service would. */
        val speech: FakeSpeech = FakeSpeech(),
    ) {
        val lessonRepo = FakeLessonRepository(lessons = listOf(Lesson(lessonId, LearningLevel.A1, "t", "ت", "topic", 5, 1)))
        val progress = FakeProgressRepository()
        val audio = FakeAudioPlayer(available)
        val speaker = RecordingSpeaker()
        val contentStatus = MutableStateFlow(ContentStatus.READY)

        /** How many times the retry re-ran the bundle write (the FAILED path's repair). */
        var seeds = 0
        val knowledge = FakeKnowledgeRepository()

        val submit = SubmitExerciseUseCase(progress, knowledge)
        val complete = CompleteLessonUseCase(lessonRepo, progress)
        val focusPlan = GetFocusPlanUseCase(knowledge, lessonRepo)

        /**
         * Builds the session. These tests are about grading, so by default the
         * lesson is skipped past its teaching steps into the exercises; the guided
         * flow itself is walked by `GuidedLessonTest` from the context step.
         */
        fun build(focusItemId: String? = null, atContext: Boolean = false): LessonViewModel {
            val vm = LessonViewModel(
                lessonId = lessonId,
                contentStatus = contentStatus,
                seedContent = {
                    seeds++
                    contentStatus.value = ContentStatus.READY
                    true
                },
                lessonRepository = lessonRepo,
                progressRepository = progress,
                submitExercise = submit,
                completeLesson = complete,
                audioPlayer = audio,
                speaker = speaker,
                speechState = speech.state,
                startRecognition = { speech.start() },
                stopRecognition = { speech.stop() },
                isAppInForeground = { true },
                getFocusPlan = focusPlan,
                profileEngine = DefaultMasteryProfileEngine(),
                selector = AdaptiveExerciseSelector(),
                focusItemId = focusItemId,
            )
            if (!atContext) vm.onEvent(LessonEvent.StartExercises)
            return vm
        }
    }

    private fun Harness.withOneExercise(): Harness {
        lessonRepo.exercises[lessonId] = listOf(choice())
        return this
    }

    @Test
    fun `a lesson whose read throws offers a retry instead of a spinner`() {
        // Before the guard, the reads sat bare in `init`'s launch: a throw ended the
        // coroutine and `isLoading` stayed true forever, on the one screen the
        // learner cannot study without.
        val h = Harness()
        h.lessonRepo.readError = IllegalStateException("database file is corrupt")
        val vm = h.build()

        assertFalse("no spinner nobody can leave", vm.state.value.isLoading)
        assertNotNull("the screen can say the lesson failed", vm.state.value.error)
        assertTrue(
            "the message says the data is local, not that the network failed",
            vm.state.value.error.orEmpty().contains("همین گوشی"),
        )

        h.lessonRepo.readError = null
        h.withOneExercise()
        vm.retry()

        assertNull("the retry clears the error", vm.state.value.error)
        assertNotNull("the lesson is loaded", vm.state.value.lesson)
        vm.onEvent(LessonEvent.StartExercises)
        assertNotNull("and the session is really usable", vm.state.value.currentExercise)
    }

    @Test
    fun `a failed dimension fold still shows the verdict`() {
        // debt.md: the fold is session input, so losing it costs one stale seed. An
        // unguarded throw here killed the coroutine before the verdict was rendered,
        // leaving the learner looking at their own answer with a button that did
        // nothing.
        val h = Harness().withOneExercise()
        h.progress.dimensionError = IllegalStateException("dimension write failed")
        val vm = h.build()

        vm.onEvent(LessonEvent.SelectOption(0))
        vm.onEvent(LessonEvent.Submit)

        val outcome = vm.state.value.outcome
        assertNotNull("the learner still gets their verdict", outcome)
        // The verdict comes from the exercise, not from whether the fold landed.
        assertEquals("and it counts as answered", 1, vm.state.value.askedCount)
        assertTrue("nothing was written to the dimension table", h.progress.dimensionWrites.isEmpty())
    }

    @Test
    fun `a failed bundle write becomes a retryable error, and the retry reseeds`() {
        // The page used to wait for "seeded": a write that failed left it false
        // forever, so the lesson sat on the spinner under `activeStage == null`
        // with nothing to press.
        val h = Harness()
        h.contentStatus.value = ContentStatus.FAILED
        val vm = h.build(atContext = true)

        assertFalse("a failed write ends the wait", vm.state.value.isLoading)
        assertTrue("the screen can name the write", vm.state.value.contentFailed)
        assertNull("and it is not reported as a failed read", vm.state.value.error)

        h.withOneExercise()
        vm.retry()

        assertEquals("the retry runs the bundle write again", 1, h.seeds)
        assertFalse(vm.state.value.contentFailed)
        assertNotNull("then the lesson really loads", vm.state.value.lesson)
        vm.onEvent(LessonEvent.StartExercises)
        assertNotNull("and the session is usable", vm.state.value.currentExercise)
    }

    private fun Harness.with(vararg exercises: Exercise): Harness {
        lessonRepo.exercises[lessonId] = exercises.toList()
        return this
    }

    @Test
    fun `study time follows interactions - an idle lesson stops crediting`() {
        val h = Harness().withOneExercise()
        val vm = h.build()
        val t0 = System.currentTimeMillis()

        vm.onStudyTick(nowMs = t0 + StudyTimePolicy.IDLE_CAP_MILLIS + 60_000)
        assertEquals("an abandoned lesson credits nothing", 0L, h.progress.studySeconds)

        vm.onEvent(LessonEvent.StartExercises)
        vm.onStudyTick(nowMs = System.currentTimeMillis())
        assertEquals("acting again reopens the window", StudyTimePolicy.TICK_SECONDS, h.progress.studySeconds)
    }

    // -------------------------------------------------------------- speaking (graded)

    @Test
    fun `a spoken take is graded on this phone and written as one answer`() {
        // The step used to be self-assessed: hear the model, record, compare. The
        // recogniser that already grades the dialogue is the judge here too, so the take
        // is evidence — and one answer is still one transaction.
        val h = Harness().with(speaking("a1.test.sp.01", "Go straight and turn left."))
        val vm = h.build()

        vm.onEvent(LessonEvent.StartTake)
        assertEquals("the mic is asked for", 1, h.speech.starts)
        assertTrue("and the screen shows it is live", vm.state.value.isListening)

        h.speech.say("Go straight and turn left.")

        val outcome = vm.state.value.outcome
        assertNotNull("the take gets a verdict", outcome)
        assertEquals(SpeakingVerdict.CORRECT, outcome?.spokenVerdict)
        assertTrue("the sentence was said back", outcome!!.correct)
        assertEquals("and it counts once", 1, vm.state.value.askedCount)
        assertEquals(1, vm.state.value.correctCount)
        assertEquals("one answer is one transaction", 1, h.progress.transactions)
        assertTrue(
            "the speaking track moved",
            h.progress.attempts.any { it.first == Skill.SPEAKING && it.second },
        )
        assertTrue(
            "the transcript is the answer the step resumes from",
            h.lessonRepo.attemptsLog.any { it.answer == "Go straight and turn left." },
        )
        assertTrue(
            "and the session's own weakness is persisted",
            h.progress.dimensionWrites.any { it.first == MasteryDimension.PRODUCTION },
        )
    }

    @Test
    fun `a take said with a slip is near, and still counts as a hit`() {
        // "Near" is the scorer's own bar: every content word of the target, in the
        // learner's own order. It is a hit for the same reason a dialogue's near turn is
        // one — the meaning came across — but the banner still says it was not exact.
        val h = Harness().with(speaking("a1.test.sp.02", "Go straight and turn left at the bank."))
        val vm = h.build()

        vm.onEvent(LessonEvent.StartTake)
        h.speech.say("turn left at the bank go straight")

        val outcome = vm.state.value.outcome
        assertEquals(SpeakingVerdict.NEAR, outcome?.spokenVerdict)
        assertTrue("still a hit", outcome!!.correct)
        assertTrue("but not exact", outcome.almostCorrect)
        assertEquals(1, vm.state.value.correctCount)
    }

    @Test
    fun `a take the scorer cannot match writes no evidence but counts as a miss`() {
        // The app cannot tell a wrong sentence from a misheard one, so a retry claims
        // nothing — no day claim, no node, no meter (the dialogue's own rule). It is
        // still an attempt, so the item comes back later in the step.
        val h = Harness().with(speaking("a1.test.sp.03", "The bank is on the left."))
        val vm = h.build()

        vm.onEvent(LessonEvent.StartTake)
        h.speech.say("where is the station")

        val outcome = vm.state.value.outcome
        assertEquals(SpeakingVerdict.RETRY, outcome?.spokenVerdict)
        assertFalse(outcome!!.correct)
        assertEquals("nothing was claimed", 0, h.progress.transactions)
        assertTrue("no skill moved", h.progress.attempts.isEmpty())
        assertTrue("no dimension was written", h.progress.dimensionWrites.isEmpty())
        assertTrue("the item is asked again", vm.state.value.repeatsCurrent)
    }

    @Test
    fun `a take with no words is not an answer`() {
        val h = Harness().with(speaking("a1.test.sp.04", "Go straight."))
        val vm = h.build()

        vm.onEvent(LessonEvent.StartTake)
        h.speech.silence()

        assertTrue("the card says nothing was heard", vm.state.value.unheard)
        assertNull("no verdict is invented", vm.state.value.outcome)
        assertEquals(0, h.progress.transactions)
    }

    @Test
    fun `a take that broke off is never graded`() {
        // A missing model or a dead recorder is an app failure: scoring it would blame
        // the learner for hardware.
        val h = Harness().with(speaking("a1.test.sp.05", "Go straight."))
        val vm = h.build()

        vm.onEvent(LessonEvent.StartTake)
        h.speech.breakOff(SpeechError.MODEL_MISSING)

        assertTrue("the card names the failure", vm.state.value.recordingFailed)
        assertNull(vm.state.value.outcome)
        assertEquals(0, h.progress.transactions)
    }

    @Test
    fun `an install without the recogniser can move on without a claim`() {
        // The old self-assessed path, kept for the device that cannot hear at all: the
        // stage is never a trap, and an unassessed take is not quoted as a hit.
        val h = Harness().with(speaking("a1.test.sp.06", "Go straight."))
        val vm = h.build()

        vm.onEvent(LessonEvent.StartTake)
        h.speech.breakOff(SpeechError.MODEL_MISSING)
        vm.onEvent(LessonEvent.ContinueUnassessed)

        assertNotNull("the step can be left", vm.state.value.outcome)
        assertEquals("nothing was claimed", 0, h.progress.transactions)
        assertEquals("and it is not counted as a hit", 0, vm.state.value.correctCount)
        assertEquals("nor as a first try", 0, vm.state.value.firstAttemptAsked)
    }

    @Test
    fun `retrying a retry verdict reopens the mic and keeps it one first try`() {
        val h = Harness().with(speaking("a1.test.sp.07", "Go straight and turn left."))
        val vm = h.build()

        vm.onEvent(LessonEvent.StartTake)
        h.speech.say("go left")
        assertEquals(SpeakingVerdict.RETRY, vm.state.value.outcome?.spokenVerdict)

        vm.onEvent(LessonEvent.RetryTake)
        assertNull("the old verdict is cleared", vm.state.value.outcome)
        assertEquals("the mic is open again", 2, h.speech.starts)

        h.speech.say("Go straight and turn left.")
        assertEquals(SpeakingVerdict.CORRECT, vm.state.value.outcome?.spokenVerdict)
        assertEquals("the first attempt already counted", 1, vm.state.value.firstAttemptAsked)
        assertEquals("a fix on the retry is not a first try", 0, vm.state.value.firstAttemptCorrect)
    }

    @Test
    fun `moving to the next item closes the take`() {
        val h = Harness().with(
            speaking("a1.test.sp.08", "Go straight."),
            speaking("a1.test.sp.09", "Turn left at the bank."),
        )
        val vm = h.build()

        vm.onEvent(LessonEvent.StartTake)
        h.speech.say("Go straight.")
        assertTrue("the take was heard", vm.state.value.heardText.isNotBlank())

        vm.onEvent(LessonEvent.Next())

        assertNull("the next item has no verdict", vm.state.value.outcome)
        assertEquals("and nothing heard from the previous sentence", "", vm.state.value.heardText)
        assertEquals(
            "the next sentence is the one on screen",
            "a1.test.sp.09",
            (vm.state.value.currentExercise as Exercise.Speaking).id,
        )
    }

    @Test
    fun `leaving the lesson closes the microphone`() {
        val h = Harness().with(speaking("a1.test.sp.10", "Go straight."))
        val vm = h.build()

        vm.onEvent(LessonEvent.StartTake)
        h.speech.state.value = SpeechState(isListening = true)
        assertTrue(vm.state.value.isListening)

        vm.onAudioInterrupted()

        assertTrue("the service was told to stop", h.speech.stops >= 1)
        assertFalse("no mic is left running behind a home screen", vm.state.value.isListening)
    }

    @Test
    fun `the model never plays into an open mic`() {
        val h = Harness().with(speaking("a1.test.sp.11", "Go straight."))
        val vm = h.build()

        vm.onEvent(LessonEvent.StartTake)
        vm.onEvent(LessonEvent.SpeakText("Go straight."))

        assertTrue("the device's own voice would be recorded as the answer", h.speaker.spoken.isEmpty())
    }

    /** Answers the current exercise correctly, whatever option the shuffle put first. */
    private fun answerCorrectly(vm: LessonViewModel) {
        when (val exercise = vm.state.value.currentExercise) {
            is Exercise.MultipleChoice -> vm.onEvent(LessonEvent.SelectOption(exercise.correctIndex))
            is Exercise.FillBlank -> vm.onEvent(LessonEvent.TextAnswerChanged(exercise.accepted.first()))
            is Exercise.WordRecall -> vm.onEvent(LessonEvent.TextAnswerChanged(exercise.accepted.first()))
            is Exercise.SentenceProduction ->
                vm.onEvent(LessonEvent.TextAnswerChanged(exercise.accepted.first()))
            else -> error("no correct answer for ${exercise?.id}")
        }
        vm.onEvent(LessonEvent.Submit)
    }

    /** Answers the current exercise wrong. */
    private fun answerWrong(vm: LessonViewModel) {
        val exercise = vm.state.value.currentExercise as Exercise.MultipleChoice
        vm.onEvent(LessonEvent.SelectOption((exercise.correctIndex + 1) % exercise.options.size))
        vm.onEvent(LessonEvent.Submit)
    }

    @Test
    fun `an answer and its history are written in one transaction`() {
        val h = Harness().withOneExercise()
        val vm = h.build()

        answerCorrectly(vm)

        assertEquals("one answer, one transaction (checklist 0.1)", 1, h.progress.transactions)
        assertEquals("the answer itself is on record", 1, h.lessonRepo.attemptsLog.size)
        assertEquals(
            vm.state.value.currentExercise?.id,
            h.lessonRepo.attemptsLog.first().exerciseId,
        )
    }

    @Test
    fun `a refused transaction still grades the answer instead of swallowing the tap`() {
        val h = Harness().withOneExercise()
        // A database that cannot write: nothing inside the transaction runs, and the
        // session sees the failure (a real one rolls back, see AnswerTransactionTest).
        h.progress.transactionError = IllegalStateException("database unavailable")
        val vm = h.build()

        answerCorrectly(vm)

        assertEquals("the tap still produces a verdict", true, vm.state.value.outcome?.correct)
        assertTrue("no history row is pretended", h.lessonRepo.attemptsLog.isEmpty())
        assertTrue("no evidence is credited", h.progress.attempts.isEmpty())
        assertTrue("the graph is untouched", h.knowledge.recorded.isEmpty())
    }

    @Test
    fun `a missed exercise is re-asked once and the lesson ends on the second pass`() {
        val h = Harness().withOneExercise()
        val vm = h.build()

        assertEquals(LearningStage.PRACTICE, vm.state.value.activeStage)

        // Options are shuffled for display, so the wrong index is derived from the
        // exercise the screen is actually showing — never assumed to be 1.
        fun displayed() = vm.state.value.currentExercise as Exercise.MultipleChoice
        fun wrongIndex() = (displayed().correctIndex + 1) % displayed().options.size

        // Wrong answer → graded wrong and marked to repeat.
        vm.onEvent(LessonEvent.SelectOption(wrongIndex()))
        vm.onEvent(LessonEvent.Submit)
        assertEquals(false, vm.state.value.outcome?.correct)
        assertTrue("a miss is flagged for a second pass", vm.state.value.repeatsCurrent)

        // Continue → the SAME exercise comes back, only once.
        vm.onEvent(LessonEvent.Next())
        assertEquals(h.lessonRepo.exercises[lessonId]!!.first().id, vm.state.value.currentExercise?.id)
        assertEquals(0, vm.state.value.remainingCount)
        assertFalse(vm.state.value.repeatsCurrent)

        // Correct answer on the second pass → the session ends and is recorded.
        vm.onEvent(LessonEvent.SelectOption(displayed().correctIndex))
        vm.onEvent(LessonEvent.Submit)
        assertEquals(true, vm.state.value.outcome?.correct)
        vm.onEvent(LessonEvent.Next())
        assertTrue("the lesson is completed", vm.state.value.completed)
        assertTrue("completion is persisted", lessonId in h.lessonRepo.completed)
    }

    @Test
    fun `a repeat shows the same question in a different order`() {
        val h = Harness()
        h.lessonRepo.exercises[lessonId] = listOf(
            Exercise.MultipleChoice(
                id = "$lessonId.ex.09",
                lessonId = lessonId,
                question = "Choose",
                questionFa = null,
                options = listOf("a", "b", "c", "d"),
                correctIndex = 1,
                skill = Skill.VOCABULARY,
                explanation = null,
            ),
        )
        val vm = h.build()

        val firstOrder = (vm.state.value.currentExercise as Exercise.MultipleChoice).options
        val firstCorrect = firstOrder[(vm.state.value.currentExercise as Exercise.MultipleChoice).correctIndex]

        answerWrong(vm)
        vm.onEvent(LessonEvent.Next())

        // The same question, laid out differently: a learner who missed it must not
        // answer from the position they remember.
        val repeat = vm.state.value.currentExercise as Exercise.MultipleChoice
        assertTrue("the repeat is a new order", repeat.options != firstOrder)
        assertEquals("the answer is the same, only its place moved", firstCorrect, repeat.options[repeat.correctIndex])
    }

    @Test
    fun `a miss fixed on the repeat still leaves the lesson needing review`() {
        val h = Harness().withOneExercise()
        val vm = h.build()

        answerWrong(vm)
        vm.onEvent(LessonEvent.Next())
        answerCorrectly(vm)
        vm.onEvent(LessonEvent.Next())

        val done = vm.state.value
        assertTrue("the lesson still finishes", done.completed)
        assertEquals("two attempts, one first try", 2, done.askedCount)
        assertEquals(1, done.firstAttemptAsked)
        assertEquals("the fix came on the repeat, not the first try", 0, done.firstAttemptCorrect)
        assertEquals(0, done.firstAttemptAccuracyPercent)
        assertFalse("so the lesson needs a review", done.isConsolidated)
    }

    @Test
    fun `a lesson answered right the first time is consolidated`() {
        val h = Harness().withOneExercise()
        val vm = h.build()

        answerCorrectly(vm)
        vm.onEvent(LessonEvent.Next())

        val done = vm.state.value
        assertTrue(done.completed)
        assertEquals(1, done.firstAttemptAsked)
        assertEquals(100, done.firstAttemptAccuracyPercent)
        assertTrue("80% first-pass accuracy or better", done.isConsolidated)
    }

    @Test
    fun `an answered dimension is persisted for the next session (Room v6)`() {
        val h = Harness().withOneExercise()
        val vm = h.build()
        assertTrue("nothing observed yet, nothing to persist", h.progress.dimensionWrites.isEmpty())

        // A multiple choice answers RECOGNITION; the first evidence seeds it at 1f
        // (the same rule the engine and the skill rows use) and writes it down.
        answerCorrectly(vm)
        val write = h.progress.dimensionWrites.single()
        assertEquals(MasteryDimension.RECOGNITION, write.first)
        assertEquals("first correct answer seeds the dimension at 1f", 1f, write.second, 0f)
        assertEquals(
            "the row is what the next session will seed from",
            1f,
            h.progress.dimensionFlow.value[MasteryDimension.RECOGNITION] ?: 0f,
            0f,
        )
        assertNull(
            "an unobserved dimension stays absent — absent means no evidence, not 0",
            h.progress.dimensionFlow.value[MasteryDimension.PRODUCTION],
        )
    }

    @Test
    fun `a returning learner seeds the session from persisted dimensions and folds on top (Room v6)`() {
        val h = Harness().withOneExercise()
        // Last session's evidence: recognition was shaky, production once seen.
        h.progress.dimensionFlow.value = mapOf(
            MasteryDimension.RECOGNITION to 0.4f,
            MasteryDimension.PRODUCTION to 0.9f,
        )
        val vm = h.build()

        answerCorrectly(vm)

        // The fold started FROM the stored 0.4 — an unseeded fold would have
        // written the fresh-install seed (1f) instead.
        val expected = DefaultMasteryProfileEngine().apply(
            MasteryProfile(byDimension = mapOf(MasteryDimension.RECOGNITION to 0.4f)),
            MasteryDimension.RECOGNITION,
            correct = true,
        ).masteryOf(MasteryDimension.RECOGNITION)
        val write = h.progress.dimensionWrites.single()
        assertEquals(MasteryDimension.RECOGNITION, write.first)
        assertEquals("the session continued the cross-session fold", expected, write.second, 0.001f)
        assertTrue("a seeded value must not collapse to the fresh-install seed", write.second < 1f)

        // Dimensions this session did not answer survive the write-back untouched.
        assertEquals(
            0.9f,
            h.progress.dimensionFlow.value[MasteryDimension.PRODUCTION] ?: 0f,
            0.001f,
        )
    }

    @Test
    fun `a device without an English voice reports audio unavailable`() {
        val h = Harness(available = false).withOneExercise()
        val vm = h.build()
        assertTrue("the listening card must fall back to text", vm.state.value.audioUnavailable)
    }

    // --- checklist B-1: the adaptive slice now drives the session ------------

    @Test
    fun `a fresh lesson keeps the authored order`() {
        val h = Harness().with(
            choice("$lessonId.ex.01"),
            choice("$lessonId.ex.02"),
            blank("$lessonId.ex.03"),
        )
        val vm = h.build()
        assertEquals("$lessonId.ex.01", vm.state.value.currentExercise?.id)
        assertEquals(2, vm.state.value.remainingCount)
        assertNull("nothing weak has been observed yet", vm.state.value.weakestDimension)
    }

    @Test
    fun `the session front-loads the kind of knowing that is still untested`() {
        // Recognition first (multiple choice), then more recognition, and a recall
        // item (fill-in-the-blank) authored last.
        val h = Harness().with(
            choice("$lessonId.ex.01"),
            choice("$lessonId.ex.02"),
            choice("$lessonId.ex.03"),
            blank("$lessonId.ex.04"),
        )
        val vm = h.build()
        assertEquals("$lessonId.ex.01", vm.state.value.currentExercise?.id)

        // One correct answer consolidates recognition, so the unassessed recall
        // item leads the rest of the session instead of more multiple choice.
        answerCorrectly(vm)
        vm.onEvent(LessonEvent.Next())
        assertEquals("$lessonId.ex.04", vm.state.value.currentExercise?.id)
    }

    @Test
    fun `a miss comes back later in the session, it does not jump the queue`() {
        val h = Harness().with(choice("$lessonId.ex.01"), choice("$lessonId.ex.02"))
        val vm = h.build()
        answerWrong(vm)
        vm.onEvent(LessonEvent.Next())

        assertEquals("$lessonId.ex.02", vm.state.value.currentExercise?.id)
        assertEquals("the missed item is still queued", 1, vm.state.value.remainingCount)

        answerCorrectly(vm)
        vm.onEvent(LessonEvent.Next())
        assertEquals("the missed item is re-asked once", "$lessonId.ex.01", vm.state.value.currentExercise?.id)
    }

    @Test
    fun `a miss names the kind of knowing that broke`() {
        val h = Harness().withOneExercise()
        val vm = h.build()
        answerWrong(vm)
        assertEquals(MasteryDimension.RECOGNITION, vm.state.value.weakestDimension)
    }

    @Test
    fun `a focused session drills only the exercises that give evidence for the node`() {
        val h = Harness()
        h.knowledge.itemsFlow.value = listOf(
            KnowledgeItem(
                id = "grammar.test",
                type = KnowledgeType.GRAMMAR,
                title = "Test grammar",
                titleFa = "گرامر آزمایشی",
                level = LearningLevel.A1,
                prerequisites = emptyList(),
                lessonIds = listOf(lessonId),
                skills = listOf(Skill.GRAMMAR),
            ),
            KnowledgeItem(
                id = "vocab.test",
                type = KnowledgeType.VOCABULARY,
                title = "Test words",
                titleFa = "واژگان آزمایشی",
                level = LearningLevel.A1,
                prerequisites = emptyList(),
                lessonIds = listOf(lessonId),
                skills = listOf(Skill.VOCABULARY),
            ),
        )
        // The node has to be a real weakness for the engine to plan a block.
        h.knowledge.statesFlow.value = listOf(knowledgeState("grammar.test", 0.2f))
        h.with(choice("$lessonId.ex.01", Skill.GRAMMAR), choice("$lessonId.ex.02", Skill.VOCABULARY))

        val vm = h.build(focusItemId = "grammar.test")

        assertEquals("only the grammar exercise is evidence for the node", 1, vm.state.value.stageExercises.size)
        assertEquals("$lessonId.ex.01", vm.state.value.currentExercise?.id)
        assertEquals("گرامر آزمایشی", vm.state.value.focus?.titleFa)
        assertTrue("the engine's reason is shown", vm.state.value.focus!!.reasonFa.isNotEmpty())
    }

    @Test
    fun `a lesson session carries no focus banner`() {
        val h = Harness().withOneExercise()
        val vm = h.build()
        assertNull(vm.state.value.focus)
    }

    // --- checklist B-9: cancellation is never swallowed ----------------------

    @Test
    fun `cancelling the scope stops the session instead of writing state`() {
        val h = Harness()
        h.knowledge.itemsFlow.value = listOf(
            KnowledgeItem(
                id = "grammar.test",
                type = KnowledgeType.GRAMMAR,
                title = "Test grammar",
                titleFa = "گرامر آزمایشی",
                level = LearningLevel.A1,
                prerequisites = emptyList(),
                lessonIds = listOf(lessonId),
                skills = listOf(Skill.GRAMMAR),
            ),
        )
        h.knowledge.statesFlow.value = listOf(knowledgeState("grammar.test", 0.2f))
        h.with(choice("$lessonId.ex.01", Skill.GRAMMAR))
        // The focus lookup parks here, so the session is cancelled mid-use-case.
        val gate = CompletableDeferred<Unit>()
        h.knowledge.allItemsGate = gate

        val vm = h.build(focusItemId = "grammar.test")
        assertEquals("the session is still loading", 0, vm.state.value.stageExercises.size)

        vm.viewModelScope.cancel()
        gate.complete(Unit)

        // With a plain runCatching the CancellationException is swallowed, the
        // coroutine carries on and loads the lesson anyway — this is the guard.
        assertEquals("nothing may be loaded after cancellation", 0, vm.state.value.stageExercises.size)
        assertTrue("the screen stays in its loading state", vm.state.value.isLoading)
    }
}
