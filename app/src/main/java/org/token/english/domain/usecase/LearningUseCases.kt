package org.token.english.domain.usecase

import kotlinx.coroutines.flow.first
import org.token.english.core.common.runCatchingCancellable
import org.token.english.domain.engine.AdaptiveLearningPlanner
import org.token.english.domain.engine.DefaultKnowledgeGraph
import org.token.english.domain.engine.DifficultyAudit
import org.token.english.domain.engine.DifficultyAuditReport
import org.token.english.domain.engine.ExerciseDimension
import org.token.english.domain.engine.KnowledgeGates
import org.token.english.domain.engine.LearningPlanner
import org.token.english.domain.engine.PrerequisiteEngine
import org.token.english.domain.engine.RemediationEngine
import org.token.english.domain.engine.STRONG_MASTERY
import org.token.english.domain.engine.nextLessonFor
import org.token.english.domain.engine.MissCategory
import org.token.english.domain.engine.MissProfile
import org.token.english.domain.engine.MissTaxonomy
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
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.LessonState
import org.token.english.domain.model.ReviewAttempt
import org.token.english.domain.model.ReviewItem
import org.token.english.domain.model.ReviewResult
import org.token.english.domain.model.Skill
import org.token.english.domain.model.skill
import org.token.english.domain.model.StudyStats
import org.token.english.domain.model.TodayPlan
import org.token.english.domain.repository.ConversationRepository
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
    /**
     * The verdict for one answer, with no writes at all.
     *
     * Split out because the answer and its evidence are written in one transaction
     * (checklist 0.1): when that transaction cannot be written, the learner still
     * gets a verdict — an accurate grade with no evidence is honest, a tap that
     * does nothing is not.
     */
    fun grade(exercise: Exercise, answer: String): ExerciseOutcome {
        val verdict = AnswerChecker.grade(exercise, answer)
        return ExerciseOutcome(
            correct = verdict == AnswerChecker.Verdict.CORRECT,
            correctAnswer = AnswerChecker.correctAnswerText(exercise),
            skill = AnswerChecker.skillOf(exercise),
            almostCorrect = verdict == AnswerChecker.Verdict.ALMOST,
        )
    }

    suspend operator fun invoke(
        exercise: Exercise,
        answer: String,
        now: Long,
    ): ExerciseOutcome {
        val outcome = grade(exercise, answer)
        val correct = outcome.correct
        val skill = outcome.skill
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
        return outcome
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
    private val conversations: ConversationRepository,
) {
    /**
     * @param completesDialogue true when this turn is the dialogue's last graded one:
     *   the "finished" mark is then written in this turn's own transaction (see below).
     * @return whether the turn counted as evidence (see [ProgressRepository.claimAnswer]).
     */
    suspend operator fun invoke(
        lessonId: String?,
        exerciseId: String,
        verdict: SpeakingVerdict,
        spoken: Boolean,
        now: Long,
        completesDialogue: Boolean = false,
    ): Boolean {
        // A retry is not a finished turn: the learner was asked to say it again, so
        // the dialogue is not closed by it and nothing is written.
        if (verdict == SpeakingVerdict.RETRY) return false
        val skill = if (spoken) Skill.SPEAKING else Skill.WRITING
        val format = if (spoken) ExerciseFormat.SPEAKING else ExerciseFormat.SENTENCE_PRODUCTION
        // The turn's whole fact — the claim, the graph and the skill track — is ONE
        // transaction (checklist 0.1). Written separately, a crash after the claim left
        // the turn spent for the day while Speaking never moved: the dialogue would have
        // been answered and silently credited to nobody.
        return progress.inTransaction {
            // The mark belongs to this transaction: written after it, a crash in
            // between reopened a dialogue whose every turn was already credited (and
            // the learner had to sit through it again); written before it, the app
            // could open the next lesson for a dialogue whose last answer never
            // landed. Either both facts stand or neither does.
            // It is written before the claim can return early on purpose: a dialogue
            // replayed on a day its turns are already claimed is still finished, and
            // the mark is `INSERT OR IGNORE`, so a repeat is free.
            if (completesDialogue) conversations.markCompleted(lessonId.orEmpty())
            // The turn *is* an exercise: claiming it by id is what stops a re-played
            // dialogue from raising the speaking bar six times for six taps.
            if (!progress.claimAnswer(lessonId.orEmpty(), exerciseId, correct = true, now)) {
                return@inTransaction false
            }
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
            progress.applyAttempt(
                skill,
                correct = true,
                source = if (spoken) "conversation" else "conversation-typed",
            )
            true
        }
    }
}

