# Design system

ForzaTimer follows `UI_REDESIGN_PLAYBOOK.md`. The token layer and component library are
ported from the reference implementation in `D:/StudioProjects/Roulette` rather than
re-derived, so the two apps share one design language exactly.

## What was ported

| Layer | Files |
| --- | --- |
| Tokens | `ui/theme/` — `Color.kt`, `Dimens.kt`, `Shape.kt`, `Type.kt`, `ColorUtils.kt`, `Theme.kt` |
| Components | `presentation/component/design/` — `AppBars.kt`, `Buttons.kt`, `Cards.kt`, `GroupedList.kt` |
| Icons | `presentation/component/AppIcons.kt` + 13 local vectors in `res/drawable/ic_*.xml` |
| Font | HarmonyOS Sans, three static faces (regular / medium / bold), 436 KB total |

Symbols were renamed from the reference's `Roulette*` prefix to the playbook's canonical
`App*` names: `AppColors`, `AppShapes`, `AppTheme`, `AccentPalette`, `lightAppColors`,
`darkAppColors`. The reference's `@Deprecated` migration aliases were dropped — ForzaTimer
had no old colour names to migrate, so they would have been dead on arrival.

## What was deliberately left out

- **`Charts.kt`** — the app has no chart. Porting it would ship ~320 lines of unused
  canvas code. Add it back the moment a chart is needed; do not invent a different one.
- **`Inputs.kt`** — no text entry anywhere in the app.
- **`BottomBar.kt`** — two screens, reached by a top-bar action, so there is no bottom nav.
  Consequently `dimens.bottomBarSpace` is *not* added to list bottom padding; that rule
  only applies when a floating bar is present.
- **`NotificationBellButton`** — removed from the ported `AppBars.kt`; the app has no
  notifications, and it was the only thing pulling `AppIcons.Notifications` into the icon
  set.

## Screens

Both screens are compositions of library components only — no bespoke cards or rows.

- `presentation/screen/ScheduleScreen.kt` — `AppScaffold` + `AppTopBar`, a pair of
  `StatCard`s for the live clock and reshuffle countdown, `AppFilterChip` row for series
  selection, then `SettingsGroup` / `SettingsRow` / `InsetDivider` for the running order.
  The out-of-date warning is a `SettingsRow` tinted with `colors.warning`; the entry state
  is a `Pill`. An unrecorded series renders `EmptyState`.
- `presentation/screen/SettingsScreen.kt` — the playbook's canonical settings pattern:
  theme mode and accent palette as check-marked rows.

## Series colours vs. the accent

The four series use fixed **categorical tile colours** (`TileBlue`, `TileAmber`,
`TileTeal`, `TileCoral`), independent of the user's accent, so the series stay
distinguishable whichever accent is active — this is what §2.1 of the playbook calls for.

They are passed through `rememberAccentOnSurface()` before being used as text or icon
colour. That matters: `TileAmber` on a white surface is about 2:1 contrast and would be
unreadable as label text in light mode. The helper lifts it to ≥ 4.5:1 in light mode and
desaturates/brightens it in dark mode.

## Theme mode

`ThemeMode` (System / Light / Dark) persists in `SharedPreferences` via `AppPreferences`.
`MainActivity.attachBaseContext` overrides the configuration before the first frame, so a
forced mode has no light-flash on cold start and `values-night/` resources agree with
Compose. `values/themes.xml` and `values-night/themes.xml` carry transparent system bars
and a matching `window_background`.

The palette index is persisted, so `PaletteOrderTest` freezes the list order — reordering
`Palettes` would silently change the stored theme.

## Release build

R8 and resource shrinking are on for release (AGP 9 spells this `optimization { enable }`
rather than `isMinifyEnabled`). Rules are in `app/proguard-rules.pro`; the playbook's
kotlinx.serialization keeps are omitted because the app does not use it.

Release APK is **1.2 MB** against 12.1 MB for debug.

## Verified on device

Installed and driven on a Pixel 10a emulator (API level of the current AVD), not on the
physical device that was also attached over Wi-Fi.

- Schedule screen in **light** and **dark** — series colours stay legible in both, which is
  the contrast helper doing its job: raw `TileAmber` on white is ~2:1 and would have been
  unreadable as GT3's countdown text.
- Series detail screen — hero countdown, entry pills, and the laps/weather/entry rows.
- Settings — theme mode and all eight accents.
- Accent switched to Crimson: the accent re-tinted the chrome while the four series kept
  their own colours, and `palette_index` persisted to `shared_prefs`.
- No entries in `logcat -s AndroidRuntime:E`.

## Checklist status

Automated checks all pass — no raw `.dp` or `Color(0x` in screen code, no
`MaterialTheme.colorScheme` outside the token layer, no `material.icons` dependency, no
raw `Scaffold`, `EmptyState` present, `attachBaseContext` override in place.
