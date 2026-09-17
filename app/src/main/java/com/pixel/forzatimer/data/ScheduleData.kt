package com.pixel.forzatimer.data

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

object ScheduleData {

    val playlistEndsOn: LocalDate = LocalDate.of(2026, 9, 16)

    val recordedCycleStart: LocalDateTime = LocalDateTime.of(2026, 9, 13, 6, 0)

    val nextCycleLengths: Map<Series, RaceLength> = emptyMap()

    fun isStale(now: LocalDateTime): Boolean = DailyCycle.startOf(now) != recordedCycleStart

    private val observedOn = LocalDate.of(2026, 9, 14)

    private val touringCar = SeriesSchedule(
        series = Series.TOURING_CAR,
        cadence = Duration.ofMinutes(20),
        anchor = LocalDateTime.of(2026, 9, 12, 11, 59),
        raceLength = RaceLength.LONG,
        entryOpensBefore = Duration.ofMinutes(25),
        entryClosesBefore = Duration.ofMinutes(3),
        seriesEndsOn = playlistEndsOn,
        lastObserved = observedOn,
        rotationComplete = true,
        rotation = listOf(
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

    private val gt3 = SeriesSchedule(
        series = Series.GT3,
        cadence = Duration.ofMinutes(16),
        anchor = LocalDateTime.of(2026, 9, 13, 19, 8),
        raceLength = RaceLength.MEDIUM,
        entryOpensBefore = Duration.ofMinutes(20),
        entryClosesBefore = Duration.ofMinutes(3),
        seriesEndsOn = playlistEndsOn,
        lastObserved = observedOn,
        rotationComplete = false,
        rotation = listOf(
            RaceSlot("WeatherTech Raceway Laguna Seca", "Full Circuit", 14, 68, "Late Morning"),
            RaceSlot("Sunset Peninsula Raceway", "Full Circuit", 13, 51, "Midnight"),
            RaceSlot("Silverstone Racing Circuit", "Grand Prix Circuit", 10, 50, "Midnight"),
            RaceSlot("Mid-Ohio Sports Car Course", "Sports Car Circuit", 14, 70, "Afternoon"),
            RaceSlot("Michelin Raceway Road Atlanta", "Grand Prix Course", 15, 72, "Afternoon"),
            RaceSlot("Grand Oak Raceway", "National Circuit Reverse", 16, 77, "Late Morning"),
            RaceSlot("Nürburgring", "GP Circuit", 10, 61, "Late Morning"),
            RaceSlot("Circuit de Spa-Francorchamps", "Full Circuit", 8, 68, "Late Afternoon"),
            RaceSlot("Brands Hatch", "Grand Prix Circuit", 13, 43, "Morning"),
            RaceSlot("Watkins Glen International Speedway", "Full Circuit", 11, 63, "Night"),
            null,
            null,
            null,
            RaceSlot("Kyalami Grand Prix Circuit", "Grand Prix Circuit", 11, 59, "Sunset"),
            RaceSlot("Sunset Peninsula Raceway", "Full Circuit Reverse", 13, 61, "Sunset"),
            RaceSlot("Virginia International Raceway", "Full", 11, 65, "Sunrise"),
            RaceSlot("Yas Marina Circuit", "Full Circuit", 10, 69, "Morning"),
            RaceSlot("Suzuka Circuit", "Full Circuit", 10, 68, "Late Afternoon"),
            null,
            null,
            RaceSlot("Le Mans - Circuit International de la Sarthe", "Full Circuit", 5, 65, "Late Afternoon"),
            RaceSlot("Nürburgring", "Full Circuit", 2, 52, "Midnight"),
            RaceSlot("Mugello Circuit", "Full Circuit", 11, 84, "Late Afternoon"),
            RaceSlot("Maple Valley", "Full Circuit", 13, 63, "Morning"),
            RaceSlot("Hakone", "Grand Prix Circuit", 16, 60, "Sunrise"),
            RaceSlot("Road America", "Full Circuit", 9, 66, "Sunset"),
            RaceSlot("Indianapolis Motor Speedway", "Grand Prix Circuit", 14, 54, "Morning"),
            RaceSlot("Circuit de Barcelona-Catalunya", "Grand Prix Circuit", 11, 69, "Sunset"),
            RaceSlot("Nürburgring", "Nordschleife", 3, 63, "Sunset"),
            RaceSlot("Homestead-Miami Speedway", "Road Circuit", 14, 79, "Late Morning")
        )
    )

    private val protoH = SeriesSchedule(
        series = Series.PROTO_H,
        cadence = Duration.ofMinutes(20),
        anchor = LocalDateTime.of(2026, 9, 13, 19, 15),
        raceLength = RaceLength.LONG,
        entryOpensBefore = Duration.ofMinutes(25),
        entryClosesBefore = Duration.ofMinutes(3),
        seriesEndsOn = playlistEndsOn,
        lastObserved = observedOn,
        rotationComplete = true,
        rotation = listOf(
            RaceSlot("Maple Valley", "Full Circuit", 22, 77, "Late Morning"),
            RaceSlot("Circuit de Barcelona-Catalunya", "Grand Prix Circuit", 18, 65, "Late Morning"),
            RaceSlot("Circuit de Spa-Francorchamps", "Full Circuit", 14, 68, "Late Afternoon"),
            RaceSlot("Lime Rock Park", "Full Circuit", 38, 75, "Late Morning"),
            RaceSlot("Nürburgring", "Full Circuit", 4, 61, "Late Morning"),
            RaceSlot("Nürburgring", "Nordschleife", 5, 66, "Late Afternoon"),
            RaceSlot("Michelin Raceway Road Atlanta", "Grand Prix Course", 25, 72, "Afternoon"),
            RaceSlot("Hakone", "Grand Prix Circuit", 27, 71, "Late Morning"),
            RaceSlot("WeatherTech Raceway Laguna Seca", "Full Circuit", 23, 68, "Late Morning"),
            null,
            RaceSlot("Virginia International Raceway", "Full", 18, 68, "Night"),
            RaceSlot("Mugello Circuit", "Full Circuit", 18, 83, "Afternoon"),
            RaceSlot("Sebring International Raceway", "Full Circuit", 16, 60, "Night"),
            RaceSlot("Brands Hatch", "Grand Prix Circuit", 22, 43, "Sunrise"),
            RaceSlot("Mount Panorama Circuit", "Bathurst Circuit", 16, 41, "Sunrise"),
            RaceSlot("Road America", "Full Circuit", 15, 59, "Sunrise"),
            RaceSlot("Homestead-Miami Speedway", "Road Circuit", 24, 79, "Late Morning"),
            RaceSlot("Sunset Peninsula Raceway", "Full Circuit Reverse", 22, 52, "Night"),
            RaceSlot("Watkins Glen International Speedway", "Full Circuit", 18, 74, "Late Morning"),
            RaceSlot("Le Mans - Circuit International de la Sarthe", "Full Circuit", 8, 48, "Morning"),
            RaceSlot("Silverstone Racing Circuit", "Grand Prix Circuit", 16, 52, "Morning"),
            RaceSlot("Indianapolis Motor Speedway", "Grand Prix Circuit", 23, 72, "Afternoon"),
            RaceSlot("Hockenheimring", "Full Circuit", 19, 65, "Night"),
            RaceSlot("Kyalami Grand Prix Circuit", "Grand Prix Circuit", 19, 56, "Midnight"),
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
    )

    private val indyCar = SeriesSchedule(
        series = Series.INDYCAR,
        cadence = Duration.ofMinutes(16),
        anchor = LocalDateTime.of(2026, 9, 13, 19, 13),
        raceLength = RaceLength.MEDIUM,
        entryOpensBefore = Duration.ofMinutes(20),
        entryClosesBefore = Duration.ofMinutes(3),
        seriesEndsOn = playlistEndsOn,
        lastObserved = observedOn,
        rotationComplete = true,
        rotation = listOf(
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

    val schedules: Map<Series, SeriesSchedule> = listOf(
        touringCar,
        gt3,
        protoH,
        indyCar
    ).associateBy { it.series }

    fun scheduleFor(series: Series): SeriesSchedule? = schedules[series]
}
