package com.flipcharge.app

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

object ChargingStateDetector {

    fun isCharging(context: Context): Boolean {
        val batteryIntent = context.applicationContext.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        ) ?: return false

        val status = batteryIntent.getIntExtra(
            BatteryManager.EXTRA_STATUS,
            BatteryManager.BATTERY_STATUS_UNKNOWN
        )

        val plugged = batteryIntent.getIntExtra(
            BatteryManager.EXTRA_PLUGGED,
            0
        )

        return status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL ||
            plugged != 0
    }

    fun syncIntoPreferences(context: Context): FlipState {
        val prefs = AppPreferences(context)
        prefs.updateChargingState(isCharging(context))
        return prefs.readState()
    }
}
