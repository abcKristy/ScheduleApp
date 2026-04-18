package com.example.scheduleapp.screens.master.items

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduleapp.R
import com.example.scheduleapp.data.state.AppState
import com.example.scheduleapp.ui.theme.customColors
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HistoryItemWithStatus(
    query: String,
    onClick: () -> Unit,
    onRefresh: suspend () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var status by remember { mutableStateOf(AppState.CacheStatus.NO_CACHE) }
    var isLoading by remember { mutableStateOf(false) }
    val customColors = MaterialTheme.customColors
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(query) {
        status = AppState.checkGroupCacheFreshness(query)
    }

    val refreshAction = SwipeAction(
        icon = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .offset((-30).dp)
                    .background(
                        Color(0xFF4CAF50),
                        RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                Row {
                    Spacer(modifier = Modifier.width(35.dp))
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Обновить",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        },
        onSwipe = {
            coroutineScope.launch {
                isLoading = true
                try {
                    onRefresh()
                } catch (e: Exception) {
                } finally {
                    isLoading = false
                }
            }
        },
        background = Color.Transparent
    )

    val deleteAction = SwipeAction(
        icon = {
            Box(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth()
                    .offset((-30).dp)
                    .background(
                        Color(0xFFF44336),
                        RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                Row {
                    Spacer(modifier = Modifier.width(35.dp))
                    Icon(
                        painter = painterResource(R.drawable.ic_del),
                        contentDescription = "Удалить",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        onSwipe = onDelete,
        background = Color.Transparent
    )

    SwipeableActionsBox(
        startActions = listOf(refreshAction),
        endActions = listOf(deleteAction),
        swipeThreshold = 100.dp,
        backgroundUntilSwipeThreshold = Color.Transparent
    ) {
        Card(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            elevation = 4.dp,
            shape = RoundedCornerShape(20.dp),
            backgroundColor = MaterialTheme.customColors.bg2,
            border = BorderStroke(2.dp, customColors.searchItem)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GroupStatusIndicator(
                    status = status,
                    isLoading = isLoading,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = query,
                        color = MaterialTheme.customColors.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    val statusText = AppState.getCacheStatusMessage(status)
                    if (statusText.isNotEmpty() && status != AppState.CacheStatus.FRESH) {
                        Text(
                            text = statusText,
                            color = when (status) {
                                AppState.CacheStatus.EXPIRED -> Color(0xFFFFC107)
                                AppState.CacheStatus.OUTDATED_SEMESTER -> Color(0xFFFF9800)
                                AppState.CacheStatus.ERROR -> Color(0xFFF44336)
                                else -> Color.Gray
                            },
                            fontSize = 12.sp
                        )
                    }
                }

                Text(
                    text = "← →",
                    color = MaterialTheme.customColors.title.copy(alpha = 0.3f),
                    fontSize = 12.sp
                )
            }
        }
    }
}