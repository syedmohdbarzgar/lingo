package org.token.english.domain.engine

import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.Skill

/**
 * One axis of the learner's profile: how well they know a track, **and on what
 * evidence that claim rests** (checklist v8 P2).
 *
 * A bare mastery number cannot be trusted on its own. Mastery is an
 * exponentially-weighted average seeded by the first observation — one correct
 * answer already reads as 100% — so "۹۰٪ واژگان" built from a single tap is
 * indistinguishable from the same number earned over fifty answers. [evidence] and
 * [accuracy] are what make the number auditable, and [status] is the honest
 * reading of the two together.
 */
data class SkillProfile(
    val skill: Skill,
    /**
     * The persisted per-skill mastery — the *same* number the daily planner reads,
     * never recomputed here. A profile that invented its own mastery would be a
     * second definition of "how well do they know this", which is exactly the
     * kind of drift the app avoids elsewhere.
     */
    val mastery: Float,
    /** Graded answers attributed to this track (curriculum evidence + SRS reviews). */
    val evidence: Int,
    /** Share of [evidence] that was correct; `null` when there is no evidence at all. */
    val accuracy: Float?,
    /** Curriculum nodes of this track that carry any evidence. */
    val nodesPractised: Int,
    val status: SkillStatus,
) {
    val masteryPercent: Int get() = (mastery.coerceIn(0f, 1f) * 100).toInt()
    val accuracyPercent: Int? get() = accuracy?.let { (it.coerceIn(0f, 1f) * 100).toInt() }
}

/**
 * How much a track's number is worth. Deliberately three states of *progress*
 * rather than a grade: the point is to stop a thin track from reading as a
 * finished one.
 */
enum class SkillStatus {
    /** No graded answer has touched this track yet. */
    NOT_STARTED,

    /** Evidence exists but is too thin to lean on. */
    BUILDING,

    /** Enough evidence, mastery still moving. */
    CONSOLIDATING,

    /** Enough evidence and mastery above the consolidation bar. */
    CONSOLIDATED,
}

/**
 * Graded answers that are **not** reachable through the curriculum graph, i.e. the
 * SRS review log (a review of a lesson word writes skill mastery but no knowledge
 * state). Counted so the vocabulary track does not look empty for a learner whose
 * practice is mostly review.
 */
data class SkillEvidence(
    val attempts: Int,
    val misses: Int,
) {
    operator fun plus(other: SkillEvidence) =
        SkillEvidence(attempts + other.attempts, misses + other.misses)
}

/**
 * Turns the learner model into independent skill tracks (checklist v8 P2).
 *
 * Two sources, neither of them new: the **curriculum graph** (which nodes a track
 * owns, and how many graded answers each node has absorbed) and the **SRS review
 * log** (the practice that never touches the graph). Mastery is passed in from the
 * persisted per-skill rows, so this engine explains the numbers the rest of the app
 * already uses rather than producing a parallel set.
 *
 * Pure and deterministic: no clock, no I/O, no Android. A track with no nodes in the
 * supplied content still yields a profile (mastery + review evidence), because a
 * curriculum that does not fund an axis is itself the finding.
 */
class SkillProfileEngine(
    /** Evidence below this leaves a track in [SkillStatus.BUILDING]. */
    private val buildingEvidence: Int = DEFAULT_BUILDING_EVIDENCE,
    /** Mastery at or above this counts as consolidated. */
    private val consolidatedMastery: Float = DEFAULT_CONSOLIDATED_MASTERY,
) {

    fun profiles(
        items: List<KnowledgeItem>,
        states: List<KnowledgeState>,
        masteryBySkill: Map<Skill, Float>,
        /** Review-log evidence per skill, keyed by the skill the grade exercised. */
        reviewEvidence: Map<Skill, SkillEvidence> = emptyMap(),
        tracks: List<Skill> = TRACKS,
    ): List<SkillProfile> {
        val byItemId = states.associateBy { it.itemId }
        return tracks.map { skill ->
            val nodes = items.filter { skill in it.skills }
            val touched = nodes.mapNotNull { byItemId[it.id] }

            // `exposureCount` counts every graded answer the node absorbed and
            // `lapses` every incorrect one (DefaultKnowledgeEngine increments it on
            // any miss, not only on a relapse), so accuracy is derivable without a
            // second counter.
            val curriculum = SkillEvidence(
                attempts = touched.sumOf { it.exposureCount },
                misses = touched.sumOf { it.lapses },
            )
            val review = reviewEvidence[skill] ?: SkillEvidence(0, 0)
            val evidence = curriculum + review

            SkillProfile(
                skill = skill,
                mastery = masteryBySkill[skill] ?: 0f,
                evidence = evidence.attempts,
                accuracy = if (evidence.attempts == 0) {
                    null
                } else {
                    (evidence.attempts - evidence.misses).coerceAtLeast(0).toFloat() / evidence.attempts
                },
                nodesPractised = touched.count { it.exposureCount > 0 },
                status = statusOf(evidence.attempts, masteryBySkill[skill] ?: 0f),
            )
        }
    }

    /**
     * Evidence gates the status; mastery only separates the two funded states. The
     * order matters: mastery is an average seeded by the first observation, so a
     * single correct answer already sits at 1.0 — letting it win would report a
     * track built from one tap as consolidated, which is the whole reason the
     * profile carries an evidence count.
     */
    private fun statusOf(evidence: Int, mastery: Float): SkillStatus = when {
        evidence == 0 -> SkillStatus.NOT_STARTED
        evidence < buildingEvidence -> SkillStatus.BUILDING
        mastery >= consolidatedMastery -> SkillStatus.CONSOLIDATED
        else -> SkillStatus.CONSOLIDATING
    }

    companion object {
        /**
         * The three axes the app actually measures and can act on. Deliberately the
         * same three `PlacementAssessmentEngine` reports: the placement test tags
         * VOCABULARY / GRAMMAR / READING and nothing else, so a profile that
         * presented a fourth axis would be inventing one.
         */
        val TRACKS = listOf(Skill.GRAMMAR, Skill.VOCABULARY, Skill.READING)

        /** Five graded answers is the point where a track stops being a guess. */
        const val DEFAULT_BUILDING_EVIDENCE = 5

        /** Same consolidation bar the per-node model and the SRS interval use. */
        const val DEFAULT_CONSOLIDATED_MASTERY = 0.75f
    }
}
