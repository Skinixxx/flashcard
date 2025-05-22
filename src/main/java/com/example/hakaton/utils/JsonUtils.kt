package com.example.hakaton.utils

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

object JsonUtils {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    fun <T> saveToJsonFile(context: Context, fileName: String, data: T, serializer: (T) -> String) {
        val file = File(context.filesDir, fileName)
        file.writeText(serializer(data))
    }

    fun <T> loadFromJsonFile(context: Context, fileName: String, deserializer: (String) -> T): T? {
        val file = File(context.filesDir, fileName)
        return if (file.exists()) {
            val content = file.readText()
            deserializer(content)
        } else null
    }

    fun fileExists(context: Context, fileName: String): Boolean {
        return File(context.filesDir, fileName).exists()
    }
}