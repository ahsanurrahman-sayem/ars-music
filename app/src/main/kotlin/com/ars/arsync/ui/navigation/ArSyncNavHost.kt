package com.ars.arsync.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ars.arsync.ui.screens.home.HomeScreen
import com.ars.arsync.ui.screens.import.ImportScreen
import com.ars.arsync.ui.screens.library.LibraryScreen
import com.ars.arsync.ui.screens.player.PlayerScreen
import com.ars.arsync.ui.screens.settings.SettingsScreen

private const val NAV_ANIM_DURATION = 300

@Composable
fun ArSyncNavHost(
    navEvent: NavEvent?,
    onNavEventConsumed: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    LaunchedEffect(navEvent) {
        when (navEvent) {
            is NavEvent.NavigateToPlayer -> {
                navController.navigate(Screen.Player.createRoute(navEvent.trackId))
                onNavEventConsumed()
            }
            is NavEvent.NavigateToImport -> {
                navController.navigate(Screen.Import.route)
                onNavEventConsumed()
            }
            is NavEvent.NavigateToPlaylist -> {
                navController.navigate(Screen.PlaylistDetail.createRoute(navEvent.playlistId))
                onNavEventConsumed()
            }
            null -> Unit
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier.fillMaxSize(),
        enterTransition = {
            fadeIn(animationSpec = tween(NAV_ANIM_DURATION)) +
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(NAV_ANIM_DURATION)
                    )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(NAV_ANIM_DURATION)) +
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(NAV_ANIM_DURATION)
                    )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(NAV_ANIM_DURATION)) +
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(NAV_ANIM_DURATION)
                    )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(NAV_ANIM_DURATION)) +
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(NAV_ANIM_DURATION)
                    )
        }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToPlayer = { trackId ->
                    navController.navigate(Screen.Player.createRoute(trackId))
                },
                onNavigateToLibrary = {
                    navController.navigate(Screen.Library.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToImport = {
                    navController.navigate(Screen.Import.route)
                }
            )
        }

        composable(
            route = Screen.Player.route,
            arguments = listOf(navArgument("trackId") { type = NavType.LongType })
        ) { backStackEntry ->
            val trackId = backStackEntry.arguments?.getLong("trackId") ?: -1L
            PlayerScreen(
                trackId = trackId,
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(Screen.Library.route) {
            LibraryScreen(
                onNavigateToPlayer = { trackId ->
                    navController.navigate(Screen.Player.createRoute(trackId))
                },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(Screen.Import.route) {
            ImportScreen(
                onImportComplete = { trackId ->
                    navController.navigate(Screen.Player.createRoute(trackId)) {
                        popUpTo(Screen.Import.route) { inclusive = true }
                    }
                },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
