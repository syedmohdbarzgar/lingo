package org.token.english

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.AppTheme
import org.token.english.core.designsystem.component.EnglishText

/**
 * Custom launch screen (checklist P8).
 *
 * The whole launch moment is ONE continuous brand field: `Theme.English.Starting`
 * shows only the brand color (no system logo, 0ms), this activity holds the same
 * blue with the mark in the middle while the offline content bundle finishes
 * seeding into Room, and [MainActivity] fades in behind it — no white flash and
 * no empty Home on a first install. A warm start still passes straight through,
 * but the brand always holds for its full [MIN_SHOW_MS] (1.5s).
 *
 * Design: the mark (`drawable/logo_lingo`) is the screen's single focus in the
 * center; the name and the Persian promise anchor the BOTTOM of the screen with
 * the seeding indicator under them as system status — both from color
 * resources, never hardcoded: the field is the icon's own blue and everything
 * on it is `@color/white` unconditionally, so dark mode cannot turn the lockup
 * near-black on the fixed blue field.
 */
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Must run before super.onCreate so the (branded) system splash is installed.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    SplashContent(application as EnglishApp, onReady = ::openApp)
                }
            }
        }
    }

    private fun openApp() {
        startActivity(Intent(this, MainActivity::class.java))
        // Fade out the splash window as Home fades in (checklist P8 animation).
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    private companion object {
        /** Never hang on the splash if seeding misbehaves. */
        const val MAX_WAIT_MS = 8_000L
        /** The brand holds for a full 1.5s before the screen is released. */
        const val MIN_SHOW_MS = 1_500L

        /** The mark owns the center; name and promise sit at the bottom. */
        val SPLASH_LOGO_SIZE = 96.dp
    }

    @Composable
    private fun SplashContent(app: EnglishApp, onReady: () -> Unit) {
        LaunchedEffect(Unit) {
            val started = SystemClock.elapsedRealtime()
            // Wait for the content bundle (bounded), so Home has data on arrival.
            withTimeoutOrNull(MAX_WAIT_MS) { app.container.contentSeeded.first { it } }
            val elapsed = SystemClock.elapsedRealtime() - started
            if (elapsed < MIN_SHOW_MS) delay(MIN_SHOW_MS - elapsed)
            onReady()
        }

        // The launcher icon's exact background — the single source shared with the
        // adaptive icon, so the field never drifts from the mark sitting on it
        // (AGENTS §6: a resource, not a hardcoded color).
        val background = colorResource(R.color.ic_launcher_background)
        // White from a resource (never hardcoded — AGENTS §6): the field is the
        // fixed brand blue day and night, so the lockup is always white on it —
        // the theme's onPrimary would go near-black in dark mode.
        val onBrand = colorResource(R.color.white)

        // One gentle entrance (450ms): the mark settles while the bundle seeds.
        // Motion explains arrival and then holds still — it never gates the task.
        val entry by animateFloatAsState(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 450),
            label = "splashEntry",
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(background),
        ) {
            Image(
                // The mark, dead center: `drawable/logo_lingo` is a plain
                // vector, so painterResource draws it directly — unlike
                // `mipmap/ic_launcher`, which resolves to an adaptive-icon XML
                // (AdaptiveIconDrawable) on API 26+ that Compose rejects with
                // "Only VectorDrawables and rasterized asset types are
                // supported" (it crashed every launch until a device run caught
                // it). White paths on the brand field = the launcher's lockup.
                painter = painterResource(R.drawable.logo_lingo),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(SPLASH_LOGO_SIZE)
                    .graphicsLayer {
                        alpha = entry
                        scaleX = 0.94f + 0.06f * entry
                        scaleY = 0.94f + 0.06f * entry
                    },
            )

            // Brand lockup at the bottom of the screen: the Latin name on its
            // own line (EnglishText re-asserts LTR — AGENTS §6), the Persian
            // promise under it, then the seeding indicator as system status —
            // a first install can legitimately wait here for several seconds.
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = AppSpacing.hero),
            ) {
                EnglishText(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    color = onBrand,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "انگلیسی را قدم به قدم یاد بگیر",
                    style = MaterialTheme.typography.bodyLarge,
                    color = onBrand,
                    textAlign = TextAlign.Center,
                )
                CircularProgressIndicator(
                    modifier = Modifier.padding(top = AppSpacing.lg),
                    color = onBrand,
                    trackColor = onBrand.copy(alpha = 0.25f),
                    strokeWidth = 3.dp,
                )
            }
        }
    }
}
