package org.token.english

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.token.english.data.local.AppDatabase
import org.token.english.data.local.entity.KnowledgeItemEntity
import org.token.english.data.repository.KnowledgeRepositoryImpl
import org.token.english.data.repository.LessonRepositoryImpl
import org.token.english.data.repository.ProgressRepositoryImpl
import org.token.english.domain.engine.DefaultMasteryEngine
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.LearningStage
import org.token.english.domain.model.LessonAttempt
import org.token.english.domain.model.Skill
import org.token.english.domain.usecase.SubmitExerciseUseCase

/**
 * One answer is written as one transaction (checklist 0.1), proven on real Room —
 * not on a fake.
 *
 * The claim, the skill meter, the curriculum node and the answer's own history are
 * four different tables owned by three DAOs. Written as separate statements, a
 * process death between two of them left the model contradicting itself, and nothing
 * in the app could tell which half was true. Room's transaction makes the whole
 * answer all-or-nothing, and this test kills the process *inside* it: after the
 * throw, every one of those four rows must be absent, and the question must still be
 * claimable as unanswered.
 *
 * Robolectric rather than a device: the rollback rule is about the database, not the
 * UI, and the suite has no instrumented run (AGENTS §8).
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class AnswerTransactionTest {

    private lateinit var db: AppDatabase
    private lateinit var progress: ProgressRepositoryImpl
    private lateinit var knowledge: KnowledgeRepositoryImpl
    private lateinit var lessons: LessonRepositoryImpl
    private lateinit var submit: SubmitExerciseUseCase

    private val lessonId = "a1.tx.lesson-01"
    private val itemId = "vocab.tx.1"
    private val now = 1_760_000_000_000L

    private val exercise = Exercise.MultipleChoice(
        id = "$lessonId.ex.01",
        lessonId = lessonId,
        question = "Choose the greeting",
        questionFa = null,
        options = listOf("hello", "goodbye"),
        correctIndex = 0,
        skill = Skill.VOCABULARY,
    )

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.getApplication(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        progress = ProgressRepositoryImpl(
            contentDao = db.contentDao(),
            reviewDao = db.reviewDao(),
            progressDao = db.progressDao(),
            masteryEngine = DefaultMasteryEngine(),
            database = db,
        )
        knowledge = KnowledgeRepositoryImpl(db.contentDao(), db.knowledgeDao())
        lessons = LessonRepositoryImpl(db.contentDao(), db.reviewDao())
        submit = SubmitExerciseUseCase(progress, knowledge)
    }

    @After
    fun tearDown() {
        db.close()
    }

    /** The authored graph node that makes this answer evidence about something. */
    private suspend fun seedGraph() {
        db.contentDao().insertKnowledgeItems(
            listOf(
                KnowledgeItemEntity(
                    id = itemId,
                    type = "VOCABULARY",
                    title = "hello",
                    titleFa = "سلام",
                    level = "A1",
                    prerequisitesJson = "[]",
                    lessonIdsJson = "[\"$lessonId\"]",
                    skillsJson = "[\"VOCABULARY\"]",
                    wordsJson = "[]",
                ),
            ),
        )
    }

    /**
     * The production shape of an answer (`LessonViewModel.submit`): the graded
     * exercise and the answer's history inside one transaction, with the crash
     * simulated at the worst moment — after both writes, before the commit.
     */
    private suspend fun answerOne(crashAfterWriting: Boolean) = progress.inTransaction {
        val graded = submit(exercise, "hello", now)
        lessons.recordAttempt(
            LessonAttempt(
                lessonId = lessonId,
                stageId = LearningStage.PRACTICE.id,
                exerciseId = exercise.id,
                answer = "hello",
                correct = graded.correct,
                at = now,
            ),
        )
        if (crashAfterWriting) throw IllegalStateException("process died mid-answer")
        graded
    }

    @Test
    fun `a crash mid-answer leaves no claim, no evidence and no history`() = runBlocking {
        seedGraph()

        val died = runCatching { answerOne(crashAfterWriting = true) }
        assertTrue("the simulated crash must reach the caller", died.isFailure)

        // The history has no answer…
        assertTrue(db.contentDao().getLessonAttempts(lessonId).isEmpty())
        // …the graph has no state…
        assertNull(knowledge.getState(itemId))
        // …the skill meter was not moved…
        assertFalse(progress.observeMastery().first().containsKey(Skill.VOCABULARY))
        // …and the day's claim is unspent. If any of the four rows had survived, the
        // claim would answer `false` here and the question would be dead for the day.
        assertTrue(progress.claimAnswer(lessonId, exercise.id, correct = true, now))
    }

    @Test
    fun `the whole answer lands together when nothing fails`() = runBlocking {
        seedGraph()

        val graded = answerOne(crashAfterWriting = false)

        assertTrue(graded.correct)
        assertEquals(1, db.contentDao().getLessonAttempts(lessonId).size)
        assertNotNull(knowledge.getState(itemId))
        assertTrue(progress.observeMastery().first().containsKey(Skill.VOCABULARY))
        // The claim was spent by the committed answer, so the same question cannot
        // move the model twice today.
        assertFalse(progress.claimAnswer(lessonId, exercise.id, correct = true, now))
    }

    @Test
    fun `a repeated answer inside its own transaction adds history but no second evidence`() =
        runBlocking {
            seedGraph()
            answerOne(crashAfterWriting = false)
            val exposure = knowledge.getState(itemId)!!.exposureCount

            answerOne(crashAfterWriting = false)

            // Two answers are on record — a retry is history — while the learner model
            // still counted the question once. The transaction boundary must not weaken
            // the claim rule (Phase 1).
            assertEquals(2, db.contentDao().getLessonAttempts(lessonId).size)
            assertEquals(exposure, knowledge.getState(itemId)!!.exposureCount)
        }
}
