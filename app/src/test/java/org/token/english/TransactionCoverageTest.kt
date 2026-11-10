package org.token.english

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.engine.PlacementAnswer
import org.token.english.domain.engine.Sm2ReviewScheduler
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.ReviewContentType
import org.token.english.domain.model.ReviewItem
import org.token.english.domain.model.ReviewResult
import org.token.english.domain.model.ReviewState
import org.token.english.domain.model.Skill
import org.token.english.domain.repository.ProgressRepository
import org.token.english.domain.usecase.AssessPlacementUseCase
import org.token.english.domain.usecase.CompleteLessonUseCase
import org.token.english.domain.usecase.RecordConversationTurnUseCase
import org.token.english.domain.usecase.SubmitReviewUseCase
import java.io.File

/**
 * The atomic-evidence rule (checklist 0.1), guarded so a new path cannot break it
 * quietly.
 *
 * Four flows write several learner rows per result — a lesson answer, a review
 * grade, a conversation turn, finishing a lesson — and each commits them in one
 * `ProgressRepository.inTransaction`. The rollback proof is behavioural
 * (`AnswerTransactionTest`, `EvidenceTransactionTest`,
 * `LessonCompletionTransactionTest`); this class answers the other question: what
 * stops the *next* multi-write use case from writing two tables in two statements?
 *
 * Two checks:
 * 1. **Discovery (static).** Every class in `domain/usecase/` that calls two or more
 *    repository write methods must be registered below, either as owning a
 *    transaction or as deliberately wrapped by its caller. Write methods are read
 *    from `Repositories.kt` itself (every `suspend fun` that is not a read), so a new
 *    repository write is covered without editing this file.
 * 2. **Behaviour.** Each registered owner really opens exactly one transaction when
 *    invoked — `inTransaction` is the seam, not a label.
 *
 * The discovery counts *call sites*, not iterations: a write inside a loop reads as one
 * call and cannot be found on its own, which is why `AssessPlacementUseCase` (one
 * `applyAttempt` per placement answer) is registered by hand — a half-seeded profile
 * is a wrong claim about the learner, not a harmless repeat.
 */
class TransactionCoverageTest {

    /** Use cases that own their transaction, and the rows it commits together. */
    private val atomic = mapOf(
        "RecordConversationTurnUseCase" to "the day's claim + the graph + the skill track",
        "CompleteLessonUseCase" to "the completion stamp + queued words + the cursor",
        "SubmitReviewUseCase" to "the next schedule + the attempt log + the meter",
        // One write call, many rows (one per answer): a partial seed is still a lie about
        // the learner's profile, so the whole calibration is registered here too.
        "AssessPlacementUseCase" to "every placement answer's fold into its skill meter",
    )

    /**
     * Use cases whose transaction is opened by their caller, with the reason. The
     * lesson answer is written together with the answer's own history, which the
     * session owns: `LessonViewModel.submit` wraps both in one transaction and
     * `LessonViewModelTest` pins that (`transactions == 1`).
     */
    private val wrappedByCaller = mapOf(
        "SubmitExerciseUseCase" to "LessonViewModel.submit wraps it with the answer history",
    )

    /** Tests run from the module dir (`app/`) or the repo root — accept both. */
    private fun repoFile(vararg candidates: String): File =
        candidates.map(::File).firstOrNull(File::exists)
            ?: error("none of ${candidates.joinToString()} exists — run tests from the repo root or app/")

    private fun useCaseSource(): String = repoFile(
        "../app/src/main/java/org/token/english/domain/usecase/LearningUseCases.kt",
        "app/src/main/java/org/token/english/domain/usecase/LearningUseCases.kt",
    ).readText()

    private fun repositorySource(): String = repoFile(
        "../app/src/main/java/org/token/english/domain/repository/Repositories.kt",
        "app/src/main/java/org/token/english/domain/repository/Repositories.kt",
    ).readText()

    /**
     * Repository writes, derived from the interfaces: a `suspend fun` that is not a
     * read. New write methods are picked up without touching this test.
     */
    private fun writeMethods(): Set<String> = Regex("""suspend fun (\w+)\s*\(""")
        .findAll(repositorySource())
        .map { it.groupValues[1] }
        .filterNot { name ->
            name.startsWith("get") || name.startsWith("observe") || name.startsWith("count") ||
                name.startsWith("items") || name.startsWith("all") || name.startsWith("find") ||
                name.startsWith("is") || name.startsWith("has") || name.startsWith("load") ||
                name.startsWith("recent")
        }
        .toSet()

