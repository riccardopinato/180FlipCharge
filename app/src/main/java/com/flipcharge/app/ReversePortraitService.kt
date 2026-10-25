package com.flipcharge.app

import android.app.*
import android.content.*
import android.content.pm.ActivityInfo
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.os.*
import android.provider.Settings
import android.view.*

class ReversePortraitService : Service() {

    private lateinit var prefs: AppPreferences
    private lateinit var windowManager: WindowManager
    private lateinit var orientationEngine: RotationOrientationEngine

    private var overlayView: View? = null
    private var overlayParams: WindowManager.LayoutParams? = null

    private var powerReceiverRegistered = false
    private var screenReceiverRegistered = false
    private var foregroundStarted = false
    private var appMonitorRunning = false
    private var suppressedByExcludedApp = false
    private var screenOn = true

    private val handler = Handler(Looper.getMainLooper())

    private val monitorRunnable = object : Runnable {
        override fun run() {
            checkForegroundApp()
            if (appMonitorRunning) {
                handler.postDelayed(this, 1200L)
            }
        }
    }

    private val preferenceListener =
        android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            applyCurrentState()
        }

    private val powerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_POWER_CONNECTED -> {
                    prefs.updateChargingState(true)
                    applyCurrentState()
                }

                Intent.ACTION_POWER_DISCONNECTED -> {
                    prefs.updateChargingState(false)
                    applyCurrentState()
                }
            }
        }
    }

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_ON -> {
                    screenOn = true
                    applyCurrentState()
                }

                Intent.ACTION_SCREEN_OFF -> {
                    screenOn = false
                    orientationEngine.stop()
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        prefs = AppPreferences(this)
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        orientationEngine = RotationOrientationEngine(this) { orientation ->
            if (
                prefs.readState().effectiveMode == ActiveRotationMode.PERMANENT_AUTO &&
                !suppressedByExcludedApp
            ) {
                showOrUpdateOverlay(orientation)
            }
        }

        createNotificationChannel()
        startForegroundInternal()
        prefs.addListener(preferenceListener)

        registerScreenReceiver()
        ChargingStateDetector.syncIntoPreferences(this)
        applyCurrentState()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ChargingStateDetector.syncIntoPreferences(this)
        applyCurrentState()

        return if (prefs.readState().serviceNeeded) {
            START_STICKY
        } else {
            START_NOT_STICKY
        }
    }

    private fun applyCurrentState() {
        registerPowerReceiverIfNeeded()

        val state = prefs.readState()

        if (!state.serviceNeeded) {
            orientationEngine.stop()
            stopAppMonitor()
            suppressedByExcludedApp = false
            removeOverlay()
            RotationController.notifyStateChanged(this)
            stopSelf()
            return
        }

        updateAppMonitorState(state)

        when (state.effectiveMode) {
            ActiveRotationMode.OFF -> {
                orientationEngine.stop()
                removeOverlay()
            }

            ActiveRotationMode.FORCE_REVERSE_PORTRAIT -> {
                orientationEngine.stop()

                if (!suppressedByExcludedApp && Settings.canDrawOverlays(this)) {
                    showOrUpdateOverlay(
                        ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                    )
                } else {
                    removeOverlay()
                }
            }

            ActiveRotationMode.PERMANENT_AUTO -> {
                if (
                    screenOn &&
                    !suppressedByExcludedApp &&
                    Settings.canDrawOverlays(this)
                ) {
                    showOrUpdateOverlay(
                        orientationEngine.lastOrientationOrDefault()
                    )
                    orientationEngine.start()
                } else {
                    orientationEngine.stop()
                    if (suppressedByExcludedApp) {
                        removeOverlay()
                    }
                }
            }
        }

        updateNotification(state)
        RotationController.notifyStateChanged(this)
    }

    private fun showOrUpdateOverlay(requestedOrientation: Int) {
        if (!Settings.canDrawOverlays(this)) {
            removeOverlay()
            return
        }

        if (overlayView == null) {
            val view = View(this).apply {
                alpha = 0.01f
                isClickable = false
                isFocusable = false
            }

            val params = WindowManager.LayoutParams(
                1,
                1,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                x = 0
                y = 0
                screenOrientation = requestedOrientation
            }

            try {
                windowManager.addView(view, params)
                overlayView = view
                overlayParams = params
            } catch (_: Exception) {
                overlayView = null
                overlayParams = null
            }
            return
        }

        val params = overlayParams ?: return

        if (params.screenOrientation != requestedOrientation) {
            params.screenOrientation = requestedOrientation
            try {
                windowManager.updateViewLayout(overlayView, params)
            } catch (_: Exception) {
            }
        }
    }

    private fun removeOverlay() {
        overlayView?.let {
            try {
                windowManager.removeView(it)
            } catch (_: Exception) {
            }
        }

        overlayView = null
        overlayParams = null
    }

    private fun registerPowerReceiverIfNeeded() {
        val needed = prefs.readState().autoChargeEnabled

        if (needed && !powerReceiverRegistered) {
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_POWER_CONNECTED)
                addAction(Intent.ACTION_POWER_DISCONNECTED)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(
                    powerReceiver,
                    filter,
                    Context.RECEIVER_NOT_EXPORTED
                )
            } else {
                @Suppress("DEPRECATION")
                registerReceiver(powerReceiver, filter)
            }

            powerReceiverRegistered = true
        } else if (!needed && powerReceiverRegistered) {
            try {
                unregisterReceiver(powerReceiver)
            } catch (_: Exception) {
            }
            powerReceiverRegistered = false
        }
    }

    private fun registerScreenReceiver() {
        if (screenReceiverRegistered) return

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                screenReceiver,
                filter,
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(screenReceiver, filter)
        }

        screenReceiverRegistered = true
    }

    private fun updateAppMonitorState(state: FlipState) {
        val shouldMonitor =
            state.active &&
                prefs.getExcludedApps().isNotEmpty() &&
                UsageAccessHelper.hasUsageAccessPermission(this)

        if (shouldMonitor && !appMonitorRunning) {
            appMonitorRunning = true
            handler.post(monitorRunnable)
        } else if (!shouldMonitor && appMonitorRunning) {
            stopAppMonitor()
            suppressedByExcludedApp = false
        }
    }

    private fun checkForegroundApp() {
        val foreground = UsageAccessHelper.getForegroundPackageName(this)
        val excluded =
            foreground != null &&
                prefs.getExcludedApps().contains(foreground)

        if (excluded != suppressedByExcludedApp) {
            suppressedByExcludedApp = excluded

            if (excluded) {
                orientationEngine.stop()
                removeOverlay()
            } else {
                applyCurrentState()
            }
        }
    }

    private fun stopAppMonitor() {
        appMonitorRunning = false
        handler.removeCallbacks(monitorRunnable)
    }

    private fun startForegroundInternal() {
        if (foregroundStarted) return

        val notification = buildNotification(prefs.readState())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                180,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            @Suppress("DEPRECATION")
            startForeground(180, notification)
        }

        foregroundStarted = true
    }

    private fun updateNotification(state: FlipState) {
        getSystemService(NotificationManager::class.java)
            .notify(180, buildNotification(state))
    }

    private fun buildNotification(state: FlipState): Notification {
        val pending = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val text = when (state.source) {
            ActivationSource.MANUAL ->
                getString(R.string.notification_manual_active)

            ActivationSource.CHARGING ->
                getString(R.string.notification_charging_active)

            ActivationSource.PERMANENT_AUTO ->
                getString(R.string.notification_permanent_auto)

            ActivationSource.NONE ->
                getString(R.string.notification_waiting_charge)
        }

        return Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_rotate_180)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(text)
            .setContentIntent(pending)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )

            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        prefs.removeListener(preferenceListener)
        orientationEngine.stop()
        stopAppMonitor()

        if (powerReceiverRegistered) {
            try {
                unregisterReceiver(powerReceiver)
            } catch (_: Exception) {
            }
        }

        if (screenReceiverRegistered) {
            try {
                unregisterReceiver(screenReceiver)
            } catch (_: Exception) {
            }
        }

        removeOverlay()
        RotationController.notifyStateChanged(this)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHANNEL_ID = "flipcharge_rotation_service"
    }
}
