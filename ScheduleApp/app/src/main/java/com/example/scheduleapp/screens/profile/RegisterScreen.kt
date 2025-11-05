package com.example.scheduleapp.screens.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, onNavigateBack: () -> Boolean) {
    Text(text = "RegisterScreen",
        modifier = Modifier.fillMaxSize().wrapContentHeight(),
        textAlign = TextAlign.Center)
}