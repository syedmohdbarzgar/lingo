package org.token.english.core.designsystem

import org.token.english.domain.model.MasteryDimension
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

/**
 * Persian label per mastery dimension: what kind of knowing a weakness is in
 * (checklist B-1 — "present simple is 82% but third-person -s is 34%" is only
 * actionable when the kind of knowing can be named).
 */
fun MasteryDimension.labelFa(): String = when (this) {
    MasteryDimension.RECOGNITION -> "شناسایی"
    MasteryDimension.RECALL -> "یادآوری"
    MasteryDimension.COMPREHENSION -> "درک مطلب"
    MasteryDimension.APPLICATION -> "کاربرد"
    MasteryDimension.PRODUCTION -> "تولید"
    MasteryDimension.RETENTION -> "یادماندگی"
}
