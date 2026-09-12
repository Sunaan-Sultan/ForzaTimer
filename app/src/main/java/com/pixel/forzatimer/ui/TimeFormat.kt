package com.pixel.forzatimer.ui

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val clockFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
private val clockWithSecondsFormatter = DateTimeFormatter.ofPattern("h:mm:ss a", Locale.US)
private val dayFormatter = DateTimeFormatter.ofPattern("EEE d MMM", Locale.US)

fun LocalDateTime.asClock(): String = format(clockFormatter)

fun LocalDateTime.asClockWithSeconds(): String = format(clockWithSecondsFormatter)

fun LocalDateTime.asDay(): String = format(dayFormatter)

fun Duration.asCountdown(): String {
    val total = coerceAtLeast(Duration.ZERO).seconds
    val hours = total / 3600
    val minutes = (total % 3600) / 60
    val seconds = total % 60
    return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
}

fun Duration.asCompact(): String {
    val total = coerceAtLeast(Duration.ZERO).seconds
    val hours = total / 3600
    val minutes = (total % 3600) / 60
    val seconds = total % 60
    return if (hours > 0) {
        String.format(Locale.US, "%dh %02dm", hours, minutes)
    } else {
        String.format(Locale.US, "%02dm %02ds", minutes, seconds)
    }
}
