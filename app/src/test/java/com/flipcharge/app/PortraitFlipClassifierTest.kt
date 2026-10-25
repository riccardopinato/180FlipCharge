package com.flipcharge.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PortraitFlipClassifierTest {

    @Test
    fun uprightZone_mapsOnlyToUpright() {
        assertEquals(PortraitFlip.UPRIGHT, PortraitFlipClassifier.classify(0))
        assertEquals(PortraitFlip.UPRIGHT, PortraitFlipClassifier.classify(30))
        assertEquals(PortraitFlip.UPRIGHT, PortraitFlipClassifier.classify(45))
        assertEquals(PortraitFlip.UPRIGHT, PortraitFlipClassifier.classify(315))
        assertEquals(PortraitFlip.UPRIGHT, PortraitFlipClassifier.classify(359))
    }

    @Test
    fun upsideDownZone_mapsOnlyToReversePortrait() {
        assertEquals(PortraitFlip.UPSIDE_DOWN, PortraitFlipClassifier.classify(135))
        assertEquals(PortraitFlip.UPSIDE_DOWN, PortraitFlipClassifier.classify(180))
        assertEquals(PortraitFlip.UPSIDE_DOWN, PortraitFlipClassifier.classify(225))
    }

    @Test
    fun landscapeAndDiagonalZones_areNeutral() {
        listOf(46, 90, 134, 226, 270, 314).forEach { degrees ->
            assertNull("Expected neutral orientation at $degrees°", PortraitFlipClassifier.classify(degrees))
        }
    }

    @Test
    fun invalidDegrees_areNeutral() {
        assertNull(PortraitFlipClassifier.classify(-1))
        assertNull(PortraitFlipClassifier.classify(360))
    }
}
