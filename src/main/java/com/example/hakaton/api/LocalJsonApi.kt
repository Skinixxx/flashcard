package com.example.hakaton.api

import android.content.Context
import com.example.hakaton.data.Folder
import com.example.hakaton.data.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
import java.io.File

class LocalJsonApi(context: Context) {
    private val json = Json { prettyPrint=true; ignoreUnknownKeys=true }
    private val foldersFile = File(context.filesDir, "folders.json").apply { if (!exists()) writeText("[]") }
    private val cardsFile   = File(context.filesDir, "cards.json").apply   { if (!exists()) writeText("[]") }

    private suspend fun <T> load(file: File, serializer: kotlinx.serialization.KSerializer<List<T>>): List<T> =
        withContext(Dispatchers.IO) {
            json.decodeFromString(serializer, file.readText())
        }
    private suspend fun <T> save(file: File, list: List<T>, serializer: kotlinx.serialization.KSerializer<List<T>>) =
        withContext(Dispatchers.IO) {
            file.writeText(json.encodeToString(serializer, list))
        }

    // FOLDERS
    suspend fun getFolders() = load(foldersFile, ListSerializer(Folder.serializer()))
    suspend fun addFolder(name: String) {
        val all = getFolders().toMutableList()
        all += Folder((all.maxOfOrNull{it.id}?:0)+1, name)
        save(foldersFile, all, ListSerializer(Folder.serializer()))
    }
    suspend fun deleteFolder(id: Int) = save(
        foldersFile,
        getFolders().filterNot { it.id==id },
        ListSerializer(Folder.serializer())
    )
    suspend fun renameFolder(id:Int,name:String)=save(
        foldersFile,
        getFolders().map{ if(it.id==id)it.copy(name=name) else it },
        ListSerializer(Folder.serializer())
    )

    // CARDS
    suspend fun getCards(folderId: Int) = load(cardsFile, ListSerializer(Card.serializer()))
        .filter { it.folderId==folderId }
    suspend fun getAllCards() = load(cardsFile, ListSerializer(Card.serializer()))

    private suspend fun saveAllCards(list: List<Card>) = save(cardsFile, list, ListSerializer(Card.serializer()))
    suspend fun addCard(card: Card) {
        val all = getAllCards().toMutableList()
        all += card.copy(id= (all.maxOfOrNull{it.id})?.plus(1) ?: 1)
        saveAllCards(all)
    }
    suspend fun updateCard(card: Card) = saveAllCards(
        getAllCards().map{ if(it.id==card.id) card else it }
    )
    suspend fun deleteCard(id: Int) = saveAllCards(
        getAllCards().filterNot { it.id==id }
    )
}
