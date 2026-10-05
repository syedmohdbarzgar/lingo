package org.token.english

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.AppTheme

/**
 * Custom launch screen (checklist P8).
 *
 * The system splash is intentionally blank (see `Theme.English.Starting`), so this
 * activity owns the whole launch moment: it shows the brand while the offline
 * content bundle finishes seeding into Room, then hands off to [MainActivity].
 * That keeps a first install from flashing an empty Home, and lets a warm start
 * skip straight through.
 */
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Must run before super.onCreate so the (blank) system splash is installed.
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.lg),
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                CircularProgressIndicator()
            }
        }
    }
}
