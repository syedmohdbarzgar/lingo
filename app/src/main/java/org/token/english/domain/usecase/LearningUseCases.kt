package org.token.english.domain.usecase

import kotlinx.coroutines.flow.first
import org.token.english.domain.engine.AdaptiveLearningPlanner
import org.token.english.domain.engine.DefaultKnowledgeGraph
import org.token.english.domain.engine.ExerciseDimension
import org.token.english.domain.engine.KnowledgeGates
import org.token.english.domain.engine.LearningPlanner
import org.token.english.domain.engine.PrerequisiteEngine
import org.token.english.domain.engine.RemediationEngine
import org.token.english.domain.engine.STRONG_MASTERY
import org.token.english.domain.engine.nextLessonFor
import org.token.english.domain.engine.PlacementAnswer
import org.token.english.domain.engine.PlacementAssessment
import org.token.english.domain.engine.PlacementAssessmentEngine
import org.token.english.domain.engine.ReviewScheduler
import org.token.english.domain.engine.SkillEvidence
import org.token.english.domain.engine.SpeakingVerdict
import org.token.english.domain.engine.SkillProfile
import org.token.english.domain.engine.SkillProfileEngine
import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.AppSettings
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.ExerciseFormat
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.LessonState
import org.token.english.domain.model.ReviewAttempt
import org.token.english.domain.model.ReviewItem
import org.token.english.domain.model.ReviewResult
import org.token.english.domain.model.Skill
import org.token.english.domain.model.skill
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
        val format = AnswerChecker.formatOf(exercise)
        // One answer per exercise per day is evidence; a retry of the same item is a
        // tap. The claim comes first, so a repeated answer writes nothing at all — not
        // the skill meter, not the graph — and the two can never disagree about what was
        // learned. A failed claim still advances the lesson cursor; only the model is
        // left alone.
        if (progress.claimAnswer(exercise.lessonId, exercise.id, correct, now)) {
            // The same answer is also evidence about the curriculum nodes the lesson
            // teaches (audit §4/§19) — this is what turns exercise results into a
            // learner model instead of a score. A lesson the graph does not know (the
            // placement test) simply yields no nodes.
            knowledge.recordAttempt(
                lessonId = exercise.lessonId,
                skill = skill,
                format = format,
                correct = correct,
                now = now,
            )
            progress.applyAttempt(skill, correct, source = "lesson")
            // A written sentence is writing evidence *as well as* evidence about what
            // it was about: the item's `skill` says which knowledge the answer tests,
            // the format says how the learner produced it. Crediting only the tagged
            // skill left WRITING with four items in the whole bundle and no way to
            // reach it, while 156 authored productions went to vocabulary.
            if (format == ExerciseFormat.SENTENCE_PRODUCTION && skill != Skill.WRITING) {
                progress.applyAttempt(Skill.WRITING, correct, source = "lesson-writing")
            }
        }
        return ExerciseOutcome(
            correct = correct,
            correctAnswer = AnswerChecker.correctAnswerText(exercise),
            skill = skill,
            almostCorrect = verdict == AnswerChecker.Verdict.ALMOST,
        )
    }
}

/**
 * One graded conversation turn, as evidence in its own track.
 *
 * The dialogue is the only place the learner *produces* language and gets a verdict
 * on it, and it used to leave no trace at all: the conversation screen graded every
 * turn and wrote nothing, so Speaking could never be shown to move and the planner
 * kept excluding it. Splitting the evidence is the point of this class — *how* the
 * answer arrived decides *which* track it feeds:
 *
 * - spoken, graded by the recogniser → [Skill.SPEAKING];
 * - typed on the keyboard → [Skill.WRITING]. A written sentence is writing evidence,
 *   and the turn is still the same query, the same scorer and the same threshold.
 *
 * A [SpeakingVerdict.RETRY] writes nothing on purpose. The app cannot tell a wrong
 * answer from a recogniser error, and the screen already refuses to accuse the
 * learner of one; turning that doubt into a mastery penalty (or a broken streak)
 * would be the same accusation, only harder to see. [SpeakingVerdict.NEAR] counts as
 * a hit: the scorer accepts key-word coverage, and "the meaning came across" is what
 * this track measures.
 */
