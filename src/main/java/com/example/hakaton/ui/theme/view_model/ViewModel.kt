package com.example.hakaton.ui.theme.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hakaton.api.LocalJsonApi
import com.example.hakaton.data.Card
import com.example.hakaton.data.Folder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val api: LocalJsonApi) : ViewModel() {

    // Список папок
    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    val folders: StateFlow<List<Folder>> = _folders

    // Список карточек для текущей папки
    private val _cards = MutableStateFlow<List<Card>>(emptyList())
    val cards: StateFlow<List<Card>> = _cards

    // Какая папка сейчас выбрана (для reload после операций с карточками)
    private var currentFolderId: Int? = null

    init {
        // При старте подгружаем папки
        loadFolders()
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
            api.updateFolderName(id, newName)
            loadFolders()
        }
    }

    fun deleteFolder(id: Int) {
        viewModelScope.launch {
            api.deleteFolder(id)
            loadFolders()
        }
    }
}
