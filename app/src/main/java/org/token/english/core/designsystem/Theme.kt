package org.token.english.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import org.token.english.domain.model.ThemeMode

/**
 * Semantic colors that Material 3's ColorScheme does not model
 * (success / warning / info). Provided through the theme so features
 * never hardcode raw colors.
 */
data class AppExtendedColors(
    val success: Color,
    val successContainer: Color,
    val onSuccess: Color,
    val warning: Color,
    val warningContainer: Color,
    val onWarning: Color,
    val info: Color,
    val infoContainer: Color,
    val onInfo: Color,
    val brandTint: Color,
)

private val LightExtended = AppExtendedColors(
    success = Brand.Success500,
    successContainer = Brand.Success100,
    onSuccess = Brand.Success700,
    warning = Brand.Warning500,
    warningContainer = Brand.Warning100,
    onWarning = Brand.Warning700,
    info = Brand.Info500,
    infoContainer = Brand.Info100,
    onInfo = Brand.Info700,
    brandTint = Brand.Primary100,
)

private val DarkExtended = AppExtendedColors(
    success = Color(0xFF4ADE80),
    successContainer = Color(0xFF14351F),
    onSuccess = Color(0xFFBBF7D0),
    warning = Color(0xFFFBBF24),
    warningContainer = Color(0xFF3A2A0B),
    onWarning = Color(0xFFFDE68A),
    info = Color(0xFF38BDF8),
    infoContainer = Color(0xFF0B2E42),
    onInfo = Color(0xFFBAE6FD),
    brandTint = Brand.NightPrimaryContainer,
)

private val LightColors = lightColorScheme(
    primary = Brand.Primary600,
    onPrimary = Color.White,
    primaryContainer = Brand.Primary100,
    onPrimaryContainer = Brand.Primary900,
    secondary = Brand.Primary500,
    onSecondary = Color.White,
    secondaryContainer = Brand.Primary100,
    onSecondaryContainer = Brand.Primary900,
    tertiary = Brand.Info700,
    onTertiary = Color.White,
    background = Color.White,
    onBackground = Brand.Neutral950,
    surface = Color.White,
    onSurface = Brand.Neutral950,
    surfaceVariant = Brand.Neutral50,
    onSurfaceVariant = Brand.Neutral600,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Brand.Neutral50,
    surfaceContainer = Brand.Neutral100,
    surfaceContainerHigh = Brand.Neutral100,
    surfaceContainerHighest = Brand.Neutral200,
    outline = Brand.Neutral300,
    outlineVariant = Brand.Neutral200,
    error = Brand.Error500,
    onError = Color.White,
    errorContainer = Brand.Error100,
    onErrorContainer = Brand.Error700,
)

private val DarkColors = darkColorScheme(
    primary = Brand.Primary400,
    onPrimary = Brand.NightBackground,
    primaryContainer = Brand.NightPrimaryContainer,
    onPrimaryContainer = Brand.Primary100,
    secondary = Brand.Primary300,
    onSecondary = Brand.NightBackground,
    secondaryContainer = Brand.NightPrimaryContainer,
    onSecondaryContainer = Brand.Primary100,
    tertiary = Color(0xFF38BDF8),
    onTertiary = Brand.NightBackground,
    background = Brand.NightBackground,
    onBackground = Brand.Neutral50,
    surface = Brand.NightSurface,
    onSurface = Brand.Neutral50,
    surfaceVariant = Brand.NightSurfaceElevated,
    onSurfaceVariant = Brand.Neutral300,
    surfaceContainerLowest = Brand.NightSurface,
    surfaceContainerLow = Brand.NightSurfaceElevated,
    surfaceContainer = Brand.NightSurfaceElevated,
    surfaceContainerHigh = Brand.Neutral800,
    surfaceContainerHighest = Brand.Neutral700,
    outline = Brand.NightBorder,
    outlineVariant = Brand.NightBorder,
    error = Color(0xFFF87171),
    onError = Brand.NightBackground,
    errorContainer = Color(0xFF401515),
    onErrorContainer = Brand.Error100,
)

val LocalAppExtendedColors = staticCompositionLocalOf { LightExtended }

@Composable
fun AppTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val dark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    CompositionLocalProvider(LocalAppExtendedColors provides if (dark) DarkExtended else LightExtended) {
        MaterialTheme(
            colorScheme = if (dark) DarkColors else LightColors,
            typography = AppTypography,
            shapes = AppShapes,
            content = content,
        )
    }
}
