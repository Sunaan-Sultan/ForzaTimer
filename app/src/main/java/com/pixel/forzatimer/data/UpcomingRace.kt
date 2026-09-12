package com.pixel.forzatimer.data

import java.time.Duration
import java.time.LocalDateTime

enum class EntryState { NOT_OPEN, OPEN, CLOSED }

data class UpcomingRace(
    val series: Series,
    val raceLength: RaceLength,
    val rotationIndex: Int,
    val startsAt: LocalDateTime,
    val slot: RaceSlot?,
    val entryOpensAt: LocalDateTime,
    val entryClosesAt: LocalDateTime
) {
    fun entryState(now: LocalDateTime): EntryState = when {
        now.isBefore(entryOpensAt) -> EntryState.NOT_OPEN
        now.isBefore(entryClosesAt) -> EntryState.OPEN
        else -> EntryState.CLOSED
    }

    fun timeUntilStart(now: LocalDateTime): Duration = Duration.between(now, startsAt)

    fun timeUntilEntryOpens(now: LocalDateTime): Duration = Duration.between(now, entryOpensAt)

    fun timeUntilEntryCloses(now: LocalDateTime): Duration = Duration.between(now, entryClosesAt)
}
