package com.example.hakaton.ui.theme.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hakaton.data.Folder
import com.example.hakaton.ui.theme.DialogField
import com.example.hakaton.ui.theme.DialogFieldType
import com.example.hakaton.ui.theme.UniversalDialog
import com.example.hakaton.ui.theme.components.CardCrudMenu
import com.example.hakaton.ui.theme.view_model.MainViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FoldersScreen(
    viewModel: MainViewModel,
    onFolderClick: (Int) -> Unit,
    onBack: (() -> Unit)? = null
) {
    val allFolders by viewModel.folders.collectAsState()
    var query by remember { mutableStateOf("") }
    var menuFor by remember { mutableStateOf<Folder?>(null) }
    var editFolder by remember { mutableStateOf<Folder?>(null) }
    var isNew by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadFolders()
    }

    // отфильтрованный список
    val folders = remember(allFolders, query) {
        if (query.isBlank()) allFolders
        else allFolders.filter {
            it.name.contains(query.trim(), ignoreCase = true)
        }
    }

    // nullable-слот навигационной иконки
    val navIcon: (@Composable () -> Unit)? = onBack?.let { back ->
        @Composable {
            IconButton(onClick = back) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { "Папки" },
                navigationIcon = navIcon
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editFolder = Folder(id = 0, name = "")
                isNew = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить папку")
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                placeholder = { Text("Поиск папок…") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            Spacer(Modifier.height(4.dp))

            if (folders.isEmpty()) {
                Box(Modifier.fillMaxSize()) {
                    Text(
                        text = if (allFolders.isEmpty()) "Папок ещё нет" else "Ничего не найдено",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(folders, key = { it.id }) { folder ->
                        Card(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .combinedClickable(
                                    onClick = { onFolderClick(folder.id) },
                                    onLongClick = { menuFor = folder }
                                ),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    folder.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                        CardCrudMenu(
                            expanded = menuFor == folder,
                            onDismiss = { menuFor = null },
                            onEdit = {
                                editFolder = folder
                                isNew = false
                            },
                            onDelete = {
                                viewModel.deleteFolder(folder.id)
                                menuFor = null
                            }
                        )
                    }
                }
            }
        }

        editFolder?.let { f ->
            UniversalDialog(
                title = if (isNew) "Новая папка" else "Переименовать папку",
                fields = listOf(
                    DialogField("name", "Название", DialogFieldType.STRING, f.name)
                ),
                onDismiss = { editFolder = null },
                onConfirm = { fields ->
                    val name = (fields.first { it.key == "name" }.initialValue as String).trim()
                    if (name.isNotEmpty()) {
                        if (isNew) viewModel.addFolder(name)
                        else viewModel.renameFolder(f.id, name)
                    }
                    editFolder = null
                    menuFor = null
                }
            )
        }
    }
}
