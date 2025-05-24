// DialogField.kt
package com.example.hakaton.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlin.reflect.KClass

enum class DialogFieldType { STRING, BOOLEAN, TIME }

data class DialogField<T>(
    val key: String,
    val label: String,
    val type: DialogFieldType,
    val initialValue: T
)

@Composable
fun UniversalDialog(
    title: String,
    fields: List<DialogField<*>>,
    onDismiss: () -> Unit,
    onConfirm: (List<DialogField<*>>) -> Unit
) {
    // stateMap хранит текущее значение каждого поля
    val stateMap = remember {
        mutableStateMapOf<String, Any?>().also { map ->
            fields.forEach { map[it.key] = it.initialValue }
        }
    }

    fun <T : Any> getValue(key: String, cls: KClass<T>): T =
        (stateMap[key] as? T) ?: throw IllegalStateException("Wrong type for $key")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                fields.forEach { field ->
                    Spacer(Modifier.height(8.dp))
                    when (field.type) {
                        DialogFieldType.STRING -> {
                            val v = getValue(field.key, String::class)
                            OutlinedTextField(
                                value = v,
                                onValueChange = { stateMap[field.key] = it },
                                label = { Text(field.label) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        DialogFieldType.BOOLEAN -> {
                            val v = getValue(field.key, Boolean::class)
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable { stateMap[field.key] = !v },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = v,
                                    onCheckedChange = { stateMap[field.key] = it }
                                )
                                Text(field.label, Modifier.padding(start = 8.dp))
                            }
                        }
                        DialogFieldType.TIME -> {
                            // храним число секунд
                            val num = getValue(field.key, Number::class).toInt()
                            var text by remember { mutableStateOf(num.toString()) }
                            LaunchedEffect(num) { text = num.toString() }
                            OutlinedTextField(
                                value = text,
                                onValueChange = {
                                    val filtered = it.filter(Char::isDigit)
                                    text = filtered
                                    stateMap[field.key] = filtered.toIntOrNull() ?: 0
                                },
                                label = { Text(field.label) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                visualTransformation = NumberTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                // собираем новые поля в том же порядке
                val result = fields.map { field ->
                    when (field.type) {
                        DialogFieldType.STRING -> DialogField(
                            field.key, field.label, field.type,
                            getValue(field.key, String::class)
                        )
                        DialogFieldType.BOOLEAN -> DialogField(
                            field.key, field.label, field.type,
                            getValue(field.key, Boolean::class)
                        )
                        DialogFieldType.TIME -> DialogField(
                            field.key, field.label, field.type,
                            getValue(field.key, Number::class).toInt()
                        )
                    }
                }
                onConfirm(result)
            }) {
                Text("Подтвердить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
private class NumberTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText =
        TransformedText(AnnotatedString(text.text.filter { it.isDigit() }), OffsetMapping.Identity)
}
