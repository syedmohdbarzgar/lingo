package org.token.english.core.designsystem

import androidx.compose.ui.unit.dp

/**
 * 4dp-based spacing grid (design.md §11). Use these tokens instead of raw dp values.
 */
object AppSpacing {
    val xs = 4.dp      // micro
    val sm = 8.dp      // small
    val compact = 12.dp
    val md = 16.dp     // standard
    val medium = 20.dp
    val lg = 24.dp     // large
    val section = 32.dp
    val major = 40.dp
    val hero = 48.dp   // primary button height / touch target
    val pageSeparation = 64.dp
}

/** Touch-target minimum from accessibility rules. */
val TouchTargetMin = 48.dp
