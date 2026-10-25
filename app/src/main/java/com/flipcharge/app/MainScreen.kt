package com.flipcharge.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipcharge.app.ui.theme.FlipBlue
import com.flipcharge.app.ui.theme.FlipMint
import com.flipcharge.app.ui.theme.FlipOrange
import com.flipcharge.app.ui.theme.FlipRose

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
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 12.dp, top = 18.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.home_eyebrow),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineLarge,
                        )
                        Text(
                            text = stringResource(R.string.app_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = stringResource(R.string.open_settings),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                ) {
                    val heroValue = when (state.effectiveMode) {
                        ActiveRotationMode.FORCE_REVERSE_PORTRAIT -> "180°"
                        ActiveRotationMode.PERMANENT_AUTO -> "AUTO+"
                        ActiveRotationMode.OFF -> "0°"
                    }

                    val status = runtimeHealthLabel(runtimeHealth, state)

                    FlipHeroCard(
                        value = heroValue,
                        title = stringResource(R.string.hero_title),
                        detail = status,
                        active = state.active && runtimeHealth.isOperational,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            item {
                PrimaryRotationAction(
                    active = state.manualEnabled,
                    enabled = overlayPermissionGranted,
                    onClick = if (overlayPermissionGranted) onMainToggle else onRequestOverlayPermission,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max)
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    FlipModeTile(
                        title = stringResource(R.string.permanent_180_title),
                        subtitle = stringResource(R.string.permanent_180_short),
                        glyph = "↻",
                        accent = FlipBlue,
                        checked = state.permanentAutoEnabled,
                        enabled = overlayPermissionGranted,
                        onCheckedChange = onPermanentAutoChange,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                    FlipModeTile(
                        title = stringResource(R.string.auto_charge_title),
                        subtitle = stringResource(R.string.auto_charge_short),
                        glyph = "⚡",
                        accent = FlipOrange,
                        checked = state.autoChargeEnabled,
                        enabled = overlayPermissionGranted,
                        onCheckedChange = onAutoChargeChange,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                }
            }

            if (!overlayPermissionGranted) {
                item {
                    PermissionCard(
                        title = stringResource(R.string.overlay_permission_title),
                        body = stringResource(R.string.overlay_permission_description),
                        action = stringResource(R.string.grant_overlay_permission),
                        accent = FlipRose,
                        onClick = onRequestOverlayPermission,
                    )
                }
            }

            if (showNotificationPermission && !notificationPermissionGranted) {
                item {
                    PermissionCard(
                        title = stringResource(R.string.notification_permission_title),
                        body = stringResource(R.string.notification_permission_description),
                        action = stringResource(R.string.grant_notification_permission),
                        accent = FlipOrange,
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

                FlipActionCard(
                    title = stringResource(R.string.app_exceptions_title),
                    subtitle = subtitle,
                    glyph = "▣",
                    accent = FlipRose,
                    onClick = onManageAppExceptions,
                    modifier = Modifier.padding(horizontal = 20.dp),
                    trailing = {
                        if (excludedAppsCount > 0 && !usageAccessGranted) {
                            FlipStatusPill(
                                text = "!",
                                accent = MaterialTheme.colorScheme.error,
                            )
                        }
                    },
                )
            }

            item {
                FlipActionCard(
                    title = stringResource(R.string.background_title),
                    subtitle = stringResource(R.string.background_description),
                    glyph = "◎",
                    accent = FlipMint,
                    onClick = onOpenBatterySettings,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }

            item { Spacer(Modifier.height(2.dp)) }
        }
    }
}

@Composable
private fun PrimaryRotationAction(
    active: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val start = if (active) FlipRose else MaterialTheme.colorScheme.primary
    val end = if (active) FlipOrange else MaterialTheme.colorScheme.secondary

    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(listOf(start, end)),
                shape = RoundedCornerShape(22.dp),
            )
            .clickable(enabled = enabled || !active, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 17.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (active) {
                stringResource(R.string.disable_forced_180)
            } else {
                stringResource(R.string.enable_forced_180)
            },
            color = androidx.compose.ui.graphics.Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PermissionCard(
    title: String,
    body: String,
    action: String,
    accent: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(24.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .flipGradientBorder(shape),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.10f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = accent,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = action,
                modifier = Modifier.clickable(onClick = onClick),
                style = MaterialTheme.typography.labelLarge,
                color = accent,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}


@Composable
private fun runtimeHealthLabel(
    health: RuntimeHealth,
    state: FlipState,
): String = when (health) {
    RuntimeHealth.MISSING_OVERLAY_PERMISSION ->
        stringResource(R.string.runtime_overlay_missing)

    RuntimeHealth.OVERLAY_ERROR ->
        stringResource(R.string.runtime_overlay_error)

    RuntimeHealth.SENSOR_UNAVAILABLE ->
        stringResource(R.string.runtime_sensor_unavailable)

    RuntimeHealth.SERVICE_START_BLOCKED ->
        stringResource(R.string.runtime_service_blocked)

    RuntimeHealth.SERVICE_STOPPED ->
        stringResource(R.string.runtime_service_stopped)

    RuntimeHealth.SUSPENDED_APP_EXCEPTION ->
        stringResource(R.string.runtime_suspended_exception)

    RuntimeHealth.SUSPENDED_SCREEN_OFF ->
        stringResource(R.string.runtime_screen_off)

    RuntimeHealth.STARTING ->
        stringResource(R.string.runtime_starting)

    RuntimeHealth.WAITING_CHARGER ->
        stringResource(R.string.status_waiting_charge)

    RuntimeHealth.ACTIVE_MANUAL ->
        stringResource(R.string.status_force_180)

    RuntimeHealth.ACTIVE_CHARGING ->
        stringResource(R.string.status_charging)

    RuntimeHealth.ACTIVE_AUTO ->
        stringResource(R.string.status_permanent_auto)

    RuntimeHealth.IDLE -> if (state.autoChargeEnabled) {
        stringResource(R.string.status_waiting_charge)
    } else {
        stringResource(R.string.status_off)
    }
}
