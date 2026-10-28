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
     * True when [keywords] appear in the take in the target's order, ignoring
     * every other word. Hesitations ("um"), fillers ("okay") and dropped articles
     * cannot move the needle; swapping two content words can.
     */
    internal fun contentWordsInOrder(spoken: String, keywords: List<String>): Boolean {
        if (keywords.isEmpty()) return false
        val wanted = keywords.toSet()
        val saidInOrder = spoken.split(' ').filter { it in wanted }
        return saidInOrder == keywords
    }

    /** 1.0 = identical, 0.0 = unrelated. Symmetric, so word order still matters. */
    fun similarity(a: String, b: String): Double {
        if (a == b) return 1.0
        val distance = levenshtein(a, b)
        val longest = maxOf(a.length, b.length)
        return if (longest == 0) 1.0 else 1.0 - distance.toDouble() / longest
    }

    /**
     * Lowercased, punctuation stripped, whitespace collapsed — so "Bank is on
     * the left, next to the pharmacy." and "bank is on the left next to the
     * pharmacy" compare as the same sentence. Latin only: the take is English and
     * a Persian character slipping in should count as noise, not as content.
     */
    internal fun normalize(text: String): String =
        text.lowercase()
            .replace(Regex("[^a-z0-9\\s]+"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

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
