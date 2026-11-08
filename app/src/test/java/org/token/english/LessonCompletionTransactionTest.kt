package org.token.english

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
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
import org.token.english.data.local.entity.LessonEntity
import org.token.english.data.local.entity.VocabularyEntity
import org.token.english.data.repository.LessonRepositoryImpl
import org.token.english.data.repository.ProgressRepositoryImpl
import org.token.english.data.repository.ReviewRepositoryImpl
import org.token.english.domain.engine.DefaultMasteryEngine
import org.token.english.domain.repository.LessonRepository
import org.token.english.domain.usecase.CompleteLessonUseCase

/**
 * Finishing a lesson is one transaction (checklist 0.1), proven on real Room —
 * alongside `AnswerTransactionTest` (a lesson answer) and `EvidenceTransactionTest`
 * (a review grade, a conversation turn).
 *
 * Completion writes three rows: the lesson's completion stamp, its vocabulary
 * entering the review queue, and the cursor going back to the start. Written
 * separately, a crash between them could leave a lesson marked done whose words
 * never reached review — the learner would never be asked them again, and nothing on
 * screen would show it.
 *
 * The crash is injected as a failing *last* write inside the use case's own
 * transaction, so what rolls back the first two rows is the use case, not the test.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class LessonCompletionTransactionTest {

    private lateinit var db: AppDatabase
    private lateinit var lessons: LessonRepositoryImpl
    private lateinit var reviews: ReviewRepositoryImpl
    private lateinit var progress: ProgressRepositoryImpl
    private lateinit var complete: CompleteLessonUseCase

    private val lessonId = "a1.tx.lesson-02"
    private val now = 1_760_000_000_000L

    /** The real repository, except that resetting the cursor dies. */
    private class CrashingOnCursorReset(
        private val delegate: LessonRepositoryImpl,
    ) : LessonRepository by delegate {
        override suspend fun saveLessonIndex(lessonId: String, index: Int) {
            throw IllegalStateException("process died before the cursor was reset")
        }
    }

    @Before
    fun setUp() = runBlocking {
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
        lessons = LessonRepositoryImpl(db.contentDao(), db.reviewDao())
        reviews = ReviewRepositoryImpl(db.reviewDao())
        complete = CompleteLessonUseCase(lessons, progress)

        db.contentDao().insertLessons(listOf(lesson()))
        db.contentDao().insertVocabulary(
            listOf(vocabulary("$lessonId.w.1"), vocabulary("$lessonId.w.2")),
        )
        // A lesson part-way through: the cursor must survive a failed completion.
        lessons.saveLessonIndex(lessonId, 3)
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun lesson() = LessonEntity(
        id = lessonId,
        level = "A1",
        title = "Greetings",
        titleFa = "سلام و احوال‌پرسی",
        topic = "greetings",
        estimatedMinutes = 5,
        orderIndex = 1,
        grammarTipFa = null,
    )

    private fun vocabulary(id: String) = VocabularyEntity(
        id = id,
        word = "hello",
        translation = "سلام",
        definition = null,
        pronunciation = null,
        level = "A1",
        partOfSpeech = null,
        examplesJson = "[]",
        collocationsJson = "[]",
        lessonId = lessonId,
    )

    @Test
    fun `a crash mid-completion leaves the lesson unfinished and its words out of review`() =
        runBlocking {
            val crashing = CompleteLessonUseCase(CrashingOnCursorReset(lessons), progress)

            val died = runCatching { crashing(lessonId, now) }
            assertTrue("the failed write must reach the caller", died.isFailure)

            val state = lessons.getLessonState(lessonId)!!
            assertFalse("the lesson is not marked done", state.completed)
            assertNull("no completion stamp was invented", state.completedAt)
            assertEquals("the cursor was not reset", 3, state.currentIndex)
            assertEquals("no card reached the review queue", 0, reviews.countDue(now))
        }

    @Test
    fun `a committed completion stamps the lesson, queues its words and resets the cursor`() =
        runBlocking {
            complete(lessonId, now)

            val state = lessons.getLessonState(lessonId)!!
            assertTrue("the lesson is done", state.completed)
            assertNotNull("the completion time is recorded", state.completedAt)
            assertEquals("the cursor goes back to the start", 0, state.currentIndex)
            assertEquals("both words entered the queue", 2, reviews.countDue(now))
        }
}
