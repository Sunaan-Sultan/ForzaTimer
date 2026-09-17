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

## Cadence summary — cycle of 2026-09-13

| Series | Race length | Cadence | Entry opens | Entry closes | Rounds recorded |
| --- | --- | --- | --- | --- | --- |
| Touring Car | Long | 20 min | 25 min before | 3 min before | 33, loop confirmed five times |
| GT3 | Medium | 16 min | 20 min before | 3 min before | 25 of 30, loop almost certainly 31 |
| Proto-H | Long | 20 min | 25 min before | 3 min before | 23 of 31, loop confirmed by wrap |
| IndyCar | Medium | 16 min | 20 min before | 3 min before | 10, loop confirmed twice |

In every case entry closes 3 min before lights out, and the entry window is longer than the
cadence, so two rounds are always joinable at once.

Start offsets within the hour: Touring Car :19/:39/:59, Proto-H :15/:35/:55, GT3 and
IndyCar drift on a 16 min cycle that doesn't divide the hour.

## The daily cycle — most of this expires

Each series is assigned a **race-length category** for the day, and the assignment
**reshuffles every day at roughly 06:00 local** (Bangladesh, UTC+6):

- **Long** — roughly 30 minutes of racing
- **Medium** — roughly 20 minutes

Known assignments:

| Series | Cycle of 2026-09-12 | Cycle of 2026-09-13 |
| --- | --- | --- |
| Touring Car | Long | Long |
| GT3 | Long | Medium |
| Proto-H | Medium | Long |
| IndyCar | Medium | Medium |

Both cycles ran two long and two medium series. GT3 and Proto-H traded places; Touring Car
and IndyCar held. That is not enough to predict 2026-09-14, so `nextCycleLengths` is empty
and the stale banner no longer offers a guess.

`DailyCycle` models the 06:00 boundary and `ScheduleData.isStale(now)` compares the current
cycle against `recordedCycleStart`; when they differ the app shows a **SCHEDULE OUT OF
DATE** banner instead of quietly presenting yesterday's lineup as today's.

### Cadence follows the category, not the series — confirmed

The 2026-09-13 cycle was the test, and it passed on both series that moved:

| Category | Cadence | Entry opens before | Entry closes before |
| --- | --- | --- | --- |
| Long | 20 min | 25 min | 3 min |
| Medium | 16 min | 20 min | 3 min |

GT3 went long → medium and dropped from 20/25 to 16/20. Proto-H went medium → long and rose
from 16/20 to 20/25. Touring Car and IndyCar kept their categories and their constants.
A new cycle now only needs its per-series category read off; the timing constants follow.

## What survives the 06:00 reshuffle

The reshuffle is not a full reset, but what survives is decided **per series**, and it is
not decided by the category. Of the four series:

| Series | Category moved? | Track order across the boundary |
| --- | --- | --- |
| Touring Car | no | survived — same rotation, same anchor, same phase |
| Proto-H | medium → long | survived — the whole 31-round rotation, phase-shifted by 17 |
| GT3 | long → medium | **did not survive** — same track pool, scrambled order |
| IndyCar | no | **did not survive** — different track pool entirely (road → oval) |

So "the category held, therefore the rotation held" is false: IndyCar kept its category and
still had its whole rotation replaced. And "the category moved, therefore the rotation is
gone" is also false: Proto-H moved and kept its order exactly. **Every series has to be
re-checked against one known round after a reshuffle, regardless of category.**

Touring Car's category did not change, and its entire model carried across the boundary
untouched:

- anchor 2026-09-12 11:59 AM, cadence 20 min, 33-round loop
- the 07:19 PM race on 2026-09-13 is rotation index 94, i.e. loop position 28
- positions 28–32 were predicted from 2026-09-12 data and matched the game exactly —
  track, layout, laps, weather and time of day, five for five
- a second capture at 08:13 PM caught the wrap itself: 08:19/08:39 PM are positions 31/32,
  then 08:59 PM is Sebring Full Circuit, 13 laps, 65 °F, Sunset — position **0** again, and
  09:19/09:39 PM are positions 1 and 2 (Maple Valley, then Fujimi Kaido Full Circuit
  Reverse). The list does not just repeat at 33, it repeats *in place* across a reshuffle.

So the rotation **keeps running through 06:00**; it is not re-rolled and it does not restart
at index 0. An anchor from an earlier cycle stays valid as long as the category holds.

What the category change *does* rewrite, seen on Proto-H (medium → long):

- **laps scale up by roughly 1.5×** — Maple Valley 15 → 22, Barcelona 12 → 18, Spa 9 → 14,
  Lime Rock 25 → 38, Nürburgring Full 2 → 4. GT3 moving the other way scales by about 0.68:
  Laguna Seca 20 → 14, Sunset Peninsula 20 → 13, Silverstone 14 → 10, Mid-Ohio 21 → 14,
  Road Atlanta 22 → 15.
- **weather and time of day are re-rolled**, not always to a different value — Proto-H's
  first three rounds kept 77/65/68 °F while the last two moved 81 → 75 and 51 → 61 °F.
- **cadence and phase change**, so the old anchor is useless even if the track order isn't.

Because laps and conditions are category-specific, none of a series' recorded rounds can be
reused after its category moves — only the track order might be.

### The track order can survive a category change — settled for Proto-H

Proto-H's first nine recorded rounds on 2026-09-13 are, in order, Maple Valley → Barcelona → Spa
→ Lime Rock → Nürburgring Full → Nürburgring Nordschleife → Road Atlanta → Hakone → Laguna
Seca. Those are exactly positions **17–25** of the 2026-09-12 Proto-H rotation, in order,
with identical layouts. Nine consecutive matches, with only laps and conditions rewritten:

