package com.pixel.forzatimer.data

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

data class SeriesSchedule(
    val series: Series,
    val cadence: Duration,
    val anchor: LocalDateTime,
    val rotation: List<RaceSlot>,
    val rotationComplete: Boolean,
    val entryOpensBefore: Duration,
    val entryClosesBefore: Duration,
    val seriesEndsOn: LocalDate? = null,
    val lastObserved: LocalDate? = null
) {
    val isRecorded: Boolean get() = rotation.isNotEmpty()
}
