package com.example.hakaton.data

import androidx.compose.foundation.lazy.layout.IntervalList
import kotlinx.serialization.Serializable

@Serializable
data class Folder(
    val id:Int,
    val name:String,

)

@Serializable
data class Card(
    val id:Int,
    val question:String,
    val answer:String,
    val timerEnable:Boolean =false,
    val intervalSeconds:Int=0,
)
