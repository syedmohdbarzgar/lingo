package org.token.english.domain.engine

/** One word of the model sentence, and whether the take contained it. */
data class WordMark(val word: String, val heard: Boolean)

/**
 * What a take did to one model sentence, word by word.
 *
 * [marks] follows the model sentence's own order, so a screen can show it as is;
 * [missed] is the shorter list a line of copy needs — content words only, because
 * "the" and "a" are not what a speaking learner has to fix.
 */
data class WordFeedback(
    val marks: List<WordMark>,
    val missed: List<String>,
) {
    val heardCount: Int get() = marks.count { it.heard }

    val isBlank: Boolean get() = marks.isEmpty()

    companion object {
        val NONE = WordFeedback(emptyList(), emptyList())
    }
}

/**
 * Word-level feedback on a spoken take — pure Kotlin, no recogniser, no Android.
 *
 * The verdict ([ConversationScorer]) answers "did this land?" with one of three
 * words. That is the right answer for evidence and the wrong one for a learner who
 * wants to know *which* word went missing: «نزدیک بود» on a five-word sentence
 * still leaves them re-listening to their own take hunting for the gap.
 *
 * The matching rule is deliberately the scorer's own — same normalisation, same
 * contraction expansion, same dialect and homophone fold, same one-edit tolerance
 * — because feedback that disagreed with the verdict would be a second, quieter
 * grader. What it adds is **one-to-one consumption**: each word of the take can
 * satisfy only one word of the model, so `very very good` said once leaves one
 * `very` unheard instead of both looking satisfied by a single utterance.
 *
 * Order is *not* required. The scorer forgives a learner who says every key word
 * in their own order (that is what makes it "near" instead of "retry"), so the mark
 * for a word must not depend on where it appeared — otherwise the same take would
 * be called near and shown as missing words at the same time.
 */
object SpokenWordFeedback {

    /**
     * Marks [target] against [spoken]. An empty target has nothing to mark; an
     * empty take marks every word unheard, because a take with no words heard
     * nothing — the screen decides whether that is worth showing.
     */
    fun of(spoken: String, target: String): WordFeedback {
        val targetWords = wordsOf(ConversationScorer.normalize(target))
        if (targetWords.isEmpty()) return WordFeedback.NONE

        // A mutable pool of what the device heard: matching removes a word, which
        // is what makes the consumption one-to-one.
        val heard = wordsOf(ConversationScorer.normalize(spoken))
            .map(ConversationScorer::foldHomophone)
            .toMutableList()

        val marks = targetWords.map { word ->
            val wanted = ConversationScorer.foldHomophone(word)
            val at = heard.indexOfFirst { said -> ConversationScorer.sameWord(said, wanted) }
            if (at >= 0) {
                heard.removeAt(at)
                WordMark(word, heard = true)
            } else {
                WordMark(word, heard = false)
            }
        }

        val missed = marks
            .filterNot { it.heard }
            // The scorer's own notion of content: a missing article is not a miss
            // to act on, and the verdict already forgives it.
            .filter { ConversationScorer.isContentWord(it.word) }
            .map { it.word }
            .distinct()

        return WordFeedback(marks, missed)
    }

    private fun wordsOf(text: String): List<String> = text.split(' ').filter { it.isNotEmpty() }
}
