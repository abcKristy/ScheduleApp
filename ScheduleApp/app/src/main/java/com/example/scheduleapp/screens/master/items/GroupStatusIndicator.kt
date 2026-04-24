package com.example.scheduleapp.screens.master.items

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.scheduleapp.R
import com.example.scheduleapp.data.state.AppState

@Composable
fun GroupStatusIndicator(
    status: AppState.CacheStatus,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(20.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                val infiniteTransition = rememberInfiniteTransition(label = "loading")
                val rotation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 800, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "rotation"
                )

                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = Color(0xFFFF9800)
                )
            }
            else -> {
                val iconInfo = getStatusIcon(status)
                when {
                    iconInfo == null -> {
                    }
                    iconInfo.iconRes != null -> {
                        Icon(
                            painter = painterResource(id = iconInfo.iconRes),
                            contentDescription = iconInfo.description,
                            tint = iconInfo.color,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    iconInfo.imageVector != null -> {
                        Icon(
                            imageVector = iconInfo.imageVector,
                            contentDescription = iconInfo.description,
                            tint = iconInfo.color,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

private data class StatusIconInfo(
    val iconRes: Int? = null,
    val imageVector: androidx.compose.ui.graphics.vector.ImageVector? = null,
    val color: Color,
    val description: String
)

private fun getStatusIcon(status: AppState.CacheStatus): StatusIconInfo? {
    return when (status) {
        AppState.CacheStatus.FRESH -> null
        AppState.CacheStatus.EXPIRED -> StatusIconInfo(
            imageVector = Icons.Default.Warning,
            color = Color(0xFFFFC107),
            description = "Требует обновления"
        )
        AppState.CacheStatus.OUTDATED_SEMESTER -> StatusIconInfo(
            imageVector = Icons.Default.Warning,
            color = Color(0xFFFF9800),
            description = "Устарело"
        )
        AppState.CacheStatus.ERROR -> StatusIconInfo(
            iconRes = R.drawable.ic_error,
            color = Color(0xFFF44336),
            description = "Ошибка"
        )
        AppState.CacheStatus.NO_CACHE -> StatusIconInfo(
            iconRes = R.drawable.ic_error,
            color = Color.Gray,
            description = "Нет данных"
        )
    }
}