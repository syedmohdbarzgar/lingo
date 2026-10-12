package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.data.content.ContentParser
import org.token.english.domain.engine.SkillEvidence
import org.token.english.domain.engine.SkillProfileEngine
import org.token.english.domain.engine.SkillStatus
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.KnowledgeType
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Skill
import java.io.File

/**
 * The three independent skill tracks (checklist v8 P2).
 *
 * What these tests are really about: a track's number must be **auditable**. Mastery
 * is an EWMA seeded by the first observation, so one correct answer already reads as
 * 100% — a bar alone cannot distinguish "knows it" from "tapped once". Everything
 * here checks that the evidence and the status keep that distinction, and that the
 * three axes are actually fed by separate work.
 */
class SkillProfileEngineTest {

    private val engine = SkillProfileEngine()

    private fun item(id: String, vararg skills: Skill) = KnowledgeItem(
        id = id,
        type = if (Skill.GRAMMAR in skills) KnowledgeType.GRAMMAR else KnowledgeType.DISCOURSE,
        title = id,
        titleFa = "«$id»",
        level = LearningLevel.A1,
        prerequisites = emptyList(),
        lessonIds = listOf("l.$id"),
        skills = skills.toList(),
    )

    private fun state(itemId: String, mastery: Float, attempts: Int, misses: Int) = KnowledgeState(
        itemId = itemId,
        mastery = mastery,
        exposureCount = attempts,
        consecutiveCorrect = 0,
        consecutiveIncorrect = misses,
        intervalDays = 0,
        easeFactor = 2.5f,
        repetitions = 0,
        lapses = misses,
        lastReviewedAt = null,
        nextReviewAt = 0L,
    )

    private fun profileOf(
        items: List<KnowledgeItem>,
        states: List<KnowledgeState>,
        mastery: Map<Skill, Float>,
        review: Map<Skill, SkillEvidence> = emptyMap(),
        skill: Skill,
    ) = engine.profiles(items, states, mastery, review).first { it.skill == skill }

    // ------------------------------------------------------------------ attribution

    @Test
    fun `a track counts only the nodes it owns`() {
        val items = listOf(
            item("g1", Skill.GRAMMAR),
            item("r1", Skill.READING),
            item("v1", Skill.VOCABULARY),
        )
        val states = listOf(
            state("g1", 0.4f, attempts = 3, misses = 1),
            state("r1", 0.9f, attempts = 7, misses = 0),
            state("v1", 0.5f, attempts = 2, misses = 2),
        )
        val mastery = mapOf(Skill.GRAMMAR to 0.4f, Skill.READING to 0.9f, Skill.VOCABULARY to 0.5f)

        val grammar = profileOf(items, states, mastery, skill = Skill.GRAMMAR)
        assertEquals("only the grammar node's answers", 3, grammar.evidence)
        // Truncated, not rounded — the same convention as masteryPercent everywhere.
        assertEquals("two of three right", 66, grammar.accuracyPercent)
        assertEquals(1, grammar.nodesPractised)

        assertEquals(7, profileOf(items, states, mastery, skill = Skill.READING).evidence)
        assertEquals(2, profileOf(items, states, mastery, skill = Skill.VOCABULARY).evidence)
    }

    @Test
    fun `mastery is read from the persisted rows, never recomputed`() {
        // The profile must explain the number the planner uses, not invent a second
        // definition of "how well do they know this".
        val profile = profileOf(
            items = listOf(item("g1", Skill.GRAMMAR)),
            states = listOf(state("g1", 0.1f, attempts = 9, misses = 9)),
            mastery = mapOf(Skill.GRAMMAR to 0.83f),
            skill = Skill.GRAMMAR,
        )
        assertEquals(0.83f, profile.mastery, 0.0001f)
        assertEquals(83, profile.masteryPercent)
        assertEquals("the node's own numbers are the evidence", 9, profile.evidence)
        assertEquals(0, profile.accuracyPercent)
    }

    @Test
    fun `a track with no nodes in the content still reports its review practice`() {
        // A curriculum that does not fund an axis is itself worth showing, and the SRS
        // log is real work even when the graph has nothing to attribute it to.
        val profile = profileOf(
            items = emptyList(),
            states = emptyList(),
            mastery = mapOf(Skill.VOCABULARY to 0.6f),
            review = mapOf(Skill.VOCABULARY to SkillEvidence(attempts = 12, misses = 3)),
            skill = Skill.VOCABULARY,
        )
        assertEquals(12, profile.evidence)
        assertEquals(75, profile.accuracyPercent)
        assertEquals(0, profile.nodesPractised)
    }

    @Test
    fun `review evidence lands on the track it exercises`() {
        val items = listOf(item("v1", Skill.VOCABULARY), item("g1", Skill.GRAMMAR))
        val profiles = engine.profiles(
            items = items,
            states = listOf(state("v1", 0.5f, attempts = 1, misses = 0)),
            masteryBySkill = mapOf(Skill.VOCABULARY to 0.5f, Skill.GRAMMAR to 0f),
            reviewEvidence = mapOf(Skill.VOCABULARY to SkillEvidence(attempts = 4, misses = 1)),
        )
        val vocabulary = profiles.first { it.skill == Skill.VOCABULARY }
        val grammar = profiles.first { it.skill == Skill.GRAMMAR }

        assertEquals("curriculum 1 + review 4", 5, vocabulary.evidence)
        assertEquals("one miss of five", 80, vocabulary.accuracyPercent)
        assertEquals("review practice never credits grammar", 0, grammar.evidence)
        assertNull("no evidence at all means no accuracy to report", grammar.accuracy)
    }

