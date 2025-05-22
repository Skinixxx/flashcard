// api/LocalJsonApi.kt
package com.example.hakaton.api

import android.content.Context
import com.example.hakaton.data.Card
import com.example.hakaton.data.Folder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileNotFoundException

class LocalJsonApi(private val context: Context) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val foldersFile by lazy {
        File(context.filesDir, "folders.json").apply {
            if (!exists()) {
                createNewFile()
                writeText("[]") // Инициализация пустым массивом
            }
        }
    }

    private val cardsFile by lazy {
        File(context.filesDir, "cards.json").apply {
            if (!exists()) {
                createNewFile()
                writeText("[]") // Инициализация пустым массивом
            }
        }
    }

    // ================ FOLDERS ================ //

    suspend fun getFolders(): List<Folder> = withContext(Dispatchers.IO) {
        try {
            if (foldersFile.length() == 0L) emptyList()
            else json.decodeFromString(foldersFile.readText())
        } catch (e: Exception) {
            when (e) {
                is FileNotFoundException -> emptyList()
                else -> throw JsonStorageException("Error reading folders", e)
            }
        }
    }

    // Метод сохранения папок
    suspend fun saveFolders(folders: List<Folder>) = withContext(Dispatchers.IO) {
        try {
            foldersFile.writeText(json.encodeToString(folders))
        } catch (e: Exception) {
            throw JsonStorageException("Error saving folders", e)
        }
    }

    suspend fun addFolder(name: String): Folder {
        val folders = getFolders().toMutableList()
        val newId = (folders.maxOfOrNull { it.id } ?: 0) + 1
        return Folder(newId, name).apply {
            folders.add(this)
            saveFolders(folders)
        }
    }

    suspend fun deleteFolder(folderId: Int) {
        val folders = getFolders().toMutableList()
        folders.removeAll { it.id == folderId }
        saveFolders(folders)
    }

    // ================ CARDS ================ //

    suspend fun getCards(folderId: Int? = null): List<Card> = withContext(Dispatchers.IO) {
        try {
            val allCards = json.decodeFromString<List<Card>>(cardsFile.readText())
            folderId?.let { id ->
                allCards.filter { it.folderId == id }
            } ?: allCards.filter { it.folderId == null }
        } catch (e: Exception) {
            throw JsonStorageException("Error reading cards", e)
        }
    }


    suspend fun updateFolderName(id: Int, newName: String) {
        val list = getFolders().toMutableList()
        val idx = list.indexOfFirst { it.id == id }
        if (idx != -1) {
            list[idx] = list[idx].copy(name = newName)
            saveFolders(list)
        }
    }


    suspend fun getOrphanCards() = getCards(null)

    suspend fun getAllCards(): List<Card> = withContext(Dispatchers.IO) {
        try {
            if (cardsFile.length() == 0L) emptyList()
            else json.decodeFromString(cardsFile.readText())
        } catch (e: Exception) {
            throw JsonStorageException("Error reading all cards", e)
        }
    }

    suspend fun addCard(card: Card) {
        val cards = getAllCards().toMutableList()
        cards.add(card)
        saveCards(cards)
    }

    suspend fun updateCard(updatedCard: Card) {
        val cards = getAllCards().toMutableList()
        val index = cards.indexOfFirst { it.id == updatedCard.id }
        if (index != -1) {
            cards[index] = updatedCard
            saveCards(cards)
        }
    }

    suspend fun deleteCard(cardId: Int) {
        val cards = getAllCards().toMutableList()
        cards.removeAll { it.id == cardId }
        saveCards(cards)
    }

    private suspend fun saveCards(cards: List<Card>) = withContext(Dispatchers.IO) {
        try {
            cardsFile.writeText(json.encodeToString(cards))
        } catch (e: Exception) {
            throw JsonStorageException("Error saving cards", e)
        }
    }

    // ================ UTILS ================ //

    class JsonStorageException(message: String, cause: Throwable) :
        Exception(message, cause)

    companion object {
        fun generateCardId(existingCards: List<Card>): Int {
            return (existingCards.maxOfOrNull { it.id } ?: 0) + 1
        }
    }
}