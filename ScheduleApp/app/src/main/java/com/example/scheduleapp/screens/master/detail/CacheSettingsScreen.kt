package com.example.scheduleapp.screens.master.detail

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.WorkManager
import com.example.scheduleapp.R
import com.example.scheduleapp.data.database.ScheduleDatabase
import com.example.scheduleapp.data.database.ScheduleRepository
import com.example.scheduleapp.data.state.AppState
import com.example.scheduleapp.data.state.PreferencesManager
import com.example.scheduleapp.ui.theme.ScheduleAppTheme
import com.example.scheduleapp.ui.theme.customColors
import com.example.scheduleapp.ui.theme.lightGray
import com.example.scheduleapp.ui.theme.white
import com.example.scheduleapp.util.SemesterUtils
import com.example.scheduleapp.workers.CacheCleanupWorker
import com.example.scheduleapp.workers.SemesterCheckWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
 * НЕИСПОЛЬЗУЕМЫЙ ФАЙЛ — ДЛЯ ОТЛАДКИ
 *
 * Этот экран был создан для тестирования и отладки системы кэширования.
 * В production-сборке не используется (удалён из навигации).
 *
 * Функционал:
 * - Просмотр статистики кэша (группы, занятия, размер БД)
 * - Очистка всего кэша
 * - Принудительное обновление всех групп
 * - Настройка TTL кэша (1-30 дней)
 * - Управление фоновыми задачами WorkManager
 *
 * КАК ПОДКЛЮЧИТЬ ОБРАТНО:
 * 1. В UserSettingsScreen.kt добавить пункт меню:
 *    SettingItem(
 *        iconPainter = painterResource(R.drawable.ic_cached),
 *        title = "Управление кэшем",
 *        value = "Очистка, автообновление",
 *        onClick = { navController?.navigate(NavigationRoute.CacheSettings.route) }
 *    )
 *
 * 2. В NavigationRoute.kt раскомментировать:
 *    object CacheSettings: NavigationRoute("cache_settings")
 *
 * 3. В MainNavGraph.kt добавить:
 *    composable(NavigationRoute.CacheSettings.route) {
 *        CacheSettingsScreen(onNavigateBack = { navController.popBackStack() })
 *    }
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CacheSettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val customColors = MaterialTheme.customColors

    var isLoading by remember { mutableStateOf(false) }
    var showClearAllDialog by remember { mutableStateOf(false) }
    var showRefreshAllDialog by remember { mutableStateOf(false) }

    var cachedGroupsCount by remember { mutableStateOf(0) }
    var cachedLessonsCount by remember { mutableStateOf(0) }
    var totalCacheSize by remember { mutableStateOf("0 KB") }

    var autoUpdateEnabled by remember { mutableStateOf(PreferencesManager.isAutoUpdateCacheEnabled(context)) }
    var cacheTtlDays by remember { mutableStateOf(PreferencesManager.getCacheTtlDays(context)) }
    var ttlExpanded by remember { mutableStateOf(false) }

    val currentSemester = SemesterUtils.getCurrentSemester()
    val semesterDisplayName = SemesterUtils.getDisplayName(currentSemester)

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val database = ScheduleDatabase.getInstance(context)
            val repository = ScheduleRepository(database)

            val groups = repository.getAllCachedGroupsInfo()
            cachedGroupsCount = groups.size


            cachedLessonsCount = repository.getTotalCachedLessons(currentSemester)

            totalCacheSize = formatCacheSize(groups.size, cachedLessonsCount)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Управление кэшем",
                        color = white,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = white
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.customColors.bg1)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoCard(
                currentSemester = semesterDisplayName,
                cachedGroups = cachedGroupsCount,
                cachedLessons = cachedLessonsCount,
                cacheSize = totalCacheSize
            )

            SettingsCard(
                autoUpdateEnabled = autoUpdateEnabled,
                onAutoUpdateChanged = { enabled ->
                    autoUpdateEnabled = enabled
                    PreferencesManager.setAutoUpdateCache(context, enabled)
                },
                cacheTtlDays = cacheTtlDays,
                ttlExpanded = ttlExpanded,
                onTtlExpandedChanged = { ttlExpanded = it },
                onTtlChanged = { days ->
                    cacheTtlDays = days
                    PreferencesManager.setCacheTtlDays(context, days)
                }
            )

            ActionsCard(
                isLoading = isLoading,
                onClearAllCache = {
                    showClearAllDialog = true
                },
                onRefreshAllGroups = {
                    showRefreshAllDialog = true
                }
            )

            WorkersInfoCard(
                onCancelWorkers = {
                    scope.launch {
                        WorkManager.getInstance(context).cancelAllWork()
                        Toast.makeText(context, "Фоновые задачи отменены", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }

    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            title = {
                Text(
                    text = "Очистить кэш?",
                    color = white,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Все сохраненные расписания будут удалены. История поиска сохранится.",
                    color = white
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClearAllDialog = false
                        scope.launch {
                            isLoading = true
                            withContext(Dispatchers.IO) {
                                val database = ScheduleDatabase.getInstance(context)
                                val repository = ScheduleRepository(database)
                                repository.performFullCleanup(currentSemester)
                            }
                            isLoading = false
                            Toast.makeText(context, "Кэш очищен", Toast.LENGTH_SHORT).show()

                            cachedGroupsCount = 0
                            cachedLessonsCount = 0
                            totalCacheSize = "0 KB"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    )
                ) {
                    Text("Очистить", color = white)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showClearAllDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray
                    )
                ) {
                    Text("Отмена", color = white)
                }
            },
            containerColor = MaterialTheme.customColors.dialogCont
        )
    }

    if (showRefreshAllDialog) {
        AlertDialog(
            onDismissRequest = { showRefreshAllDialog = false },
            title = {
                Text(
                    text = "Обновить все группы?",
                    color = white,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Будет запущено фоновое обновление всех кэшированных групп ($cachedGroupsCount шт.).",
                    color = white
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRefreshAllDialog = false
                        scope.launch {
                            val workRequest = androidx.work.OneTimeWorkRequestBuilder<SemesterCheckWorker>()
                                .build()
                            WorkManager.getInstance(context).enqueue(workRequest)
                            Toast.makeText(context, "Обновление запущено в фоне", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.customColors.shiny
                    )
                ) {
                    Text("Обновить", color = white)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showRefreshAllDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray
                    )
                ) {
                    Text("Отмена", color = white)
                }
            },
            containerColor = MaterialTheme.customColors.dialogCont
        )
    }
}

