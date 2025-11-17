package com.example.scheduleapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.scheduleapp.R

val GothicR = FontFamily(
    Font(R.font.gothic_regular)
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

val CustomTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = GothicR
    ),
    displayMedium = TextStyle(
        fontFamily = GothicR
    ),
    displaySmall = TextStyle(
        fontFamily = GothicR
    ),
)
