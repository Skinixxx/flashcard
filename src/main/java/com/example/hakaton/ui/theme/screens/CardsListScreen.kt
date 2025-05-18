package com.example.hakaton.ui.theme.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.hakaton.data.Card

@Composable
fun CardsListScreen(
    navController: NavHostController,
    folderId: String
) {
    val cards = remember {
        listOf(
            Card(1, "Вопрос 1", "Ответ 1"),
            Card(2, "Вопрос 2", "Ответ 2", timerEnable = true, intervalSeconds = 30)
        )
    }
    LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        items(cards) { card ->
            CardItem(card) { updated -> /* TODO */ }
        }
    }
}