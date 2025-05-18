package com.example.hakaton.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun RegistrationScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Регистрация (заглушка)")
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            // после регистрации — переходим на домашний экран
            navController.navigate("home") {
                // чтобы нельзя было вернуться назад на регистрацию
                popUpTo("register") { inclusive = true }
            }
        }) {
            Text(text = "Продолжить")
        }
    }
}
