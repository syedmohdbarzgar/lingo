package org.token.english.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.automirrored.outlined.MenuBook
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import org.token.english.EnglishApp
import org.token.english.core.designsystem.AppMotion
import org.token.english.feature.conversation.ConversationPracticeScreen
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

/**
 * The moves this app makes, and what each one looks like.
 *
 * - **Forward / back** (a tab → its detail, a lesson → Home): the screen being left
 *   slides a little in the direction of travel and the one arriving comes in from
 *   the opposite edge, so "I went deeper" and "I came back" read differently.
 * - **Sibling** (Home → Review → Vocabulary → Progress): a **cross-fade only**. The
 *   four bottom-bar tabs sit at the same level, so sliding them sideways was the
 *   app telling the learner they had gone somewhere deeper. It also animated two
 *   full screens horizontally for a move that has no depth — the heaviest of all the
 *   route changes, spent on the most frequent one.
 *
 * The slide offsets are fractions of the width, and the root layout is RTL: forward
 * arrives from the left, which is where a Persian reader's eye starts. The full
 * distance is never used — a fifth of the screen keeps the bottom bar steady and
 * stops the motion from feeling like a page-turn.
 */
private val TAB_ROUTES = setOf(Routes.HOME, Routes.REVIEW, Routes.VOCABULARY, Routes.PROGRESS)

/**
 * True when both ends of a route change are bottom-bar tabs — the one case with no
 * depth, so it fades instead of sliding. Pure and route-string based, so the rule is
 * testable without a `NavController` (see `AppMotionTest`).
 */
internal fun isSiblingMove(from: String?, to: String?): Boolean =
    from != null && to != null && from in TAB_ROUTES && to in TAB_ROUTES

private fun forwardEnter(): EnterTransition =
    slideInHorizontally(tween(AppMotion.SCREEN_MS)) { -it / 5 } +
        fadeIn(tween(AppMotion.SCREEN_MS / 2))

private fun forwardExit(): ExitTransition =
    slideOutHorizontally(tween(AppMotion.SCREEN_MS)) { it / 12 } +
        fadeOut(tween(AppMotion.SCREEN_MS / 2))

private fun backEnter(): EnterTransition =
    slideInHorizontally(tween(AppMotion.SCREEN_MS)) { it / 5 } +
        fadeIn(tween(AppMotion.SCREEN_MS / 2))

private fun backExit(): ExitTransition =
    slideOutHorizontally(tween(AppMotion.SCREEN_MS)) { -it / 12 } +
        fadeOut(tween(AppMotion.SCREEN_MS / 2))

/** A sibling swap: no direction to show, so only opacity changes. */
private fun siblingEnter(): EnterTransition = fadeIn(tween(AppMotion.SCREEN_MS / 2))

private fun siblingExit(): ExitTransition = fadeOut(tween(AppMotion.SCREEN_MS / 2))

/** The route a back-stack entry belongs to (its pattern, so `lesson/7` reads as `lesson/{lessonId}`). */
private fun NavBackStackEntry.routeOrPattern(): String? = destination.route

object Routes {
    const val ONBOARDING = "onboarding"
    const val PLACEMENT = "placement"
    const val HOME = "home"
    /** `focusItemId` turns the lesson into a focused practice block (checklist B-1). */
    const val LESSON = "lesson/{lessonId}?focusItemId={focusItemId}"
    const val REVIEW = "review"
    const val VOCABULARY = "vocabulary"
    /** A lesson's dialogue; `lessonId` focuses one lesson, absent means the current one. */
    const val CONVERSATION = "conversation?lessonId={lessonId}"
    const val VOCABULARY_DETAIL = "vocabulary/{vocabId}"
    const val PROGRESS = "progress"
    const val SETTINGS = "settings"
    const val PAYWALL = "paywall"

    fun lesson(lessonId: String) = "lesson/$lessonId"

    fun focusLesson(lessonId: String, itemId: String) =
        "lesson/$lessonId?focusItemId=$itemId"
    fun vocabularyDetail(vocabId: String) = "vocabulary/$vocabId"

    fun conversation(lessonId: String?) =
        if (lessonId.isNullOrBlank()) "conversation" else "conversation?lessonId=$lessonId"
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
    Tab(
        Routes.VOCABULARY,
        "واژگان",
        Icons.AutoMirrored.Filled.MenuBook,
        Icons.AutoMirrored.Outlined.MenuBook,
    ),
    Tab(Routes.PROGRESS, "پیشرفت", Icons.Filled.BarChart, Icons.Outlined.BarChart),
)