class CompleteLessonUseCase(
    private val lessons: LessonRepository,
    private val progress: ProgressRepository,
) {
    suspend operator fun invoke(lessonId: String, now: Long) {
        // Finishing a lesson is three rows — the completion stamp, its vocabulary
        // entering the review queue and the cursor going back to the start — and they
        // are one transaction (checklist 0.1). Written separately, a crash in the middle
        // could leave a lesson marked done whose words never reached review (the learner
        // would never be asked them again), or a queue that grew for a lesson the app
        // does not consider finished.
        progress.inTransaction {
            lessons.completeLesson(lessonId, now)
            lessons.enqueueLessonVocabulary(lessonId, now)
            lessons.saveLessonIndex(lessonId, 0)
        }
    }
}

/** Grades an SRS review and persists the next schedule. */
class SubmitReviewUseCase(
    private val reviews: ReviewRepository,
    private val scheduler: ReviewScheduler,
    private val progress: ProgressRepository,
    private val knowledge: KnowledgeRepository,
) {
    /**
     * @param lessonId the lesson that taught the reviewed content — how the grade
     *   reaches the curriculum node (see [KnowledgeRepository.recordRecall]). Null when
     *   the card cannot be placed; the card is still graded, the graph simply has
     *   nothing to attribute it to.
     */
    suspend operator fun invoke(
        item: ReviewItem,
        result: ReviewResult,
        now: Long,
        responseTimeMs: Long,
        lessonId: String?,
    ) {
        // One grade writes four rows — the card's next schedule, the attempt log, the
        // skill meter and the curriculum node — and they are ONE transaction
        // (checklist 0.1). Written separately, a crash between them could store a new
        // interval with no attempt behind it, move the skill and leave the card due (so
        // the same card is asked again and again until a schedule finally lands), or
        // credit a node for a review the log has no grade for.
        val schedule = scheduler.schedule(item, result, now)
        progress.inTransaction {
            reviews.schedule(
                item.copy(
                    state = schedule.state,
                    dueAt = schedule.nextReviewAt,
                    intervalDays = schedule.intervalDays,
                    easeFactor = scheduler.nextEase(item.easeFactor, result),
                    repetitions = if (result == ReviewResult.AGAIN) {
                        item.repetitions
                    } else {
                        item.repetitions + 1
                    },
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
            // The graph last, so the learner model and the cards never disagree: before
            // this, review was practice the curriculum could not see at all.
            knowledge.recordRecall(
                lessonId = lessonId,
                skill = item.contentType.skill(),
                correct = result != ReviewResult.AGAIN,
                now = now,
            )
        }
    }
}

class GetTodayPlanUseCase(
    private val lessons: LessonRepository,
    private val reviews: ReviewRepository,
    private val progress: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val planner: LearningPlanner,
    private val knowledge: KnowledgeRepository,
    /** The recent miss mix, which used to be reported and never used (roadmap). */
    private val missProfile: GetMissProfileUseCase,
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
        // The mix is a nudge, not an input the day depends on. A profile read that
        // fails (an unreadable log, a lesson whose exercises are gone) leaves the
        // graph-driven plan exactly as it was; it must not be able to turn Home into an
        // error screen over a hint. Same rule the Progress screen uses for its half.
        val missMix = runCatchingCancellable { missProfile() }.getOrNull()
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
                missProfile = missMix,
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
    /**
     * @param now the clock, used for exactly one question: whether an explicitly
     *   requested node's own schedule is **due** (see the retest/review branch below).
     *   No default — a hidden clock would decide that question for the caller.
     */
    suspend operator fun invoke(itemId: String? = null, now: Long): FocusPlan? {
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
            // The weakest node is only meaningful among nodes that could be weak *and
            // that the learner has actually met*. Both halves matter:
            //
            // - `findWeakness` needs a stored state (a node never studied is taught,
            //   not drilled), so stateless nodes can never be chosen;
            // - their lessons must therefore not be loaded either. This used to load
            //   the exercises of *every* candidate lesson before asking the engine,
            //   and on a fresh install every node is a candidate — so Home parsed the
            //   JSON of all 192 lessons' exercises on the main thread to produce an
            //   answer that was guaranteed to be null. On a mature account it loaded
            //   the course again for a single weak node. Now the read is proportional
            //   to what the learner has answered, not to the size of the bundle.
            val candidates = graph.items.filter { item ->
                val state = states[item.id] ?: return@filter false
                state.mastery < weaknessThreshold
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
        // either (checklist B-1). An **explicit** request is different, for two reasons
        // that are both about the caller having decided already:
        //
        // - the daily plan sends the learner here with a RETEST action for exactly the
        //   node the gate has not yet secured, and that session is a short retest of it
        //   (chapter 1) — refusing it would drop the learner into the whole lesson;
        // - the review screen sends the learner here for a node whose **own schedule is
        //   due** (review→graph pass). That node can sit in the middle band (0.5..0.85)
        //   where it is neither weak enough to auto-pick nor strong enough to retest, and
        //   refusing it would leave the due list with entries that open a whole lesson
        //   instead of the node's exercises — the exact fallback this change exists to
        //   remove.
        //
        // A due-but-secured node is still refused: the gate's "needs no more practice
        // today" stays the one bar (the long-term claim is what covers its retention).
        val secured = KnowledgeGates.isSecured(state)
        val retest = itemId != null && mastery >= STRONG_MASTERY && !secured
        val dueReview = itemId != null && !secured && KnowledgeGates.isDue(state, now)
        if (mastery >= weaknessThreshold && !retest && !dueReview) return null
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
 * One curriculum node whose **own** review is due, with the block that serves it.
 *
 * [lessonId] and [exerciseIds] come from the focused drill, not from a guess here, so
 * the review screen can only offer rows the lesson screen will actually run as a drill.
 */
data class DueKnowledgeReview(
    val itemId: String,
    val titleFa: String,
    val lessonId: String,
    /** The node's `nextReviewAt` — how long it has been owed. */
    val dueAt: Long,
    val mastery: Float,
    /** Persian "why this node, why now", written by the remediation engine. */
    val reasonFa: String,
)

/**
 * The other direction of the review queue (review→graph pass): which **knowledge
 * nodes** are due, not which cards.
 *
 * The card queue is words; a node's schedule is the curriculum's own. Until this read
 * existed, `nextReviewAt` on a node was written by lessons and reviews and read by
 * nothing — a date that scheduled work and never served it. Today's answer is the
 * focused drill that already exists (`GetFocusPlanUseCase` → the lesson screen with
 * `focusItemId`), so this use case does not invent a new exercise surface: it finds the
 * due nodes and asks that use case for the block, keeping exactly the nodes it can serve.
 *
 * Two deliberate filters:
 *
 * - **Not secured.** [KnowledgeGates.isSecured] is the one "needs no more practice
 *   today" bar; a secured node's due date is covered by its long-term claim, and
 *   offering it would let the review screen contradict the planner.
 * - **A real drill only.** A due node the focused drill refuses (no evidence-bearing
 *   exercise in any of its lessons) is dropped rather than offered as a row that opens a
 *   whole lesson instead.
 *
 * The probes are bounded: only the most overdue [MAX_PROBES] nodes are asked, and the
 * result stops at [limit]. Each probe re-reads the graph through the focus use case, so
 * the bound is what keeps this a screen-local read rather than a scan of the bundle.
 *
 * **The miss mix steers ties, never urgency (miss-driven drill pass).** Two nodes owed
 * since the same midnight are equally urgent, and of those the one training the
 * category the learner keeps missing is served first — the same rule, and the same
 * `… · ضعف اخیر: …` label, that `AdaptiveLearningPlanner` applies inside one plan
 * priority. A node owed for longer still comes first: a miss count is one window of
 * evidence, while an overdue interval is what retention actually pays for. Nothing is
 * invented or dropped either way — the same due nodes are served, in a different order
 * among equals.
 */
class GetDueKnowledgeReviewsUseCase(
    private val knowledge: KnowledgeRepository,
    private val focus: GetFocusPlanUseCase,
    /**
     * The recent miss mix, optional on purpose: the list is a queue of retention first,
     * and a caller with no profile seam still gets the overdue order. The same "a hint,
     * never a dependency" rule the day's plan follows — a read that throws steers
     * nothing instead of failing the screen.
     *
     * A lambda rather than the use case itself: the only thing this list needs is the
     * mix, and a caller (or a test) that has none should not have to build a log reader
     * to say so.
     */
    private val missProfile: suspend () -> MissProfile? = { null },
) {
    suspend operator fun invoke(
        now: Long,
        limit: Int = DEFAULT_LIMIT,
    ): List<DueKnowledgeReview> {
        // A clean window is not a weakness: "weakest" of five right answers steers nothing.
        // The rule itself lives in `MissTaxonomy`, where the day's plan asks the same
        // question — one definition, so the two surfaces cannot start pointing at different
        // gaps (miss-driven drill pass).
        val weakest = MissTaxonomy.weakestCategory(
            runCatchingCancellable { missProfile() }.getOrNull(),
        )
        val missRank: Map<String, Int> = if (weakest == null) {
            emptyMap()
        } else {
            MissTaxonomy.rank(knowledge.allItems(), weakest)
        }

        val candidates = knowledge.observeStates().first()
            .filter { state ->
                KnowledgeGates.isDue(state, now) && !KnowledgeGates.isSecured(state)
            }
            .sortedWith(
                compareBy<KnowledgeState> { it.nextReviewAt }
                    .thenBy { state -> missRank[state.itemId] ?: 1 }
                    .thenBy { it.itemId },
            )
            .take(MAX_PROBES)

        val reviews = mutableListOf<DueKnowledgeReview>()
        for (state in candidates) {
            if (reviews.size >= limit) break
            val plan = focus(state.itemId, now) ?: continue
            reviews += DueKnowledgeReview(
                itemId = plan.itemId,
                titleFa = plan.titleFa,
                lessonId = plan.lessonId,
                dueAt = state.nextReviewAt,
                mastery = plan.mastery,
                reasonFa = plan.reasonFa,
            )
        }
        return if (weakest == null) reviews else nameGap(reviews, weakest, missRank)
    }

    /**
     * Names the gap once, on the first row whose node trains it — a promotion the screen
     * cannot explain is worse than no promotion, so the order and its reason ship
     * together (the plan's own rule). No promoted row, no label.
     */
    private fun nameGap(
        reviews: List<DueKnowledgeReview>,
        weakest: MissCategory,
        missRank: Map<String, Int>,
    ): List<DueKnowledgeReview> {
        val at = reviews.indexOfFirst { missRank[it.itemId] == 0 }
        if (at < 0) return reviews
        val suffix = MissTaxonomy.gapSuffix(weakest)
        return reviews.mapIndexed { index, review ->
            if (index == at) review.copy(reasonFa = review.reasonFa + suffix) else review
        }
    }

    companion object {
        /** Rows the review screen shows: a short block, not another wall of work. */
        const val DEFAULT_LIMIT = 3

        /** How many of the most overdue nodes are asked for a drill before giving up. */
        const val MAX_PROBES = 8
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
        // The whole calibration is one transaction: every answer seeds its skill's
        // meter through the EWMA, so a crash half-way used to leave a profile seeded
        // from some of the test and silently missing the rest — the learner would look
        // weaker (or stronger) than the test they actually took. Either the test
        // calibrates the profile or it changes nothing (checklist 0.1).
        progress.inTransaction {
            answers.forEach { progress.applyAttempt(it.skill, it.correct, source = "placement") }
        }
        return engine(answers)
    }
}

/**
 * The error taxonomy's read (roadmap): what the learner's recent answers went wrong
 * *about*, classified by the skill each exercise is authored for.
 *
 * The log stores which exercise was asked, not its skill, so the authored exercises are
 * read here and joined on id. Two deliberate narrowings:
 *
 * - **A window, not the archive** ([MissTaxonomy.WINDOW] newest answers), because the
 *   point is what the learner should fix *now*. Both sources are pooled and then the
 *   newest [window] are kept, so a burst of review cannot crowd the lessons out of the
 *   window (or the other way round) the way two separate windows would.
 * - **Cards are labelled by content type.** A recall card knows it is a word or a rule
 *   and nothing finer, so review can only ever land in [MissCategory.VOCABULARY] or
 *   [MissCategory.GRAMMAR] — never comprehension or production. That limit is in
 *   `docs/debt.md`.
 *
 * Exercises are cached per lesson for the life of the process: content is static once
 * seeded, and this read runs on every mastery emission — without the cache a single
 * answer would re-parse the bundle's exercises for every lesson in the window.
 */
class GetMissProfileUseCase(
    private val lessons: LessonRepository,
    private val reviews: ReviewRepository,
) {
    private val exercisesByLesson = mutableMapOf<String, Map<String, Exercise>>()

    suspend operator fun invoke(window: Int = MissTaxonomy.WINDOW): MissProfile? {
        val attempts = lessons.recentAttempts(window)
        val cards = reviews.recentCards(window)
        if (attempts.isEmpty() && cards.isEmpty()) return null
        // A bundle with hundreds of lessons could not all be cached at once; the window
        // only ever names a handful, so the cap is a guard against a long-lived process
        // accumulating lessons, not a normal exit.
        if (exercisesByLesson.size > MAX_CACHED_LESSONS) exercisesByLesson.clear()
        val byId = mutableMapOf<String, Exercise>()
        attempts.map { it.lessonId }.distinct().forEach { lessonId ->
            val exercises = exercisesByLesson[lessonId] ?: lessons.getExercises(lessonId)
                .associateBy { it.id }
                .also { exercisesByLesson[lessonId] = it }
            byId += exercises
        }
        val pooled = MissTaxonomy.classify(attempts, byId) + MissTaxonomy.classify(cards)
        // Newest-first over the merged set, so the window is one window and not two.
        return MissTaxonomy.profile(pooled.sortedByDescending { it.at }.take(window))
    }

    private companion object {
        const val MAX_CACHED_LESSONS = 64
    }
}

/**
 * Checks the authored `difficulty` against the answers the log already holds (roadmap
 * P1): per exercise, how often it was right, how long it took, and on how many days.
 *
 * Nothing new is recorded — `lesson_attempt` has stored the exercise, the verdict and the
 * time of every answer since v9 — so this is a **read** that joins the log to the authored
 * exercises (for `difficulty`) and to the lessons (for the level the per-level fold needs).
 *
 * The window is deliberately larger than the miss taxonomy's: eight answers per item is
 * the audit's own bar, so a session-sized window would leave almost every item
 * `INSUFFICIENT` forever. [DifficultyAudit.MIN_ATTEMPTS] and its day rule are what keep the
 * larger window from turning into a verdict on thin evidence.
 *
 * Exercises are cached per lesson for the process, exactly as the miss profile does:
 * content is static once seeded, and this read is not on a hot path but does walk the
 * bundle's exercises per reading lesson.
 */
class GetDifficultyAuditUseCase(
    private val lessons: LessonRepository,
) {
    private val exercisesByLesson = mutableMapOf<String, Map<String, Exercise>>()

    suspend operator fun invoke(window: Int = WINDOW): DifficultyAuditReport {
        val attempts = lessons.recentAttempts(window)
        if (attempts.isEmpty()) return DifficultyAuditReport(emptyList(), emptyList())
        if (exercisesByLesson.size > MAX_CACHED_LESSONS) exercisesByLesson.clear()
        val byId = mutableMapOf<String, Exercise>()
        attempts.map { it.lessonId }.distinct().forEach { lessonId ->
            val exercises = exercisesByLesson[lessonId] ?: lessons.getExercises(lessonId)
                .associateBy { it.id }
                .also { exercisesByLesson[lessonId] = it }
            byId += exercises
        }
        val levels = lessons.observeLessons().first().associate { it.id to it.level }
        return DifficultyAudit.report(attempts, byId, levels)
    }

    companion object {
        /** A term of answers, not a session's: the audit needs eight per item. */
        const val WINDOW = 2_000

        private const val MAX_CACHED_LESSONS = 64
    }
}
