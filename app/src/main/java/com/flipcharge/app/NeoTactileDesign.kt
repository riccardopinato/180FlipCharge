package com.flipcharge.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** All surfaces are native Compose. No external raster art / fonts / runtime dependency. */
internal object NeoPalette {
    val Background = Color(0xFF0B1015)
    val SurfaceHigh = Color(0xFF24343A)
    val SurfaceMid = Color(0xFF17262C)
    val SurfaceLow = Color(0xFF101A20)
    val Border = Color(0xFF425B61)
    val Highlight = Color(0xFF66868A)
    val Ink = Color(0xFFEAF1F1)
    val Subtle = Color(0xFF9EADB3)
    val Muted = Color(0xFF71868C)
    val Teal = Color(0xFF38DCCA)
    val Amber = Color(0xFFF1C487)
    val Danger = Color(0xFFF9A19A)
}

private val panelShape = RoundedCornerShape(24.dp)
private val panelGradient = Brush.linearGradient(
    colors = listOf(NeoPalette.SurfaceHigh, NeoPalette.SurfaceMid, NeoPalette.SurfaceLow),
)

internal fun Modifier.neoSmallPanel(): Modifier = this
    .size(48.dp)
    .border(1.dp, NeoPalette.Border, RoundedCornerShape(13.dp))
    .background(
        Brush.linearGradient(listOf(NeoPalette.SurfaceHigh, NeoPalette.SurfaceLow)),
        RoundedCornerShape(13.dp),
    )

@Composable
internal fun NeoSectionHeader(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.3.sp,
        color = NeoPalette.Subtle,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
internal fun NeoHeroCard(
    value: String,
    title: String,
    detail: String,
    operational: Boolean,
    waiting: Boolean,
    issue: Boolean,
) {
    val statusColor = when {
        issue -> NeoPalette.Danger
        operational -> NeoPalette.Teal
        waiting -> NeoPalette.Amber
        else -> NeoPalette.Subtle
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NeoPalette.Border, panelShape)
            .background(panelGradient, panelShape)
            .padding(horizontal = 18.dp, vertical = 19.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = NeoPalette.Subtle,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.weight(1f),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(CircleShape)
                    .background(NeoPalette.Background.copy(alpha = 0.72f))
                    .padding(horizontal = 10.dp, vertical = 7.dp),
            ) {
                Box(Modifier.size(6.dp).background(statusColor, CircleShape))
                Text(
                    text = if (operational) "ON" else if (issue) "!" else "OFF",
                    color = statusColor,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = value,
                    color = if (operational) Color(0xFFD7FFF6) else NeoPalette.Ink,
                    fontSize = if (value.length > 3) 46.sp else 60.sp,
                    lineHeight = 62.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-2).sp,
                )
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodySmall,
                    color = NeoPalette.Subtle,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.width(10.dp))
            NeoOrientationDial(operational)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(NeoPalette.Highlight.copy(alpha = 0.23f)),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "180°",
                style = MaterialTheme.typography.labelMedium,
                color = NeoPalette.Muted,
            )
            Text(
                text = when {
                    issue -> "!"
                    operational -> "●"
                    waiting -> "◌"
                    else -> "○"
                },
                color = statusColor,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun NeoOrientationDial(operational: Boolean) {
    Box(
        modifier = Modifier
            .size(122.dp)
            .border(1.dp, NeoPalette.Highlight, CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF536C6C), Color(0xFF1B2D32), Color(0xFF344B50)),
                ),
                CircleShape,
            )
            .padding(9.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(104.dp)
                .border(1.dp, Color(0xFF263D42), CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF2B3E45), Color(0xFF0A151A)),
                    ),
                    CircleShape,
                ),
        )
        Canvas(Modifier.size(115.dp)) {
            drawArc(
                color = if (operational) NeoPalette.Teal else Color(0xFF547876),
                startAngle = if (operational) 135f else 210f,
                sweepAngle = 168f,
                useCenter = false,
                style = Stroke(width = 3.dp.toPx()),
            )
        }
        NeoMetalThumb(61.dp)
        Text(
            text = "↻",
            color = Color(0xFF244C4D),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

/** Fine brushed lines are drawn at runtime; no bitmap assets or third-party IP. */
@Composable
private fun NeoMetalThumb(diameter: Dp) {
    Box(
        modifier = Modifier
            .size(diameter)
            .border(1.dp, Color(0xFFE9F1EB), CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFFEAF1EA),
                        Color(0xFFA9C0BA),
                        Color(0xFFD3E0D9),
                        Color(0xFF829995),
                    ),
                ),
                CircleShape,
            )
            .clip(CircleShape),
    ) {
        Canvas(Modifier.matchParentSize()) {
            var x = -size.height
            while (x < size.width + size.height) {
                drawLine(
                    color = Color.White.copy(alpha = 0.20f),
                    start = Offset(x, 0f),
                    end = Offset(x + size.height, size.height),
                    strokeWidth = 1f,
                )
                x += 6f
            }
        }
    }
}

