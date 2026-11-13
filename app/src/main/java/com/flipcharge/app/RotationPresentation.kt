package com.flipcharge.app

enum class TileMode {
    FORCE_180,
    CHARGING,
    AUTO_180,
    WAITING,
    OFF,
}

/**
 * Converts the current state into the tile display mode.
 */
fun FlipState.tileMode(): TileMode = when (source) {
    ActivationSource.MANUAL -> TileMode.FORCE_180
    ActivationSource.CHARGING -> TileMode.CHARGING
    ActivationSource.PERMANENT_AUTO -> TileMode.AUTO_180
    ActivationSource.NONE -> if (autoChargeEnabled) TileMode.WAITING else TileMode.OFF
}
