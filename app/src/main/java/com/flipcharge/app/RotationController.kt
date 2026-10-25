package com.flipcharge.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.service.quicksettings.TileService
import androidx.core.content.ContextCompat

object RotationController {

    const val ACTION_REFRESH = "com.flipcharge.app.action.REFRESH"

    fun hasOverlayPermission(context: Context): Boolean =
        Settings.canDrawOverlays(context)

    fun currentState(context: Context): FlipState =
        AppPreferences(context).readState()

    fun toggleManualForce(context: Context) {
        val prefs = AppPreferences(context)
        val state = prefs.readState()

        prefs.setManualEnabled(!state.manualEnabled)

        if (!state.manualEnabled) {
            prefs.setChargingSuppressed(false)
        }

        reconcileService(context)
        notifyStateChanged(context)
    }

    fun setPermanentAuto(context: Context, enabled: Boolean) {
        AppPreferences(context).setPermanentAutoEnabled(enabled)
        reconcileService(context)
        notifyStateChanged(context)
    }

    fun setAutoCharge(context: Context, enabled: Boolean) {
        val prefs = AppPreferences(context)
        prefs.setAutoChargeEnabled(enabled)

        if (!enabled) {
            prefs.setChargingSuppressed(false)
        } else {
            ChargingStateDetector.syncIntoPreferences(context)
        }

        reconcileService(context)
        notifyStateChanged(context)
    }

    fun suppressChargingUntilUnplugged(context: Context) {
        val prefs = AppPreferences(context)
        val state = prefs.readState()

        if (state.autoChargeEnabled && state.chargingNow) {
            prefs.setChargingSuppressed(true)
        }

        reconcileService(context)
        notifyStateChanged(context)
    }

    fun restoreAfterBoot(context: Context) {
        val prefs = AppPreferences(context)
        val state = prefs.readState()

        if (!state.serviceNeeded) {
            notifyStateChanged(context)
            return
        }

        ChargingStateDetector.syncIntoPreferences(context)
        reconcileService(context)
        notifyStateChanged(context)
    }

    fun reconcileService(context: Context) {
        val appContext = context.applicationContext
        val state = AppPreferences(appContext).readState()

        if (!state.serviceNeeded) {
            appContext.stopService(
                Intent(appContext, ReversePortraitService::class.java)
            )
            notifyStateChanged(appContext)
            return
        }

        val intent = Intent(appContext, ReversePortraitService::class.java).apply {
            action = ACTION_REFRESH
        }

        try {
            ContextCompat.startForegroundService(appContext, intent)
        } catch (_: Exception) {
            // Lo stato rimane persistito e verrà riconciliato alla prossima
            // interazione consentita dal sistema.
        }
    }

    fun notifyStateChanged(context: Context) {
        try {
            TileService.requestListeningState(
                context.applicationContext,
                ComponentName(
                    context.applicationContext,
                    ReverseRotateTileService::class.java
                )
            )
        } catch (_: Exception) {
        }
    }
}
