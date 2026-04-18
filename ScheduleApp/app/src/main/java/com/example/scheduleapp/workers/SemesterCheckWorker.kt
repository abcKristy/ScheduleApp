package com.example.scheduleapp.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.scheduleapp.data.database.ScheduleDatabase
import com.example.scheduleapp.data.database.ScheduleRepository
import com.example.scheduleapp.data.state.PreferencesManager
import com.example.scheduleapp.logic.getScheduleItemsWithCache
import com.example.scheduleapp.util.SemesterUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SemesterCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting semester check worker")

            val database = ScheduleDatabase.getInstance(applicationContext)
            val repository = ScheduleRepository(database)

            val currentSemester = SemesterUtils.getCurrentSemester()
            val savedSemester = PreferencesManager.getLastKnownSemester(applicationContext)

            if (savedSemester != currentSemester) {
                Log.d(TAG, "Semester changed: $savedSemester -> $currentSemester")
                PreferencesManager.saveLastKnownSemester(applicationContext, currentSemester)

                val cachedGroups = repository.getAllCachedGroupsInfo()
                Log.d(TAG, "Found ${cachedGroups.size} cached groups to check")

                var updatedCount = 0
                var errorCount = 0

                cachedGroups.forEach { groupInfo ->
                    try {
                        if (groupInfo.semester != currentSemester) {
                            Log.d(TAG, "Updating group: ${groupInfo.groupName}")

                            getScheduleItemsWithCache(
                                group = groupInfo.groupName,
                                repository = repository,
                                forceRefresh = true,
                                onSuccess = { items ->
                                    updatedCount++
                                    Log.d(TAG, "Successfully updated ${groupInfo.groupName}: ${items.size} items")
                                },
                                onError = { error ->
                                    errorCount++
                                    Log.e(TAG, "Failed to update ${groupInfo.groupName}: $error")
                                }
                            )
                        }
                    } catch (e: Exception) {
                        errorCount++
                        Log.e(TAG, "Error updating group ${groupInfo.groupName}", e)
                    }
                }

                Log.d(TAG, "Semester check completed: updated=$updatedCount, errors=$errorCount")
            } else {
                Log.d(TAG, "Semester unchanged: $currentSemester")
            }

            PreferencesManager.saveLastSemesterCheck(applicationContext, System.currentTimeMillis())

            Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "Error in semester check worker", e)
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "SemesterCheckWorker"
        const val WORK_NAME = "semester_check_worker"
    }
}