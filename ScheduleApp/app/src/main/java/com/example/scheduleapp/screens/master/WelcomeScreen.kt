package com.example.scheduleapp.screens.master

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduleapp.ui.theme.ScheduleAppTheme
import com.example.scheduleapp.ui.theme.black
import com.example.scheduleapp.ui.theme.blue
import com.example.scheduleapp.ui.theme.customColors
import com.example.scheduleapp.ui.theme.gray
import com.example.scheduleapp.ui.theme.lightGreen
import kotlinx.coroutines.delay
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.scheduleapp.data.LocalThemeViewModel
import com.example.scheduleapp.ui.theme.GothicR

@Composable
fun WelcomeScreen(
    onNavigateToMain: () -> Unit
) {
    // Получаем themeViewModel через CompositionLocal
    val themeViewModel = LocalThemeViewModel.current

    // Используем тему из ViewModel
    val isDarkTheme = if (themeViewModel != null) {
        val themeState by themeViewModel.isDarkTheme.collectAsState()
        themeState
    } else {
        isSystemInDarkTheme()
    }

    LaunchedEffect(Unit) {
        delay(3000)
        onNavigateToMain()
    }

    ScheduleAppTheme(darkTheme = isDarkTheme) {
        val shiny = MaterialTheme.customColors.shiny
        Box(
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.customColors.bg1)
        ) {
            Canvas(
                modifier = Modifier
                    .size(320.dp)
                    .offset(x = (180).dp, y = (520).dp)
            ) {
                val radius = 320.dp.toPx()
                val colorList = if (shiny == blue) {
                    listOf(
                        shiny,
                        shiny.copy(alpha = 0.95f),
                        shiny.copy(alpha = 0.9f),
                        shiny.copy(alpha = 0.8f),
                        shiny.copy(alpha = 0.65f),
                        shiny.copy(alpha = 0.42f),
                        shiny.copy(alpha = 0.2f),
                        Color.Transparent
                    )
                } else {
                    listOf(
                        shiny,
                        shiny.copy(alpha = 0.85f),
                        shiny.copy(alpha = 0.7f),
                        shiny.copy(alpha = 0.5f),
                        shiny.copy(alpha = 0.35f),
                        shiny.copy(alpha = 0.2f),
                        shiny.copy(alpha = 0.1f),
                        shiny.copy(alpha = 0.05f),
                        Color.Transparent
                    )
                }
                val brush = Brush.radialGradient(
                    colors = colorList,
                    center = center,
                    radius = radius,
                )
                drawCircle(brush = brush, radius = radius)
            }

            Canvas(
                modifier = Modifier
                    .size(320.dp)
                    .offset(x = (-70).dp, y = (-40).dp)
            ) {
                val radius = 320.dp.toPx()
                val colorList = listOf(
                    lightGreen,
                    lightGreen.copy(alpha = 0.9f),
                    lightGreen.copy(alpha = 0.8f),
                    lightGreen.copy(alpha = 0.7f),
                    lightGreen.copy(alpha = 0.5f),
                    lightGreen.copy(alpha = 0.3f),
                    lightGreen.copy(alpha = 0.2f),
                    lightGreen.copy(alpha = 0.1f),
                    Color.Transparent
                )
                val brush = Brush.radialGradient(
                    colors = colorList,
                    center = center,
                    radius = radius,
                )
                drawCircle(brush = brush, radius = radius)
            }

            Text(
                text = "STUDY",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.customColors.title,
                fontWeight = FontWeight.Bold,
                fontSize = 60.sp,
                modifier = Modifier.align(Alignment.Center)
                    .padding(bottom = 100.dp)
            )
            Text(
                text = "BY KRIS",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.customColors.subTitle.copy(alpha = 0.55f),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp)
            )
            Text(
                text = "2025",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.customColors.subTitle.copy(alpha = 0.55f),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp)
            )
        }
    }
}

@Preview(name = "Day Theme")
@Composable
fun WelcomeScreenDayPreview() {
    ScheduleAppTheme {
        Surface {
            WelcomeScreen(onNavigateToMain = {})
        }
    }
}

@Preview(
    name = "Night Theme",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun WelcomeScreenNightPreview() {
    ScheduleAppTheme {
        Surface {
            WelcomeScreen(onNavigateToMain = {})
        }
    }
}