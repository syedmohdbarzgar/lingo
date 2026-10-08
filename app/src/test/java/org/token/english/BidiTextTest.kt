package org.token.english

import androidx.compose.ui.text.style.TextDirection
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.core.designsystem.component.firstStrongIsLtr
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
     * `prompt` is deliberately excluded from translation exercises: there the prompt
     * IS the Persian sentence to translate. The one reviewed exception below is an
     * authored Persian gloss kept inside an English sentence, which the BiDi rule
     * handles (the paragraph stays LTR and the gloss renders RTL within it).
     */
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
                if (type != "translation") add("prompt")
            }
            keys.forEach { key ->
                val value = o.optString(key)
                if (value.isNotBlank()) result += "$id.$key" to value
            }
            val accepted = o.optJSONArray("accepted")
            if (accepted != null) {
                for (j in 0 until accepted.length()) {
                    result += "$id.accepted[$j]" to accepted.getString(j)
                }
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