| 09-12 pos | Track / layout | Laps then → now | Ratio |
| --- | --- | --- | --- |
| 17 | Maple Valley, Full Circuit | 15 → 22 | 1.47 |
| 18 | Barcelona, Grand Prix Circuit | 12 → 18 | 1.50 |
| 19 | Spa, Full Circuit | 9 → 14 | 1.56 |
| 20 | Lime Rock Park, Full Circuit | 25 → 38 | 1.52 |
| 21 | Nürburgring, Full Circuit | 2 → 4 | 2.00 |
| 22 | Nürburgring, Nordschleife | 3 → 5 | 1.67 |
| 23 | Road Atlanta, Grand Prix Course | 16 → 25 | 1.56 |
| 24 | Hakone, Grand Prix Circuit | 18 → 27 | 1.50 |
| 25 | Laguna Seca, Full Circuit | 15 → 23 | 1.53 |

The order also does not restart — position 17 is where the rotation happened to be, so the
*phase* moved (new anchor, new cadence) while the *sequence* kept running.

**But GT3 disproves it as a rule.** GT3 also changed category, its laps scaled by the same
kind of constant (0.65–0.71 on its five new rounds that appear in the old list), and every
one of its 2026-09-13 tracks appears somewhere in the 2026-09-12 GT3 list — but the order is
scrambled. Its twenty-five observed rounds map to old positions 26, 18, 21, 6, 27, 15, 8, 19,
25, 1, 24, 7, 12, 9, 29, 22, 23, 14, 11, 28, 16, 10, 20, 30, 0. That is not a run, not a
reversal, and not a constant stride — but all twenty-five are **distinct**, so GT3 looks like a
re-permutation of the same 31-slot pool rather than a new list. See the GT3 section below for
how strong that has now got.

So a category change *may* preserve track order and *may* not, and there is no signal yet
for which. The 2026-09-13 data is still stored as pure observation — nothing is
extrapolated from the previous cycle's list into `ScheduleData.kt`.

**The Proto-H run does keep going — predicted and then observed.** This file previously
wrote down positions 26–30 of the 2026-09-12 list as the cheapest test. The 22:30 capture
set caught four of those five slots, and every one that was testable landed:

| Round | Race start | 09-12 pos | Predicted track | Observed | Laps then → now |
| --- | --- | --- | --- | --- | --- |
| 9 | 10:15 PM | 26 | Mid-Ohio Sports Car Circuit | *not captured* | — |
| 10 | 10:35 PM | 27 | Virginia International Raceway, Full | ✔ same | 12 → 18 |
| 11 | 10:55 PM | 28 | Mugello Circuit, Full Circuit | ✔ same | 12 → 18 |
| 12 | 11:15 PM | 29 | *(unrecorded on 09-12)* | Sebring, Full Circuit | — |
| 13 | 11:35 PM | 30 | Brands Hatch, Grand Prix Circuit | ✔ same | 15 → 22 |

Three predictions, three hits, with the 1.5× lap scaling holding to the round. Position 29
was a hole in the old list, so it could not be predicted; it is now filled from this cycle.
That makes **thirteen consecutive matched positions (17–30)** rather than the nine claimed
above, and it rules out "the first nine happened to line up". Proto-H's track order really
did survive its category change intact; only laps, weather and time of day were re-rolled.

**And then it wrapped — Proto-H's rotation is the 2026-09-12 rotation entire.** The 00:10
capture set caught rounds 15–19, and round 14 had already reached 09-12 position 30, the last
one in that list. Position 30 is followed by position 0, and that is exactly what the game
showed:

| Round | Race start | 09-12 pos | Track / layout | Laps then → now |
| --- | --- | --- | --- | --- |
| 14 | 11:55 PM | 30 | Mount Panorama, Bathurst Circuit | *(wrap point)* 10 → 16 |
| 15 | 12:15 AM | **0** | Road America, Full Circuit | 10 → 15 |
| 16 | 12:35 AM | 1 | Homestead-Miami, Road Circuit | 16 → 24 |
| 17 | 12:55 AM | 2 | Sunset Peninsula, Full Circuit Reverse | 15 → 22 |
| 18 | 01:15 AM | 3 | Watkins Glen, Full Circuit | 12 → 18 |
| 19 | 01:35 AM | 4 | Le Mans, Full Circuit | 6 → 8 |

Every 2026-09-13 round maps to 09-12 position `(17 + n) mod 31`, and **twenty-two of
twenty-two testable positions match** — nine, then four, then five across the wrap, and then
rounds 20–23 at 01:30, which had been written into this file as predictions before they ran:
Silverstone GP, Indianapolis GP, Hockenheimring Full, Kyalami GP, i.e. 09-12 positions 6–9.
Four predictions, four hits. Proto-H's rotation is **31 rounds long and complete**:
`rotationComplete` is `true` with a 31-entry list.

Eight of those 31 entries are still `null` — round 9, and rounds 24–30, which this cycle has
not reached yet. Their **tracks** are known from the 09-12 list, but their laps and conditions
are category-specific and were re-rolled, so nothing is copied across. Storing the length is a
structural fact; storing borrowed content would not be. What the old list buys is that the
remaining rounds are predictions rather than discoveries — 09-12 positions 10–16:

| Round | Race start | Expected track / layout |
| --- | --- | --- |
| 24 | 03:15 AM | Sunset Peninsula Raceway, Full Circuit |
| 25 | 03:35 AM | *(hole in the 09-12 list — unknown)* |
| 26 | 03:55 AM | Grand Oak Raceway, National Circuit Reverse |
| 27 | 04:15 AM | Nürburgring, GP Circuit |
| 28 | 04:35 AM | Daytona Intl Speedway, Sports Car Circuit |
| 29 | 04:55 AM | Yas Marina Circuit, Full Circuit |
| 30 | 05:15 AM | Grand Oak Raceway, National Circuit |

