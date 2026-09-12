package com.pixel.forzatimer.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.pixel.forzatimer.data.DailyCycle
import com.pixel.forzatimer.data.EntryState
import com.pixel.forzatimer.data.ScheduleData
import com.pixel.forzatimer.data.ScheduleEngine
import com.pixel.forzatimer.data.Series
import com.pixel.forzatimer.data.SeriesSchedule
import com.pixel.forzatimer.data.UpcomingRace
import com.pixel.forzatimer.presentation.component.AppIcons
import com.pixel.forzatimer.presentation.component.design.AppFilterChip
import com.pixel.forzatimer.presentation.component.design.AppScaffold
import com.pixel.forzatimer.presentation.component.design.AppTopBar
import com.pixel.forzatimer.presentation.component.design.EmptyState
import com.pixel.forzatimer.presentation.component.design.GroupedCard
import com.pixel.forzatimer.presentation.component.design.InsetDivider
import com.pixel.forzatimer.presentation.component.design.Pill
import com.pixel.forzatimer.presentation.component.design.SectionHeader
import com.pixel.forzatimer.presentation.component.design.SettingsGroup
import com.pixel.forzatimer.presentation.component.design.SettingsRow
import com.pixel.forzatimer.presentation.component.design.StatCard
import com.pixel.forzatimer.presentation.component.design.StatCardStyle
import com.pixel.forzatimer.presentation.component.seriesAccent
import com.pixel.forzatimer.presentation.component.seriesTile
import com.pixel.forzatimer.ui.asClock
import com.pixel.forzatimer.ui.asCompact
import com.pixel.forzatimer.ui.asCountdown
import com.pixel.forzatimer.ui.asDay
import com.pixel.forzatimer.ui.theme.AppTheme
import kotlinx.coroutines.delay
import java.time.LocalDateTime

@Composable
private fun rememberNow(): LocalDateTime {
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L - (System.currentTimeMillis() % 1000L))
            now = LocalDateTime.now()
        }
    }
    return now
}

@Composable
fun ScheduleScreen(onOpenSettings: () -> Unit) {
    val now = rememberNow()
    var selected by rememberSaveable { mutableStateOf<Series?>(null) }
    val colors = AppTheme.colors
    val dimens = AppTheme.dimens

    AppScaffold(
        topBar = {
            AppTopBar(
                title = "Forza Timer",
                eyebrow = "Featured Multiplayer",
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            painter = AppIcons.Settings,
                            contentDescription = "Settings",
                            tint = colors.textSecondary
                        )
                    }
                }
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
            item { ClockCards(now) }
            if (ScheduleData.isStale(now)) {
                item { StaleGroup() }
            }
            item { SeriesFilter(selected) { selected = it } }

            val current = selected
            if (current == null) {
                overviewItems(now) { selected = it }
            } else {
                seriesItems(current, now)
            }
        }
    }
}

