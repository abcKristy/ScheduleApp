package com.example.scheduleapp.screens.master

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.scheduleapp.R
import com.example.scheduleapp.data.state.AppState
import com.example.scheduleapp.logic.LocalThemeViewModel
import com.example.scheduleapp.navigation.NavigationRoute
import com.example.scheduleapp.screens.master.items.AnimatedShinyBottom
import com.example.scheduleapp.screens.master.items.AnimatedShinyTop
import com.example.scheduleapp.ui.theme.ScheduleAppTheme
import com.example.scheduleapp.ui.theme.blue
import com.example.scheduleapp.ui.theme.customColors
import com.example.scheduleapp.ui.theme.lightBlue
import com.example.scheduleapp.ui.theme.lightGray
import com.example.scheduleapp.ui.theme.lightGreen
import com.example.scheduleapp.ui.theme.white

@SuppressLint("UnrememberedMutableState")
@Composable
fun ScreenProfile(navController: NavHostController? = null) {
    val themeViewModel = LocalThemeViewModel.current
    val userName = AppState.userName
    val userGroup = AppState.userGroup
    val userEmail = AppState.userEmail
    val userAvatar = AppState.userAvatar

    val isDarkTheme = if (themeViewModel != null) {
        val themeState by themeViewModel.isDarkTheme.collectAsState()
        themeState
    } else {
        isSystemInDarkTheme()
    }
    println("DEBUG: ScreenProfile - isDarkTheme = $isDarkTheme, themeViewModel = $themeViewModel")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.customColors.bg1),
        contentAlignment = Alignment.Center
    ) {
        if (isDarkTheme) {
            AnimatedShinyTop(shiny = lightGreen, initialX = -190f, initialY = -400f)
            AnimatedShinyBottom(shiny = blue, initialX = 120f, initialY = 320f)
        } else {
            AnimatedShinyTop(shiny = lightGreen, initialX = -100f, initialY = -300f)
            AnimatedShinyBottom(shiny = blue, initialX = 100f, initialY = 300f)
        }
        Box(
            modifier = Modifier
                .width(350.dp)
                .height(500.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            lightGray.copy(0.4f),
                            lightGray.copy(0.4f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(
                            width = 3.dp,
                            color = Color.White,
                            shape = CircleShape
                        )
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    if (userAvatar != null) {
                        Image(
                            painter = rememberImagePainter(
                                ImageRequest.Builder(LocalContext.current)
                                    .data(userAvatar)
                                    .build()
                            ),
                            contentDescription = "Аватар пользователя",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Icon",
                            tint = Color.White,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

                Text(
                    text = userName,
                    color = white,
                    fontSize = 25.sp,
                    modifier = Modifier.padding(top = 16.dp),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = userGroup,
                    color = white,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 8.dp),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = white,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = userEmail,
                        color = white,
                        modifier = Modifier.padding(start = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                }

                val context = LocalContext.current
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .clickable {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                "https://online-edu.mirea.ru/login/index.php".toUri()
                            )
                            context.startActivity(intent)
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Website",
                        tint = white,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Перейти на сайт СДО",
                        color = if (isSystemInDarkTheme()){
                            lightBlue
                        }else{
                            Color(0xFF6578F7)
                        },
                        modifier = Modifier.padding(start = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(50.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clickable {
                            println("DEBUG: Toggle theme clicked")
                            themeViewModel?.toggleTheme(context)
                        },
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = if (isDarkTheme) painterResource(R.drawable.ic_light_mode) else painterResource(R.drawable.ic_dark_mode),
                        contentDescription = "Переключить тему",
                        tint = white,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isDarkTheme) "Переключить на светлую тему" else "Переключить на темную тему",
                        color = white,
                        modifier = Modifier.padding(start = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                }


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController?.navigate(NavigationRoute.UserSettings.route)
                        },
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = white,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Настройки",
                        color = white,
                        modifier = Modifier.padding(start = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Light Theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun ScreenProfileLightPreview() {
    ScheduleAppTheme(darkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ScreenProfile()
        }
    }
}

@Preview(
    name = "Dark Theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun ScreenProfileDarkPreview() {
    ScheduleAppTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ScreenProfile()
        }
    }
}