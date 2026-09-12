package com.pixel.forzatimer

import com.pixel.forzatimer.ui.theme.Palettes
import org.junit.Assert.assertEquals
import org.junit.Test

class PaletteOrderTest {

    private val frozenNames = listOf(
        "Purple", "Teal", "Rose", "Ocean", "Amber", "Coral", "Mint", "Crimson"
    )

    @Test
    fun paletteNamesAndOrderAreFrozen() {
        assertEquals(frozenNames, Palettes.map { it.name })
    }
}
