package com.flipcharge.app

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipcharge.app.ui.theme.Manrope
import com.flipcharge.app.ui.theme.SpaceGrotesk
import kotlin.math.min

/**
 * Pixel-level translation of the APPROVED Neo Tactile Dark web preview V2,
 * not the superseded Retro Pixel PR. UI-only; sensor/service state is supplied
 * by MainActivity exactly as before. Original geometry/gradients authored
 * in Compose; no WebView or third-party image assets.
 */
internal object NeoPalette {
    val Background = Color(0xFF0B1015)
    val SurfaceHigh = Color(0xFF24343A)
    val SurfaceMid = Color(0xFF17262C)
    val SurfaceLow = Color(0xFF101A20)
    val Border = Color(0xFF425B61)
    val Highlight = Color(0xFF86AAA9)
    val Ink = Color(0xFFEAF1F1)
    val Subtle = Color(0xFF9EADB3)
    val Muted = Color(0xFF71868C)
    val Teal = Color(0xFF38DCCA)
    val Amber = Color(0xFFF1C487)
    val Danger = Color(0xFFF9A19A)
}

internal enum class NeoRuntimeTone { ACTIVE, INACTIVE, WAITING, ERROR, PERMISSION, STARTING, PAUSED }

internal enum class NeoModeGlyph { ROTATE, CHARGING }
internal enum class NeoUtilityGlyph { APPS, BATTERY }

private val heroShape = RoundedCornerShape(24.dp)
private val manualShape = RoundedCornerShape(21.dp)
private val modeShape = RoundedCornerShape(17.dp)

private val heroSurface = Brush.linearGradient(
    listOf(Color(0xFF24343A), Color(0xFF17262C), Color(0xFF111B22)),
)
private val controlSurface = Brush.linearGradient(
    listOf(Color(0xFF25353A), Color(0xFF162329), Color(0xFF101C22)),
)
private val modeSurface = Brush.linearGradient(
    listOf(Color(0xFF1B282E), Color(0xFF121D23)),
)

/** Subtle layered elevation; avoids a generic flat Material Card. */
private fun Modifier.neoRaisedSurface(
    shape: RoundedCornerShape,
    brush: Brush,
    border: Color,
    elevation: Dp,
): Modifier = this
    .shadow(elevation = elevation, shape = shape, ambientColor = Color.Black, spotColor = Color.Black)
    .background(brush = brush, shape = shape)
    .border(1.dp, border, shape)
    .drawBehind {
        // Bottom edge shadow is intentionally non-uniform.
        drawLine(
            color = Color.Black.copy(alpha = .33f),
            start = Offset(24.dp.toPx(), size.height - 1.dp.toPx()),
            end = Offset(size.width - 24.dp.toPx(), size.height - 1.dp.toPx()),
            strokeWidth = 1.dp.toPx(),
        )
    }

/** The small raised settings button from the web reference, 48dp touch target. */
internal fun Modifier.neoSmallPanel(): Modifier = this
    .size(48.dp)
    .neoRaisedSurface(
        shape = RoundedCornerShape(13.dp),
        brush = Brush.linearGradient(listOf(Color(0xFF1E2D34), Color(0xFF101B21))),
        border = Color(0xFF3D555C),
        elevation = 4.dp,
    )

@Composable
internal fun NeoSectionHeader(title: String, index: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontFamily = Manrope,
            fontSize = 10.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Bold,
            color = NeoPalette.Subtle,
            letterSpacing = 1.25.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = index,
            fontFamily = Manrope,
            fontSize = 9.sp,
            lineHeight = 13.sp,
            color = Color(0xFF597E80),
            letterSpacing = .3.sp,
            maxLines = 1,
        )
    }
}

private fun toneColor(tone: NeoRuntimeTone): Color = when (tone) {
    NeoRuntimeTone.ACTIVE -> NeoPalette.Teal
    NeoRuntimeTone.WAITING -> NeoPalette.Amber
    NeoRuntimeTone.ERROR, NeoRuntimeTone.PERMISSION -> NeoPalette.Danger
    NeoRuntimeTone.STARTING -> NeoPalette.Amber
    NeoRuntimeTone.PAUSED, NeoRuntimeTone.INACTIVE -> NeoPalette.Subtle
}

