// SplashScreen.kt
package com.example.hakaton.ui.theme.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.example.hakaton.ui.theme.LightBackgroundGradient
import kotlinx.coroutines.delay

/**
 * @param onTimeout вызывать переход на следующий экран (Register)
 */
@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val transition = rememberInfiniteTransition()
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue  = 1f,
        animationSpec = infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(
                durationMillis = 1200,
                easing         = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.alpha(alpha))
    }

    LaunchedEffect(Unit) {
        delay(1500)
        onTimeout()
    }
}
