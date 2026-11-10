package org.token.english.core.designsystem.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import org.token.english.core.designsystem.AppMotion

/**
 * A gentle entrance for a list of cards.
 *
 * The page used to assemble itself fully formed, so every card landed in the same
 * frame — which is what makes a page feel like a wall of paper being unfolded at
 * once. Each card now fades and rises into place, [AppMotion.REVEAL_STEP_MS] after
 * the one above it.
 *
 * **One clock, not one timer per card.** The first version gave every card its own
 * `LaunchedEffect` + `delay(index × 55)` + two `animateFloatAsState`s. That is 2N
 * animatables and N coroutines for one page, and worse, the delay was captured on
 * first composition: when a card appeared above an existing one (the plan or the
 * focus card arriving a moment later) the slots below kept the timing of the old
 * layout, so the page could reveal out of order. Here a single [Animatable] runs the
 * whole reveal and every card reads its own window out of it, so the order is a
 * property of the layout rather than of when each card started.
 *
 * The motion lives in `graphicsLayer`, not in a visibility wrapper: a card that
 * animates in still occupies its final slot from the first frame, so nothing jumps,
 * no spacing appears where a hidden card would have been, and the scroll range never
 * changes mid-animation. Nothing depends on the animation to be shown — the end
 * state is the only state after [AppMotion.REVEAL_TOTAL_MS].
 */
class PageReveal internal constructor(private val progress: () -> Float) {

    /**
     * This slot's opacity. Read inside `graphicsLayer { }` so a running reveal
     * re-runs the layer block only — never a recomposition, and never a re-layout.
     */
    fun alpha(index: Int): Float {
        val slot = window(index)
        return FastOutSlowInEasing.transform(slot).coerceIn(0f, 1f)
    }

    /** How far this slot still has to rise, in px (0 when settled). */
    fun risePx(index: Int, distancePx: Float): Float = (1f - alpha(index)) * distancePx

    /**
     * This slot's 0..1 progress: it starts at its own offset in the shared clock and
     * finishes one fade-length later. Slots past [AppMotion.REVEAL_SLOTS] share the
     * last window instead of queueing for a later one, which is what bounds the page.
     */
    private fun window(index: Int): Float {
        val slot = index.coerceIn(0, AppMotion.REVEAL_SLOTS - 1)
        val startMs = slot * AppMotion.REVEAL_STEP_MS
        val elapsedMs = progress() * AppMotion.REVEAL_TOTAL_MS - startMs
        return elapsedMs / AppMotion.REVEAL_FADE_MS
    }
}

/**
 * Starts the page reveal and returns the handle every card reads from.
 *
 * Call it once per screen (not once per card): the returned [PageReveal] is the one
 * clock the cards share.
 */
@Composable
fun rememberPageReveal(): PageReveal {
    val clock = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        // Linear master clock: each card's own easing is applied when it reads its
        // window, so the stagger is even instead of easing eight times in a row.
        clock.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = AppMotion.REVEAL_TOTAL_MS,
                easing = LinearEasing,
            ),
        )
    }
    return remember(clock) { PageReveal { clock.value } }
}

/**
 * A change *inside* a screen — the lesson's next step, the next review card, the
 * next question.
 *
 * These used to swap in a single frame: the content was a `when` over the current
 * step, so finishing a teaching page and landing on the exercise was a hard cut.
 * The route had a transition, the content inside it had none, which is why moving
 * between screens felt animated and moving within one felt broken.
 *
 * Short on purpose ([AppMotion.STEP_MS]) and small ([AppMotion.STEP_SHIFT_DP]): a
 * step change is a hint that the thing under your eyes is different, not a journey.
 * Keyed on the change, so reading the same card or grading it never re-plays.
 */
@Composable
private fun rememberStepProgress(changeKey: Any?): State<Float> {
    // Starts settled, and the *first* key is not a change: the content already on the
    // screen when it opened is carried by the route transition, so animating it too
    // would make the screen wait for its own entrance twice — and snapping to 0 after
    // the first frame is exactly the blink this is meant to remove.
    val progress = remember { Animatable(1f) }
    var firstKey by remember { mutableStateOf(true) }
    LaunchedEffect(changeKey) {
        if (firstKey) {
            firstKey = false
            return@LaunchedEffect
        }
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(AppMotion.STEP_MS, easing = FastOutSlowInEasing),
        )
    }
    return progress.asState()
}

/** [Modifier] form, for a container that already exists (a card, a column). */
@Composable
fun Modifier.stepChange(changeKey: Any?): Modifier {
    val progress = rememberStepProgress(changeKey)
    val shiftPx = with(LocalDensity.current) { AppMotion.STEP_SHIFT_DP.dp.toPx() }
    return graphicsLayer {
        alpha = progress.value
        translationY = (1f - progress.value) * shiftPx
    }
}

/** Composable form, for a branch that has no container of its own. */
@Composable
fun StepChange(
    changeKey: Any?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.stepChange(changeKey)) { content() }
}

/**
 * One slot of a page reveal. [index] is the card's position in reading order; the
 * first card never waits.
 */
@Composable
fun StaggeredReveal(
    reveal: PageReveal,
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val distancePx = with(LocalDensity.current) { AppMotion.REVEAL_RISE_DP.dp.toPx() }
    Box(
        modifier = modifier.graphicsLayer {
            // Both reads happen here, in the layer block: the reveal animates the
            // GPU layer and leaves composition and layout alone.
            alpha = reveal.alpha(index)
            translationY = reveal.risePx(index, distancePx)
        },
    ) {
        content()
    }
}
