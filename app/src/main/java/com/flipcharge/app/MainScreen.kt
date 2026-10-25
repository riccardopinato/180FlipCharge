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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
    showNotificationPermission: Boolean,
    onRequestOverlayPermission: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onMainToggle: () -> Unit,
    onPermanentAutoChange: (Boolean) -> Unit,
    onAutoChargeChange: (Boolean) -> Unit,
    onManageAppExceptions: () -> Unit,
    onOpenBatterySettings: () -> Unit
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
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
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
            }

            item {
                val heroValue = when (state.effectiveMode) {
                    ActiveRotationMode.FORCE_REVERSE_PORTRAIT -> "180°"
                    ActiveRotationMode.PERMANENT_AUTO -> "AUTO+"
                    ActiveRotationMode.OFF -> "0°"
                }

                val status = when (state.source) {
                    ActivationSource.MANUAL -> stringResource(R.string.status_force_180)
                    ActivationSource.CHARGING -> stringResource(R.string.status_charging)
                    ActivationSource.PERMANENT_AUTO -> stringResource(R.string.status_permanent_auto)
                    ActivationSource.NONE -> if (state.autoChargeEnabled) {
                        stringResource(R.string.status_waiting_charge)
                    } else {
                        stringResource(R.string.status_off)
                    }
                }

                FlipHeroCard(
                    value = heroValue,
                    title = stringResource(R.string.hero_title),
                    detail = status,
                    active = state.active,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }

            item {
                PrimaryRotationAction(
                    active = state.manualEnabled,
                    enabled = overlayPermissionGranted,
                    onClick = if (overlayPermissionGranted) onMainToggle else onRequestOverlayPermission,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
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
                        modifier = Modifier.weight(1f),
                    )
                    FlipModeTile(
                        title = stringResource(R.string.auto_charge_title),
                        subtitle = stringResource(R.string.auto_charge_short),
                        glyph = "⚡",
                        accent = FlipOrange,
                        checked = state.autoChargeEnabled,
                        enabled = overlayPermissionGranted,
                        onCheckedChange = onAutoChargeChange,
                        modifier = Modifier.weight(1f),
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
            .fillMaxWidth()
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
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
