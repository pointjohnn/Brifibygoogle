package ru.brifi.app.ui.theme

import android.graphics.Typeface
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ru.brifi.app.R

val GoogleSansFamily: FontFamily = runCatching {
    FontFamily(Typeface.create("google-sans", Typeface.NORMAL))
}.getOrElse { FontFamily.SansSerif }

val A24DisplayFamily: FontFamily = FontFamily(
    Font(R.font.a24_regular, FontWeight.Normal)
)

val BrifiTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = GoogleSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 40.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = GoogleSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = GoogleSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 26.sp
    ),
    titleLarge = TextStyle(
        fontFamily = GoogleSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = GoogleSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = GoogleSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 19.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = GoogleSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = GoogleSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = GoogleSansFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 13.sp
    )
)
