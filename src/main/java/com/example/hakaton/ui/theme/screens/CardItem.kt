package com.example.hakaton.ui.theme.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.hakaton.data.Card
import com.example.hakaton.ui.theme.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardItem(
    card: Card,
    onUpdate: (Card) -> Unit,
    modifier: Modifier = Modifier
) {
    var flipped by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(targetValue = if (flipped) 180f else 0f)

    // Основной «корпус» карточки с формой, тенью и фоном
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .graphicsLayer { rotationY = rotation }
            .combinedClickable(
                onClick = { flipped = !flipped },
                onLongClick = { showDialog = true }
            ),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp,
        color = HakatonTheme.palette.singleTheme
    ) {
        Box(Modifier.fillMaxSize()) {
            // Контент: вопрос или ответ
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

            // Индикатор стороны (необязательно)
            Text(
                text = if (flipped) "Ответ" else "Вопрос",
                style = MaterialTheme.typography.labelSmall,
                color = HakatonTheme.palette.border,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            )

            // Иконка «Edit» вместо отдельной кнопки убрана, по лонгклику открывается меню.
        }
    }

    // Диалог редактирования карточки
    if (showDialog) {
        UniversalDialog(
            title = "Редактировать карточку",
            fields = listOf(
                DialogField("question", "Вопрос", DialogFieldType.STRING, card.question),
                DialogField("answer",   "Ответ",  DialogFieldType.STRING, card.answer),
                DialogField("timer",    "Таймер", DialogFieldType.BOOLEAN, card.timerEnable),
                DialogField("interval", "Интервал (сек)", DialogFieldType.STRING, card.intervalSeconds.toString())
            ),
            onDismiss = { showDialog = false },
            onConfirm = { updatedFields ->
                val q = updatedFields.first { it.key == "question" }.initialValue as String
                val a = updatedFields.first { it.key == "answer"   }.initialValue as String
                val t = updatedFields.first { it.key == "timer"    }.initialValue as Boolean
                val i = updatedFields.first { it.key == "interval" }.initialValue.toString().toIntOrNull() ?: card.intervalSeconds

                onUpdate(card.copy(
                    question       = q,
                    answer         = a,
                    timerEnable    = t,
                    intervalSeconds = i
                ))
                showDialog = false
            }
        )
    }
}
