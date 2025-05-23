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
import com.example.hakaton.ui.theme.screens.FolderScreen
import com.example.hakaton.ui.theme.screens.FoldersScreen
import com.example.hakaton.ui.theme.view_model.MainViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel
) {
    val folders by viewModel.folders.collectAsState()
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = TODO()
    )
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Flashcards") })
        },
        // FAB оставляем пустым: в каждом экране будет свой
        floatingActionButton = {}
    ) { padding ->
        HorizontalPager(
            beyondViewportPageCount = 3,
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { page ->
            when (page) {
                // 0 — список папок
                0 -> FoldersScreen(
                    viewModel = viewModel,
                    onFolderClick = { folderId ->
                        // При выборе папки переключаемся на страницу 1
                        scope.launch { pagerState.animateScrollToPage(1) }
                        viewModel.loadCards(folderId)
                    },
                    onBack = null // на корневом экране кнопки «назад» нет
                )

                // 1 — экран конкретной папки с карточками
                1 -> {
                    // Если нет папок — показываем заглушку
                    if (folders.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Папки не найдены", style = MaterialTheme.typography.bodyLarge)
                        }
                    } else {
                        val folderId = folders.first()
                        FolderScreen(
                            folderId  = folderId,
                            viewModel  = viewModel,
                            onBack     = { scope.launch { pagerState.animateScrollToPage(0) } },
                            onBlitz    = { scope.launch { pagerState.animateScrollToPage(2) } }
                        )
                    }
                }

                // 2 — блиц-тест
                2 -> {
                    if (folders.isNotEmpty()) {
                        val folderId = folders.first()
                        BlitzTestScreen(
                            folderId  = folderId,
                            viewModel  = viewModel,
                            onFinish   = { scope.launch { pagerState.animateScrollToPage(1) } }
                        )
                    } else {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Нет папок для теста", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}