class RecordConversationTurnUseCase(
    private val progress: ProgressRepository,
    private val knowledge: KnowledgeRepository,
) {
    /** @return whether the turn counted as evidence (see [ProgressRepository.claimAnswer]). */
    suspend operator fun invoke(
        lessonId: String?,
        exerciseId: String,
        verdict: SpeakingVerdict,
        spoken: Boolean,
        now: Long,
    ): Boolean {
        if (verdict == SpeakingVerdict.RETRY) return false
        val skill = if (spoken) Skill.SPEAKING else Skill.WRITING
        val format = if (spoken) ExerciseFormat.SPEAKING else ExerciseFormat.SENTENCE_PRODUCTION
        // The turn *is* an exercise: claiming it by id is what stops a re-played
        // dialogue from raising the speaking bar six times for six taps.
        if (!progress.claimAnswer(lessonId.orEmpty(), exerciseId, correct = true, now)) return false
        // A null lesson (the practice opened before the bundle landed) has no dialogue
        // graph, so only the skill track is credited; the repository ignores an unknown
        // lesson rather than inventing nodes for it.
        knowledge.recordAttempt(
            lessonId = lessonId.orEmpty(),
            skill = skill,
            format = format,
            correct = true,
            now = now,
        )
        progress.applyAttempt(skill, correct = true, source = if (spoken) "conversation" else "conversation-typed")
        return true
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
            // One definition of the mapping (domain/model): the code that *counts*
            // review evidence into a skill track reads the same helper, so the grade
            // and the track it credits can never disagree.
            skill = item.contentType.skill(),
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
        val nextLesson = nextLessonFor(allLessons, completedIds, settings.level)

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
            item.id to (lesson?.estimatedMinutes ?: AdaptiveLearningPlanner.DEFAULT_ITEM_MINUTES)
        }
        val decision = if (items.isEmpty() || focusItems.isEmpty() && due == 0) {
            null
        } else {
            val graph = DefaultKnowledgeGraph(items)
            AdaptiveLearningPlanner(
                graph,
                PrerequisiteEngine(graph),
            ).plan(
                states = knowledge.observeStates().first(),
                dueReviewCount = due,
                focusItems = focusItems,
                targetMinutes = settings.dailyGoalMinutes,
                todayStudySeconds = stats.todayStudySeconds,
                now = now,
                estimatedMinutesByItem = minutesByItem,
            )
        }

        return base.copy(actions = decision?.actions.orEmpty())
    }
}

/**
 * The engine's decision for one focused practice block (checklist B-1): which
 * curriculum node to work on, why it was picked, which of the lesson's exercises
 * actually provide evidence about it, and what the block has to reach to count as
 * fixed.
 *
 * The surface only renders this — the node, the subset and the reason are all
 * decided here (technical spec §19).
 */
data class FocusPlan(
    val itemId: String,
    val titleFa: String,
    /** The lesson that teaches the node — where the focused session runs. */
    val lessonId: String,
    /** Persian "why this node, why now", authored by the remediation engine. */
    val reasonFa: String,
    /** The node's mastery now (0..1). */
    val mastery: Float,
    /**
     * Ids of the exercises in [lessonId] that give evidence about the node. A
     * focused session shows only these, so a grammar weakness is not padded with
     * the lesson's vocabulary drills.
     */
    val exerciseIds: Set<String>,
    /** Mastery after which the node counts as consolidated. */
    val reassessThreshold: Float,
    /** Attempts the block is allowed before the verdict escalates. */
    val remainingAttempts: Int,
    /** Nodes that stay locked until this one is fixed — why it is worth doing now. */
    val blockedCount: Int,
)

/**
 * Asks the remediation engine for the learner's next focused block (checklist
 * B-1). With no [itemId] the engine isolates the weakest node that has evidence
 * (error isolation, not "the whole subject"); with an [itemId] it plans for the
 * node the daily plan already pointed at.
 *
 * Pure decision: the seeded graph, the learner's knowledge states and the lessons'
 * exercises go in; a [FocusPlan] (or `null` when there is nothing worth drilling)
 * comes out. Nothing here knows about screens.
 */