Round 31 at 05:35 AM is then the wrap back to round 0 (Maple Valley, Full Circuit, 22 laps,
77 °F, Late Morning) and is the last check available before the 06:00 reshuffle.

## A rotation loop is easy to call too early

**Touring Car's 33-round loop is real** — confirmed a second time, 61 rounds after the
anchor, by the cross-boundary prediction above.

**IndyCar's loop is 10 rounds, not 5 — and the 5 was recorded and then retracted.** This is
the second time a short loop has been called too early on this series, and it is worth keeping
the whole sequence visible rather than tidying it away.

What happened, in order:

1. Rounds 5–8 repeated 0–3 exactly. Round 6 disagreed with round 1 on **both** weather and
   time of day (64 °F / Late Morning against 71 °F / Afternoon), so the loop was *not* called.
2. Rounds 13–17 arrived and matched their `index % 5` slots on every field, including round 16
   agreeing with round 6. Two readings against one looked like a misread on the 07:29 PM card,
   so `rotationComplete` was set to `true` with a 5-round rotation and round 1 was overwritten
   with 64 °F / Late Morning.
3. **Round 21 read 71 °F / Afternoon.** That is round 1's original value, at `21 % 10 == 1`.
   Nothing was misread. The Sunset Peninsula slot genuinely alternates between two condition
   sets, and the true period is **10**.

Under a period of 10 every observation fits and all ten positions are pinned:

| Pos | Track / layout | Laps | Weather | Time of day | Seen at indices |
| --- | --- | --- | --- | --- | --- |
| 0 | Indianapolis, The Brickyard Speedway | 27 | 54 °F | Morning | 0, 20 |
| 1 | Sunset Peninsula, Speedway | 29 | **71 °F** | **Afternoon** | 1, 21 |
| 2 | Homestead-Miami, Speedway Circuit | 45 | 79 °F | Late Morning | 2, 22 |
| 3 | Daytona, Tri-Oval Circuit | 29 | 51 °F | Sunrise | 3, 13, 23 |
| 4 | Eaglerock Speedway, Oval Circuit | 50 | 68 °F | Late Morning | 4, 14 |
| 5 | Indianapolis, The Brickyard Speedway | 27 | 54 °F | Morning | 5, 15 |
| 6 | Sunset Peninsula, Speedway | 29 | **64 °F** | **Late Morning** | 6, 16 |
| 7 | Homestead-Miami, Speedway Circuit | 45 | 79 °F | Late Morning | 7, 17 |
| 8 | Daytona, Tri-Oval Circuit | 29 | 51 °F | Sunrise | 8 |
| 9 | Eaglerock Speedway, Oval Circuit | 50 | 68 °F | Late Morning | 19 |

Nineteen observed rounds across indices 0–23, zero contradictions, and the loop verified at
offset 10 on five pairs and at offset 20 on four more. Positions 0/5, 2/7, 3/8 and 4/9 are
identical to each other, which is exactly why the sequence reads as a 5-round loop from any
window that never contains both a position-1 and a position-6 round. **The track sequence has
period 5; the full slot including conditions has period 10.**

`ScheduleData.kt` now stores ten slots with `rotationComplete = true`, and round 1 is restored
to its original 71 °F / Afternoon.

**Confirmed again at 01:30.** Rounds 24–28 are positions 4–8, and position 6 read 29 laps,
**64 °F, Late Morning** — five rounds after index 21 read position 1 as **71 °F, Afternoon**.
Both Sunset Peninsula variants were therefore seen inside a single capture window, in the
right order, which is the one observation a 5-round loop cannot produce. Twenty-four rounds
observed across indices 0–28, still zero contradictions.

**The lesson, sharpened.** The rule already in this file — a full-slot repeat is not proof of
a loop — was applied correctly at step 1 and then abandoned at step 2 under the pressure of a
2-against-1 vote. Two readings agreeing does not outvote one reading disagreeing; in a
rotation, identical values are the *expected* case and a lone disagreement is the informative
one. When one field pair refuses to fit a candidate period, prefer a longer period over
declaring the odd reading a mistake, unless the screenshot has actually been re-examined.

**IndyCar's 17-round loop was wrong.** Its category and phase did not change on 2026-09-13,
so the old anchor still places the 07:13 PM race at rotation index 113, which is position 11
of a 17-round loop — Lime Rock Park, Full Circuit, 25 laps. The game showed Indianapolis
Motor Speedway, The Brickyard Speedway, 27 laps. Every IndyCar round seen on 2026-09-13 is
an oval or speedway layout, and not one of those layouts appears anywhere in the 17 road
rounds recorded on 2026-09-12. The rotation was not extended, it was replaced outright — the
real one is five oval rounds with no overlap at all with the old list.

GT3's 31-round loop is unverifiable now that its category has moved, but it is suspect for
the same reason: its five rounds on 2026-09-13 are not consecutive anywhere in the 31.

So `rotationComplete` is `false` for GT3 alone, and the pre-existing warning stands and needs
strengthening:

Touring Car visits **Fujimi Kaido** twice — round 2 is *Full Circuit Reverse*, 3 laps,
63 °F, Morning; round 9 is *Full Circuit*, 3 laps, 68 °F, Afternoon. Proto-H visits
**Sunset Peninsula Raceway** twice — round 3 *Full Circuit Reverse*, Late Morning, round 10
*Full Circuit*, Late Afternoon. Same venue, different layout and conditions, so these are
distinct rounds, not the rotation wrapping.

