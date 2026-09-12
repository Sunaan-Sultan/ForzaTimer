package com.pixel.forzatimer.data

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.floor

object ScheduleEngine {

    @RequiresApi(Build.VERSION_CODES.O)
    fun upcoming(schedule: SeriesSchedule, now: LocalDateTime, count: Int): List<UpcomingRace> {
        if (!schedule.isRecorded || schedule.cadence.isZero) return emptyList()

        val cadenceSeconds = schedule.cadence.seconds.toDouble()
        val elapsedSeconds = Duration.between(schedule.anchor, now).seconds.toDouble()
        val firstIndex = floor(elapsedSeconds / cadenceSeconds).toInt() + 1

        return (0 until count).map { offset ->
            val index = firstIndex + offset
            val startsAt = schedule.anchor.plus(schedule.cadence.multipliedBy(index.toLong()))
            UpcomingRace(
                series = schedule.series,
                raceLength = schedule.raceLength,
                rotationIndex = index,
                startsAt = startsAt,
                slot = slotAt(schedule, index),
                entryOpensAt = startsAt.minus(schedule.entryOpensBefore),
                entryClosesAt = startsAt.minus(schedule.entryClosesBefore)
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun nextRace(schedule: SeriesSchedule, now: LocalDateTime): UpcomingRace? =
        upcoming(schedule, now, 1).firstOrNull()

    private fun slotAt(schedule: SeriesSchedule, index: Int): RaceSlot? {
        val size = schedule.rotation.size
        if (size == 0) return null
        if (schedule.rotationComplete) {
            val wrapped = ((index % size) + size) % size
            return schedule.rotation[wrapped]
        }
        return schedule.rotation.getOrNull(index)
    }
}
