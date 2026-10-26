package org.token.english

import androidx.compose.ui.text.style.TextDirection
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.core.designsystem.component.firstStrongIsLtr
import org.token.english.core.designsystem.component.isolateEmbeddedLtr
import org.token.english.core.designsystem.component.textDirectionFor
import java.io.File

/**
 * RTL / BiDi safety (checklist P0).
 *
 * The app's root layout is **forced RTL** (Persian UI), so every piece of English
 * learning content has to re-assert LTR to render correctly. Two things can break
 * that and both are testable without a device:
 *
 *  1. the direction rule itself — the paragraph direction must follow English
 *     content and must NOT flip when Persian text leaks in (see [EnglishText]), and
 *  2. the content contract — English fields must actually be English, because
 *     Persian text in an LTR field is exactly what produces broken BiDi wrapping.
 */
class BidiTextTest {

    // ------------------------------------------------------------ 1. direction rule

    @Test
    fun `plain english keeps content direction`() {
        assertEquals(TextDirection.Content, textDirectionFor("hello"))
        assertEquals(TextDirection.Content, textDirectionFor("Where is the station?"))
    }

    @Test
    fun `an english sentence with persian after it still starts ltr`() {
        assertEquals(TextDirection.Content, textDirectionFor("book — کتاب"))
    }

    @Test
    fun `persian-first text forces an explicit ltr paragraph`() {
        // Forced LTR is the safety net: the paragraph must never flip inside the
        // English subtree even when the first strong character is Persian.
        assertEquals(TextDirection.Ltr, textDirectionFor("کتاب book"))
        assertEquals(TextDirection.Ltr, textDirectionFor("سلام، حال شما چطور است؟"))
    }

    @Test
    fun `leading punctuation digits and whitespace are skipped`() {
        assertTrue(firstStrongIsLtr("   hello"))
        assertTrue(firstStrongIsLtr("\"quoted\""))
        assertTrue(firstStrongIsLtr("12 apples"))
        assertTrue(firstStrongIsLtr("— emphasis"))
        assertFalse(firstStrongIsLtr("  «سلام»"))
        assertFalse(firstStrongIsLtr("۳ سیب")) // Persian digits, then a Persian letter
    }

    @Test
    fun `format characters inside english text do not flip the paragraph`() {
        // ZWNJ and combining marks are not letters, so they must be skipped.
        assertTrue(firstStrongIsLtr("\u200cHello"))
        assertFalse(firstStrongIsLtr("\u200cکتاب"))
    }

    @Test
    fun `text with no letters defaults to ltr`() {
        assertFalse("digits only", firstStrongIsLtr("123 456"))
        assertFalse("punctuation only", firstStrongIsLtr("?!…"))
        assertFalse("empty", firstStrongIsLtr(""))
        assertEquals(TextDirection.Ltr, textDirectionFor("123"))
    }

    @Test
    fun `emoji are skipped like any other non-letter`() {
        assertTrue(firstStrongIsLtr("🎧 listen"))
        assertFalse(firstStrongIsLtr("🎧 گوش بده"))
    }

    @Test
    fun `the rule only inspects the first letter it meets`() {
        // Accents after the first letter are irrelevant — the first ASCII Latin
        // letter settles the direction, so "café" is content-directed.
        assertTrue(firstStrongIsLtr("café"))
        // A non-ASCII letter as the FIRST letter forces the explicit LTR paragraph.
        assertFalse(firstStrongIsLtr("étude"))
    }

    // ------------------------------------- 1b. mixed Persian/English isolation

    @Test
    fun `embedded english runs are isolated inside persian prose`() {
        // The garbled lesson-card glitch: without isolation the period and the
        // Persian comma reorder against the two Latin runs ("go" drifts).
        val source = "می‌آیند: turn left، go straight. حرف اضافه را انتخاب کن."
        val expected = "می‌آیند: \u2066turn left، go straight.\u2069 حرف اضافه را انتخاب کن."
        assertEquals(expected, isolateEmbeddedLtr(source))
    }

