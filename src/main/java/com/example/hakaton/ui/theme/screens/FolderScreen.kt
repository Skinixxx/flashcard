package com.example.hakaton.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.hakaton.data.Folder

@Composable
fun FoldersScreen(navController: NavHostController) {
    val folders = remember {
        listOf(
            Folder(1, "Колода 1"),
            Folder(2, "История"),
            Folder(3, "Наука")
        )
    }
    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(folders) { folder ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { navController.navigate("cards/${folder.id}") }
                ) {
                    Text(folder.name, modifier = Modifier.padding(16.dp))
                }
            }
        }
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Добавить папку")
        }
    }
}