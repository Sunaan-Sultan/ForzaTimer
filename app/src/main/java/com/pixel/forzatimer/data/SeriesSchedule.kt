package com.pixel.forzatimer.data

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

data class SeriesSchedule(
    val series: Series,
    val raceLength: RaceLength,
    val anchor: LocalDateTime?,
    val rotation: SeriesRotation,
    val rotationOffset: Int = 0,
    val lastObserved: LocalDate? = null
) {
    val cadence: Duration get() = raceLength.cadence

    val entryOpensBefore: Duration get() = raceLength.entryOpensBefore

    val entryClosesBefore: Duration get() = raceLength.entryClosesBefore

    val rotationComplete: Boolean get() = rotation.complete

    val recordedRounds: Int get() = rotation.recordedRounds

    val hasGaps: Boolean get() = rotation.hasGaps

    val isRecorded: Boolean get() = anchor != null && rotation.recordedRounds > 0
}
