package com.example.hakaton.ui.theme

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// 1) Ваш объект палитры
data class ColorPallet(
    val mainColor: Color,
    val singleTheme: Color,
    val background: Color,
    val formColor: Color,
    val border: Color,
    val fontColor: Color
)

// 2) Определяем две палитры
val DarkPallet = ColorPallet(
    mainColor    = LightBrown,
    singleTheme  = LightBrown,
    background   = MiddleBrown,
    formColor    = darken(MiddleBrown, 0.3f),
    border       = LightGrey,
    fontColor    = BrughtwGrey
)
val LightPallet = ColorPallet(
    mainColor    = LightBrown,
    singleTheme  = LightBrown,
    background   = MiddleBrown,
    formColor    = darken(MiddleBrown, 0.3f),
    border       = LightGrey,
    fontColor    = BrughtwGrey
)

// 3) Фон‐градиенты
val LightBackgroundGradient = Brush.verticalGradient(
    listOf(
        opacity(LightBrown, 0.92f, true),
        opacity(MiddleBrown, 0.86f, true),
        opacity(SecPart,       0.96f, true),
        opacity(DarkBrown,     0.90f, true),
        opacity(DeepBrown,     0.95f, true)
    )
)
val DarkBackgroundGradient = Brush.verticalGradient(
    listOf(
        LightFir,
        LightPink,
        LightThr,
        LightBrown
    )
)

private val LocalColorPallet = staticCompositionLocalOf { LightPallet }

object HakatonTheme {
    /** Текущая палитра (берётся из CompositionLocal) */
    val palette: ColorPallet
        @Composable get() = LocalColorPallet.current
}

// ==== 7) Тема приложения, оборачивает контент в фон-градиент и даёт доступ к палитре ====

@Composable
fun HakatonTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // выбираем палитру и фоновый градиент
    val pallet = if (darkTheme) DarkPallet else LightPallet
    val backgroundGradient = if (darkTheme) DarkBackgroundGradient else LightBackgroundGradient

    CompositionLocalProvider(LocalColorPallet provides pallet) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
        ) {
            content()
        }
    }
}
