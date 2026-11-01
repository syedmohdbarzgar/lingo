package org.token.english.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Radius tokens, Xiaomi-inspired (v28): generous rounding instead of Material's
 * tighter corners — cards 22dp, fields 14dp, chips and buttons fully rounded
 * (pill). The larger radius is what stops a card from reading as a sheet of
 * paper: a 22dp corner plus a soft shadow makes it a surface.
 */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(22.dp),
    // Pills: CEFR badges, chips and every button.
    extraLarge = RoundedCornerShape(percent = 50),
)

/** Fully rounded action shape (Xiaomi's 999px button). */
val PillShape = RoundedCornerShape(percent = 50)

/**
 * Dialogs and sheets get their own tokens (v31). Material3 resolves
 * `AlertDialogDefaults.shape` and `BottomSheetDefaults.ExpandedShape` from
 * `MaterialTheme.shapes.extraLarge` — which this design system uses for **pills** — so
 * every dialog and every sheet came up as a 50% capsule. A sheet is a surface, not a
 * button: it takes the card's 22dp on its top corners, and a dialog sits a touch
 * tighter at 20dp.
 */
val DialogShape = RoundedCornerShape(20.dp)
val SheetShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
