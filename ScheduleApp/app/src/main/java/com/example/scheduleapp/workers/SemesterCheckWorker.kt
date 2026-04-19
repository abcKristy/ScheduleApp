package com.example.scheduleapp.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.scheduleapp.data.database.CachedGroupEntity
import com.example.scheduleapp.data.database.ScheduleDatabase
import com.example.scheduleapp.data.database.ScheduleRepository
import com.example.scheduleapp.data.state.PreferencesManager
import com.example.scheduleapp.logic.getScheduleItemsWithCache
import com.example.scheduleapp.util.SemesterUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class SemesterCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "========== ЗАПУСК ФОНОВОГО ОБНОВЛЕНИЯ ГРУПП ==========")
            val startTime = System.currentTimeMillis()

            val database = ScheduleDatabase.getInstance(applicationContext)
            val repository = ScheduleRepository(database)

            val currentSemester = SemesterUtils.getCurrentSemester()
            val savedSemester = PreferencesManager.getLastKnownSemester(applicationContext)

            if (savedSemester != currentSemester) {
                Log.d(TAG, "Семестр изменился: $savedSemester -> $currentSemester")
                PreferencesManager.saveLastKnownSemester(applicationContext, currentSemester)
            }

            val allCachedGroups = repository.getAllCachedGroupsInfo()
            Log.d(TAG, "Найдено ${allCachedGroups.size} кэшированных групп")

            if (allCachedGroups.isEmpty()) {
                Log.d(TAG, "Нет групп для обновления")
                return@withContext Result.success()
            }

            val prioritizedGroups = prioritizeGroups(allCachedGroups)
            Log.d(TAG, "Группы после приоритизации: ${prioritizedGroups.size}")

            val groupsToUpdate = prioritizedGroups.filter { groupInfo ->
                groupInfo.semester != currentSemester || isCacheExpired(groupInfo)
            }

            Log.d(TAG, "Требуют обновления: ${groupsToUpdate.size} групп")

            if (groupsToUpdate.isEmpty()) {
                Log.d(TAG, "Все группы актуальны")
                return@withContext Result.success()
            }

            val batchSize = 3
            var updatedCount = 0
            var errorCount = 0

            groupsToUpdate.chunked(batchSize).forEachIndexed { batchIndex, batch ->
                Log.d(TAG, "Обработка пакета ${batchIndex + 1}/${(groupsToUpdate.size + batchSize - 1) / batchSize}, групп: ${batch.size}")

                batch.forEach { groupInfo ->
                    try {
                        Log.d(TAG, "Обновление группы: ${groupInfo.groupName}")

                        var success = false
                        var errorMessage: String? = null

                        getScheduleItemsWithCache(
                            context = applicationContext,
                            group = groupInfo.groupName,
                            repository = repository,
                            forceRefresh = true,
                            onSuccess = { items ->
                                success = true
                                Log.d(TAG, "✅ ${groupInfo.groupName}: загружено ${items.size} занятий")
                            },
                            onError = { error ->
                                errorMessage = error
                                Log.e(TAG, "❌ ${groupInfo.groupName}: $error")
                            }
                        )

                        if (success) {
                            updatedCount++
                        } else {
                            errorCount++
                        }

                    } catch (e: Exception) {
                        errorCount++
                        Log.e(TAG, "Ошибка обновления ${groupInfo.groupName}", e)
                    }
                }

                if (batchIndex < groupsToUpdate.chunked(batchSize).size - 1) {
                    delay(2000)
                }
            }

            PreferencesManager.saveLastSemesterCheck(applicationContext, System.currentTimeMillis())

            val duration = System.currentTimeMillis() - startTime
            Log.d(TAG, "========== ОБНОВЛЕНИЕ ЗАВЕРШЕНО ==========")
            Log.d(TAG, "Успешно: $updatedCount, ошибок: $errorCount, время: ${duration}ms")

            if (updatedCount == 0 && errorCount > 0) {
                Result.retry()
            } else {
                Result.success()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Критическая ошибка в worker", e)
            Result.retry()
        }
    }

    private fun prioritizeGroups(groups: List<CachedGroupEntity>): List<CachedGroupEntity> {
        val currentGroup = PreferencesManager.getCurrentGroup(applicationContext)
        val userGroup = PreferencesManager.getUserGroup(applicationContext)

        val priorityGroups = mutableSetOf<String>()
        if (currentGroup.isNotBlank() && currentGroup != " ") {
            priorityGroups.add(currentGroup)
        }
        if (userGroup.isNotBlank() && userGroup != "не задано") {
            priorityGroups.add(userGroup)
        }

        val sorted = groups.sortedWith(compareByDescending<CachedGroupEntity> { group ->
            when {
                priorityGroups.contains(group.groupName) -> 3
                else -> 1
            }
        }.thenByDescending { it.lastAccessed })

        Log.d(TAG, "Приоритетные группы: $priorityGroups")
        sorted.forEachIndexed { index, group ->
            val priority = when {
                priorityGroups.contains(group.groupName) -> "ВЫСОКИЙ"
                else -> "ОБЫЧНЫЙ"
            }
            Log.d(TAG, "  ${index + 1}. ${group.groupName} ($priority, доступ: ${group.lastAccessed})")
        }

        return sorted
    }

    private fun isCacheExpired(groupInfo: CachedGroupEntity): Boolean {
        val cacheTTL = 7 * 24 * 60 * 60 * 1000L
        val currentTime = System.currentTimeMillis()
        return (currentTime - groupInfo.cachedAt) > cacheTTL
    }

    companion object {
        private const val TAG = "SemesterCheckWorker"
        const val WORK_NAME = "semester_check_worker"
        const val PERIODIC_WORK_NAME = "semester_periodic_check"
    }
}