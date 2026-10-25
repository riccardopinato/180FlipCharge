package com.flipcharge.app

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.SensorManager
import android.view.OrientationEventListener

class RotationOrientationEngine(
    context: Context,
    private val onStableOrientationChanged: (Int) -> Unit,
) {

    private var enabled = false
    private var lastAppliedOrientation: Int? = null
    private var candidateOrientation: Int? = null
    private var candidateSince = 0L

    private val listener = object : OrientationEventListener(
        context.applicationContext,
        SensorManager.SENSOR_DELAY_NORMAL,
    ) {
        override fun onOrientationChanged(orientation: Int) {
            if (!enabled || orientation == ORIENTATION_UNKNOWN) return

            val mapped = mapDegreesToRequestedOrientation(orientation)

            if (mapped == null) {
                // Sideways / landscape-like positions are intentionally neutral.
                // Auto 180° must never rotate the device to 90° or 270°.
                candidateOrientation = null
                candidateSince = 0L
                return
            }

            val now = android.os.SystemClock.elapsedRealtime()

            if (mapped != candidateOrientation) {
                candidateOrientation = mapped
                candidateSince = now
                return
            }

            if (
                mapped != lastAppliedOrientation &&
                now - candidateSince >= STABLE_TIME_MS
            ) {
                lastAppliedOrientation = mapped
                onStableOrientationChanged(mapped)
            }
        }
    }

    fun start(): Boolean {
        if (enabled) return true
        if (!listener.canDetectOrientation()) return false

        enabled = true
        candidateOrientation = null
        candidateSince = 0L
        listener.enable()
        return true
    }

    fun stop() {
        enabled = false
        listener.disable()
        candidateOrientation = null
        candidateSince = 0L
    }

    fun resetLastApplied() {
        lastAppliedOrientation = null
    }

    fun lastOrientationOrDefault(): Int =
        lastAppliedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    private fun mapDegreesToRequestedOrientation(degrees: Int): Int? =
        when (PortraitFlipClassifier.classify(degrees)) {
            PortraitFlip.UPRIGHT ->
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            PortraitFlip.UPSIDE_DOWN ->
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT

            null -> null
        }

    companion object {
        private const val STABLE_TIME_MS = 380L
    }
}
