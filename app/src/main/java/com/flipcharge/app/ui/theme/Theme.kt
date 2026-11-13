package com.flipcharge.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipcharge.app.R

// SIL OFL 1.1: font licenses are committed under docs/fonts/.
// Bundled files are the original Google Fonts distributions.
val Manrope = FontFamily(
    Font(R.font.manrope_variable, weight = FontWeight.Normal),
)
val SpaceGrotesk = FontFamily(
    Font(R.font.space_grotesk_variable, weight = FontWeight.Normal),
)

val FlipBlue = Color(0xFF38DCCA)
val FlipPurple = Color(0xFF628784)
val FlipMint = Color(0xFF38DCCA)
val FlipOrange = Color(0xFFF1C487)
val FlipRose = Color(0xFFF9A19A)

private val NeoDarkColors = darkColorScheme(
    primary = Color(0xFF38DCCA),
    onPrimary = Color(0xFF062A28),
    primaryContainer = Color(0xFF173A38),
    onPrimaryContainer = Color(0xFFBDF9EB),
    secondary = Color(0xFF9EB6BA),
    onSecondary = Color(0xFF182C30),
    secondaryContainer = Color(0xFF263B42),
    onSecondaryContainer = Color(0xFFD8E9E9),
    tertiary = Color(0xFFF1C487),
    onTertiary = Color(0xFF33200E),
    tertiaryContainer = Color(0xFF4B3424),
    onTertiaryContainer = Color(0xFFFFE3BA),
    background = Color(0xFF0B1015),
    onBackground = Color(0xFFEAF1F1),
    surface = Color(0xFF151E24),
    onSurface = Color(0xFFEAF1F1),
    surfaceVariant = Color(0xFF24343A),
    onSurfaceVariant = Color(0xFF9EADB3),
    outline = Color(0xFF425B61),
    error = Color(0xFFF9A19A),
)

private val DesignTypography = Typography(
    headlineLarge = TextStyle(fontFamily = SpaceGrotesk, fontSize = 26.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold, letterSpacing = (-1.0).sp),
    headlineMedium = TextStyle(fontFamily = SpaceGrotesk, fontSize = 24.sp, lineHeight = 29.sp, fontWeight = FontWeight.SemiBold),
    titleLarge = TextStyle(fontFamily = SpaceGrotesk, fontSize = 19.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontFamily = SpaceGrotesk, fontSize = 17.sp, lineHeight = 23.sp, fontWeight = FontWeight.SemiBold),
    titleSmall = TextStyle(fontFamily = Manrope, fontSize = 13.sp, lineHeight = 19.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontFamily = Manrope, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = Manrope, fontSize = 14.sp, lineHeight = 21.sp),
    bodySmall = TextStyle(fontFamily = Manrope, fontSize = 11.sp, lineHeight = 17.sp),
    labelLarge = TextStyle(fontFamily = Manrope, fontSize = 12.sp, lineHeight = 17.sp, fontWeight = FontWeight.Bold),
    labelMedium = TextStyle(fontFamily = Manrope, fontSize = 10.sp, lineHeight = 15.sp, fontWeight = FontWeight.Bold),
    labelSmall = TextStyle(fontFamily = Manrope, fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.Medium),
)

private val NeoShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
)

/**
 * Design approved as dark-first. Dark palette is intentional, independent of OS
 * theme mode, to match the v2 preview; status/system bar tint is handled by Android.
 */
@Composable
fun FlipChargeTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = NeoDarkColors,
        typography = DesignTypography,
        shapes = NeoShapes,
        content = content,
    )
}
