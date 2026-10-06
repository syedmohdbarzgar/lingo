package org.token.english.domain.model

/**
 * Normalizes and compares free-text answers (fill-blank / translation / listening
 * / review production). Pure Kotlin — unit tested. Comparison is case-insensitive,
 * punctuation-tolerant, Persian/Arabic character-normalizing and
 * contraction-tolerant (`I'm` ≡ `I am`, curly `’` ≡ straight `'`).
 */
object AnswerChecker {

    private val punctuation = Regex("[.!?،,;:]+")
    private val whitespace = Regex("\\s+")

    /**
     * Grading verdict for a typed answer (checklist A-2).
     * ALMOST is never credited — it exists only so the UI can say
     * «تقریباً درست — املای کلمه را بررسی کن» instead of a bare miss.
     */
    enum class Verdict { CORRECT, ALMOST, WRONG }

    /** Digits that may stand in for their word form (0–20 + tens). */
    private val numberWords = mapOf(
        "0" to "zero", "1" to "one", "2" to "two", "3" to "three",
        "4" to "four", "5" to "five", "6" to "six", "7" to "seven",
        "8" to "eight", "9" to "nine", "10" to "ten", "11" to "eleven",
        "12" to "twelve", "13" to "thirteen", "14" to "fourteen",
        "15" to "fifteen", "16" to "sixteen", "17" to "seventeen",
        "18" to "eighteen", "19" to "nineteen", "20" to "twenty",
        "30" to "thirty", "40" to "forty", "50" to "fifty",
        "60" to "sixty", "70" to "seventy", "80" to "eighty", "90" to "ninety",
    )

    /** Standalone 1–2 digit tokens ("3" matches, "1st" and "100" do not). */
    private val digitToken = Regex("\\b\\d{1,2}\\b")

    /** Minimum length before a single edit counts as a near-miss (checklist A-2). */
    private const val ALMOST_MIN_LENGTH = 5

    /** Common English contractions → their expanded form (apostrophe-normalized). */
    private val contractions = mapOf(
        "i'm" to "i am",
        "you're" to "you are",
        "he's" to "he is",
        "she's" to "she is",
        "it's" to "it is",
        "we're" to "we are",
        "they're" to "they are",
        "that's" to "that is",
        "there's" to "there is",
        "here's" to "here is",
        "who's" to "who is",
        "what's" to "what is",
        "where's" to "where is",
        "when's" to "when is",
        "let's" to "let us",
        "i've" to "i have",
        "you've" to "you have",
        "we've" to "we have",
        "they've" to "they have",
        "i'll" to "i will",
        "you'll" to "you will",
        "he'll" to "he will",
        "she'll" to "she will",
        "we'll" to "we will",
        "they'll" to "they will",
        "it'll" to "it will",
        "i'd" to "i would",
        "you'd" to "you would",
        "he'd" to "he would",
        "she'd" to "she would",
        "we'd" to "we would",
        "they'd" to "they would",
        "don't" to "do not",
        "doesn't" to "does not",
        "didn't" to "did not",
        "isn't" to "is not",
        "aren't" to "are not",
        "wasn't" to "was not",
        "weren't" to "were not",
        "haven't" to "have not",
        "hasn't" to "has not",
        "hadn't" to "had not",
        "won't" to "will not",
        "wouldn't" to "would not",
        "couldn't" to "could not",
        "shouldn't" to "should not",
        "mustn't" to "must not",
        "can't" to "can not",
        "cannot" to "can not",
        "shan't" to "shall not",
        "ain't" to "is not",
    )

    private val contractionRegex = Regex("\\b(" + contractions.keys.joinToString("|") { Regex.escape(it) } + ")\\b")

    fun normalize(raw: String): String = raw
        .trim()
        .lowercase()
        .replace(Regex("[\u2018\u2019\u02bc\u00b4`]"), "'") // curly/typographic apostrophes → straight
        .replace("\u200c", "") // ZWNJ removed (Persian)
        .replace('\u064a', '\u06cc') // Arabic yeh → Persian yeh (ی)
        .replace('\u0649', '\u06cc') // alef maksura → Persian yeh
        .replace('\u0643', '\u06a9') // Arabic kaf → Persian kaf (ک)
        .replace(punctuation, "")
        .replace(whitespace, " ")
        // "3" ≡ "three": digits normalize to their word form so a learner who
        // types the number gets credit against a word-form accepted answer (A-2).
        .replace(digitToken) { m -> numberWords[m.value] ?: m.value }

    /** Expands contractions so `I'm hungry` equals `I am hungry`. */
    fun expandContractions(normalized: String): String =
        contractionRegex.replace(normalized) { match -> contractions[match.value] ?: match.value }

    /** True when [answer] matches any accepted variant, contractions included. */
    fun matchesAny(accepted: List<String>, answer: String): Boolean {
        val normalized = normalize(answer)
        if (normalized.isEmpty()) return false
        val expanded = expandContractions(normalized)
        return accepted.any { candidate ->
            val norm = normalize(candidate)
            norm == normalized || norm == expanded ||
                expandContractions(norm) == normalized || expandContractions(norm) == expanded
        }
    }

