package com.pixel.forzatimer.data

import java.time.LocalDate
import java.time.LocalDateTime

object ScheduleData {

    private val observations: Map<Series, Map<LocalDate, RaceLength>> = mapOf(
        Series.TOURING_CAR to mapOf(
            LocalDate.of(2026, 9, 12) to RaceLength.LONG,
            LocalDate.of(2026, 9, 13) to RaceLength.LONG
        ),
        Series.GT2 to mapOf(
            LocalDate.of(2026, 9, 12) to RaceLength.LONG,
            LocalDate.of(2026, 9, 13) to RaceLength.MEDIUM,
            LocalDate.of(2026, 9, 16) to RaceLength.LONG,
            LocalDate.of(2026, 9, 17) to RaceLength.LONG
        ),
        Series.PROTO_H to mapOf(
            LocalDate.of(2026, 9, 12) to RaceLength.MEDIUM,
            LocalDate.of(2026, 9, 13) to RaceLength.LONG
        ),
        Series.INDYCAR to mapOf(
            LocalDate.of(2026, 9, 12) to RaceLength.MEDIUM,
            LocalDate.of(2026, 9, 13) to RaceLength.MEDIUM
        )
    )

    val firstObservedOn: LocalDate = LocalDate.of(2026, 9, 12)

    fun lengthFor(series: Series, cycleStart: LocalDateTime): RaceLength {
        val date = cycleStart.toLocalDate()
        val seen = observations.getValue(series)
        seen[date]?.let { return it }
        val last = seen.keys.filter { !it.isAfter(date) }.maxOrNull() ?: seen.keys.min()
        val elapsed = date.toEpochDay() - last.toEpochDay()
        val observed = seen.getValue(last)
        return if (Math.floorMod(elapsed, 2L) == 1L) observed.opposite else observed
    }

    fun isObserved(series: Series, cycleStart: LocalDateTime): Boolean =
        observations.getValue(series).containsKey(cycleStart.toLocalDate())

    fun lengthsForCycle(cycleStart: LocalDateTime): Map<Series, RaceLength> =
        Series.entries.associateWith { lengthFor(it, cycleStart) }

    fun expectedLengths(now: LocalDateTime): Map<Series, RaceLength> =
        lengthsForCycle(DailyCycle.startOf(now))

    private val seenOn0912 = LocalDate.of(2026, 9, 12)

    private val seenOn0914 = LocalDate.of(2026, 9, 14)

    private val seenOn0917 = LocalDate.of(2026, 9, 17)

    private val touringCarRotation = SeriesRotation(
        complete = true,
        slots = listOf(
            RaceSlot("Sebring International Raceway", "Full Circuit", 13, 65, "Sunset"),
            RaceSlot("Maple Valley", "Full Circuit", 18, 61, "Sunrise"),
            RaceSlot("Fujimi Kaido", "Full Circuit Reverse", 3, 63, "Morning"),
            RaceSlot("Hockenheimring", "Full Circuit", 16, 65, "Night"),
            RaceSlot("Michelin Raceway Road Atlanta", "Grand Prix Course", 20, 51, "Midnight"),
            RaceSlot("Circuit de Spa-Francorchamps", "Full Circuit", 11, 65, "Sunset"),
            RaceSlot("Nürburgring", "GP Circuit", 13, 55, "Night"),
            RaceSlot("Mid-Ohio Sports Car Course", "Sports Car Circuit", 20, 63, "Late Morning"),
            RaceSlot("Daytona Intl Speedway", "Sports Car Circuit", 16, 53, "Night"),
            RaceSlot("Fujimi Kaido", "Full Circuit", 3, 68, "Afternoon"),
            RaceSlot("Hakone", "Grand Prix Circuit", 22, 68, "Late Afternoon"),
            RaceSlot("Yas Marina Circuit", "Full Circuit", 14, 69, "Morning"),
            RaceSlot("Circuit de Barcelona-Catalunya", "Grand Prix Circuit", 15, 57, "Sunrise"),
            RaceSlot("Watkins Glen International Speedway", "Full Circuit", 15, 74, "Late Morning"),
            RaceSlot("Mount Panorama Circuit", "Bathurst Circuit", 13, 70, "Afternoon"),
            RaceSlot("Sunset Peninsula Raceway", "Full Circuit", 18, 52, "Night"),
            RaceSlot("Indianapolis Motor Speedway", "Grand Prix Circuit", 19, 72, "Afternoon"),
            RaceSlot("Homestead-Miami Speedway", "Road Circuit", 20, 69, "Sunrise"),
            RaceSlot("Brands Hatch", "Grand Prix Circuit", 19, 59, "Afternoon"),
            RaceSlot("Silverstone Racing Circuit", "Grand Prix Circuit", 13, 66, "Late Afternoon"),
            RaceSlot("Grand Oak Raceway", "National Circuit", 22, 72, "Late Afternoon"),
            RaceSlot("Suzuka Circuit", "Full Circuit", 14, 65, "Sunset"),
            RaceSlot("Le Mans - Circuit International de la Sarthe", "Full Circuit", 7, 66, "Afternoon"),
            RaceSlot("Grand Oak Raceway", "National Circuit Reverse", 22, 64, "Sunset"),
            RaceSlot("Virginia International Raceway", "Full", 15, 65, "Sunrise"),
            RaceSlot("WeatherTech Raceway Laguna Seca", "Full Circuit", 19, 50, "Sunrise"),
            RaceSlot("Nürburgring", "Full Circuit", 3, 61, "Late Morning"),
            RaceSlot("Mugello Circuit", "Full Circuit", 15, 84, "Late Afternoon"),
            RaceSlot("Road America", "Full Circuit", 13, 59, "Sunrise"),
            RaceSlot("Nürburgring", "Nordschleife", 4, 51, "Sunrise"),
            RaceSlot("Sunset Peninsula Raceway", "Full Circuit Reverse", 18, 52, "Night"),
            RaceSlot("Lime Rock Park", "Full Circuit", 32, 81, "Late Afternoon"),
            RaceSlot("Kyalami Grand Prix Circuit", "Grand Prix Circuit", 16, 74, "Late Morning")
        )
    )

