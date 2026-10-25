package com.flipcharge.app

import org.junit.Assert.assertEquals
import org.junit.Test

class TilePresentationTest {

    @Test
    fun tilePriority_matchesRuntimePriority() {
        assertEquals(
            TileMode.FORCE_180,
            FlipState(
                manualEnabled = true,
                permanentAutoEnabled = true,
                autoChargeEnabled = true,
                chargingNow = true,
            ).tileMode(),
        )

        assertEquals(
            TileMode.CHARGING,
            FlipState(
                permanentAutoEnabled = true,
                autoChargeEnabled = true,
                chargingNow = true,
            ).tileMode(),
        )

        assertEquals(
            TileMode.AUTO_180,
            FlipState(permanentAutoEnabled = true).tileMode(),
        )
    }

    @Test
    fun chargingAutomationWaiting_isDistinctFromOff() {
        assertEquals(
            TileMode.WAITING,
            FlipState(autoChargeEnabled = true).tileMode(),
        )
        assertEquals(TileMode.OFF, FlipState().tileMode())
    }
}
