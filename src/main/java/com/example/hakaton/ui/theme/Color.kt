package com.example.hakaton.ui.theme

import androidx.compose.ui.graphics.Color


/////////////////
// MAIN COLOR //
////////////////
val DarkRed = Color(0xFF2D262E);
val YellowGrey = Color(0xFF68605B);
val DarkGrey = Color(0xFF6F7585);
val LightGrey = Color(0xFFCCCEDD);
val BrughtwGrey = Color(0xFFFCEDEA);
val LightPink = Color(0xFFBDA299);
val LightBrown = Color(0xFF714E4C);
val MiddleBrown = Color(0xFF452824);
val DarkBrown = Color(0xFF230D10);
val DeepBrown = Color(0xFF0C0608);

/////////////////
// PART COLOR //
////////////////
val SecPart = Color(78,45,41)
val LightFir= Color(0xFFE3C4BA)
val LightThr=Color(0xFFBC8177)

/////////////////
// FUNC COLOR //
////////////////
fun darken(baseColor:Color,factor:Float):Color{
    val f = factor.coerceIn(0f,1f)
    return Color(
        red = baseColor.red * (1-f),
        green = baseColor.green * (1-f),
        blue = baseColor.blue*(1-f),
        alpha = baseColor.alpha
    )
}
fun light(baseColor:Color,factor:Float):Color{
    val f = factor.coerceIn(0f,1f)
    return Color(
        red = baseColor.red / (1-f),
        green = baseColor.green / (1-f),
        blue = baseColor.blue/(1-f),
        alpha = baseColor.alpha
    )
}
fun opacity(baseColor:Color,factor: Float,more:Boolean):Color{
    val f = factor.coerceIn(0f,1f)
    if (more){
    return Color(
        red=baseColor.red,
        green = baseColor.green,
        blue = baseColor.blue,
        alpha=baseColor.alpha/(1-f)
    )
    }
    return Color(
        red=baseColor.red,
        green = baseColor.green,
        blue = baseColor.blue,
        alpha=baseColor.alpha*(1-f)
    )
}