**Matching the whole slot — track, layout, laps, weather, time of day — is necessary but
not sufficient.** A full-slot repeat can still be a genuine repeat inside a longer rotation,
which is how the 17-round IndyCar loop survived a pass and was still wrong. Only call a
rotation complete when the loop has held across a second pass, or better, across a cycle
boundary — and even then, check that no *single* field disagrees anywhere in the data before
committing. IndyCar's real 10-round loop was found only because a lone weather reading kept
refusing to fit. Touring Car's 33-round loop has now cleared the bar four times.

## What we have so far — cycle of 2026-09-13

Five capture sets, all in the cycle that began 06:00 on **2026-09-13**: the first between
19:02 and 19:04 local, the second between 20:12 and 20:13, the third between 22:30 and 22:31,
the fourth between 00:09 and 00:10 on 2026-09-14 and the fifth between 01:29 and 01:30 — all
the same cycle, since the reshuffle is at 06:00. Each is two scrolled screenshots per series.
All four series showed
**ENDS: 3D**, consistent with the playlist rolling over on **2026-09-16**
(`ScheduleData.playlistEndsOn`, unchanged).

Rows marked ✎ come from the second set, ✦ from the third, ✧ from the fourth and ✩ from the
fifth. Where the sets overlap or predict each other they agree exactly.

The later sets skip rounds — each was taken an hour or more after the last, and the rounds in
between had already run. Those slots are stored as `null` in the rotation list;
`SeriesSchedule.hasGaps` covers this and the app falls back to "Not recorded yet" for them
while still showing correct start times.

### Forza Touring Car Series — every 20 min, anchor 2026-09-12 11:59 AM

Unchanged from the 2026-09-12 cycle: same category, same cadence, same anchor, same
33-round loop, `rotationComplete = true`. Every round below was predicted before it was
observed: positions 28–32, the wrap into 0–2, then 5–9, 10–14 and 15–18 from the third, fourth
and fifth sets. Loop positions 0–2 and 5–18 have now been verified in this cycle.

| Loop # | Race start | Track | Layout | Laps | Weather | Time of day |
| --- | --- | --- | --- | --- | --- | --- |
| 28 | 07:19 PM | Road America | Full Circuit | 13 | 59 °F | Sunrise |
| 29 | 07:39 PM | Nürburgring | Nordschleife | 4 | 51 °F | Sunrise |
| 30 | 07:59 PM | Sunset Peninsula Raceway | Full Circuit Reverse | 18 | 52 °F | Night |
| 31 | 08:19 PM | Lime Rock Park | Full Circuit | 32 | 81 °F | Late Afternoon |
| 32 | 08:39 PM | Kyalami Grand Prix Circuit | Grand Prix Circuit | 16 | 74 °F | Late Morning |
| ✎ 0 | 08:59 PM | Sebring International Raceway | Full Circuit | 13 | 65 °F | Sunset |
| ✎ 1 | 09:19 PM | Maple Valley | Full Circuit | 18 | 61 °F | Sunrise |
| ✎ 2 | 09:39 PM | Fujimi Kaido | Full Circuit Reverse | 3 | 63 °F | Morning |
| ✦ 5 | 10:39 PM | Circuit de Spa-Francorchamps | Full Circuit | 11 | 65 °F | Sunset |
| ✦ 6 | 10:59 PM | Nürburgring | GP Circuit | 13 | 55 °F | Night |
| ✦ 7 | 11:19 PM | Mid-Ohio Sports Car Course | Sports Car Circuit | 20 | 63 °F | Late Morning |
| ✦ 8 | 11:39 PM | Daytona Intl Speedway | Sports Car Circuit | 16 | 53 °F | Night |
| ✦ 9 | 11:59 PM | Fujimi Kaido | Full Circuit | 3 | 68 °F | Afternoon |
| ✧ 10 | 12:19 AM | Hakone | Grand Prix Circuit | 22 | 68 °F | Late Afternoon |
| ✧ 11 | 12:39 AM | Yas Marina Circuit | Full Circuit | 14 | 69 °F | Morning |
| ✧ 12 | 12:59 AM | Circuit de Barcelona-Catalunya | Grand Prix Circuit | 15 | 57 °F | Sunrise |
| ✧ 13 | 01:19 AM | Watkins Glen International Speedway | Full Circuit | 15 | 74 °F | Late Morning |
| ✧ 14 | 01:39 AM | Mount Panorama Circuit | Bathurst Circuit | 13 | 70 °F | Afternoon |
| ✩ 15 | 01:59 AM | Sunset Peninsula Raceway | Full Circuit | 18 | 52 °F | Night |
| ✩ 16 | 02:19 AM | Indianapolis Motor Speedway | Grand Prix Circuit | 19 | 72 °F | Afternoon |
| ✩ 17 | 02:39 AM | Homestead-Miami Speedway | Road Circuit | 20 | 69 °F | Sunrise |
| ✩ 18 | 02:59 AM | Brands Hatch | Grand Prix Circuit | 19 | 59 °F | Afternoon |

Round 14 (01:39 AM) was captured in both the fourth and fifth sets and reads identically.

The ✦ rows are rotation indices 104–108 (loop positions 5–9), the ✧ rows 109–113 (positions
10–14) and the ✩ rows 114–117 (positions 15–18). All were already in the recorded rotation from
2026-09-12 and all matched on every field — third, fourth and fifth confirmations of the
33-round loop, at parts of the list the 28–32 check never touched. Twenty-four rounds checked
against the stored list across five sets, not one wrong. Position 9 is also the second Fujimi Kaido visit (Full Circuit,
68 °F, Afternoon), distinct from position 2 (Full Circuit Reverse, 63 °F, Morning), exactly as
recorded.

