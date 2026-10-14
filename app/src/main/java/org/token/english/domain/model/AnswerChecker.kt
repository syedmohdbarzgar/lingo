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
     * A dot or comma with digits on BOTH sides is a decimal/thousands separator,
     * not sentence punctuation (checklist A-2b). It is swapped for [SEPARATOR]
     * before the punctuation strip and restored after, so `3.5` can never collapse
     * into `35` — the collision that would make a price lesson ungradeable.
     * A sentence-final dot (`3.`) still goes, so `3.` ≡ `three` keeps working.
     */
    private val digitSeparator = Regex("(?<=\\d)[.,](?=\\d)")
    private const val SEPARATOR = "\u0001"

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

    /** Any run of digits; [normalize] decides whether it may become a word. */
    private val numberRun = Regex("\\d+")

    /** Minimum length before a single edit counts as a near-miss (checklist A-2). */
    private const val ALMOST_MIN_LENGTH = 5

    /**
     * Share of an authored exemplar's words a structurally-matching produced
     * sentence must reuse (checklist v9 B-10). 0.5 lets a real paraphrase through
     * (swapping a pronoun or a synonym keeps most words) while rejecting a filler
     * sentence that merely carries the anchors. Anchors are never adjacency-checked
     * because the authored `mustUse` pairs are not adjacent in their own exemplars
     * (`usually … have breakfast`, `if … we will`).
     */
    private const val PRODUCTION_MIN_EXEMPLAR_OVERLAP = 0.5f

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

    /**
     * Second valid reading of an apostrophe-*ambiguous* contraction (checklist
     * A-2c). `he's` is `he is` **or** `he has`, `i'd` is `i would` **or** `i had` —
     * `'s` is is/has and `'d` is would/had. [contractions] keeps the primary (more
     * common) reading for structural matching; grading accepts either reading
     * because the app cannot know which one the learner meant and both are
     * correct English.
     */
    private val contractionAlternatives = mapOf(
        "he's" to listOf("he has"),
        "she's" to listOf("she has"),
        "it's" to listOf("it has"),
        "that's" to listOf("that has"),
        "there's" to listOf("there has"),
        "who's" to listOf("who has"),
        "what's" to listOf("what has"),
        "where's" to listOf("where has"),
        "when's" to listOf("when has"),
        "i'd" to listOf("i had"),
        "you'd" to listOf("you had"),
        "he'd" to listOf("he had"),
        "she'd" to listOf("she had"),
        "we'd" to listOf("we had"),
        "they'd" to listOf("they had"),
    )

    /** Every accepted reading per contraction: the primary form plus the alternatives. */
    private val contractionForms: Map<String, List<String>> =
        contractions.mapValues { (key, form) -> listOf(form) + contractionAlternatives[key].orEmpty() }

    /** Per-contraction regexes, precompiled — [expansionVariants] walks these. */
    private val contractionVariants: List<Pair<Regex, List<String>>> =
        contractionForms.map { (contraction, forms) ->
            Regex("\\b" + Regex.escape(contraction) + "\\b") to forms
        }

    fun normalize(raw: String): String {
        val stripped = raw
            .trim()
            .lowercase()
            .replace(Regex("[\u2018\u2019\u02bc\u00b4`]"), "'") // curly/typographic apostrophes → straight
            .replace("\u200c", "") // ZWNJ removed (Persian)
            .replace('\u064a', '\u06cc') // Arabic yeh → Persian yeh (ی)
            .replace('\u0649', '\u06cc') // alef maksura → Persian yeh
            .replace('\u0643', '\u06a9') // Arabic kaf → Persian kaf (ک)
            .replace(digitSeparator, SEPARATOR) // protect 3.5 / 1,000 before stripping
            .replace(punctuation, "")
            .replace(whitespace, " ")
            .replace(SEPARATOR, ".")
        // "3" ≡ "three": digits normalize to their word form so a learner who
        // types the number gets credit against a word-form accepted answer (A-2).
        // A run that is glued to other digits or to a protected separator (`3.5`,
        // `9.30`) or to letters (`1st`, `30th`) is left exactly as typed.
        return stripped.replace(numberRun) { m ->
            val before = stripped.getOrNull(m.range.first - 1)
            val after = stripped.getOrNull(m.range.last + 1)
            if (glued(before) || glued(after)) m.value else numberWords[m.value] ?: m.value
        }
    }

    /** True when [c] would make a neighbouring digit run part of a bigger token. */
    private fun glued(c: Char?): Boolean =
        c != null && (c.isDigit() || c.isLetter() || c == '.' || c == ',' || c == ':')

    /** Expands contractions so `I'm hungry` equals `I am hungry` (primary reading). */
    fun expandContractions(normalized: String): String =
        contractionRegex.replace(normalized) { match -> contractions[match.value] ?: match.value }

    /**
     * Every reading of a normalized answer after expanding its contractions
     * (checklist A-2c). Unambiguous text yields a single variant; `he's been`
     * yields both `he is been` and `he has been`, so a learner may type either the
     * contraction or the reading they meant. Text with no ambiguous contraction is
     * its own single variant; the variants are cross-producted when a sentence has
     * more than one ambiguous contraction.
     */
    fun expansionVariants(normalized: String): Set<String> {
        var variants = setOf(normalized)
        for ((regex, forms) in contractionVariants) {
            if (variants.none { regex.containsMatchIn(it) }) continue
            variants = variants.flatMap { text -> forms.map { regex.replace(text, it) } }.toSet()
        }
        return variants
    }

    /** True when [answer] matches any accepted variant, contractions included. */
    fun matchesAny(accepted: List<String>, answer: String): Boolean {
        val normalized = normalize(answer)
        if (normalized.isEmpty()) return false
        val answerVariants = expansionVariants(normalized)
        return accepted.any { candidate ->
            val norm = normalize(candidate)
            norm in answerVariants || expansionVariants(norm).any { it in answerVariants }
        }
    }

    fun isCorrect(exercise: Exercise, answer: String): Boolean {
        val accepted = when (exercise) {
            is Exercise.MultipleChoice -> return exercise.options.getOrNull(exercise.correctIndex)
                ?.let { matchesAny(listOf(it), answer) } ?: false
            is Exercise.FillBlank -> exercise.accepted
            is Exercise.WordRecall -> exercise.accepted
            is Exercise.Translation -> exercise.accepted
            is Exercise.Listening -> exercise.accepted
            // P2: a produced sentence passes on an exemplar match OR on the target
            // structure, so an original correct sentence is not graded wrong just
            // for not matching the one the author happened to write.
            is Exercise.SentenceProduction ->
                return matchesAny(exercise.accepted, answer) || productionMatches(exercise, answer)
            is Exercise.Speaking -> exercise.referenceAnswers
        }
        return matchesAny(accepted, answer)
    }

    /**
     * Structural credit for [Exercise.SentenceProduction] (checklist P2).
     *
     * An answer counts when it uses **every** [Exercise.SentenceProduction.mustUse]
     * anchor in the authored order and still reaches the author's word floor. The
     * anchors are matched whole-word and contraction-expanded on both sides, so
     * an authored `didn't` is satisfied by a learner typing `did not`. Order is
     * part of the contract: a sentence that merely contains both anchor words
     * somewhere (`yesterday I didn't` for a `didn't … yesterday` target) is a
     * different structure and must not pass.
     *
     * Deliberately offline and deterministic: no model, no server, and no attempt
     * to judge whether the sentence is *good* English — it checks that the target
     * form was actually attempted, and the exemplar stays the reference answer
     * shown after a miss.
     *
     * The anchor walk alone is not enough (checklist v9 B-10): a learner can write a
     * meaningless string that merely contains the anchors in order (`usually
     * breakfast five words here`) and pass. So the structural shortcut additionally
     * requires the answer to reuse at least [PRODUCTION_MIN_EXEMPLAR_OVERLAP] of one
     * authored exemplar's words — a genuine paraphrase keeps most of the exemplar's
     * vocabulary, while a filler sentence that only carries the anchors does not. An
     * answer close enough to an exemplar is accepted by the exemplar branch anyway.
     */
    private fun productionMatches(exercise: Exercise.SentenceProduction, answer: String): Boolean {
        val words = expandContractions(normalize(answer)).split(' ').filter { it.isNotEmpty() }
        if (words.isEmpty() || words.size < exercise.minWords) return false
        val anchors = exercise.mustUse
            .flatMap { expandContractions(normalize(it)).split(' ') }
            .filter { it.isNotEmpty() }
        if (anchors.isEmpty()) return false
        // Walk the typed words once, consuming anchors in order (subsequence match).
        var cursor = 0
        for (anchor in anchors) {
            var found = -1
            for (i in cursor until words.size) {
                if (words[i] == anchor) {
                    found = i
                    break
                }
            }
            if (found < 0) return false
            cursor = found + 1
        }
        return reusesEnoughOfAnExemplar(words, exercise.accepted)
    }

    /**
     * True when the typed words cover at least [PRODUCTION_MIN_EXEMPLAR_OVERLAP] of
     * some authored exemplar's words. Words are compared after the same
     * normalization/contraction expansion as everything else, and the share is
     * measured against the *exemplar* (not the answer), so padding the answer with
     * unrelated words cannot raise the score — it can only add words the exemplar
     * does not use, which lowers the share of the target that is actually present.
     */
    private fun reusesEnoughOfAnExemplar(words: List<String>, accepted: List<String>): Boolean {
        val typed = words.toSet()
        return accepted.any { exemplar ->
            val target = expandContractions(normalize(exemplar)).split(' ').filter { it.isNotEmpty() }
            if (target.isEmpty()) return@any false
            val shared = target.count { it in typed }
            shared.toFloat() / target.size >= PRODUCTION_MIN_EXEMPLAR_OVERLAP
        }
    }

    /**
     * Grades a typed answer: exact match → CORRECT, one-edit near-miss → ALMOST.
     *
     * ALMOST is a **spelling** signal, so it is not offered where the learner was
     * asked for a form rather than a spelling (checklist A-2b): on a GRAMMAR item
     * `listen` vs `listens` is a missing `-s`, not a typo, and telling the learner
     * to "check the spelling" points at the wrong thing. Grammar items therefore
     * grade straight to WRONG — the item's own explanation says what the form
     * should have been. The review session (bare words, [grade] below) keeps the
     * near-miss hint, because there the target really is one word's spelling.
     */
    fun grade(exercise: Exercise, answer: String): Verdict = when {
        isCorrect(exercise, answer) -> Verdict.CORRECT
        skillOf(exercise) == Skill.GRAMMAR -> Verdict.WRONG
        isAlmost(exercise, answer) -> Verdict.ALMOST
        else -> Verdict.WRONG
    }

    /**
     * List-based grading for surfaces without an [Exercise] — SRS production in
     * the review session grades a bare vocabulary word the same way lessons do
     * (checklist A-2 parity: «تقریباً درست» must appear there too).
     */
    fun grade(accepted: List<String>, answer: String): Verdict = when {
        matchesAny(accepted, answer) -> Verdict.CORRECT
        isAlmost(accepted, answer) -> Verdict.ALMOST
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
            is Exercise.WordRecall -> exercise.accepted
            is Exercise.Translation -> exercise.accepted
            is Exercise.Listening -> exercise.accepted
            is Exercise.MultipleChoice -> return false
            // A produced sentence is not a spelling question, and in any case the
            // near-miss rule only ever fires on a single word (P2).
            is Exercise.SentenceProduction -> return false
            is Exercise.Speaking -> return false
        }
        return isAlmost(accepted, answer)
    }

    /** Near-miss against raw accepted variants (single word ≥ 5 letters, one edit). */
    fun isAlmost(accepted: List<String>, answer: String): Boolean {
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
        is Exercise.WordRecall -> exercise.accepted.firstOrNull().orEmpty()
        is Exercise.Translation -> exercise.accepted.firstOrNull().orEmpty()
        is Exercise.Listening -> exercise.accepted.firstOrNull().orEmpty()
        is Exercise.SentenceProduction -> exercise.accepted.firstOrNull().orEmpty()
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
        // Recalling a word from its meaning is vocabulary work, not form work.
        is Exercise.WordRecall -> exercise.skill ?: Skill.VOCABULARY
        is Exercise.Translation -> exercise.skill ?: Skill.WRITING
        is Exercise.SentenceProduction -> exercise.skill ?: Skill.WRITING
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
