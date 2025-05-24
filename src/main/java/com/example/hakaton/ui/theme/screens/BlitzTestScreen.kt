package com.example.hakaton.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.hakaton.data.Card
import com.example.hakaton.ui.theme.view_model.MainViewModel
import kotlinx.coroutines.delay
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlitzTestScreen(
    folderId: Int,
    viewModel: MainViewModel,
    onFinish: () -> Unit
) {
    // 1) Получаем карточки из VM
    val allCards by viewModel.cards.collectAsState()
    LaunchedEffect(folderId) { viewModel.loadCards(folderId) }

    // фазы экрана
    var phase by remember { mutableStateOf(Phase.Settings) }

    // Параметры теста
    var countInput by remember { mutableStateOf("") }
    var useTimer by remember { mutableStateOf(false) }
    var timePerQuestion by remember { mutableStateOf("10") }

    // Состояние теста
    var deck by remember { mutableStateOf<List<Card>>(emptyList()) }
    var index by remember { mutableStateOf(0) }
    var flipped by remember { mutableStateOf(false) }
    var correctCount by remember { mutableStateOf(0) }

    // Таймер
    var timeLeft by remember { mutableStateOf(0) }
    var timerRunning by remember { mutableStateOf(false) }

    // Запуск теста
    LaunchedEffect(phase) {
        if (phase == Phase.Testing) {
            // формируем колоду
            val cnt = countInput.toIntOrNull()
                ?.coerceIn(1, allCards.size)
                ?: allCards.size
            deck = allCards.shuffled().take(cnt)
            index = 0
            correctCount = 0
            flipped = false
            if (useTimer) {
                timeLeft = timePerQuestion.toIntOrNull() ?: 10
                timerRunning = true
            }
        }
    }

    // Отсчет таймера
    LaunchedEffect(index, phase) {
        if (phase == Phase.Testing && useTimer) {
            timeLeft = timePerQuestion.toIntOrNull() ?: 10
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            // По окончании времени — переход
            if (phase == Phase.Testing) {
                if (index < deck.lastIndex) {
                    index++
                    flipped = false
                } else {
                    phase = Phase.Result
                }
            }
        }
    }

    when (phase) {
        Phase.Settings -> {
            // экран настроек
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Блиц-тест", style = MaterialTheme.typography.headlineMedium)
                OutlinedTextField(
                    value = countInput,
                    onValueChange = { countInput = it.filter(Char::isDigit) },
                    label = { Text("Вопросов (max ${allCards.size})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = useTimer, onCheckedChange = { useTimer = it })
                    Spacer(Modifier.width(8.dp))
                    Text("Таймер")
                }
                if (useTimer) {
                    OutlinedTextField(
                        value = timePerQuestion,
                        onValueChange = { timePerQuestion = it.filter(Char::isDigit) },
                        label = { Text("Секунд на вопрос") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
                Button(onClick = { phase = Phase.Testing }) {
                    Text("Начать")
                }
                TextButton(onClick = onFinish) {
                    Text("Отмена")
                }
            }
        }
        Phase.Testing -> {
            // экран теста
            val card = deck.getOrNull(index) ?: return
            var userAnswer by remember { mutableStateOf("") }
            var checkResult by remember { mutableStateOf<Boolean?>(null) }

            LaunchedEffect(index) {
                userAnswer = ""
                checkResult = null
            }

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Вопрос: ${index + 1}/${deck.size}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                if (useTimer) {
                    Text("Осталось: $timeLeft сек", style = MaterialTheme.typography.bodyLarge)
                }
                // Блок вопроса и ответа
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(card.question, style = MaterialTheme.typography.headlineSmall)

                    OutlinedTextField(
                        value = userAnswer,
                        onValueChange = { userAnswer = it },
                        label = { Text("Ваш ответ") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = checkResult == null,
                        isError = checkResult == false
                    )

                    if (checkResult != null) {
                        Column {
                            Text("Правильный ответ:", style = MaterialTheme.typography.bodySmall)
                            Text(card.answer)
                            Text(
                                text = if (checkResult == true) "✅ Верно!" else "❌ Неверно",
                                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
                            )
                        }
                    }
                }

                // Кнопки управления
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (checkResult == null) {
                        Button(
                            onClick = {
                                // Проверка ответа
                                val normalizedAnswer = userAnswer.trim().lowercase()
                                val correctAnswer = card.answer.trim().lowercase()
                                checkResult = normalizedAnswer == correctAnswer

                                if (useTimer) timerRunning = false
                            },
                            enabled = userAnswer.isNotBlank()
                        ) {
                            Text("Проверить ответ")
                        }
                    } else {
                        Button(
                            onClick = {
                                if (checkResult == true) correctCount++

                                if (index < deck.lastIndex) {
                                    index++
                                    flipped = false
                                    if (useTimer) {
                                        timeLeft = timePerQuestion.toIntOrNull() ?: 10
                                        timerRunning = true
                                    }
                                } else {
                                    phase = Phase.Result
                                }
                            }
                        ) {
                            Text("Следующий вопрос →")
                        }
                    }
                }
            }
        }
        Phase.Result -> {
            // экран результата
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Тест завершён!", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(16.dp))
                Text("Правильных: $correctCount из ${deck.size}", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(24.dp))
                Button(onClick = onFinish) {
                    Text("Готово")
                }
            }
        }
    }
}

private enum class Phase { Settings, Testing, Result }

