package org.token.english

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.core.billing.AccessLevel
import org.token.english.core.billing.EntitlementPolicy
import org.token.english.core.billing.TrialAndSubscription
import org.token.english.data.content.ContentParser
import org.token.english.domain.engine.DefaultKnowledgeEngine
import org.token.english.domain.engine.DefaultLearningPlanner
import org.token.english.domain.engine.DefaultMasteryEngine
import org.token.english.domain.engine.KnowledgeEvidence
import org.token.english.domain.engine.MasteryEngine
import org.token.english.domain.engine.PlacementAnswer
import org.token.english.domain.engine.Sm2ReviewScheduler
import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.AppSettings
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.LearningActionType
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.LessonState
import org.token.english.domain.model.ReviewAttempt
import org.token.english.domain.model.ReviewContentType
import org.token.english.domain.model.ReviewItem
import org.token.english.domain.model.ReviewResult
import org.token.english.domain.model.ReviewState
import org.token.english.domain.model.Skill
import org.token.english.domain.model.StudyStats
import org.token.english.domain.model.ThemeMode
import org.token.english.domain.model.VocabularyItem
import org.token.english.domain.repository.KnowledgeRepository
import org.token.english.domain.repository.LessonRepository
import org.token.english.domain.repository.ProgressRepository
import org.token.english.domain.repository.ReviewRepository
import org.token.english.domain.repository.SettingsRepository
import org.token.english.domain.usecase.AssessPlacementUseCase
import org.token.english.domain.usecase.CompleteLessonUseCase
import org.token.english.domain.usecase.GetTodayPlanUseCase
import org.token.english.domain.usecase.ScorePlacementUseCase
import org.token.english.domain.usecase.SubmitExerciseUseCase
import org.token.english.domain.usecase.SubmitReviewUseCase
import java.io.File

/**
 * The whole learner journey, end to end (checklist P0) — the path a phone actually
 * takes, against the **real bundled content** and the real use cases, with the
 * repositories faked in memory because they are the only Android/Room boundary:
 *
 *   install → placement → start lesson → wrong answer → explanation → retry →
 *   complete lesson → review → progress
 *
 * The engines are the production ones ([DefaultMasteryEngine], [DefaultKnowledgeEngine],
 * [Sm2ReviewScheduler], [DefaultLearningPlanner]), so this is a genuine check that the
 * pieces compose — their individual behaviour is already pinned by their own unit
 * tests. Android-side rendering is out of scope here and needs a device.
 */
class LearningPathJourneyTest {

    private val now = 1_759_300_000_000L

    private fun contentFile(name: String): File {
        val candidates = listOf(
            File("src/main/assets/content/$name"),
            File("app/src/main/assets/content/$name"),
        )
        return candidates.firstOrNull { it.exists() } ?: error("content file not found: $name")
    }

    private val lessons: List<Lesson> = ContentParser.parseLessons(contentFile("lessons.json").readText())
    private val vocabulary: List<VocabularyItem> =
        ContentParser.parseVocabulary(contentFile("vocabulary.json").readText())
    private val knowledgeItems: List<KnowledgeItem> =
        ContentParser.parseKnowledge(contentFile("knowledge.json").readText())
    private val exercisesJson = contentFile("exercises.json").readText()

    private val firstLesson = lessons.first { it.level == LearningLevel.A1 }

    // ------------------------------------------------------------------ fakes

    private class FakeProgress(
        private val masteryEngine: MasteryEngine = DefaultMasteryEngine(),
    ) : ProgressRepository {
        val masteryFlow = MutableStateFlow<Map<Skill, Float>>(emptyMap())
        val statsFlow = MutableStateFlow(StudyStats(0L, 0, 0, 0, 0, 0f))
        private var studySeconds = 0L
        private var lessonCompletions = 0
        private val reviewResults = mutableListOf<ReviewResult>()

        override fun observeMastery(): Flow<Map<Skill, Float>> = masteryFlow
        override fun observeStats(): Flow<StudyStats> = statsFlow

        override suspend fun applyAttempt(skill: Skill, correct: Boolean, source: String) {
            val current = masteryFlow.value[skill] ?: 0f
            masteryFlow.value = masteryFlow.value + (skill to masteryEngine.update(current, correct))
            if (source == "review") {
                statsFlow.value = statsFlow.value.copy(reviewsToday = statsFlow.value.reviewsToday + 1)
            }
            statsFlow.value = statsFlow.value.copy(streakDays = maxOf(1, statsFlow.value.streakDays))
        }

        fun recordReviewResult(result: ReviewResult) {
            reviewResults += result
            val accuracy =
                if (reviewResults.isEmpty()) 0f
                else reviewResults.count { it != ReviewResult.AGAIN }.toFloat() / reviewResults.size
            statsFlow.value = statsFlow.value.copy(reviewAccuracy = accuracy)
        }

        override suspend fun addStudySeconds(seconds: Long) {
            studySeconds += seconds
            statsFlow.value = statsFlow.value.copy(todayStudySeconds = studySeconds)
        }

        fun noteLessonCompleted() {
            lessonCompletions++
            statsFlow.value = statsFlow.value.copy(lessonsCompleted = lessonCompletions)
        }

        fun noteWordLearned() {
            statsFlow.value = statsFlow.value.copy(wordsLearned = statsFlow.value.wordsLearned + 1)
        }

        override suspend fun reset() {
            masteryFlow.value = emptyMap()
            statsFlow.value = StudyStats(0L, 0, 0, 0, 0, 0f)
        }
    }

