// CardsScreen.kt
package com.example.hakaton.ui.theme.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.Card as M3Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hakaton.data.Card
import com.example.hakaton.ui.theme.DialogField
import com.example.hakaton.ui.theme.DialogFieldType
import com.example.hakaton.ui.theme.UniversalDialog
import com.example.hakaton.ui.theme.components.CardCrudMenu
import com.example.hakaton.ui.theme.view_model.MainViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardsScreen(
    folderId: Int,
    viewModel: MainViewModel
) {
    val cards by viewModel.cards.collectAsState()
    var menuFor by remember { mutableStateOf<Card?>(null) }
    var editCard by remember { mutableStateOf<Card?>(null) }
    var isNew by remember { mutableStateOf(false) }

    LaunchedEffect(folderId) {
        viewModel.loadCards(folderId)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        if (cards.isEmpty()) {
            item {
                Text(
                    "Карточек пока нет",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        items(cards) { c ->
            Box {
                M3Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .combinedClickable(
                            onClick = { /* flip внутри CardItem, если нужно */ },
                            onLongClick = { menuFor = c }
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(c.question, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                CardCrudMenu(
                    expanded = menuFor == c,
                    onDismiss = { menuFor = null },
                    onEdit = {
                        editCard = c
                        isNew = false
                    },
                    onDelete = {
                        viewModel.deleteCard(c)
                        menuFor = null
                    }
                )
            }
        }
    }

    Button(
        onClick = {
            editCard = Card(
                id = 1,
                folderId = folderId,
                question = "",
                answer = "",
                timerEnable = false,
                intervalSeconds = 0
            )
            isNew = true
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text("Добавить карточку")
    }

    editCard?.let { c ->
        UniversalDialog(
            title = if (isNew) "Новая карточка" else "Редактировать карточку",
            fields = listOf(
                DialogField("q", "Вопрос", DialogFieldType.STRING, c.question),
                DialogField("a", "Ответ",   DialogFieldType.STRING, c.answer),
                DialogField("t", "Таймер",  DialogFieldType.BOOLEAN, c.timerEnable),
                // Для простоты времени используем STRING и храним секунды как строку
                DialogField("i", "Интервал (сек)", DialogFieldType.STRING, c.intervalSeconds.toString())
            ),
            onDismiss = { editCard = null },
            onConfirm = { fields ->
                val q = fields.first { it.key == "q" }.initialValue as String
                val a = fields.first { it.key == "a" }.initialValue as String
                val t = fields.first { it.key == "t" }.initialValue as Boolean
                val i = (fields.first { it.key == "i" }.initialValue as String)
                    .toIntOrNull().let { it ?: c.intervalSeconds }

                val newId = if (isNew) {
                    (cards.maxOfOrNull { it.id } ?: 0) + 1
                } else c.id

                val newCard = c.copy(
                    id = newId,
                    question = q,
                    answer = a,
                    timerEnable = t,
                    intervalSeconds = i
                )

                if (isNew) viewModel.addCard(newCard)
                else        viewModel.updateCard(newCard)

                editCard = null
                menuFor = null
            }
        )
    }
}
