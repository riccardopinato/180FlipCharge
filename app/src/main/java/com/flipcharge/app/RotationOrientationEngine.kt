package com.flipcharge.app

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.SensorManager
import android.view.OrientationEventListener

class RotationOrientationEngine(
    context: Context,
    private val onStableOrientationChanged: (Int) -> Unit
) {

    private var enabled = false
    private var lastAppliedOrientation: Int? = null
    private var candidateOrientation: Int? = null
    private var candidateSince = 0L

    private val listener = object : OrientationEventListener(
        context.applicationContext,
        SensorManager.SENSOR_DELAY_NORMAL
    ) {
        override fun onOrientationChanged(orientation: Int) {
            if (!enabled || orientation == ORIENTATION_UNKNOWN) return

            val mapped = mapDegreesToRequestedOrientation(orientation)
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

    fun start() {
        if (enabled) return
        enabled = true
        candidateOrientation = null
        candidateSince = 0L

        if (listener.canDetectOrientation()) {
            listener.enable()
        }
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

    private fun mapDegreesToRequestedOrientation(degrees: Int): Int {
        return when {
            degrees >= 330 || degrees < 30 ->
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            degrees in 60..120 ->
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE

            degrees in 150..210 ->
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT

            degrees in 240..300 ->
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            else ->
                lastAppliedOrientation ?: nearestOrientation(degrees)
        }
    }

    private fun nearestOrientation(degrees: Int): Int {
        return when {
            degrees < 45 || degrees >= 315 ->
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            degrees < 135 ->
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE

            degrees < 225 ->
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT

            else ->
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    companion object {
        private const val STABLE_TIME_MS = 380L
    }
}
