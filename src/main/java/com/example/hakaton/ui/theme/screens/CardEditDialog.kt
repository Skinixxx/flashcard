package com.example.hakaton.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hakaton.data.Card

@Composable
fun CardEditDialog(
    initialCard: Card,
    onDismiss: () -> Unit,
    onSave: (Card) -> Unit
) {
    var question by remember { mutableStateOf(initialCard.question) }
    var answer by remember { mutableStateOf(initialCard.answer) }
    var timerEnabled by remember { mutableStateOf(initialCard.timerEnable) }
    var intervalText by remember { mutableStateOf(initialCard.intervalSeconds.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редактировать карточку") },
        text = {
            Column {
                TextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text("Вопрос") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = answer,
                    onValueChange = { answer = it },
                    label = { Text("Ответ") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = timerEnabled,
                        onCheckedChange = { timerEnabled = it }
                    )
                    Text("Таймер")
                }
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = intervalText,
                    onValueChange = { intervalText = it.filter(Char::isDigit) },
                    label = { Text("Интервал (сек)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    initialCard.copy(
                        question = question,
                        answer = answer,
                        timerEnable = timerEnabled,
                        intervalSeconds = intervalText.toIntOrNull() ?: 0
                    )
                )
            }) { Text("Сохранить") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Отмена") }
        }
    )
}