    private class FakeKnowledge(
        private val items: List<KnowledgeItem>,
        private val engine: DefaultKnowledgeEngine = DefaultKnowledgeEngine(),
    ) : KnowledgeRepository {
        private val statesFlow = MutableStateFlow<List<KnowledgeState>>(emptyList())

        override fun observeStates(): Flow<List<KnowledgeState>> = statesFlow
        override fun observePractisedCount(): Flow<Int> = statesFlow.map { it.size }
        override suspend fun getState(itemId: String): KnowledgeState? =
            statesFlow.value.firstOrNull { it.itemId == itemId }

        override suspend fun itemsForLesson(lessonId: String): List<KnowledgeItem> =
            items.filter { lessonId in it.lessonIds }

        override suspend fun allItems(): List<KnowledgeItem> = items

        override suspend fun recordAttempt(lessonId: String, skill: Skill, correct: Boolean, now: Long) {
            val lessonItems = itemsForLesson(lessonId)
            val byId = statesFlow.value.associateBy { it.itemId }
            KnowledgeEvidence.itemsFor(lessonItems, skill).forEach { item ->
                val updated = engine.applyAttempt(item.id, byId[item.id], correct, now)
                statesFlow.value = statesFlow.value.filterNot { it.itemId == item.id } + updated
            }
        }

        override suspend fun reset() {
            statesFlow.value = emptyList()
        }
    }

    private class FakeLessons(
        private val allLessons: List<Lesson>,
        private val vocabulary: List<VocabularyItem>,
        private val exercisesJson: String,
        private val review: FakeReviews,
        private val progress: FakeProgress,
    ) : LessonRepository {
        private val lessonStates = MutableStateFlow<List<LessonState>>(emptyList())
        private val indexes = mutableMapOf<String, Int>()

        override fun observeLessons(): Flow<List<Lesson>> = MutableStateFlow(allLessons)
        override fun observeLessonStates(): Flow<List<LessonState>> = lessonStates
        override suspend fun getLesson(lessonId: String): Lesson? =
            allLessons.firstOrNull { it.id == lessonId }

        override suspend fun getExercises(lessonId: String): List<Exercise> =
            ContentParser.parseExercises(exercisesJson, lessonId).map { it.second }

        override suspend fun getVocabulary(lessonId: String): List<VocabularyItem> =
            vocabulary.filter { it.lessonId == lessonId }

        override suspend fun saveLessonIndex(lessonId: String, index: Int) {
            indexes[lessonId] = index
        }

        override suspend fun getLessonState(lessonId: String): LessonState? =
            lessonStates.value.firstOrNull { it.lessonId == lessonId }

        override suspend fun completeLesson(lessonId: String, at: Long) {
            val rest = lessonStates.value.filterNot { it.lessonId == lessonId }
            lessonStates.value = rest + LessonState(lessonId, indexes[lessonId] ?: 0, true, at)
            progress.noteLessonCompleted()
        }

        override suspend fun enqueueLessonVocabulary(lessonId: String, now: Long) {
            getVocabulary(lessonId).forEach { word ->
                review.ensure(word.id, now)
                progress.noteWordLearned()
            }
        }
    }

    private class FakeReviews : ReviewRepository {
        val items = linkedMapOf<String, ReviewItem>()
        private val attempts = mutableMapOf<String, MutableList<ReviewAttempt>>()

