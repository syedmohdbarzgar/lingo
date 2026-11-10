package org.token.english

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import org.token.english.core.designsystem.AppTheme
import org.token.english.navigation.AppNavGraph
import org.token.english.navigation.Routes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySplashHandOff()
        enableEdgeToEdge()
        setContent {
            val app = application as EnglishApp
            val settings by app.container.settingsRepository.settings.collectAsState(initial = null)
            val current = settings
            if (current == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                AppRoot(
                    firstLaunch = current.isFirstLaunch,
                    themeMode = current.themeMode,
                )
            }
        }
    }

    /**
     * The splash → Home cross-fade, on the side API 34+ reads it from.
     *
     * From Android 14 the pair is declared by the activity being *opened*
     * (`OVERRIDE_TRANSITION_OPEN`: enter for this screen, exit for the one that is
     * finishing), which is why [SplashActivity] only calls the deprecated
     * `overridePendingTransition` below 34. Same two fades, same 300ms, declared where
     * the platform now looks for them — see `docs/debt.md` for the device comparison
     * that is still owed.
     */
    private fun applySplashHandOff() {
        if (!SplashHandOff.usesNewApi(android.os.Build.VERSION.SDK_INT)) return
        overrideActivityTransition(
            android.app.Activity.OVERRIDE_TRANSITION_OPEN,
            R.anim.fade_in,
            R.anim.fade_out,
        )
    }
}

/**
 * Root composable: forces the RTL (Persian) layout direction for the whole app —
 * English learning content re-asserts LTR locally through EnglishText (design.md §51).
 */
@Composable
private fun AppRoot(firstLaunch: Boolean, themeMode: org.token.english.domain.model.ThemeMode) {
    AppTheme(themeMode = themeMode) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            AppNavGraph(
                startDestination = if (firstLaunch) Routes.ONBOARDING else Routes.HOME,
            )
        }
    }
}
