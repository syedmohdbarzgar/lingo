package org.token.english.domain.usecase

import kotlinx.coroutines.flow.first
import org.token.english.domain.engine.LearningPlanner
import org.token.english.domain.engine.ReviewScheduler
import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.AppSettings
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.LessonState
import org.token.english.domain.model.ReviewAttempt
import org.token.english.domain.model.ReviewContentType
import org.token.english.domain.model.ReviewItem
import org.token.english.domain.model.ReviewResult
import org.token.english.domain.model.Skill
import org.token.english.domain.model.StudyStats
import org.token.english.domain.model.TodayPlan
import org.token.english.domain.repository.KnowledgeRepository
import org.token.english.domain.repository.LessonRepository
import org.token.english.domain.repository.ProgressRepository
import org.token.english.domain.repository.ReviewRepository
import org.token.english.domain.repository.SettingsRepository

data class ExerciseOutcome(
    val correct: Boolean,
    val correctAnswer: String,
    val skill: Skill,
    /**
     * One edit away from an accepted answer (checklist A-2): still graded wrong
     * (no auto-accept, mastery untouched in the positive direction), but the UI
     * can say «تقریباً درست — املای کلمه را بررسی کن» instead of a bare miss.
     */
    val almostCorrect: Boolean = false,
)

/**
 * Grades one exercise, updates skill mastery and advances the lesson cursor.
 * A word enters the SRS queue when its lesson completes (it has been taught then).
 */
class SubmitExerciseUseCase(
    private val progress: ProgressRepository,
    private val knowledge: KnowledgeRepository,
) {
    suspend operator fun invoke(
        exercise: Exercise,
        answer: String,
        now: Long,
    ): ExerciseOutcome {
        val verdict = AnswerChecker.grade(exercise, answer)
        val correct = verdict == AnswerChecker.Verdict.CORRECT
        val skill = AnswerChecker.skillOf(exercise)
        progress.applyAttempt(skill, correct, source = "lesson")
        // The same answer is also evidence about the curriculum nodes the lesson
        // teaches (audit §4/§19) — this is what turns exercise results into a
        // learner model instead of a score. A lesson the graph does not know (the
        // placement test) simply yields no nodes.
        knowledge.recordAttempt(exercise.lessonId, skill, correct, now)
        return ExerciseOutcome(
            correct = correct,
            correctAnswer = AnswerChecker.correctAnswerText(exercise),
            skill = skill,
            almostCorrect = verdict == AnswerChecker.Verdict.ALMOST,
        )
    }
}

class CompleteLessonUseCase(
    private val lessons: LessonRepository,
    private val progress: ProgressRepository,
) {
    suspend operator fun invoke(lessonId: String, now: Long) {
        lessons.completeLesson(lessonId, now)
        lessons.enqueueLessonVocabulary(lessonId, now)
        lessons.saveLessonIndex(lessonId, 0)
    }
}

/** Grades an SRS review and persists the next schedule. */
class SubmitReviewUseCase(
    private val reviews: ReviewRepository,
    private val scheduler: ReviewScheduler,
    private val progress: ProgressRepository,
) {
    suspend operator fun invoke(
        item: ReviewItem,
        result: ReviewResult,
        now: Long,
        responseTimeMs: Long,
    ) {
        val schedule = scheduler.schedule(item, result, now)
        reviews.schedule(
            item.copy(
                state = schedule.state,
                dueAt = schedule.nextReviewAt,
                intervalDays = schedule.intervalDays,
                easeFactor = scheduler.nextEase(item.easeFactor, result),
                repetitions = if (result == ReviewResult.AGAIN) item.repetitions else item.repetitions + 1,
                lapses = if (result == ReviewResult.AGAIN) item.lapses + 1 else item.lapses,
                lastReviewedAt = now,
            ),
        )
        reviews.recordAttempt(
            ReviewAttempt(
                contentId = item.contentId,
                timestamp = now,
                result = result,
                responseTimeMs = responseTimeMs,
                source = "review",
            ),
        )
        progress.applyAttempt(
            skill = if (item.contentType == ReviewContentType.VOCABULARY) Skill.VOCABULARY else Skill.GRAMMAR,
            correct = result != ReviewResult.AGAIN,
            source = "review",
        )
    }
}

