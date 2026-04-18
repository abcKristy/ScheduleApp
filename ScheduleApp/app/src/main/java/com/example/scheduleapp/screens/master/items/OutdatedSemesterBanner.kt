package com.example.scheduleapp.screens.master.items

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduleapp.data.state.AppState
import com.example.scheduleapp.ui.theme.customColors

@Composable
fun OutdatedSemesterBanner(
    cacheStatus: AppState.CacheStatus,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dismissed by remember { mutableStateOf(false) }

    if (dismissed || cacheStatus == AppState.CacheStatus.FRESH) {
        return
    }

    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (cacheStatus) {
                    AppState.CacheStatus.OUTDATED_SEMESTER -> Color(0xFFFFF3E0)
                    AppState.CacheStatus.EXPIRED -> Color(0xFFFFF9C4)
                    else -> MaterialTheme.customColors.searchItem.copy(alpha = 0.9f)
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when {
                        isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = Color(0xFFFF9800)
                            )
                        }
                        else -> {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Предупреждение",
                                tint = Color(0xFFFF9800),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = getBannerTitle(cacheStatus),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                        Text(
                            text = getBannerMessage(cacheStatus, isLoading),
                            fontSize = 12.sp,
                            color = Color(0xFFBF360C)
                        )
                    }
                }

                Row {
                    if (!isLoading && cacheStatus != AppState.CacheStatus.FRESH) {
                        IconButton(
                            onClick = onRefresh,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Обновить",
                                tint = Color(0xFFFF9800),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            dismissed = true
                            onDismiss()
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Закрыть",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SemesterInfoChip(
    semester: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.customColors.searchItem.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = semester,
            fontSize = 12.sp,
            color = MaterialTheme.customColors.title.copy(alpha = 0.7f)
        )
    }
}

private fun getBannerTitle(status: AppState.CacheStatus): String {
    return when (status) {
        AppState.CacheStatus.OUTDATED_SEMESTER -> "Новый семестр"
        AppState.CacheStatus.EXPIRED -> "Требуется обновление"
        AppState.CacheStatus.NO_CACHE -> "Загрузка данных"
        else -> "Информация"
    }
}

private fun getBannerMessage(status: AppState.CacheStatus, isLoading: Boolean): String {
    return when {
        isLoading -> "Обновление расписания..."
        status == AppState.CacheStatus.OUTDATED_SEMESTER -> "Показано расписание за прошлый семестр. Нажмите обновить."
        status == AppState.CacheStatus.EXPIRED -> "Данные устарели. Рекомендуется обновить."
        status == AppState.CacheStatus.NO_CACHE -> "Загрузка расписания с сервера..."
        else -> ""
    }
}