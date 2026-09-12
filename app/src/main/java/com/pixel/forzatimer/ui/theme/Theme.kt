package com.pixel.forzatimer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.pixel.forzatimer.ui.Ink
import com.pixel.forzatimer.ui.Outline
import com.pixel.forzatimer.ui.Surface1
import com.pixel.forzatimer.ui.Surface2
import com.pixel.forzatimer.ui.TextMuted
import com.pixel.forzatimer.ui.TextPrimary

private val ForzaColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Ink,
    surface = Surface1,
    surfaceVariant = Surface2,
    outline = Outline,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextMuted
)

@Composable
fun ForzaTimerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ForzaColorScheme,
        typography = Typography,
        content = content
    )
}