@Composable
internal fun NeoHeroCard(
    value: String,
    title: String,
    detail: String,
    modeLabel: String,
    stateLabel: String,
    engineLabel: String,
    modeEyebrow: String,
    dialDescription: String,
    tone: NeoRuntimeTone,
) {
    val accent = toneColor(tone)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .neoRaisedSurface(heroShape, heroSurface, Color(0xFF3A565B), 11.dp)
            .clip(heroShape)
            .padding(horizontal = 18.dp, vertical = 19.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                fontFamily = Manrope,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 14.sp,
                color = Color(0xFFB5CCCE),
                letterSpacing = 1.25.sp,
                maxLines = 2,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .border(
                        1.dp,
                        accent.copy(alpha = if (tone == NeoRuntimeTone.ACTIVE) .45f else .24f),
                        CircleShape,
                    )
                    .background(Color(0xFF0F1A20).copy(alpha = .84f), CircleShape)
                    .padding(horizontal = 10.dp, vertical = 7.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .shadow(
                            if (tone == NeoRuntimeTone.ACTIVE) 5.dp else 0.dp,
                            CircleShape,
                            ambientColor = accent,
                            spotColor = accent,
                        )
                        .background(accent, CircleShape),
                )
                Text(
                    text = stateLabel,
                    color = accent,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    letterSpacing = .7.sp,
                    maxLines = 1,
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Text(
                    text = modeEyebrow,
                    fontFamily = Manrope,
                    color = Color(0xFF658F94),
                    letterSpacing = 1.15.sp,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    maxLines = 2,
                )
                Text(
                    text = value,
                    fontFamily = SpaceGrotesk,
                    fontSize = if (value.length > 3) 43.sp else 62.sp,
                    lineHeight = 64.sp,
                    letterSpacing = (-3).sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    color = if (tone == NeoRuntimeTone.ACTIVE) Color(0xFFD6FFF5) else NeoPalette.Ink,
                )
                Text(
                    text = detail,
                    fontFamily = Manrope,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = NeoPalette.Subtle,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            NeoOrientationDial(
                active = tone == NeoRuntimeTone.ACTIVE,
                description = dialDescription,
            )
        }
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Brush.horizontalGradient(listOf(Color(0xFF93C6C5).copy(alpha = .20f), Color.Transparent))),
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = engineLabel,
                fontFamily = Manrope,
                fontSize = 10.sp,
                letterSpacing = .7.sp,
                color = Color(0xFF7F969B),
            )
            Text(
                text = modeLabel,
                fontFamily = SpaceGrotesk,
                fontSize = 10.sp,
                lineHeight = 13.sp,
                letterSpacing = .8.sp,
                maxLines = 1,
                color = Color(0xFFACC3C2),
            )
        }
    }
}

