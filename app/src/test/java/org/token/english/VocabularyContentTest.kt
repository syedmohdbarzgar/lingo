package org.token.english

import org.json.JSONObject
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Guards the two vocabulary-content invariants the P0-9 A1/A2 audit surfaced
 * (both were broken before it, silently — nothing failed the build):
 *
 * 1. An example sentence must actually contain the word it illustrates. The
 *    check is inflection-tolerant (a token that *starts with* the head word
 *    counts, so `blocks` illustrates `block` and `children` illustrates
 *    `child`) and multi-word entries only require their parts to appear, in any
 *    order — that covers separable phrasal verbs (`put … off`). The genuinely
 *    irregular forms (went/children/women/implied …) are listed explicitly and
 *    the list may not rot.
 *
 * 2. A machine-generated "What does X mean?" choice (`scripts/archive/enrich_content.mjs`
 *    writes exactly one per lesson, always at `*.ex.07`) must draw every option
 *    from the lesson's own vocabulary. It used to pick distractors from a
 *    global cursor, so an A1 question could offer a C2 gloss (and vice versa),
 *    which makes the item trivially guessable and tests nothing. The
 *    hand-authored meaning items (`ex.01`/`ex.02`/`ex.08` …) are curated and
 *    may use plausible non-lesson distractors, so they are out of scope here.
 */
class VocabularyContentTest {

    private fun contentFile(name: String): File {
        val candidates = listOf(
            File("src/main/assets/content/$name"),
            File("app/src/main/assets/content/$name"),
        )
        return candidates.firstOrNull { it.exists() } ?: error("content file not found: $name")
    }

    private fun vocabulary(): List<JSONObject> {
        val array = JSONObject(contentFile("vocabulary.json").readText()).getJSONArray("vocabulary")
        return (0 until array.length()).map { array.getJSONObject(it) }
    }

    private fun exercises(): List<JSONObject> {
        val array = JSONObject(contentFile("exercises.json").readText()).getJSONArray("exercises")
        return (0 until array.length()).map { array.getJSONObject(it) }
    }

    private val tokens = Regex("[a-z'-]+")

    private fun tokensOf(text: String): List<String> =
        tokens.findAll(text.lowercase().replace('’', '\'')).map { it.value }.toList()

    /**
     * Words whose example legitimately uses a different form. Keyed by entry id
     * so a stale entry (renamed/removed word) fails the build instead of
     * silently widening the allowlist.
     */
    private val irregularForms = mapOf(
        // Irregular past — the whole point of the A2 past-simple lesson.
        "a2.past-simple.word.go" to "went",
        "a2.past-simple.word.buy" to "bought",
        "a2.past-simple.word.see" to "saw",
        "a2.past-simple.word.eat" to "ate",
        "a2.past-simple.word.take" to "took",
        "a2.past-simple.word.write" to "wrote",
        // Irregular plural (the A1 lesson teaches exactly this contrast).
        "a1.plurals.word.woman" to "women",
        // Consonant doubling / y→ied stems the prefix rule cannot see.
        "b1.work.word.apply" to "applied",
        "c2.nuance.word.imply" to "implies",
        "c1.inference.word.imply" to "implied",
        // Separable phrasal verbs: the particle moves away from the verb.
        "c1.phrasal-idioms.word.give-up" to "gave up",
        "c1.phrasal-idioms.word.come-across" to "came across",
        "c1.phrasal-idioms.word.run-out-of" to "ran out of",
    )

    private fun exampleUses(entry: JSONObject, word: String): Boolean {
        val parts = tokensOf(word)
        if (parts.isEmpty()) return false
        val examples = entry.getJSONArray("examples")
        val exampleTokens = (0 until examples.length()).flatMap { tokensOf(examples.getString(it)) }
        return parts.all { part -> exampleTokens.any { it == part || it.startsWith(part) } }
    }

    @Test
    fun `every vocabulary example actually contains its word`() {
        val offenders = vocabulary().filterNot { entry ->
            exampleUses(entry, entry.getString("word")) ||
                irregularForms.containsKey(entry.getString("id"))
        }
        assertTrue(
            "vocabulary example(s) never name their word — fix the sentence " +
                "or record the inflection in irregularForms: " +
                offenders.joinToString { "${it.getString("id")} (${it.getString("word")})" },
            offenders.isEmpty(),
        )
    }

    @Test
    fun `irregular-form allowlist is sharp - no stale entries and each form is present`() {
        val byId = vocabulary().associateBy { it.getString("id") }
        val stale = irregularForms.keys.filterNot { byId.containsKey(it) }
        assertTrue("irregularForms entry no longer exists — drop it: $stale", stale.isEmpty())

        val missing = irregularForms.filterNot { (id, form) ->
            val examples = byId.getValue(id).getJSONArray("examples")
            val text = (0 until examples.length()).joinToString(" ") { examples.getString(it) }.lowercase()
            text.contains(form)
        }
        assertTrue("irregularForms entry does not match its example any more: $missing", missing.isEmpty())
    }

    @Test
    fun `meaning choices only offer glosses from their own lesson`() {
        val glossesByLesson = vocabulary()
            .groupBy({ it.getString("lessonId") }, { it.getString("translation") })
            .mapValues { (_, glosses) -> glosses.toSet() }

        val problems = mutableListOf<String>()
        exercises().forEach { e ->
            if (e.optString("type") != "multiple_choice") return@forEach
            if (!e.getString("id").endsWith(".ex.07")) return@forEach
            val match = MEANING.find(e.optString("question")) ?: return@forEach
            val options = e.getJSONArray("options")
            val glosses = glossesByLesson[e.getString("lessonId")].orEmpty()
            (0 until options.length())
                .map { options.getString(it) }
                .filterNot { it in glosses }
                .forEach { problems += "${e.getString("id")}: foreign option «$it»" }

            // The answer must be the gloss of the word the question names.
            val word = match.groupValues[1]
            val expected = vocabulary()
                .firstOrNull { it.getString("word") == word && it.getString("lessonId") == e.getString("lessonId") }
            val correct = options.getString(e.getInt("correctIndex"))
            if (expected == null || expected.getString("translation") != correct) {
                problems += "${e.getString("id")}: answer «$correct» does not match the word «$word»"
            }
        }
        assertTrue("meaning-choice problem(s): $problems", problems.isEmpty())
    }

    @Test
    fun `no two multiple-choice questions ask the same thing`() {
        val seen = mutableMapOf<String, String>()
        val duplicates = mutableListOf<String>()
        exercises().filter { it.optString("type") == "multiple_choice" }.forEach { e ->
            val options = e.getJSONArray("options")
            val key = (e.optString("question") + "|" + options.getString(e.getInt("correctIndex"))).lowercase()
            val previous = seen.put(key, e.getString("id"))
            if (previous != null) duplicates += "$previous == ${e.getString("id")} ($key)"
        }
        assertTrue("duplicate multiple-choice item(s): $duplicates", duplicates.isEmpty())
    }

    private companion object {
        private val MEANING = Regex("""^What does "(.+)" mean\?$""")
    }
}
