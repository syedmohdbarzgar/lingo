package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.model.GrammarSectionKind
import org.token.english.domain.model.parseGrammarTip

/**
 * A-8 stage 1: `grammarTipFa` may be a legacy plain tip or the multi-section
 * format (`## قاعده` …). The parser is total — authored content must never
 * crash the intro, and legacy lessons must render exactly as before.
 */
class GrammarTipTest {

    @Test
    fun `legacy plain tip becomes one TIP section with the original title`() {
        val raw = "سلام‌ها به زمان روز بستگی دارند: Good morning تا ظهر."
        val sections = parseGrammarTip(raw)

        assertEquals(1, sections.size)
        val tip = sections.single()
        assertEquals(GrammarSectionKind.TIP, tip.kind)
        assertEquals("نکتهٔ گرامری", tip.title)
        assertEquals(raw, tip.lines.joinToString(" "))
    }

    @Test
    fun `multi-section tip splits into kinds by header keyword`() {
        val raw = """
            ## قاعده
            برای کارهای تکراری از زمان حال ساده استفاده کن.

            ## جدول
            I go / you go / he goes

            ## مثال‌ها
            I go to work. — من به سر کار می‌روم.
            She goes to school. — او به مدرسه می‌رود.

            ## خطاهای رایج
            گذاشتن s برای I و we اشتباه است.
        """.trimIndent()

        val sections = parseGrammarTip(raw)
        assertEquals(listOf("قاعده", "جدول", "مثال‌ها", "خطاهای رایج"), sections.map { it.title })
        assertEquals(
            listOf(
                GrammarSectionKind.RULE,
                GrammarSectionKind.TABLE,
                GrammarSectionKind.EXAMPLES,
                GrammarSectionKind.MISTAKES,
            ),
            sections.map { it.kind },
        )
        assertEquals("I go / you go / he goes", sections[1].lines.single())
        assertEquals(2, sections[2].lines.size)
        // Blank lines inside are dropped; edge content is trimmed.
        sections.forEach { section -> assertTrue(section.lines.none { it.isBlank() }) }
    }

    @Test
    fun `unknown header becomes CUSTOM and empty sections are dropped`() {
        val raw = """
            ## تمرین آزاد
            هر جمله‌ای بنویس.

            ## جدول
        """.trimIndent()

        val sections = parseGrammarTip(raw)
        assertEquals(1, sections.size)
        assertEquals(GrammarSectionKind.CUSTOM, sections[0].kind)
        assertEquals("تمرین آزاد", sections[0].title)
    }

    @Test
    fun `blank input yields no sections`() {
        assertTrue(parseGrammarTip(null).isEmpty())
        assertTrue(parseGrammarTip("").isEmpty())
        assertTrue(parseGrammarTip("   \n  ").isEmpty())
    }

    @Test
    fun `lines before the first header are ignored not fatal`() {
        val sections = parseGrammarTip("مقدمه‌ای که نباید بیاید\n## قاعده\nقانون.")
        assertEquals(1, sections.size)
        assertEquals(listOf("قانون."), sections[0].lines)
    }

    @Test
    fun `summary is the rule section for sectioned tips and the whole tip otherwise`() {
        val sectioned = parseGrammarTip("## قاعده\nقانون کوتاه.\n## مثال‌ها\nA — الف.")
        assertEquals("قانون کوتاه.", sectioned.first().asText())

        val legacy = parseGrammarTip("نکتهٔ قدیمی.")
        assertEquals("نکتهٔ قدیمی.", legacy.first().asText())
    }
}
