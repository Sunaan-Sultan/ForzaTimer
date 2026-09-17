package com.pixel.forzatimer.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import com.pixel.forzatimer.data.ScheduleData
import com.pixel.forzatimer.data.ThemeMode
import com.pixel.forzatimer.presentation.component.AppIcons
import com.pixel.forzatimer.presentation.component.design.AppScaffold
import com.pixel.forzatimer.presentation.component.design.AppTopBar
import com.pixel.forzatimer.presentation.component.design.InsetDivider
import com.pixel.forzatimer.presentation.component.design.SettingsGroup
import com.pixel.forzatimer.presentation.component.design.SettingsRow
import com.pixel.forzatimer.ui.asDay
import com.pixel.forzatimer.ui.theme.AppTheme
import com.pixel.forzatimer.ui.theme.Palettes

@Composable
private fun iconFor(mode: ThemeMode): Painter = when (mode) {
    ThemeMode.SYSTEM -> AppIcons.Contrast
    ThemeMode.LIGHT -> AppIcons.LightMode
    ThemeMode.DARK -> AppIcons.DarkMode
}

@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    paletteIndex: Int,
    onThemeModeChange: (ThemeMode) -> Unit,
    onPaletteChange: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    val colors = AppTheme.colors
    val dimens = AppTheme.dimens

    AppScaffold(
        topBar = {
            AppTopBar(
                title = "Settings",
                eyebrow = "Forza Timer",
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start = dimens.screenPadding,
                end = dimens.screenPadding,
                top = dimens.listTopPadding,
                bottom = dimens.listBottomPadding
            ),
            verticalArrangement = Arrangement.spacedBy(dimens.space24)
        ) {
            item {
                SettingsGroup(
                    title = "Appearance",
                    footnote = "System follows your device setting."
                ) {
                    ThemeMode.entries.forEachIndexed { index, mode ->
                        if (index > 0) InsetDivider(dimens.dividerInsetWithIcon)
                        SettingsRow(
                            title = mode.label,
                            leadingIcon = iconFor(mode),
                            leadingIconTint = if (themeMode == mode) {
                                colors.primary
                            } else {
                                colors.textSecondary
                            },
                            showChevron = false,
                            trailing = if (themeMode == mode) {
                                {
                                    Icon(
                                        painter = AppIcons.Check,
                                        contentDescription = null,
                                        tint = colors.primary,
                                        modifier = Modifier.size(dimens.iconSize)
                                    )
                                }
                            } else {
                                null
                            },
                            onClick = { onThemeModeChange(mode) }
                        )
                    }
                }
            }

            item {
                SettingsGroup(
                    title = "Accent",
                    footnote = "Series keep their own colours; the accent tints everything else."
                ) {
                    Palettes.forEachIndexed { index, palette ->
                        if (index > 0) InsetDivider(dimens.dividerInsetWithIcon)
                        SettingsRow(
                            title = palette.name,
                            leadingIcon = AppIcons.Trophy,
                            leadingIconTint = colors.onPrimary,
                            leadingIconContainer = if (colors.isLight) {
                                palette.onLight
                            } else {
                                palette.onDark
                            },
                            showChevron = false,
                            trailing = if (paletteIndex == index) {
                                {
                                    Icon(
                                        painter = AppIcons.Check,
                                        contentDescription = null,
                                        tint = colors.primary,
                                        modifier = Modifier.size(dimens.iconSize)
                                    )
                                }
                            } else {
                                null
                            },
                            onClick = { onPaletteChange(index) }
                        )
                    }
                }
            }

            item {
                SettingsGroup(
                    title = "Data",
                    footnote = "Timings are recorded by hand from Featured Multiplayer. " +
                        "See RECORDING.md in the project for the full tables."
                ) {
                    SettingsRow(
                        title = "Shuffle anchor",
                        subtitle = "Lengths flip each 6:00 AM cycle from here",
                        value = ScheduleData.firstObservedOn.toString(),
                        leadingIcon = AppIcons.Calendar,
                        leadingIconTint = colors.primary,
                        leadingIconContainer = colors.primarySubtle,
                        showChevron = false
                    )
                    InsetDivider(dimens.dividerInsetWithIcon)
                    SettingsRow(
                        title = "Rotations recorded",
                        value = ScheduleData.recordedProfiles,
                        leadingIcon = AppIcons.Timer,
                        leadingIconTint = colors.primary,
                        leadingIconContainer = colors.primarySubtle,
                        showChevron = false
                    )
                }
            }

        }
    }
}