@Composable
internal fun NeoSwitch(
    checked: Boolean,
    enabled: Boolean,
    label: String,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    large: Boolean = false,
) {
    val width = if (large) 118.dp else 66.dp
    val height = if (large) 65.dp else 39.dp
    val knob = if (large) 52.dp else 30.dp
    val shift by animateDpAsState(
        targetValue = if (checked) width - knob - 10.dp else 0.dp,
        animationSpec = tween(250),
        label = "tactile-switch-travel",
    )
    val trackGradient = if (checked) {
        Brush.linearGradient(
            listOf(Color(0xFF0A9C91), Color(0xFF22E1C4), Color(0xFF0C8479)),
        )
    } else {
        Brush.verticalGradient(
            listOf(Color(0xFF111C21), Color(0xFF273A3F), Color(0xFF101A20)),
        )
    }
    Box(
        modifier = modifier
            .size(width, height)
            .clip(CircleShape)
            .border(
                2.dp,
                if (checked) Color(0xFF55C5B6) else Color(0xFF687C81),
                CircleShape,
            )
            .background(trackGradient, CircleShape)
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            )
            .semantics { contentDescription = label }
            .padding(5.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier.offset(x = shift),
        ) {
            NeoMetalThumb(knob)
        }
    }
}

@Composable
internal fun NeoManualControl(
    title: String,
    detail: String,
    checked: Boolean,
    permissionGranted: Boolean,
    onToggle: () -> Unit,
) {
    val shape = RoundedCornerShape(21.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (checked) Color(0xFF3D9789) else NeoPalette.Border,
                shape,
            )
            .background(panelGradient, shape)
            .padding(horizontal = 16.dp, vertical = 17.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Text(
                    text = "180°",
                    color = NeoPalette.Teal,
                    style = MaterialTheme.typography.labelLarge,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = title,
                    color = NeoPalette.Ink,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                )
            }
            NeoSwitch(
                checked = checked,
                enabled = true,
                label = title,
                onCheckedChange = { onToggle() },
                large = true,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(NeoPalette.Highlight.copy(alpha = 0.18f)),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                Modifier.size(6.dp).background(
                    if (checked) NeoPalette.Teal else if (!permissionGranted) NeoPalette.Amber else NeoPalette.Muted,
                    CircleShape,
                ),
            )
            Text(
                text = if (!permissionGranted) {
                    "!"
                } else if (checked) {
                    "ON"
                } else {
                    "OFF"
                },
                style = MaterialTheme.typography.labelSmall,
                color = if (checked) NeoPalette.Teal else NeoPalette.Subtle,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "180°",
                color = NeoPalette.Muted,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
internal fun NeoModeRow(
    title: String,
    subtitle: String,
    glyph: String,
    accent: Color,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.60f)
            .border(1.dp, NeoPalette.Border.copy(alpha = 0.80f), RoundedCornerShape(17.dp))
            .background(
                Brush.linearGradient(listOf(NeoPalette.SurfaceMid, NeoPalette.SurfaceLow)),
                RoundedCornerShape(17.dp),
            )
            .heightIn(min = 82.dp)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(39.dp)
                .border(1.dp, NeoPalette.Highlight, RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(listOf(Color(0xFF30494F), Color(0xFF192A30))),
                    RoundedCornerShape(12.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = glyph, color = accent, fontSize = 19.sp, fontWeight = FontWeight.Bold)
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                color = NeoPalette.Ink,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                color = NeoPalette.Subtle,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
        NeoSwitch(
            checked = checked,
            enabled = enabled,
            label = title,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
internal fun NeoUtilityRow(
    title: String,
    subtitle: String,
    glyph: String,
    onClick: () -> Unit,
    attention: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .heightIn(min = 68.dp)
            .padding(vertical = 8.dp, horizontal = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(37.dp)
                .border(1.dp, NeoPalette.Border, RoundedCornerShape(11.dp))
                .background(NeoPalette.SurfaceMid, RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = glyph,
                color = if (attention) NeoPalette.Amber else NeoPalette.Subtle,
                style = MaterialTheme.typography.titleLarge,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = NeoPalette.Ink,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = NeoPalette.Subtle,
                maxLines = 3,
            )
        }
        Text(
            text = "›",
            color = NeoPalette.Subtle,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Composable
internal fun NeoPermissionCard(
    title: String,
    body: String,
    action: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF8D6750), RoundedCornerShape(17.dp))
            .background(Color(0xFF2D2724), RoundedCornerShape(17.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = NeoPalette.Amber,
        )
        Text(text = body, style = MaterialTheme.typography.bodySmall, color = NeoPalette.Subtle)
        Text(
            text = action,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = NeoPalette.Amber,
        )
    }
}
