package com.pixel.forzatimer.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Immutable
data class AppColors(
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val surfacePressed: Color,
    val divider: Color,
    val outline: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val primary: Color,
    val onPrimary: Color,
    val primarySubtle: Color,
    val primaryBorder: Color,
    val danger: Color,
    val dangerSubtle: Color,
    val success: Color,
    val successSubtle: Color,
    val warning: Color,
    val warningSubtle: Color,
    val info: Color,
    val scrim: Color,
    val isLight: Boolean
)

fun lightAppColors(palette: AccentPalette) = AppColors(
    background = GroupedBgLight,
    surface = SurfaceLight,
    surfaceElevated = SurfaceElevatedLight,
    surfacePressed = SurfacePressedLight,
    divider = DividerLight,
    outline = OutlineLight,
    textPrimary = TextPrimaryLight,
    textSecondary = TextSecondaryLight,
    textTertiary = TextTertiaryLight,
    primary = palette.onLight,
    onPrimary = contentColorOn(palette.onLight),
    primarySubtle = palette.onLight.copy(alpha = 0.10f),
    primaryBorder = palette.onLight.copy(alpha = 0.28f),
    danger = DangerLight,
    dangerSubtle = DangerLight.copy(alpha = 0.10f),
    success = SuccessLight,
    successSubtle = SuccessLight.copy(alpha = 0.10f),
    warning = WarningLight,
    warningSubtle = WarningLight.copy(alpha = 0.10f),
    info = InfoLight,
    scrim = ScrimLight,
    isLight = true
)

fun darkAppColors(palette: AccentPalette) = AppColors(
    background = GroupedBgDark,
    surface = SurfaceDarkNew,
    surfaceElevated = SurfaceElevatedDark,
    surfacePressed = SurfacePressedDark,
    divider = DividerDark,
    outline = OutlineDark,
    textPrimary = TextPrimaryDark,
    textSecondary = TextSecondaryDark,
    textTertiary = TextTertiaryDark,
    primary = palette.onDark,
    onPrimary = contentColorOn(palette.onDark),
    primarySubtle = palette.onDark.copy(alpha = 0.16f),
    primaryBorder = palette.onDark.copy(alpha = 0.32f),
    danger = DangerDark,
    dangerSubtle = DangerDark.copy(alpha = 0.16f),
    success = SuccessDark,
    successSubtle = SuccessDark.copy(alpha = 0.16f),
    warning = WarningDark,
    warningSubtle = WarningDark.copy(alpha = 0.16f),
    info = InfoDark,
    scrim = ScrimDark,
    isLight = false
)

private fun AppColors.toColorScheme(): ColorScheme {
    val base = if (isLight) lightColorScheme() else darkColorScheme()
    return base.copy(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primarySubtle,
        onPrimaryContainer = primary,
        secondary = primary,
        onSecondary = onPrimary,
        secondaryContainer = primarySubtle,
        onSecondaryContainer = primary,
        tertiary = info,
        onTertiary = Color.White,
        background = background,
        onBackground = textPrimary,
        surface = surface,
        onSurface = textPrimary,
        surfaceVariant = surfacePressed,
        onSurfaceVariant = textSecondary,
        surfaceContainer = surfaceElevated,
        surfaceContainerHigh = surfaceElevated,
        surfaceContainerLow = surface,
        surfaceContainerHighest = surfacePressed,
        inverseSurface = textPrimary,
        inverseOnSurface = surface,
        error = danger,
        onError = Color.White,
        errorContainer = dangerSubtle,
        onErrorContainer = danger,
        outline = outline,
        outlineVariant = divider,
        scrim = scrim
    )
}

val LocalAppColors = staticCompositionLocalOf { darkAppColors(Palettes[0]) }

object AppTheme {
    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current

    val shapes: AppShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalAppShapes.current

    val dimens: Dimens
        @Composable
        @ReadOnlyComposable
        get() = LocalDimens.current
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    paletteIndex: Int = 0,
    content: @Composable () -> Unit
) {
    val palette = Palettes.getOrElse(paletteIndex) { Palettes[0] }
    val colors = remember(darkTheme, palette) {
        if (darkTheme) darkAppColors(palette) else lightAppColors(palette)
    }
    val colorScheme = remember(colors) { colors.toColorScheme() }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = M3Shapes
    ) {
        CompositionLocalProvider(
            LocalAppColors provides colors,
            LocalAppShapes provides AppShapes(),
            LocalDimens provides Dimens(),
            content = content
        )
    }
}
