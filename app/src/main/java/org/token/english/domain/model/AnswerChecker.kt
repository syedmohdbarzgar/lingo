package org.token.english.domain.model

/**
 * Normalizes and compares free-text answers (fill-blank / translation / listening
 * / review production). Pure Kotlin — unit tested. Comparison is case-insensitive,
 * punctuation-tolerant, Persian/Arabic character-normalizing,
 * contraction-tolerant (`I'm` ≡ `I am`, curly `’` ≡ straight `'`) and
 * dialect-tolerant (`neighbour` ≡ `neighbor`, `practise` ≡ `practice`), so a
 * learner is never marked wrong for spelling a word the way their own variety of
 * English does (checklist P1-4).
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
     * sentence must reuse (checklist v9 B-10). `internal` so `DocCodeConsistencyTest`
     * can pin `docs/debt.md` to the value the app actually grades with (checklist
     * v15 B-12 — the doc drifted to "60%" while the code said 0.5).
     * 0.5 lets a real paraphrase through
     * (swapping a pronoun or a synonym keeps most words) while rejecting a filler
     * sentence that merely carries the anchors. Anchors are never adjacency-checked
     * because the authored `mustUse` pairs are not adjacent in their own exemplars
     * (`usually … have breakfast`, `if … we will`).
     */
    internal const val PRODUCTION_MIN_EXEMPLAR_OVERLAP = 0.5f

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

    /**
     * British → American spellings, folded onto one canonical form (checklist P1-4).
     *
     * The content teaches British English (`neighbour`, `practise`, `favourite`,
     * `grey`), so an American learner who spelled the same word the way their own
     * course taught it was graded **wrong** — a correct answer marked as a mistake,
     * which is the one thing a grader must never do. Both sides are folded onto the
     * American form here, so the equivalence is symmetric and does not depend on
     * which spelling the author happened to write.
     *
     * Deliberately a **curated table**, not a rule: a blanket `-ise` → `-ize`
     * rewrite would also mangle `exercise`, `advise`, `surprise`, `promise` and
     * `raise`, none of which are `-ize` verbs in any dialect. Every entry is a pair
     * with the same meaning, so folding two of them together can only accept a
     * correct answer — never a different word. Inflected forms are listed explicitly
     * because the right inflection is not always base + suffix (`practise` + `ing` is
     * `practicing`, not `practiceing`). Adding a dialect pair a lesson ships is one
     * line here.
     */
    private val dialectCanonical: Map<String, String> = mapOf(
        // -our → -or
        "colour" to "color", "colours" to "colors", "coloured" to "colored",
        "colourful" to "colorful", "colouring" to "coloring",
        "favour" to "favor", "favours" to "favors", "favoured" to "favored",
        "favourite" to "favorite", "favourites" to "favorites",
        "behaviour" to "behavior", "behaviours" to "behaviors",
        "flavour" to "flavor", "flavours" to "flavors",
        "honour" to "honor", "honours" to "honors", "honoured" to "honored",
        "humour" to "humor", "labour" to "labor",
        "neighbour" to "neighbor", "neighbours" to "neighbors",
        "neighbourhood" to "neighborhood", "neighbourhoods" to "neighborhoods",
        "rumour" to "rumor", "rumours" to "rumors",
        "harbour" to "harbor", "armour" to "armor", "vapour" to "vapor",
        "endeavour" to "endeavor",
        // -re → -er
        "centre" to "center", "centres" to "centers",
        "theatre" to "theater", "theatres" to "theaters",
        "metre" to "meter", "metres" to "meters",
        "litre" to "liter", "litres" to "liters",
        "fibre" to "fiber", "fibres" to "fibers", "sombre" to "somber",
        // -ce → -se
        "defence" to "defense", "defences" to "defenses",
        "offence" to "offense", "offences" to "offenses",
        "pretence" to "pretense", "licence" to "license", "licences" to "licenses",
        // practise (BrE verb) / practice (AmE verb and noun)
        "practise" to "practice", "practises" to "practices",
        "practised" to "practiced", "practising" to "practicing",
        // -ise → -ize, only where -ise really is the British spelling
        "organise" to "organize", "organises" to "organizes",
        "organised" to "organized", "organising" to "organizing",
        "organisation" to "organization", "organisations" to "organizations",
        "realise" to "realize", "realises" to "realizes",
        "realised" to "realized", "realising" to "realizing",
        "recognise" to "recognize", "recognises" to "recognizes",
        "recognised" to "recognized", "recognising" to "recognizing",
        "apologise" to "apologize", "apologised" to "apologized",
        "criticise" to "criticize", "criticised" to "criticized",
        "emphasise" to "emphasize", "emphasised" to "emphasized",
        "memorise" to "memorize", "memorised" to "memorized",
        "summarise" to "summarize", "summarised" to "summarized",
        "prioritise" to "prioritize", "prioritised" to "prioritized",
        "specialise" to "specialize", "specialised" to "specialized",
        "standardise" to "standardize", "standardised" to "standardized",
        "visualise" to "visualize", "visualised" to "visualized",
        "minimise" to "minimize", "minimised" to "minimized",
        "maximise" to "maximize", "maximised" to "maximized",
        "normalise" to "normalize", "normalised" to "normalized",
        "socialise" to "socialize", "socialised" to "socialized",
        "sympathise" to "sympathize", "sympathised" to "sympathized",
        "characterise" to "characterize", "characterised" to "characterized",
        "categorise" to "categorize", "categorised" to "categorized",
        "mobilise" to "mobilize", "mobilised" to "mobilized",
        "stabilise" to "stabilize", "stabilised" to "stabilized",
        "utilise" to "utilize", "utilised" to "utilized",
        "globalise" to "globalize", "globalised" to "globalized",
        "industrialise" to "industrialize", "industrialised" to "industrialized",
        // -yse → -yze
        "analyse" to "analyze", "analyses" to "analyzes",
        "analysed" to "analyzed", "analysing" to "analyzing",
        "paralyse" to "paralyze", "paralysed" to "paralyzed",
        "catalyse" to "catalyze", "catalysed" to "catalyzed",
        // doubled consonant → single
        "travelled" to "traveled", "travelling" to "traveling",
        "traveller" to "traveler", "travellers" to "travelers",
        "cancelled" to "canceled", "cancelling" to "canceling",
        "modelled" to "modeled", "modelling" to "modeling",
        "labelled" to "labeled", "labelling" to "labeling",
        "signalled" to "signaled", "signalling" to "signaling",
        "fuelled" to "fueled", "fuelling" to "fueling",
        "levelled" to "leveled", "levelling" to "leveling",
        "marvellous" to "marvelous", "jewellery" to "jewelry",
        // everything else
        "grey" to "gray", "greyer" to "grayer", "greyest" to "grayest",
        "programme" to "program", "programmes" to "programs",
        "catalogue" to "catalog", "catalogues" to "catalogs",
        "judgement" to "judgment", "judgements" to "judgments",
        "acknowledgement" to "acknowledgment",
        "aluminium" to "aluminum",
        "sceptical" to "skeptical", "sceptic" to "skeptic",
        "manoeuvre" to "maneuver", "manoeuvres" to "maneuvers",
        "plough" to "plow", "ploughs" to "plows",
        "tyre" to "tire", "tyres" to "tires",
        "kerb" to "curb", "kerbs" to "curbs",
        "cheque" to "check", "cheques" to "checks",
        "cosy" to "cozy", "moustache" to "mustache", "pyjamas" to "pajamas",
        "aeroplane" to "airplane", "aeroplanes" to "airplanes",
        "storey" to "story", "storeys" to "stories",
    )

    /**
     * The table's canonical (US) form of one already-normalized word, or the word
     * itself when the table does not know it. Exposed so every grader that compares
     * English text — typed answers here, the recogniser's transcript in
     * [org.token.english.domain.engine.ConversationScorer] — folds the same pairs
     * from the same list instead of each keeping its own idea of "the same word".
     */
    fun unifyDialect(word: String): String = dialectCanonical[word] ?: word

    /**
     * Every spelling of [normalized] under the US/UK table: the text as typed plus
     * each variant with one dialect word canonicalised.
     *
     * Word-by-word rather than regex-by-regex so the cross-product stays small (a
     * sentence carries at most a couple of dialect words); a token the table does
     * not know — including a protected decimal like `3.5` — is left untouched.
     */
    private fun dialectVariants(normalized: String): Set<String> {
        val words = normalized.split(' ')
        if (words.none { it in dialectCanonical }) return setOf(normalized)
        var variants = listOf(words)
        words.forEachIndexed { index, word ->
            val canonical = dialectCanonical[word] ?: return@forEachIndexed
            variants = variants.flatMap { variant ->
                listOf(variant, variant.toMutableList().also { it[index] = canonical })
            }
        }
        return variants.map { it.joinToString(" ") }.toSet()
    }

    /** Every reading of an answer: contractions expanded, dialect spellings folded. */
    private fun readings(normalized: String): Set<String> =
        expansionVariants(normalized).flatMapTo(mutableSetOf()) { dialectVariants(it) }

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

    /**
     * True when [answer] matches any accepted variant — contractions expanded on
     * both sides, and US/UK spellings folded together (checklist P1-4).
     */
    fun matchesAny(accepted: List<String>, answer: String): Boolean {
        val normalized = normalize(answer)
        if (normalized.isEmpty()) return false
        val answerVariants = readings(normalized)
        return accepted.any { candidate ->
            val norm = normalize(candidate)
            norm in answerVariants || readings(norm).any { it in answerVariants }
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
            // A text is graded on the shape it was asked for (a floor of words and
            // sentences) plus its anchors, anywhere; an exemplar match is the shortcut
            // an answer that reads back as authored takes.
            is Exercise.Writing ->
                return matchesAny(exercise.accepted, answer) || writingMatches(exercise, answer)
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
     * Structural credit for [Exercise.Writing] — a short connected text.
     *
     * Three conditions, all of them checkable without a model: every
     * [Exercise.Writing.mustUse] anchor appears **somewhere** in the answer, the
     * answer reaches [Exercise.Writing.minWords], and it is at least
     * [Exercise.Writing.minSentences] finished sentences. The last one is what makes
     * this a text rather than a sentence: a paragraph's connective may open the
     * second sentence, so anchor order is not part of the contract — but a single
     * run-on that happens to carry every anchor and enough words is not two
     * sentences and does not pass.
     *
     * A sentence is counted by its terminator (`.` `!` `?`), and a final clause with
     * no punctuation still counts as a sentence once it carries a word — a learner
     * who forgets the last full stop has written two sentences, not one, and the
     * answer should not fail for punctuation. It is otherwise deliberately not a
     * judge of prose: nothing here decides whether the text is *good*.
     */
    private fun writingMatches(exercise: Exercise.Writing, answer: String): Boolean {
        val words = expandContractions(normalize(answer)).split(' ').filter { it.isNotEmpty() }
        if (words.isEmpty() || words.size < exercise.minWords) return false
        if (sentencesIn(answer) < exercise.minSentences) return false
        val anchors = exercise.mustUse
            .flatMap { expandContractions(normalize(it)).split(' ') }
            .filter { it.isNotEmpty() }
        if (anchors.isEmpty()) return false
        // Anchor space as a set: a text may use its structures in any order.
        val present = words.toSet()
        return anchors.all { it in present }
    }

    /**
     * Finished sentences in a text: a terminator ends one, and the trailing words
     * after the last terminator are one more if they say anything.
     */
    internal fun sentencesIn(text: String): Int {
        val terminators = text.count { it == '.' || it == '!' || it == '?' }
        val tail = text.trimEnd().lastOrNull()?.let { it !in ".!?" } ?: false
        return terminators + if (tail && normalize(text).isNotEmpty()) 1 else 0
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
            // A text is not a spelling question, and the near-miss rule only ever
            // fires on a single word (the same reason a production is excluded).
            is Exercise.Writing -> return false
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
        is Exercise.Writing -> exercise.accepted.firstOrNull().orEmpty()
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
        is Exercise.Writing -> exercise.skill ?: Skill.WRITING
        is Exercise.Listening -> exercise.skill ?: Skill.LISTENING
        is Exercise.Speaking -> exercise.skill ?: Skill.SPEAKING
    }

    /**
     * The format an exercise is answered in — the evidence unit of the learner
     * model's "confirmed in more than one way" (checklist 2.1). One mapping, next to
     * [skillOf], so the writer of the attempt and any reader agree on the label.
     */
    fun formatOf(exercise: Exercise): ExerciseFormat = when (exercise) {
        is Exercise.MultipleChoice -> ExerciseFormat.MULTIPLE_CHOICE
        is Exercise.FillBlank -> ExerciseFormat.FILL_BLANK
        is Exercise.WordRecall -> ExerciseFormat.WORD_RECALL
        is Exercise.Translation -> ExerciseFormat.TRANSLATION
        is Exercise.SentenceProduction -> ExerciseFormat.SENTENCE_PRODUCTION
        is Exercise.Writing -> ExerciseFormat.WRITING_TEXT
        is Exercise.Listening -> ExerciseFormat.LISTENING
        is Exercise.Speaking -> ExerciseFormat.SPEAKING
    }

    private fun heuristicMc(exercise: Exercise.MultipleChoice): Skill {
        val hasPersianOption = exercise.options.any { o -> o.any { it in '\u0600'..'\u06ff' } }
        if (hasPersianOption) return Skill.VOCABULARY
        if (exercise.question.contains("______") || exercise.question.contains("___")) return Skill.GRAMMAR
        if (exercise.question.contains("sentence", ignoreCase = true)) return Skill.GRAMMAR
        return Skill.VOCABULARY
    }
}
