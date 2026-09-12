package com.pixel.forzatimer.data

import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

object DailyCycle {

    val resetTime: LocalTime = LocalTime.of(6, 0)

    fun startOf(now: LocalDateTime): LocalDateTime =
        if (now.toLocalTime() < resetTime) {
            now.toLocalDate().minusDays(1).atTime(resetTime)
        } else {
            now.toLocalDate().atTime(resetTime)
        }

    fun nextResetAfter(now: LocalDateTime): LocalDateTime = startOf(now).plusDays(1)

    fun timeUntilNextReset(now: LocalDateTime): Duration =
        Duration.between(now, nextResetAfter(now))
}
