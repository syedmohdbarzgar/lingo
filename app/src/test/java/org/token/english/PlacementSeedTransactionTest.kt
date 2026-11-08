package org.token.english

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.token.english.data.local.AppDatabase
import org.token.english.data.repository.ProgressRepositoryImpl
import org.token.english.domain.engine.DefaultMasteryEngine
import org.token.english.domain.engine.PlacementAnswer
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Skill
import org.token.english.domain.repository.ProgressRepository
import org.token.english.domain.usecase.AssessPlacementUseCase

/**
 * The placement test seeds the learner's profile in one transaction (checklist 0.1),
 * proven on real Room — the fifth flow behind `AnswerTransactionTest`,
 * `EvidenceTransactionTest`, `LessonCompletionTransactionTest` and the answer.
 *
 * Each answer folds into its skill meter, so a crash half-way through the test would
 * calibrate the profile from some answers and silently drop the rest: the learner
 * would look weaker (or stronger) than the test they actually took, and nothing on
 * screen would say so. Either the whole test calibrates or it changes nothing.
 *
 * The crash is injected as a failing *later* write inside the use case's own
 * transaction, so the rollback under test is the production code's.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class PlacementSeedTransactionTest {

    private lateinit var db: AppDatabase
    private lateinit var progress: ProgressRepositoryImpl

    /** The real repository, except that the third fold dies. */
    private class CrashingOnThirdAttempt(
        private val delegate: ProgressRepositoryImpl,
    ) : ProgressRepository by delegate {
        private var calls = 0

        override suspend fun applyAttempt(skill: Skill, correct: Boolean, source: String) {
            calls++
            if (calls == 3) throw IllegalStateException("process died mid-calibration")
            delegate.applyAttempt(skill, correct, source)
        }
    }

    private val answers = listOf(
        PlacementAnswer(LearningLevel.A1, Skill.VOCABULARY, correct = true),
        PlacementAnswer(LearningLevel.A1, Skill.GRAMMAR, correct = false),
        PlacementAnswer(LearningLevel.A2, Skill.VOCABULARY, correct = true),
        PlacementAnswer(LearningLevel.A2, Skill.READING, correct = true),
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
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `a crash mid-placement leaves no skill seeded rather than half of one`() = runBlocking {
        val crashing = AssessPlacementUseCase(CrashingOnThirdAttempt(progress))

        val died = runCatching { crashing(answers) }
        assertTrue("the failed write must reach the caller", died.isFailure)

        // The first two folds were written before the third threw; the transaction must
        // have taken them back with it.
        assertEquals(
            "no skill meter survives a failed calibration",
            emptyMap<Skill, Float>(),
            progress.observeMastery().first(),
        )
    }

    @Test
    fun `a completed placement seeds every answered skill in one go`() = runBlocking {
        val assessment = AssessPlacementUseCase(progress)(answers)

        assertNotNull("the test still returns its assessment", assessment)
        val mastery = progress.observeMastery().first()
        // A first correct answer seeds at 1.0 and an EWMA holds it there; the wrong one
        // seeds at 0.0. Two answers for VOCABULARY still mean one row.
        assertEquals(1f, mastery[Skill.VOCABULARY])
        assertEquals(0f, mastery[Skill.GRAMMAR])
        assertEquals(1f, mastery[Skill.READING])
    }
}
