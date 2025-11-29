package com.example.scheduleapp.screens.master.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.scheduleapp.R
import com.example.scheduleapp.data.entity.ScheduleItem
import com.example.scheduleapp.data.entity.TestSchedule
import com.example.scheduleapp.data.state.AppState
import com.example.scheduleapp.screens.master.items.AnimatedShinyBottom
import com.example.scheduleapp.ui.theme.ScheduleAppTheme
import com.example.scheduleapp.ui.theme.blue
import com.example.scheduleapp.ui.theme.customColors
import com.example.scheduleapp.ui.theme.lightGray
import com.example.scheduleapp.ui.theme.lightGreen
import com.example.scheduleapp.ui.theme.pink40
import com.example.scheduleapp.ui.theme.pink80
import kotlinx.coroutines.delay
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDetailScreen(
    onNavigateBack: () -> Boolean,
    navController: NavController? = null
) {
    val selectedScheduleItem = AppState.selectedScheduleItem
    val scheduleItem = selectedScheduleItem ?: getDefaultScheduleItem()
    var showHint by remember { mutableStateOf(false) }

    val backgroundImage = when (scheduleItem.lessonType.uppercase()) {
        "LECTURE", "ЛЕКЦИЯ", "LK" -> R.drawable.bg_lk
        else -> R.drawable.bg_pr
    }

    val lessonNumber = getLessonNumber(scheduleItem.startTime)
    val lessonTypeText = getLessonTypeText(scheduleItem.lessonType)
    val circleColor = when (scheduleItem.lessonType.uppercase()) {
        "LECTURE", "ЛЕКЦИЯ", "LK" -> lightGreen
        "PRACTICE", "ПРАКТИКА","PR" -> blue
        "LAB", "ЛАБОРАТОРНАЯ" -> pink40
        else -> pink80
    }

    var swipeHandled by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    if (!swipeHandled && dragAmount > 30) {
                        swipeHandled = true
                        onNavigateBack()
                    }
                    change.consume()
                }
            }
    ) {
        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = "Фон занятия",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 255.dp)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(MaterialTheme.colorScheme.background)
        )

        when (scheduleItem.lessonType.uppercase()) {
            "LECTURE", "ЛЕКЦИЯ", "LK" -> AnimatedShinyBottom(shiny = lightGreen, 180f, 650f, shouldMove = false)
            "PRACTICE", "ПРАКТИКА","PR" -> AnimatedShinyBottom(shiny = blue, 200f, 630f, shouldMove = false)
            "LAB", "ЛАБОРАТОРНАЯ" -> AnimatedShinyBottom(shiny = pink40, 200f, 630f, shouldMove = false)
            else ->  AnimatedShinyBottom(shiny = pink80, 200f, 630f, shouldMove = false)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 255.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, end = 16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(
                    onClick = { showHint = !showHint },
                    modifier = Modifier
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Показать подсказку",
                        tint = MaterialTheme.customColors.title,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = scheduleItem.discipline,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp,
                color = MaterialTheme.customColors.title,
                textAlign = TextAlign.Center,
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(circleColor)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = lessonTypeText,
                    fontWeight = FontWeight.Medium,
                    color = lightGray
                )

                Spacer(modifier = Modifier.size(16.dp))

                Text(
                    text = "${scheduleItem.formattedStartTime}-${scheduleItem.formattedEndTime}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.customColors.title
                )

                Spacer(modifier = Modifier.size(16.dp))

                Text(
                    text = "$lessonNumber пара",
                    fontWeight = FontWeight.Medium,
                    color = lightGray
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                OutlineButton(
                    text = scheduleItem.room,
                    onClick = {
                        AppState.setCurrentGroupAndNavigate(scheduleItem.room)
                        navController?.popBackStack()
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlineButton(
                    text = scheduleItem.teacher,
                    onClick = {
                        AppState.setCurrentGroupAndNavigate(scheduleItem.teacher)
                        navController?.popBackStack()
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                GroupsRow(
                    groups = scheduleItem.groups,
                    onGroupClick = { group ->
                        AppState.setCurrentGroupAndNavigate(group)
                        navController?.popBackStack()
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                scheduleItem.description?.let { description ->
                    if (description.isNotBlank()) {
                        InfoCard(
                            title = "Описание",
                            value = description
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        AnimatedVisibility(
            visible = showHint,
            enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                animationSpec = tween(300),
                initialOffsetY = { -it }
            ),
            exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
                animationSpec = tween(300),
                targetOffsetY = { -it }
            ),
            modifier = Modifier
                .wrapContentSize()
                .padding(top = 295.dp, end = 16.dp)
                .wrapContentWidth()
                .align(Alignment.TopEnd)
        ) {
            Card(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Свайпните вниз, чтобы выйти",
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun OutlineButton(
    text: String,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier
            .wrapContentWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(50.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.customColors.subTitle
        )
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 12.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GroupsRow(
    groups: List<String>,
    onGroupClick: (String) -> Unit
) {
    val rows = groups.chunked(3)

    Column {
        rows.forEach { rowGroups ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Start
            ) {
                rowGroups.forEach { group ->
                    OutlineButton(
                        text = group,
                        onClick = { onGroupClick(group) }
                    )
                    if (group != rowGroups.last()) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun getLessonNumber(startTime: LocalDateTime): Int {
    val hour = startTime.hour
    return when (hour) {
        8, 9 -> 1
        10, 11 -> 2
        12, 13 -> 3
        14, 15 -> 4
        16, 17 -> 5
        18, 19 -> 6
        else -> 1
    }
}

private fun getLessonTypeText(lessonType: String): String {
    return when (lessonType.uppercase()) {
        "LECTURE", "ЛЕКЦИЯ" -> "Лекция"
        "PRACTICE", "ПРАКТИКА" -> "Практика"
        "LAB", "ЛАБОРАТОРНАЯ" -> "Лабораторная"
        else -> lessonType
    }
}

private fun getDefaultScheduleItem(): ScheduleItem {
    return TestSchedule().firstOrNull() ?: ScheduleItem(
        discipline = "Разработка мобильных приложений на Android",
        lessonType = "LECTURE",
        startTime = LocalDateTime.of(2025, 9, 6, 9, 0),
        endTime = LocalDateTime.of(2025, 9, 6, 10, 30),
        room = "А-15 (В-78)",
        teacher = "Иванов Петр Сергеевич",
        groups = listOf("ИКБО-60-23", "ИКБО-61-23", "ИКБО-62-23"),
        groupsSummary = "ИКБО-60-23, ИКБО-61-23, ИКБО-62-23",
        description = "Введение в разработку мобильных приложений. Основные концепции и архитектурные паттерны, используемые в современной мобильной разработке.",
        recurrence = null,
        exceptions = emptyList()
    )
}

@Preview(showBackground = true)
@Composable
fun ScheduleDetailScreenPreview() {
    ScheduleAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ScheduleDetailScreen(onNavigateBack = { true })
        }
    }
}