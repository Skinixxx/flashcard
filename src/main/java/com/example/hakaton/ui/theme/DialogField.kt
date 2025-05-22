// DialogField.kt
package com.example.hakaton.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Типы полей, которые мы поддерживаем
enum class DialogFieldType { STRING, BOOLEAN, TIME }

// Описание одного поля диалога
data class DialogField<T>(
    val key: String,
    val label: String,
    val type: DialogFieldType,
    val initialValue: T
)

/**
 * UniversalDialog — универсальный диалог, который:
 *  • Показывает набор полей, заданных в `fields`
 *  • Хранит внутреннее состояние в `stateMap`
 *  • При нажатии OK возвращает обновлённый список DialogField с новыми значениями
 */
@Composable
fun UniversalDialog(
    title: String,
    fields: List<DialogField<*>>,
    onDismiss: () -> Unit,
    onConfirm: (List<DialogField<*>>) -> Unit
) {
    // Состояния всех полей по их ключу
    val stateMap = remember {
        mutableStateMapOf<String, Any?>().apply {
            fields.forEach { put(it.key, it.initialValue) }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(Modifier.fillMaxWidth()) {
                fields.forEach { field ->
                    Spacer(Modifier.height(8.dp))
                    when (field.type) {
                        DialogFieldType.STRING -> {
                            // Берём строку или пустую строку
                            val v = stateMap[field.key] as? String ?: ""
                            OutlinedTextField(
                                value = v,
                                onValueChange = { stateMap[field.key] = it },
                                label = { Text(field.label) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        DialogFieldType.BOOLEAN -> {
                            // Берём булево или false
                            val c = stateMap[field.key] as? Boolean ?: false
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = c,
                                    onCheckedChange = { stateMap[field.key] = it }
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(field.label)
                            }
                        }
                        DialogFieldType.TIME -> {
                            // Всегда приводим к строке
                            val raw = stateMap[field.key]
                            val s = when (raw) {
                                is String -> raw
                                is Number -> raw.toString()
                                else       -> raw?.toString() ?: ""
                            }
                            OutlinedTextField(
                                value = s,
                                onValueChange = {
                                    // Оставляем только цифры
                                    stateMap[field.key] = it.filter(Char::isDigit)
                                },
                                label = { Text(field.label) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("0") }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                // Собираем поле с новыми значениями
                val updated = fields.map { f ->
                    when (f.type) {
                        DialogFieldType.STRING ->
                            DialogField(f.key, f.label, f.type, stateMap[f.key] as String)
                        DialogFieldType.BOOLEAN ->
                            DialogField(f.key, f.label, f.type, stateMap[f.key] as Boolean)
                        DialogFieldType.TIME ->
                            DialogField(f.key, f.label, f.type, stateMap[f.key] as String)
                    }
                }
                onConfirm(updated)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
