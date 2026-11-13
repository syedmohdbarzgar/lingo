package org.token.english.domain.engine

import org.token.english.domain.model.VocabularyItem

/**
 * How well an entry answers the query — the ranking key, best first.
 *
 * The browser used to filter with two `contains` calls on the raw strings, so
 * "book" found "book" and "كتاب" (the Arabic kaf a Persian keyboard produces just as
 * readily as the Persian one) found **nothing**, while the 1154-entry word list sat
 * there. A learner searching their own language should not have to type it the way
 * the bundle happens to spell it.
 */
enum class VocabularyMatch {
    /** The English headword itself starts with the query (`app` → `apple`). */
    WORD_PREFIX,

    /** A word *inside* the headword starts with it (`up` → `wake up`). */
    WORD_TOKEN_PREFIX,

    /** The headword contains the query anywhere (`oak` → `croak`). */
    WORD_CONTAINS,

    /** The Persian meaning starts with the query. */
    MEANING_PREFIX,

    /** The Persian meaning contains it. */
    MEANING_CONTAINS,

    /** Only an example sentence does — still a real hit, at the bottom. */
    EXAMPLE,

    NONE,
}

/**
 * The vocabulary browser's search: one normalisation, one ranking, no Room.
 *
 * Pure so the rules can be tested (and so the same fold can serve any other surface
 * that searches authored text). Two things it deliberately does **not** do: it does
 * not stem English (`went` does not find `go` — the headwords are what they are), and
 * it does not rank by level, because a learner looking up a word wants the word, not
 * a curriculum order.
 */
object VocabularySearch {

    /**
     * Folds every spelling of the same letter onto one form, so a query and the
     * bundle meet in the middle:
     *
     *  - Arabic ك (U+0643) → Persian ک, Arabic ي/ى (U+064A/U+0649) → ی, ة → ه, ۀ → ه,
     *    because a Persian keyboard produces either and both are the same letter to a
     *    reader;
     *  - أ إ آ → ا, ؤ → و, ئ → ی, the same characters without their hamza;
     *  - the zero-width non-joiner and the joining dash are **removed**, not spaced:
     *    «می‌روم» and «میروم» are one word to a learner, and removing keeps both on
     *    the same side of a contains-check (a space would make neither find the other);
     *  - Arabic diacritics (harakat) and the tatweel are stripped — nobody types them;
     *  - Persian (۰-۹) and Arabic-Indic (٠-٩) digits become ASCII digits;
     *  - lowercased, whitespace collapsed.
     */
    fun normalize(text: String): String = buildString(text.length) {
        for (raw in text) {
            when (val ch = FOLD[raw] ?: raw) {
                '\u200C', '\u200D', '\u200E', '\u200F', '\u0640', ' ', '\t', '\n', '\r' -> {
                    // ZWNJ/ZWJ/bidi marks/tatweel vanish; whitespace becomes one space.
                    if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') append(' ')
                }
                in '\u064B'..'\u0652', '\u0670' -> Unit
                in '\u06F0'..'\u06F9' -> append('0' + (ch - '\u06F0'))
                in '\u0660'..'\u0669' -> append('0' + (ch - '\u0660'))
                else -> append(ch.lowercaseChar())
            }
        }
    }
        .replace(Regex(" +"), " ")
        .trim()

    /** Where [item] answers [query], or [VocabularyMatch.NONE]. */
    fun match(item: VocabularyItem, query: String): VocabularyMatch {
        val q = normalize(query)
        if (q.isEmpty()) return VocabularyMatch.NONE

        val word = normalize(item.word)
        when {
            word.startsWith(q) -> return VocabularyMatch.WORD_PREFIX
            word.split(' ', '-').any { token -> token.isNotEmpty() && token.startsWith(q) } ->
                return VocabularyMatch.WORD_TOKEN_PREFIX
            word.contains(q) -> return VocabularyMatch.WORD_CONTAINS
        }

        val meaning = normalize(item.translation)
        when {
            meaning.startsWith(q) -> return VocabularyMatch.MEANING_PREFIX
            meaning.contains(q) -> return VocabularyMatch.MEANING_CONTAINS
        }

        // An example is the last place to look, and the last thing shown: a word whose
        // own text does not contain the query is a weaker answer than one whose does.
        val inExample = item.examples.any { example ->
            normalize(example.en).contains(q) || normalize(example.fa).contains(q)
        }
        return if (inExample) VocabularyMatch.EXAMPLE else VocabularyMatch.NONE
    }

    /**
     * The entries that answer [query], best match first and the authored order kept
     * inside a tier. A blank query is not a filter: it returns [items] untouched, in
     * the order the browser already had.
     */
    fun rank(items: List<VocabularyItem>, query: String): List<VocabularyItem> {
        if (query.isBlank()) return items
        return items
            .mapNotNull { item ->
                match(item, query).takeIf { it != VocabularyMatch.NONE }?.let { item to it }
            }
            // `sortedBy` is stable, so two entries of the same tier keep their order.
            .sortedBy { it.second.ordinal }
            .map { it.first }
    }

    /** Letters that are one letter to a Persian reader, folded to the bundle's spelling. */
    private val FOLD: Map<Char, Char> = mapOf(
        '\u0643' to '\u06A9', // ك → ک
        '\u064A' to '\u06CC', // ي → ی
        '\u0649' to '\u06CC', // ى → ی
        '\u0629' to '\u0647', // ة → ه
        '\u06C0' to '\u0647', // ۀ → ه
        '\u0623' to '\u0627', // أ → ا
        '\u0625' to '\u0627', // إ → ا
        '\u0622' to '\u0627', // آ → ا
        '\u0624' to '\u0648', // ؤ → و
        '\u0626' to '\u06CC', // ئ → ی
    )
}