### Forza GT3 Series — every 16 min, anchor 07:08 PM

| # | Race start | Track | Layout | Laps | Weather | Time of day |
| --- | --- | --- | --- | --- | --- | --- |
| 0 | 07:08 PM | WeatherTech Raceway Laguna Seca | Full Circuit | 14 | 68 °F | Late Morning |
| 1 | 07:24 PM | Sunset Peninsula Raceway | Full Circuit | 13 | 51 °F | Midnight |
| 2 | 07:40 PM | Silverstone Racing Circuit | Grand Prix Circuit | 10 | 50 °F | Midnight |
| 3 | 07:56 PM | Mid-Ohio Sports Car Course | Sports Car Circuit | 14 | 70 °F | Afternoon |
| 4 | 08:12 PM | Michelin Raceway Road Atlanta | Grand Prix Course | 15 | 72 °F | Afternoon |
| ✎ 5 | 08:28 PM | Grand Oak Raceway | National Circuit Reverse | 16 | 77 °F | Late Morning |
| ✎ 6 | 08:44 PM | Nürburgring | GP Circuit | 10 | 61 °F | Late Morning |
| ✎ 7 | 09:00 PM | Circuit de Spa-Francorchamps | Full Circuit | 8 | 68 °F | Late Afternoon |
| ✎ 8 | 09:16 PM | Brands Hatch | Grand Prix Circuit | 13 | 43 °F | Morning |
| ✎ 9 | 09:32 PM | Watkins Glen International Speedway | Full Circuit | 11 | 63 °F | Night |
| — | 09:48–10:20 PM | *rounds 10–12 not captured* | | | | |
| ✦ 13 | 10:36 PM | Kyalami Grand Prix Circuit | Grand Prix Circuit | 11 | 59 °F | Sunset |
| ✦ 14 | 10:52 PM | Sunset Peninsula Raceway | Full Circuit Reverse | 13 | 61 °F | Sunset |
| ✦ 15 | 11:08 PM | Virginia International Raceway | Full | 11 | 65 °F | Sunrise |
| ✦ 16 | 11:24 PM | Yas Marina Circuit | Full Circuit | 10 | 69 °F | Morning |
| ✦ 17 | 11:40 PM | Suzuka Circuit | Full Circuit | 10 | 68 °F | Late Afternoon |
| — | 11:56 PM–12:12 AM | *rounds 18–19 not captured* | | | | |
| ✧ 20 | 12:28 AM | Le Mans - Circuit International de la Sarthe | Full Circuit | 5 | 65 °F | Late Afternoon |
| ✧ 21 | 12:44 AM | Nürburgring | Full Circuit | 2 | 52 °F | Midnight |
| ✧ 22 | 01:00 AM | Mugello Circuit | Full Circuit | 11 | 84 °F | Late Afternoon |
| ✧ 23 | 01:16 AM | Maple Valley | Full Circuit | 13 | 63 °F | Morning |
| ✧ 24 | 01:32 AM | Hakone | Grand Prix Circuit | 16 | 60 °F | Sunrise |
| ✩ 25 | 01:48 AM | Road America | Full Circuit | 9 | 66 °F | Sunset |
| ✩ 26 | 02:04 AM | Indianapolis Motor Speedway | Grand Prix Circuit | 14 | 54 °F | Morning |
| ✩ 27 | 02:20 AM | Circuit de Barcelona-Catalunya | Grand Prix Circuit | 11 | 69 °F | Sunset |
| ✩ 28 | 02:36 AM | Nürburgring | Nordschleife | 3 | 63 °F | Sunset |
| ✩ 29 | 02:52 AM | Homestead-Miami Speedway | Road Circuit | 14 | 79 °F | Late Morning |

No GT3 round repeats any earlier one, so the rotation is at least 25 long and still
incomplete. Round 14 is Sunset Peninsula again but as *Full Circuit Reverse* against round 1's
*Full Circuit*, and round 21 is Nürburgring *Full Circuit* against round 6's *GP Circuit* —
second venue visits, not wraps.

**GT3 almost certainly loops at 31, and the counting now closes.** All twenty-five observed
rounds map to twenty-five *distinct* positions of the 31-round 2026-09-12 GT3 rotation. Six
rounds of this cycle remain unobserved — 10, 11, 12, 18, 19 and 30 — and exactly **six**
positions of the old list remain unused:

| Unused 09-12 pos | Track / layout |
| --- | --- |
| 2 | Sebring International Raceway, Full Circuit |
| 3 | Grand Oak Raceway, National Circuit |
| 4 | Daytona Intl Speedway, Sports Car Circuit |
| 5 | Lime Rock Park, Full Circuit Alt |
| 13 | Mount Panorama Circuit, Bathurst Circuit |
| 17 | Hockenheimring, Full Circuit |

Six unknown rounds, six unclaimed slots, and a perfect bijection on the twenty-five that are
known. GT3's reshuffle re-permuted the same 31-track pool rather than building a new list, so
the rotation is 31 long — which also means **round 30 (03:08 AM) must be one of those six
tracks**, and nothing else.

The direct test is still the wrap: round **31, at 03:24 AM**, should be WeatherTech Raceway
Laguna Seca, Full Circuit, 14 laps, 68 °F, Late Morning — identical to round 0. Round 32 at
03:40 AM would then be Sunset Peninsula, Full Circuit, 13 laps, 51 °F, Midnight. Both fall
before the 06:00 reshuffle.

`rotationComplete` stays **`false`** until that wrap is actually seen. The pool argument is
counting, not observation, and this file has already recorded two loops that counted well and
were wrong.

