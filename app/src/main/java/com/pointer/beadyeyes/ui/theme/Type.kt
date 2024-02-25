package com.pointer.beadyeyes.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.pointer.beadyeyes.R


val pretendard_light = FontFamily(
    Font(R.font.pretendard_light)
)
val pretendard_thin = FontFamily(
    Font(R.font.pretendard_thin),
)
val pretendard_bold = FontFamily(
    Font(R.font.pretendard_bold),
)
val pretendard_regular = FontFamily(
    Font(R.font.pretendard_regular),
)
val pretendard_semibold = FontFamily(
    Font(R.font.pretendard_semibold),
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = pretendard_light,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
)
