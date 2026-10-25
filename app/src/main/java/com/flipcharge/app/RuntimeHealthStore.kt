package com.flipcharge.app

import android.content.Context
import android.content.SharedPreferences

enum class RuntimeHealth {
    IDLE,
    STARTING,
    WAITING_CHARGER,
    ACTIVE_MANUAL,
    ACTIVE_CHARGING,
    ACTIVE_AUTO,
    SUSPENDED_APP_EXCEPTION,
    SUSPENDED_SCREEN_OFF,
    MISSING_OVERLAY_PERMISSION,
    OVERLAY_ERROR,
    SENSOR_UNAVAILABLE,
    SERVICE_START_BLOCKED,
    SERVICE_STOPPED;

    val isOperational: Boolean
        get() = this == ACTIVE_MANUAL ||
            this == ACTIVE_CHARGING ||
            this == ACTIVE_AUTO ||
            this == WAITING_CHARGER ||
            this == SUSPENDED_APP_EXCEPTION ||
            this == SUSPENDED_SCREEN_OFF
}

class RuntimeHealthStore(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(
        FILE_NAME,
        Context.MODE_PRIVATE,
    )

    fun read(): RuntimeHealth {
        val raw = prefs.getString(KEY_STATUS, null) ?: return RuntimeHealth.IDLE
        return runCatching { RuntimeHealth.valueOf(raw) }
            .getOrDefault(RuntimeHealth.IDLE)
    }

    fun write(status: RuntimeHealth) {
        if (read() == status) return
        prefs.edit().putString(KEY_STATUS, status.name).apply()
    }

    fun addListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun removeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

    companion object {
        private const val FILE_NAME = "flipcharge_runtime_health"
        private const val KEY_STATUS = "status"
    }
}
