package com.example.hakaton.data

import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable

@Serializable
data class Folder(
    val id: Int,
    val name: String,
    val cards: List<Card> = emptyList() // Необязательная вложенность
)

// data/Card.kt
@Serializable
data class Card(
    val id: Int,
    val folderId: Int? = null, // Необязательный параметр
    val question: String,
    val answer: String,
    val timerEnable: Boolean = false,
    val intervalSeconds: Int = 0
)
