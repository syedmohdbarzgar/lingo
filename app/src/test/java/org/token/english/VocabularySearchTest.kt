package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.engine.VocabularyMatch
import org.token.english.domain.engine.VocabularySearch
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.VocabularyExample
import org.token.english.domain.model.VocabularyItem

/**
 * The vocabulary browser's search.
 *
 * The rules exist because a Persian learner's keyboard and the bundle's spelling are
 * not the same keyboard: the Arabic kaf and yeh are on the Persian layout too, and
 * the zero-width non-joiner is a spelling detail a search box has no business
 * insisting on. Ranking exists because a query that hits the headword is a better
 * answer than one that hits an example sentence ten lines down.
 */
class VocabularySearchTest {

    private fun word(
        id: String,
        word: String,
        translation: String,
        examples: List<VocabularyExample> = emptyList(),
        level: LearningLevel = LearningLevel.A2,
    ) = VocabularyItem(
        id = id,
        word = word,
        translation = translation,
        definition = null,
        pronunciation = null,
        level = level,
        partOfSpeech = null,
        examples = examples,
        collocations = emptyList(),
        lessonId = null,
    )

    @Test
    fun `the Arabic kaf and yeh of a Persian keyboard find the Persian word`() {
        val book = word("b", "book", "کتاب")

        // «كتاب» — the kaf a Persian layout produces just as readily as «ک».
        assertEquals(VocabularyMatch.MEANING_PREFIX, VocabularySearch.match(book, "كتاب"))
        assertEquals(listOf(book), VocabularySearch.rank(listOf(book), "كتاب"))
        // And the yeh: «يك» and «یك» are one query.
        assertEquals("یک", VocabularySearch.normalize("يك"))
        assertEquals("یک", VocabularySearch.normalize("یك"))
    }

    @Test
    fun `the zero-width non-joiner is not something a searcher must type`() {
        val go = word("g", "go", "می‌روم")

        // Typed without the ZWNJ, which is how most keyboards produce it.
        assertEquals(VocabularyMatch.MEANING_PREFIX, VocabularySearch.match(go, "میروم"))
        assertEquals("one spelling on both sides", VocabularySearch.normalize("می‌روم"), VocabularySearch.normalize("میروم"))
        // A prefix of the joined form still lands, because the joiner is gone.
        assertEquals(VocabularyMatch.MEANING_PREFIX, VocabularySearch.match(go, "می‌ر"))
    }

    @Test
    fun `diacritics and the tatweel are not part of a search`() {
        val writing = word("w", "writing", "نوشتـن")

        assertEquals(VocabularyMatch.MEANING_PREFIX, VocabularySearch.match(writing, "نوشتن"))
        assertEquals("کتاب", VocabularySearch.normalize("كِتَاب"))
    }

    @Test
    fun `Persian digits are the same digits`() {
        val nine = word("n", "nine", "۹")

        assertEquals(VocabularyMatch.MEANING_PREFIX, VocabularySearch.match(nine, "9"))
        assertEquals("9", VocabularySearch.normalize("۹"))
    }

    @Test
    fun `the headword outranks an example, and a miss is dropped`() {
        // One query, three entries: one answers it as a headword, one only inside an
        // example sentence, one not at all.
        val headword = word("1", "book", "کتاب")
        val example = word("2", "borrow", "قرض گرفتن", listOf(VocabularyExample("I borrow a book.", "کتاب قرض می‌گیرم.")))
        val miss = word("3", "apple", "سیب")

        val ranked = VocabularySearch.rank(listOf(example, miss, headword), "book")

        // The headword first although it came last, the example last, the miss gone.
        assertEquals(listOf("book", "borrow"), ranked.map { it.word })
    }

    @Test
    fun `a token inside a multi-word headword still matches`() {
        val wake = word("w", "wake up", "بیدار شدن")

        assertEquals(VocabularyMatch.WORD_TOKEN_PREFIX, VocabularySearch.match(wake, "up"))
        assertEquals(VocabularyMatch.WORD_PREFIX, VocabularySearch.match(wake, "wake"))
    }

    @Test
    fun `an example sentence is a real hit at the end of the list`() {
        val trip = word(
            "t",
            "journey",
            "سفر",
            listOf(VocabularyExample("It was a long journey.", "سفر طولانی بود.")),
        )

        assertEquals(VocabularyMatch.EXAMPLE, VocabularySearch.match(trip, "long"))
        assertEquals(listOf(trip), VocabularySearch.rank(listOf(trip), "long"))
    }

    @Test
    fun `a blank query is no filter and a miss is no hit`() {
        val items = listOf(word("a", "apple", "سیب"), word("b", "book", "کتاب"))

        assertEquals(items, VocabularySearch.rank(items, "   "))
        assertEquals(emptyList<VocabularyItem>(), VocabularySearch.rank(items, "zzz"))
        assertEquals(VocabularyMatch.NONE, VocabularySearch.match(items.first(), "zzz"))
    }

    @Test
    fun `a level filter narrows the list to that level`() {
        val a1 = word("1", "apple", "سیب", level = LearningLevel.A1)
        val a2 = word("2", "book", "کتاب", level = LearningLevel.A2)
        val b1 = word("3", "budget", "بودجه", level = LearningLevel.B1)

        assertEquals(listOf(a1), VocabularySearch.browse(listOf(a1, a2, b1), "", LearningLevel.A1))
        // «همه» is not an empty level: it narrows nothing at all.
        assertEquals(
            listOf(a1, a2, b1),
            VocabularySearch.browse(listOf(a1, a2, b1), "", null),
        )
    }

    @Test
    fun `the filter is applied before the ranking, so a headword outside it is not a hit`() {
        // `book` is the best answer of the three to "book" — but it is an A2 word and
        // the learner asked for B1, where only `borrow` mentions it, in an example.
        val book = word("1", "book", "کتاب", level = LearningLevel.A2)
        val borrowing = word(
            "2",
            "borrow",
            "قرض گرفتن",
            listOf(VocabularyExample("I borrow a book.", "کتاب قرض می‌گیرم.")),
            level = LearningLevel.B1,
        )
        val items = listOf(book, borrowing)

        assertEquals(listOf(borrowing), VocabularySearch.browse(items, "book", LearningLevel.B1))
        assertEquals(listOf(book, borrowing), VocabularySearch.browse(items, "book", null))
    }

    @Test
    fun `the authored order is kept inside a tier`() {
        val first = word("1", "book", "کتاب")
        val second = word("2", "booklet", "جزوه")
        val third = word("3", "bookshelf", "قفسه کتاب")

        // All three are prefix hits: no ranking between them, so the list stands.
        assertEquals(
            listOf("book", "booklet", "bookshelf"),
            VocabularySearch.rank(listOf(first, second, third), "book").map { it.word },
        )
    }
}
