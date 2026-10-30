package org.token.english.core.designsystem

import androidx.compose.ui.graphics.Color

/**
 * Brand + semantic palette. Single source of truth for raw colors.
 * Source: files/stitch_english_learning_app_design_system/design.md
 * Features MUST use MaterialTheme.colorScheme / AppExtendedColors instead of these values.
 */
object Brand {
    // Primary scale
    val Primary50 = Color(0xFFF4F8FF)
    val Primary100 = Color(0xFFE8F2FF)
    val Primary200 = Color(0xFFBFDBFE)
    val Primary300 = Color(0xFF93C5FD)
    val Primary400 = Color(0xFF60A5FA)
    val Primary500 = Color(0xFF3D7FF2)
    val Primary600 = Color(0xFF2169EF)
    val Primary700 = Color(0xFF1856C7)
    val Primary800 = Color(0xFF123E91)
    val Primary900 = Color(0xFF0F2D6B)

    // Semantic
    val Success100 = Color(0xFFDCFCE7)
    val Success500 = Color(0xFF22C55E)
    val Success700 = Color(0xFF15803D)
    val Warning100 = Color(0xFFFEF3C7)
    val Warning500 = Color(0xFFF59E0B)
    val Warning700 = Color(0xFFB45309)
    val Error100 = Color(0xFFFEE2E2)
    val Error500 = Color(0xFFEF4444)
    val Error700 = Color(0xFFB91C1C)
    val Info100 = Color(0xFFE0F2FE)
    val Info500 = Color(0xFF0EA5E9)
    val Info700 = Color(0xFF0369A1)

    // Violet scale — the B2 CEFR badge (CefrBadge needs one hue per level;
    // added alongside the semantic scales, same ramp shape).
    val Violet50 = Color(0xFFF5F3FF)
    val Violet100 = Color(0xFFEDE9FE)
    val Violet200 = Color(0xFFDDD6FE)
    val Violet300 = Color(0xFFC4B5FD)
    val Violet400 = Color(0xFFA78BFA)
    val Violet500 = Color(0xFF8B5CF6)
    val Violet600 = Color(0xFF7C3AED)
    val Violet700 = Color(0xFF6D28D9)
    val Violet800 = Color(0xFF5B21B6)
    val Violet900 = Color(0xFF4C1D95)

    // Rose scale — the C2 CEFR badge (the far end of the level ramp; deliberately
    // not the Error scale, which stays reserved for failures).
    val Rose50 = Color(0xFFFFF1F2)
    val Rose100 = Color(0xFFFFE4E6)
    val Rose200 = Color(0xFFFECDD3)
    val Rose300 = Color(0xFFFDA4AF)
    val Rose400 = Color(0xFFFB7185)
    val Rose500 = Color(0xFFF43F5E)
    val Rose600 = Color(0xFFE11D48)
    val Rose700 = Color(0xFFBE123C)
    val Rose800 = Color(0xFF9F1239)
    val Rose900 = Color(0xFF881337)

    // ── Xiaomi-inspired layer ────────────────────────────────────────────────
    // The interface direction changed in v28: the review was that a Material
    // default reads as "paper" — flat white boxes with hairline borders. The
    // replacement follows Xiaomi's public design language: a bright canvas, one
    // decisive orange accent, large rounded surfaces and soft shadows, with the
    // accent reserved for actions rather than poured over every surface.
    // Values from the Xiaomi DESIGN.md (ndroussi/design-md-for-ai).
    val XiaoOrange = Color(0xFFFF6900)      // accent + primary action
    val XiaoOrangeDeep = Color(0xFFC2410C)  // filled surface that carries white text
    val XiaoOrangeSoft = Color(0xFFFFE8D6)  // light callout fill
    val XiaoOrangeInk = Color(0xFF7A2E00)   // text on the soft fill
    val XiaoInk = Color(0xFF191919)         // headings and navigation ink
    val XiaoText = Color(0xFF222222)        // body text
    val XiaoMuted = Color(0xFF6B7280)       // secondary text
    val XiaoSurface = Color(0xFFF5F6F8)     // light section background
    val XiaoBorder = Color(0xFFD9DEE5)      // card borders and separators
    val XiaoGreen = Color(0xFF22A06B)       // positive state
    val XiaoNight = Color(0xFF0E0E10)       // dark canvas
    val XiaoNightSurface = Color(0xFF17171A) // dark card
    val XiaoNightElevated = Color(0xFF202024)
    val XiaoNightBorder = Color(0xFF2B2B31)
    val XiaoOrangeLight = Color(0xFFFF8A3D) // accent on the dark canvas
    /** Shadow ink — the soft, low-alpha black Xiaomi uses instead of heavy elevation. */
    val ShadowInk = Color(0xFF191919)

    // Neutral
    val Neutral50 = Color(0xFFF8FAFC)
    val Neutral100 = Color(0xFFF1F5F9)
    val Neutral200 = Color(0xFFE2E8F0)
    val Neutral300 = Color(0xFFCBD5E1)
    val Neutral400 = Color(0xFF94A3B8)
    val Neutral500 = Color(0xFF64748B)
    val Neutral600 = Color(0xFF475569)
    val Neutral700 = Color(0xFF334155)
    val Neutral800 = Color(0xFF1F2937)
    val Neutral900 = Color(0xFF111827)
    val Neutral950 = Color(0xFF0F172A)

    // Dark theme surfaces
    val NightBackground = Color(0xFF0B1220)
    val NightSurface = Color(0xFF111827)
    val NightSurfaceElevated = Color(0xFF172033)
    val NightBorder = Color(0xFF263449)
    val NightPrimaryContainer = Color(0xFF17366F)
}
