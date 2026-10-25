package com.flipcharge.app

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.flipcharge.app.ui.theme.FlipBlue
import com.flipcharge.app.ui.theme.FlipRose

@Composable
fun AppExceptionsScreen(
    apps: List<InstalledAppInfo>,
    excludedApps: Set<String>,
    usageAccessGranted: Boolean,
    onBack: () -> Unit,
    onOpenUsageAccess: () -> Unit,
    onToggleApp: (String, Boolean) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    TextButton(
                        onClick = onBack,
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        Text("←  " + stringResource(R.string.back))
                    }
                    Text(
                        text = stringResource(R.string.exceptions_eyebrow),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = stringResource(R.string.app_exceptions_title),
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Text(
                        text = stringResource(R.string.app_exceptions_intro_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (!usageAccessGranted) {
                item {
                    val shape = RoundedCornerShape(24.dp)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .flipGradientBorder(shape),
                        shape = shape,
                        colors = CardDefaults.cardColors(
                            containerColor = FlipRose.copy(alpha = 0.10f),
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.usage_access_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = FlipRose,
                            )
                            Text(
                                text = stringResource(R.string.usage_access_description),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            TextButton(
                                onClick = onOpenUsageAccess,
                                contentPadding = PaddingValues(0.dp),
                            ) {
                                Text(stringResource(R.string.open_usage_access_settings))
                            }
                        }
                    }
                }
            }

            if (apps.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.no_apps_found),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                items(apps, key = { it.packageName }) { app ->
                    val selected = excludedApps.contains(app.packageName)
                    val shape = RoundedCornerShape(22.dp)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .flipGradientBorder(shape),
                        shape = shape,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            FlipStatusPill(
                                text = if (selected) "180° OFF" else "180°",
                                accent = if (selected) FlipRose else FlipBlue,
                            )
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                Text(
                                    text = app.label,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    text = app.packageName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Switch(
                                checked = selected,
                                onCheckedChange = { onToggleApp(app.packageName, it) },
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(2.dp)) }
        }
    }
}
