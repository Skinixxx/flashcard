package com.example.hakaton.ui.theme.screens

import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hakaton.data.Folder
import com.example.hakaton.ui.theme.LightGrey
import com.example.hakaton.ui.theme.YellowGrey

@Composable
fun FoldersTab(
    folders: List<Folder>,
    onFolderClick: (String) -> Unit,
    selectedId: String
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrey)
            .padding(8.dp)
    ) {
        items(folders) { folder ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onFolderClick(folder.id.toString()) },
                colors = CardDefaults.cardColors(
                    containerColor = if (folder.id == selectedId.toInt()) LightGrey else YellowGrey
                )
            ) {
                Text(
                    text = folder.name,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
