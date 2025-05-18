package com.example.hakaton.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hakaton.data.Card
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CardItem(
    card: Card,
    onUpdate: (Card) -> Unit
) {
    var showAnswer by remember { mutableStateOf(false) }
    var showEdit by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)

    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(if (!showAnswer) card.question else card.answer)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = if (!showAnswer) "Показать ответ" else "Показать вопрос",
                    modifier = Modifier.clickable { showAnswer = !showAnswer }
                )
                IconButton(onClick = { showEdit = true }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Редактировать"
                    )
                }
            }
        }
    }
    if (showEdit) {
        CardEditDialog(
            initialCard = card,
            onDismiss = { showEdit = false },
            onSave = { updated -> showEdit = false; onUpdate(updated) }
        )
    }
}