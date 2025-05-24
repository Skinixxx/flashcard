package com.example.hakaton.ui.screens

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.hakaton.api.LocalJsonApi
import com.example.hakaton.data.Card
import com.example.hakaton.ui.theme.HakatonTheme
import com.example.hakaton.ui.theme.LightBackgroundGradient
import com.example.hakaton.ui.theme.view_model.MainViewModel
import kotlin.random.Random

class OverlayActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory(LocalJsonApi(applicationContext))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HakatonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    OverlayScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun OverlayScreen(viewModel: MainViewModel) {
    // вместо делегата — читаем .value
    val cardsState = viewModel.cards.collectAsState()
    val cards: List<Card> = cardsState.value

    val context = LocalContext.current

    var showAnswer by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        if (cards.isEmpty()) {
            Text("Нет карточек для показа", style = MaterialTheme.typography.bodyLarge)
        } else {
            val idx = Random.nextInt(cards.size)
            val card = cards[idx.coerceAtMost(cards.lastIndex)]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                Text("Напоминание!", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(16.dp))
                Text(
                    text = if (!showAnswer) card.question else card.answer,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(Modifier.height(24.dp))
                Button(onClick = { showAnswer = !showAnswer }) {
                    Text(if (!showAnswer) "Показать ответ" else "Скрыть ответ")
                }
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    if (context is Activity) {
                        context.finish()
                    }
                }) {
                    Text("Закрыть")
                }
            }
        }
    }
}
