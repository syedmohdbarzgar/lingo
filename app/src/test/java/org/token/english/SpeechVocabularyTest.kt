package org.token.english

import org.junit.Assert.assertTrue
import org.junit.Test
import org.json.JSONObject
import org.token.english.data.content.ContentParser
import org.token.english.domain.model.Exercise
import java.io.File

/**
 * Every word of a spoken answer must be a word the bundled recogniser can hear
 * (checklist 1.8).
 *
 * A Vosk `Recognizer` built with a constrained grammar — which is what conversation
 * practice uses, so a small English model is not free to invent words — silently
 * drops a token that is not in its lexicon. The turn then can never be graded
 * `CORRECT`, however well the learner speaks it. The real case was `Karimi` in
 * `a2.hotel-checkin.ex.10`.
 *
 * Two rules, both checked here:
 *  - **No proper nouns.** A name is almost never in a small English model. Weekdays,
 *    months and the few names the content deliberately uses (`tools/speech-names-allowed.txt`)
 *    are allowed; anything else must be reworded.
 *  - **No digits or non-ASCII characters** — the recogniser produces words.
 *
 * A model lexicon (`tools/vosk-words.txt`) is used too when it is present, which
 * makes every token a hard failure if the model does not list it. The bundled
 * `vosk-model-small-en-us-0.15` ships no `graph/words.txt` (its lexicon lives inside
 * `graph/HCLr.fst` without a symbol table — see docs/debt.md), so that half is
 * opt-in; the name rule is what runs in CI today.
 */
class SpeechVocabularyTest {

    private fun repoFile(name: String): File {
        val candidates = listOf(File(name), File("../$name"))
        return candidates.firstOrNull { it.exists() } ?: error("repo file not found: $name")
    }

    private fun contentFile(name: String): File {
        val candidates = listOf(
            File("src/main/assets/content/$name"),
            File("app/src/main/assets/content/$name"),
        )
        return candidates.firstOrNull { it.exists() } ?: error("content file not found: $name")
    }

    private fun speakingAnswers(): List<Pair<String, List<String>>> {
        val root = JSONObject(contentFile("exercises.json").readText())
        val exercises = root.getJSONArray("exercises")
        return (0 until exercises.length()).mapNotNull { i ->
            val o = exercises.getJSONObject(i)
            if (o.optString("type") != "speaking") return@mapNotNull null
            val parsed = ContentParser.parseExercise(o, o.optString("lessonId")) as? Exercise.Speaking
                ?: return@mapNotNull null
            parsed.id to parsed.referenceAnswers
        }
    }

    private fun lines(name: String): Set<String> =
        repoFile(name).readLines()
            .map { it.trim().lowercase() }
            .filter { it.isNotEmpty() && !it.startsWith("#") }
            .toSet()

    /**
     * Words of one answer, each with whether it opens a sentence.
     *
     * The flag has to come from the raw text before punctuation is stripped: an
     * answer can be two sentences ("Yes, please. It's Karimi."), and the capital
     * after the full stop is ordinary English, not a proper noun.
     */
    private fun tokens(text: String): List<Pair<Boolean, String>> {
        var opens = true
        return text.replace(Regex("[\u2018\u2019`\u00b4]"), "'")
            .split(Regex("\\s+"))
            .mapNotNull { raw ->
                val word = raw.replace(Regex("[^A-Za-z0-9']"), "")
                val token = if (word.isEmpty()) null else opens to word
                opens = raw.endsWith(".") || raw.endsWith("?") || raw.endsWith("!")
                token
            }
    }

    @Test
    fun `no spoken answer uses a word the recogniser cannot hear`() {
        val answers = speakingAnswers()
        assertTrue("the bundle has speaking items to check", answers.isNotEmpty())

        val allowedNames = lines("tools/speech-names-allowed.txt")
        val lexicon = runCatching {
            lines("tools/vosk-words.txt")
        }.getOrNull()?.takeIf { it.isNotEmpty() }

        val neverAName = setOf(
            "i", "i'm", "i'll", "i'd", "i've", "a", "an", "the", "yes", "no", "okay", "ok",
            "well", "oh", "hi", "hello", "sure", "please", "thanks", "thank", "sir", "madam",
        )
        val daysAndMonths = setOf(
            "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday",
            "january", "february", "march", "april", "may", "june", "july", "august",
            "september", "october", "november", "december",
        )

        val problems = mutableListOf<String>()
        for ((id, answers) in answers) {
            for (answer in answers) {
                for ((opensSentence, raw) in tokens(answer)) {
                    val lower = raw.lowercase()
                    val where = "$id: \"$answer\" → \"$raw\""
                    if (raw.any { it.isDigit() }) {
                        problems += "$where — a digit is not a word the recogniser produces"
                        continue
                    }
                    // "Mondays" is as legitimate as "Monday".
                    val dayOrMonth = lower in daysAndMonths || lower.removeSuffix("s") in daysAndMonths
                    val capitalisedInside = !opensSentence && raw.first().isUpperCase()
                    if (capitalisedInside && lower !in neverAName &&
                        !dayOrMonth && lower !in allowedNames
                    ) {
                        problems += "$where — proper noun in a spoken answer; reword it or " +
                            "add it to tools/speech-names-allowed.txt with a reason"
                    }
                    if (lexicon != null && lower !in lexicon) {
                        problems += "$where — not in the model's word list (tools/vosk-words.txt)"
                    }
                }
            }
        }

        assertTrue(
            "spoken answers must be recognisable:\n" + problems.joinToString("\n"),
            problems.isEmpty(),
        )
    }
}