    private val gt2Rotation = SeriesRotation(
        complete = true,
        slots = listOf(
            RaceSlot("Homestead-Miami Speedway", "Road Circuit", 21, 82, "Afternoon"),
            RaceSlot("Watkins Glen International Speedway", "Full Circuit", 16, 81, "Late Afternoon"),
            RaceSlot("Sebring International Raceway", "Full Circuit", 15, 58, "Midnight"),
            RaceSlot("Grand Oak Raceway", "National Circuit", 24, 72, "Late Afternoon"),
            RaceSlot("Daytona Intl Speedway", "Sports Car Circuit", 17, 56, "Sunset"),
            RaceSlot("Lime Rock Park", "Full Circuit Alt", 31, 61, "Midnight"),
            RaceSlot("Mid-Ohio Sports Car Course", "Sports Car Circuit", 21, 50, "Sunrise"),
            RaceSlot("Sunset Peninsula Raceway", "Full Circuit Reverse", 20, 52, "Night"),
            RaceSlot("Nürburgring", "GP Circuit", 14, 65, "Afternoon"),
            RaceSlot("Yas Marina Circuit", "Full Circuit", 15, 71, "Night"),
            RaceSlot("Indianapolis Motor Speedway", "Grand Prix Circuit", 21, 70, "Sunset"),
            RaceSlot("Maple Valley", "Full Circuit", 20, 63, "Morning"),
            RaceSlot("Virginia International Raceway", "Full", 16, 84, "Afternoon"),
            RaceSlot("Mount Panorama Circuit", "Bathurst Circuit", 14, 49, "Sunset"),
            RaceSlot("Mugello Circuit", "Full Circuit", 16, 83, "Afternoon"),
            RaceSlot("Grand Oak Raceway", "National Circuit Reverse", 23, 63, "Morning"),
            RaceSlot("Road America", "Full Circuit", 14, 62, "Night"),
            RaceSlot("Hockenheimring", "Full Circuit", 17, 78, "Afternoon"),
            RaceSlot("Sunset Peninsula Raceway", "Full Circuit", 20, 71, "Afternoon"),
            RaceSlot("Circuit de Spa-Francorchamps", "Full Circuit", 12, 66, "Afternoon"),
            RaceSlot("Circuit de Barcelona-Catalunya", "Grand Prix Circuit", 16, 69, "Afternoon"),
            RaceSlot("Silverstone Racing Circuit", "Grand Prix Circuit", 14, 52, "Sunset"),
            RaceSlot("Le Mans - Circuit International de la Sarthe", "Full Circuit", 7, 66, "Afternoon"),
            RaceSlot("Nürburgring", "Full Circuit", 3, 66, "Late Afternoon"),
            RaceSlot("Kyalami Grand Prix Circuit", "Grand Prix Circuit", 17, 57, "Night"),
            RaceSlot("Brands Hatch", "Grand Prix Circuit", 20, 43, "Sunrise"),
            RaceSlot("WeatherTech Raceway Laguna Seca", "Full Circuit", 20, 52, "Sunset"),
            RaceSlot("Michelin Raceway Road Atlanta", "Grand Prix Course", 22, 50, "Sunrise"),
            RaceSlot("Hakone", "Grand Prix Circuit", 24, 68, "Late Afternoon"),
            RaceSlot("Suzuka Circuit", "Full Circuit", 15, 68, "Late Afternoon"),
            RaceSlot("Nürburgring", "Nordschleife", 4, 51, "Sunrise")
        )
    )

