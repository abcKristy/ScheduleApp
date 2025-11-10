package com.example.scheduleapp.screens.master

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduleapp.data.AppState
import com.example.scheduleapp.data.SearchHistoryManager
import com.example.scheduleapp.logic.getScheduleItems
import com.example.scheduleapp.ui.theme.darkGray
import com.example.scheduleapp.ui.theme.deepGreen
import com.example.scheduleapp.ui.theme.gray
import com.example.scheduleapp.ui.theme.lightGray
import com.example.scheduleapp.ui.theme.lightGreen

@Composable
fun ScreenSearch() {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val searchHistory = SearchHistoryManager.historyList

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        // Поисковая строка
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
                    ambientColor = deepGreen,
                    spotColor = deepGreen
                ),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = lightGreen,
                unfocusedBorderColor = deepGreen,
                cursorColor = deepGreen,
                textColor = darkGray,
                backgroundColor = lightGray,
                trailingIconColor = deepGreen
            ),
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (searchQuery.isNotBlank()) {
                            AppState.setCurrentGroup(searchQuery)
                            SearchHistoryManager.addToHistory(searchQuery)
                            loadScheduleData(context, searchQuery)
                            searchQuery = ""
                        }
                    },
                    enabled = searchQuery.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "search",
                        modifier = Modifier.size(24.dp),
                        tint = deepGreen
                    )
                }
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Отображаем текущую выбранную группу
        if (AppState.currentGroup.isNotBlank()) {
            Text(
                text = "Текущая группа: ${AppState.currentGroup}",
                modifier = Modifier.padding(bottom = 16.dp),
                color = deepGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        // История поиска
        if (searchHistory.isNotEmpty()) {
            Text(
                text = "Последние запросы:",
                modifier = Modifier.padding(bottom = 8.dp),
                color = deepGreen
            )

            LazyColumn {
                items(searchHistory) { historyItem ->
                    HistoryItem(
                        query = historyItem,
                        onClick = {
                            AppState.setCurrentGroup(historyItem)
                            loadScheduleData(context, historyItem)
                        }
                    )
                }
            }
        } else {
            // Сообщение когда история пуста
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
                        tint = deepGreen
                    )
                    Text(
                        text = "История поиска пуста",
                        color = deepGreen
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HistoryItem(query: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(20.dp),
        backgroundColor = gray,
        border = BorderStroke(3.dp, lightGray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = query,
                color = deepGreen,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun loadScheduleData(context: android.content.Context, group: String) {
    AppState.setLoading(true)
    AppState.setErrorMessage(null)

    getScheduleItems(
        context = context,
        group = group,
        onSuccess = { items ->
            AppState.setScheduleItems(items)
            AppState.setLoading(false)
        },
        onError = { error ->
            AppState.setErrorMessage(error)
            AppState.setLoading(false)
        }
    )
}

@Composable
@Preview(name = "Пустая история поиска")
fun ScreenSearchEmptyPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gray)
        ) {
            ScreenSearch()
        }
    }
}

@Composable
@Preview(name = "С историей поиска")
fun ScreenSearchWithHistoryPreview() {
    // Временно добавляем тестовые данные в историю для превью
    val testHistory = listOf(
        "ИКБО-11-23",
        "ИКБО-12-23",
        "ИКБО-13-23",
        "Иван Иванов",
        "Петр Петров"
    )

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gray)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                // Поисковая строка
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                        .height(60.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            clip = true,
                            ambientColor = deepGreen,
                            spotColor = deepGreen
                        ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = lightGreen,
                        unfocusedBorderColor = deepGreen,
                        cursorColor = deepGreen,
                        textColor = darkGray,
                        backgroundColor = lightGray,
                        trailingIconColor = deepGreen
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = {},
                            enabled = false
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "search",
                                modifier = Modifier.size(24.dp),
                                tint = deepGreen
                            )
                        }
                    },
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "Введите название группы или имя преподавателя",
                            color = lightGray
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Текущая группа
                Text(
                    text = "Текущая группа: ИКБО-11-23",
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = deepGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                // История поиска
                Text(
                    text = "Последние запросы:",
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = deepGreen
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