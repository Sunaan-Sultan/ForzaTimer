package com.pixel.forzatimer.presentation.component

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.pixel.forzatimer.R

object AppIcons {

    object Res {
        @DrawableRes val ArrowBack = R.drawable.ic_arrow_back
        @DrawableRes val ChevronRight = R.drawable.ic_chevron_right
        @DrawableRes val Check = R.drawable.ic_check
        @DrawableRes val Settings = R.drawable.ic_settings
        @DrawableRes val Timer = R.drawable.ic_timer
        @DrawableRes val Warning = R.drawable.ic_warning
        @DrawableRes val LightMode = R.drawable.ic_light_mode
        @DrawableRes val DarkMode = R.drawable.ic_dark_mode
        @DrawableRes val Contrast = R.drawable.ic_contrast
        @DrawableRes val Speed = R.drawable.ic_speed
        @DrawableRes val Calendar = R.drawable.ic_calendar_month
        @DrawableRes val Refresh = R.drawable.ic_refresh
        @DrawableRes val Trophy = R.drawable.ic_trophy
    }

    val ArrowBack: Painter @Composable get() = painterResource(Res.ArrowBack)
    val ChevronRight: Painter @Composable get() = painterResource(Res.ChevronRight)
    val Check: Painter @Composable get() = painterResource(Res.Check)
    val Settings: Painter @Composable get() = painterResource(Res.Settings)
    val Timer: Painter @Composable get() = painterResource(Res.Timer)
    val Warning: Painter @Composable get() = painterResource(Res.Warning)
    val LightMode: Painter @Composable get() = painterResource(Res.LightMode)
    val DarkMode: Painter @Composable get() = painterResource(Res.DarkMode)
    val Contrast: Painter @Composable get() = painterResource(Res.Contrast)
    val Speed: Painter @Composable get() = painterResource(Res.Speed)
    val Calendar: Painter @Composable get() = painterResource(Res.Calendar)
    val Refresh: Painter @Composable get() = painterResource(Res.Refresh)
    val Trophy: Painter @Composable get() = painterResource(Res.Trophy)
}
