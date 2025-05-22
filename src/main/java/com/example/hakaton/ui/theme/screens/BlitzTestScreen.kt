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
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlitzTestScreen(
    folderId: Int,
    viewModel: MainViewModel,
    onFinish: () -> Unit
) {
    // 1) загрузка всех карточек данной папки
    val allCards by viewModel.cards.collectAsState()
    LaunchedEffect(folderId) { viewModel.loadCards(folderId) }

    // экран: настройки или сам тест или результат
    var phase by remember { mutableStateOf(Phase.Settings) }

    // настройки
    var countInput by remember { mutableStateOf("") }
    var useTimer by remember { mutableStateOf(false) }
    var timePerQuestion by remember { mutableStateOf("10") }

    // внутри теста
    var deck by remember { mutableStateOf<List<Card>>(emptyList()) }
    var index by remember { mutableStateOf(0) }
    var flipped by remember { mutableStateOf(false) }
    var correctCount by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(0) }
    var timerRunning by remember { mutableStateOf(false) }

    // когда начинается тест — формируем колоду и сбрасываем счётчики
    if (phase == Phase.Start) {
        LaunchedEffect(Unit) {
            // фильтруем и перемешиваем нужное кол-во карт
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
            phase = Phase.Testing
        }
    }

    // таймер
    if (phase == Phase.Testing && useTimer && timerRunning) {
        LaunchedEffect(index, timeLeft) {
            if (timeLeft > 0) {
                kotlinx.coroutines.delay(1000L)
                timeLeft--
            } else {
                // время вышло — считаем как неправильный и переходим далее
                timerRunning = false
                if (index < deck.lastIndex) {
                    index++; flipped = false; timerRunning = true
                } else {
                    phase = Phase.Result
                }
            }
        }
    }

    when (phase) {
        Phase.Settings -> {
            // --- Настройки теста ---
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
                    label = { Text("Кол-во вопросов (max ${allCards.size})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = useTimer, onCheckedChange = { useTimer = it })
                    Spacer(Modifier.width(8.dp))
                    Text("Таймер на вопрос")
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
                Button(onClick = { phase = Phase.Start }) {
                    Text("Начать тест")
                }
                Spacer(Modifier.height(32.dp))
                Button(onClick = onFinish, colors = ButtonDefaults.textButtonColors()) {
                    Text("Отмена")
                }
            }
        }
        Phase.Testing -> {
            // --- Показываем текущий вопрос / ответ ---
            val card = deck[index]
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // таймер
                if (useTimer) {
                    Text("Осталось: $timeLeft сек", style = MaterialTheme.typography.bodyLarge)
                }
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    CardItemSimple(card, flipped) { flipped = !flipped }
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        // неверно
                        nextQuestion(
                            isLast = index == deck.lastIndex,
                            onAdvance = { index++ ; flipped = false; timeLeft = timePerQuestion.toIntOrNull() ?: 10; timerRunning = true },
                            onFinish = { phase = Phase.Result }
                        )
                    }) { Text("❌") }
                    Button(onClick = {
                        // верно
                        correctCount++
                        nextQuestion(
                            isLast = index == deck.lastIndex,
                            onAdvance = { index++ ; flipped = false; timeLeft = timePerQuestion.toIntOrNull() ?: 10; timerRunning = true },
                            onFinish = { phase = Phase.Result }
                        )
                    }) { Text("✔️") }
                }
            }
        }
        Phase.Result -> {
            // --- Результат ---
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

        Phase.Start -> TODO()
    }
}

private fun nextQuestion(isLast: Boolean, onAdvance: () -> Unit, onFinish: () -> Unit) {
    if (isLast) onFinish() else onAdvance()
}

@Composable
private fun CardItemSimple(
    card: Card,
    flipped: Boolean,
    onClick: () -> Unit
) {
    Card(
        Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(8.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                if (!flipped) card.question else card.answer,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

private enum class Phase { Settings, Start, Testing, Result }
