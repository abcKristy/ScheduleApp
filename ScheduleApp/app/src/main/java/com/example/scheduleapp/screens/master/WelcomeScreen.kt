package com.example.scheduleapp.screens.master

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduleapp.logic.LocalThemeViewModel
import com.example.scheduleapp.screens.master.items.AnimatedShinyBottom
import com.example.scheduleapp.screens.master.items.AnimatedShinyTop
import com.example.scheduleapp.ui.theme.ScheduleAppTheme
import com.example.scheduleapp.ui.theme.customColors
import com.example.scheduleapp.ui.theme.lightGreen
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onNavigateToMain: () -> Unit
) {
    val themeViewModel = LocalThemeViewModel.current

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
            AnimatedShinyTop(shiny = shiny, initialX = 0f, initialY = 0f)
            AnimatedShinyBottom(shiny = lightGreen, initialX = 180f, initialY = 520f)

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