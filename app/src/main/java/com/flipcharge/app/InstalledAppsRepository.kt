package com.flipcharge.app

import android.content.Context
import android.content.Intent
import android.os.Build
import android.content.pm.PackageManager

data class InstalledAppInfo(
    val packageName: String,
    val label: String
)

object InstalledAppsRepository {

    fun loadLaunchableApps(context: Context): List<InstalledAppInfo> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val results = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(0)
            )
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(intent, 0)
        }

        return results
            .mapNotNull { resolveInfo ->
                val activityInfo = resolveInfo.activityInfo ?: return@mapNotNull null
                val pkg = activityInfo.packageName
                if (pkg == context.packageName) return@mapNotNull null

                InstalledAppInfo(
                    packageName = pkg,
                    label = resolveInfo.loadLabel(pm).toString().trim().ifEmpty { pkg }
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
    }
}
