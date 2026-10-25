package com.flipcharge.app

enum class PortraitFlip {
    UPRIGHT,
    UPSIDE_DOWN,
}

object PortraitFlipClassifier {

    fun classify(degrees: Int): PortraitFlip? {
        if (degrees !in 0..359) return null

        return when {
            degrees <= UPRIGHT_MAX || degrees >= UPRIGHT_MIN ->
                PortraitFlip.UPRIGHT

            degrees in UPSIDE_DOWN_MIN..UPSIDE_DOWN_MAX ->
                PortraitFlip.UPSIDE_DOWN

            else -> null
        }
    }

    private const val UPRIGHT_MAX = 45
    private const val UPRIGHT_MIN = 315
    private const val UPSIDE_DOWN_MIN = 135
    private const val UPSIDE_DOWN_MAX = 225
}
