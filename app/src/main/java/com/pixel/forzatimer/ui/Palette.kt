package com.pixel.forzatimer.ui

import androidx.compose.ui.graphics.Color
import com.pixel.forzatimer.data.Series

val Ink = Color(0xFF0B0E13)
val Surface1 = Color(0xFF161B24)
val Surface2 = Color(0xFF1F2632)
val Outline = Color(0xFF2C3543)
val TextPrimary = Color(0xFFEDF1F7)
val TextMuted = Color(0xFF8B97A8)
val LiveGreen = Color(0xFF37B24D)

fun accentFor(series: Series): Color = when (series) {
    Series.TOURING_CAR -> Color(0xFFE5383B)
    Series.GT3 -> Color(0xFFF77F00)
    Series.PROTO_H -> Color(0xFF48CAE4)
    Series.INDYCAR -> Color(0xFF9D4EDD)
}
