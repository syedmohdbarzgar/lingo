package org.token.english.domain.engine

import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeType
import org.token.english.domain.model.Skill

/**
 * Maps one graded answer onto the knowledge items it is evidence about
 * (audit §4/§19: "Knowledge Item ← multiple exercises ← evidence").
 *
 * The curriculum graph is authored at lesson granularity, and an exercise only
 * knows its lesson, so attribution is a small pure rule rather than a table in
 * the content files. Keeping it here means the rule is unit-tested and the
 * repositories stay dumb.
 */
object KnowledgeEvidence {

    /** The kind of knowledge an attempt of [skill] says something about. */
    fun knowledgeTypeFor(skill: Skill): KnowledgeType = when (skill) {
        Skill.GRAMMAR -> KnowledgeType.GRAMMAR
        Skill.VOCABULARY, Skill.LISTENING -> KnowledgeType.VOCABULARY
        Skill.READING, Skill.WRITING -> KnowledgeType.DISCOURSE
        Skill.SPEAKING -> KnowledgeType.PHONOLOGY
    }

    /**
     * Knowledge items an attempt of [skill] counts for, in priority order:
     *
     * 1. the lesson's items of the matching type,
     * 2. items that explicitly list [skill] — unioned with (1), never either/or,
     * 3. every item of the lesson.
     *
     * Step 2 must be a union: a PHONOLOGY node lists LISTENING, but listening's
     * default knowledge type is VOCABULARY (A-7). Under an either/or rule such a
     * node would never see an attempt and would sit at zero mastery forever,
     * while a lesson always has vocabulary nodes to match on.
     *
     * Step 3 is deliberate: a translation exercise really does test both the
     * lesson's words and its grammar, so spreading the evidence beats dropping
     * it (dropping it would leave the graph blind for exercises whose skill has
     * no dedicated node yet).
     */
    fun itemsFor(lessonItems: List<KnowledgeItem>, skill: Skill): List<KnowledgeItem> {
        if (lessonItems.isEmpty()) return emptyList()
        val wanted = knowledgeTypeFor(skill)
        val byType = lessonItems.filter { it.type == wanted }
        val bySkill = lessonItems.filter { skill in it.skills }
        // Type-first order keeps the result deterministic across callers.
        val union = (byType + bySkill).distinctBy { it.id }
        return union.ifEmpty { lessonItems }
    }

    /**
     * Knowledge items a graded **review card** is evidence about — deliberately
     * narrower than [itemsFor].
     *
     * A card is one word of one lesson, so the only honest claim it makes is about
     * that lesson's node **of the card's own type** — nothing else. Unlike
     * [itemsFor] there is no by-skill pass and no last-resort fallback:
     *
     * - the by-skill pass is not a narrowing here. The 35 lessons that ship words but
     *   no VOCABULARY node (a2.airport, b1.plans, b2.environment, …) have grammar
     *   nodes that *list* VOCABULARY in `skills`, so the pass selects exactly the
     *   nodes the rule is meant to exclude — reviewing «airport» raised
     *   `grammar.future-will` while the word had no node to move at all;
     * - "every node of the lesson" is right for an exercise, because a translation
     *   really does test the lesson's grammar too, and wrong for one word.
     *
     * An empty result is therefore a real answer: this lesson has no node the card
     * can be evidence about, and the card is still graded (schedule, log, meter) —
     * the graph simply stays out of a claim it cannot support.
     */
    fun itemsForCard(lessonItems: List<KnowledgeItem>, skill: Skill): List<KnowledgeItem> {
        if (lessonItems.isEmpty()) return emptyList()
        val wanted = knowledgeTypeFor(skill)
        return lessonItems.filter { it.type == wanted }
    }
}
