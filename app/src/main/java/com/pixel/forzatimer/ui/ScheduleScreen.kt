package com.pixel.forzatimer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixel.forzatimer.data.EntryState
import com.pixel.forzatimer.data.ScheduleData
import com.pixel.forzatimer.data.ScheduleEngine
import com.pixel.forzatimer.data.Series
import com.pixel.forzatimer.data.SeriesSchedule
import com.pixel.forzatimer.data.UpcomingRace
import kotlinx.coroutines.delay
import java.time.Duration
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
fun ScheduleScreen(modifier: Modifier = Modifier) {
    val now = rememberNow()
    var selected by rememberSaveable { mutableStateOf<Series?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Ink)
    ) {
        Header(now)
        SeriesTabs(selected) { selected = it }
        val current = selected
        if (current == null) {
            OverviewList(now)
        } else {
            SeriesDetail(current, now)
        }
    }
}

@Composable
private fun Header(now: LocalDateTime) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface1)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "FEATURED MULTIPLAYER",
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = now.asClockWithSeconds(),
                color = TextPrimary,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = now.asDay(),
                color = TextMuted,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        val daysLeft = Duration.between(now, ScheduleData.playlistEndsOn.atStartOfDay()).toDays()
        if (daysLeft >= 0) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Playlist ends in ${daysLeft}d",
                color = TextMuted,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun SeriesTabs(selected: Series?, onSelect: (Series?) -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface1),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            TabChip("ALL", selected == null, TextPrimary) { onSelect(null) }
        }
        items(Series.entries) { series ->
            TabChip(
                label = series.shortName.uppercase(),
                active = selected == series,
                accent = accentFor(series)
            ) { onSelect(series) }
        }
    }
}

@Composable
private fun TabChip(label: String, active: Boolean, accent: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (active) accent.copy(alpha = 0.18f) else Surface2)
            .border(
                width = 1.dp,
                color = if (active) accent else Outline,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        Text(
            text = label,
            color = if (active) accent else TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun OverviewList(now: LocalDateTime) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(Series.entries) { series ->
            val schedule = ScheduleData.scheduleFor(series)
            val race = schedule?.let { ScheduleEngine.nextRace(it, now) }
            if (race == null) UnrecordedCard(series) else OverviewCard(race, now)
        }
    }
}

