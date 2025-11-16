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

// Ваши кастомные цвета
data class CustomColors(
    val bg1: Color,
    val bg2: Color,
    val shiny: Color,
    val botnav: Color,
    val searchBar: Color,
    val title: Color,
    val subTitle: Color,
    val dialogCont: Color,
    val searchItem: Color
)

private val darkCustomColors = CustomColors(
    bg1 = gray,
    bg2 = darkGray,
    shiny = lightGreen,
    botnav = darkGray,
    searchBar = blue,
    title = white,
    subTitle = white,
    dialogCont = deepGreen,
    searchItem = gray
)

private val lightCustomColors = CustomColors(
    bg1 = white,
    bg2 = white,
    shiny = blue,
    botnav = lightGray,
    searchBar = deepGreen,
    title = black,
    subTitle = gray,
    dialogCont = blue,
    searchItem = whiteGray
)

val LocalCustomColors = staticCompositionLocalOf { lightCustomColors }

// Material 3 Color Schemes
private val DarkColorScheme = darkColorScheme(
    primary = purple80,
    secondary = purpleGrey80,
    tertiary = pink80
)

private val LightColorScheme = lightColorScheme(
    primary = purple40,
    secondary = purpleGrey40,
    tertiary = pink40
)

@Composable
fun ScheduleAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val dynamicColor = dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val customColors = if (darkTheme) darkCustomColors else lightCustomColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = {
            CompositionLocalProvider(
                LocalCustomColors provides customColors,
                content = content
            )
        }
    )
}

val MaterialTheme.customColors: CustomColors
    @Composable get() = LocalCustomColors.current