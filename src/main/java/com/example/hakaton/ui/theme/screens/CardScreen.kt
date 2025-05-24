package com.example.hakaton.ui.theme.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.Card as M3Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.hakaton.data.Card
import com.example.hakaton.ui.theme.DialogField
import com.example.hakaton.ui.theme.DialogFieldType
import com.example.hakaton.ui.theme.HakatonTheme
import com.example.hakaton.ui.theme.UniversalDialog
import com.example.hakaton.ui.theme.components.CardCrudMenu
import com.example.hakaton.ui.theme.view_model.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val allCards by viewModel.allCards.collectAsState()
    var editCard by remember { mutableStateOf<Card?>(null) }
    var isNew by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Загружаем все карточки
    LaunchedEffect(Unit) {
        viewModel.loadAllCards()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Все карточки (${allCards.size})") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editCard = Card(
                    id = 0,
                    question = "",
                    answer = "",
                    folderId = null,
                    timerEnable = false,
                    intervalSeconds = 0
                )
                isNew = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить карточку")
            }
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.padding(padding),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allCards.size, key = { it }) { index ->
                val card = allCards[index]
                CardItem(
                    card = card,
                    onUpdate = { updated ->
                        viewModel.updateCard(updated)
                        viewModel.loadAllCards()           // <-- пересоздаём полный список
                    },
                    onDelete = { deleted ->
                        viewModel.deleteCard(deleted)
                        viewModel.loadAllCards()           // <-- и здесь тоже
                    },
                    onSchedule = { seconds ->
                        viewModel.scheduleCardReview(
                            card, seconds, context
                        )
                        // после расписания, если меняется nextReviewTime:
                        viewModel.loadAllCards()
                    },
                    modifier = Modifier
                        .padding(4.dp)
                        .aspectRatio(1.2f)
                )
            }
        }

        editCard?.let { c ->
            UniversalDialog(
                title  = if (isNew) "Новая карточка" else "Редактировать карточку",
                fields = listOf(
                    DialogField("q", "Вопрос", DialogFieldType.STRING, c.question),
                    DialogField("a", "Ответ",   DialogFieldType.STRING, c.answer),
                    DialogField("t", "Таймер",  DialogFieldType.BOOLEAN, c.timerEnable),
                    DialogField("i", "Интервал (сек)", DialogFieldType.TIME, c.intervalSeconds)
                ),
                onDismiss = { editCard = null },
                onConfirm = { fields ->
                    val q = fields.first { it.key == "q" }.initialValue as String
                    val a = fields.first { it.key == "a" }.initialValue as String
                    val t = fields.first { it.key == "t" }.initialValue as Boolean
                    val i = fields.first { it.key == "i" }.initialValue as Int

                    val newCard = c.copy(
                        question        = q,
                        answer          = a,
                        timerEnable     = t,
                        intervalSeconds = i
                    )

                    if (isNew) viewModel.addCard(newCard)
                    else        viewModel.updateCard(newCard)

                    // а после — снова перезагрузим
                    viewModel.loadAllCards()

                    editCard = null
                }
            )
        }
    }
}
