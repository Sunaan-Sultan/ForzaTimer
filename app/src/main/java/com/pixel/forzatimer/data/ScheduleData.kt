package com.pixel.forzatimer.data

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

object ScheduleData {

    val playlistEndsOn: LocalDate = LocalDate.of(2026, 9, 16)

    val recordedCycleStart: LocalDateTime = LocalDateTime.of(2026, 9, 12, 6, 0)

    val nextCycleLengths: Map<Series, RaceLength> = mapOf(
        Series.GT3 to RaceLength.MEDIUM,
        Series.PROTO_H to RaceLength.LONG
    )

    fun isStale(now: LocalDateTime): Boolean = DailyCycle.startOf(now) != recordedCycleStart

    private val observedOn = LocalDate.of(2026, 9, 12)

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
        cadence = Duration.ofMinutes(20),
        anchor = LocalDateTime.of(2026, 9, 12, 12, 5),
        raceLength = RaceLength.LONG,
        entryOpensBefore = Duration.ofMinutes(25),
        entryClosesBefore = Duration.ofMinutes(3),
        seriesEndsOn = playlistEndsOn,
        lastObserved = observedOn,
        rotationComplete = true,
        rotation = listOf(
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

    private val protoH = SeriesSchedule(
        series = Series.PROTO_H,
        cadence = Duration.ofMinutes(16),
        anchor = LocalDateTime.of(2026, 9, 12, 12, 6),
        raceLength = RaceLength.MEDIUM,
        entryOpensBefore = Duration.ofMinutes(20),
        entryClosesBefore = Duration.ofMinutes(3),
        seriesEndsOn = playlistEndsOn,
        lastObserved = observedOn,
        rotationComplete = true,
        rotation = listOf(
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
            null,
            RaceSlot("Brands Hatch", "Grand Prix Circuit", 15, 45, "Night")
        )
    )

    private val indyCar = SeriesSchedule(
        series = Series.INDYCAR,
        cadence = Duration.ofMinutes(16),
        anchor = LocalDateTime.of(2026, 9, 12, 13, 5),
        raceLength = RaceLength.MEDIUM,
        entryOpensBefore = Duration.ofMinutes(20),
        entryClosesBefore = Duration.ofMinutes(3),
        seriesEndsOn = playlistEndsOn,
        lastObserved = observedOn,
        rotationComplete = true,
        rotation = listOf(
            RaceSlot("Daytona Intl Speedway", "Sports Car Circuit", 13, 70, "Late Afternoon"),
            RaceSlot("Sunset Peninsula Raceway", "Full Circuit Reverse", 15, 50, "Morning"),
            RaceSlot("WeatherTech Raceway Laguna Seca", "Full Circuit", 15, 68, "Late Afternoon"),
            RaceSlot("Michelin Raceway Road Atlanta", "Grand Prix Course", 16, 50, "Morning"),
            RaceSlot("Sebring International Raceway", "Full Circuit", 11, 57, "Morning"),
            RaceSlot("Grand Oak Raceway", "National Circuit", 17, 61, "Sunrise"),
            RaceSlot("Watkins Glen International Speedway", "Full Circuit", 12, 74, "Late Morning"),
            RaceSlot("Homestead-Miami Speedway", "Road Circuit", 16, 79, "Late Morning"),
            RaceSlot("Mid-Ohio Sports Car Course", "Sports Car Circuit", 16, 72, "Late Afternoon"),
            RaceSlot("Indianapolis Motor Speedway", "Grand Prix Circuit", 15, 65, "Late Morning"),
            RaceSlot("Grand Oak Raceway", "National Circuit Reverse", 17, 72, "Late Afternoon"),
            RaceSlot("Lime Rock Park", "Full Circuit", 25, 61, "Morning"),
            RaceSlot("Daytona Intl Speedway", "Sports Car Circuit", 13, 65, "Late Morning"),
            RaceSlot("Road America", "Full Circuit", 10, 78, "Afternoon"),
            RaceSlot("Sunset Peninsula Raceway", "Full Circuit", 15, 50, "Morning"),
            RaceSlot("Virginia International Raceway", "Full", 12, 85, "Late Afternoon"),
            RaceSlot("Maple Valley", "Full Circuit", 15, 72, "Late Afternoon")
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
