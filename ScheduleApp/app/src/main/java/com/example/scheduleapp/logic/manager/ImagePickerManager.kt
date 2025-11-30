package com.example.scheduleapp.logic.manager

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImagePickerManager(private val context: Context) {

    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(null)
        return File.createTempFile(
            "AVATAR_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    fun getUriForFile(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "com.example.scheduleapp.fileprovider",
            file
        )
    }
}


@Composable
fun rememberImagePickerManager(): ImagePickerManager {
    val context = LocalContext.current
    return remember { ImagePickerManager(context) }
}