class GetTodayPlanUseCase(
    private val lessons: LessonRepository,
    private val reviews: ReviewRepository,
    private val progress: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val planner: LearningPlanner,
    private val knowledge: KnowledgeRepository,
) {
    suspend operator fun invoke(now: Long): TodayPlan {
        val settings: AppSettings = settingsRepository.settings.first()
        val due = reviews.countDue(now)
        val allLessons: List<Lesson> = lessons.observeLessons().first()
        val states: List<LessonState> = lessons.observeLessonStates().first()
        val mastery: Map<Skill, Float> = progress.observeMastery().first()
        val stats: StudyStats = progress.observeStats().first()

        val completedIds = states.filter { it.completed }.map { it.lessonId }.toSet()
        val nextLesson = org.token.english.domain.engine.nextLessonFor(allLessons, completedIds, settings.level)

        val base = planner.createPlan(
            dueReviewCount = due,
            nextLesson = nextLesson,
            masteryBySkill = mastery,
            targetMinutes = settings.dailyGoalMinutes,
            todayStudySeconds = stats.todayStudySeconds,
        )

        // Adaptive slice (checklist B-1): the knowledge engine turns the day into an
        // ordered, justified list of actions — review first, then remediation of an
        // unmet prerequisite, then a new unit, then practice. The graph is rebuilt
        // from the seeded items here (83 nodes) so the use case stays pure and the
        // planner keeps its no-Android, no-DB contract.
        val items = knowledge.allItems()
        val focusItems = nextLesson?.let { knowledge.itemsForLesson(it.id) }.orEmpty()
        val minutesByItem = focusItems.associate { item ->
            val lesson = allLessons.firstOrNull { it.id in item.lessonIds }
            item.id to (lesson?.estimatedMinutes ?: org.token.english.domain.engine.AdaptiveLearningPlanner.DEFAULT_ITEM_MINUTES)
        }
        val decision = if (items.isEmpty() || focusItems.isEmpty() && due == 0) {
            null
        } else {
            val graph = org.token.english.domain.engine.DefaultKnowledgeGraph(items)
            org.token.english.domain.engine.AdaptiveLearningPlanner(
                graph,
                org.token.english.domain.engine.PrerequisiteEngine(graph),
            ).plan(
                states = knowledge.observeStates().first(),
                dueReviewCount = due,
                focusItems = focusItems,
                targetMinutes = settings.dailyGoalMinutes,
                todayStudySeconds = stats.todayStudySeconds,
                estimatedMinutesByItem = minutesByItem,
            )
        }

        return base.copy(actions = decision?.actions.orEmpty())
    }
}

/**
 * Placement test scoring (methodology spec §18): the test ships as six CEFR
 * bands of [DEFAULT_BAND_SIZE] questions each (A1 → C2, ordered by difficulty
 * in placement.json). The test ends early once the current band is unwinnable
 * (PlacementViewModel adaptive stop) — a short final chunk is fine: it can
 * never reach the pass threshold after enough wrong answers.
 *
 * Walks bands from the bottom up and stops at the first band the learner fails.
 * A band passes when at least 2/3 of it is correct AND cumulative accuracy
 * through that band is at least 60% — so one lucky guess can't jump a level.
 * Returns the highest passed band as the estimated level (floor: A1).
 */
class ScorePlacementUseCase {
    operator fun invoke(results: List<Boolean>, bandSize: Int = DEFAULT_BAND_SIZE): LearningLevel {
        if (results.isEmpty()) return LearningLevel.A1
        var best = LearningLevel.A1
        var cumulativeCorrect = 0
        var cumulativeTotal = 0
        for ((index, band) in results.chunked(bandSize).withIndex()) {
            val bandCorrect = band.count { it }
            cumulativeCorrect += bandCorrect
            cumulativeTotal += band.size
            val level = LearningLevel.entries.getOrNull(index) ?: break
            val bandPassed = bandCorrect * 3 >= band.size * 2
            val cumulativePassed = cumulativeCorrect * 5 >= cumulativeTotal * 3
            if (bandPassed && cumulativePassed) {
                best = level
            } else {
                break
            }
        }
        return best
    }

    companion object {
        /** Questions per CEFR band in placement.json (6 bands × 5 = 30 total). */
        const val DEFAULT_BAND_SIZE = 5
    }
}
