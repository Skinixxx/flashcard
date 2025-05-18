package com.example.hakaton.ui.theme

import android.app.Activity
import android.graphics.drawable.GradientDrawable
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Box

data class ColorPallet(
    val mainColor:Color,
    val singleTheme:Color,
    val background:Color,
    val formColor:Color,
    val border:Color,
    val fontColor:Color
)


val DarkPallet = ColorPallet(
    mainColor = LightBrown,
    singleTheme = LightBrown,
    background = MiddleBrown,
    formColor = darken(MiddleBrown,0.3f),
    border = LightGrey,
    fontColor = BrughtwGrey
)
val LightPallet= ColorPallet(
    mainColor = LightBrown,
    singleTheme = LightBrown,
    background = MiddleBrown,
    formColor = darken(MiddleBrown,0.3f),
    border = LightGrey,
    fontColor = BrughtwGrey
)


val LightBackgroundGradient = Brush.verticalGradient(
    colors = listOf(
        opacity(LightBrown,0.92f,true),
        opacity(MiddleBrown,0.86f,true),
        opacity(SecPart,0.96f,true),
        opacity(DarkBrown,0.9f,true),
        opacity(DeepBrown,0.95f,true)
    )
)
val DarkBackgroundGradient = Brush.verticalGradient(
    colors = listOf(
        LightFir,
        LightPink,
        LightThr,
        LightBrown
    )
)


private val DarkColorScheme = darkColorScheme(
    primary = DarkBrown,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun HakatonTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val colors = if(darkTheme) DarkPallet else LightPallet
    val backgroundGradient = if(darkTheme) DarkBackgroundGradient else LightBackgroundGradient

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
    ){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
        ){
            content()
        }
    }
}