class GetFocusPlanUseCase(
    private val knowledge: KnowledgeRepository,
    private val lessons: LessonRepository,
    private val weaknessThreshold: Float = RemediationEngine.DEFAULT_WEAKNESS_THRESHOLD,
) {
    suspend operator fun invoke(itemId: String? = null): FocusPlan? {
        val items = knowledge.allItems()
        if (items.isEmpty()) return null
        val graph = DefaultKnowledgeGraph(items)
        val engine = RemediationEngine(graph, weaknessThreshold = weaknessThreshold)
        val states = knowledge.observeStates().first().associateBy { it.itemId }

        // Each lesson's exercises are fetched at most once per call.
        val loaded = mutableMapOf<String, List<Exercise>>()
        suspend fun exercisesOf(lessonId: String): List<Exercise> =
            loaded[lessonId] ?: lessons.getExercises(lessonId).also { loaded[lessonId] = it }

        val targetId = itemId ?: run {
            // The weakest node is only meaningful among nodes that could be weak.
            val candidates = graph.items.filter { item ->
                val state = states[item.id]
                state == null || state.mastery < weaknessThreshold
            }
            if (candidates.isEmpty()) return null
            candidates.flatMap { it.lessonIds }.distinct().forEach { exercisesOf(it) }
            engine.findWeakness(states, loaded)?.itemId ?: return null
        }
        val item = graph.byId(targetId) ?: return null
        val state = states[targetId] ?: return null
        val mastery = state.mastery
        // A node the learner has never studied is *taught*, not drilled — and one the
        // gate calls secured has nothing left to fix, so the auto-pick never chooses
        // either (checklist B-1). An **explicit** request is different: the daily plan
        // sends the learner here with a RETEST action for exactly the node the gate has
        // not yet secured, and that session is a short retest of it (chapter 1) —
        // refusing it would drop the learner into the whole lesson instead. A node that
        // is merely "not weak" (0.5..0.85) is still not a retest: it has not earned the
        // strong claim the retest is testing, so it keeps the old "nothing to drill"
        // answer.
        val retest = itemId != null &&
            mastery >= STRONG_MASTERY &&
            !KnowledgeGates.isSecured(state)
        if (mastery >= weaknessThreshold && !retest) return null
        item.lessonIds.forEach { exercisesOf(it) }

        val plan = engine.plan(targetId, loaded) ?: return null
        val plannedIds = plan.exercises.map { it.id }.toSet()
        // The session runs in one lesson: the first of the node's lessons that the
        // engine has evidence-bearing exercises for.
        val lessonId = item.lessonIds.firstOrNull { lesson ->
            loaded[lesson].orEmpty().any { it.id in plannedIds }
        } ?: return null

        return FocusPlan(
            itemId = item.id,
            titleFa = item.titleFa,
            lessonId = lessonId,
            // A retest says what it is: the learner already knows this node and is
            // here to prove it survived a delay, not to be told it is a weakness.
            reasonFa = if (retest) {
                "بازآزمایی — بعد از یک فاصله دوباره بسنجیم"
            } else {
                plan.reasonFa
            },
            mastery = mastery,
            exerciseIds = loaded[lessonId].orEmpty().map { it.id }.filter { it in plannedIds }.toSet(),
            reassessThreshold = plan.reassessThreshold,
            remainingAttempts = plan.remainingAttempts,
            blockedCount = graph.items.count { item.id in it.prerequisites },
        )
    }
}

/**
 * The learner's independent skill tracks (checklist v8 P2): Grammar, Vocabulary and
 * Reading, each with the mastery the rest of the app already uses **plus** the
 * evidence behind it.
 *
 * Both screens need this, so it lives here rather than being re-derived in two
 * ViewModels — and it is the only place that combines the two evidence sources: the
 * curriculum graph (every lesson answer is attributed to the nodes it teaches) and
 * the SRS review log (practice that never touches the graph). Mastery is read, never
 * recomputed, so a track can never disagree with the planner about how well the
 * learner knows it.
 */
class GetSkillProfilesUseCase(
    private val knowledge: KnowledgeRepository,
    private val progress: ProgressRepository,
    private val reviews: ReviewRepository,
    private val engine: SkillProfileEngine = SkillProfileEngine(),
) {
    suspend operator fun invoke(): List<SkillProfile> {
        val items = knowledge.allItems()
        // No content (before seeding finishes, or a broken install) means no tracks:
        // three zeroed bars would claim the learner has done nothing when in fact
        // there is nothing to do yet. The screen hides the card on an empty list.
        if (items.isEmpty()) return emptyList()
        val states = knowledge.observeStates().first()
        val mastery = progress.observeMastery().first()
        // Review grades are credited to the skill the reviewed content type exercises
        // — the same mapping that moved the mastery (see `ReviewContentType.skill`),
        // so a track's number and its evidence cannot tell different stories.
        val reviewEvidence = reviews.attemptStatsByType()
            .mapKeys { (type, _) -> type.skill() }
            .mapValues { (_, stats) -> SkillEvidence(attempts = stats.attempts, misses = stats.misses) }
        return engine.profiles(items, states, mastery, reviewEvidence)
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
        /**
         * Questions per CEFR band in placement.json (6 bands × 6 = 36 total).
         * Each band carries one reading-comprehension item alongside the grammar
         * and vocabulary ones, so the per-skill report measures reading too (A-6).
         */
        const val DEFAULT_BAND_SIZE = 6
    }
}

/**
 * Scores a finished placement test as a *skill assessment* (P1-1), not just a
 * single CEFR level: returns the overall estimate plus a per-skill read from
 * [PlacementAssessmentEngine], and calibrates skill mastery from the very same
 * answers so the learner's profile starts informed instead of blank.
 *
 * Mastery is seeded by replaying each answer through [ProgressRepository] — never
 * by writing mastery directly — so the EWMA stays the single source of truth and a
 * correct placement answer means the same thing as a correct lesson answer.
 */
class AssessPlacementUseCase(
    private val progress: ProgressRepository,
    private val engine: PlacementAssessmentEngine = PlacementAssessmentEngine(),
) {
    suspend operator fun invoke(answers: List<PlacementAnswer>): PlacementAssessment {
        answers.forEach { progress.applyAttempt(it.skill, it.correct, source = "placement") }
        return engine(answers)
    }
}