@Composable
private fun NeoOrientationDial(active: Boolean, description: String) {
    Box(
        modifier = Modifier
            .size(130.dp)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(128.dp)
                .shadow(11.dp, CircleShape, ambientColor = Color.Black, spotColor = Color.Black)
                .background(
                    Brush.sweepGradient(
                        0.0f to Color(0xFF31484C),
                        .07f to Color(0xFF779395),
                        .17f to Color(0xFF1B2E32),
                        .52f to Color(0xFF142126),
                        .84f to Color(0xFF536B6D),
                        1.0f to Color(0xFF31484C),
                    ),
                    CircleShape,
                )
                .border(1.dp, Color(0xFF8BA9A5).copy(alpha = .48f), CircleShape),
        )
        Box(
            modifier = Modifier
                .size(110.dp)
                .shadow(1.dp, CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF293D44), Color(0xFF121D23), Color(0xFF0A1419)),
                        center = Offset(28f, 26f),
                    ),
                    CircleShape,
                )
                .border(1.dp, Color(0xFF0A171B), CircleShape),
        )
        Canvas(Modifier.size(99.dp)) {
            drawCircle(
                color = Color(0xFF81B2AC).copy(alpha = .18f),
                radius = size.minDimension * .43f,
                style = Stroke(
                    width = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f, 7f)),
                ),
            )
        }
        Canvas(Modifier.size(130.dp)) {
            drawArc(
                color = if (active) Color(0xFF3EF1D4) else Color(0xFF528C88),
                startAngle = if (active) 316f else 135f,
                sweepAngle = 105f,
                useCenter = false,
                style = Stroke(3.dp.toPx(), cap = StrokeCap.Round),
            )
        }
        NeoMetalDisc(69.dp)
        Canvas(Modifier.size(25.dp)) {
            val w = size.width
            val h = size.height
            drawArc(
                color = Color(0xFF244C4D),
                startAngle = 20f,
                sweepAngle = 292f,
                useCenter = false,
                topLeft = Offset(w * .10f, h * .10f),
                size = androidx.compose.ui.geometry.Size(w * .80f, h * .80f),
                style = Stroke(2.dp.toPx(), cap = StrokeCap.Round),
            )
            // Arrow head integrated into the etched circular symbol.
            val arrow = Path().apply {
                moveTo(w * .76f, h * .18f)
                lineTo(w * .87f, h * .44f)
                lineTo(w * .58f, h * .40f)
            }
            drawPath(arrow, color = Color(0xFF244C4D), style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
        }
    }
}

/**
 * Multi-stop brushed titanium with etched diagonal lines, inner bevel and
 * soft shadow. This recreates the tactile switch thumb in the approved web V2.
 */
@Composable
private fun NeoMetalDisc(diameter: Dp) {
    Box(
        modifier = Modifier
            .size(diameter)
            .shadow(8.dp, CircleShape, ambientColor = Color.Black, spotColor = Color.Black)
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFFA6BAB8),
                        Color(0xFFD7E5E0),
                        Color(0xFF98ADAB),
                        Color(0xFFE5EFEA),
                        Color(0xFF859B97),
                    ),
                ),
                CircleShape,
            )
            .border(1.dp, Color(0xFFE8F6EE), CircleShape)
            .clip(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.matchParentSize()) {
            // Lines stay inside the clipped circle and scale with thumb radius.
            var x = -size.height
            while (x <= size.width + size.height) {
                drawLine(
                    color = Color.White.copy(alpha = .28f),
                    start = Offset(x, 0f),
                    end = Offset(x + size.height, size.height),
                    strokeWidth = 1.1f,
                )
                drawLine(
                    color = Color(0xFF284848).copy(alpha = .11f),
                    start = Offset(x + 2f, 0f),
                    end = Offset(x + 2f + size.height, size.height),
                    strokeWidth = 1f,
                )
                x += 5.0f
            }
            drawCircle(
                color = Color.White.copy(alpha = .52f),
                radius = size.minDimension * .39f,
                style = Stroke(width = 1.dp.toPx()),
            )
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
    val height = if (large) 65.dp else 48.dp
    val trackHeight = if (large) 65.dp else 38.dp
    val thumbSize = if (large) 51.dp else 28.dp
    val travel by animateDpAsState(
        targetValue = if (checked) width - thumbSize - 10.dp else 0.dp,
        animationSpec = tween(280),
        label = "metal-thumb-translation",
    )
    val border by animateColorAsState(
        if (checked) Color(0xFF54BBAE) else Color(0xFF607277),
        animationSpec = tween(220),
        label = "track-border",
    )
    val track = if (checked) {
        Brush.linearGradient(listOf(Color(0xFF07AD9F), Color(0xFF1FE2C0), Color(0xFF0C8E84)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFF101A20), Color(0xFF26363B), Color(0xFF121D23)))
    }

    Box(
        modifier = modifier
            .size(width, height)
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            )
            .semantics { contentDescription = label }
            .alpha(if (enabled) 1f else .52f),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .size(width, trackHeight)
                .shadow(
                    if (checked) 8.dp else 4.dp,
                    CircleShape,
                    ambientColor = if (checked) NeoPalette.Teal.copy(alpha = .30f) else Color.Black,
                    spotColor = if (checked) NeoPalette.Teal.copy(alpha = .30f) else Color.Black,
                )
                .background(track, CircleShape)
                .border(2.dp, border, CircleShape)
                .padding(4.dp)
                .border(1.dp, Color.Black.copy(alpha = .32f), CircleShape),
        )
        Box(
            modifier = Modifier
                .padding(start = 5.dp)
                .offset(x = travel),
        ) {
            NeoMetalDisc(thumbSize)
        }
    }
}