    private val protoHRotation = SeriesRotation(
        complete = true,
        slots = listOf(
            RaceSlot("Mount Panorama Circuit", "Bathurst Circuit", 10, 70, "Afternoon"),
            RaceSlot("Road America", "Full Circuit", 10, 78, "Late Afternoon"),
            RaceSlot("Homestead-Miami Speedway", "Road Circuit", 16, 75, "Sunset"),
            RaceSlot("Sunset Peninsula Raceway", "Full Circuit Reverse", 15, 64, "Late Morning"),
            RaceSlot("Watkins Glen International Speedway", "Full Circuit", 12, 61, "Morning"),
            RaceSlot("Le Mans - Circuit International de la Sarthe", "Full Circuit", 6, 48, "Sunrise"),
            RaceSlot("Silverstone Racing Circuit", "Grand Prix Circuit", 11, 70, "Afternoon"),
            RaceSlot("Indianapolis Motor Speedway", "Grand Prix Circuit", 15, 53, "Sunrise"),
            RaceSlot("Hockenheimring", "Full Circuit", 13, 65, "Night"),
            RaceSlot("Kyalami Grand Prix Circuit", "Grand Prix Circuit", 13, 77, "Afternoon"),
            RaceSlot("Sunset Peninsula Raceway", "Full Circuit", 15, 68, "Late Afternoon"),
            null,
            RaceSlot("Grand Oak Raceway", "National Circuit Reverse", 17, 61, "Midnight"),
            RaceSlot("Nürburgring", "GP Circuit", 11, 66, "Late Afternoon"),
            RaceSlot("Daytona Intl Speedway", "Sports Car Circuit", 13, 70, "Late Afternoon"),
            RaceSlot("Yas Marina Circuit", "Full Circuit", 11, 87, "Afternoon"),
            RaceSlot("Grand Oak Raceway", "National Circuit", 17, 64, "Sunset"),
            RaceSlot("Maple Valley", "Full Circuit", 15, 77, "Afternoon"),
            RaceSlot("Circuit de Barcelona-Catalunya", "Grand Prix Circuit", 12, 65, "Late Morning"),
            RaceSlot("Circuit de Spa-Francorchamps", "Full Circuit", 9, 68, "Late Afternoon"),
            RaceSlot("Lime Rock Park", "Full Circuit", 25, 81, "Afternoon"),
            RaceSlot("Nürburgring", "Full Circuit", 2, 51, "Sunrise"),
            RaceSlot("Nürburgring", "Nordschleife", 3, 61, "Late Morning"),
            RaceSlot("Michelin Raceway Road Atlanta", "Grand Prix Course", 16, 73, "Late Afternoon"),
            RaceSlot("Hakone", "Grand Prix Circuit", 18, 72, "Afternoon"),
            RaceSlot("WeatherTech Raceway Laguna Seca", "Full Circuit", 15, 52, "Morning"),
            RaceSlot("Mid-Ohio Sports Car Course", "Sports Car Circuit", 16, 70, "Afternoon"),
            RaceSlot("Virginia International Raceway", "Full", 12, 85, "Late Afternoon"),
            RaceSlot("Mugello Circuit", "Full Circuit", 12, 64, "Morning"),
            RaceSlot("Sebring International Raceway", "Full Circuit"),
            RaceSlot("Brands Hatch", "Grand Prix Circuit", 15, 45, "Night")
        )
    )

