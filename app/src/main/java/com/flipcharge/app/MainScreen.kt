package com.flipcharge.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipcharge.app.ui.theme.Manrope
import com.flipcharge.app.ui.theme.SpaceGrotesk

/** Android-native production screen faithful to approved V2 web phone preview. */
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
    val tone = when {
        !overlayPermissionGranted -> NeoRuntimeTone.PERMISSION
        runtimeHealth == RuntimeHealth.MISSING_OVERLAY_PERMISSION -> NeoRuntimeTone.PERMISSION
        runtimeHealth == RuntimeHealth.OVERLAY_ERROR ||
            runtimeHealth == RuntimeHealth.SENSOR_UNAVAILABLE ||
            runtimeHealth == RuntimeHealth.SERVICE_START_BLOCKED ||
            runtimeHealth == RuntimeHealth.SERVICE_STOPPED -> NeoRuntimeTone.ERROR
        runtimeHealth == RuntimeHealth.STARTING -> NeoRuntimeTone.STARTING
        runtimeHealth == RuntimeHealth.WAITING_CHARGER -> NeoRuntimeTone.WAITING
        runtimeHealth == RuntimeHealth.SUSPENDED_APP_EXCEPTION ||
            runtimeHealth == RuntimeHealth.SUSPENDED_SCREEN_OFF -> NeoRuntimeTone.PAUSED
        state.active && runtimeHealth.isOperational -> NeoRuntimeTone.ACTIVE
        else -> NeoRuntimeTone.INACTIVE
    }

    val modeLabel = when (tone) {
        NeoRuntimeTone.PERMISSION -> stringResource(R.string.neo_mode_permission)
        NeoRuntimeTone.ERROR -> stringResource(R.string.neo_mode_error)
        NeoRuntimeTone.STARTING -> stringResource(R.string.neo_mode_starting)
        NeoRuntimeTone.PAUSED -> stringResource(R.string.neo_mode_paused)
        NeoRuntimeTone.WAITING -> stringResource(R.string.neo_mode_waiting)
        NeoRuntimeTone.INACTIVE -> stringResource(R.string.neo_mode_standby)
        NeoRuntimeTone.ACTIVE -> when (state.source) {
            ActivationSource.MANUAL -> stringResource(R.string.neo_mode_manual)
            ActivationSource.CHARGING -> stringResource(R.string.neo_mode_charging)
            ActivationSource.PERMANENT_AUTO -> stringResource(R.string.neo_mode_auto)
            ActivationSource.NONE -> stringResource(R.string.neo_mode_standby)
        }
    }
    val stateLabel = when (tone) {
        NeoRuntimeTone.ACTIVE -> stringResource(R.string.neo_state_active)
        NeoRuntimeTone.INACTIVE -> stringResource(R.string.neo_state_inactive)
        NeoRuntimeTone.WAITING -> stringResource(R.string.neo_state_waiting)
        NeoRuntimeTone.ERROR -> stringResource(R.string.neo_state_error)
        NeoRuntimeTone.PERMISSION -> stringResource(R.string.neo_state_permission)
        NeoRuntimeTone.STARTING -> stringResource(R.string.neo_state_starting)
        NeoRuntimeTone.PAUSED -> stringResource(R.string.neo_state_paused)
    }
    val heroValue = when (state.effectiveMode) {
        ActiveRotationMode.FORCE_REVERSE_PORTRAIT -> "180°"
        ActiveRotationMode.PERMANENT_AUTO -> "AUTO+"
        ActiveRotationMode.OFF -> "0°"
    }

    Scaffold(containerColor = NeoPalette.Background) { insets ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF101A1F), NeoPalette.Background, NeoPalette.Background),
                    ),
                )
                .padding(insets),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 17.dp,
                bottom = 26.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.neo_header_eyebrow),
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            fontSize = 10.sp,
                            lineHeight = 14.sp,
                            color = Color(0xFF75C9BD),
                        )
                        Text(
                            text = stringResource(R.string.app_name),
                            fontFamily = SpaceGrotesk,
                            fontSize = 25.sp,
                            lineHeight = 27.sp,
                            letterSpacing = (-1).sp,
                            fontWeight = FontWeight.Bold,
                            color = NeoPalette.Ink,
                        )
                    }
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.neoSmallPanel(),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            tint = Color(0xFFBBCFD0),
                            contentDescription = stringResource(R.string.open_settings),
                        )
                    }
                }
            }

            item {
                NeoHeroCard(
                    value = heroValue,
                    title = stringResource(R.string.neo_hero_heading),
                    detail = runtimeHealthLabel(runtimeHealth, state),
                    modeLabel = modeLabel,
                    stateLabel = stateLabel,
                    engineLabel = stringResource(R.string.neo_state_engine),
                    modeEyebrow = stringResource(R.string.neo_mode_title),
                    dialDescription = stringResource(R.string.neo_accessibility_dial),
                    tone = tone,
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
                    NeoSectionHeader(
                        title = stringResource(R.string.neo_section_manual),
                        index = stringResource(R.string.neo_section_manual_index),
                    )
                    NeoManualControl(
                        eyebrow = stringResource(R.string.neo_manual_overline),
                        title = stringResource(R.string.neo_manual_title),
                        subtitle = stringResource(R.string.neo_manual_description),
                        onState = stringResource(R.string.neo_manual_active),
                        offState = stringResource(R.string.neo_manual_inactive),
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
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    NeoSectionHeader(
                        title = stringResource(R.string.neo_section_automation),
                        index = stringResource(R.string.neo_section_automation_index),
                    )
                    NeoModeRow(
                        title = stringResource(R.string.permanent_180_title),
                        subtitle = stringResource(R.string.neo_auto_description),
                        glyph = NeoModeGlyph.ROTATE,
                        accent = NeoPalette.Teal,
                        checked = state.permanentAutoEnabled,
                        enabled = overlayPermissionGranted,
                        onCheckedChange = onPermanentAutoChange,
                    )
                    NeoModeRow(
                        title = stringResource(R.string.neo_charging_title),
                        subtitle = stringResource(R.string.neo_charging_description),
                        glyph = NeoModeGlyph.CHARGING,
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
                val excludedSubtitle = if (excludedAppsCount > 0) {
                    pluralStringResource(
                        R.plurals.app_exceptions_selected_count,
                        excludedAppsCount,
                        excludedAppsCount,
                    )
                } else {
                    stringResource(R.string.neo_exceptions_description)
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    NeoSectionHeader(
                        title = stringResource(R.string.neo_section_tools),
                        index = stringResource(R.string.neo_section_tools_index),
                    )
                    NeoUtilityRow(
                        title = stringResource(R.string.app_exceptions_title),
                        subtitle = excludedSubtitle,
                        glyph = NeoUtilityGlyph.APPS,
                        attention = excludedAppsCount > 0 && !usageAccessGranted,
                        onClick = onManageAppExceptions,
                    )
                    NeoUtilityRow(
                        title = stringResource(R.string.background_title),
                        subtitle = stringResource(R.string.neo_background_description),
                        glyph = NeoUtilityGlyph.BATTERY,
                        onClick = onOpenBatterySettings,
                    )
                }
            }
            item { Spacer(Modifier.height(3.dp)) }
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
