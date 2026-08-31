package com.example.magicloop.navigation

import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.magicloop.data.repository.ProjectRepository
import com.example.magicloop.data.repository.StreakRepository
import com.example.magicloop.ui.common.ViewModelFactory
import com.example.magicloop.ui.pattern.PatternViewModel
import com.example.magicloop.ui.projectdetail.ProjectDetailScreen
import com.example.magicloop.ui.projectdetail.ProjectDetailViewModel
import com.example.magicloop.ui.projectlist.ProjectListScreen
import com.example.magicloop.ui.projectlist.ProjectListViewModel
import com.example.magicloop.ui.streak.StreakViewModel

sealed class Screen(val route: String) {
    data object ProjectList : Screen("project_list")
    data object ProjectDetail : Screen("project_detail/{projectId}") {
        fun createRoute(projectId: Long) = "project_detail/$projectId"
    }
}

@Composable
fun MagicLoopNavHost(
    repository: ProjectRepository,
    streakRepository: StreakRepository,
    navController: NavHostController = rememberNavController()
) {
    val factory = remember(repository) { ViewModelFactory(repository,streakRepository) }

    NavHost(navController = navController, startDestination = Screen.ProjectList.route) {
        composable(Screen.ProjectList.route) {
            val listFactory = remember(repository, streakRepository) {
                ViewModelFactory(repository, streakRepository)
            }
            val viewModel: ProjectListViewModel = viewModel(factory = listFactory)
            val streakViewModel: StreakViewModel = viewModel(factory = listFactory)

            ProjectListScreen(
                viewModel = viewModel,
                streakViewModel = streakViewModel,
                onProjectClick = { projectId ->
                    navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                }
            )
        }

        composable(Screen.ProjectDetail.route) { backStackEntry ->
            val projectId = backStackEntry.arguments
                ?.getString("projectId")?.toLongOrNull() ?: return@composable

            val context = LocalContext.current
            val detailFactory = remember(repository, streakRepository, projectId) {
                ViewModelFactory(repository, streakRepository, projectId)
            }
            val patternFactory = remember(repository, streakRepository, projectId, context) {
                ViewModelFactory(repository, streakRepository, projectId, context.applicationContext)
            }

            val viewModel: ProjectDetailViewModel = viewModel(factory = detailFactory)
            val patternViewModel: PatternViewModel = viewModel(factory = patternFactory)

            ProjectDetailScreen(
                viewModel = viewModel,
                patternViewModel = patternViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}