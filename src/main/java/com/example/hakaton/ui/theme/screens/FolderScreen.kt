package com.example.hakaton.ui.theme.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.hakaton.data.Card
import com.example.hakaton.ui.theme.DialogField
import com.example.hakaton.ui.theme.DialogFieldType
import com.example.hakaton.ui.theme.UniversalDialog
import com.example.hakaton.ui.theme.components.CardCrudMenu
import com.example.hakaton.ui.theme.view_model.MainViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FolderScreen(
    folderId: Int,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onBlitz: () -> Unit
) {
    val cards by viewModel.cards.collectAsState()
    var menuFor by remember { mutableStateOf<Card?>(null) }
    var editCard by remember { mutableStateOf<Card?>(null) }
    var isNew by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(folderId) {
        viewModel.selectFolder(folderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Папка №$folderId") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = onBlitz) {
                        Icon(Icons.Default.Face, contentDescription = "Блиц-тест")
                    }
                }
            )
        },
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                ExtendedFloatingActionButton(
                    onClick = {
                        cards.forEach { card ->
                            viewModel.scheduleCardReview(
                                card = card,
                                intervalSeconds = card.intervalSeconds,
                                context = context
                            )
                        }
                    },
                    icon = { Icon(Icons.Default.Call, contentDescription = null) },
                    text = { Text("Запланировать все") }
                )

                ExtendedFloatingActionButton(
                    onClick = {
                        editCard = Card(
                            id = 0,
                            folderId = folderId,
                            question = "",
                            answer = "",
                            timerEnable = false,
                            intervalSeconds = 0,
                            scheduledTime = 0
                        )
                        isNew = true
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Новая") }
                )
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (cards.isEmpty()) {
                Box(Modifier.fillMaxSize()) {
                    Text("Карточек нет", Modifier.align(Alignment.Center))
                }
            } else {
                cards.forEach { card ->
                    var flipped by remember(card.id) { mutableStateOf(false) }

                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = { flipped = !flipped },
                                onLongClick = { menuFor = card }
                            ),
                        headlineContent = {
                            Text(
                                text = if (!flipped) card.question else card.answer,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        supportingContent = {
                            Text(
                                text = if (!flipped) "Нажмите, чтобы увидеть ответ"
                                else "Нажмите, чтобы вернуть вопрос",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                    Divider()

                    CardCrudMenu(
                        expanded = (menuFor == card),
                        onDismiss = { menuFor = null },
                        onEdit = {
                            editCard = card
                            isNew = false
                        },
                        onDelete = {
                            viewModel.deleteCard(card)
                            menuFor = null
                        },
                        onSchedule = {
                            viewModel.scheduleCardReview(
                                card = card,
                                intervalSeconds = card.intervalSeconds,
                                context = context
                            )
                            menuFor = null
                        }
                    )
                }
            }
        }

        editCard?.let { c ->
            UniversalDialog(
                title = if (isNew) "Новая карточка" else "Редактировать",
                fields = listOf(
                    DialogField("q", "Вопрос", DialogFieldType.STRING, c.question),
                    DialogField("a", "Ответ", DialogFieldType.STRING, c.answer),
                    DialogField("t", "Таймер", DialogFieldType.BOOLEAN, c.timerEnable),
                    DialogField("i", "Интервал (сек)", DialogFieldType.TIME, c.intervalSeconds)
                ),
                onDismiss = { editCard = null },
                onConfirm = { fields ->
                    // Извлекаем обновлённые значения из initialValue каждого поля:
                    val q = fields.first { it.key == "q" }.initialValue as String
                    val a = fields.first { it.key == "a" }.initialValue as String
                    val t = fields.first { it.key == "t" }.initialValue as Boolean
                    val i = fields.first { it.key == "i" }.initialValue as Int

                    // Копируем старую карточку, подставляя новые значения:
                    val newCard = c.copy(
                        question        = q,
                        answer          = a,
                        timerEnable     = t,
                        intervalSeconds = i
                    )

                    if (isNew) viewModel.addCard(newCard)
                    else        viewModel.updateCard(newCard)

                    editCard = null
                    menuFor  = null
                }

            )
        }
    }
}