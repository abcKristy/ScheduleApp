package com.example.scheduleapp.screens.master

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.scheduleapp.R
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onNavigateToMain: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(5000)
        onNavigateToMain()
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.start_text_img),
            contentDescription = "App Logo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Preview
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(
        onNavigateToMain = {}
    )
}