    private val indyCarRotation = SeriesRotation(
        complete = true,
        slots = listOf(
            RaceSlot("Indianapolis Motor Speedway", "The Brickyard Speedway", 27, 54, "Morning"),
            RaceSlot("Sunset Peninsula Raceway", "Speedway", 29, 71, "Afternoon"),
            RaceSlot("Homestead-Miami Speedway", "Speedway Circuit", 45, 79, "Late Morning"),
            RaceSlot("Daytona Intl Speedway", "Tri-Oval Circuit", 29, 51, "Sunrise"),
            RaceSlot("Eaglerock Speedway", "Oval Circuit", 50, 68, "Late Morning"),
            RaceSlot("Indianapolis Motor Speedway", "The Brickyard Speedway", 27, 54, "Morning"),
            RaceSlot("Sunset Peninsula Raceway", "Speedway", 29, 64, "Late Morning"),
            RaceSlot("Homestead-Miami Speedway", "Speedway Circuit", 45, 79, "Late Morning"),
            RaceSlot("Daytona Intl Speedway", "Tri-Oval Circuit", 29, 51, "Sunrise"),
            RaceSlot("Eaglerock Speedway", "Oval Circuit", 50, 68, "Late Morning")
        )
    )

    private fun profile(
        series: Series,
        raceLength: RaceLength,
        rotation: SeriesRotation,
        anchor: LocalDateTime? = null,
        rotationOffset: Int = 0,
        lastObserved: LocalDate? = null
    ) = SeriesSchedule(
        series = series,
        raceLength = raceLength,
        anchor = anchor,
        rotation = rotation,
        rotationOffset = rotationOffset,
        lastObserved = lastObserved
    )

    private val profiles: Map<Series, Map<RaceLength, SeriesSchedule>> = mapOf(
        Series.TOURING_CAR to mapOf(
            RaceLength.LONG to profile(
                Series.TOURING_CAR, RaceLength.LONG, touringCarRotation,
                anchor = LocalDateTime.of(2026, 9, 12, 11, 59),
                rotationOffset = 0,
                lastObserved = seenOn0914
            ),
            RaceLength.MEDIUM to profile(
                Series.TOURING_CAR, RaceLength.MEDIUM, touringCarRotation
            )
        ),
        Series.GT2 to mapOf(
            RaceLength.LONG to profile(
                Series.GT2, RaceLength.LONG, gt2Rotation,
                anchor = LocalDateTime.of(2026, 9, 12, 12, 5),
                rotationOffset = 0,
                lastObserved = seenOn0917
            ),
            RaceLength.MEDIUM to profile(
                Series.GT2, RaceLength.MEDIUM, gt2Rotation,
                anchor = LocalDateTime.of(2026, 9, 13, 19, 8),
                rotationOffset = 26,
                lastObserved = seenOn0914
            )
        ),
        Series.PROTO_H to mapOf(
            RaceLength.LONG to profile(
                Series.PROTO_H, RaceLength.LONG, protoHRotation,
                anchor = LocalDateTime.of(2026, 9, 13, 19, 15),
                rotationOffset = 17,
                lastObserved = seenOn0914
            ),
            RaceLength.MEDIUM to profile(
                Series.PROTO_H, RaceLength.MEDIUM, protoHRotation,
                anchor = LocalDateTime.of(2026, 9, 12, 12, 6),
                rotationOffset = 0,
                lastObserved = seenOn0912
            )
        ),
        Series.INDYCAR to mapOf(
            RaceLength.LONG to profile(
                Series.INDYCAR, RaceLength.LONG, indyCarRotation
            ),
            RaceLength.MEDIUM to profile(
                Series.INDYCAR, RaceLength.MEDIUM, indyCarRotation,
                anchor = LocalDateTime.of(2026, 9, 13, 19, 13),
                rotationOffset = 0,
                lastObserved = seenOn0914
            )
        )
    )

    fun scheduleFor(series: Series, now: LocalDateTime): SeriesSchedule =
        profiles.getValue(series).getValue(lengthFor(series, DailyCycle.startOf(now)))

    fun profilesFor(series: Series): Map<RaceLength, SeriesSchedule> = profiles.getValue(series)

    val recordedProfiles: String
        get() {
            val all = profiles.values.flatMap { it.values }
            return "${all.count { it.isRecorded }} of ${all.size}"
        }

    fun unrecordedFor(now: LocalDateTime): List<Series> =
        Series.entries.filter { !scheduleFor(it, now).isRecorded }
}
