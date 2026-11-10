package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.core.designsystem.AppMotion
import org.token.english.core.designsystem.component.PageReveal
import org.token.english.navigation.Routes
import org.token.english.navigation.isSiblingMove

/**
 * The two motion rules a screen cannot state for itself:
 *
 * - **Which route change is a sibling.** The four bottom-bar tabs sit at the same
 *   level, so moving between them must not slide like a push — but the graph needs
 *   *both* ends of a move to know that, which is why the rule is a pure function
 *   here and a transition lambda there.
 * - **What a page reveal looks like at a given instant.** The reveal is one clock
 *   shared by the cards, so "card 3 is behind card 1" has to hold at every point of
 *   the clock — not only at the start. That property is what the previous
 *   timer-per-card version could not guarantee once a card appeared above another.
 */
class AppMotionTest {

    // ------------------------------------------------------------ navigation

    @Test
    fun `a move between two tabs is a sibling swap`() {
        assertTrue(isSiblingMove(Routes.HOME, Routes.REVIEW))
        assertTrue(isSiblingMove(Routes.REVIEW, Routes.PROGRESS))
        assertTrue(isSiblingMove(Routes.VOCABULARY, Routes.HOME))
        // Re-tapping the tab you are on is still the same level.
        assertTrue(isSiblingMove(Routes.HOME, Routes.HOME))
    }

    @Test
    fun `leaving a tab for anything deeper is not a sibling swap`() {
        assertFalse(isSiblingMove(Routes.HOME, Routes.LESSON))
        assertFalse(isSiblingMove(Routes.HOME, Routes.SETTINGS))
        assertFalse(isSiblingMove(Routes.REVIEW, Routes.PAYWALL))
        assertFalse(isSiblingMove(Routes.VOCABULARY, Routes.VOCABULARY_DETAIL))
    }

    @Test
    fun `coming back from a detail to a tab keeps its direction`() {
        // A pop is not a cross-fade: the learner went deeper, so the return slides.
        assertFalse(isSiblingMove(Routes.LESSON, Routes.HOME))
        assertFalse(isSiblingMove(Routes.CONVERSATION, Routes.HOME))
        assertFalse(isSiblingMove(Routes.SETTINGS, Routes.HOME))
    }

    @Test
    fun `the first screen has no previous route, so it never reads as a sibling`() {
        assertFalse(isSiblingMove(null, Routes.HOME))
        assertFalse(isSiblingMove(Routes.HOME, null))
        assertFalse(isSiblingMove(null, null))
    }

    // ---------------------------------------------------------------- reveal

    private fun revealAt(progress: Float) = PageReveal { progress }

    @Test
    fun `a settled page is fully opaque and in place`() {
        val settled = revealAt(1f)
        repeat(AppMotion.REVEAL_SLOTS + 3) { index ->
            assertEquals(1f, settled.alpha(index), 0.0001f)
            assertEquals(0f, settled.risePx(index, distancePx = 78f), 0.0001f)
        }
    }

    @Test
    fun `nothing is visible at the first frame`() {
        val start = revealAt(0f)
        repeat(AppMotion.REVEAL_SLOTS) { index ->
            assertEquals(0f, start.alpha(index), 0.0001f)
            assertEquals(78f, start.risePx(index, distancePx = 78f), 0.0001f)
        }
    }

    @Test
    fun `a later card never overtakes an earlier one`() {
        val clock = revealAt(0.35f)
        for (index in 1 until AppMotion.REVEAL_SLOTS) {
            assertTrue(
                "slot $index must not be further along than slot ${index - 1}",
                clock.alpha(index) <= clock.alpha(index - 1) + 0.0001f,
            )
        }
    }

    @Test
    fun `a page with more cards than slots still settles inside the bound`() {
        // Cards past the last slot share it rather than queueing for a later one, so
        // a long page cannot out-wait the clock the reveal is budgeted for.
        val settled = revealAt(1f)
        assertEquals(settled.alpha(AppMotion.REVEAL_SLOTS - 1), settled.alpha(40), 0.0001f)
        assertTrue(AppMotion.REVEAL_TOTAL_MS <= 600)
    }
}
