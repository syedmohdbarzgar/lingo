package org.token.english.domain.model

/**
 * One section of a lesson's grammar teaching page (checklist A-8, option 1:
 * a multi-section format inside the existing `grammarTipFa` string — no schema
 * change, no Room migration).
 *
 * Format inside the raw string:
 *
 * ```
 * ## قاعده
 * 2–4 plain-Persian sentences…
 *
 * ## جدول
 * I am / you are / he is
 *
 * ## مثال‌ها
 * I am a student. — من دانشجو هستم.
 *
 * ## خطاهای رایج
 * نبودن فعل be در فارسی…
 * ```
 *
 * A string without any `## ` header is the legacy single tip and parses to one
 * [GrammarSectionKind.TIP] section, so unexpanded lessons render exactly as
 * before. The parser is total: unknown headers become [GrammarSectionKind.CUSTOM],
 * empty sections are dropped, and nothing here can throw on authored content.
 */
enum class GrammarSectionKind { TIP, RULE, TABLE, EXAMPLES, MISTAKES, CUSTOM }

data class GrammarSection(
    val kind: GrammarSectionKind,
    val title: String,
    val lines: List<String>,
) {
    /** Body text — what the short "after a miss" explanation shows. */
    fun asText(): String = lines.joinToString(" ")
}

/** Splits [raw] into sections; null/blank → empty list. */
fun parseGrammarTip(raw: String?): List<GrammarSection> {
    val text = raw?.trim().orEmpty()
    if (text.isEmpty()) return emptyList()

    val lines = text.split('\n')
    if (lines.none { it.startsWith(HEADER_PREFIX) }) {
        // Legacy plain tip — one section, exactly what the UI showed before A-8.
        return listOf(GrammarSection(GrammarSectionKind.TIP, TIP_TITLE, lines.map { it.trim() }.filter { it.isNotEmpty() }))
    }

    val sections = mutableListOf<GrammarSection>()
    var title: String? = null
    val body = mutableListOf<String>()
    fun flush() {
        val header = title ?: return
        val content = body.toList()
        body.clear()
        if (content.isEmpty()) return
        sections += GrammarSection(kindFor(header), header, content)
    }
    lines.forEach { line ->
        if (line.startsWith(HEADER_PREFIX)) {
            flush()
            title = line.removePrefix(HEADER_PREFIX).trim()
        } else if (title != null) {
            val trimmed = line.trim()
            if (trimmed.isNotEmpty()) body += trimmed
        }
        // Lines before the first header are ignored (nothing may precede "## ").
    }
    flush()
    return sections
}

private const val HEADER_PREFIX = "## "
private const val TIP_TITLE = "نکتهٔ گرامری"

/** Maps an authored header to a kind by its Persian keywords; unknown → CUSTOM. */
private fun kindFor(header: String): GrammarSectionKind = when {
    "قاعده" in header -> GrammarSectionKind.RULE
    "جدول" in header -> GrammarSectionKind.TABLE
    "مثال" in header -> GrammarSectionKind.EXAMPLES
    "خطا" in header || "اشتباه" in header -> GrammarSectionKind.MISTAKES
    else -> GrammarSectionKind.CUSTOM
}
