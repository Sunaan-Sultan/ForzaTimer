# Recording Forza timings

## How the app models a series

Forza's Featured Multiplayer runs each series on a **fixed cadence**. Races are not an
arbitrary list of times — they are one repeating track rotation fired every N minutes.
So the app stores four things per series instead of a big table of times:

| Field | Meaning |
| --- | --- |
| `cadence` | minutes between consecutive race starts |
| `anchor` | one known absolute race start, which is rotation index 0 |
| `rotation` | the ordered list of tracks, starting at the anchor |
| `rotationComplete` | `true` once we've seen the rotation loop back to the first round |

From those, `ScheduleEngine` computes every future race start forever. While
`rotationComplete` is `false`, the app still shows correct *times* past the end of the
recorded rotation, but labels the track "Not recorded yet" rather than guessing.

Entry windows are derived the same way: `entryOpensBefore` / `entryClosesBefore`.

## Reading the game UI

The left rail shows all four series. The **orange ✓ in the top-right corner of a tile is
the selected series** — the three event cards on the right belong to whichever tile carries
it. The tile at the top of the list is *not* the selected one. This is easy to misread:
the first pass of data collection logged GT3's events as Touring Car because of it.

Scroll the event cards right to see further into the rotation.

## Cadence summary

| Series | Cadence | Entry opens | Entry closes | Rounds recorded |
| --- | --- | --- | --- | --- |
| Touring Car | 20 min | 25 min before | 3 min before | 10 |
| GT3 | 20 min | 25 min before | 3 min before | 10 |
| Proto-H | 16 min | 20 min before | 3 min before | 11 |
| IndyCar | 16 min | 20 min before | 3 min before | 7 |

The entry lead time tracks the cadence: 20 min races get a 25 min lead, 16 min races get
20 min. In every case entry closes 3 min before lights out, and the entry window is longer
than the cadence, so two rounds are always joinable at once.

Start offsets within the hour: Touring Car :19/:39/:59, GT3 :05/:25/:45,
Proto-H and IndyCar drift on a 16 min cycle that doesn't divide the hour.

## A track repeating is not the rotation looping

Touring Car visits **Fujimi Kaido** twice — round 2 is *Full Circuit Reverse*, 3 laps,
63 °F, Morning; round 9 is *Full Circuit*, 3 laps, 68 °F, Afternoon. Proto-H visits
**Sunset Peninsula Raceway** twice — round 3 *Full Circuit Reverse*, Late Morning, round 10
*Full Circuit*, Late Afternoon. Same venue, different layout and conditions, so these are
distinct rounds, not the rotation wrapping.

**Only call a rotation complete when the whole slot matches** — track, layout, laps,
weather and time of day together. Matching on track name alone would have closed both of
these rotations far too early and made the app print confident nonsense.

## What we have so far

All observed **2026-09-12**, between 11:47 and 13:29 local. All four series showed
**ENDS: 4D**, so the playlist is assumed to roll over on **2026-09-16**
(`ScheduleData.playlistEndsOn`).

### Forza Touring Car Series — every 20 min, anchor 11:59 AM

| # | Race start | Track | Layout | Laps | Weather | Time of day |
| --- | --- | --- | --- | --- | --- | --- |
| 0 | 11:59 AM | Sebring International Raceway | Full Circuit | 13 | 65 °F | Sunset |
| 1 | 12:19 PM | Maple Valley | Full Circuit | 18 | 61 °F | Sunrise |
| 2 | 12:39 PM | Fujimi Kaido | Full Circuit Reverse | 3 | 63 °F | Morning |
| 3 | 12:59 PM | Hockenheimring | Full Circuit | 16 | 65 °F | Night |
| 4 | 01:19 PM | Michelin Raceway Road Atlanta | Grand Prix Course | 20 | 51 °F | Midnight |
| 5 | 01:39 PM | Circuit de Spa-Francorchamps | Full Circuit | 11 | 65 °F | Sunset |
| 6 | 01:59 PM | Nürburgring | GP Circuit | 13 | 55 °F | Night |
| 7 | 02:19 PM | Mid-Ohio Sports Car Course | Sports Car Circuit | 20 | 63 °F | Late Morning |
| 8 | 02:39 PM | Daytona Intl Speedway | Sports Car Circuit | 16 | 53 °F | Night |
| 9 | 02:59 PM | Fujimi Kaido | Full Circuit | 3 | 68 °F | Afternoon |

### Forza GT3 Series — every 20 min, anchor 12:05 PM

| # | Race start | Track | Layout | Laps | Weather | Time of day |
| --- | --- | --- | --- | --- | --- | --- |
| 0 | 12:05 PM | Homestead-Miami Speedway | Road Circuit | 21 | 82 °F | Afternoon |
| 1 | 12:25 PM | Watkins Glen International Speedway | Full Circuit | 16 | 81 °F | Late Afternoon |
| 2 | 12:45 PM | Sebring International Raceway | Full Circuit | 15 | 58 °F | Midnight |
| 3 | 01:05 PM | Grand Oak Raceway | National Circuit | 24 | 72 °F | Late Afternoon |
| 4 | 01:25 PM | Daytona Intl Speedway | Sports Car Circuit | 17 | 56 °F | Sunset |
| 5 | 01:45 PM | Lime Rock Park | Full Circuit Alt | 31 | 61 °F | Midnight |
| 6 | 02:05 PM | Mid-Ohio Sports Car Course | Sports Car Circuit | 21 | 50 °F | Sunrise |
| 7 | 02:25 PM | Sunset Peninsula Raceway | Full Circuit Reverse | 20 | 52 °F | Night |
| 8 | 02:45 PM | Nürburgring | GP Circuit | 14 | 65 °F | Afternoon |
| 9 | 03:05 PM | Yas Marina Circuit | Full Circuit | 15 | 71 °F | Night |

