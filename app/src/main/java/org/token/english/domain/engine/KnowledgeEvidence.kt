package org.token.english.domain.engine

import org.token.english.domain.model.AnswerChecker
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
     * that lesson's node **of the card's own type** — and, when the lesson owns more
     * than one node of that type, about the one node that **names that word**
     * ([KnowledgeItem.words], authored in `knowledge.json` since bundle v82). Three
     * lessons own two vocabulary nodes each (`c1.phrasal-idioms`, `c2.nuance`,
     * `c2.collocation`), so "the lesson's vocabulary node" used to be two nodes and a
     * card there moved both. A word no node claims picks none of them: the card is
     * still graded, and the graph stays out of a claim it cannot support rather than
     * guessing between two.
     *
     * A card that carries no word at all (a grammar-shaped review, a content id the
     * bundle cannot resolve) has no identity to pick with, so the lesson's nodes of
     * the card's type all stand — the bundle only enqueues vocabulary cards today.
     * Unlike [itemsFor] there is no by-skill pass and no last-resort fallback:
     *
     * - the by-skill pass is not a narrowing here. 35 lessons used to ship words with
     *   no VOCABULARY node (a2.airport, b1.plans, b2.environment, …) while their
     *   grammar nodes *list* VOCABULARY in `skills`, so the pass selected exactly the
     *   nodes the rule is meant to exclude — reviewing «airport» raised
     *   `grammar.future-will` while the word had no node to move at all. v65 shipped
     *   those nodes and `ContentDistributionTest` refuses a lesson that ships words
     *   without one, so the rule narrows on every lesson today — and stays the rule
     *   regardless, because one card is still one word;
     * - "every node of the lesson" is right for an exercise, because a translation
     *   really does test the lesson's grammar too, and wrong for one word.
     *
     * An empty result is therefore a real answer: this lesson has no node the card
     * can be evidence about, and the card is still graded (schedule, log, meter) —
     * the graph simply stays out of a claim it cannot support.
     */
    fun itemsForCard(
        lessonItems: List<KnowledgeItem>,
        skill: Skill,
        word: String? = null,
    ): List<KnowledgeItem> {
        if (lessonItems.isEmpty()) return emptyList()
        val typed = lessonItems.filter { it.type == knowledgeTypeFor(skill) }
        // One node of the type: it is the answer by construction, list or no list.
        if (typed.size <= 1) return typed
        val claimed = typed.filter { node -> node.claims(word) }
        if (claimed.isNotEmpty()) return claimed
        return if (word.isNullOrBlank()) typed else emptyList()
    }

    /** Whether this node names [word] among the headwords it teaches. */
    private fun KnowledgeItem.claims(word: String?): Boolean {
        if (word.isNullOrBlank() || words.isEmpty()) return false
        val wanted = AnswerChecker.normalize(word)
        return words.any { AnswerChecker.normalize(it) == wanted }
    }
}
