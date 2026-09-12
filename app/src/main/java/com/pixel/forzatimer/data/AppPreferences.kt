package com.pixel.forzatimer.data

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {

    const val PREFS_NAME = "forza_timer_prefs"
    const val KEY_THEME_MODE = "theme_mode"
    const val KEY_PALETTE_INDEX = "palette_index"

    fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun themeMode(context: Context): ThemeMode =
        ThemeMode.fromName(prefs(context).getString(KEY_THEME_MODE, null))

    fun setThemeMode(context: Context, mode: ThemeMode) {
        prefs(context).edit().putString(KEY_THEME_MODE, mode.name).apply()
    }

    fun paletteIndex(context: Context): Int = prefs(context).getInt(KEY_PALETTE_INDEX, 0)

    fun setPaletteIndex(context: Context, index: Int) {
        prefs(context).edit().putInt(KEY_PALETTE_INDEX, index).apply()
    }
}