### Forza Proto-H Series — every 20 min, anchor 07:15 PM

| # | Race start | Track | Layout | Laps | Weather | Time of day |
| --- | --- | --- | --- | --- | --- | --- |
| 0 | 07:15 PM | Maple Valley | Full Circuit | 22 | 77 °F | Late Morning |
| 1 | 07:35 PM | Circuit de Barcelona-Catalunya | Grand Prix Circuit | 18 | 65 °F | Late Morning |
| 2 | 07:55 PM | Circuit de Spa-Francorchamps | Full Circuit | 14 | 68 °F | Late Afternoon |
| 3 | 08:15 PM | Lime Rock Park | Full Circuit | 38 | 75 °F | Late Morning |
| 4 | 08:35 PM | Nürburgring | Full Circuit | 4 | 61 °F | Late Morning |
| ✎ 5 | 08:55 PM | Nürburgring | Nordschleife | 5 | 66 °F | Late Afternoon |
| ✎ 6 | 09:15 PM | Michelin Raceway Road Atlanta | Grand Prix Course | 25 | 72 °F | Afternoon |
| ✎ 7 | 09:35 PM | Hakone | Grand Prix Circuit | 27 | 71 °F | Late Morning |
| ✎ 8 | 09:55 PM | WeatherTech Raceway Laguna Seca | Full Circuit | 23 | 68 °F | Late Morning |
| — | 10:15 PM | *round 9 not captured* | | | | |
| ✦ 10 | 10:35 PM | Virginia International Raceway | Full | 18 | 68 °F | Night |
| ✦ 11 | 10:55 PM | Mugello Circuit | Full Circuit | 18 | 83 °F | Afternoon |
| ✦ 12 | 11:15 PM | Sebring International Raceway | Full Circuit | 16 | 60 °F | Night |
| ✦ 13 | 11:35 PM | Brands Hatch | Grand Prix Circuit | 22 | 43 °F | Sunrise |
| ✦ 14 | 11:55 PM | Mount Panorama Circuit | Bathurst Circuit | 16 | 41 °F | Sunrise |
| ✧ 15 | 12:15 AM | Road America | Full Circuit | 15 | 59 °F | Sunrise |
| ✧ 16 | 12:35 AM | Homestead-Miami Speedway | Road Circuit | 24 | 79 °F | Late Morning |
| ✧ 17 | 12:55 AM | Sunset Peninsula Raceway | Full Circuit Reverse | 22 | 52 °F | Night |
| ✧ 18 | 01:15 AM | Watkins Glen International Speedway | Full Circuit | 18 | 74 °F | Late Morning |
| ✧ 19 | 01:35 AM | Le Mans - Circuit International de la Sarthe | Full Circuit | 8 | 48 °F | Morning |
| ✩ 20 | 01:55 AM | Silverstone Racing Circuit | Grand Prix Circuit | 16 | 52 °F | Morning |
| ✩ 21 | 02:15 AM | Indianapolis Motor Speedway | Grand Prix Circuit | 23 | 72 °F | Afternoon |
| ✩ 22 | 02:35 AM | Hockenheimring | Full Circuit | 19 | 65 °F | Night |
| ✩ 23 | 02:55 AM | Kyalami Grand Prix Circuit | Grand Prix Circuit | 19 | 56 °F | Midnight |

Rounds 10, 11 and 13 were predicted in advance from the 2026-09-12 rotation and all three
landed; rounds 15–19 then crossed the wrap and matched positions 0–4 of that list; rounds
20–23 were written into this file as predictions and all four landed too. Round 19 appears in
both the fourth and fifth sets and reads identically. The rotation is 31 long and complete —
see the track-order section above.

### IndyCar Series — every 16 min, anchor 07:13 PM

An all-oval block; nothing like the road-course rounds recorded for IndyCar on 2026-09-12.

| # | Race start | Track | Layout | Laps | Weather | Time of day |
| --- | --- | --- | --- | --- | --- | --- |
| 0 | 07:13 PM | Indianapolis Motor Speedway | The Brickyard Speedway | 27 | 54 °F | Morning |
| 1 | 07:29 PM | Sunset Peninsula Raceway | Speedway | 29 | 71 °F | Afternoon |
| 2 | 07:45 PM | Homestead-Miami Speedway | Speedway Circuit | 45 | 79 °F | Late Morning |
| 3 | 08:01 PM | Daytona Intl Speedway | Tri-Oval Circuit | 29 | 51 °F | Sunrise |
| 4 | 08:17 PM | Eaglerock Speedway | Oval Circuit | 50 | 68 °F | Late Morning |
| ✎ 5 | 08:33 PM | Indianapolis Motor Speedway | The Brickyard Speedway | 27 | 54 °F | Morning |
| ✎ 6 | 08:49 PM | Sunset Peninsula Raceway | Speedway | 29 | 64 °F | Late Morning |
| ✎ 7 | 09:05 PM | Homestead-Miami Speedway | Speedway Circuit | 45 | 79 °F | Late Morning |
| ✎ 8 | 09:21 PM | Daytona Intl Speedway | Tri-Oval Circuit | 29 | 51 °F | Sunrise |
| — | 09:37–10:25 PM | *rounds 9–12 not captured* | | | | |
| ✦ 13 | 10:41 PM | Daytona Intl Speedway | Tri-Oval Circuit | 29 | 51 °F | Sunrise |
| ✦ 14 | 10:57 PM | Eaglerock Speedway | Oval Circuit | 50 | 68 °F | Late Morning |
| ✦ 15 | 11:13 PM | Indianapolis Motor Speedway | The Brickyard Speedway | 27 | 54 °F | Morning |
| ✦ 16 | 11:29 PM | Sunset Peninsula Raceway | Speedway | 29 | 64 °F | Late Morning |
| ✦ 17 | 11:45 PM | Homestead-Miami Speedway | Speedway Circuit | 45 | 79 °F | Late Morning |
| — | 12:01 AM | *round 18 not captured* | | | | |
| ✧ 19 | 12:17 AM | Eaglerock Speedway | Oval Circuit | 50 | 68 °F | Late Morning |
| ✧ 20 | 12:33 AM | Indianapolis Motor Speedway | The Brickyard Speedway | 27 | 54 °F | Morning |
| ✧ 21 | 12:49 AM | Sunset Peninsula Raceway | Speedway | 29 | **71 °F** | **Afternoon** |
| ✧ 22 | 01:05 AM | Homestead-Miami Speedway | Speedway Circuit | 45 | 79 °F | Late Morning |
| ✧ 23 | 01:21 AM | Daytona Intl Speedway | Tri-Oval Circuit | 29 | 51 °F | Sunrise |
| ✩ 24 | 01:37 AM | Eaglerock Speedway | Oval Circuit | 50 | 68 °F | Late Morning |
| ✩ 25 | 01:53 AM | Indianapolis Motor Speedway | The Brickyard Speedway | 27 | 54 °F | Morning |
| ✩ 26 | 02:09 AM | Sunset Peninsula Raceway | Speedway | 29 | **64 °F** | **Late Morning** |
| ✩ 27 | 02:25 AM | Homestead-Miami Speedway | Speedway Circuit | 45 | 79 °F | Late Morning |
| ✩ 28 | 02:41 AM | Daytona Intl Speedway | Tri-Oval Circuit | 29 | 51 °F | Sunrise |