@Composable
internal fun NeoManualControl(
    eyebrow: String,
    title: String,
    subtitle: String,
    onState: String,
    offState: String,
    checked: Boolean,
    permissionGranted: Boolean,
    onToggle: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .neoRaisedSurface(
                manualShape,
                controlSurface,
                if (checked) Color(0xFF3D9789) else Color(0xFF496368),
                elevation = if (checked) 12.dp else 10.dp,
            )
            .clip(manualShape)
            .padding(start = 16.dp, end = 16.dp, top = 19.dp, bottom = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = eyebrow,
                    color = Color(0xFF82AEB1),
                    fontFamily = Manrope,
                    fontSize = 9.sp,
                    letterSpacing = 1.1.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
                Text(
                    text = title,
                    fontFamily = SpaceGrotesk,
                    color = NeoPalette.Ink,
                    fontSize = 19.sp,
                    lineHeight = 23.sp,
                    letterSpacing = (-.4).sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                )
                Text(
                    text = subtitle,
                    fontFamily = Manrope,
                    color = Color(0xFF849CA3),
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    maxLines = 2,
                )
            }
            NeoSwitch(
                checked = checked,
                enabled = true, // Without permission, native callback opens permission settings.
                label = title,
                onCheckedChange = { onToggle() },
                large = true,
            )
        }
        Spacer(Modifier.height(16.dp))
        Box(
            Modifier.fillMaxWidth().height(1.dp)
                .background(Color(0xFF9DD6D5).copy(alpha = .18f)),
        )
        Spacer(Modifier.height(11.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                Modifier.size(6.dp).background(
                    if (!permissionGranted) NeoPalette.Amber else if (checked) NeoPalette.Teal else NeoPalette.Muted,
                    CircleShape,
                ),
            )
            Text(
                text = if (checked) onState else offState,
                fontFamily = Manrope,
                color = if (checked) NeoPalette.Teal else NeoPalette.Subtle,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "MANUAL OVERRIDE",
                fontFamily = SpaceGrotesk,
                color = Color(0xFF66868B),
                fontSize = 9.sp,
                letterSpacing = .7.sp,
            )
        }
    }
}

@Composable
private fun NeoModeSymbol(glyph: NeoModeGlyph, accent: Color) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .shadow(3.dp, RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(listOf(Color(0xFF31494F), Color(0xFF192C31))),
                RoundedCornerShape(12.dp),
            )
            .border(1.dp, Color(0xFF48666B), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(20.dp)) {
            if (glyph == NeoModeGlyph.ROTATE) {
                drawArc(
                    color = accent,
                    startAngle = 20f,
                    sweepAngle = 285f,
                    useCenter = false,
                    topLeft = Offset(size.width * .12f, size.height * .12f),
                    size = androidx.compose.ui.geometry.Size(size.width * .76f, size.height * .76f),
                    style = Stroke(1.75.dp.toPx(), cap = StrokeCap.Round),
                )
                val arrow = Path().apply {
                    moveTo(size.width * .76f, size.height * .16f)
                    lineTo(size.width * .90f, size.height * .44f)
                    lineTo(size.width * .62f, size.height * .42f)
                }
                drawPath(arrow, color = accent, style = Stroke(1.8.dp.toPx(), cap = StrokeCap.Round))
            } else {
                val bolt = Path().apply {
                    moveTo(size.width * .56f, size.height * .03f)
                    lineTo(size.width * .20f, size.height * .55f)
                    lineTo(size.width * .47f, size.height * .55f)
                    lineTo(size.width * .39f, size.height * .98f)
                    lineTo(size.width * .82f, size.height * .40f)
                    lineTo(size.width * .55f, size.height * .40f)
                    close()
                }
                drawPath(bolt, color = accent)
            }
        }
    }
}

