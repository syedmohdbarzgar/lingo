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

/**
 * The lesson whose conversation is still owed before the path may move on.
 *
 * A lesson is not finished when its last exercise is answered: its dialogue is
 * where the learner *produces* the structure the exercises only tested. So once a
 * lesson's exercises are complete, its conversation becomes a gate — the next
 * lesson stays shut until the exchange has been walked through to its last turn.
 * Only lessons that actually ship a dialogue can owe one, so content without a
 * conversation never blocks the path.
 *
 * @param dialogueLessonIds lessons whose bundle carries a spoken dialogue.
 * @param conversationsDone lessons whose dialogue has been practised end to end.
 */
fun owedConversation(
    allLessons: List<Lesson>,
    completedIds: Set<String>,
    dialogueLessonIds: Set<String>,
    conversationsDone: Set<String>,
): Lesson? = allLessons
    .sortedBy { it.order }
    .firstOrNull { lesson ->
        lesson.id in dialogueLessonIds &&
            lesson.id in completedIds &&
            lesson.id !in conversationsDone
    }

/**
 * Whether [lessonId] may be opened right now: no earlier lesson may be sitting on
 * an unfinished conversation. This is the hard side of the gate — the Home screen
 * uses the same rule to decide what its primary button offers, so the button and
 * the permission can never disagree.
 */
fun canOpenLesson(
    allLessons: List<Lesson>,
    completedIds: Set<String>,
    dialogueLessonIds: Set<String>,
    conversationsDone: Set<String>,
    lessonId: String,
): Boolean {
    val target = allLessons.firstOrNull { it.id == lessonId } ?: return false
    val owed = owedConversation(allLessons, completedIds, dialogueLessonIds, conversationsDone)
    // The owed lesson itself stays open — that is where the learner has to go.
    return owed == null || owed.order >= target.order
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
        // SPEAKING has no exercises yet (speech evaluation not built), so it is never
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
