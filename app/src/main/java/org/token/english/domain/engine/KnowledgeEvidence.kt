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
     * 2. items that explicitly list [skill],
     * 3. every item of the lesson.
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
        if (byType.isNotEmpty()) return byType
        val bySkill = lessonItems.filter { skill in it.skills }
        return bySkill.ifEmpty { lessonItems }
    }
}
