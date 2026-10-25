package com.flipcharge.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(
    state: FlipState,
    overlayPermissionGranted: Boolean,
    notificationPermissionGranted: Boolean,
    usageAccessGranted: Boolean,
    excludedAppsCount: Int,
    showNotificationPermission: Boolean,
    onRequestOverlayPermission: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onMainToggle: () -> Unit,
    onPermanentAutoChange: (Boolean) -> Unit,
    onAutoChargeChange: (Boolean) -> Unit,
    onManageAppExceptions: () -> Unit,
    onOpenBatterySettings: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(R.string.app_subtitle),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            StatusCard(state)

            Spacer(Modifier.height(18.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                enabled = overlayPermissionGranted,
                onClick = onMainToggle
            ) {
                Text(
                    text = if (state.manualEnabled) {
                        stringResource(R.string.disable_forced_180)
                    } else {
                        stringResource(R.string.enable_forced_180)
                    },
                    fontWeight = FontWeight.Bold
                )
            }

            if (!overlayPermissionGranted) {
                Spacer(Modifier.height(10.dp))
                Button(onClick = onRequestOverlayPermission) {
                    Text(stringResource(R.string.grant_overlay_permission))
                }
            }

            Spacer(Modifier.height(16.dp))

            SettingSwitchCard(
                title = stringResource(R.string.permanent_180_title),
                description = stringResource(R.string.permanent_180_description),
                checked = state.permanentAutoEnabled,
                enabled = overlayPermissionGranted,
                onCheckedChange = onPermanentAutoChange
            )

            Spacer(Modifier.height(12.dp))

            SettingSwitchCard(
                title = stringResource(R.string.auto_charge_title),
                description = stringResource(R.string.auto_charge_description),
                checked = state.autoChargeEnabled,
                enabled = overlayPermissionGranted,
                onCheckedChange = onAutoChargeChange
            )

            if (showNotificationPermission && !notificationPermissionGranted) {
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onRequestNotificationPermission
                ) {
                    Text(stringResource(R.string.grant_notification_permission))
                }
            }

            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text(
                        text = stringResource(R.string.app_exceptions_title),
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = if (excludedAppsCount == 0) {
                            stringResource(
                                R.string.app_exceptions_description_empty
                            )
                        } else {
                            pluralStringResource(
                                R.plurals.app_exceptions_selected_count,
                                excludedAppsCount,
                                excludedAppsCount
                            )
                        },
                        style = MaterialTheme.typography.bodySmall
                    )

                    if (excludedAppsCount > 0 && !usageAccessGranted) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = stringResource(
                                R.string.app_exceptions_permission_missing
                            ),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    OutlinedButton(onClick = onManageAppExceptions) {
                        Text(stringResource(R.string.manage_app_exceptions))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text(
                        text = stringResource(R.string.background_title),
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = stringResource(R.string.background_description),
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(Modifier.height(10.dp))

                    OutlinedButton(onClick = onOpenBatterySettings) {
                        Text(stringResource(R.string.open_battery_settings))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun StatusCard(state: FlipState) {
    val status = when (state.source) {
        ActivationSource.MANUAL ->
            stringResource(R.string.status_force_180)

        ActivationSource.CHARGING ->
            stringResource(R.string.status_charging)

        ActivationSource.PERMANENT_AUTO ->
            stringResource(R.string.status_permanent_auto)

        ActivationSource.NONE ->
            if (state.autoChargeEnabled) {
                stringResource(R.string.status_waiting_charge)
            } else {
                stringResource(R.string.status_off)
            }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (state.effectiveMode) {
                    ActiveRotationMode.FORCE_REVERSE_PORTRAIT -> "180°"
                    ActiveRotationMode.PERMANENT_AUTO -> "AUTO+"
                    ActiveRotationMode.OFF -> "0°"
                },
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Black
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = status,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun SettingSwitchCard(
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = checked,
                enabled = enabled,
                onCheckedChange = onCheckedChange
            )
        }
    }
}
