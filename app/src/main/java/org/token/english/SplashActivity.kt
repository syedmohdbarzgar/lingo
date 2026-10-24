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
 * shows the launcher's own blue and mark, this activity holds the same blue with
 * the icon and name while the offline content bundle finishes seeding into Room,
 * and [MainActivity] fades in behind it — so the icon the learner just tapped
 * appears to grow into the app, with no white flash and no empty Home on a first
 * install. A warm start still passes straight through (MIN_SHOW_MS only keeps the
 * brand from flickering).
 *
 * Design: the mark is the screen's single focus, the name carries the brand, and
 * the seeding indicator sits low in the frame as system status — brand blue comes
 * from the icon's own color resource (never hardcoded), text and progress use the
 * theme's `onPrimary` token.
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
        /** Keep the brand on screen long enough to not flicker. */
        const val MIN_SHOW_MS = 500L

        /** The mark is the screen's single focus; the name below carries the brand. */
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
        // White as the theme's onPrimary token: the semantic "text on brand" color.
        val onBrand = MaterialTheme.colorScheme.onPrimary

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
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.lg),
                modifier = Modifier.graphicsLayer {
                    alpha = entry
                    scaleX = 0.94f + 0.06f * entry
                    scaleY = 0.94f + 0.06f * entry
                },
            ) {
                Image(
                    painter = painterResource(R.mipmap.ic_launcher),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier.size(SPLASH_LOGO_SIZE),
                )
                EnglishText(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    color = onBrand,
                )
            }

            // System status: bounded work (seeding) is happening. Low in the frame
            // so the brand keeps the one point of attention, but always visible —
            // a first install can legitimately wait here for several seconds.
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = AppSpacing.hero),
                color = onBrand,
                trackColor = onBrand.copy(alpha = 0.25f),
                strokeWidth = 3.dp,
            )
        }
    }
}
