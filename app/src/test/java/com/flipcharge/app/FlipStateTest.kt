package com.flipcharge.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FlipStateTest {

    @Test
    fun defaultState_isInactiveAndDoesNotNeedService() {
        val state = FlipState()

        assertFalse(state.active)
        assertFalse(state.serviceNeeded)
        assertEquals(ActivationSource.NONE, state.source)
    }

    @Test
    fun manualMode_hasPriorityAndNeedsService() {
        val state = FlipState(
            manualEnabled = true,
            autoChargeEnabled = true,
            chargingNow = true
        )

        assertTrue(state.active)
        assertTrue(state.serviceNeeded)
        assertEquals(ActivationSource.MANUAL, state.source)
    }

    @Test
    fun chargingAutomation_activatesWhileCharging() {
        val state = FlipState(
            manualEnabled = false,
            autoChargeEnabled = true,
            chargingNow = true,
            chargingSuppressed = false
        )

        assertTrue(state.active)
        assertTrue(state.chargingContribution)
        assertEquals(ActivationSource.CHARGING, state.source)
    }

    @Test
    fun chargingSuppression_disablesChargingContributionButKeepsListenerService() {
        val state = FlipState(
            manualEnabled = false,
            autoChargeEnabled = true,
            chargingNow = true,
            chargingSuppressed = true
        )

        assertFalse(state.active)
        assertFalse(state.chargingContribution)
        assertTrue(state.serviceNeeded)
        assertEquals(ActivationSource.NONE, state.source)
    }

    @Test
    fun autoChargeWaiting_needsServiceWithoutBeingActive() {
        val state = FlipState(
            autoChargeEnabled = true,
            chargingNow = false
        )

        assertFalse(state.active)
        assertTrue(state.serviceNeeded)
        assertEquals(ActivationSource.NONE, state.source)
    }
}
