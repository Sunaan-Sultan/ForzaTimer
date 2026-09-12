package com.pixel.forzatimer.data

data class RaceSlot(
    val track: String,
    val layout: String,
    val laps: Int? = null,
    val weatherF: Int? = null,
    val timeOfDay: String? = null
)
