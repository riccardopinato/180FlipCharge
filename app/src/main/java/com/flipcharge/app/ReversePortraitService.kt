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
    private lateinit var healthStore: RuntimeHealthStore
    private lateinit var windowManager: WindowManager
    private lateinit var orientationEngine: RotationOrientationEngine

    private val foregroundTracker = ForegroundAppTracker()

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
                handler.postDelayed(this, APP_MONITOR_INTERVAL_MS)
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
                    stopAppMonitor()
                    suppressedByExcludedApp = false
                    healthStore.write(RuntimeHealth.SUSPENDED_SCREEN_OFF)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        prefs = AppPreferences(this)
        healthStore = RuntimeHealthStore(this)
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        orientationEngine = RotationOrientationEngine(this) { orientation ->
            if (
                prefs.readState().effectiveMode == ActiveRotationMode.PERMANENT_AUTO &&
                !suppressedByExcludedApp
            ) {
                if (!showOrUpdateOverlay(orientation)) {
                    healthStore.write(RuntimeHealth.OVERLAY_ERROR)
                }
            }
        }

        healthStore.write(RuntimeHealth.STARTING)
        createNotificationChannel()
        startForegroundInternal()
        prefs.addListener(preferenceListener)

        registerScreenReceiver()
        ChargingStateDetector.syncIntoPreferences(this)
        applyCurrentState()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == RotationController.ACTION_STOP_ALL) {
            RotationController.stopAll(this)
            stopSelf()
            return START_NOT_STICKY
        }

        createNotificationChannel()
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
            foregroundTracker.reset()
            removeOverlay()
            healthStore.write(RuntimeHealth.IDLE)
            RotationController.notifyStateChanged(this)
            stopSelf()
            return
        }

        if (state.active && !Settings.canDrawOverlays(this)) {
            orientationEngine.stop()
            stopAppMonitor()
            suppressedByExcludedApp = false
            removeOverlay()
            healthStore.write(RuntimeHealth.MISSING_OVERLAY_PERMISSION)
            updateNotification(state)
            RotationController.notifyStateChanged(this)
            return
        }

        updateAppMonitorState(state)

        if (suppressedByExcludedApp) {
            orientationEngine.stop()
            removeOverlay()
            healthStore.write(RuntimeHealth.SUSPENDED_APP_EXCEPTION)
            updateNotification(state)
            RotationController.notifyStateChanged(this)
            return
        }

        when (state.effectiveMode) {
            ActiveRotationMode.OFF -> {
                orientationEngine.stop()
                removeOverlay()
                healthStore.write(
                    if (state.autoChargeEnabled) {
                        RuntimeHealth.WAITING_CHARGER
                    } else {
                        RuntimeHealth.IDLE
                    },
                )
            }

            ActiveRotationMode.FORCE_REVERSE_PORTRAIT -> {
                orientationEngine.stop()

                val attached = showOrUpdateOverlay(
                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT,
                )

                healthStore.write(
                    if (!attached) {
                        RuntimeHealth.OVERLAY_ERROR
                    } else if (state.source == ActivationSource.CHARGING) {
                        RuntimeHealth.ACTIVE_CHARGING
                    } else {
                        RuntimeHealth.ACTIVE_MANUAL
                    },
                )
            }

            ActiveRotationMode.PERMANENT_AUTO -> {
                if (!screenOn) {
                    orientationEngine.stop()
                    healthStore.write(RuntimeHealth.SUSPENDED_SCREEN_OFF)
                } else {
                    val attached = showOrUpdateOverlay(
                        orientationEngine.lastOrientationOrDefault(),
                    )

                    if (!attached) {
                        orientationEngine.stop()
                        healthStore.write(RuntimeHealth.OVERLAY_ERROR)
                    } else if (!orientationEngine.start()) {
                        healthStore.write(RuntimeHealth.SENSOR_UNAVAILABLE)
                    } else {
                        healthStore.write(RuntimeHealth.ACTIVE_AUTO)
                    }
                }
            }
        }

        updateNotification(state)
        RotationController.notifyStateChanged(this)
    }

    private fun showOrUpdateOverlay(requestedOrientation: Int): Boolean {
        if (!Settings.canDrawOverlays(this)) {
            removeOverlay()
            return false
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
                PixelFormat.TRANSLUCENT,
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                x = 0
                y = 0
                screenOrientation = requestedOrientation
            }

            return try {
                windowManager.addView(view, params)
                overlayView = view
                overlayParams = params
                true
            } catch (_: Exception) {
                overlayView = null
                overlayParams = null
                false
            }
        }

        val params = overlayParams ?: return false

        if (params.screenOrientation != requestedOrientation) {
            params.screenOrientation = requestedOrientation
            return try {
                windowManager.updateViewLayout(overlayView, params)
                true
            } catch (_: Exception) {
                false
            }
        }

        return true
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
                    Context.RECEIVER_NOT_EXPORTED,
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
                Context.RECEIVER_NOT_EXPORTED,
            )
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(screenReceiver, filter)
        }

        screenReceiverRegistered = true
    }

    private fun updateAppMonitorState(state: FlipState) {
        val shouldMonitor =
            screenOn &&
                state.active &&
                prefs.getExcludedApps().isNotEmpty() &&
                UsageAccessHelper.hasUsageAccessPermission(this)

        if (shouldMonitor && !appMonitorRunning) {
            appMonitorRunning = true
            handler.post(monitorRunnable)
        } else if (!shouldMonitor) {
            stopAppMonitor()
            suppressedByExcludedApp = false
            foregroundTracker.reset()
        }
    }

    private fun checkForegroundApp() {
        val foreground = foregroundTracker.resolve(this)
        val excluded =
            foreground != null &&
                prefs.getExcludedApps().contains(foreground)

        if (excluded != suppressedByExcludedApp) {
            suppressedByExcludedApp = excluded

            if (excluded) {
                orientationEngine.stop()
                removeOverlay()
                healthStore.write(RuntimeHealth.SUSPENDED_APP_EXCEPTION)
                updateNotification(prefs.readState())
                RotationController.notifyStateChanged(this)
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
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            )
        } else {
            @Suppress("DEPRECATION")
            startForeground(NOTIFICATION_ID, notification)
        }

        foregroundStarted = true
    }

    private fun updateNotification(state: FlipState) {
        getSystemService(NotificationManager::class.java)
            .notify(NOTIFICATION_ID, buildNotification(state))
    }

    private fun buildNotification(state: FlipState): Notification {
        val openPending = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val stopPending = PendingIntent.getService(
            this,
            1,
            Intent(this, ReversePortraitService::class.java).apply {
                action = RotationController.ACTION_STOP_ALL
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val text = when (state.source) {
            ActivationSource.MANUAL ->
                AppStrings.get(this, R.string.notification_manual_active)

            ActivationSource.CHARGING ->
                AppStrings.get(this, R.string.notification_charging_active)

            ActivationSource.PERMANENT_AUTO ->
                AppStrings.get(this, R.string.notification_permanent_auto)

            ActivationSource.NONE ->
                AppStrings.get(this, R.string.notification_waiting_charge)
        }

        return Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_rotate_180)
            .setContentTitle(AppStrings.get(this, R.string.app_name))
            .setContentText(text)
            .setContentIntent(openPending)
            .addAction(
                Notification.Action.Builder(
                    null,
                    AppStrings.get(this, R.string.notification_stop),
                    stopPending,
                ).build(),
            )
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                AppStrings.get(this, R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW,
            )

            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        prefs.removeListener(preferenceListener)
        orientationEngine.stop()
        stopAppMonitor()
        foregroundTracker.reset()

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

        if (prefs.readState().serviceNeeded) {
            healthStore.write(RuntimeHealth.SERVICE_STOPPED)
        } else {
            healthStore.write(RuntimeHealth.IDLE)
        }

        RotationController.notifyStateChanged(this)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHANNEL_ID = "flipcharge_rotation_service"
        private const val NOTIFICATION_ID = 180
        private const val APP_MONITOR_INTERVAL_MS = 2_000L
    }
}
