package org.token.english.domain.engine

import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.Skill
import org.token.english.domain.model.TodayPlan

/**
 * Builds the learner's day (technical spec §22–24):
 * reviews first, then the next lesson, then recommendations aimed at weak skills.
 */
interface LearningPlanner {
    fun createPlan(
        dueReviewCount: Int,
        nextLesson: Lesson?,
        masteryBySkill: Map<Skill, Float>,
        targetMinutes: Int,
        todayStudySeconds: Long,
    ): TodayPlan
}

/**
 * Next lesson for a learner placed at [level] (checklist P4): uncompleted
 * lessons AT the placed level first, then levels above it (natural
 * progression); below-level lessons come last as optional practice — a B1
 * placement must not force A1 as the default path.
 */
fun nextLessonFor(
    allLessons: List<Lesson>,
    completedIds: Set<String>,
    level: LearningLevel,
): Lesson? {
    val pending = allLessons.filter { it.id !in completedIds }
    return pending.firstOrNull { it.level == level }
        ?: pending.firstOrNull { it.level.ordinal > level.ordinal }
        ?: pending.firstOrNull()
}

class DefaultLearningPlanner(
    private val weaknessThreshold: Float = 0.6f,
    private val maxRecommendedSkills: Int = 2,
) : LearningPlanner {

    override fun createPlan(
        dueReviewCount: Int,
        nextLesson: Lesson?,
        masteryBySkill: Map<Skill, Float>,
        targetMinutes: Int,
        todayStudySeconds: Long,
    ): TodayPlan {
        // Trained-but-weak skills first (worst score wins). Skills never
        // exercised yet are unproven — they count as weakest and fill the
        // remaining slots, so they DO appear in suggestions (checklist P6).
        // SPEAKING cannot be exercised offline (suspended), so it is never
        // recommended — no dead-end suggestions.
        val trainable = Skill.entries.filter { it != Skill.SPEAKING }
        val trainedWeak = trainable
            .mapNotNull { skill -> masteryBySkill[skill]?.let { skill to it } }
            .filter { it.second < weaknessThreshold }
            .sortedBy { it.second }
            .map { it.first }
        val untrained = trainable.filter { it !in masteryBySkill }
        val recommended = (trainedWeak + untrained).distinct().take(maxRecommendedSkills)

        return TodayPlan(
            targetMinutes = targetMinutes,
            todayStudySeconds = todayStudySeconds,
            dueReviewCount = dueReviewCount,
            nextLesson = nextLesson,
            recommendedSkills = recommended,
        )
    }
}