        fun ensure(contentId: String, now: Long) {
            if (contentId in items) return
            items[contentId] = ReviewItem(
                contentId = contentId,
                contentType = ReviewContentType.VOCABULARY,
                state = ReviewState.NEW,
                dueAt = now,
                intervalDays = 0,
                easeFactor = Sm2ReviewScheduler.DEFAULT_EASE,
                repetitions = 0,
                lapses = 0,
                lastReviewedAt = null,
                introducedAt = now,
            )
        }

        override fun observeDueCount(now: Long): Flow<Int> =
            MutableStateFlow(items.values.count { it.dueAt <= now })

        override suspend fun getDue(now: Long, limit: Int): List<ReviewItem> =
            items.values.filter { it.dueAt <= now }.sortedBy { it.dueAt }.take(limit)

        override suspend fun countDue(now: Long): Int = items.values.count { it.dueAt <= now }
        override suspend fun getItem(contentId: String): ReviewItem? = items[contentId]

        override suspend fun schedule(item: ReviewItem) {
            items[item.contentId] = item
        }

        override suspend fun recordAttempt(attempt: ReviewAttempt) {
            attempts.getOrPut(attempt.contentId) { mutableListOf() } += attempt
        }

        override suspend fun getAttempts(contentId: String): List<ReviewAttempt> =
            attempts[contentId].orEmpty()

        override suspend fun reset() {
            items.clear()
            attempts.clear()
        }
    }

    private class FakeSettings : SettingsRepository {
        private val flow = MutableStateFlow(
            AppSettings(
                isFirstLaunch = false,
                level = LearningLevel.A1,
                themeMode = ThemeMode.SYSTEM,
                dailyGoalMinutes = 15,
                soundEnabled = true,
                // 0 == "no bundle stored yet", i.e. what a fresh install sees.
                contentVersion = 0,
                trialStartedAt = 0L,
                subscriptionUntil = 0L,
            ),
        )

        override val settings: Flow<AppSettings> = flow
        override suspend fun completeFirstLaunch() {}
        override suspend fun setLevel(level: LearningLevel) { flow.value = flow.value.copy(level = level) }
        override suspend fun setThemeMode(mode: ThemeMode) { flow.value = flow.value.copy(themeMode = mode) }
        override suspend fun setDailyGoalMinutes(minutes: Int) {
            flow.value = flow.value.copy(dailyGoalMinutes = minutes)
        }
        override suspend fun setSoundEnabled(enabled: Boolean) { flow.value = flow.value.copy(soundEnabled = enabled) }
        override suspend fun setDailyReminderEnabled(enabled: Boolean) {
            flow.value = flow.value.copy(dailyReminderEnabled = enabled)
        }
        override suspend fun setContentVersion(version: Int) { flow.value = flow.value.copy(contentVersion = version) }

        // SubscriptionStore: billing is out of scope for this journey — see
        // SubscriptionRecoveryTest and EntitlementPolicyTest.
        override fun observeTrialAndSubscription(): Flow<TrialAndSubscription> =
            flow.map { TrialAndSubscription(it.trialStartedAt, it.subscriptionUntil, 0L, 0L, 0L) }
        override suspend fun ensureTrialStarted(now: Long, elapsedRealtime: Long) {}
        override suspend fun saveTrialCheckpoint(consumedMs: Long, lastWallMs: Long, lastElapsedMs: Long) {}
        override suspend fun setSubscriptionUntil(epochMillis: Long) {
            flow.value = flow.value.copy(subscriptionUntil = epochMillis)
        }
    }

    /** One wired-up app instance, as AppContainer builds it. */
    private class Harness(
        lessons: List<Lesson>,
        vocabulary: List<VocabularyItem>,
        knowledgeItems: List<KnowledgeItem>,
        exercisesJson: String,
    ) {
        val progress = FakeProgress()
        val knowledge = FakeKnowledge(knowledgeItems)
        val reviews = FakeReviews()
        val settings = FakeSettings()
        val lessonsRepo = FakeLessons(lessons, vocabulary, exercisesJson, reviews, progress)
        val submitExercise = SubmitExerciseUseCase(progress, knowledge)
        val completeLesson = CompleteLessonUseCase(lessonsRepo, progress)
        val submitReview = SubmitReviewUseCase(reviews, Sm2ReviewScheduler(), progress)
        val assessPlacement = AssessPlacementUseCase(progress)
        val todayPlan = GetTodayPlanUseCase(
            lessons = lessonsRepo,
            reviews = reviews,
            progress = progress,
            settingsRepository = settings,
            planner = DefaultLearningPlanner(),
            knowledge = knowledge,
        )
    }

