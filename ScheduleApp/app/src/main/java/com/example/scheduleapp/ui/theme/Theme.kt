package com.example.scheduleapp.ui.theme

import android.app.Activity
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
    val subTitle: Color
)

// Темная тема с вашими названиями
private val darkCustomColors = CustomColors(
    bg1 = gray,
    bg2 = darkGray,
    shiny = lightGreen,
    botnav = darkGray,
    searchBar = blue,
    title = white,
    subTitle = white
)

// Светлая тема с вашими названиями
private val lightCustomColors = CustomColors(
    bg1 = white,
    bg2 = lightGray,
    shiny = blue,
    botnav = lightGray,
    searchBar = deepGreen,
    title = black,
    subTitle = gray
)

// CompositionLocal для доступа к кастомным цветам
val LocalCustomColors = staticCompositionLocalOf { lightCustomColors }

@Composable
fun ScheduleAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Базовые цвета Material Theme
    val baseColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    // Ваши кастомные цвета
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

// Extension для удобного доступа к кастомным цветам
val MaterialTheme.customColors: CustomColors
    @Composable get() = LocalCustomColors.current