    fun isCorrect(exercise: Exercise, answer: String): Boolean {
        val accepted = when (exercise) {
            is Exercise.MultipleChoice -> return exercise.options.getOrNull(exercise.correctIndex)
                ?.let { matchesAny(listOf(it), answer) } ?: false
            is Exercise.FillBlank -> exercise.accepted
            is Exercise.Translation -> exercise.accepted
            is Exercise.Listening -> exercise.accepted
            is Exercise.Speaking -> exercise.referenceAnswers
        }
        return matchesAny(accepted, answer)
    }

    /** Grades a typed answer: exact match → CORRECT, one-edit near-miss → ALMOST. */
    fun grade(exercise: Exercise, answer: String): Verdict = when {
        isCorrect(exercise, answer) -> Verdict.CORRECT
        isAlmost(exercise, answer) -> Verdict.ALMOST
        else -> Verdict.WRONG
    }

    /**
     * Near-miss detection (checklist A-2): a single word of at least
     * [ALMOST_MIN_LENGTH] letters exactly one edit (substitution, insertion or
     * deletion) away from an accepted answer. Deliberately narrow:
     *  - never applies to multiple choice (options are picked, not typed),
     *  - never applies to multi-word answers (edit distance over a sentence
     *    would accept sloppy paraphrases as "almost"),
     *  - never auto-accepts — the caller still grades the answer wrong.
     */
    fun isAlmost(exercise: Exercise, answer: String): Boolean {
        val accepted = when (exercise) {
            is Exercise.FillBlank -> exercise.accepted
            is Exercise.Translation -> exercise.accepted
            is Exercise.Listening -> exercise.accepted
            is Exercise.MultipleChoice -> return false
            is Exercise.Speaking -> return false
        }
        val typed = expandContractions(normalize(answer))
        if (typed.isEmpty() || ' ' in typed || typed.length < ALMOST_MIN_LENGTH) return false
        return accepted.any { candidate ->
            val norm = expandContractions(normalize(candidate))
            ' ' !in norm && norm.length >= ALMOST_MIN_LENGTH && levenshtein(typed, norm, max = 1) == 1
        }
    }

    /** Levenshtein distance capped at [max]; returns `max + 1` once exceeded. */
    private fun levenshtein(a: String, b: String, max: Int): Int {
        if (a == b) return 0
        if (kotlin.math.abs(a.length - b.length) > max) return max + 1
        var prev = IntArray(b.length + 1) { it }
        var curr = IntArray(b.length + 1)
        for (i in 1..a.length) {
            curr[0] = i
            var rowMin = curr[0]
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                curr[j] = minOf(prev[j] + 1, curr[j - 1] + 1, prev[j - 1] + cost)
                if (curr[j] < rowMin) rowMin = curr[j]
            }
            if (rowMin > max) return max + 1
            val tmp = prev
            prev = curr
            curr = tmp
        }
        return prev[b.length]
    }

    /** Human-readable correct answer shown after a miss (design.md §28). */
    fun correctAnswerText(exercise: Exercise): String = when (exercise) {
        is Exercise.MultipleChoice -> exercise.options.getOrElse(exercise.correctIndex) { "" }
        is Exercise.FillBlank -> exercise.accepted.firstOrNull().orEmpty()
        is Exercise.Translation -> exercise.accepted.firstOrNull().orEmpty()
        is Exercise.Listening -> exercise.accepted.firstOrNull().orEmpty()
        is Exercise.Speaking -> exercise.referenceAnswers.firstOrNull().orEmpty()
    }

    /**
     * Skill exercised by an exercise — feeds the mastery engine.
     * An authored `skill` tag in the content JSON always wins; otherwise this
     * heuristic classifies from the exercise shape (P4/P6):
     * meaning-choices with Persian options are vocabulary, sentence-completion
     * and "which sentence" items are grammar, translation is typed production
     * (writing), listening is listening.
     */
    fun skillOf(exercise: Exercise): Skill = when (exercise) {
        is Exercise.MultipleChoice -> exercise.skill ?: heuristicMc(exercise)
        is Exercise.FillBlank -> exercise.skill ?: Skill.GRAMMAR
        is Exercise.Translation -> exercise.skill ?: Skill.WRITING
        is Exercise.Listening -> exercise.skill ?: Skill.LISTENING
        is Exercise.Speaking -> exercise.skill ?: Skill.SPEAKING
    }

    private fun heuristicMc(exercise: Exercise.MultipleChoice): Skill {
        val hasPersianOption = exercise.options.any { o -> o.any { it in '\u0600'..'\u06ff' } }
        if (hasPersianOption) return Skill.VOCABULARY
        if (exercise.question.contains("______") || exercise.question.contains("___")) return Skill.GRAMMAR
        if (exercise.question.contains("sentence", ignoreCase = true)) return Skill.GRAMMAR
        return Skill.VOCABULARY
    }
}
