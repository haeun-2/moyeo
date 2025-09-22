package com.d108.moyeo.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.d108.moyeo.R

val PretendardFontFamily = FontFamily(
    Font(R.font.pretendard_black, FontWeight.Black),
    Font(R.font.pretendard_bold, FontWeight.Bold),
    Font(R.font.pretendard_extrabold, FontWeight.ExtraBold),
    Font(R.font.pretendard_extralight, FontWeight.ExtraLight),
    Font(R.font.pretendard_light, FontWeight.Light),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_semibold, FontWeight.SemiBold),
    Font(R.font.pretendard_thin, FontWeight.Thin)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 30.sp,
        lineHeight = 38.sp,
    ),
//    displayMedium = TextStyle(
//        fontFamily = PretendardFontFamily,
//        fontWeight = FontWeight.Normal,
//        fontSize = 45.sp,
//        lineHeight = 52.sp,
//    ),
//    displaySmall = TextStyle(
//        fontFamily = PretendardFontFamily,
//        fontWeight = FontWeight.Normal,
//        fontSize = 36.sp,
//        lineHeight = 44.sp,
//    ),
    titleLarge = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 30.sp,
    ),
//    titleSmall = TextStyle(
//        fontFamily = PretendardFontFamily,
//        fontWeight = FontWeight.SemiBold,
//        fontSize = 14.sp,
//        lineHeight = 20.sp,
//    ),
    headlineLarge = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 26.sp,
    ),
//    headlineSmall = TextStyle(
//        fontFamily = PretendardFontFamily,
//        fontWeight = FontWeight.Bold,
//        fontSize = 24.sp,
//        lineHeight = 32.sp,
//    ),
    bodyLarge = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
//    bodySmall = TextStyle(
//        fontFamily = PretendardFontFamily,
//        fontWeight = FontWeight.Normal,
//        fontSize = 12.sp,
//        lineHeight = 16.sp,
//    ),
    labelLarge = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
//    labelSmall = TextStyle(
//        fontFamily = PretendardFontFamily,
//        fontWeight = FontWeight.Medium,
//        fontSize = 11.sp,
//        lineHeight = 16.sp,
//    )
)