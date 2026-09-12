package com.pixel.forzatimer.data

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

object ScheduleData {

    val playlistEndsOn: LocalDate = LocalDate.of(2026, 9, 16)

    private val observedOn = LocalDate.of(2026, 9, 12)

    private val touringCar = SeriesSchedule(
        series = Series.TOURING_CAR,
        cadence = Duration.ofMinutes(20),
        anchor = LocalDateTime.of(2026, 9, 12, 11, 59),
        entryOpensBefore = Duration.ofMinutes(25),
        entryClosesBefore = Duration.ofMinutes(3),
        seriesEndsOn = playlistEndsOn,
        lastObserved = observedOn,
        rotationComplete = false,
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
            RaceSlot("Fujimi Kaido", "Full Circuit", 3, 68, "Afternoon")
        )
    )

    private val gt3 = SeriesSchedule(
        series = Series.GT3,
        cadence = Duration.ofMinutes(20),
        anchor = LocalDateTime.of(2026, 9, 12, 12, 5),
        entryOpensBefore = Duration.ofMinutes(25),
        entryClosesBefore = Duration.ofMinutes(3),
        seriesEndsOn = playlistEndsOn,
        lastObserved = observedOn,
        rotationComplete = false,
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
            RaceSlot("Yas Marina Circuit", "Full Circuit", 15, 71, "Night")
        )
    )

    private val protoH = SeriesSchedule(
        series = Series.PROTO_H,
        cadence = Duration.ofMinutes(16),
        anchor = LocalDateTime.of(2026, 9, 12, 12, 6),
        entryOpensBefore = Duration.ofMinutes(20),
        entryClosesBefore = Duration.ofMinutes(3),
        seriesEndsOn = playlistEndsOn,
        lastObserved = observedOn,
        rotationComplete = false,
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
            RaceSlot("Sunset Peninsula Raceway", "Full Circuit", 15, 68, "Late Afternoon")
        )
    )

    private val indyCar = SeriesSchedule(
        series = Series.INDYCAR,
        cadence = Duration.ofMinutes(16),
        anchor = LocalDateTime.of(2026, 9, 12, 13, 5),
        entryOpensBefore = Duration.ofMinutes(20),
        entryClosesBefore = Duration.ofMinutes(3),
        seriesEndsOn = playlistEndsOn,
        lastObserved = observedOn,
        rotationComplete = false,
        rotation = listOf(
            RaceSlot("Daytona Intl Speedway", "Sports Car Circuit", 13, 70, "Late Afternoon"),
            RaceSlot("Sunset Peninsula Raceway", "Full Circuit Reverse", 15, 50, "Morning"),
            RaceSlot("WeatherTech Raceway Laguna Seca", "Full Circuit", 15, 68, "Late Afternoon"),
            RaceSlot("Michelin Raceway Road Atlanta", "Grand Prix Course", 16, 50, "Morning"),
            RaceSlot("Sebring International Raceway", "Full Circuit", 11, 57, "Morning"),
            RaceSlot("Grand Oak Raceway", "National Circuit", 17, 61, "Sunrise"),
            RaceSlot("Watkins Glen International Speedway", "Full Circuit", 12, 74, "Late Morning")
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
