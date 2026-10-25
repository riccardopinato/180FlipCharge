package com.flipcharge.app

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RuntimeHealthTest {

    @Test
    fun activeAndSuspendedStates_areOperationalTruth() {
        assertTrue(RuntimeHealth.ACTIVE_MANUAL.isOperational)
        assertTrue(RuntimeHealth.ACTIVE_CHARGING.isOperational)
        assertTrue(RuntimeHealth.ACTIVE_AUTO.isOperational)
        assertTrue(RuntimeHealth.WAITING_CHARGER.isOperational)
        assertTrue(RuntimeHealth.SUSPENDED_APP_EXCEPTION.isOperational)
        assertTrue(RuntimeHealth.SUSPENDED_SCREEN_OFF.isOperational)
    }

    @Test
    fun failureAndIdleStates_areNotOperational() {
        assertFalse(RuntimeHealth.IDLE.isOperational)
        assertFalse(RuntimeHealth.STARTING.isOperational)
        assertFalse(RuntimeHealth.MISSING_OVERLAY_PERMISSION.isOperational)
        assertFalse(RuntimeHealth.OVERLAY_ERROR.isOperational)
        assertFalse(RuntimeHealth.SENSOR_UNAVAILABLE.isOperational)
        assertFalse(RuntimeHealth.SERVICE_START_BLOCKED.isOperational)
        assertFalse(RuntimeHealth.SERVICE_STOPPED.isOperational)
    }
}
