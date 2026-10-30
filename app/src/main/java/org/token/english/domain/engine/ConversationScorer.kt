package org.token.english.domain.engine

/**
 * Grades one recognised utterance against the practice target — pure Kotlin, no
 * Android, no Vosk: the screen feeds it text and gets a verdict back.
 *
 * Two signals, deliberately both, because neither is enough alone:
 *
 *  - **Keyword coverage** counts the target's content words present anywhere in
 *    the take, whole-word. It answers "was this an attempt at the target?" and is
 *    order-blind on purpose: a learner who says every key word in their own order
 *    is "near" instead of "retry", which is what a speaking learner needs to hear.
 *  - **Levenshtein similarity** then separates "said back as authored" from
 *    "attempted with a slip". It is order-sensitive, so it is only consulted once
 *    the content words are already in place — never as the first gate.
 *
 * The thresholds below are the product decision: 0.85 is "the sentence, said
 * back", 0.60 is "most of the sentence's content words are here". They live here,
 * not in the UI, so changing the bar is a one-line engine change with a test.
 */
object ConversationScorer {

    /** Learner said the target back: every content word is present AND the take
     *  is nearly identical to the authored sentence. */
    const val CORRECT_MIN_SIMILARITY = 0.85

    /** Share of the target's content words a take must contain to be "near"
     *  rather than "retry". Below this, it is a different sentence. */
    const val NEAR_MIN_KEYWORD_RATIO = 0.60

    fun assess(spoken: String, target: String): SpeakingVerdict {
        val normalizedSpoken = normalize(spoken)
        val normalizedTarget = normalize(target)
        if (normalizedTarget.isEmpty()) return SpeakingVerdict.CORRECT
        if (normalizedSpoken.isEmpty()) return SpeakingVerdict.RETRY

        val similarity = similarity(normalizedSpoken, normalizedTarget)
        val keywords = contentWords(normalizedTarget)
        // Whole-word membership, not substring: `contains("the")` would also hit
        // "other", quietly inflating the coverage of an unrelated take.
        val spokenWords = normalizedSpoken.split(' ').toSet()
        val hits = keywords.count { it in spokenWords }
        val keywordRatio = if (keywords.isEmpty()) 1.0 else hits.toDouble() / keywords.size

        // Two-stage verdict, content words first. Levenshtein is order-sensitive,
        // so it alone would fail a learner who says every key word in a slightly
        // different order (0.18 on a pure reorder) — exactly the stumble speaking
        // practice should forgive. Keyword coverage decides "attempt or not";
        // the edit distance only decides whether the attempt reads back intact.
        return when {
            // Every content word present AND the sentence reads back as authored.
            // "As authored" is judged two ways because either alone fails a real
            // learner: whole-sentence similarity punishes dropping an article
            // ("the bank is on the left" vs "bank is on the left" is 0.81 on a
            // short sentence, below any sane bar), while content-word order
            // punishes a pure reorder at the same level as a wrong word. A take
            // that satisfies either one, with every content word present, is the
            // target said back — the differences left are filler and articles.
            keywordRatio >= 1.0 &&
                (contentWordsInOrder(normalizedSpoken, keywords) ||
                    similarity >= CORRECT_MIN_SIMILARITY) -> SpeakingVerdict.CORRECT
            // Enough content words to be unmistakably the same sentence — order,
            // hesitation and a slipped word are all forgiven here.
            keywordRatio >= NEAR_MIN_KEYWORD_RATIO -> SpeakingVerdict.NEAR
            else -> SpeakingVerdict.RETRY
        }
    }

    /**
     * True when [keywords] can be found **in the target's order** inside the take,
     * ignoring every other word. Hesitations ("um"), fillers ("okay") and dropped
     * articles cannot move the needle; swapping two content words can.
     *
     * The test is subsequence, not equality: speech repeats itself — a false start
     * ("go straight … go straight and turn left") or a recogniser re-emission puts
     * a word in twice, and that learner is saying the target, not a different one.
     * A reorder still fails, because the authored order cannot be found at all.
     */
    internal fun contentWordsInOrder(spoken: String, keywords: List<String>): Boolean {
        if (keywords.isEmpty()) return false
        val wanted = keywords.toSet()
        var next = 0
        for (word in spoken.split(' ')) {
            if (word in wanted && next < keywords.size && word == keywords[next]) next++
        }
        return next == keywords.size
    }