    @Test
    fun `a quoted english run keeps its guillemets inside the isolate`() {
        // The pair must not straddle the isolate: the half left in the RTL text
        // mirrors while the half inside does not, and the quotes come out doubled.
        val source = "«in the left» غلط است — «on the left» درست."
        val expected = "\u2066«in the left»\u2069 غلط است — \u2066«on the left»\u2069 درست."
        assertEquals(expected, isolateEmbeddedLtr(source))
    }

    @Test
    fun `parentheses around english stay a pair`() {
        // Device run: «(capital)» rendered as «(capital))» before this rule.
        val source = "حرف بزرگ (capital) در آغاز نام‌ها."
        val expected = "حرف بزرگ \u2066(capital)\u2069 در آغاز نام‌ها."
        assertEquals(expected, isolateEmbeddedLtr(source))
    }

    @Test
    fun `a bracket pair followed by a comma stays balanced`() {
        val source = "پنج حرف صدادار (vowel)، بقیه بی‌صدا."
        val expected = "پنج حرف صدادار \u2066(vowel)،\u2069 بقیه بی‌صدا."
        assertEquals(expected, isolateEmbeddedLtr(source))
    }

    @Test
    fun `a colon stays persian while its english run is isolated`() {
        val source = "بعد از go، جهت بدون to می‌آید: «go to straight» غلط."
        val expected =
            "بعد از \u2066go،\u2069 جهت بدون \u2066to\u2069 می‌آید: \u2066«go to straight»\u2069 غلط."
        assertEquals(expected, isolateEmbeddedLtr(source))
    }

    @Test
    fun `isolation is idempotent and skips persian-only text`() {
        val persian = "سلام دنیا، حالت خوب است؟"
        assertEquals(persian, isolateEmbeddedLtr(persian))
        val once = isolateEmbeddedLtr("پاسخ درست: passport")
        assertEquals(once, isolateEmbeddedLtr(once))
    }

    @Test
    fun `bare numbers and trailing spaces stay outside`() {
        // Persian digits are not a Latin run; a Latin word+number group is one
        // run and only its internal spaces ride along.
        assertEquals("حدود ۲۰ دانش آموز", isolateEmbeddedLtr("حدود ۲۰ دانش آموز"))
        assertEquals(
            "\u206620 students\u2069  بهتر است",
            isolateEmbeddedLtr("20 students  بهتر است"),
        )
    }

    // ------------------------------------------------------------ 2. content contract

    private fun contentFile(name: String): File {
        val candidates = listOf(
            File("src/main/assets/content/$name"),
            File("app/src/main/assets/content/$name"),
        )
        return candidates.firstOrNull { it.exists() } ?: error("content file not found: $name")
    }

    private val persian = Regex("[\\u0600-\\u06FF\\uFB50-\\uFDFF\\uFE70-\\uFEFF]")

    /**
     * Fields the UI renders as English through EnglishText / AnswerField (LTR), and
     * therefore must actually be English.
     *
     * `prompt` is deliberately excluded from the exercises that cue FROM Persian
     * (`translation`, and the P2 `word_recall` / `sentence_production`): there the
     * prompt IS the Persian cue. The one reviewed exception below is an authored
     * Persian gloss kept inside an English sentence, which the BiDi rule handles
     * (the paragraph stays LTR and the gloss renders RTL within it).
     */
    private val persianCueTypes =
        setOf("translation", "word_recall", "sentence_production", "speaking")

    private val reviewedPersianGlosses = setOf(
        // "My ______ is a teacher. (مادر)" — the gloss names the word being tested.
        "a1.family.ex.03.sentence",
    )

