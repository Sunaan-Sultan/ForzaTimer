package com.pixel.forzatimer

import com.pixel.forzatimer.data.RaceLength
import com.pixel.forzatimer.data.ScheduleData
import com.pixel.forzatimer.data.ScheduleEngine
import com.pixel.forzatimer.data.Series
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class RaceLengthShuffleTest {

    private fun cycle(month: Int, day: Int) = LocalDateTime.of(2026, month, day, 6, 0)

    @Test
    fun observedCyclesReturnWhatWasSeenInGame() {
        assertEquals(RaceLength.LONG, ScheduleData.lengthFor(Series.GT2, cycle(9, 16)))
        assertEquals(RaceLength.LONG, ScheduleData.lengthFor(Series.GT2, cycle(9, 17)))
        assertEquals(RaceLength.MEDIUM, ScheduleData.lengthFor(Series.GT2, cycle(9, 13)))
        assertEquals(RaceLength.MEDIUM, ScheduleData.lengthFor(Series.INDYCAR, cycle(9, 13)))
    }

    @Test
    fun unobservedCyclesFlipFromTheMostRecentObservation() {
        assertEquals(RaceLength.MEDIUM, ScheduleData.lengthFor(Series.GT2, cycle(9, 18)))
        assertEquals(RaceLength.LONG, ScheduleData.lengthFor(Series.GT2, cycle(9, 19)))
        assertEquals(RaceLength.MEDIUM, ScheduleData.lengthFor(Series.GT2, cycle(9, 20)))
    }

    @Test
    fun anObservationOverridesWhatTheFlipWouldHavePredicted() {
        assertTrue(ScheduleData.isObserved(Series.GT2, cycle(9, 17)))
        assertEquals(RaceLength.LONG, ScheduleData.lengthFor(Series.GT2, cycle(9, 17)))
    }

    @Test
    fun cycleBoundaryIsSixAmNotMidnight() {
        val before = LocalDateTime.of(2026, 9, 13, 5, 59)
        val after = LocalDateTime.of(2026, 9, 13, 6, 1)
        assertEquals(RaceLength.LONG, ScheduleData.expectedLengths(before).getValue(Series.GT2))
        assertEquals(RaceLength.MEDIUM, ScheduleData.expectedLengths(after).getValue(Series.GT2))
    }

    @Test
    fun bothLengthsOfASeriesShareOneRotation() {
        Series.entries.forEach { series ->
            val rotations = ScheduleData.profilesFor(series).values.map { it.rotation }
            assertEquals(2, rotations.size)
            assertSame("${series.shortName} must share one track list", rotations[0], rotations[1])
        }
    }

    @Test
    fun profilesAreCadenceConsistent() {
        Series.entries.forEach { series ->
            ScheduleData.profilesFor(series).forEach { (length, schedule) ->
                assertEquals(length, schedule.raceLength)
                assertEquals(series, schedule.series)
                val expected = if (length == RaceLength.LONG) 20L else 16L
                assertEquals(expected, schedule.cadence.toMinutes())
                assertEquals(expected + 5, schedule.entryOpensBefore.toMinutes())
                assertEquals(3L, schedule.entryClosesBefore.toMinutes())
            }
        }
    }

    @Test
    fun rotationsHaveTheDocumentedLoopLengths() {
        val expected = mapOf(
            Series.TOURING_CAR to 33,
            Series.GT2 to 31,
            Series.PROTO_H to 31,
            Series.INDYCAR to 10
        )
        expected.forEach { (series, size) ->
            val rotation = ScheduleData.profilesFor(series).values.first().rotation
            assertEquals(series.shortName, size, rotation.size)
            assertTrue("${series.shortName} must loop", rotation.complete)
        }
    }

    @Test
    fun phaseOffsetsMatchTheCapturedAnchors() {
        assertEquals(17, ScheduleData.profilesFor(Series.PROTO_H).getValue(RaceLength.LONG).rotationOffset)
        assertEquals(0, ScheduleData.profilesFor(Series.PROTO_H).getValue(RaceLength.MEDIUM).rotationOffset)
        assertEquals(26, ScheduleData.profilesFor(Series.GT2).getValue(RaceLength.MEDIUM).rotationOffset)
        assertEquals(0, ScheduleData.profilesFor(Series.GT2).getValue(RaceLength.LONG).rotationOffset)
    }

    @Test
    fun protoHLongAndMediumAgreeOnTrackOrderAtTheirOffsets() {
        val long = ScheduleData.profilesFor(Series.PROTO_H).getValue(RaceLength.LONG)
        val medium = ScheduleData.profilesFor(Series.PROTO_H).getValue(RaceLength.MEDIUM)
        val size = long.rotation.size
        (0 until size).forEach { n ->
            val fromLong = long.rotation.slots[Math.floorMod(long.rotationOffset + n, size)]
            val fromMedium = medium.rotation.slots[Math.floorMod(17 + n, size)]
            assertEquals(fromLong?.track, fromMedium?.track)
        }
    }

    @Test
    fun aRecordedSeriesNeverRunsOffTheEndOfItsList() {
        val far = LocalDateTime.of(2027, 3, 1, 21, 0)
        Series.entries.forEach { series ->
            val schedule = ScheduleData.scheduleFor(series, far)
            if (!schedule.isRecorded) return@forEach
            val races = ScheduleEngine.upcoming(schedule, far, 40)
            assertEquals(40, races.size)
            assertTrue(
                "${series.shortName} should still name tracks months out",
                races.count { it.slot != null } > 30
            )
        }
    }

    @Test
    fun everySeriesHasTimingsOnTheCurrentCycle() {
        val now = LocalDateTime.of(2026, 9, 17, 21, 0)
        Series.entries.forEach { series ->
            val race = ScheduleEngine.nextRace(ScheduleData.scheduleFor(series, now), now)
            assertTrue("${series.shortName} should have a next race", race != null)
        }
    }
}
