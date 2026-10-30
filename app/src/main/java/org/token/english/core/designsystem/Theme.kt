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
    /**
     * A full-strength brand fill that always carries white text (the free-access
     * status card). `colorScheme.primary` cannot serve: its dark-theme value
     * (Primary400) is a light blue, so white ink on it would measure ~2:1. The
     * pair is therefore its own token, one step deeper per theme.
     */
    val brandStrong: Color,
    val onBrandStrong: Color,
    // The two CEFR-badge hues Material 3 does not model (see CefrBadge: one
    // color per level, B2 = violet and C2 = rose).
    val violet: Color,
    val violetContainer: Color,
    val onViolet: Color,
    val rose: Color,
    val roseContainer: Color,
    val onRose: Color,
    /**
     * Ink for content drawn **over a bundled artwork** (the premium banner on the
     * subscription card). The artwork is light pastel and does not follow the
     * theme, so these are the same dark neutrals in both themes: a dark theme's
     * [androidx.compose.material3.ColorScheme.onSurface] would put near-white text
     * on a near-white picture and vanish. Measured against the artwork's darkest
     * region (#92C2FD, the blue swoosh) [onArtwork] reads ~9:1 and [onArtworkMuted]
     * ~7:1 before the veil, and ≥13:1 / ≥8:1 through it.
     */
    val onArtwork: Color,
    val onArtworkMuted: Color,
    /**
     * The veil laid over that artwork: a near-white surface that lifts the pastel
     * to a readable field without hiding it. Near-white — not the theme surface —
     * for the same reason: on a dark theme a dark veil plus dark ink is 3.7:1.
     */
    val artworkVeil: Color,
)

private val LightExtended = AppExtendedColors(
    // The three tinted Home cards (plan / path / weak spot) keep the palette they
    // already had: the blue accent belongs to actions, and recolouring the card
    // families on top of it made the weak-spot card read as olive mud.
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
    // Deep blue, not Primary600: white body text on the 600 step measures 4.0:1,
    // while white on Primary700 reads 5.5:1. The accent stays for actions; a
    // surface that carries white text uses this pair.
    brandStrong = Brand.Primary700,
    onBrandStrong = Color.White,
    violet = Brand.Violet500,
    violetContainer = Brand.Violet100,
    onViolet = Brand.Violet700,
    rose = Brand.Rose500,
    roseContainer = Brand.Rose100,
    onRose = Brand.Rose700,
    onArtwork = Brand.Neutral950,
    onArtworkMuted = Brand.Neutral700,
    artworkVeil = Brand.Neutral50,
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
    // The same deep blue in both themes: white on it reads 5.5:1, and on the
    // dark canvas it still separates from the surface without glowing.
    brandStrong = Brand.Primary700,
    onBrandStrong = Color.White,
    violet = Color(0xFFA78BFA),
    violetContainer = Color(0xFF241553),
    onViolet = Color(0xFFDDD6FE),
    rose = Color(0xFFFB7185),
    roseContainer = Color(0xFF4C0E21),
    onRose = Color(0xFFFECDD3),
    // Deliberately the light values in the dark theme: the banner is the same
    // light artwork in both, and dark ink on it is what stays readable.
    onArtwork = Brand.Neutral950,
    onArtworkMuted = Brand.Neutral700,
    artworkVeil = Brand.Neutral50,
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
    onBackground = Brand.XiaoInk,
    surface = Color.White,
    onSurface = Brand.XiaoInk,
    surfaceVariant = Brand.XiaoSurface,
    onSurfaceVariant = Brand.XiaoMuted,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF7F8FA),
    surfaceContainer = Color(0xFFF1F2F5),
    surfaceContainerHigh = Color(0xFFECEEF2),
    surfaceContainerHighest = Color(0xFFE4E7EC),
    outline = Brand.XiaoBorder,
    outlineVariant = Color(0xFFE8EBEF),
    // Error700, not Error500: this role is the *text* colour on surface (the two
    // inline failure sentences in the lesson exercise are its only consumers), and
    // #EF4444 on white measures 3.76:1 — under the 4.5:1 that bodySmall needs
    // (phase-2 contrast audit). #B91C1C reads 6.47:1 there, and white on it is
    // 6.47:1 too, so the onError role stays safe. It is also the M3 baseline shape
    // for a light error role: a dark red rather than a 500-level one.
    error = Brand.Error700,
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
    onTertiary = Brand.XiaoNight,
    background = Brand.XiaoNight,
    onBackground = Color(0xFFF3F4F6),
    surface = Brand.XiaoNightSurface,
    onSurface = Color(0xFFF3F4F6),
    surfaceVariant = Brand.XiaoNightElevated,
    onSurfaceVariant = Color(0xFFA1A1AA),
    surfaceContainerLowest = Brand.XiaoNight,
    surfaceContainerLow = Brand.XiaoNightSurface,
    surfaceContainer = Brand.XiaoNightElevated,
    surfaceContainerHigh = Color(0xFF26262B),
    surfaceContainerHighest = Color(0xFF2E2E34),
    outline = Brand.XiaoNightBorder,
    outlineVariant = Color(0xFF26262B),
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
