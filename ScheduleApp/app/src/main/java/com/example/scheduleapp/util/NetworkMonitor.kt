package com.example.scheduleapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import com.example.scheduleapp.workers.NetworkRetryWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object NetworkMonitor {

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _pendingGroups = mutableSetOf<String>()
    private var connectivityManager: ConnectivityManager? = null

    fun initialize(context: Context) {
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d("NetworkMonitor", "Сеть доступна")
                _isConnected.value = true

                if (_pendingGroups.isNotEmpty()) {
                    Log.d("NetworkMonitor", "Запуск отложенных загрузок для ${_pendingGroups.size} групп")
                    _pendingGroups.forEach { group ->
                        scheduleRetryForGroup(context, group)
                    }
                    _pendingGroups.clear()
                }
            }

            override fun onLost(network: Network) {
                Log.d("NetworkMonitor", "Сеть потеряна")
                _isConnected.value = false
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                val isValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

                _isConnected.value = hasInternet && isValidated
                Log.d("NetworkMonitor", "Статус сети: connected=${_isConnected.value}")
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        connectivityManager?.registerNetworkCallback(request, networkCallback)

        val activeNetwork = connectivityManager?.activeNetwork
        val capabilities = connectivityManager?.getNetworkCapabilities(activeNetwork)
        _isConnected.value = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    fun addPendingGroup(group: String) {
        _pendingGroups.add(group)
        Log.d("NetworkMonitor", "Группа добавлена в ожидание: $group, всего: ${_pendingGroups.size}")
    }

    fun removePendingGroup(group: String) {
        _pendingGroups.remove(group)
    }

    fun getPendingGroups(): Set<String> = _pendingGroups.toSet()

    private fun scheduleRetryForGroup(context: Context, group: String) {
        val workRequest = androidx.work.OneTimeWorkRequestBuilder<NetworkRetryWorker>()
            .setInputData(
                androidx.work.Data.Builder()
                    .putString(NetworkRetryWorker.KEY_GROUP, group)
                    .putBoolean(NetworkRetryWorker.KEY_FORCE_REFRESH, true)
                    .build()
            )
            .setConstraints(
                androidx.work.Constraints.Builder()
                    .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                    .build()
            )
            .addTag("${NetworkRetryWorker.WORK_NAME}_$group")
            .build()

        androidx.work.WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "${NetworkRetryWorker.WORK_NAME}_$group",
                androidx.work.ExistingWorkPolicy.REPLACE,
                workRequest
            )
    }
}