@Composable
internal fun NeoModeRow(
    title: String,
    subtitle: String,
    glyph: NeoModeGlyph,
    accent: Color,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .neoRaisedSurface(modeShape, modeSurface, Color(0xFF364B51), 5.dp)
            .heightIn(min = 78.dp)
            .padding(horizontal = 13.dp, vertical = 13.dp)
            .alpha(if (enabled) 1f else .55f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        NeoModeSymbol(glyph, accent)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                fontFamily = Manrope,
                color = NeoPalette.Ink,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
            )
            Text(
                text = subtitle,
                fontFamily = Manrope,
                color = Color(0xFF83999F),
                fontSize = 10.sp,
                lineHeight = 14.sp,
                maxLines = 2,
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
private fun NeoUtilitySymbol(glyph: NeoUtilityGlyph, attention: Boolean) {
    Box(
        modifier = Modifier
            .size(37.dp)
            .background(Color(0xFF1A2A30), RoundedCornerShape(11.dp))
            .border(1.dp, Color(0xFF365258), RoundedCornerShape(11.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(18.dp)) {
            val lineColor = if (attention) NeoPalette.Amber else Color(0xFF9FBFC0)
            if (glyph == NeoUtilityGlyph.APPS) {
                drawRoundRect(
                    color = lineColor,
                    topLeft = Offset(size.width * .13f, size.height * .13f),
                    size = androidx.compose.ui.geometry.Size(size.width * .74f, size.height * .74f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f),
                    style = Stroke(1.55.dp.toPx()),
                )
                drawRect(
                    color = lineColor,
                    topLeft = Offset(size.width * .36f, size.height * .36f),
                    size = androidx.compose.ui.geometry.Size(size.width * .28f, size.height * .28f),
                    style = Stroke(1.5.dp.toPx()),
                )
            } else {
                drawRoundRect(
                    color = lineColor,
                    topLeft = Offset(size.width * .06f, size.height * .24f),
                    size = androidx.compose.ui.geometry.Size(size.width * .79f, size.height * .53f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f),
                    style = Stroke(1.55.dp.toPx()),
                )
                drawLine(
                    color = lineColor,
                    start = Offset(size.width * .92f, size.height * .41f),
                    end = Offset(size.width * .92f, size.height * .62f),
                    strokeWidth = 1.6.dp.toPx(),
                )
            }
        }
    }
}

@Composable
internal fun NeoUtilityRow(
    title: String,
    subtitle: String,
    glyph: NeoUtilityGlyph,
    onClick: () -> Unit,
    attention: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .heightIn(min = 70.dp)
            .padding(vertical = 10.dp, horizontal = 1.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        NeoUtilitySymbol(glyph, attention)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(
                text = title,
                fontFamily = Manrope,
                color = NeoPalette.Ink,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
            )
            Text(
                text = subtitle,
                fontFamily = Manrope,
                color = NeoPalette.Muted,
                fontSize = 10.sp,
                lineHeight = 14.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = "›",
            fontFamily = SpaceGrotesk,
            fontSize = 21.sp,
            color = NeoPalette.Subtle,
        )
    }
    Box(Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = .075f)))
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
            .background(
                Brush.linearGradient(listOf(Color(0xFF332B26), Color(0xFF211B1A))),
                RoundedCornerShape(15.dp),
            )
            .border(1.dp, Color(0xFFB07B5C), RoundedCornerShape(15.dp))
            .clickable(onClick = onClick)
            .padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Text(
            text = title,
            fontFamily = SpaceGrotesk,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFF5D0AB),
        )
        Text(
            text = body,
            fontFamily = Manrope,
            fontSize = 11.sp,
            lineHeight = 17.sp,
            color = Color(0xFFBFA99A),
        )
        Text(
            text = action,
            fontFamily = Manrope,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF3DDC5),
        )
    }
}