    /** `class Name` → its body, up to the next top-level class. */
    private fun classBodies(source: String): Map<String, String> {
        val starts = Regex("""(?m)^class (\w+)""").findAll(source).toList()
        return starts.mapIndexed { index, match ->
            val end = starts.getOrNull(index + 1)?.range?.first ?: source.length
            match.groupValues[1] to source.substring(match.range.first, end)
        }.toMap()
    }

    private fun repositoryWrites(body: String, writes: Set<String>): Set<String> =
        Regex("""\.(\w+)\s*\(""").findAll(body)
            .map { it.groupValues[1] }
            .toSet()
            .intersect(writes)

    @Test
    fun `every use case that writes more than once is registered as atomic`() {
        val bodies = classBodies(useCaseSource())
        val writes = writeMethods()
        assertTrue("no repository write method was found — the parser broke", writes.isNotEmpty())

        val unregistered = bodies
            .filterValues { repositoryWrites(it, writes).size >= 2 }
            .keys
            .filterNot { it in atomic || it in wrappedByCaller }

        assertTrue(
            "these use cases write two or more tables without one transaction: " +
                "$unregistered — wrap the writes in ProgressRepository.inTransaction and " +
                "register the class here, or say here why its caller owns the transaction",
            unregistered.isEmpty(),
        )
    }

    @Test
    fun `each registered owner and caller-wrapped case still exists`() {
        val bodies = classBodies(useCaseSource())
        (atomic.keys + wrappedByCaller.keys).filterNot { it in bodies }.let { gone ->
            assertTrue(
                "registered use case(s) $gone no longer exist — a rename must not silently " +
                    "drop the rule's coverage",
                gone.isEmpty(),
            )
        }
        atomic.keys.forEach { name ->
            assertTrue(
                "$name is registered as atomic but does not call inTransaction",
                bodies.getValue(name).contains("inTransaction"),
            )
        }
    }

    /** Counts how many transactions a use case opens, delegating everything else. */
    private class CountingProgress(
        private val delegate: FakeProgressRepository = FakeProgressRepository(),
    ) : ProgressRepository by delegate {
        var opened = 0

        override suspend fun <T> inTransaction(block: suspend () -> T): T {
            opened++
            return block()
        }
    }

    @Test
    fun `every registered owner opens exactly one transaction`() = runTest {
        val card = ReviewItem(
            contentId = "word.guard.1",
            contentType = ReviewContentType.VOCABULARY,
            state = ReviewState.NEW,
            dueAt = NOW,
            intervalDays = 0,
            easeFactor = 2.5f,
            repetitions = 0,
            lapses = 0,
            lastReviewedAt = null,
        )

        val invocations: Map<String, suspend (ProgressRepository) -> Unit> = mapOf(
            "SubmitReviewUseCase" to { progress ->
                SubmitReviewUseCase(
                    FakeReviewRepository(),
                    Sm2ReviewScheduler(),
                    progress,
                    FakeKnowledgeRepository(),
                )(
                    card,
                    ReviewResult.GOOD,
                    NOW,
                    responseTimeMs = 800L,
                    lessonId = "a1.guard.lesson-01",
                )
            },
            "RecordConversationTurnUseCase" to { progress ->
                RecordConversationTurnUseCase(progress, FakeKnowledgeRepository(), FakeConversationRepository())(
                    "a1.guard.lesson-01",
                    "a1.guard.lesson-01.sp.01",
                    org.token.english.domain.engine.SpeakingVerdict.CORRECT,
                    spoken = true,
                    now = NOW,
                )
            },
            "CompleteLessonUseCase" to { progress ->
                CompleteLessonUseCase(FakeLessonRepository(), progress)("a1.guard.lesson-01", NOW)
            },
            "AssessPlacementUseCase" to { progress ->
                AssessPlacementUseCase(progress)(
                    listOf(
                        PlacementAnswer(LearningLevel.A1, Skill.VOCABULARY, correct = true),
                        PlacementAnswer(LearningLevel.A1, Skill.GRAMMAR, correct = false),
                    ),
                )
            },
        )

        assertEquals(
            "the behaviour check must cover every registered owner",
            atomic.keys,
            invocations.keys,
        )
        invocations.forEach { (name, invoke) ->
            val counting = CountingProgress()
            invoke(counting)
            assertEquals("$name must commit its rows in one transaction", 1, counting.opened)
        }
    }

    private companion object {
        const val NOW = 1_760_000_000_000L
    }
}
