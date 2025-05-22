package com.example.hakaton.ui.theme.components

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.hakaton.ui.screens.*
import com.example.hakaton.ui.theme.screens.BlitzTestScreen
import com.example.hakaton.ui.theme.screens.FoldersScreen
import com.example.hakaton.ui.theme.screens.RegistrationScreen
import com.example.hakaton.ui.theme.screens.SplashScreen
import com.example.hakaton.ui.theme.view_model.MainViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    NavHost(navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onTimeout = {
                navController.navigate("register") { popUpTo("splash") { inclusive = true } }
            })
        }
        composable("register") {
            RegistrationScreen(onContinue = {
                navController.navigate("folders") { popUpTo("register") { inclusive = true } }
            })
        }
        composable("folders") {
            FoldersScreen(
                viewModel = viewModel,
                onFolderClick = { folderId ->
                    navController.navigate("folder/$folderId")
                }
            )
        }
        composable("folder/{folderId}") { back ->
            val folderId = back.arguments?.getString("folderId")?.toIntOrNull() ?: return@composable
            FolderScreen(
                folderId  = folderId,
                viewModel = viewModel,
                onBack    = { navController.popBackStack() },
                onBlitz   = { navController.navigate("blitz/$folderId") }
            )
        }
        composable("blitz/{folderId}") { back ->
            val folderId = back.arguments?.getString("folderId")?.toIntOrNull() ?: return@composable
            BlitzTestScreen(
                folderId = folderId,
                viewModel = viewModel,
                onFinish = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun FolderScreen(
    folderId: Int,
    viewModel: MainViewModel,
    onBack: () -> Boolean,
    onBlitz: () -> Unit
) {
    TODO("Not yet implemented")
}