@Composable
private fun ClockCards(now: LocalDateTime) {
    val dimens = AppTheme.dimens
    Row(horizontalArrangement = Arrangement.spacedBy(dimens.space12)) {
        StatCard(
            value = now.asClock(),
            label = now.asDay(),
            icon = AppIcons.Timer,
            style = StatCardStyle.Tinted,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            value = DailyCycle.timeUntilNextReset(now).asCompact(),
            label = "Until reshuffle",
            icon = AppIcons.Refresh,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StaleGroup() {
    val colors = AppTheme.colors
    val dimens = AppTheme.dimens
    val expected = ScheduleData.nextCycleLengths
    SettingsGroup(
        title = "Schedule",
        footnote = "Recorded for the cycle starting ${ScheduleData.recordedCycleStart.asDay()}, 6:00 AM."
    ) {
        SettingsRow(
            title = "Schedule out of date",
            subtitle = "The lineup reshuffles at 6:00 AM, so today's tracks and race lengths " +
                "differ. Re-record from the game.",
            leadingIcon = AppIcons.Warning,
            leadingIconTint = colors.warning,
            leadingIconContainer = colors.warningSubtle,
            showChevron = false
        )
        if (expected.isNotEmpty()) {
            InsetDivider(dimens.dividerInsetWithIcon)
            SettingsRow(
                title = "Expected next cycle",
                subtitle = expected.entries.joinToString(", ") {
                    "${it.key.shortName} ${it.value.label.lowercase()}"
                },
                leadingIcon = AppIcons.Calendar,
                leadingIconTint = colors.primary,
                leadingIconContainer = colors.primarySubtle,
                showChevron = false
            )
        }
    }
}

@Composable
private fun SeriesFilter(selected: Series?, onSelect: (Series?) -> Unit) {
    val dimens = AppTheme.dimens
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimens.space8)
    ) {
        item {
            AppFilterChip(label = "All", selected = selected == null, onClick = { onSelect(null) })
        }
        items(Series.entries) { series ->
            AppFilterChip(
                label = series.shortName,
                selected = selected == series,
                onClick = { onSelect(series) }
            )
        }
    }
}

private fun LazyListScope.overviewItems(now: LocalDateTime, onSelect: (Series) -> Unit) {
    item {
        val dimens = AppTheme.dimens
        SettingsGroup(
            title = "Next up",
            footnote = "Tap a series for its full running order."
        ) {
            Series.entries.forEachIndexed { index, series ->
                if (index > 0) InsetDivider(dimens.dividerInsetWithIcon)
                OverviewRow(series, now) { onSelect(series) }
            }
        }
    }
}

@Composable
private fun OverviewRow(series: Series, now: LocalDateTime, onClick: () -> Unit) {
    val colors = AppTheme.colors
    val accent = seriesAccent(series)
    val schedule = ScheduleData.scheduleFor(series)
    val race = schedule?.let { ScheduleEngine.nextRace(it, now) }

    SettingsRow(
        title = series.shortName,
        subtitle = when {
            race == null -> "Not recorded yet"
            race.slot == null -> "Track not recorded · starts ${race.startsAt.asClock()}"
            else -> "${race.slot.track} · ${race.startsAt.asClock()}"
        },
        leadingIcon = AppIcons.Speed,
        leadingIconTint = accent,
        leadingIconContainer = seriesTile(series).copy(alpha = if (colors.isLight) 0.12f else 0.20f),
        value = race?.timeUntilStart(now)?.asCountdown(),
        valueColor = accent,
        onClick = onClick
    )
}

private fun LazyListScope.seriesItems(series: Series, now: LocalDateTime) {
    val schedule = ScheduleData.scheduleFor(series)
    if (schedule == null) {
        item {
            EmptyState(
                icon = AppIcons.Timer,
                title = "No timings for ${series.shortName}",
                message = "Open Featured Multiplayer in game, put the check mark on " +
                    "${series.fullName}, and log the event cards."
            )
        }
        return
    }

    val races = ScheduleEngine.upcoming(schedule, now, 8)
    val next = races.firstOrNull()

    if (next != null) {
        item { HeroGroup(schedule, next, now) }
    }
    if (races.size > 1) {
        item { LaterGroup(races.drop(1), now) }
    }
    item { SeriesInfoGroup(schedule) }
}

@Composable
private fun HeroGroup(schedule: SeriesSchedule, race: UpcomingRace, now: LocalDateTime) {
    val colors = AppTheme.colors
    val dimens = AppTheme.dimens
    val accent = seriesAccent(race.series)

    Column {
        SectionHeader("Next up")
        GroupedCard {
            Column(modifier = Modifier.padding(dimens.space16)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Pill(
                        text = "${schedule.raceLength.label} · ~${schedule.raceLength.approxMinutes} min",
                        accent = accent
                    )
                    Spacer(Modifier.width(dimens.space8))
                    EntryPill(race, now)
                }
                Spacer(Modifier.height(dimens.space16))
                Text(
                    text = race.timeUntilStart(now).asCountdown(),
                    style = MaterialTheme.typography.displayMedium,
                    color = accent
                )
                Text(
                    text = "until lights out at ${race.startsAt.asClock()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
                Spacer(Modifier.height(dimens.space16))
                Text(
                    text = race.slot?.track ?: "Track not recorded yet",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (race.slot == null) colors.textSecondary else colors.textPrimary
                )
                race.slot?.layout?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
            }
            race.slot?.let { slot ->
                slot.laps?.let {
                    InsetDivider()
                    SettingsRow(title = "Laps", value = it.toString(), showChevron = false)
                }
                slot.weatherF?.let {
                    InsetDivider()
                    SettingsRow(title = "Weather", value = "$it °F", showChevron = false)
                }
                slot.timeOfDay?.let {
                    InsetDivider()
                    SettingsRow(title = "Time of day", value = it, showChevron = false)
                }
            }
            InsetDivider()
            SettingsRow(
                title = "Entry opens",
                value = race.entryOpensAt.asClock(),
                showChevron = false
            )
            InsetDivider()
            SettingsRow(
                title = "Entry closes",
                value = race.entryClosesAt.asClock(),
                showChevron = false
            )
        }
    }
}

@Composable
private fun LaterGroup(races: List<UpcomingRace>, now: LocalDateTime) {
    val dimens = AppTheme.dimens
    val colors = AppTheme.colors
    SettingsGroup(title = "Later today") {
        races.forEachIndexed { index, race ->
            if (index > 0) InsetDivider()
            SettingsRow(
                title = race.slot?.track ?: "Not recorded yet",
                titleColor = if (race.slot == null) colors.textSecondary else colors.textPrimary,
                subtitle = listOfNotNull(
                    race.slot?.layout,
                    "in ${race.timeUntilStart(now).asCompact()}"
                ).joinToString(" · "),
                value = race.startsAt.asClock(),
                showChevron = false
            )
        }
    }
}

@Composable
private fun SeriesInfoGroup(schedule: SeriesSchedule) {
    val rotationValue = when {
        schedule.rotationComplete -> "${schedule.recordedRounds} rounds, confirmed"
        schedule.hasGaps -> "${schedule.recordedRounds} of ${schedule.rotation.size} rounds"
        else -> "${schedule.recordedRounds} rounds so far"
    }
    SettingsGroup(
        title = "Series info",
        footnote = when {
            schedule.rotationComplete ->
                "The full rotation is known, so start times and tracks are predicted indefinitely."

            schedule.hasGaps ->
                "The rotation has not been seen looping yet, and some rounds in the middle " +
                    "were never captured. Every start time is still exact; unrecorded rounds " +
                    "are left blank rather than guessed."

            else ->
                "The rotation has not been seen looping yet. Start times past the last " +
                    "recorded round are still exact; the track is left blank rather than guessed."
        }
    ) {
        SettingsRow(
            title = "Race length",
            value = "${schedule.raceLength.label} · ~${schedule.raceLength.approxMinutes} min",
            showChevron = false
        )
        InsetDivider()
        SettingsRow(
            title = "Cadence",
            value = "Every ${schedule.cadence.toMinutes()} min",
            showChevron = false
        )
        InsetDivider()
        SettingsRow(
            title = "Entry window",
            value = "−${schedule.entryOpensBefore.toMinutes()} to " +
                "−${schedule.entryClosesBefore.toMinutes()} min",
            showChevron = false
        )
        InsetDivider()
        SettingsRow(title = "Rotation", value = rotationValue, showChevron = false)
        schedule.lastObserved?.let {
            InsetDivider()
            SettingsRow(title = "Last observed", value = it.toString(), showChevron = false)
        }
    }
}

@Composable
private fun EntryPill(race: UpcomingRace, now: LocalDateTime) {
    val colors = AppTheme.colors
    when (race.entryState(now)) {
        EntryState.OPEN -> Pill(
            text = "Entry open · ${race.timeUntilEntryCloses(now).asCompact()}",
            accent = colors.success,
            showDot = true
        )

        EntryState.NOT_OPEN -> Pill(
            text = "Opens in ${race.timeUntilEntryOpens(now).asCompact()}",
            accent = colors.textSecondary
        )

        EntryState.CLOSED -> Pill(text = "Entry closed", accent = colors.danger)
    }
}
