package com.pixel.forzatimer

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.pixel.forzatimer.data.AppPreferences
import com.pixel.forzatimer.data.ThemeMode
import com.pixel.forzatimer.presentation.screen.ScheduleScreen
import com.pixel.forzatimer.presentation.screen.SettingsScreen
import com.pixel.forzatimer.ui.theme.AppTheme

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val mode = ThemeMode.fromName(
            newBase.getSharedPreferences(AppPreferences.PREFS_NAME, Context.MODE_PRIVATE)
                .getString(AppPreferences.KEY_THEME_MODE, null)
        )
        val context = if (mode == ThemeMode.SYSTEM) {
            newBase
        } else {
            val config = Configuration(newBase.resources.configuration)
            config.uiMode = (config.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or
                if (mode == ThemeMode.DARK) {
                    Configuration.UI_MODE_NIGHT_YES
                } else {
                    Configuration.UI_MODE_NIGHT_NO
                }
            newBase.createConfigurationContext(config)
        }
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ForzaTimerApp() }
    }
}

@Composable
private fun ForzaTimerApp() {
    val context = LocalContext.current
    val activity = context as ComponentActivity

    var themeMode by remember { mutableStateOf(AppPreferences.themeMode(context)) }
    var paletteIndex by remember { mutableIntStateOf(AppPreferences.paletteIndex(context)) }
    var showSettings by rememberSaveable { mutableStateOf(false) }

    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    LaunchedEffect(darkTheme) {
        activity.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme },
            navigationBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ) { darkTheme }
        )
    }

    AppTheme(darkTheme = darkTheme, paletteIndex = paletteIndex) {
        if (showSettings) {
            SettingsScreen(
                themeMode = themeMode,
                paletteIndex = paletteIndex,
                onThemeModeChange = {
                    themeMode = it
                    AppPreferences.setThemeMode(context, it)
                },
                onPaletteChange = {
                    paletteIndex = it
                    AppPreferences.setPaletteIndex(context, it)
                },
                onNavigateBack = { showSettings = false }
            )
        } else {
            ScheduleScreen(onOpenSettings = { showSettings = true })
        }
    }
}
