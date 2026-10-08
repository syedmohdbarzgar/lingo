package org.token.english.domain.engine

import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Skill
import org.token.english.domain.usecase.ScorePlacementUseCase

/**
 * One graded placement answer, tagged with the curriculum level it tests and the
 * skill it exercises (the placement JSON carries an authored `skill`).
 *
 * The assessment needs both axes: the level axis feeds the CEFR band scorer, and
 * the skill axis turns the same answers into a per-skill profile. Placement used
 * to throw the skill away and keep only a single level (P1-1).
 */
data class PlacementAnswer(
    val level: LearningLevel,
    val skill: Skill,
    val correct: Boolean,
)

/** How the learner did on one [Skill] across the placement questions that tested it. */
data class PlacementSkillReport(
    val skill: Skill,
    val asked: Int,
    val correct: Int,
) {
    val accuracy: Float get() = if (asked == 0) 0f else correct.toFloat() / asked

    /** True when this skill needs attention before the learner races ahead. */
    val isWeak: Boolean get() = accuracy < PlacementAssessmentEngine.STRONG_ACCURACY
}

/**
 * What the placement test actually measured (P1-1): an overall CEFR estimate plus
 * a per-skill read. The overall [level] still gates where the curriculum starts;
 * [bySkill] tells the app (Progress, planner) which skill to lean on first instead
 * of treating the very first lesson as a blank slate.
 *
 * Pure and deterministic: no Android, no Room, no clock. [bySkill] follows [Skill]
 * declaration order so surfaces never reorder it accidentally.
 */
data class PlacementAssessment(
    val level: LearningLevel,
    val answered: Int,
    val bySkill: List<PlacementSkillReport>,
) {
    /** Assessed skills the learner handled well, strongest first. */
    val strengths: List<Skill>
        get() = bySkill.filterNot { it.isWeak }
            .sortedWith(compareByDescending<PlacementSkillReport> { it.accuracy }.thenBy { it.skill.ordinal })
            .map { it.skill }

    /**
     * Assessed skills to work on next, weakest first. Ties break by declaration
     * order so the recommendation is stable across runs.
     */
    val focusSkills: List<Skill>
        get() = bySkill.filter { it.isWeak }
            .sortedWith(compareBy({ it.accuracy }, { it.skill.ordinal }))
            .map { it.skill }

    fun reportFor(skill: Skill): PlacementSkillReport? = bySkill.firstOrNull { it.skill == skill }
}

/**
 * Turns graded placement answers into a [PlacementAssessment] (P1-1).
 *
 * The overall level reuses [ScorePlacementUseCase] — one scoring rule for the app,
 * so the assessment can never disagree with the curriculum gate. The per-skill
 * half is a plain accuracy read: with only a handful of questions per skill, an
 * honest count beats a fabricated per-skill CEFR band.
 */
class PlacementAssessmentEngine(
    private val scorer: ScorePlacementUseCase = ScorePlacementUseCase(),
) {
    operator fun invoke(answers: List<PlacementAnswer>): PlacementAssessment {
        val level = scorer(answers.map { it.correct })
        val bySkill = Skill.entries.mapNotNull { skill ->
            val rows = answers.filter { it.skill == skill }
            if (rows.isEmpty()) null else PlacementSkillReport(skill, rows.size, rows.count { it.correct })
        }
        return PlacementAssessment(level = level, answered = answers.size, bySkill = bySkill)
    }

    companion object {
        /** Accuracy at or above which a skill counts as a strength rather than a weakness. */
        const val STRONG_ACCURACY = 0.75f
    }
}
