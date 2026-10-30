package org.token.english.core.designsystem.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay

/**
 * A gentle entrance for a list of cards.
 *
 * The screen used to assemble itself fully formed, so every card landed in the
 * same frame — which is what makes a page feel like a wall of paper being
 * unfolded at once. Each card now fades and rises into place, 55ms after the one
 * above it: slow enough to read as motion, short enough that nothing is ever
 * waited on (a full Home has settled well under half a second).
 *
 * The motion lives in `graphicsLayer`, not in a visibility wrapper: a card that
 * animates in still occupies its final slot from the first frame, so nothing
 * jumps, no spacing appears where a hidden card would have been, and the scroll
 * range never changes mid-animation. Nothing depends on the animation to be
 * shown — the end state is the only state after [index] × 55ms.
 */
@Composable
fun StaggeredReveal(
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var started by remember { mutableStateOf(index == 0) }

    LaunchedEffect(Unit) {
        // The first card never waits: a stagger must not delay the top of the page.
        if (index > 0) {
            delay(index * 55L)
            started = true
        }
    }

    val alpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
        label = "reveal-alpha",
    )
    val rise by animateFloatAsState(
        targetValue = if (started) 0f else 26f,
        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
        label = "reveal-rise",
    )

    Box(
        modifier = modifier.graphicsLayer {
            this.alpha = alpha
            translationY = rise
        },
    ) {
        content()
    }
}
