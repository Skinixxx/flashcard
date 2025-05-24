package com.example.hakaton.ui.theme.view_model

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hakaton.api.LocalJsonApi
import com.example.hakaton.data.Card
import com.example.hakaton.data.Folder
import com.example.hakaton.utils.ScheduleHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val api: LocalJsonApi) : ViewModel() {

    // Список папок
    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    val folders: StateFlow<List<Folder>> = _folders

    var selectedFolderId by mutableStateOf(0)

    private val _allCards = MutableStateFlow<List<Card>>(emptyList())
    val allCards: StateFlow<List<Card>> = _allCards

    // Список карточек для текущей папки
    private val _cards = MutableStateFlow<List<Card>>(emptyList())
    val cards: StateFlow<List<Card>> = _cards

    // Какая папка сейчас выбрана (для reload после операций с карточками)
    private var currentFolderId: Int? = null

    init {
        // При старте подгружаем папки
        loadFolders()
        loadAllCards()
    }

    fun loadAllCards() {
        viewModelScope.launch {
            _allCards.value = api.getAllCards()
        }
    }

    /** Загружает из API все папки */
    fun loadFolders() {
        viewModelScope.launch {
            _folders.value = api.getFolders()
        }
    }

    /** Добавляет новую папку по имени и перезагружает список */
    fun addFolder(name: String) {
        viewModelScope.launch {
            api.addFolder(name)
            loadFolders()
        }
    }

    /** Загружает карточки для заданной папки */
    fun loadCards(folderId: Int) {
        currentFolderId = folderId
        viewModelScope.launch {
            _cards.value = api.getCards(folderId)
        }
    }

    /** Добавляет карточку и перезагружает список карточек текущей папки */
    fun addCard(card: Card) {
        viewModelScope.launch {
            api.addCard(card)
            currentFolderId?.let { loadCards(it) }
        }
    }

    /** Обновляет карточку и перезагружает список карточек */
    fun updateCard(card: Card) {
        viewModelScope.launch {
            api.updateCard(card)
            currentFolderId?.let { loadCards(it) }
        }
    }

    /** Удаляет карточку и перезагружает список карточек */
    fun deleteCard(card: Card) {
        viewModelScope.launch {
            api.deleteCard(card.id)
            currentFolderId?.let { loadCards(it) }
        }
    }

    /** Фабрика для создания MainViewModel с передачей LocalJsonApi */
    @Suppress("UNCHECKED_CAST")
    class MainViewModelFactory(
        private val api: LocalJsonApi
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MainViewModel(api) as T
    }



    fun renameFolder(id: Int, newName: String) {
        viewModelScope.launch {
            api.renameFolder(id, newName)
            loadFolders()
        }
    }
    fun selectFolder(id: Int) {
        selectedFolderId = id
        loadCards(id)
    }


    fun deleteFolder(id: Int) {
        viewModelScope.launch {
            api.deleteFolder(id)
            loadFolders()
        }
    }

    fun scheduleCardReview(card: Card, intervalSeconds: Int, context: Context) {
        viewModelScope.launch {
            if (intervalSeconds <= 0 ) return@launch
            val newCard = card.copy(
                timerEnable = true,
                intervalSeconds = intervalSeconds,
                scheduledTime = System.currentTimeMillis() + intervalSeconds * 1000L
            )

            api.updateCard(newCard) // Исправлено с repository на api
            currentFolderId?.let { loadCards(it) } // Обновляем список карточек
            ScheduleHelper.scheduleOverlay(
                context = context,
                cardId = newCard.id,
                delaySec = intervalSeconds
            )
        }
    }
    fun scheduleFolderReview(folderId: Int, context: Context) {
        viewModelScope.launch {
            // Загружаем карточки папки
            val folderCards = api.getCards(folderId)

            // Планируем каждую карточку
            folderCards.forEach { card ->
                scheduleCardReview(
                    card = card,
                    intervalSeconds = card.intervalSeconds,
                    context = context
                )
            }

            // Обновляем список карточек
            loadCards(folderId)
        }
    }

}
