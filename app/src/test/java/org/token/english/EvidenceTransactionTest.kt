package org.token.english

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
import org.token.english.data.repository.ProgressRepositoryImpl
import org.token.english.data.repository.ReviewRepositoryImpl
import org.token.english.domain.engine.DefaultMasteryEngine
import org.token.english.domain.engine.Sm2ReviewScheduler
import org.token.english.domain.engine.SpeakingVerdict
import org.token.english.domain.model.ReviewContentType
import org.token.english.domain.model.ReviewItem
import org.token.english.domain.model.ReviewResult
import org.token.english.domain.model.ReviewState
import org.token.english.domain.model.Skill
import org.token.english.domain.repository.ProgressRepository
import org.token.english.domain.usecase.RecordConversationTurnUseCase
import org.token.english.domain.usecase.SubmitReviewUseCase

/**
 * The other two graded results are one transaction each, proven on real Room
 * (checklist 0.1, alongside `AnswerTransactionTest` for the lesson answer).
 *
 * A **review grade** writes the card's next schedule, the attempt log and the skill
 * meter; a **conversation turn** writes the day's claim, the graph and the skill
 * track. Both used to be several statements, so a process death in the middle could
 * store an interval with no attempt behind it, or spend the day's claim on a turn
 * that never reached Speaking.
 *
 * The crash is injected as a **failing last write** inside the use case's own
 * transaction — not by wrapping the call in another transaction here, which would
 * prove nothing about the use case. The two writes that run before it must be gone.
 *
 * Robolectric rather than a device: the rule is about the database, not the UI, and
 * the suite has no instrumented run (AGENTS §8).
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class EvidenceTransactionTest {

    private lateinit var db: AppDatabase
    private lateinit var progress: ProgressRepositoryImpl
    private lateinit var knowledge: KnowledgeRepositoryImpl
    private lateinit var reviews: ReviewRepositoryImpl
    private lateinit var submitReview: SubmitReviewUseCase
    private lateinit var recordTurn: RecordConversationTurnUseCase

    private val lessonId = "a1.tx.dialogue-01"
    private val itemId = "phonology.tx.1"
    private val turnId = "$lessonId.sp.01"
    private val now = 1_760_000_000_000L

    private val card = ReviewItem(
        contentId = "word.tx.1",
        contentType = ReviewContentType.VOCABULARY,
        state = ReviewState.NEW,
        dueAt = now,
        intervalDays = 0,
        easeFactor = 2.5f,
        repetitions = 0,
        lapses = 0,
        lastReviewedAt = null,
    )

    /**
     * The real repository, except that the *last* write of both flows (the skill
     * meter) dies — the worst possible moment, since the schedule or the claim is
     * already on disk. `inTransaction` is inherited from the real implementation, so
     * the rollback under test is the use case's own.
     */
    private class CrashingOnMeter(
        private val delegate: ProgressRepositoryImpl,
    ) : ProgressRepository by delegate {
        override suspend fun applyAttempt(skill: Skill, correct: Boolean, source: String) {
            throw IllegalStateException("process died before the meter was written")
        }
    }

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
        reviews = ReviewRepositoryImpl(db.reviewDao())
        submitReview = SubmitReviewUseCase(reviews, Sm2ReviewScheduler(), progress)
        recordTurn = RecordConversationTurnUseCase(progress, knowledge)
    }

    @After
    fun tearDown() {
        db.close()
    }

    /** The authored node a spoken turn is evidence about (PHONOLOGY ← SPEAKING). */
    private suspend fun seedPhonology() {
        db.contentDao().insertKnowledgeItems(
            listOf(
                KnowledgeItemEntity(
                    id = itemId,
                    type = "PHONOLOGY",
                    title = "th sounds",
                    titleFa = "صداهای th",
                    level = "A1",
                    prerequisitesJson = "[]",
                    lessonIdsJson = "[\"$lessonId\"]",
                    skillsJson = "[\"SPEAKING\"]",
                ),
            ),
        )
    }

    @Test
    fun `a crash while grading a card leaves the schedule, the log and the meter untouched`() =
        runBlocking {
            reviews.schedule(card)
            val crashing = SubmitReviewUseCase(
                reviews,
                Sm2ReviewScheduler(),
                CrashingOnMeter(progress),
            )

            val died = runCatching {
                crashing(card, ReviewResult.GOOD, now, responseTimeMs = 900L)
            }
            assertTrue("the failed write must reach the caller", died.isFailure)

            assertTrue("no grade is on record", reviews.getAttempts(card.contentId).isEmpty())
            val after = reviews.getItem(card.contentId)!!
            assertEquals("the card was not rescheduled", card.dueAt, after.dueAt)
            assertEquals("its repetition count did not move", card.repetitions, after.repetitions)
            assertNull(after.lastReviewedAt)
            assertFalse(
                "the skill meter was not moved",
                progress.observeMastery().first().containsKey(Skill.VOCABULARY),
            )
        }

    @Test
    fun `a committed grade writes the schedule, the log and the meter together`() = runBlocking {
        reviews.schedule(card)

        submitReview(card, ReviewResult.GOOD, now, responseTimeMs = 900L)

        val after = reviews.getItem(card.contentId)!!
        assertEquals(1, after.repetitions)
        assertEquals(now, after.lastReviewedAt)
        assertTrue("the card moved into the future", after.dueAt > now)
        assertEquals(1, reviews.getAttempts(card.contentId).size)
        assertTrue(progress.observeMastery().first().containsKey(Skill.VOCABULARY))
    }

    @Test
    fun `a crash mid-turn leaves the turn unclaimed and Speaking unmoved`() = runBlocking {
        seedPhonology()
        val crashing = RecordConversationTurnUseCase(CrashingOnMeter(progress), knowledge)

        val died = runCatching {
            crashing(lessonId, turnId, SpeakingVerdict.CORRECT, spoken = true, now = now)
        }
        assertTrue("the failed write must reach the caller", died.isFailure)

        assertNull("the graph has no state for the node", knowledge.getState(itemId))
        assertFalse(
            "Speaking was not credited",
            progress.observeMastery().first().containsKey(Skill.SPEAKING),
        )
        // The claim is unspent, so the learner can answer this turn again instead of it
        // being silently used up for the day by a write that never completed.
        assertTrue(progress.claimAnswer(lessonId, turnId, correct = true, now))
    }

    @Test
    fun `a committed turn writes the claim, the graph and the track together`() = runBlocking {
        seedPhonology()

        val counted = recordTurn(lessonId, turnId, SpeakingVerdict.CORRECT, spoken = true, now = now)

        assertTrue("the turn counted", counted)
        assertEquals(1, knowledge.getState(itemId)?.exposureCount ?: -1)
        assertTrue(progress.observeMastery().first().containsKey(Skill.SPEAKING))
        // A replayed dialogue cannot raise the bar twice for the same turn.
        assertFalse(progress.claimAnswer(lessonId, turnId, correct = true, now))
    }
}
