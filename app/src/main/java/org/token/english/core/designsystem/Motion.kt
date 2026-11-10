package org.token.english.core.designsystem

/**
 * The app's motion tokens — durations and the reveal shape, in one place.
 *
 * They used to be scattered: the navigation slide had a private `NAV_SLIDE_MS` in
 * the graph, the card reveal had `240`/`320`/`55` inside `StaggeredReveal`, and the
 * splash hand-off had its own `300`. Nothing was wrong with the numbers themselves;
 * what was wrong is that "how long does this app take to move" could only be
 * answered by reading four files, so a screen added later picked its own.
 *
 * The rule these numbers encode ([docs/design-direction.md]): motion explains a
 * state change, it never delays the task. Nothing here is above ~0.35s, and the
 * whole reveal of an opening page is bounded by [REVEAL_TOTAL_MS] no matter how many
 * cards it has.
 */
object AppMotion {

    /** A route change — push or pop. Long enough to read as travel, short enough to ignore. */
    const val SCREEN_MS = 260

    /** A change *inside* a screen (lesson step, next review card): a hint, not a journey. */
    const val STEP_MS = 180

    /** How far an in-screen change moves, in dp. Small on purpose: the content carries it. */
    const val STEP_SHIFT_DP = 8

    /** The splash → Home fade. */
    const val SPLASH_MS = 300

    /** Delay between two cards of an opening page. */
    const val REVEAL_STEP_MS = 40

    /** One card's fade and its rise, and how far it rises. */
    const val REVEAL_FADE_MS = 240
    const val REVEAL_RISE_MS = 300
    const val REVEAL_RISE_DP = 26

    /**
     * Slots the reveal is spread over. A page with more cards than this does not
     * keep waiting: the extra cards ride the last slot, so the page always settles
     * in [REVEAL_TOTAL_MS] — the number a screen can be read at, not the number of
     * cards it happens to have.
     */
    const val REVEAL_SLOTS = 8

    /** The bound on the whole page reveal: the last slot's step plus one card's rise. */
    const val REVEAL_TOTAL_MS = (REVEAL_SLOTS - 1) * REVEAL_STEP_MS + REVEAL_RISE_MS
}
