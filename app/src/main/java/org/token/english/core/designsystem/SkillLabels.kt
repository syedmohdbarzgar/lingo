package org.token.english.core.designsystem

import org.token.english.domain.model.Skill

/** Persian UI label per skill (single-locale MVP — see AGENTS.md). */
fun Skill.labelFa(): String = when (this) {
    Skill.VOCABULARY -> "واژگان"
    Skill.GRAMMAR -> "دستور زبان"
    Skill.LISTENING -> "شنیداری"
    Skill.SPEAKING -> "مکالمه"
    Skill.READING -> "خواندن"
    Skill.WRITING -> "نوشتن"
}