    /** 1.0 = identical, 0.0 = unrelated. Symmetric, so word order still matters. */
    fun similarity(a: String, b: String): Double {
        if (a == b) return 1.0
        val distance = levenshtein(a, b)
        val longest = maxOf(a.length, b.length)
        return if (longest == 0) 1.0 else 1.0 - distance.toDouble() / longest
    }

    /**
     * Lowercased, contractions expanded, punctuation stripped, whitespace
     * collapsed — so "I'm going to visit my family." and "i am going to visit my
     * family" compare as the same sentence. Latin only: the take is English and a
     * Persian character slipping in should count as noise, not as content.
     *
     * Contraction expansion is a fairness rule, not a nicety. A recogniser is free
     * to return either form ("I'm" / "I am"), and a learner is *supposed* to speak
     * the contracted one — comparing them literally docked a correct answer for
     * writing style. Both sides go through this function, so the two forms meet in
     * the middle. Curly apostrophes arrive from the speech model as often as the
     * ASCII one, so they are unified first.
     */
    internal fun normalize(text: String): String =
        text.lowercase()
            .replace(Regex("[\u2018\u2019`\u00b4]"), "'")
            .split(Regex("\\s+"))
            .map { word -> word.replace(Regex("[^a-z0-9']"), "") }
            .filter { it.isNotEmpty() }
            .flatMap { word -> CONTRACTIONS[word]?.split(' ') ?: listOf(word) }
            .joinToString(" ")

    /** Expanded forms, both sides of the comparison. */
    private val CONTRACTIONS = mapOf(
        "i'm" to "i am", "i've" to "i have", "i'll" to "i will", "i'd" to "i would",
        "you're" to "you are", "you've" to "you have", "you'll" to "you will",
        "you'd" to "you would", "he's" to "he is", "she's" to "she is",
        "it's" to "it is", "we're" to "we are", "we've" to "we have",
        "we'll" to "we will", "we'd" to "we would", "they're" to "they are",
        "they've" to "they have", "they'll" to "they will", "they'd" to "they would",
        "that's" to "that is", "there's" to "there is", "what's" to "what is",
        "who's" to "who is", "let's" to "let us",
        "don't" to "do not", "doesn't" to "does not", "didn't" to "did not",
        "isn't" to "is not", "aren't" to "are not", "wasn't" to "was not",
        "weren't" to "were not", "can't" to "cannot", "won't" to "will not",
        "wouldn't" to "would not", "shouldn't" to "should not",
        "couldn't" to "could not", "haven't" to "have not", "hasn't" to "has not",
        "hadn't" to "had not",
    )

    /**
     * Content words of the target — everything but a short stoplist — used for
     * the keyword signal. A bare function so a test can pin it directly.
     */
    internal fun contentWords(target: String): List<String> =
        normalize(target)
            .split(' ')
            .filter { it.isNotEmpty() && it !in STOP_WORDS }

    private fun levenshtein(a: String, b: String): Int {
        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length
        // Two rows only — the full matrix is O(n·m) memory for one number.
        var previous = IntArray(b.length + 1) { it }
        var current = IntArray(b.length + 1)
        for (i in a.indices) {
            current[0] = i + 1
            for (j in b.indices) {
                val cost = if (a[i] == b[j]) 0 else 1
                current[j + 1] = minOf(
                    current[j] + 1,        // insertion
                    previous[j + 1] + 1,    // deletion
                    previous[j] + cost,     // substitution
                )
            }
            // Swap the two rows by reference — no allocation in the hot loop.
            val done = previous
            previous = current
            current = done
        }
        return previous[b.length]
    }

    private val STOP_WORDS = setOf(
        "a", "an", "the", "and", "or", "to", "of", "in", "on", "at", "is", "are",
        "was", "were", "be", "it", "that", "this", "with", "for", "as", "by", "near",
    )
}

/** Feedback shown to the learner — the three states the practice screen renders. */
enum class SpeakingVerdict { CORRECT, NEAR, RETRY }
