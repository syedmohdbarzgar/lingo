package org.token.english.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import org.token.english.EnglishApp
import org.token.english.feature.home.HomeScreen
import org.token.english.feature.lesson.LessonScreen
import org.token.english.feature.onboarding.OnboardingScreen
import org.token.english.feature.onboarding.PlacementScreen
import org.token.english.feature.paywall.PaywallScreen
import org.token.english.feature.progress.ProgressScreen
import org.token.english.feature.review.ReviewScreen
import org.token.english.feature.settings.SettingsScreen
import org.token.english.feature.vocabulary.VocabularyDetailScreen
import org.token.english.feature.vocabulary.VocabularyScreen

object Routes {
    const val ONBOARDING = "onboarding"
    const val PLACEMENT = "placement"
    const val HOME = "home"
    const val LESSON = "lesson/{lessonId}"
    const val REVIEW = "review"
    const val VOCABULARY = "vocabulary"
    const val VOCABULARY_DETAIL = "vocabulary/{vocabId}"
    const val PROGRESS = "progress"
    const val SETTINGS = "settings"
    const val PAYWALL = "paywall"

    fun lesson(lessonId: String) = "lesson/$lessonId"
    fun vocabularyDetail(vocabId: String) = "vocabulary/$vocabId"
}

private data class Tab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

private val tabs = listOf(
    Tab(Routes.HOME, "خانه", Icons.Filled.Home, Icons.Outlined.Home),
    Tab(Routes.REVIEW, "مرور", Icons.Filled.Replay, Icons.Outlined.Replay),
    Tab(Routes.VOCABULARY, "واژگان", Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
    Tab(Routes.PROGRESS, "پیشرفت", Icons.Filled.BarChart, Icons.Outlined.BarChart),
)

@Composable
fun AppNavGraph(startDestination: String) {
    val navController: NavHostController = androidx.navigation.compose.rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Subscription gating: once the 24h trial is over and no subscription is
    // active, every learning screen redirects to the paywall (settings stay open
    // so the learner can always manage the app).
    val app = LocalContext.current.applicationContext as EnglishApp
    val access by remember { app.container.settingsRepository.observeTrialAndSubscription() }
        .collectAsStateWithLifecycle(initialValue = null)
    // Entitlement depends on wall-clock time, but the flow only emits when the
    // stored values change — tick `now` so a trial that expires mid-session
    // locks the app without waiting for a restart.
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(60_000L)
            nowMillis = System.currentTimeMillis()
        }
    }
    val accessLevel = access?.let {
        org.token.english.core.billing.EntitlementPolicy.level(
            now = nowMillis,
            trialRemainingMs = org.token.english.core.billing.TrialClock.remainingMs(
                state = it.trialClockState(),
                now = nowMillis,
                elapsedRealtime = android.os.SystemClock.elapsedRealtime(),
            ),
            subscriptionUntil = it.subscriptionUntil,
        )
    }
    val locked = accessLevel == org.token.english.core.billing.AccessLevel.LOCKED
    val paywallOpen = currentRoute == Routes.PAYWALL
    val lockExempt = currentRoute == null || currentRoute in LOCK_EXEMPT
    LaunchedEffect(locked, currentRoute) {
        if (locked && !paywallOpen && !lockExempt) {
            navController.navigate(Routes.PAYWALL) { launchSingleTop = true }
        }
    }

    fun open(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        bottomBar = {
            val showBottomBar = tabs.any { it.route == currentRoute }
            if (showBottomBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        val selected = tab.route == currentRoute
                        NavigationBarItem(
                            selected = selected,
                            onClick = { open(tab.route) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.label,
                                )
                            },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding),
        ) {
            onboarding(navController)
            placement(navController)
            paywall(navController)
            home(navController)
            lesson(navController)
            review(navController)
            vocabulary(navController)
            progress(navController)
            settings(navController)
        }
    }
}

/** Routes reachable even when the subscription is locked. */
private val LOCK_EXEMPT = setOf(Routes.ONBOARDING, Routes.PLACEMENT, Routes.SETTINGS)

private fun NavGraphBuilder.paywall(navController: NavHostController) {
    composable(Routes.PAYWALL) {
        PaywallScreen(
            onClose = { navController.popBackStack() },
            onLockedExit = {
                // A locked learner would be redirected straight back to the
                // paywall from any learning screen; settings is the one
                // always-reachable place (restore purchases, manage account).
                navController.navigate(Routes.SETTINGS) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onPurchased = {
                navController.navigate(Routes.HOME) {
                    popUpTo(0) { inclusive = true }
                }
            },
        )
    }
}

private fun NavGraphBuilder.onboarding(navController: NavHostController) {
    composable(Routes.ONBOARDING) {
        OnboardingScreen(
            onPlacement = { navController.navigate(Routes.PLACEMENT) },
            onSkip = {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.ONBOARDING) { inclusive = true }
                }
            },
        )
    }
}

private fun NavGraphBuilder.placement(navController: NavHostController) {
    composable(Routes.PLACEMENT) {
        PlacementScreen(
            onFinished = {
                navController.navigate(Routes.HOME) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onBack = { navController.popBackStack() },
        )
    }
}

private fun NavGraphBuilder.home(navController: NavHostController) {
    composable(Routes.HOME) {
        HomeScreen(
            onOpenLesson = { navController.navigate(Routes.lesson(it)) },
            onOpenSettings = { navController.navigate(Routes.SETTINGS) },
            onOpenReview = { navController.navigate(Routes.REVIEW) },
            onOpenVocabulary = { navController.navigate(Routes.VOCABULARY) },
            onOpenPaywall = { navController.navigate(Routes.PAYWALL) },
        )
    }
}

private fun NavGraphBuilder.lesson(navController: NavHostController) {
    composable(
        route = Routes.LESSON,
        arguments = listOf(navArgument("lessonId") { type = NavType.StringType }),
    ) { entry ->
        val lessonId = entry.arguments?.getString("lessonId").orEmpty()
        LessonScreen(
            lessonId = lessonId,
            onExit = { navController.popBackStack() },
        )
    }
}

private fun NavGraphBuilder.review(navController: NavHostController) {
    composable(Routes.REVIEW) {
        ReviewScreen(
            onBrowseVocabulary = { navController.navigate(Routes.VOCABULARY) },
            onGoHome = { navController.navigate(Routes.HOME) },
        )
    }
}

private fun NavGraphBuilder.vocabulary(navController: NavHostController) {
    composable(Routes.VOCABULARY) {
        VocabularyScreen(
            onOpenDetail = { navController.navigate(Routes.vocabularyDetail(it)) },
        )
    }
    composable(
        route = Routes.VOCABULARY_DETAIL,
        arguments = listOf(navArgument("vocabId") { type = NavType.StringType }),
    ) { entry ->
        val vocabId = entry.arguments?.getString("vocabId").orEmpty()
        VocabularyDetailScreen(
            vocabId = vocabId,
            onBack = { navController.popBackStack() },
        )
    }
}

private fun NavGraphBuilder.progress(navController: NavHostController) {
    composable(Routes.PROGRESS) { ProgressScreen() }
}

private fun NavGraphBuilder.settings(navController: NavHostController) {
    composable(Routes.SETTINGS) {
        SettingsScreen(
            onBack = { navController.popBackStack() },
            onReTakePlacement = { navController.navigate(Routes.PLACEMENT) },
        )
    }
}
