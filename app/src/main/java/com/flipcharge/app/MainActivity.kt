package com.flipcharge.app

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import com.flipcharge.app.ui.theme.FlipChargeTheme

class MainActivity : ComponentActivity() {

    private val uiState = mutableStateOf(FlipState())
    private val overlayGranted = mutableStateOf(false)
    private val notificationsGranted = mutableStateOf(true)
    private val usageAccessGranted = mutableStateOf(false)
    private val excludedAppsCount = mutableIntStateOf(0)

    private val prefListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            runOnUiThread { refreshState() }
        }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            refreshState()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        refreshState()

        setContent {
            FlipChargeTheme {
                MainScreen(
                    state = uiState.value,
                    overlayPermissionGranted = overlayGranted.value,
                    notificationPermissionGranted = notificationsGranted.value,
                    usageAccessGranted = usageAccessGranted.value,
                    excludedAppsCount = excludedAppsCount.intValue,
                    showNotificationPermission =
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU,

                    onRequestOverlayPermission = ::openOverlayPermission,
                    onRequestNotificationPermission = ::requestNotificationPermission,

                    onMainToggle = {
                        if (overlayGranted.value) {
                            RotationController.toggleManualForce(this)
                            refreshState()
                        } else {
                            openOverlayPermission()
                        }
                    },

                    onPermanentAutoChange = { enabled ->
                        if (overlayGranted.value || !enabled) {
                            RotationController.setPermanentAuto(this, enabled)
                            refreshState()
                        } else {
                            openOverlayPermission()
                        }
                    },

                    onAutoChargeChange = { enabled ->
                        if (overlayGranted.value || !enabled) {
                            RotationController.setAutoCharge(this, enabled)
                            refreshState()
                        } else {
                            openOverlayPermission()
                        }
                    },

                    onManageAppExceptions = {
                        startActivity(
                            Intent(this, AppExceptionsActivity::class.java)
                        )
                    },

                    onOpenBatterySettings = ::openBatterySettings
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        AppPreferences(this).addListener(prefListener)
    }

    override fun onStop() {
        AppPreferences(this).removeListener(prefListener)
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        refreshState()

        if (overlayGranted.value) {
            RotationController.reconcileService(this)
        }
    }

    private fun refreshState() {
        val prefs = AppPreferences(this)

        uiState.value = prefs.readState()
        excludedAppsCount.intValue = prefs.getExcludedApps().size
        overlayGranted.value = Settings.canDrawOverlays(this)
        usageAccessGranted.value =
            UsageAccessHelper.hasUsageAccessPermission(this)

        notificationsGranted.value =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
    }

    private fun openOverlayPermission() {
        startActivity(
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
        )
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    private fun openBatterySettings() {
        try {
            startActivity(
                Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            )
        } catch (_: Exception) {
            startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:$packageName")
                )
            )
        }
    }
}