    // ------------------------------------------------------------------ honesty

    @Test
    fun `an untouched track reports no accuracy rather than zero`() {
        // 0% would read as "answered everything wrong", which is a different claim.
        val profile = profileOf(emptyList(), emptyList(), mapOf(Skill.GRAMMAR to 0f), skill = Skill.GRAMMAR)
        assertEquals(0, profile.evidence)
        assertNull(profile.accuracy)
        assertEquals(SkillStatus.NOT_STARTED, profile.status)
    }

    @Test
    fun `a track built from a couple of answers cannot read as consolidated`() {
        // The exact failure mode this engine exists to prevent: mastery 1.0 from one
        // correct answer (the EWMA seed) must not be presented as a finished track.
        val seeded = profileOf(
            items = listOf(item("g1", Skill.GRAMMAR)),
            states = listOf(state("g1", 1f, attempts = 2, misses = 0)),
            mastery = mapOf(Skill.GRAMMAR to 1f),
            skill = Skill.GRAMMAR,
        )
        assertEquals("100% mastery is still reported honestly", 100, seeded.masteryPercent)
        assertEquals("but the status refuses to call it done", SkillStatus.BUILDING, seeded.status)
    }

    @Test
    fun `enough evidence and high mastery is what consolidation means`() {
        val consolidated = profileOf(
            items = listOf(item("g1", Skill.GRAMMAR)),
            states = listOf(state("g1", 0.9f, attempts = 9, misses = 1)),
            mastery = mapOf(Skill.GRAMMAR to 0.9f),
            skill = Skill.GRAMMAR,
        )
        assertEquals(SkillStatus.CONSOLIDATED, consolidated.status)

        val working = profileOf(
            items = listOf(item("g1", Skill.GRAMMAR)),
            states = listOf(state("g1", 0.55f, attempts = 9, misses = 4)),
            mastery = mapOf(Skill.GRAMMAR to 0.55f),
            skill = Skill.GRAMMAR,
        )
        assertEquals(SkillStatus.CONSOLIDATING, working.status)
    }

    @Test
    fun `a node shared by two tracks counts for both, and mastery still does not`() {
        // Some nodes genuinely train two axes (a passive-voice reading text is both).
        // The evidence counter may overlap; the numbers must not blend, because they
        // come from separate persisted rows.
        val shared = item("d1", Skill.GRAMMAR, Skill.READING)
        val profiles = engine.profiles(
            items = listOf(shared),
            states = listOf(state("d1", 0.7f, attempts = 6, misses = 2)),
            masteryBySkill = mapOf(Skill.GRAMMAR to 0.2f, Skill.READING to 0.95f),
        )
        val grammar = profiles.first { it.skill == Skill.GRAMMAR }
        val reading = profiles.first { it.skill == Skill.READING }

        assertEquals(6, grammar.evidence)
        assertEquals(6, reading.evidence)
        assertEquals(0.2f, grammar.mastery, 0.0001f)
        assertEquals(0.95f, reading.mastery, 0.0001f)
    }

    // ------------------------------------------------------------------ real bundle

    private fun contentFile(name: String): File {
        val candidates = listOf(
            File("src/main/assets/content/$name"),
            File("app/src/main/assets/content/$name"),
        )
        return candidates.firstOrNull { it.exists() } ?: error("content file not found: $name")
    }

    private val items: List<KnowledgeItem> =
        ContentParser.parseKnowledge(contentFile("knowledge.json").readText())

    @Test
    fun `every track is funded by the shipped curriculum`() {
        val profiles = engine.profiles(items, emptyList(), emptyMap())
        assertEquals(SkillProfileEngine.TRACKS, profiles.map { it.skill })
        // A declared axis with no nodes behind it would be a track that can never move.
        val unfunded = profiles.filter { profile -> items.none { profile.skill in it.skills } }
        assertTrue(
            "track(s) with no curriculum nodes: ${unfunded.map { it.skill }}",
            unfunded.isEmpty(),
        )
    }

    @Test
    fun `the three tracks are separate except a listed set of shared nodes`() {
        // Independence is the claim this feature makes, so it is asserted rather than
        // assumed: the tracks partition the graph except for the nodes below, which
        // genuinely train two axes. The list is exhaustive — a new shared node fails
        // the build until someone says why it is shared.
        val trackSkills = SkillProfileEngine.TRACKS.toSet()
        val shared = items
            .map { it.id to it.skills.filter { s -> s in trackSkills }.distinct().toSet() }
            .filter { (_, skills) -> skills.size > 1 }
            .toMap()

        assertEquals(
            "nodes claimed by more than one track — add the reason here if this is intended",
            setOf(
                "grammar.passive-simple", // taught through a reading passage as well as drills
                "discourse.reason-cause", // a reading-comprehension skill stated as grammar
                "discourse.narrative-tenses", // narrative reading and tense control
                "vocab.idioms-and-figurative", // figurative vocabulary read in context
            ),
            shared.keys,
        )
    }
}