### Forza Proto-H Series — every 16 min, anchor 12:06 PM

| # | Race start | Track | Layout | Laps | Weather | Time of day |
| --- | --- | --- | --- | --- | --- | --- |
| 0 | 12:06 PM | Mount Panorama Circuit | Bathurst Circuit | 10 | 70 °F | Afternoon |
| 1 | 12:22 PM | Road America | Full Circuit | 10 | 78 °F | Late Afternoon |
| 2 | 12:38 PM | Homestead-Miami Speedway | Road Circuit | 16 | 75 °F | Sunset |
| 3 | 12:54 PM | Sunset Peninsula Raceway | Full Circuit Reverse | 15 | 64 °F | Late Morning |
| 4 | 01:10 PM | Watkins Glen International Speedway | Full Circuit | 12 | 61 °F | Morning |
| 5 | 01:26 PM | Le Mans - Circuit International de la Sarthe | Full Circuit | 6 | 48 °F | Sunrise |
| 6 | 01:42 PM | Silverstone Racing Circuit | Grand Prix Circuit | 11 | 70 °F | Afternoon |
| 7 | 01:58 PM | Indianapolis Motor Speedway | Grand Prix Circuit | 15 | 53 °F | Sunrise |
| 8 | 02:14 PM | Hockenheimring | Full Circuit | 13 | 65 °F | Night |
| 9 | 02:30 PM | Kyalami Grand Prix Circuit | Grand Prix Circuit | 13 | 77 °F | Afternoon |
| 10 | 02:46 PM | Sunset Peninsula Raceway | Full Circuit | 15 | 68 °F | Late Afternoon |

The Le Mans name is truncated in game as "LE MANS - CIRCUIT INTERNATIONAL DE LA SART…";
stored in full.

### IndyCar Series — every 16 min, anchor 01:05 PM

| # | Race start | Track | Layout | Laps | Weather | Time of day |
| --- | --- | --- | --- | --- | --- | --- |
| 0 | 01:05 PM | Daytona Intl Speedway | Sports Car Circuit | 13 | 70 °F | Late Afternoon |
| 1 | 01:21 PM | Sunset Peninsula Raceway | Full Circuit Reverse | 15 | 50 °F | Morning |
| 2 | 01:37 PM | WeatherTech Raceway Laguna Seca | Full Circuit | 15 | 68 °F | Late Afternoon |
| 3 | 01:53 PM | Michelin Raceway Road Atlanta | Grand Prix Course | 16 | 50 °F | Morning |
| 4 | 02:09 PM | Sebring International Raceway | Full Circuit | 11 | 57 °F | Morning |
| 5 | 02:25 PM | Grand Oak Raceway | National Circuit | 17 | 61 °F | Sunrise |
| 6 | 02:41 PM | Watkins Glen International Speedway | Full Circuit | 12 | 74 °F | Late Morning |

## Confidence in the derived constants

Three capture sets so far — ~11:50, ~13:00, ~13:28 — and on each later set every round the
app had already predicted from the earlier anchors matched the game exactly. Cadences and
anchors are solid; new captures only ever extend the rotations, never correct them.

Entry-window arithmetic, each confirmed twice:

- GT3, 11:54:57, the 12:05 PM race read "Entry ends in 00:07:02" → closes 12:01:59.
- GT3, 11:54:57, the 12:25 PM race read "Entry starts in 00:05:02" → opens 12:00:00.
- Proto-H, 11:56:56, the 12:22 PM race read "Entry starts in 00:05:04" → opens 12:02:00.
- Touring Car, 11:57:24, the 12:59 PM race read "Entry starts in 00:36:35" → opens 12:33:59.
- IndyCar, 13:01:25, the 01:37 PM race read "Entry starts in 00:15:35" → opens 01:17:00.

A race whose entry has closed but which hasn't started yet shows as **EXPIRED** in game;
the app shows it as `ENTRY CLOSED` and still counts down to lights out.

## Still open

No rotation has looped yet, so all four are `rotationComplete = false`. Known lower bounds
on rotation length: Touring Car ≥ 10, GT3 ≥ 10, Proto-H ≥ 11, IndyCar ≥ 7.

To close one out, capture far enough ahead to catch round 0 recurring as an exact slot
match. For Touring Car that is Sebring / Full Circuit / 13 laps / 65 °F / Sunset; at 20 min
a round, 10 rounds already covers 3h 20m, so the loop point is late afternoon at the
earliest. Proto-H is the cheapest to finish — 16 min rounds, so its 11 known rounds only
span 2h 56m.

## How to record more

Open **Featured Multiplayer → FEATURED**, put the ✓ on a series, and capture the event
cards plus the `LOCAL TIME` readout top right. Scroll right for later rounds. What's needed
per card: track, layout, laps, weather, time of day, "Race starts at".

Data lives in one file: `app/src/main/java/com/pixel/forzatimer/data/ScheduleData.kt`.
Nothing else needs touching to add a series.

## Building

```
gradlew :app:assembleDebug
```

`java.time` is used throughout with core library desugaring enabled, so it works down to
the project's `minSdk 24`.
