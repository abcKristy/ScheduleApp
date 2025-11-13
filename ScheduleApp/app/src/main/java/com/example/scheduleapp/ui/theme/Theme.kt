package com.example.scheduleapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

data class CustomColors(
    val bg1: Color,
    val bg2: Color,
    val shiny: Color,
    val botnav: Color,
    val searchBar: Color,
    val title: Color,
    val subTitle: Color
)

private val darkCustomColors = CustomColors(
    bg1 = gray,
    bg2 = darkGray,
    shiny = lightGreen,
    botnav = darkGray,
    searchBar = blue,
    title = white,
    subTitle = white
)

private val lightCustomColors = CustomColors(
    bg1 = white,
    bg2 = lightGray,
    shiny = blue,
    botnav = lightGray,
    searchBar = deepGreen,
    title = black,
    subTitle = gray
)

val LocalCustomColors = staticCompositionLocalOf { lightCustomColors }

@Composable
fun ScheduleAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val baseColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    val customColors = if (darkTheme) darkCustomColors else lightCustomColors

    MaterialTheme(
        colorScheme = baseColorScheme,
        typography = Typography
    ) {
        CompositionLocalProvider(
            LocalCustomColors provides customColors,
            content = content
        )
    }
}

val MaterialTheme.customColors: CustomColors
    @Composable get() = LocalCustomColors.current