Round 21 is the one that broke the 5: 71 °F / Afternoon, matching round 1 and *not* round 6.
Round 26 then closed it — 64 °F / Late Morning, five rounds later, matching round 6 and not
round 1. Both Sunset Peninsula variants inside one capture window, in the order a 10-round loop
requires. The stored rotation is ten slots with `rotationComplete = true` and round 1 keeps its
original 71 °F / Afternoon. See the loop-length section for the full retraction.

The previous cycle's full tables for all four series are in git history, at commit `6477155`
and in the 2026-09-12 revision of this file. They are not reusable for GT3, Proto-H or
IndyCar: two changed category, and IndyCar's was recorded against a loop length that turned
out to be wrong.

## Confidence in the derived constants

Entry-window arithmetic, re-confirmed on this cycle for all four series:

- GT3, 19:02:52, the 07:08 PM race read "Entry ends in 00:02:07" → closes 07:04:59.
- GT3, 19:02:52, the 07:24 PM race read "Entry starts in 00:01:07" → opens 07:03:59.
- Proto-H, 19:03:06, the 07:15 PM race read "Entry ends in 00:08:53" → closes 07:11:59.
- Proto-H, 19:03:06, the 07:35 PM race read "Entry starts in 00:06:53" → opens 07:09:59.
- Touring Car, 19:03:55, the 07:19 PM race read "Entry ends in 00:12:04" → closes 07:15:59.
- Touring Car, 19:03:55, the 07:39 PM race read "Entry starts in 00:10:04" → opens 07:13:59.
- IndyCar, 19:04:06, the 07:13 PM race read "Entry ends in 00:05:53" → closes 07:09:59.
- IndyCar, 19:04:06, the 07:29 PM race read "Entry starts in 00:04:53" → opens 07:08:59.

Each lands one to two seconds past the round minute, which is the countdown's own rounding.

Re-confirmed again on the 20:12 set, on a completely different pair of rounds per series:

- GT3, 20:12:30, 08:28 PM read "Entry ends in 00:12:29" → closes 08:24:59.
- GT3, 20:12:30, 08:44 PM read "Entry starts in 00:11:29" → opens 08:23:59.
- Proto-H, 20:12:43, 08:35 PM read "Entry ends in 00:19:16" → closes 08:31:59.
- Proto-H, 20:12:43, 08:55 PM read "Entry starts in 00:17:16" → opens 08:29:59.
- IndyCar, 20:12:56, 08:17 PM read "Entry ends in 00:01:03" → closes 08:13:59.
- IndyCar, 20:12:56, 08:33 PM read "Entry starts in 00:00:03" → opens 08:12:59.
- Touring Car, 20:13:08, 08:19 PM read "Entry ends in 00:02:51" → closes 08:15:59.
- Touring Car, 20:13:08, 08:39 PM read "Entry starts in 00:00:51" → opens 08:13:59.

And a third time on the 22:30 set:

- GT3, 22:30:48, 10:36 PM read "Entry ends in 00:02:11" → closes 10:32:59.
- GT3, 22:30:48, 10:52 PM read "Entry starts in 00:01:11" → opens 10:31:59.
- Proto-H, 22:30:59, 10:55 PM read "Entry ends in 00:21:01" → closes 10:51:59.
- Proto-H, 22:30:59, 11:15 PM read "Entry starts in 00:19:01" → opens 10:49:59.
- Touring Car, 22:31:08, 10:39 PM read "Entry ends in 00:04:52" → closes 10:35:59.
- Touring Car, 22:31:08, 10:59 PM read "Entry starts in 00:02:52" → opens 10:33:59.
- IndyCar, 22:31:20, 10:41 PM read "Entry ends in 00:06:40" → closes 10:37:59.
- IndyCar, 22:31:20, 10:57 PM read "Entry starts in 00:05:40" → opens 10:36:59.

And a fourth time on the 00:10 set, which also carries the constants across midnight intact:

