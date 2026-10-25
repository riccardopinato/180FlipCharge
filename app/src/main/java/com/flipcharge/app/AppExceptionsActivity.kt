package com.flipcharge.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import com.flipcharge.app.ui.theme.FlipChargeTheme

class AppExceptionsActivity : LocalizedActivity() {

    private val apps = mutableStateOf<List<InstalledAppInfo>>(emptyList())
    private val excluded = mutableStateOf<Set<String>>(emptySet())
    private val usageGranted = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        reload()

        setContent {
            FlipChargeTheme {
                AppExceptionsScreen(
                    apps = apps.value,
                    excludedApps = excluded.value,
                    usageAccessGranted = usageGranted.value,
                    onBack = { finish() },
                    onOpenUsageAccess = {
                        UsageAccessHelper.openUsageAccessSettings(this)
                    },
                    onToggleApp = { packageName, enabled ->
                        val prefs = AppPreferences(this)
                        prefs.setAppExcluded(packageName, enabled)
                        excluded.value = prefs.getExcludedApps()
                        RotationController.reconcileService(this)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        reload()
    }

    private fun reload() {
        apps.value = InstalledAppsRepository.loadLaunchableApps(this)
        excluded.value = AppPreferences(this).getExcludedApps()
        usageGranted.value = UsageAccessHelper.hasUsageAccessPermission(this)
    }
}
