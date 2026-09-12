package com.pixel.forzatimer.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.pixel.forzatimer.data.Series
import com.pixel.forzatimer.ui.theme.TileAmber
import com.pixel.forzatimer.ui.theme.TileBlue
import com.pixel.forzatimer.ui.theme.TileCoral
import com.pixel.forzatimer.ui.theme.TileTeal
import com.pixel.forzatimer.ui.theme.rememberAccentOnSurface

private fun tileFor(series: Series): Color = when (series) {
    Series.TOURING_CAR -> TileBlue
    Series.GT3 -> TileAmber
    Series.PROTO_H -> TileTeal
    Series.INDYCAR -> TileCoral
}

@Composable
fun seriesAccent(series: Series): Color = rememberAccentOnSurface(tileFor(series))

fun seriesTile(series: Series): Color = tileFor(series)
