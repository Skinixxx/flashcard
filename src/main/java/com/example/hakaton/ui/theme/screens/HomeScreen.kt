package com.example.hakaton.ui.screens

import android.R.id.input
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.hakaton.R
import com.example.hakaton.data.Folder
import com.example.hakaton.ui.theme.LightBackgroundGradient
import com.example.hakaton.ui.theme.screens.CardsListScreen
import com.example.hakaton.ui.theme.screens.FoldersScreen
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.hakaton.ui.theme.LightBackgroundGradient
import com.example.hakaton.ui.theme.screens.FoldersTab
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    // Состояние папок и выбранной папки
    val folders = remember { mutableStateListOf(
        Folder(1, "Колода 1"),
        Folder(2, "История")
    )}
    var selectedFolderId by remember { mutableStateOf(folders.firstOrNull()?.id ?: "") }
    var showAddDialog by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState()

    // Функция добавления новой папки
    fun addFolder(name: String) {
        val newId = (folders.size + 1)
        folders += Folder(newId, name)
    }

    // Сам диалог ввода
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Новая папка") },
            text = {
                var input by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("Название") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    addFolder(input.toString())
                }) { Text("Создать") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Отмена") }
            }
        )
    }

    Scaffold(
        modifier = Modifier.background(LightBackgroundGradient),
        topBar = {
            TopAppBar(title = { Text("Flashcards") })
        },
        floatingActionButton = {
            // Добавляем папку только на первой странице
            if (pagerState.currentPage == 0) {
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить папку")
                }
            }
        }
    ) { padding ->
        HorizontalPager(
            count = 3,
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { page ->
            when (page) {
                0 -> FoldersTab(
                    folders = folders,
                    onFolderClick = { selectedFolderId = it.toInt() },
                    selectedId = selectedFolderId.toString()
                )
                1 -> CardsListScreen(folderId = selectedFolderId)
                2 -> Text("Будем думать что сюда поместить…", modifier = Modifier.padding(16.dp))
            }
        }
    }
}