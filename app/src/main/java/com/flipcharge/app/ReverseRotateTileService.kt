package com.flipcharge.app

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class ReverseRotateTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        super.onClick()

        if (!Settings.canDrawOverlays(this)) {
            openMainActivity()
            return
        }

        RotationController.toggleManualForce(this)
        updateTile()
    }

    private fun updateTile() {
        val tile = qsTile ?: return
        val state = RotationController.currentState(this)

        tile.state = if (state.manualEnabled) {
            Tile.STATE_ACTIVE
        } else {
            Tile.STATE_INACTIVE
        }

        tile.label = getString(R.string.tile_label)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = when {
                state.manualEnabled ->
                    getString(R.string.tile_force_180)

                state.permanentAutoEnabled ->
                    getString(R.string.tile_auto_180)

                state.chargingContribution ->
                    getString(R.string.tile_charging)

                state.autoChargeEnabled ->
                    getString(R.string.tile_waiting)

                else ->
                    getString(R.string.tile_off)
            }
        }

        tile.updateTile()
    }

    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val pendingIntent = PendingIntent.getActivity(
                this,
                180,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            startActivityAndCollapse(pendingIntent)
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }
}
