package org.token.english

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.data.content.ContentParser
import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.Skill
import java.io.File

/**
 * The authored side of P2 (checklist v8): the bundled `word_recall` and
 * `sentence_production` items must actually be well formed, not just parse.
 *
 * The bar this file sets is the one that keeps the feature from quietly rotting:
 *
 *  - both shapes exist at **every** level, so no CEFR band loses the exercise type
 *    (a single forgotten level would otherwise only show up on a device);
 *  - the cue is Persian and the answers are English, because a Persian cue is what
 *    makes it production rather than a spelling test, and an English cue would both
 *    leak the answer and flip the RTL paragraph;
 *  - every production item carries anchors and a word floor that is above them, so
 *    the structural check always has something real to check;
 *  - and the **author's own exemplar must grade correct through the production
 *    grader** — the invariant that catches an anchor the exemplar does not use, an
 *    anchor out of order, or a floor the exemplar cannot reach.
 */
class ProductionRecallContentTest {

    private val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")

    private val persian = Regex("[\\u0600-\\u06FF\\uFB50-\\uFDFF\\uFE70-\\uFEFF]")

    private fun contentFile(name: String): File {
        val candidates = listOf(
            File("src/main/assets/content/$name"),
            File("app/src/main/assets/content/$name"),
        )
        return candidates.firstOrNull { it.exists() } ?: error("content file not found: $name")
    }

    private fun rows(file: String, key: String): List<JSONObject> {
        val array = JSONObject(contentFile(file).readText()).getJSONArray(key)
        return (0 until array.length()).map { array.getJSONObject(it) }
    }

    /** Every row is parsed through the production parser, so an unknown type fails here. */
    private fun parsed(type: String): List<Pair<JSONObject, Exercise>> =
        rows("exercises.json", "exercises")
            .filter { it.optString("type") == type }
            .map { row ->
                val exercise = ContentParser.parseExercise(row, row.getString("lessonId"))
                    ?: error("${row.optString("id")}: type '$type' does not parse")
                row to exercise
            }

    private fun strings(o: JSONObject, key: String): List<String> {
        val array = o.optJSONArray(key) ?: return emptyList()
        return (0 until array.length()).map { array.getString(it) }
    }

    private fun lessonLevels(): Map<String, String> =
        rows("lessons.json", "lessons").associate { it.getString("id") to it.getString("level") }

    // ------------------------------------------------------------------ recall

    @Test
    fun `every word recall item is a persian cue with english answers`() {
        val items = parsed("word_recall")
        assertTrue("no word_recall items in the bundle", items.isNotEmpty())

        val offenders = items.mapNotNull { (row, _) ->
            val id = row.getString("id")
            val prompt = row.optString("prompt")
            when {
                prompt.isBlank() -> "$id: blank prompt"
                !persian.containsMatchIn(prompt) -> "$id: prompt is not Persian (the meaning to name)"
                prompt.any { it in 'A'..'Z' || it in 'a'..'z' } -> "$id: prompt leaks English letters"
                strings(row, "accepted").isEmpty() -> "$id: no accepted answers"
                strings(row, "accepted").any { persian.containsMatchIn(it) } -> "$id: accepted answer is not English"
                else -> null
            }
        }
        assertTrue("malformed word_recall items: ${offenders.joinToString(", ")}", offenders.isEmpty())
    }

    @Test
    fun `word recall is always a vocabulary exercise`() {
        val wrong = parsed("word_recall")
            .filter { (_, exercise) -> AnswerChecker.skillOf(exercise) != Skill.VOCABULARY }
            .map { it.first.getString("id") }
        assertTrue(
            "recalling a word from its meaning is vocabulary work, not form work: $wrong",
            wrong.isEmpty(),
        )
    }

    // ------------------------------------------------------------------ production

