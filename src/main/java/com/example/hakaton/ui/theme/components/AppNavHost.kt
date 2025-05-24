// AppNavHost.kt
package com.example.hakaton.ui.theme.components

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.hakaton.ui.screens.HomeScreen
import com.example.hakaton.ui.theme.screens.*
import com.example.hakaton.ui.theme.view_model.MainViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    NavHost(navController, startDestination = "splash") {

        // 1) Splash → Register
        composable("splash") {
            SplashScreen(onTimeout = {
                navController.navigate("register") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        // 2) Register → Folders
        composable("register") {
            RegistrationScreen(onContinue = {
                // вместо "folders" сразу кидаем на HomeScreen
                navController.navigate("home") { popUpTo("register") { inclusive = true } }
            })
        }

        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                navController = navController // Добавляем navController в параметры
            )
        }

        composable("folders") {
            FoldersScreen(
                viewModel = viewModel,
                onFolderClick = { fid ->
                    navController.navigate("folder/$fid")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("folder/{folderId}") { backStackEntry ->
            val folderId = backStackEntry.arguments?.getString("folderId")?.toIntOrNull() ?: 0
            FolderScreen(
                folderId = folderId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onBlitz = { navController.navigate("blitz/$folderId") }
            )
        }

        composable("blitz/{folderId}") { back ->
            val folderId = back.arguments?.getString("folderId")?.toIntOrNull() ?: 0
            BlitzTestScreen(
                folderId = folderId,
                viewModel = viewModel,
                onFinish = { navController.popBackStack() }
            )
        }
    }
}