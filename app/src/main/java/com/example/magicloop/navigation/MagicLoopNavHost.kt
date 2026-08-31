package com.example.magicloop.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.magicloop.MagicLoopApplication
import com.example.magicloop.data.repository.BadgeRepository
import com.example.magicloop.data.repository.ProjectRepository
import com.example.magicloop.data.repository.StreakRepository
import com.example.magicloop.data.repository.YarnRepository
import com.example.magicloop.gamification.BadgeChecker
import com.example.magicloop.gamification.BadgeUnlockEvents
import com.example.magicloop.notification.ReminderScheduler
import com.example.magicloop.ui.archive.ArchiveScreen
import com.example.magicloop.ui.archive.ArchiveViewModel
import com.example.magicloop.ui.common.ViewModelFactory
import com.example.magicloop.ui.pattern.PatternViewModel
import com.example.magicloop.ui.projectdetail.ProjectDetailScreen
import com.example.magicloop.ui.projectdetail.ProjectDetailViewModel
import com.example.magicloop.ui.projectlist.ProjectListScreen
import com.example.magicloop.ui.projectlist.ProjectListViewModel
import com.example.magicloop.ui.settings.ReminderSettingsScreen
import com.example.magicloop.ui.settings.ReminderSettingsViewModel
import com.example.magicloop.ui.stash.StashScreen
import com.example.magicloop.ui.stash.StashViewModel
import com.example.magicloop.ui.streak.StreakViewModel

sealed class Screen(val route: String) {
    data object ProjectList : Screen("project_list")
    data object ProjectDetail : Screen("project_detail/{projectId}") {
        fun createRoute(projectId: Long) = "project_detail/$projectId"
    }
    data object Settings : Screen("settings")
    data object Archive : Screen("archive")

    data object Stash : Screen("stash")
}

@Composable
fun MagicLoopNavHost(
    repository: ProjectRepository,
    streakRepository: StreakRepository,
    badgeRepository: BadgeRepository,
    badgeChecker: BadgeChecker,
    yarnRepository: YarnRepository,
    navController: NavHostController = rememberNavController()
) {
    val factory = remember(repository) { ViewModelFactory(repository,streakRepository,badgeRepository,badgeChecker,yarnRepository) }



    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        BadgeUnlockEvents.events.collect { badge ->
            snackbarHostState.showSnackbar(
                message = "Nova značka: ${badge.title}",
                withDismissAction = true
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {

    NavHost(navController = navController, startDestination = Screen.ProjectList.route) {
        composable(Screen.ProjectList.route) {
            val listFactory = remember(repository, streakRepository) {
                ViewModelFactory(repository, streakRepository, badgeRepository, badgeChecker, yarnRepository)
            }
            val viewModel: ProjectListViewModel = viewModel(factory = listFactory)
            val streakViewModel: StreakViewModel = viewModel(factory = listFactory)

            ProjectListScreen(
                viewModel = viewModel,
                streakViewModel = streakViewModel,
                onProjectClick = { projectId ->
                    navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onArchiveClick = {
                    navController.navigate(Screen.Archive.route)
                },
                onStashClick = {
                    navController.navigate(Screen.Stash.route)
                }
            )
        }



        composable(Screen.ProjectDetail.route) { backStackEntry ->
            val projectId = backStackEntry.arguments
                ?.getString("projectId")?.toLongOrNull() ?: return@composable

            val context = LocalContext.current
            val detailFactory = remember(repository, streakRepository, badgeRepository, badgeChecker, projectId, context) {
                ViewModelFactory(repository, streakRepository, badgeRepository, badgeChecker, yarnRepository,projectId, context.applicationContext)
            }
            val patternFactory = remember(repository, streakRepository, projectId, context) {
                ViewModelFactory(repository, streakRepository,badgeRepository,badgeChecker,yarnRepository, projectId, context.applicationContext)
            }

            val viewModel: ProjectDetailViewModel = viewModel(factory = detailFactory)
            val patternViewModel: PatternViewModel = viewModel(factory = patternFactory)

            ProjectDetailScreen(
                viewModel = viewModel,
                patternViewModel = patternViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Archive.route) {
            val factory = remember(repository) { ViewModelFactory(repository, streakRepository, badgeRepository, badgeChecker,yarnRepository) }
            val viewModel: ArchiveViewModel = viewModel(factory = factory)
            ArchiveScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onProjectClick = { projectId ->
                    navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                }
            )
        }

        composable(Screen.Settings.route) {
            val context = LocalContext.current
            val app = context.applicationContext as MagicLoopApplication

            val settingsViewModel = remember {
                ReminderSettingsViewModel(
                    preferences = app.reminderPreferences,
                    onScheduleChanged = { enabled, hour, minute ->
                        if (enabled) {
                            ReminderScheduler.schedule(app, hour, minute)
                        } else {
                            ReminderScheduler.cancel(app)
                        }
                    }
                )
            }

            ReminderSettingsScreen(viewModel = settingsViewModel)
        }

        composable(Screen.Stash.route) {
            val factory = remember(yarnRepository) {
                ViewModelFactory(repository, streakRepository, badgeRepository, badgeChecker, yarnRepository)
            }
            val viewModel: StashViewModel = viewModel(factory = factory)
            StashScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
    }}
    }
}


