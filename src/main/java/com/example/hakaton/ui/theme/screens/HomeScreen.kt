package com.example.hakaton.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.hakaton.ui.theme.LightBackgroundGradient
import com.example.hakaton.ui.theme.components.AppNavHost
import com.example.hakaton.ui.theme.screens.BlitzTestScreen
import com.example.hakaton.ui.theme.screens.CardsListScreen
import com.example.hakaton.ui.theme.screens.CardsScreen
import com.example.hakaton.ui.theme.screens.FolderScreen
import com.example.hakaton.ui.theme.screens.FoldersScreen
import com.example.hakaton.ui.theme.view_model.MainViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    navController: NavHostController // Добавляем navController в параметры
) {
    val folders = viewModel.folders.collectAsState().value
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.background(LightBackgroundGradient),
        topBar = {
            TopAppBar(
                title = { Text("Flashcards") },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("settings") // Пример использования навигации
                    }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> FoldersScreen(
                    viewModel = viewModel,
                    onFolderClick = { folderId ->
                        // Используем навигацию вместо переключения страниц
                        navController.navigate("folder/$folderId")
                    },
                    onBack = { navController.popBackStack() }
                )

                1 -> CardsScreen(

                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )

                2 -> BlitzTestScreen(
                    folderId = viewModel.selectedFolderId,
                    viewModel = viewModel,
                    onFinish = { navController.popBackStack() }
                )
            }
        }
    }
}
