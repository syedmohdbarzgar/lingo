package org.token.english

import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.token.english.core.designsystem.AppTheme
import org.token.english.core.designsystem.component.AppChip

/**
 * The chip's two sizes, measured rather than reasoned about.
 *
 * `AppChip` is a ~32dp pill, which is the right look and — before this test existed —
 * the whole tappable area: `Modifier.clickable` does not grow a target the way a
 * Material component does, so the level pickers in Settings and the level row in the
 * vocabulary browser sat under the 48dp bar AGENTS §6 sets. The floor is now applied
 * to the tappable half only, and these two cases pin both directions: a chip that can
 * be tapped clears the bar, and a chip that is only metadata stays a chip instead of
 * turning into a 48dp band that would wreck the rows it sits in.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class ChipTouchTargetTest {

    @get:Rule
    val compose = createComposeRule()

    /** 48dp is `TouchTargetMin`; spelled out here so the bar is visible in the test. */
    private val minTouchTarget = 48.dp

    @Test
    fun `a chip that can be tapped clears the touch bar`() {
        compose.setContent {
            AppTheme { AppChip(label = "A2", selected = true, onClick = {}) }
        }

        // The merged node is the clickable one — its size is the finger's target,
        // not the label's drawn height.
        compose.onNodeWithText("A2").assertHeightIsAtLeast(minTouchTarget)
    }

    @Test
    fun `a chip that is only a label is not inflated to a touch target`() {
        compose.setContent {
            AppTheme { AppChip(label = "GRAMMAR") }
        }

        // A metadata chip is not tappable, so it must keep its own height: they sit in
        // rows of two or three, and three 48dp bands would be a layout change nobody
        // asked for.
        val bounds = compose.onNodeWithText("GRAMMAR").getUnclippedBoundsInRoot()
        val height = bounds.bottom - bounds.top
        assertTrue("a label chip is not a touch target (was $height)", height < minTouchTarget)
    }
}
