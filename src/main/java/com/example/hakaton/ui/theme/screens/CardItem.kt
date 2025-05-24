package com.example.hakaton.ui.theme.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.hakaton.data.Card
import com.example.hakaton.ui.theme.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardItem(
    card: Card,
    onUpdate: (Card) -> Unit,
    onDelete: (Card) -> Unit,
    onSchedule: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var flipped by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }
    var editCard by remember { mutableStateOf<Card?>(null) }
    val rotation by animateFloatAsState(targetValue = if (flipped) 180f else 0f)
    var showScheduleDialog by remember { mutableStateOf(false) }

    // Градиент для фона карточки
    val bgBrush = Brush.verticalGradient(
        colors = listOf(
            HakatonTheme.palette.mainColor,
            HakatonTheme.palette.singleTheme
        )
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .graphicsLayer { rotationY = rotation }
            .combinedClickable(
                onClick = { flipped = !flipped },
                onLongClick = { menuExpanded = true }
            ),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp,
        color = Color.Transparent
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(bgBrush)
        ) {
            if (rotation <= 90f) {
                Text(
                    text = card.question,
                    style = MaterialTheme.typography.bodyLarge,
                    color = HakatonTheme.palette.fontColor,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else {
                Text(
                    text = card.answer,
                    style = MaterialTheme.typography.bodyLarge,
                    color = HakatonTheme.palette.fontColor,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .graphicsLayer { rotationY = 180f }
                        .padding(16.dp)
                )
            }

            Text(
                text = if (flipped) "Ответ" else "Вопрос",
                style = MaterialTheme.typography.labelSmall,
                color = HakatonTheme.palette.border,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            )

            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Menu",
                tint = HakatonTheme.palette.fontColor,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clickable { menuExpanded = true }
            )
        }
    }

    // Контекстное меню
    DropdownMenu(
        expanded = menuExpanded,
        onDismissRequest = { menuExpanded = false }
    ) {
        DropdownMenuItem(
            text = { Text("Изменить") },
            onClick = {
                menuExpanded = false
                editCard = card      // показываем диалог ниже
            }
        )
        DropdownMenuItem(
            text = { Text("Удалить") },
            onClick = {
                menuExpanded = false
                onDelete(card)
            }
        )
        DropdownMenuItem(
            text = { Text("Запланировать") },
            onClick = {
                menuExpanded = false
                showScheduleDialog = true
            }
        )

        if (showScheduleDialog) {
            ScheduleDialog(
                onDismiss = { showScheduleDialog = false },
                onConfirm = { seconds ->
                    onSchedule(seconds)
                }
            )
        }

    }

    // Диалог редактирования карточки
    editCard?.let { c ->
        UniversalDialog(
            title = "Редактировать карточку",
            fields = listOf(
                DialogField("q", "Вопрос",   DialogFieldType.STRING,  c.question),
                DialogField("a", "Ответ",     DialogFieldType.STRING,  c.answer),
                DialogField("t", "Таймер",    DialogFieldType.BOOLEAN, c.timerEnable),
                DialogField("i", "Интервал",  DialogFieldType.TIME,    c.intervalSeconds)
            ),
            onDismiss = { editCard = null },
            onConfirm = { fields ->
                val q = fields.first { it.key == "q" }.initialValue as String
                val a = fields.first { it.key == "a" }.initialValue as String
                val t = fields.first { it.key == "t" }.initialValue as Boolean
                val i = fields.first { it.key == "i" }.initialValue as Int
                onUpdate(c.copy(
                    question       = q,
                    answer         = a,
                    timerEnable    = t,
                    intervalSeconds= i
                ))
                editCard = null
            }
        )
    }
}


@Composable
fun ScheduleDialog(
    onDismiss: () -> Unit,
    onConfirm: (seconds: Int) -> Unit
) {
    var timeInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Запланировать повторение") },
        text = {
            Column {
                OutlinedTextField(
                    value = timeInput,
                    onValueChange = { timeInput = it.filter { c -> c.isDigit() } },
                    label = { Text("Время в секундах") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Через сколько секунд показать карточку?")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val seconds = timeInput.toIntOrNull() ?: 0
                    if (seconds > 0) {
                        onConfirm(seconds)
                    }
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}