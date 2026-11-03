package org.token.english.domain.model

/**
 * The eight steps of a lesson, in the order a learner walks them (checklist
 * Phase 1). The UI shows **one** of them at a time; the ordering and the
 * completion rules live here, in pure Kotlin, so neither the screen nor the
 * ViewModel invents its own sequence.
 *
 * [id] is the stable identifier persisted between sessions — the enum name itself
 * is not storage (a rename must not lose a learner's place).
 *
 * [required] decides whether skipping is even offered: the teaching steps and the
 * productive ones are the lesson, while listening is optional (a device with no
 * English TTS voice cannot do it, and `LessonScreen` already falls back to text).
 * A skipped stage is never counted as mastered — [LessonStageProgress.skipped]
 * keeps that separate from [LessonStageProgress.completed].
 */
enum class LearningStage(
    val id: String,
    val labelFa: String,
    val required: Boolean,
) {
    CONTEXT("context", "موقعیت و هدف", required = true),
    GRAMMAR("grammar", "دستور زبان", required = true),
    VOCABULARY("vocabulary", "واژگان", required = true),
    PRACTICE("practice", "تمرین هدایت‌شده", required = true),
    LISTENING("listening", "شنیدن", required = false),
    WRITING("writing", "نوشتن و جمله‌سازی", required = true),
    SPEAKING("speaking", "گفتار و مکالمه", required = true),
    REVIEW("review", "مرور و ارزیابی", required = true),
    ;

    /** One-based position in the authored order — what the header shows. */
    val number: Int get() = ordinal + 1

    companion object {
        /** The authored order, so no caller depends on `values()` incidentally. */
        val ordered: List<LearningStage> = listOf(
            CONTEXT, GRAMMAR, VOCABULARY, PRACTICE, LISTENING, WRITING, SPEAKING, REVIEW,
        )

        val total: Int get() = ordered.size

        /**
         * Which step an exercise belongs to. This is the one mapping both the
         * planner and the session use: listening is its own step, free production
         * is writing, a dialogue turn is speaking, and everything else is
         * controlled practice.
         */
        fun of(exercise: Exercise): LearningStage = when (exercise) {
            is Exercise.Listening -> LISTENING
            is Exercise.SentenceProduction -> WRITING
            is Exercise.Speaking -> SPEAKING
            else -> PRACTICE
        }

        fun fromId(id: String): LearningStage? = ordered.firstOrNull { it.id == id }
    }
}

/**
 * The lesson as a sequence of steps, derived from the content that already exists
 * — no new JSON fields, so every shipped lesson gets the guided flow.
 *
 * [stages] only holds the available ones: a lesson with no dialogue simply has no
 * speaking step, and the learner is never shown an empty stage. [requiredStages]
 * is the denominator of the progress bar, so an optional step that is skipped
 * cannot inflate it.
 */
data class LessonStagePlan(val stages: List<LessonStageUnit>) {

    val requiredStages get() = stages.filter { it.stage.required }

    fun indexOf(stage: LearningStage): Int = stages.indexOfFirst { it.stage == stage }

    fun unitOf(stage: LearningStage): LessonStageUnit? = stages.firstOrNull { it.stage == stage }

    fun next(after: LearningStage): LearningStage? =
        stages.getOrNull(indexOf(after) + 1)?.stage

    fun previous(before: LearningStage): LearningStage? =
        stages.getOrNull(indexOf(before) - 1)?.stage

    /**
     * Where a returning learner starts when nothing was saved: the first step, or
     * the first step that still has something to do.
     */
    val firstStage: LearningStage get() = stages.firstOrNull()?.stage ?: LearningStage.CONTEXT

