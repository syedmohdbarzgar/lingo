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
