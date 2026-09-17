package com.pixel.forzatimer.data

import java.time.Duration

enum class RaceLength(val label: String, val approxMinutes: Int, val cadenceMinutes: Long) {
    LONG("Long", 30, 20),
    MEDIUM("Medium", 20, 16);

    val cadence: Duration get() = Duration.ofMinutes(cadenceMinutes)

    val entryOpensBefore: Duration get() = Duration.ofMinutes(cadenceMinutes + 5)

    val entryClosesBefore: Duration get() = Duration.ofMinutes(3)

    val opposite: RaceLength get() = if (this == LONG) MEDIUM else LONG
}
