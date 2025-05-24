package com.example.hakaton.ui.theme.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.hakaton.ui.theme.view_model.MainViewModel

/**
 * Точка навигации: разбираем строковый folderId и передаём дальше в CardsScreen
 */
@Composable
fun CardsListScreen(
    navController: NavHostController,
    folderId: Int,
    viewModel: MainViewModel
) {
    CardsScreen(viewModel = viewModel, onBack = {})
}