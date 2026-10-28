package org.token.english.core.designsystem.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Settings switch — Material 3 [Switch] with the **unchecked** thumb lifted to a
 * readable tone.
 *
 * The M3 default draws an off switch's thumb in `outline` on a
 * `surfaceContainerHighest` track, which measures 1.20:1 in the light theme and
 * 1.21:1 in the dark one (phase-2 contrast audit): the control then reads as an empty
 * pill, and which way it is set is the whole reason the learner is looking at it — so
 * WCAG 1.4.11 applies to that state. `onSurfaceVariant` puts the thumb at 6.15:1
 * against its own track in the light theme and 6.97:1 in the dark one, and the
 * checked state keeps the M3 defaults, which already pass (white on `primary`).
 *
 * Deliberately not restyled further: an off switch (pale pill, dark thumb at the
 * start) and an on switch (filled pill, light thumb at the end) are already
 * unmistakable once the thumb is readable, so the track keeps the M3 tone.
 */
@Composable
fun AppSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    )
}
