package org.token.english

import org.json.JSONObject
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Guards the two vocabulary-content invariants the P0-9 A1/A2 audit surfaced
 * (both were broken before it, silently — nothing failed the build):
 *
 * 1. An example sentence must actually contain the word it illustrates — every
 *    example individually (A-10 gives each word two). The check is
 *    inflection-tolerant (a token that *starts with* the head word counts, so
 *    `blocks` illustrates `block` and `children` illustrates `child`) and
 *    multi-word entries only require their parts to appear, in any order — that
 *    covers separable phrasal verbs (`put … off`). The genuinely irregular
 *    forms (went/children/women/implied …) are listed explicitly and the list
 *    may not rot.
 *
 * 1b. Depth (A-10): every word carries at least two examples, each an object
 *     `{en, fa}` whose Persian translation is really Persian. The legacy
 *     plain-string shape is rejected here so a rolled-back bundle fails loudly.
 *
 * 3. A word a lesson ships must be **asked** in that lesson's own exercises — it appears
 *    (inflection-tolerant) in their English, or is one of their accepted answers. Before
 *    v78, 133 words across 75 lessons were taught and then not asked until the review queue.
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
        // teeth is the plural the body lesson's own teaching page drills
        // (tooth → teeth), so the second example has to use it.
        "a1.body.word.tooth" to "teeth",
        // Consonant doubling / y→ied stems the prefix rule cannot see.
        "b1.work.word.apply" to "applied",
        "c2.nuance.word.imply" to "implies",
        "c1.inference.word.imply" to "implied",
        // Irregular past of a phrasal verb (grow → grew), so the particle still matches.
        "a2.childhood.word.grow-up" to "grew up",
        // y → ies plural: the plural rule cannot see a stem it has to respell.
        "a2.childhood.word.memory" to "memories",
        // y → ied past of reply, same respelling the prefix rule cannot see.
        "a2.emails.word.reply" to "replied",
        // Separable phrasal verbs: the particle moves away from the verb.
        "c1.phrasal-idioms.word.give-up" to "gave up",
        "c1.phrasal-idioms.word.come-across" to "came across",
        "c1.phrasal-idioms.word.run-out-of" to "ran out of",
    )

    /**
     * The example sentences as plain English strings, accepting both the
     * bilingual A-10 shape (`[{"en": …, "fa": …}]`) and the legacy
     * plain-string shape (`["…"]`) so the word check reads either bundle.
     */
    private fun exampleTexts(entry: JSONObject): List<String> {
        val examples = entry.getJSONArray("examples")
        return (0 until examples.length()).map { i ->
            when (val raw = examples.get(i)) {
                is JSONObject -> raw.optString("en")
                else -> raw.toString()
            }
        }
    }

    private fun exampleUses(example: String, word: String): Boolean {
        val parts = tokensOf(word)
        if (parts.isEmpty()) return false
        val exampleTokens = tokensOf(example)
        return parts.all { part -> exampleTokens.any { it == part || it.startsWith(part) } }
    }

    @Test
    fun `every vocabulary example actually contains its word`() {
        val offenders = mutableListOf<String>()
        vocabulary().forEach { entry ->
            val id = entry.getString("id")
            val word = entry.getString("word")
            val irregular = irregularForms[id]
            exampleTexts(entry).forEachIndexed { index, example ->
                val named = exampleUses(example, word) ||
                    (irregular != null && example.lowercase().contains(irregular))
                if (!named) offenders += "$id[$index]: «$example»"
            }
        }
        assertTrue(
            "vocabulary example(s) never name their word — fix the sentence " +
                "or record the inflection in irregularForms: " +
                offenders.joinToString(),
            offenders.isEmpty(),
        )
    }

    @Test
    fun `every word carries at least two bilingual examples`() {
        val persian = Regex("[\\u0600-\\u06FF]")
        val problems = mutableListOf<String>()
        vocabulary().forEach { entry ->
            val id = entry.getString("id")
            val examples = entry.getJSONArray("examples")
            if (examples.length() < 2) {
                problems += "$id: ${examples.length()} example(s) — A-10 requires 2"
                return@forEach
            }
            for (i in 0 until examples.length()) {
                val raw = examples.get(i)
                if (raw !is JSONObject) {
                    problems += "$id[$i]: plain-string example — must be {en, fa}"
                    continue
                }
                if (raw.optString("en").isBlank()) problems += "$id[$i]: no English sentence"
                val fa = raw.optString("fa")
                if (fa.isBlank()) problems += "$id[$i]: no Persian translation"
                else if (!persian.containsMatchIn(fa)) problems += "$id[$i]: translation is not Persian"
            }
        }
        assertTrue("vocabulary depth problem(s): ${problems.joinToString()}", problems.isEmpty())
    }

    @Test
    fun `irregular-form allowlist is sharp - no stale entries and each form is present`() {
        val byId = vocabulary().associateBy { it.getString("id") }
        val stale = irregularForms.keys.filterNot { byId.containsKey(it) }
        assertTrue("irregularForms entry no longer exists — drop it: $stale", stale.isEmpty())

        val missing = irregularForms.filterNot { (id, form) ->
            val text = exampleTexts(byId.getValue(id)).joinToString(" ").lowercase()
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

    /**
     * The English one exercise puts in front of the learner — every string field except the
     * Persian cues (`questionFa`, `explanationFa`, a recall's `prompt`).
     */
    private fun englishOf(exercise: JSONObject): String {
        val parts = mutableListOf<String>()
        val keys = exercise.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            if (key.endsWith("Fa") || key == "prompt") continue
            when (val value = exercise.opt(key)) {
                is String -> parts += value
                is org.json.JSONArray -> parts += (0 until value.length())
                    .map { value.get(it) }
                    .filterIsInstance<String>()
                else -> Unit
            }
        }
        return parts.joinToString(" ")
    }

    /**
     * 3. **A word a lesson ships is asked in that lesson.** The examples rule above keeps the word
     * in context (A-10 gives each one two bilingual sentences), but nothing held the *exercises* to
     * anything: 133 words across 75 lessons were taught and then not asked until the review queue,
     * which is the wrong place for a first retrieval. "Asked" is deliberately the weaker claim —
     * the word appears (inflection-tolerant, with the irregular allowlist) in the English of one of
     * the lesson's exercises, or is one of their accepted answers. Requiring a *sentence* about
     * every one of 1154 words would have been 133 hand-authored context items for no pedagogical
     * gain; the sentence is already guaranteed by the examples.
     */
    @Test
    fun `every word a lesson ships is asked in its own exercises`() {
        val byLesson = exercises().groupBy { it.getString("lessonId") }
        val offenders = mutableListOf<String>()
        vocabulary().forEach { entry ->
            val lessonId = entry.getString("lessonId")
            val word = entry.getString("word")
            val items = byLesson[lessonId].orEmpty()
            val askedInEnglish = exampleUses(items.joinToString(" ") { englishOf(it) }, word)
            val irregular = irregularForms[entry.getString("id")]
            val askedAsIrregular = irregular != null &&
                items.any { englishOf(it).lowercase().contains(irregular) }
            val askedAsAnswer = items.any { item ->
                val accepted = item.optJSONArray("accepted") ?: return@any false
                (0 until accepted.length()).any { accepted.getString(it).equals(word, ignoreCase = true) }
            }
            if (!askedInEnglish && !askedAsIrregular && !askedAsAnswer) {
                offenders += "${entry.getString("id")} («$word»)"
            }
        }
        assertTrue(
            "word(s) a lesson ships but never asks — add an exercise that uses the word, " +
                "a word_recall for it, or move it to a lesson that teaches it: ${offenders.joinToString()}",
            offenders.isEmpty(),
        )
    }

    private companion object {
        private val MEANING = Regex("""^What does "(.+)" mean\?$""")
    }
}
