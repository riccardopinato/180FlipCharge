package com.flipcharge.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val FlipBlue = Color(0xFF4776FF)
val FlipPurple = Color(0xFF8E63FF)
val FlipMint = Color(0xFF30C79B)
val FlipOrange = Color(0xFFFF9F43)
val FlipRose = Color(0xFFFF6B8A)

private val LightColors = lightColorScheme(
    primary = FlipBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE7EDFF),
    onPrimaryContainer = Color(0xFF163B9C),
    secondary = FlipPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF0EAFF),
    onSecondaryContainer = Color(0xFF4C2B9C),
    tertiary = FlipMint,
    onTertiary = Color(0xFF00382A),
    tertiaryContainer = Color(0xFFD8F8EC),
    onTertiaryContainer = Color(0xFF075D45),
    background = Color(0xFFF7F8FC),
    onBackground = Color(0xFF15171D),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF17191F),
    surfaceVariant = Color(0xFFF0F2F7),
    onSurfaceVariant = Color(0xFF646977),
    outline = Color(0xFFD9DCE5),
    error = Color(0xFFBA1A1A),
)

private val DarkColors = darkColorScheme(
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

private val FlipTypography = Typography(
    headlineLarge = TextStyle(
        fontSize = 32.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.Bold,
    ),
    headlineMedium = TextStyle(
        fontSize = 26.sp,
        lineHeight = 31.sp,
        fontWeight = FontWeight.Bold,
    ),
    titleLarge = TextStyle(
        fontSize = 20.sp,
        lineHeight = 25.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        lineHeight = 21.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        lineHeight = 23.sp,
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        lineHeight = 17.sp,
    ),
    labelLarge = TextStyle(
        fontSize = 14.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.SemiBold,
    ),
)

private val FlipShapes = Shapes(
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(22.dp),
    large = RoundedCornerShape(30.dp),
)

@Composable
fun FlipChargeTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = FlipTypography,
        shapes = FlipShapes,
        content = content,
    )
}
