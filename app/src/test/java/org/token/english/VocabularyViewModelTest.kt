package org.token.english

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.VocabularyItem
import org.token.english.feature.vocabulary.VocabularyViewModel

/**
 * VocabularyViewModel tests (checklist v9 B-6 — the browser had none). The
 * repository is an in-memory fake, so the search rules the learner depends on
 * (which field matches, case folding, and what an empty result looks like) are
 * exercised without Room or a device.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class VocabularyViewModelTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun word(id: String, word: String, translation: String) = VocabularyItem(
        id = id,
        word = word,
        translation = translation,
        definition = null,
        pronunciation = null,
        level = LearningLevel.A1,
        partOfSpeech = null,
        examples = emptyList(),
        collocations = emptyList(),
        lessonId = null,
    )

    private fun harness(vararg words: VocabularyItem): VocabularyViewModel =
        VocabularyViewModel(vocabularyRepository = FakeVocabularyRepository(words.toList()))

    @Test
    fun `every word is loaded and the spinner stops`() {
        val vm = harness(word("a", "apple", "سیب"), word("b", "book", "کتاب"))

        assertFalse(vm.state.value.isLoading)
        assertEquals(2, vm.state.value.all.size)
        assertEquals(listOf("apple", "book"), vm.state.value.filtered.map { it.word })
    }

    @Test
    fun `a query matches the English word, case-insensitively`() {
        val vm = harness(word("a", "apple", "سیب"), word("b", "book", "کتاب"))

        vm.onQueryChange("APP")

        assertEquals(listOf("apple"), vm.state.value.filtered.map { it.word })
    }

    @Test
    fun `a query matches the Persian meaning too`() {
        val vm = harness(word("a", "apple", "سیب"), word("b", "book", "کتاب"))

        vm.onQueryChange("کتاب")

        assertEquals(listOf("book"), vm.state.value.filtered.map { it.word })
    }

    @Test
    fun `clearing the query restores every word`() {
        val vm = harness(word("a", "apple", "سیب"), word("b", "book", "کتاب"))

        vm.onQueryChange("app")
        assertEquals(1, vm.state.value.filtered.size)

        vm.onQueryChange("   ")
        assertEquals("a blank query is no filter", 2, vm.state.value.filtered.size)
    }

    @Test
    fun `a query with no matches is an empty list, never an error`() {
        val vm = harness(word("a", "apple", "سیب"))

        vm.onQueryChange("zzz")

        assertTrue("no matches is a valid, quiet state", vm.state.value.filtered.isEmpty())
        assertEquals("the full list is kept so clearing the filter works", 1, vm.state.value.all.size)
    }
}
