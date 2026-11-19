package ru.brifi.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BrifiDarkColorScheme = darkColorScheme(
    primary = BrifiColors.ActiveCountAccent,
    onPrimary = Color.White,
    secondary = BrifiColors.DeadlineTodayAccent,
    onSecondary = Color.Black,
    background = BrifiColors.Background,
    onBackground = BrifiColors.TextPrimary,
    surface = BrifiColors.SectionDark,
    onSurface = BrifiColors.TextPrimary,
    surfaceVariant = BrifiColors.InProgressCardBg,
    onSurfaceVariant = BrifiColors.TextSecondary,
    outline = BrifiColors.CapsuleBorder
)

@Composable
fun BrifiTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BrifiDarkColorScheme,
        typography = BrifiTypography,
        content = content
    )
}