@Composable
private fun OverviewCard(race: UpcomingRace, now: LocalDateTime) {
    val accent = accentFor(race.series)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface1)
            .border(1.dp, Outline, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AccentBar(accent)
            Spacer(Modifier.width(10.dp))
            Text(
                text = race.series.shortName.uppercase(),
                color = accent,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.weight(1f))
            EntryPill(race, now)
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = race.slot?.track ?: "Track not recorded yet",
            color = if (race.slot == null) TextMuted else TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        race.slot?.layout?.let {
            Text(text = it, color = TextMuted, fontSize = 13.sp)
        }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Column {
                Label("Race starts at")
                Text(
                    text = race.startsAt.asClock(),
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Label("In")
                Text(
                    text = race.timeUntilStart(now).asCountdown(),
                    color = accent,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun SeriesDetail(series: Series, now: LocalDateTime) {
    val schedule = ScheduleData.scheduleFor(series)
    if (schedule == null) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) { UnrecordedCard(series) }
        return
    }
    val races = ScheduleEngine.upcoming(schedule, now, 8)
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        races.firstOrNull()?.let { next ->
            item { HeroCard(next, now) }
        }
        if (races.size > 1) {
            item { SectionLabel("LATER TODAY") }
            items(races.drop(1)) { race -> CompactRow(race, now) }
        }
        item { SourceFooter(schedule) }
    }
}

@Composable
private fun HeroCard(race: UpcomingRace, now: LocalDateTime) {
    val accent = accentFor(race.series)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface1)
            .border(1.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "NEXT UP",
                color = accent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.weight(1f))
            EntryPill(race, now)
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = race.slot?.track ?: "Track not recorded yet",
            color = if (race.slot == null) TextMuted else TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        race.slot?.layout?.let {
            Spacer(Modifier.height(2.dp))
            Text(text = it, color = TextMuted, fontSize = 14.sp)
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = race.timeUntilStart(now).asCountdown(),
            color = accent,
            fontSize = 46.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "until lights out at ${race.startsAt.asClock()}",
            color = TextMuted,
            fontSize = 13.sp
        )
        race.slot?.let { slot ->
            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                slot.laps?.let { Stat("Laps", it.toString()) }
                slot.weatherF?.let { Stat("Weather", "$it °F") }
                slot.timeOfDay?.let { Stat("Time of day", it) }
            }
        }
        Spacer(Modifier.height(18.dp))
        Row {
            Column(Modifier.weight(1f)) {
                Label("Entry opens")
                Text(race.entryOpensAt.asClock(), color = TextPrimary, fontSize = 14.sp)
            }
            Column(Modifier.weight(1f)) {
                Label("Entry closes")
                Text(race.entryClosesAt.asClock(), color = TextPrimary, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun CompactRow(race: UpcomingRace, now: LocalDateTime) {
    val accent = accentFor(race.series)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface1)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.width(82.dp)) {
            Text(
                text = race.startsAt.asClock(),
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = race.timeUntilStart(now).asCompact(),
                color = TextMuted,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = race.slot?.track ?: "Not recorded yet",
                color = if (race.slot == null) TextMuted else TextPrimary,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            race.slot?.layout?.let {
                Text(text = it, color = TextMuted, fontSize = 11.sp)
            }
        }
        Spacer(Modifier.width(8.dp))
        AccentBar(if (race.slot == null) Outline else accent)
    }
}

@Composable
private fun UnrecordedCard(series: Series) {
    val accent = accentFor(series)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface1)
            .border(1.dp, Outline, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AccentBar(accent.copy(alpha = 0.4f))
            Spacer(Modifier.width(10.dp))
            Text(
                text = series.shortName.uppercase(),
                color = accent.copy(alpha = 0.6f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = "No timings recorded yet",
            color = TextMuted,
            fontSize = 15.sp
        )
        Text(
            text = "Open Featured Multiplayer and log this series.",
            color = TextMuted.copy(alpha = 0.7f),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun EntryPill(race: UpcomingRace, now: LocalDateTime) {
    val (label, color) = when (race.entryState(now)) {
        EntryState.OPEN -> "ENTRY OPEN · ${race.timeUntilEntryCloses(now).asCompact()}" to LiveGreen
        EntryState.NOT_OPEN -> "OPENS IN ${race.timeUntilEntryOpens(now).asCompact()}" to TextMuted
        EntryState.CLOSED -> "ENTRY CLOSED" to Color(0xFFE03131)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = label,
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
private fun Stat(label: String, value: String) {
    Column {
        Label(label)
        Text(text = value, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun Label(text: String) {
    Text(text = text, color = TextMuted, fontSize = 11.sp)
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = TextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp,
        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
    )
}

@Composable
private fun SourceFooter(schedule: SeriesSchedule) {
    val rotationNote = if (schedule.rotationComplete) {
        "Full rotation of ${schedule.rotation.size} tracks confirmed."
    } else {
        "${schedule.rotation.size} tracks recorded so far, rotation not yet confirmed."
    }
    Column(Modifier.padding(top = 12.dp)) {
        Text(
            text = "Every ${schedule.cadence.toMinutes()} min · entry opens " +
                "${schedule.entryOpensBefore.toMinutes()} min before, closes " +
                "${schedule.entryClosesBefore.toMinutes()} min before",
            color = TextMuted,
            fontSize = 11.sp
        )
        Text(text = rotationNote, color = TextMuted, fontSize = 11.sp)
        schedule.lastObserved?.let {
            Text(text = "Last observed in game on $it", color = TextMuted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun AccentBar(color: Color) {
    Box(
        Modifier
            .width(4.dp)
            .height(16.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(color)
    )
}
