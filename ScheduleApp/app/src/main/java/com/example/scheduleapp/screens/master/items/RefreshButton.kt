package com.example.scheduleapp.screens.master.items

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.scheduleapp.ui.theme.customColors

@Composable
fun RefreshButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val customColors = MaterialTheme.customColors

    Box(
        modifier = modifier
            .size(40.dp)
            .background(
                color = customColors.searchItem.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .clickable(enabled = !isLoading) { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            val infiniteTransition = rememberInfiniteTransition(label = "refresh_rotation")
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "rotation"
            )

            CircularProgressIndicator(
                modifier = Modifier
                    .size(20.dp)
                    .rotate(rotation),
                strokeWidth = 2.dp,
                color = customColors.shiny
            )
        } else {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Обновить расписание",
                tint = customColors.title,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}