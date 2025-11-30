//package com.example.scheduleapp.logic
//
//import android.Manifest
//import android.content.Context
//import android.content.pm.PackageManager
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.platform.LocalContext
//import androidx.core.content.ContextCompat
//
//@Composable
//fun rememberCameraPermissionManager(): CameraPermissionManager {
//    val context = LocalContext.current
//
//    val permissionLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { /* Обработка результата будет в вызывающем коде */ }
//
//    return remember {
//        CameraPermissionManager(context, permissionLauncher)
//    }
//}
//
//class CameraPermissionManager(
//    private val context: Context,
//    private val permissionLauncher: androidx.activity.result.ActivityResultLauncher<String>
//) {
//    fun checkCameraPermission(): Boolean {
//        return ContextCompat.checkSelfPermission(
//            context,
//            Manifest.permission.CAMERA
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    fun requestCameraPermission() {
//        permissionLauncher.launch(Manifest.permission.CAMERA)
//    }
//}