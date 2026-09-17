package com.pixel.forzatimer.data

data class SeriesRotation(
    val slots: List<RaceSlot?>,
    val complete: Boolean
) {
    val size: Int get() = slots.size

    val recordedRounds: Int get() = slots.count { it != null }

    val hasGaps: Boolean get() = slots.any { it == null }
}
