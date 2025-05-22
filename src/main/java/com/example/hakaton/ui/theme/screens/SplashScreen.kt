package com.example.hakaton.ui.theme.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.hakaton.R
import com.example.hakaton.ui.theme.LightBackgroundGradient
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    // для анимации прозрачности
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Логотип приложения

            // Пульсирующий индикатор
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .alpha(alpha)
            )
        }
    }

    // Ждём 1.5 секунды и переходим на регистрацию
    LaunchedEffect(Unit) {
        delay(1500)
        navController.navigate("register") {
            popUpTo("splash") { inclusive = true }
        }
    }
}
