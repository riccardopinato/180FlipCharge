package com.flipcharge.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RotationModeTest {

    @Test
    fun defaultState_isOff() {
        val state = FlipState()

        assertEquals(ActiveRotationMode.OFF, state.effectiveMode)
        assertFalse(state.serviceNeeded)
    }

    @Test
    fun permanentAuto_needsService() {
        val state = FlipState(
            permanentAutoEnabled = true
        )

        assertEquals(
            ActiveRotationMode.PERMANENT_AUTO,
            state.effectiveMode
        )
        assertTrue(state.serviceNeeded)
        assertEquals(
            ActivationSource.PERMANENT_AUTO,
            state.source
        )
    }

    @Test
    fun charging_hasPriorityOverPermanentAuto() {
        val state = FlipState(
            permanentAutoEnabled = true,
            autoChargeEnabled = true,
            chargingNow = true
        )

        assertEquals(
            ActiveRotationMode.FORCE_REVERSE_PORTRAIT,
            state.effectiveMode
        )
        assertEquals(
            ActivationSource.CHARGING,
            state.source
        )
    }

    @Test
    fun manualForce_hasPriorityOverEverything() {
        val state = FlipState(
            manualEnabled = true,
            permanentAutoEnabled = true,
            autoChargeEnabled = true,
            chargingNow = true
        )

        assertEquals(
            ActiveRotationMode.FORCE_REVERSE_PORTRAIT,
            state.effectiveMode
        )
        assertEquals(
            ActivationSource.MANUAL,
            state.source
        )
    }

    @Test
    fun suppressedCharging_fallsBackToPermanentAuto() {
        val state = FlipState(
            permanentAutoEnabled = true,
            autoChargeEnabled = true,
            chargingNow = true,
            chargingSuppressed = true
        )

        assertEquals(
            ActiveRotationMode.PERMANENT_AUTO,
            state.effectiveMode
        )
        assertEquals(
            ActivationSource.PERMANENT_AUTO,
            state.source
        )
    }

    @Test
    fun autoChargeWaiting_keepsServiceAlive() {
        val state = FlipState(
            autoChargeEnabled = true,
            chargingNow = false
        )

        assertEquals(ActiveRotationMode.OFF, state.effectiveMode)
        assertTrue(state.serviceNeeded)
    }
}