    private fun harness() = Harness(lessons, vocabulary, knowledgeItems, exercisesJson)

    // ------------------------------------------------------------------ journey

    @Test
    fun `a new install starts clean with a plan pointing at the first lesson`() = runBlocking {
        val app = harness()

        assertTrue(
            "no lesson progress on a fresh install",
            app.lessonsRepo.observeLessonStates().first().isEmpty(),
        )
        assertEquals("nothing due for review yet", 0, app.reviews.countDue(now))
        assertTrue("no skill mastery yet", app.progress.masteryFlow.value.isEmpty())

        val plan = app.todayPlan(now)
        assertNotNull("a new learner still gets a next lesson", plan.nextLesson)
        assertEquals("the first A1 lesson", firstLesson.id, plan.nextLesson?.id)
        assertEquals("and no review block", 0, plan.dueReviewCount)
    }

    @Test
    fun `placement is a skill assessment that calibrates mastery`() = runBlocking {
        val app = harness()
        val questions = ContentParser.parsePlacementQuestions(contentFile("placement.json").readText())
            .filterIsInstance<Exercise.MultipleChoice>()
        assertEquals("the bundle ships six bands of six", 36, questions.size)

        // Run it as a learner who is solid on grammar and shaky on vocabulary.
        val answers = questions.mapIndexed { index, question ->
            val skill = AnswerChecker.skillOf(question)
            PlacementAnswer(
                level = LearningLevel.entries[index / ScorePlacementUseCase.DEFAULT_BAND_SIZE],
                skill = skill,
                correct = skill == Skill.GRAMMAR,
            )
        }
        assertTrue("the placement answers carry authored skills", answers.any { it.skill == Skill.VOCABULARY })

        val assessment = app.assessPlacement(answers)

        // Only the skills this test actually measures are reported (in Skill order);
        // reading is now one of them (A-6).
        assertEquals(listOf(Skill.VOCABULARY, Skill.GRAMMAR, Skill.READING), assessment.bySkill.map { it.skill })
        assertEquals(listOf(Skill.GRAMMAR), assessment.strengths)
        assertEquals(listOf(Skill.VOCABULARY, Skill.READING), assessment.focusSkills)

        // Calibration: the profile is no longer blank, and it reflects the answers.
        val mastery = app.progress.masteryFlow.value
        assertFalse("placement seeds skill mastery", mastery.isEmpty())
        assertTrue(
            "a weak vocabulary must rank below a strong grammar",
            mastery.getValue(Skill.GRAMMAR) > (mastery[Skill.VOCABULARY] ?: 0f),
        )
    }

