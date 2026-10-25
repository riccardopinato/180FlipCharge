package com.flipcharge.app

import android.content.Context
import android.content.SharedPreferences

enum class ActivationSource {
    NONE,
    MANUAL,
    CHARGING,
    PERMANENT_AUTO
}

enum class ActiveRotationMode {
    OFF,
    FORCE_REVERSE_PORTRAIT,
    PERMANENT_AUTO
}

data class FlipState(
    val manualEnabled: Boolean = false,
    val permanentAutoEnabled: Boolean = false,
    val autoChargeEnabled: Boolean = false,
    val chargingNow: Boolean = false,
    val chargingSuppressed: Boolean = false
) {
    val chargingContribution: Boolean
        get() = autoChargeEnabled && chargingNow && !chargingSuppressed

    val effectiveMode: ActiveRotationMode
        get() = when {
            manualEnabled -> ActiveRotationMode.FORCE_REVERSE_PORTRAIT
            chargingContribution -> ActiveRotationMode.FORCE_REVERSE_PORTRAIT
            permanentAutoEnabled -> ActiveRotationMode.PERMANENT_AUTO
            else -> ActiveRotationMode.OFF
        }

    val active: Boolean
        get() = effectiveMode != ActiveRotationMode.OFF

    val source: ActivationSource
        get() = when {
            manualEnabled -> ActivationSource.MANUAL
            chargingContribution -> ActivationSource.CHARGING
            permanentAutoEnabled -> ActivationSource.PERMANENT_AUTO
            else -> ActivationSource.NONE
        }

    val serviceNeeded: Boolean
        get() = manualEnabled || permanentAutoEnabled || autoChargeEnabled
}

class AppPreferences(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(
        FILE_NAME,
        Context.MODE_PRIVATE
    )

    fun readState(): FlipState = FlipState(
        manualEnabled = prefs.getBoolean(KEY_MANUAL, false),
        permanentAutoEnabled = prefs.getBoolean(KEY_PERMANENT_AUTO, false),
        autoChargeEnabled = prefs.getBoolean(KEY_AUTO_CHARGE, false),
        chargingNow = prefs.getBoolean(KEY_CHARGING_NOW, false),
        chargingSuppressed = prefs.getBoolean(KEY_CHARGING_SUPPRESSED, false)
    )

    fun setManualEnabled(value: Boolean) =
        prefs.edit().putBoolean(KEY_MANUAL, value).apply()

    fun setPermanentAutoEnabled(value: Boolean) =
        prefs.edit().putBoolean(KEY_PERMANENT_AUTO, value).apply()

    fun setAutoChargeEnabled(value: Boolean) =
        prefs.edit().putBoolean(KEY_AUTO_CHARGE, value).apply()

    fun setChargingSuppressed(value: Boolean) =
        prefs.edit().putBoolean(KEY_CHARGING_SUPPRESSED, value).apply()

    fun updateChargingState(charging: Boolean) {
        val editor = prefs.edit().putBoolean(KEY_CHARGING_NOW, charging)
        if (!charging) editor.putBoolean(KEY_CHARGING_SUPPRESSED, false)
        editor.apply()
    }

    fun getExcludedApps(): Set<String> =
        prefs.getStringSet(KEY_EXCLUDED_APPS, emptySet())?.toSet() ?: emptySet()

    fun setAppExcluded(packageName: String, excluded: Boolean) {
        val updated = getExcludedApps().toMutableSet()
        if (excluded) updated.add(packageName) else updated.remove(packageName)
        prefs.edit().putStringSet(KEY_EXCLUDED_APPS, updated).apply()
    }

    fun addListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) =
        prefs.registerOnSharedPreferenceChangeListener(listener)

    fun removeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) =
        prefs.unregisterOnSharedPreferenceChangeListener(listener)

    companion object {
        private const val FILE_NAME = "flipcharge_prefs"
        private const val KEY_MANUAL = "manual_enabled"
        private const val KEY_PERMANENT_AUTO = "permanent_auto_enabled"
        private const val KEY_AUTO_CHARGE = "auto_charge_enabled"
        private const val KEY_CHARGING_NOW = "charging_now"
        private const val KEY_CHARGING_SUPPRESSED = "charging_suppressed"
        private const val KEY_EXCLUDED_APPS = "excluded_apps"
    }
}
