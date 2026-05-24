package com.example

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.MainViewModel
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.GraphScreen
import com.example.ui.screens.HomeScreen

@Composable
fun NexusAppNavGraph(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = "home", modifier = modifier) {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToEditor = { noteId ->
                    navController.navigate("editor/${noteId ?: 0L}")
                },
                onNavigateToGraph = { navController.navigate("graph") }
            )
        }
        composable(
            route = "editor/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L
            EditorScreen(
                noteId = noteId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToNote = { id -> 
                    navController.navigate("editor/${id}") 
                }
            )
        }
        composable("graph") {
            GraphScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToEditor = { noteId ->
                    navController.navigate("editor/${noteId}")
                }
            )
        }
    }
}
