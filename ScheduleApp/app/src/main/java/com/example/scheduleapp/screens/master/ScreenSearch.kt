package com.example.scheduleapp.screens.master

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduleapp.R
import com.example.scheduleapp.data.state.AppState
import com.example.scheduleapp.logic.LocalThemeViewModel
import com.example.scheduleapp.data.state.SearchHistoryManager
import com.example.scheduleapp.logic.getScheduleItemsWithCache
import com.example.scheduleapp.ui.theme.ScheduleAppTheme
import com.example.scheduleapp.ui.theme.blue
import com.example.scheduleapp.ui.theme.customColors
import com.example.scheduleapp.ui.theme.lightGreen
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun ScreenSearch() {
    val themeViewModel = LocalThemeViewModel.current

    val isDarkTheme = if (themeViewModel != null) {
        val themeState by themeViewModel.isDarkTheme.collectAsState()
        themeState
    } else {
        isSystemInDarkTheme()
    }

    ScheduleAppTheme(darkTheme = isDarkTheme) {
        val customColors = MaterialTheme.customColors
        val context = LocalContext.current
        var searchQuery by remember { mutableStateOf("") }
        val searchHistory = SearchHistoryManager.historyList
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            val userGroup = AppState.userGroup
            AppState.repository?.getAllCachedGroups()
            if (userGroup != "не задано" && userGroup.isNotBlank() && AppState.currentGroup != userGroup) {
                AppState.setCurrentGroup(userGroup)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(customColors.bg2)
        ) {
            if (isDarkTheme) {
                ShinyBottom(shiny = blue,200,630)
            } else {
                ShinyBottom(shiny = lightGreen,180,520)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                        .height(60.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            clip = true,
                            ambientColor = customColors.searchBar,
                            spotColor = customColors.searchBar
                        )
                        .border(
                            border = BorderStroke(
                                width = 3.dp,
                                color = customColors.searchBar
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = customColors.searchBar,
                        textColor = MaterialTheme.customColors.title,
                        backgroundColor = customColors.bg2,
                        trailingIconColor = customColors.searchBar
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (searchQuery.isNotBlank()) {
                                    val trimmedQuery = searchQuery.trimEnd()
                                    AppState.setCurrentGroup(trimmedQuery)
                                    coroutineScope.launch {
                                        loadScheduleData(context, trimmedQuery)
                                    }
                                    searchQuery = ""
                                }
                            },
                            enabled = searchQuery.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "search",
                                modifier = Modifier.size(24.dp),
                                tint = customColors.searchBar
                            )
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (AppState.currentGroup.isNotBlank()) {
                    Text(
                        text = "Текущая группа: ${AppState.currentGroup}",
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = MaterialTheme.customColors.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                if (searchHistory.isNotEmpty()) {
                    Text(
                        text = "Последние запросы:",
                        modifier = Modifier.padding(bottom = 8.dp),
                        color = MaterialTheme.customColors.title
                    )

                    LazyColumn {
                        items(searchHistory) { historyItem ->
                            val deleteAction = SwipeAction(
                                onSwipe = {
                                    SearchHistoryManager.removeFromHistory(context, historyItem)
                                },
                                icon = {
                                    Box(
                                        modifier = Modifier
                                            .height(48.dp)
                                            .fillMaxWidth()
                                            .offset((-30).dp)
                                            .background(MaterialTheme.customColors.searchBar,
                                                RoundedCornerShape(20.dp)),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Row{
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
                                background = Color.Transparent
                            )

                            SwipeableActionsBox(
                                endActions = listOf(deleteAction),
                                swipeThreshold = 100.dp,
                                backgroundUntilSwipeThreshold = Color.Transparent
                            ) {
                                HistoryItem(
                                    query = historyItem,
                                    onClick = {
                                        AppState.setCurrentGroup(historyItem)
                                        coroutineScope.launch {
                                            loadScheduleData(context, historyItem)
                                        }
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "search",
                                tint = MaterialTheme.customColors.title,
                                modifier = Modifier.size(30.dp)
                            )
                            Text(
                                text = "История поиска пуста",
                                color = MaterialTheme.customColors.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HistoryItem(query: String, onClick: () -> Unit) {
    val customColors = MaterialTheme.customColors

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(20.dp),
        backgroundColor = MaterialTheme.customColors.bg2,
        border = BorderStroke(2.dp, customColors.searchItem)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = query,
                color = MaterialTheme.customColors.title,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ShinyBottom(shiny: Color,x:Int, y:Int){
    Canvas(
        modifier = Modifier
            .size(320.dp)
            .offset(x = (x).dp, y = (y).dp)
    ) {
        val radius = 320.dp.toPx()
        val colorList = if (shiny == blue) {
            listOf(
                shiny,
                shiny.copy(alpha = 0.95f),
                shiny.copy(alpha = 0.9f),
                shiny.copy(alpha = 0.8f),
                shiny.copy(alpha = 0.65f),
                shiny.copy(alpha = 0.42f),
                shiny.copy(alpha = 0.2f),
                Color.Transparent
            )
        } else {
            listOf(
                shiny,
                shiny.copy(alpha = 0.9f),
                shiny.copy(alpha = 0.8f),
                shiny.copy(alpha = 0.6f),
                shiny.copy(alpha = 0.4f),
                shiny.copy(alpha = 0.3f),
                shiny.copy(alpha = 0.15f),
                shiny.copy(alpha = 0.05f),
                Color.Transparent
            )
        }
        val brush = Brush.radialGradient(
            colors = colorList,
            center = center,
            radius = radius,
        )
        drawCircle(brush = brush, radius = radius)
    }
}

private suspend fun loadScheduleData(context: android.content.Context, group: String) {
    AppState.setLoading(true)
    AppState.setErrorMessage(null)

    getScheduleItemsWithCache(
        group = group,
        repository = AppState.repository,
        onSuccess = { items ->
            AppState.setLoading(false)
            if (items.isNotEmpty()) {
                AppState.setScheduleItems(items)
                SearchHistoryManager.addToHistory(context, group)
                Log.d("SCREEN_SEARCH", "Successfully loaded schedule for group: $group")
            } else {
                showToast(context, "Группа '$group' не найдена")
                AppState.setCurrentGroup("")
                Log.d("SCREEN_SEARCH", "No schedule found for group: $group")
            }
        },
        onError = { error ->
            AppState.setLoading(false)
            AppState.setErrorMessage(error)

            val errorMessage = if (error.contains("Нет данных в кэше")) {
                "Сервер недоступен и нет сохраненных данных для группы '$group'"
            } else {
                "Ошибка поиска: $error"
            }

            showToast(context, errorMessage)
            Log.e("SCREEN_SEARCH", "Error loading schedule: $error")
        }
    )
}

private fun showToast(context: android.content.Context, message: String) {
    android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_LONG).show()
}

@Preview(
    name = "Пустая история - День",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun ScreenSearchEmptyDayPreview() {
    ScreenSearch()
}

@Preview(
    name = "Пустая история - Ночь",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun ScreenSearchEmptyNightPreview() {
    ScreenSearch()
}

@Preview(
    name = "С историей - День",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun ScreenSearchWithHistoryDayPreview() {
    val testHistory = listOf(
        "ИКБО-11-23",
        "ИКБО-12-23",
        "ИКБО-13-23",
        "Иван Иванов",
        "Петр Петров"
    )

    ScheduleAppTheme(darkTheme = false) {
        val customColors = MaterialTheme.customColors

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(customColors.bg2)
        ) {
            ShinyBottom(shiny = lightGreen,180,520)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                // Поисковая строка
                OutlinedTextField(
                    value = "ИКБО-11-23",
                    onValueChange = {},
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                        .height(60.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            clip = true,
                            ambientColor = customColors.searchBar,
                            spotColor = customColors.searchBar
                        )
                        .border(
                            border = BorderStroke(
                                width = 3.dp,
                                color = customColors.searchBar
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = customColors.searchBar,
                        textColor = MaterialTheme.customColors.title,
                        backgroundColor = customColors.bg2,
                        trailingIconColor = customColors.searchBar
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = {},
                            enabled = true
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "search",
                                modifier = Modifier.size(24.dp),
                                tint = customColors.searchBar
                            )
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Текущая группа: ИКБО-11-23",
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = MaterialTheme.customColors.title ,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Text(
                    text = "Последние запросы:",
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MaterialTheme.customColors.title
                )

                LazyColumn {
                    items(testHistory) { historyItem ->
                        HistoryItem(
                            query = historyItem,
                            onClick = {}
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    name = "С историей - Ночь",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun ScreenSearchWithHistoryNightPreview() {
    val testHistory = listOf(
        "ИКБО-11-23",
        "ИКБО-12-23",
        "ИКБО-13-23",
        "Иван Иванов",
        "Петр Петров"
    )

    ScheduleAppTheme(darkTheme = true) {
        val customColors = MaterialTheme.customColors

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(customColors.bg2)
        ) {
            ShinyBottom(shiny = blue,200,630)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                OutlinedTextField(
                    value = "ИКБО-11-23",
                    onValueChange = {},
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                        .height(60.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            clip = true,
                            ambientColor = customColors.searchBar,
                            spotColor = customColors.searchBar
                        )
                        .border(
                            border = BorderStroke(
                                width = 3.dp,
                                color = customColors.searchBar
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = customColors.searchBar,
                        textColor = MaterialTheme.customColors.title,
                        backgroundColor = customColors.bg2,
                        trailingIconColor = customColors.searchBar
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = {},
                            enabled = true
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "search",
                                modifier = Modifier.size(24.dp),
                                tint = customColors.searchBar
                            )
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Текущая группа: ИКБО-11-23",
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = MaterialTheme.customColors.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Text(
                    text = "Последние запросы:",
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MaterialTheme.customColors.title
                )

                LazyColumn {
                    items(testHistory) { historyItem ->
                        HistoryItem(
                            query = historyItem,
                            onClick = {}
                        )
                    }
                }
            }
        }
    }
}
