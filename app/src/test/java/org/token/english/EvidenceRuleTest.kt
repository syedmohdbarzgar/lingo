package org.token.english

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.Skill
import org.token.english.domain.usecase.SubmitExerciseUseCase

/**
 * The evidence rule (Phase 1): one answer per exercise per day moves the learner
 * model, and one answer can feed more than one track.
 *
 * The unit is the **exercise**, not the exercise shape: a remediation block drills
 * several different questions that happen to share a format, and collapsing those to
 * one confirmation a day would leave a drilled learner with no progress until
 * tomorrow. What must not count twice is the same question answered again — the retry
 * button, or re-running the lesson.
 *
 * What is pinned here:
 *
 * - a repeat of the same exercise on the same day writes nothing (no mastery, no graph);
 * - a different exercise in the same shape is new evidence;
 * - the same exercise the next day counts again;
 * - a miss can be corrected the same day, but a miss after a hit does not punish twice;
 * - a written sentence credits WRITING as well as the skill its item is tagged with.
 */
class EvidenceRuleTest {

    private val hour = 3_600_000L

    /** Local noon, so a ±1h DST shift can never land on the same calendar day. */
    private val dayOne = 1_790_000_000_000L + 12 * hour
    private val dayTwo = dayOne + 30 * hour

    private fun production(id: String, skill: Skill? = Skill.VOCABULARY) = Exercise.SentenceProduction(
        id = id,
        lessonId = "a1.articles.lesson-01",
        prompt = "بگو دیروز خرید نکردم.",
        mustUse = listOf("didn't", "yesterday"),
        minWords = 4,
        accepted = listOf("I didn't go shopping yesterday."),
        skill = skill,
    )

    private fun multipleChoice(id: String) = Exercise.MultipleChoice(
        id = id,
        lessonId = "a1.articles.lesson-01",
        question = "Choose the article.",
        questionFa = "حرف تعریف را انتخاب کن.",
        options = listOf("a", "an", "the", "—"),
        correctIndex = 0,
        skill = Skill.GRAMMAR,
    )

    // ---- one answer per exercise per day --------------------------------------

    @Test
    fun `re-answering the same exercise on the same day moves nothing`() = runTest {
        val progress = FakeProgressRepository()
        val knowledge = FakeKnowledgeRepository()
        val submit = SubmitExerciseUseCase(progress, knowledge)
        val exercise = production("a1.articles.ex.09")

        submit(exercise, "I didn't go shopping yesterday.", dayOne)
        submit(exercise, "I didn't go shopping yesterday.", dayOne + 1_000L)

        assertEquals(
            "the second answer is the same question, so it is not new evidence",
            listOf(Skill.VOCABULARY to true, Skill.WRITING to true),
            progress.attempts,
        )
        assertEquals("and the graph was told once", 1, knowledge.recorded.size)
    }

    @Test
    fun `a different exercise in the same shape still counts`() = runTest {
        val progress = FakeProgressRepository()
        val submit = SubmitExerciseUseCase(progress, FakeKnowledgeRepository())

        // A remediation block drills several questions of one format; each one is a
        // different thing to answer, so each is evidence.
        submit(production("a1.articles.ex.09"), "I didn't go shopping yesterday.", dayOne)
        submit(production("a1.articles.ex.10"), "I didn't go shopping yesterday.", dayOne + 1_000L)

        assertEquals(
            listOf(
                Skill.VOCABULARY to true,
                Skill.WRITING to true,
                Skill.VOCABULARY to true,
                Skill.WRITING to true,
            ),
            progress.attempts,
        )
    }

    @Test
    fun `the same exercise the next day counts again`() = runTest {
        val progress = FakeProgressRepository()
        val submit = SubmitExerciseUseCase(progress, FakeKnowledgeRepository())
        val exercise = production("a1.articles.ex.09")

        submit(exercise, "I didn't go shopping yesterday.", dayOne)
        submit(exercise, "I didn't go shopping yesterday.", dayTwo)

        assertEquals(
            "a delayed recall is the whole point of the retest gate",
            2,
            progress.attempts.count { it.first == Skill.VOCABULARY },
        )
    }

    @Test
    fun `a corrected miss counts, but a miss after a hit does not punish twice`() = runTest {
        val progress = FakeProgressRepository()
        val submit = SubmitExerciseUseCase(progress, FakeKnowledgeRepository())
        val exercise = production("a1.articles.ex.09")

        // Wrong, then fixed: fixing it is real evidence, and blocking it would leave the
        // learner stuck on a question the app had just marked wrong.
        submit(exercise, "I go shopping", dayOne)
        submit(exercise, "I didn't go shopping yesterday.", dayOne + 1_000L)

        // Right, then a stray wrong answer: already claimed as a hit today.
        submit(production("a1.articles.ex.11"), "I didn't go shopping yesterday.", dayOne)
        submit(production("a1.articles.ex.11"), "nonsense", dayOne + 2_000L)

        assertEquals(
            listOf(
                Skill.VOCABULARY to false,
                Skill.WRITING to false,
                Skill.VOCABULARY to true,
                Skill.WRITING to true,
                Skill.VOCABULARY to true,
                Skill.WRITING to true,
            ),
            progress.attempts,
        )
    }

    @Test
    fun `the claim survives the process, because it is stored`() = runTest {
        // The rule is only worth something if a restart cannot re-earn it: the fake
        // stands in for the v9 `question_attempt` row, so a second use-case instance
        // reading the same store must see the claim.
        val progress = FakeProgressRepository()
        val first = SubmitExerciseUseCase(progress, FakeKnowledgeRepository())
        val exercise = production("a1.articles.ex.09")
        first(exercise, "I didn't go shopping yesterday.", dayOne)

        val second = SubmitExerciseUseCase(progress, FakeKnowledgeRepository())
        second(exercise, "I didn't go shopping yesterday.", dayOne + hour)

        assertEquals(
            "a fresh use case over the same row writes nothing new",
            1,
            progress.attempts.count { it.first == Skill.VOCABULARY },
        )
    }

    // ---- one answer, the tracks it actually evidences --------------------------

    @Test
    fun `a written sentence credits writing as well as the skill it is tagged with`() = runTest {
        val progress = FakeProgressRepository()
        val submit = SubmitExerciseUseCase(progress, FakeKnowledgeRepository())

        val outcome = submit(production("a1.articles.ex.09"), "I didn't go shopping yesterday.", dayOne)

        assertTrue(outcome.correct)
        assertEquals(
            "the item's skill says what it tests; the format says how it was produced",
            listOf(Skill.VOCABULARY to true, Skill.WRITING to true),
            progress.attempts,
        )
    }

    @Test
    fun `a written sentence that misses the target structure credits neither`() = runTest {
        val progress = FakeProgressRepository()
        val submit = SubmitExerciseUseCase(progress, FakeKnowledgeRepository())

        val outcome = submit(production("a1.articles.ex.09"), "I go shopping", dayOne)

        assertFalse(outcome.correct)
        assertEquals(
            listOf(Skill.VOCABULARY to false, Skill.WRITING to false),
            progress.attempts,
        )
    }

    @Test
    fun `a multiple-choice answer never credits writing`() = runTest {
        val progress = FakeProgressRepository()
        val submit = SubmitExerciseUseCase(progress, FakeKnowledgeRepository())

        submit(multipleChoice("a1.articles.ex.01"), "a", dayOne)

        assertEquals(listOf(Skill.GRAMMAR to true), progress.attempts)
    }
}