- GT3, 00:09:43, 12:28 AM read "Entry ends in 00:15:16" → closes 12:24:59.
- GT3, 00:09:43, 12:44 AM read "Entry starts in 00:14:16" → opens 12:23:59.
- Touring Car, 00:09:55, 12:19 AM read "Entry ends in 00:06:04" → closes 12:15:59.
- Touring Car, 00:09:55, 12:39 AM read "Entry starts in 00:04:04" → opens 12:13:59.
- Proto-H, 00:10:05, 12:35 AM read "Entry ends in 00:21:54" → closes 12:31:59.
- Proto-H, 00:10:05, 12:55 AM read "Entry starts in 00:19:54" → opens 12:29:59.
- IndyCar, 00:10:20, 12:17 AM read "Entry ends in 00:03:39" → closes 12:13:59.
- IndyCar, 00:10:20, 12:33 AM read "Entry starts in 00:02:39" → opens 12:12:59.

And a fifth time on the 01:30 set:

- GT3, 01:29:07, 01:48 AM read "Entry ends in 00:15:51" → closes 01:44:58.
- GT3, 01:29:07, 02:04 AM read "Entry starts in 00:14:51" → opens 01:43:58.
- Proto-H, 01:29:45, 01:35 AM read "Entry ends in 00:02:14" → closes 01:31:59.
- Proto-H, 01:29:45, 01:55 AM read "Entry starts in 00:00:14" → opens 01:29:59.
- Touring Car, 01:29:59, 01:39 AM read "Entry ends in 00:06:01" → closes 01:36:00.
- Touring Car, 01:29:59, 01:59 AM read "Entry starts in 00:04:01" → opens 01:34:00.
- IndyCar, 01:30:08, 01:37 AM read "Entry ends in 00:03:51" → closes 01:33:59.
- IndyCar, 01:30:08, 01:53 AM read "Entry starts in 00:02:51" → opens 01:32:59.

Forty independent checks, no drift: 3 min to close, 20 or 25 min to open by category. Each
lands within a second either side of the round minute, which is the countdown's own rounding
and not a real offset.
Midnight is not a boundary for anything — cadence, phase and rotation all run straight
through it. The only boundary is 06:00.

Cadences and anchors remain solid: across three capture sets on 2026-09-12 and this one,
every round the app had already predicted matched the game exactly. The one thing that has
ever been wrong is a rotation *length*.

A race whose entry has closed but which hasn't started yet shows as **EXPIRED** in game;
the app shows it as `ENTRY CLOSED` and still counts down to lights out.

## Still open

- **GT3's rotation length — the only unknown rotation left, and one capture from settled.**
  Twenty-five observed rounds occupy twenty-five distinct positions of the 31-round 2026-09-12
  GT3 list, and the six rounds still unseen match the six positions still unclaimed exactly.
  Capture **03:24 AM** (round 31): a 31-round loop makes it Laguna Seca, Full Circuit, 14 laps,
  68 °F, Late Morning, the same as round 0. Round 30 at **03:08 AM** is worth catching in the
  same pass — it has to be one of Sebring Full, Grand Oak National, Daytona Sports Car, Lime
  Rock Full Circuit Alt, Mount Panorama Bathurst or Hockenheimring Full. Both are before 06:00.
- **Proto-H rounds 24–30 are all recordable before the reshuffle** — they run 03:15 AM to
  05:15 AM, and the 09-12 list says which tracks to expect, so capturing them fills the list
  and re-tests the mapping seven more times. Round 31 at 05:35 AM is the wrap back to round 0
  (Maple Valley, 22 laps, 77 °F, Late Morning) and is the last check this cycle.
- **The remaining gaps cannot be filled this cycle.** Proto-H round 9 next runs at 08:35 AM and
  GT3 rounds 10–12 at 06:04–06:36 AM, all past the 06:00 reshuffle. They stay `null` until a
  future cycle happens to line up.
- **Why some rotations survive the reshuffle and others don't.** Proto-H and Touring Car
  kept their order across 2026-09-13; GT3 and IndyCar did not. Category change does not
  explain the split — one survivor changed category and one non-survivor didn't. GT3 looks
  like a re-permutation of the same pool, IndyCar like a wholesale replacement, so "did not
  survive" may itself have two different modes.
- **No basis yet for predicting the next day's categories**, beyond two long and two medium.
- **Whether a series' rotation *length* survives even when its order doesn't.** Proto-H kept
  31 across the boundary, and GT3's pool count says it kept 31 too while scrambling the order.
  If the 03:24 AM wrap confirms that, length looks like the durable property and order the
  volatile one — leaving IndyCar, at 10 rounds drawn from a pool it did not previously use, as
  the single case of a series being rebuilt outright rather than re-permuted.

## How to record more

Open **Featured Multiplayer → FEATURED**, put the ✓ on a series, and capture the event
cards plus the `LOCAL TIME` readout top right. Scroll right for later rounds. What's needed
per card: track, layout, laps, weather, time of day, "Race starts at".

After a 06:00 reshuffle, check each series' category first, then **verify one round against
the game for every series, including the ones whose category held** — IndyCar kept its
category on 2026-09-13 and still had its entire rotation replaced. A series that verifies
keeps its anchor, cadence and rotation as-is. A series that doesn't needs a fresh anchor,
cadence and rotation from scratch; if its category also moved, the timing constants follow
from the category table above. Bump `recordedCycleStart` to that cycle's 06:00 and the stale
banner clears.

Data lives in one file: `app/src/main/java/com/pixel/forzatimer/data/ScheduleData.kt`.
Nothing else needs touching to add a series.

## Building

```
gradlew :app:assembleDebug
```

`java.time` is used throughout with core library desugaring enabled, so it works down to
the project's `minSdk 24`.
