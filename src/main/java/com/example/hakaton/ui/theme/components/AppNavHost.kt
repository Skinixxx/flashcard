// AppNavHost.kt
package com.example.hakaton.ui.theme.components

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
                navController.navigate("folders") {
                    popUpTo("register") { inclusive = true }
                }
            })
        }

        // 3) Список папок
        composable("folders") {
            FoldersScreen(
                viewModel      = viewModel,
                onFolderClick  = { fid ->
                    navController.navigate("folder/$fid")
                },
                onBack         = null
            )
        }

        // 4) Экран конкретной папки — карточки + блиц
        composable("folder/{folderId}") { back ->
            val folderId = back.arguments?.getString("folderId")!!.toInt()
            FolderScreen(
                folderId  = folderId,
                viewModel = viewModel,
                onBack    = { navController.popBackStack() },
                onBlitz   = { navController.navigate("blitz/$folderId") }
            )
        }

        // 5) Blitz-тест
        composable("blitz/{folderId}") { back ->
            val folderId = back.arguments?.getString("folderId")!!.toInt()
            BlitzTestScreen(
                folderId = folderId,
                viewModel = viewModel,
                onFinish = { navController.popBackStack() }
            )
        }
    }
}