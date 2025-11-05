package com.example.scheduleapp.screens.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateBack: () -> Boolean
) {
    Text(text = "LoginScreen",
        modifier = Modifier.fillMaxSize().wrapContentHeight(),
        textAlign = TextAlign.Center)
}