    @Test
    fun `the journey runs install to progress without losing a step`() = runBlocking {
        val app = harness()
        val lessonId = firstLesson.id

        // --- 2. Placement ------------------------------------------------------
        assertEquals(
            "all-correct placement answers unlock the top band",
            LearningLevel.C2,
            ScorePlacementUseCase()(List(36) { true }),
        )
        assertEquals(
            "a learner who passes only the first two bands is placed at A2",
            LearningLevel.A2,
            ScorePlacementUseCase()(List(12) { true } + List(24) { false }),
        )

        // --- 3. Start the lesson ----------------------------------------------
        val exercises = app.lessonsRepo.getExercises(lessonId)
        assertTrue("the lesson ships exercises", exercises.isNotEmpty())
        app.lessonsRepo.saveLessonIndex(lessonId, 0)

        // --- 4. A wrong answer is graded wrong AND explains itself (A-1) ------
        val choice = exercises.filterIsInstance<Exercise.MultipleChoice>()
            .first { it.explanation?.isNotBlank() == true }
        val wrongOption = choice.options.withIndex()
            .first { it.index != choice.correctIndex && it.value.isNotBlank() }
            .value

        val miss = app.submitExercise(choice, wrongOption, now)
        assertFalse("a wrong option is graded wrong", miss.correct)
        assertFalse("and is not reported as an almost-miss", miss.almostCorrect)
        assertEquals(
            "the correct answer is reported back for the banner",
            choice.options[choice.correctIndex],
            miss.correctAnswer,
        )
        assertTrue(
            "the exercise carries a Persian explanation to show after the miss",
            !choice.explanation.isNullOrBlank(),
        )

        // The miss reaches the learner model, not just the score.
        val touched = KnowledgeEvidence.itemsFor(app.knowledge.itemsForLesson(lessonId), miss.skill)
        if (touched.isNotEmpty()) {
            val state = app.knowledge.getState(touched.first().id)
            assertNotNull("the answer attributed evidence to a curriculum node", state)
            assertTrue(
                "a first miss seeds mastery at zero, not at 'half known'",
                (state!!.mastery) <= 0.0001f,
            )
        }

        // --- 5. Retry with the right answer -----------------------------------
        val retry = app.submitExercise(choice, choice.options[choice.correctIndex], now + 1_000)
        assertTrue("the retry is accepted", retry.correct)
        assertTrue(
            "a correct retry moves mastery up",
            (app.progress.masteryFlow.value[miss.skill] ?: 0f) > 0f,
        )

        // Answer the rest of the lesson so it can be completed.
        exercises.forEachIndexed { index, exercise ->
            val answer = when (exercise) {
                is Exercise.MultipleChoice -> exercise.options[exercise.correctIndex]
                is Exercise.FillBlank -> exercise.accepted.first()
                is Exercise.Translation -> exercise.accepted.first()
                is Exercise.Listening -> exercise.accepted.first()
                is Exercise.Speaking -> exercise.referenceAnswers.first()
            }
            app.submitExercise(exercise, answer, now + index)
        }

        // --- 6. Complete the lesson: its words enter the SRS queue -----------
        app.completeLesson(lessonId, now)
        val lessonWords = app.lessonsRepo.getVocabulary(lessonId)
        assertTrue("the lesson teaches vocabulary", lessonWords.isNotEmpty())
        assertEquals(
            "every word of the finished lesson is queued for review",
            lessonWords.size,
            app.reviews.countDue(now),
        )
        assertTrue(
            "the lesson is recorded as completed",
            app.lessonsRepo.getLessonState(lessonId)?.completed == true,
        )

        // --- 7. Review a due card --------------------------------------------
        val card = app.reviews.getDue(now, limit = 100).first()
        app.reviews.recordAttempt(
            ReviewAttempt(
                contentId = card.contentId,
                timestamp = now,
                result = ReviewResult.GOOD,
                responseTimeMs = 900,
                source = "review",
            ),
        )
        app.submitReview(card, ReviewResult.GOOD, now, responseTimeMs = 900)
        app.progress.recordReviewResult(ReviewResult.GOOD)

        val scheduled = app.reviews.getItem(card.contentId)!!
        assertTrue("a GOOD grade pushes the card into the future", scheduled.intervalDays > 0)
        assertTrue("…and out of today's queue", scheduled.dueAt > now)
        assertTrue(
            "the attempt is logged for the session",
            app.reviews.getAttempts(card.contentId).isNotEmpty(),
        )
        assertEquals(
            "one fewer card is due",
            lessonWords.size - 1,
            app.reviews.countDue(now),
        )

        // --- 8. Progress reflects the work ------------------------------------
        val stats = app.progress.statsFlow.value
        assertTrue("the review session is counted", stats.reviewsToday >= 1)
        assertEquals("the finished lesson shows up", 1, stats.lessonsCompleted)
        assertEquals("the words are counted as learned", lessonWords.size, stats.wordsLearned)
        assertTrue("perfect review session", stats.reviewAccuracy >= 0.999f)
        assertTrue(
            "skills were trained",
            app.progress.masteryFlow.value.keys.any { it == Skill.VOCABULARY || it == Skill.GRAMMAR },
        )
        assertTrue("the streak starts", stats.streakDays >= 1)

        // --- and the daily plan now leads with review (B-1) -------------------
        val plan = app.todayPlan(now)
        assertEquals("due cards are reported", lessonWords.size - 1, plan.dueReviewCount)
        assertEquals(
            "the adaptive plan leads with the review block",
            LearningActionType.REVIEW,
            plan.actions.firstOrNull()?.type,
        )
    }

    @Test
    fun `a locked entitlement does not touch local learning progress`() = runBlocking {
        val app = harness()

        // Trial over, no subscription: the paywall gates the UI, but nothing about
        // the offline learning core changes.
        assertEquals(
            AccessLevel.LOCKED,
            EntitlementPolicy.level(now, trialRemainingMs = 0L, subscriptionUntil = 0L),
        )

        app.completeLesson(firstLesson.id, now)
        assertTrue(
            "local progress still exists while locked",
            app.reviews.countDue(now) > 0,
        )
    }
}
