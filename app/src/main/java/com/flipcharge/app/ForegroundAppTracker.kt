package com.flipcharge.app

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context

class ForegroundAppTracker {

    private var lastKnownPackage: String? = null
    private var lastQueryEndMillis: Long = 0L

    fun reset() {
        lastKnownPackage = null
        lastQueryEndMillis = 0L
    }

    fun resolve(context: Context): String? {
        if (!UsageAccessHelper.hasUsageAccessPermission(context)) {
            reset()
            return null
        }

        val manager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val end = System.currentTimeMillis()
        val start = if (lastQueryEndMillis > 0L) {
            (lastQueryEndMillis - QUERY_OVERLAP_MS).coerceAtLeast(0L)
        } else {
            (end - INITIAL_EVENT_LOOKBACK_MS).coerceAtLeast(0L)
        }

        val events = manager.queryEvents(start, end)
        val event = UsageEvents.Event()
        var latestResumeAt = Long.MIN_VALUE

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (
                event.eventType == UsageEvents.Event.ACTIVITY_RESUMED ||
                event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND
            ) {
                if (event.timeStamp >= latestResumeAt) {
                    latestResumeAt = event.timeStamp
                    lastKnownPackage = event.packageName
                }
            }
        }

        lastQueryEndMillis = end

        if (lastKnownPackage == null) {
            lastKnownPackage = manager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                (end - FALLBACK_USAGE_LOOKBACK_MS).coerceAtLeast(0L),
                end,
            )
                .asSequence()
                .filter { it.lastTimeUsed > 0L }
                .maxByOrNull { it.lastTimeUsed }
                ?.packageName
        }

        return lastKnownPackage
    }

    companion object {
        private const val QUERY_OVERLAP_MS = 2_500L
        private const val INITIAL_EVENT_LOOKBACK_MS = 60L * 60L * 1000L
        private const val FALLBACK_USAGE_LOOKBACK_MS = 24L * 60L * 60L * 1000L
    }
}
