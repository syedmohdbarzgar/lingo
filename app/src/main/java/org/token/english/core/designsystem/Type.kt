package org.token.english.core.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.token.english.R

/**
 * Vazirmatn — bundled Persian UI font (SIL OFL, see licenses/Vazirmatn-OFL.txt).
 * Persian UI renders with Vazirmatn; English learning content re-asserts the system
 * (Roboto) family inside EnglishText (design.md §8: Vazirmatn + Inter/Roboto split).
 */
val Vazirmatn = FontFamily(
    Font(R.font.vazirmatn_regular, FontWeight.Normal),
    Font(R.font.vazirmatn_medium, FontWeight.Medium),
    Font(R.font.vazirmatn_semibold, FontWeight.SemiBold),
    Font(R.font.vazirmatn_bold, FontWeight.Bold),
)

/**
 * Type scale from design.md (section 9) + two learning-content styles (section 10).
 */
val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = Vazirmatn, fontSize = 48.sp, lineHeight = 56.sp, fontWeight = FontWeight.Bold),
    displayMedium = TextStyle(fontFamily = Vazirmatn, fontSize = 40.sp, lineHeight = 48.sp, fontWeight = FontWeight.Bold),
    displaySmall = TextStyle(fontFamily = Vazirmatn, fontSize = 36.sp, lineHeight = 44.sp, fontWeight = FontWeight.Bold),
    headlineLarge = TextStyle(fontFamily = Vazirmatn, fontSize = 32.sp, lineHeight = 40.sp, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontFamily = Vazirmatn, fontSize = 28.sp, lineHeight = 36.sp, fontWeight = FontWeight.Bold),
    headlineSmall = TextStyle(fontFamily = Vazirmatn, fontSize = 24.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontFamily = Vazirmatn, fontSize = 22.sp, lineHeight = 30.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontFamily = Vazirmatn, fontSize = 18.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold),
    titleSmall = TextStyle(fontFamily = Vazirmatn, fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontFamily = Vazirmatn, fontSize = 16.sp, lineHeight = 26.sp, fontWeight = FontWeight.Normal),
    bodyMedium = TextStyle(fontFamily = Vazirmatn, fontSize = 14.sp, lineHeight = 22.sp, fontWeight = FontWeight.Normal),
    bodySmall = TextStyle(fontFamily = Vazirmatn, fontSize = 12.sp, lineHeight = 18.sp, fontWeight = FontWeight.Normal),
    labelLarge = TextStyle(fontFamily = Vazirmatn, fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium),
    labelMedium = TextStyle(fontFamily = Vazirmatn, fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium),
    labelSmall = TextStyle(fontFamily = Vazirmatn, fontSize = 11.sp, lineHeight = 14.sp, fontWeight = FontWeight.Medium),
)

/** English learning content — larger, LTR, relaxed line height (design.md §10). */
val LearningTargetStyle = TextStyle(
    fontSize = 24.sp,
    lineHeight = 36.sp,
    fontWeight = FontWeight.SemiBold,
    fontFamily = FontFamily.Default,
)

/** Persian translation under English content — secondary color, RTL (design.md §10). */
val LearningTranslationStyle = TextStyle(
    fontFamily = Vazirmatn,
    fontSize = 16.sp,
    lineHeight = 28.sp,
    fontWeight = FontWeight.Normal,
)