    companion object {
        /**
         * Builds the plan from a lesson's own content. [hasDialogue] comes from the
         * bundle (a lesson ships speaking turns or it does not) rather than from the
         * exercises, because the dialogue turns are filtered out of the lesson
         * session — they run on the conversation screen.
         */
        fun of(
            lesson: Lesson,
            vocabulary: List<VocabularyItem>,
            exercises: List<Exercise>,
            hasDialogue: Boolean,
        ): LessonStagePlan {
            val byStage = exercises.groupBy(LearningStage::of)
            return LessonStagePlan(
                LearningStage.ordered.mapNotNull { stage ->
                    when (stage) {
                        LearningStage.CONTEXT -> LessonStageUnit(stage)
                        LearningStage.GRAMMAR ->
                            if (lesson.grammarTipFa.orEmpty().isBlank()) null else LessonStageUnit(stage)
                        LearningStage.VOCABULARY ->
                            if (vocabulary.isEmpty()) null else LessonStageUnit(stage)
                        LearningStage.SPEAKING -> if (hasDialogue) LessonStageUnit(stage) else null
                        LearningStage.REVIEW -> LessonStageUnit(stage)
                        else -> byStage[stage]
                            ?.takeIf { it.isNotEmpty() }
                            ?.let { LessonStageUnit(stage, it) }
                    }
                },
            )
        }
    }
}

/**
 * What happened to one step in this lesson, persisted per lesson.
 *
 * `skipped` is deliberately its own flag: checklist Phase 1 asks that a skip never
 * marks a stage as mastered, so mastery-facing code must look at [completed].
 */
data class LessonStageProgress(
    val stage: LearningStage,
    val completed: Boolean = false,
    val skipped: Boolean = false,
) {
    /** Only a completed stage counts toward progress; a skip or a visit does not. */
    val countsAsDone: Boolean get() = completed
}

/**
 * Progress of a guided lesson: which step is active, which are done, and the
 * percentage those completed **required** steps represent.
 *
 * Progress is computed from stage status alone, never from the number of screens
 * opened, so leaving a stage early does not move the bar (checklist Phase 4).
 */
data class LessonStageState(
    val plan: LessonStagePlan,
    val progress: List<LessonStageProgress> = emptyList(),
    val active: LearningStage = plan.firstStage,
    /** Stages already open in this session — locking is derived from completion, not from this. */
    val visited: Set<LearningStage> = emptySet(),
) {
    private fun statusOf(stage: LearningStage): LessonStageProgress =
        progress.firstOrNull { it.stage == stage } ?: LessonStageProgress(stage)

    fun isCompleted(stage: LearningStage): Boolean = statusOf(stage).completed

    fun isSkipped(stage: LearningStage): Boolean = statusOf(stage).skipped

    /** A stage is reachable when it is the active one, already done, or behind it. */
    fun isAccessible(stage: LearningStage): Boolean {
        if (stage == active || stage in visited || isCompleted(stage)) return true
        val target = plan.indexOf(stage)
        if (target < 0) return false
        // Everything before the active stage is either done or was skipped.
        return target < plan.indexOf(active)
    }

    val completedCount: Int
        get() = plan.requiredStages.count { isCompleted(it.stage) }

    val requiredCount: Int get() = plan.requiredStages.size

    /** 0..100 over the completed required stages — the header's percentage. */
    val percent: Int
        get() = if (requiredCount == 0) 0 else (completedCount * 100) / requiredCount

    /** True when every step has been completed or deliberately skipped. */
    val isFinished: Boolean
        get() = plan.stages.all { isCompleted(it.stage) || isSkipped(it.stage) }

    fun withActive(stage: LearningStage): LessonStageState =
        copy(active = stage, visited = visited + stage)

    fun withCompleted(stage: LearningStage): LessonStageState =
        copy(progress = upsert(LessonStageProgress(stage, completed = true)))

    fun withSkipped(stage: LearningStage): LessonStageState =
        copy(progress = upsert(LessonStageProgress(stage, skipped = true)))

    private fun upsert(entry: LessonStageProgress): List<LessonStageProgress> =
        progress.filterNot { it.stage == entry.stage } + entry
}
