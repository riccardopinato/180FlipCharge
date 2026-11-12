package com.flipcharge.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Approved Neo Tactile Dark consumer home, derived from the v2 interactive
 * design preview. Only presentation changed; all callbacks retain their
 * original native Android implementations and data owner.
 */
@Composable
fun MainScreen(
    state: FlipState,
    overlayPermissionGranted: Boolean,
    notificationPermissionGranted: Boolean,
    usageAccessGranted: Boolean,
    excludedAppsCount: Int,
    runtimeHealth: RuntimeHealth,
    showNotificationPermission: Boolean,
    onRequestOverlayPermission: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onMainToggle: () -> Unit,
    onPermanentAutoChange: (Boolean) -> Unit,
    onAutoChargeChange: (Boolean) -> Unit,
    onManageAppExceptions: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Scaffold(containerColor = NeoPalette.Background) { insets ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(NeoPalette.Background)
                .padding(insets),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 20.dp,
                bottom = 34.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(17.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(7.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.home_eyebrow),
                            style = MaterialTheme.typography.labelLarge,
                            color = NeoPalette.Teal,
                        )
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineLarge,
                            color = NeoPalette.Ink,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.neoSmallPanel(),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = stringResource(R.string.open_settings),
                            tint = NeoPalette.Ink,
                        )
                    }
                }
            }

            item {
                val heroValue = when (state.effectiveMode) {
                    ActiveRotationMode.FORCE_REVERSE_PORTRAIT -> "180°"
                    ActiveRotationMode.PERMANENT_AUTO -> "AUTO+"
                    ActiveRotationMode.OFF -> "0°"
                }
                val operational = state.active && runtimeHealth.isOperational
                val isWaiting = runtimeHealth == RuntimeHealth.WAITING_CHARGER
                val hasIssue = runtimeHealth in setOf(
                    RuntimeHealth.MISSING_OVERLAY_PERMISSION,
                    RuntimeHealth.OVERLAY_ERROR,
                    RuntimeHealth.SENSOR_UNAVAILABLE,
                    RuntimeHealth.SERVICE_START_BLOCKED,
                    RuntimeHealth.SERVICE_STOPPED,
                )
                NeoHeroCard(
                    value = heroValue,
                    title = stringResource(R.string.hero_title),
                    detail = runtimeHealthLabel(runtimeHealth, state),
                    operational = operational,
                    waiting = isWaiting,
                    issue = hasIssue,
                )
            }

            item {
                NeoSectionHeader(text = stringResource(R.string.enable_forced_180))
                Spacer(Modifier.height(10.dp))
                NeoManualControl(
                    title = stringResource(R.string.enable_forced_180),
                    detail = stringResource(R.string.status_force_180),
                    checked = state.manualEnabled,
                    permissionGranted = overlayPermissionGranted,
                    onToggle = {
                        if (overlayPermissionGranted) {
                            onMainToggle()
                        } else {
                            onRequestOverlayPermission()
                        }
                    },
                )
            }

            item {
                NeoSectionHeader(text = stringResource(R.string.permanent_180_title))
                Spacer(Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    NeoModeRow(
                        title = stringResource(R.string.permanent_180_title),
                        subtitle = stringResource(R.string.permanent_180_short),
                        glyph = "↻",
                        accent = NeoPalette.Teal,
                        checked = state.permanentAutoEnabled,
                        enabled = overlayPermissionGranted,
                        onCheckedChange = onPermanentAutoChange,
                    )
                    NeoModeRow(
                        title = stringResource(R.string.auto_charge_title),
                        subtitle = stringResource(R.string.auto_charge_short),
                        glyph = "ϟ",
                        accent = NeoPalette.Amber,
                        checked = state.autoChargeEnabled,
                        enabled = overlayPermissionGranted,
                        onCheckedChange = onAutoChargeChange,
                    )
                }
            }

            if (!overlayPermissionGranted) {
                item {
                    NeoPermissionCard(
                        title = stringResource(R.string.overlay_permission_title),
                        body = stringResource(R.string.overlay_permission_description),
                        action = stringResource(R.string.grant_overlay_permission),
                        onClick = onRequestOverlayPermission,
                    )
                }
            }

            if (showNotificationPermission && !notificationPermissionGranted) {
                item {
                    NeoPermissionCard(
                        title = stringResource(R.string.notification_permission_title),
                        body = stringResource(R.string.notification_permission_description),
                        action = stringResource(R.string.grant_notification_permission),
                        onClick = onRequestNotificationPermission,
                    )
                }
            }

            item {
                val subtitle = if (excludedAppsCount == 0) {
                    stringResource(R.string.app_exceptions_description_empty)
                } else {
                    pluralStringResource(
                        R.plurals.app_exceptions_selected_count,
                        excludedAppsCount,
                        excludedAppsCount,
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    NeoUtilityRow(
                        title = stringResource(R.string.app_exceptions_title),
                        subtitle = subtitle,
                        glyph = "▣",
                        onClick = onManageAppExceptions,
                        attention = excludedAppsCount > 0 && !usageAccessGranted,
                    )
                    NeoUtilityRow(
                        title = stringResource(R.string.background_title),
                        subtitle = stringResource(R.string.background_description),
                        glyph = "◎",
                        onClick = onOpenBatterySettings,
                    )
                }
            }

            item { Spacer(Modifier.height(4.dp)) }
        }
    }
}

@Composable
private fun runtimeHealthLabel(
    health: RuntimeHealth,
    state: FlipState,
): String = when (health) {
    RuntimeHealth.MISSING_OVERLAY_PERMISSION -> stringResource(R.string.runtime_overlay_missing)
    RuntimeHealth.OVERLAY_ERROR -> stringResource(R.string.runtime_overlay_error)
    RuntimeHealth.SENSOR_UNAVAILABLE -> stringResource(R.string.runtime_sensor_unavailable)
    RuntimeHealth.SERVICE_START_BLOCKED -> stringResource(R.string.runtime_service_blocked)
    RuntimeHealth.SERVICE_STOPPED -> stringResource(R.string.runtime_service_stopped)
    RuntimeHealth.SUSPENDED_APP_EXCEPTION -> stringResource(R.string.runtime_suspended_exception)
    RuntimeHealth.SUSPENDED_SCREEN_OFF -> stringResource(R.string.runtime_screen_off)
    RuntimeHealth.STARTING -> stringResource(R.string.runtime_starting)
    RuntimeHealth.WAITING_CHARGER -> stringResource(R.string.status_waiting_charge)
    RuntimeHealth.ACTIVE_MANUAL -> stringResource(R.string.status_force_180)
    RuntimeHealth.ACTIVE_CHARGING -> stringResource(R.string.status_charging)
    RuntimeHealth.ACTIVE_AUTO -> stringResource(R.string.status_permanent_auto)
    RuntimeHealth.IDLE -> if (state.autoChargeEnabled) {
        stringResource(R.string.status_waiting_charge)
    } else {
        stringResource(R.string.status_off)
    }
}