@Composable
private fun InfoCard(
    currentSemester: String,
    cachedGroups: Int,
    cachedLessons: Int,
    cacheSize: String
) {
    val customColors = MaterialTheme.customColors

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = lightGray.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_cached),
                    contentDescription = null,
                    tint = customColors.shiny,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Информация о кэше",
                    color = white,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(color = white.copy(alpha = 0.2f))

            InfoRow(label = "Текущий семестр", value = currentSemester)
            InfoRow(label = "Кэшировано групп", value = "$cachedGroups")
            InfoRow(label = "Всего занятий", value = "$cachedLessons")
            InfoRow(label = "Размер кэша", value = cacheSize)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = white.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = white,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsCard(
    autoUpdateEnabled: Boolean,
    onAutoUpdateChanged: (Boolean) -> Unit,
    cacheTtlDays: Int,
    ttlExpanded: Boolean,
    onTtlExpandedChanged: (Boolean) -> Unit,
    onTtlChanged: (Int) -> Unit
) {
    val customColors = MaterialTheme.customColors
    val ttlOptions = listOf(1, 3, 5, 7, 14, 30)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = lightGray.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Настройки",
                color = white,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider(color = white.copy(alpha = 0.2f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Автообновление кэша",
                    color = white,
                    fontSize = 14.sp
                )
                Switch(
                    checked = autoUpdateEnabled,
                    onCheckedChange = onAutoUpdateChanged,
                    thumbContent = if (autoUpdateEnabled) {
                        {
                            Icon(
                                painter = painterResource(R.drawable.ic_cached),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                        }
                    } else null
                )
            }

            ExposedDropdownMenuBox(
                expanded = ttlExpanded,
                onExpandedChange = { onTtlExpandedChanged(it) }
            ) {
                OutlinedTextField(
                    value = "$cacheTtlDays ${getDaysWord(cacheTtlDays)}",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ttlExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        focusedTextColor = white,
                        unfocusedTextColor = white,
                        focusedContainerColor = customColors.searchBar.copy(alpha = 0.3f),
                        unfocusedContainerColor = customColors.searchBar.copy(alpha = 0.3f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                ExposedDropdownMenu(
                    expanded = ttlExpanded,
                    onDismissRequest = { onTtlExpandedChanged(false) }
                ) {
                    ttlOptions.forEach { days ->
                        DropdownMenuItem(
                            text = {
                                Text("$days ${getDaysWord(days)}", color = white)
                            },
                            onClick = {
                                onTtlChanged(days)
                                onTtlExpandedChanged(false)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionsCard(
    isLoading: Boolean,
    onClearAllCache: () -> Unit,
    onRefreshAllGroups: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = lightGray.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Действия",
                color = white,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider(color = white.copy(alpha = 0.2f))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.customColors.shiny
                    )
                }
            } else {
                ActionButton(
                    icon = Icons.Default.Delete,
                    text = "Очистить кэш всех групп",
                    color = Color(0xFFF44336),
                    onClick = onClearAllCache
                )

                ActionButton(
                    icon = Icons.Default.Refresh,
                    text = "Обновить все группы сейчас",
                    color = MaterialTheme.customColors.shiny,
                    onClick = onRefreshAllGroups
                )
            }
        }
    }
}

@Composable
private fun WorkersInfoCard(
    onCancelWorkers: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = lightGray.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Фоновые задачи",
                color = white,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider(color = white.copy(alpha = 0.2f))

            Text(
                text = "WorkManager управляет фоновым обновлением кэша. Вы можете отменить все запланированные задачи.",
                color = white.copy(alpha = 0.7f),
                fontSize = 13.sp
            )

            Button(
                onClick = onCancelWorkers,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Отменить фоновые задачи", color = white)
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = white,
            fontSize = 15.sp
        )
    }
}

private fun getDaysWord(days: Int): String {
    return when {
        days % 10 == 1 && days % 100 != 11 -> "день"
        days % 10 in 2..4 && days % 100 !in 12..14 -> "дня"
        else -> "дней"
    }
}

private fun formatCacheSize(groups: Int, lessons: Int): String {
    val estimatedSize = groups * 2L + lessons * 10L
    return when {
        estimatedSize > 1024 -> "${estimatedSize / 1024} MB"
        else -> "$estimatedSize KB"
    }
}

@Preview(
    name = "Light Theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun CacheSettingsScreenLightPreview() {
    ScheduleAppTheme(darkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            CacheSettingsScreen(onNavigateBack = {})
        }
    }
}

@Preview(
    name = "Dark Theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun CacheSettingsScreenDarkPreview() {
    ScheduleAppTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            CacheSettingsScreen(onNavigateBack = {})
        }
    }
}