@Composable
fun AppNavGraph(startDestination: String) {
    val navController: NavHostController = androidx.navigation.compose.rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Subscription gating: with no active subscription (and no companion grant)
    // every learning screen redirects to the paywall; settings stay open so the
    // learner can always manage the app.
    val app = LocalContext.current.applicationContext as EnglishApp
    val access by remember { app.container.settingsRepository.observeTrialAndSubscription() }
        .collectAsStateWithLifecycle(initialValue = null)
    // Free-access companion (org.token.zaribar): while it is installed the app is
    // unlocked without a purchase, so gating must consider it too.
    val companionInstalled by remember { app.container.companionInstalled }
        .collectAsStateWithLifecycle(initialValue = false)
    // Entitlement depends on wall-clock time, but the flow only emits when the
    // stored values change — tick `now` so a subscription that lapses mid-session
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
            subscriptionUntil = it.subscriptionUntil,
            companionAppInstalled = companionInstalled,
        )
    }
    val locked = accessLevel == org.token.english.core.billing.AccessLevel.LOCKED
    val paywallOpen = currentRoute == Routes.PAYWALL
    val lockExempt = currentRoute == null || currentRoute in LOCK_EXEMPT
    // A locked learner who deliberately closed the paywall rests on Home instead
    // of being bounced straight back into it. The app-start redirect still fires
    // (the flag starts false and dies with the process), and only Home is quieted
    // by it — every learning route still redirects, so the gate stays honest.
    // Before this, "خروج" rooted Settings on the stack (popUpTo(0)) and the next
    // back gesture closed the app instead of reaching Home.
    var paywallDismissed by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(locked, currentRoute, paywallDismissed) {
        val restsOnHome = paywallDismissed && currentRoute == Routes.HOME
        if (locked && !paywallOpen && !lockExempt && !restsOnHome) {
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
        // The shell only reserves space for the bottom bar; each screen's own
        // Scaffold consumes the system insets (its TopAppBar pads the status bar).
        // Leaving the default insets here double-counts the status bar and leaves
        // an unusual gap above every toolbar.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
            // Declared once for the whole graph. They used to be repeated on all ten
            // destinations, which is how the four tabs ended up sliding like pushes:
            // a per-route default cannot see *both* ends of a move.
            enterTransition = {
                if (isSiblingMove(initialState.routeOrPattern(), targetState.routeOrPattern())) {
                    siblingEnter()
                } else {
                    forwardEnter()
                }
            },
            exitTransition = {
                if (isSiblingMove(initialState.routeOrPattern(), targetState.routeOrPattern())) {
                    siblingExit()
                } else {
                    forwardExit()
                }
            },
            popEnterTransition = {
                if (isSiblingMove(initialState.routeOrPattern(), targetState.routeOrPattern())) {
                    siblingEnter()
                } else {
                    backEnter()
                }
            },
            popExitTransition = {
                if (isSiblingMove(initialState.routeOrPattern(), targetState.routeOrPattern())) {
                    siblingExit()
                } else {
                    backExit()
                }
            },
        ) {
            onboarding(navController)
            placement(navController)
            // The dismissal flag lives in AppNavGraph's scope (the gate reads it);
            // the route extension only receives the setter it needs.
            paywall(navController) { paywallDismissed = true }
            home(navController)
            lesson(navController)
            review(navController)
            vocabulary(navController)
            conversation(navController)
            progress(navController)
            settings(navController)
        }
    }
}

/** Routes reachable even when the subscription is locked. */
private val LOCK_EXEMPT = setOf(Routes.ONBOARDING, Routes.PLACEMENT, Routes.SETTINGS)

private fun NavGraphBuilder.paywall(
    navController: NavHostController,
    onDismissLocked: () -> Unit,
) {
    composable(Routes.PAYWALL) {
        PaywallScreen(
            onClose = { navController.popBackStack() },
            onLockedExit = {
                // Closing the paywall while locked lands on Home — never on
                // Settings as the root, whose back gesture then killed the app.
                onDismissLocked()
                navController.navigate(Routes.HOME) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onLockedSettings = {
                // A deliberate trip to settings (restore purchases, account);
                // back from there returns to Home, not out of the app.
                onDismissLocked()
                navController.navigate(Routes.SETTINGS) {
                    popUpTo(Routes.HOME)
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
            onOpenPaywall = { navController.navigate(Routes.PAYWALL) },
            onOpenConversation = { lessonId ->
                navController.navigate(Routes.conversation(lessonId))
            },
            onOpenFocus = { lessonId, itemId ->
                navController.navigate(Routes.focusLesson(lessonId, itemId))
            },
        )
    }
}

private fun NavGraphBuilder.lesson(navController: NavHostController) {
    composable(
        route = Routes.LESSON,
        arguments = listOf(
            navArgument("lessonId") { type = NavType.StringType },
            navArgument("focusItemId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
        ),
    ) { entry ->
        val lessonId = entry.arguments?.getString("lessonId").orEmpty()
        LessonScreen(
            lessonId = lessonId,
            onExit = { navController.popBackStack() },
            focusItemId = entry.arguments?.getString("focusItemId"),
        )
    }
}

private fun NavGraphBuilder.review(navController: NavHostController) {
    composable(Routes.REVIEW) {
        ReviewScreen(
            onBrowseVocabulary = { navController.navigate(Routes.VOCABULARY) },
            onGoHome = { navController.navigate(Routes.HOME) },
            // A due node opens the focused drill of its own exercises — the same
            // destination Home's weak-spot card uses.
            onOpenFocus = { lessonId, itemId ->
                navController.navigate(Routes.focusLesson(lessonId, itemId))
            },
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

private fun NavGraphBuilder.conversation(navController: NavHostController) {
    composable(
        route = Routes.CONVERSATION,
        arguments = listOf(
            navArgument("lessonId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
        ),
    ) { entry ->
        ConversationPracticeScreen(
            lessonId = entry.arguments?.getString("lessonId"),
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
            onOpenPaywall = { navController.navigate(Routes.PAYWALL) },
        )
    }
}
