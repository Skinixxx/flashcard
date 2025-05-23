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
    // Состояние списка карточек
    val cards by viewModel.cards.collectAsState()
    // Для контекстного меню и диалога
    var menuFor by remember { mutableStateOf<Card?>(null) }
    var editCard by remember { mutableStateOf<Card?>(null) }
    var isNew by remember { mutableStateOf(false) }

    // Загрузка при смене folderId
    LaunchedEffect(folderId) {
        viewModel.loadCards(folderId)
    }

    // Список
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

        items(cards) { card ->
            // каждую карточку можно флипнуть и вызвать меню долгим тапом
            var flipped by remember(card.id) { mutableStateOf(false) }

            M3Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .combinedClickable(
                        onClick = { flipped = !flipped },
                        onLongClick = { menuFor = card }
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(Modifier.padding(16.dp)) {
                    Text(
                        text = if (!flipped) card.question else card.answer,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Контекстное меню «Изменить/Удалить»
            CardCrudMenu(
                expanded = menuFor == card,
                onDismiss = { menuFor = null },
                onEdit = {
                    editCard = card
                    isNew = false
                },
                onDelete = {
                    viewModel.deleteCard(card)
                    menuFor = null
                }
            )
        }
    }

    // Кнопка «Добавить карточку»
    Button(
        onClick = {
            editCard = Card(
                id = 0,
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

    // Диалог создания/редактирования
    editCard?.let { c ->
        UniversalDialog(
            title = if (isNew) "Новая карточка" else "Редактировать карточку",
            fields = listOf(
                DialogField("q", "Вопрос", DialogFieldType.STRING, c.question),
                DialogField("a", "Ответ", DialogFieldType.STRING, c.answer),
                DialogField("t", "Таймер", DialogFieldType.BOOLEAN, c.timerEnable),
                DialogField("i", "Интервал (сек)", DialogFieldType.STRING, c.intervalSeconds.toString())
            ),
            onDismiss = { editCard = null },
            onConfirm = { fields ->
                val q = fields.first { it.key == "q" }.initialValue as String
                val a = fields.first { it.key == "a" }.initialValue as String
                val t = fields.first { it.key == "t" }.initialValue as Boolean
                val i = (fields.first { it.key == "i" }.initialValue as String)
                    .toIntOrNull() ?: c.intervalSeconds

                val newId = if (isNew) (cards.maxOfOrNull { it.id } ?: 0) + 1 else c.id
                val newCard = c.copy(
                    id = newId,
                    question = q,
                    answer = a,
                    timerEnable = t,
                    intervalSeconds = i
                )

                if (isNew) viewModel.addCard(newCard)
                else viewModel.updateCard(newCard)

                editCard = null
                menuFor = null
            }
        )
    }
}