    @Test
    fun `every production item carries anchors and a word floor above them`() {
        val items = parsed("sentence_production")
        assertTrue("no sentence_production items in the bundle", items.isNotEmpty())

        val offenders = items.mapNotNull { (row, exercise) ->
            val id = row.getString("id")
            val anchors = strings(row, "mustUse").map { it.trim() }.filter { it.isNotEmpty() }
            val anchorWords = anchors.sumOf { anchor -> anchor.split(" ").count { it.isNotBlank() } }
            val minWords = row.optInt("minWords", 0)
            val typed = exercise as Exercise.SentenceProduction
            when {
                row.optString("prompt").isBlank() -> "$id: blank prompt"
                !persian.containsMatchIn(row.optString("prompt")) -> "$id: prompt is not Persian"
                anchors.isEmpty() -> "$id: no mustUse anchors"
                anchors.any { anchor -> persian.containsMatchIn(anchor) } -> "$id: anchor is not English"
                minWords < 3 -> "$id: minWords $minWords is too low to be a sentence"
                minWords < anchorWords -> "$id: minWords $minWords is below its $anchorWords anchor word(s)"
                strings(row, "accepted").isEmpty() -> "$id: no exemplar answer"
                strings(row, "accepted").any { persian.containsMatchIn(it) } -> "$id: exemplar is not English"
                typed.mustUse != anchors -> "$id: anchors did not survive parsing"
                typed.minWords != minWords -> "$id: minWords did not survive parsing"
                else -> null
            }
        }
        assertTrue("malformed sentence_production items: ${offenders.joinToString(", ")}", offenders.isEmpty())
    }

    @Test
    fun `the authored anchors actually describe the exemplar`() {
        // The invariant that makes structural credit safe: the anchors and the floor
        // must be satisfiable by the author's own sentence.
        //
        // The answer is the author's exemplar. Its `accepted` list is replaced by a
        // **decoy** the answer cannot exact-match (the exemplar with its last word
        // swapped), so the exemplar shortcut cannot fire and only the structural
        // branch can credit the answer. The decoy still shares all the other words,
        // so it also satisfies the B-10 overlap gate — the assertions that still bite
        // are the anchor order and the word floor.
        val broken = parsed("sentence_production").mapNotNull { (row, exercise) ->
            val typed = exercise as Exercise.SentenceProduction
            val exemplar = typed.accepted.first()
            val decoy = exemplar.trimEnd('.', '!', '?').split(' ')
                .let { ws -> (ws.dropLast(1) + "indeed").joinToString(" ") }
            val structuralOnly = typed.copy(accepted = listOf(decoy))
            if (AnswerChecker.isCorrect(structuralOnly, exemplar)) {
                null
            } else {
                "${row.getString("id")} (anchors=${typed.mustUse}, minWords=${typed.minWords}, exemplar=$exemplar)"
            }
        }
        assertTrue(
            "production items whose own anchors cannot credit their own exemplar: $broken",
            broken.isEmpty(),
        )
    }

    @Test
    fun `an unanchored rewrite of a production answer is not credited`() {
        // The grader's guarantee, checked on real content rather than a fixture: for
        // every item, a long sentence that avoids the anchors must not pass.
        val escaped = parsed("sentence_production").mapNotNull { (row, exercise) ->
            val filler = "the quick brown fox jumps over the lazy dog again and again and again"
            if (AnswerChecker.isCorrect(exercise, filler)) row.getString("id") else null
        }
        assertTrue("production items passed by an answer that ignores the target: $escaped", escaped.isEmpty())
    }

    // ------------------------------------------------------------------ coverage

    @Test
    fun `both new shapes are present at every cefr level`() {
        val levelOf = lessonLevels()
        val byLevel = { type: String ->
            parsed(type).map { (row, _) -> levelOf[row.getString("lessonId")] }
                .groupingBy { it }.eachCount()
        }
        val recall = byLevel("word_recall")
        val production = byLevel("sentence_production")

        val missing = levels.filter { (recall[it] ?: 0) == 0 || (production[it] ?: 0) == 0 }
        assertTrue(
            "level(s) without both new exercise shapes (recall=$recall, production=$production): $missing",
            missing.isEmpty(),
        )
    }

    @Test
    fun `the new shapes are a real part of the bundle, not a token sample`() {
        // A couple of items per level is the floor; the checklist asks for the shapes
        // to be *developed*, so the count is asserted rather than left to drift.
        assertEquals("two word_recall items per level", 12, parsed("word_recall").size)
        assertEquals("two sentence_production items per level", 12, parsed("sentence_production").size)
    }
}