    private fun englishFields(): List<Pair<String, String>> {
        val exercises = JSONObject(contentFile("exercises.json").readText()).getJSONArray("exercises")
        val result = mutableListOf<Pair<String, String>>()
        for (i in 0 until exercises.length()) {
            val o = exercises.getJSONObject(i)
            val id = o.getString("id")
            val type = o.optString("type")
            val keys = buildList {
                addAll(listOf("question", "sentence", "text"))
                if (type !in persianCueTypes) add("prompt")
                // A-5: an authored reading passage is English and renders LTR.
                add("passage")
            }
            keys.forEach { key ->
                val value = o.optString(key)
                if (value.isNotBlank()) result += "$id.$key" to value
            }
            // P2: `mustUse` anchors grade the produced sentence, so they are as
            // English as the accepted answers are.
            for (key in listOf("accepted", "mustUse")) {
                val arr = o.optJSONArray(key) ?: continue
                for (j in 0 until arr.length()) result += "$id.$key[$j]" to arr.getString(j)
            }
            // Multiple-choice options are Persian for meaning questions and English
            // for form questions; the correct English form is covered by `question`.
        }
        return result
    }

    @Test
    fun `english exercise fields contain no persian characters`() {
        val offenders = englishFields()
            .filter { (key, _) -> key !in reviewedPersianGlosses }
            .filter { (_, value) -> persian.containsMatchIn(value) }
        assertTrue(
            "English fields must not contain Persian text (it breaks BiDi in an LTR field): " +
                offenders.joinToString(", ") { it.first },
            offenders.isEmpty(),
        )
    }

    @Test
    fun `every reviewed persian gloss exception still exists`() {
        // Keeps the exception list honest: a renamed or deleted exercise must not
        // silently leave a stale allowance behind.
        val keys = englishFields().map { it.first }.toSet()
        val stale = reviewedPersianGlosses - keys
        assertTrue("stale BiDi exceptions: ${stale.joinToString(", ")}", stale.isEmpty())
    }

    @Test
    fun `persian prompt fields actually contain persian`() {
        // The mirror image: a Persian field that lost its translation would render
        // as a bare English sentence in an RTL paragraph.
        val vocabulary = JSONObject(contentFile("vocabulary.json").readText()).getJSONArray("vocabulary")
        val offenders = (0 until vocabulary.length()).mapNotNull { i ->
            val o = vocabulary.getJSONObject(i)
            val translation = o.optString("translation")
            if (translation.isBlank() || !persian.containsMatchIn(translation)) o.getString("id") else null
        }
        assertTrue("vocabulary translations without Persian: ${offenders.joinToString(", ")}", offenders.isEmpty())
    }

    @Test
    fun `the persian cue of every recall and production item is persian`() {
        // These items are cued in Persian by definition; an English cue would both
        // flip the RTL paragraph and hand the learner the answer (P2).
        val exercises = JSONObject(contentFile("exercises.json").readText()).getJSONArray("exercises")
        val offenders = (0 until exercises.length()).mapNotNull { i ->
            val o = exercises.getJSONObject(i)
            if (o.optString("type") !in persianCueTypes) return@mapNotNull null
            val prompt = o.optString("prompt")
            if (prompt.isBlank() || !persian.containsMatchIn(prompt)) o.getString("id") else null
        }
        assertTrue("cue prompts that are not Persian: ${offenders.joinToString(", ")}", offenders.isEmpty())
    }

    @Test
    fun `exercise skill tags are a known vocabulary`() {
        // Skill drives which mastery meter (and which label) a screen shows; a typo
        // silently becomes a different skill via the heuristic fallback.
        val allowed = setOf("VOCABULARY", "GRAMMAR", "LISTENING", "SPEAKING", "READING", "WRITING")
        val exercises = JSONObject(contentFile("exercises.json").readText()).getJSONArray("exercises")
        val offenders = (0 until exercises.length()).mapNotNull { i ->
            val o = exercises.getJSONObject(i)
            val skill = o.optString("skill")
            if (skill.isNotBlank() && skill.uppercase() !in allowed) "${o.getString("id")}($skill)" else null
        }
        assertTrue("unknown skill tags: ${offenders.joinToString(", ")}", offenders.isEmpty())
    }
}
