package com.example.hakaton.ui.theme.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun CardCrudMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSchedule: () -> Unit,
) {
    DropdownMenu(
        expanded          = expanded,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(text = { Text("Изменить") }, onClick = {
            onEdit()
            onDismiss()
        })
        DropdownMenuItem(text = { Text("Удалить") }, onClick = {
            onDelete()
            onDismiss()
        })
        DropdownMenuItem(
            text = { Text("Запланировать") },
            onClick = {
                onSchedule()
                onDismiss()
            }
        )
    }
}
