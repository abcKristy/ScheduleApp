package com.example.scheduleapp.screens.master.detail

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.scheduleapp.data.state.AppState
import com.example.scheduleapp.logic.manager.rememberImagePickerManager
import com.example.scheduleapp.screens.master.dialogs.AvatarPickerDialog
import com.example.scheduleapp.screens.master.dialogs.EditDialog
import com.example.scheduleapp.screens.master.items.AnimatedShinyBottom
import com.example.scheduleapp.screens.master.items.AnimatedShinyTop
import com.example.scheduleapp.ui.theme.ScheduleAppTheme
import com.example.scheduleapp.ui.theme.blue
import com.example.scheduleapp.ui.theme.customColors
import com.example.scheduleapp.ui.theme.lightGray
import com.example.scheduleapp.ui.theme.lightGreen
import com.example.scheduleapp.ui.theme.white
import java.io.File

@Composable
fun UserSettingsScreen(
    onNavigateBack: () -> Unit
) {
    var showNameDialog by remember { mutableStateOf(false) }
    var showGroupDialog by remember { mutableStateOf(false) }
    var showEmailDialog by remember { mutableStateOf(false) }
    var showAvatarPicker by remember { mutableStateOf(false) }
    var showCameraPermissionDialog by remember { mutableStateOf(false) }

    val currentName = AppState.userName
    val currentGroup = AppState.userGroup
    val currentEmail = AppState.userEmail
    val currentAvatar = AppState.userAvatar

    val context = LocalContext.current
    val imagePickerManager = rememberImagePickerManager()

    var tempFile by remember { mutableStateOf<File?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val avatarPath = saveImageFromUri(context, it)
            AppState.setUserAvatar(avatarPath)
        }
        showAvatarPicker = false
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempFile?.let { file ->
                AppState.setUserAvatar(file.absolutePath)
            }
        }
        showAvatarPicker = false
    }

    fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val file = imagePickerManager.createImageFile()
            tempFile = file
            cameraLauncher.launch(Uri.fromFile(file))
        } else {
            showCameraPermissionDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.customColors.bg1),
        contentAlignment = Alignment.Center
    ) {
        if (isSystemInDarkTheme()){
            AnimatedShinyTop(lightGreen,100f,-370f)
            AnimatedShinyBottom(blue,-190f,400f)
        }else{
            AnimatedShinyTop(blue,120f,-370f)
            AnimatedShinyBottom(lightGreen,-100f,300f)
        }

        Box(
            modifier = Modifier
                .width(350.dp)
                .height(550.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            lightGray.copy(0.4f),
                            lightGray.copy(0.4f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = white,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onNavigateBack() }
                    )
                    Text(
                        text = "Настройки профиля",
                        color = white,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(
                            width = 3.dp,
                            color = Color.White,
                            shape = CircleShape
                        )
                        .background(Color.LightGray)
                        .clickable { showAvatarPicker = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (currentAvatar != null) {
                        Image(
                            painter = rememberImagePainter(
                                ImageRequest.Builder(LocalContext.current)
                                    .data(currentAvatar)
                                    .build()
                            ),
                            contentDescription = "Аватар пользователя",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Icon",
                            tint = Color.White,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp)
                        .clickable { showAvatarPicker = true },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Сменить аватар",
                        tint = white,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Сменить аватар",
                        color = white,
                        modifier = Modifier.padding(start = 8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    SettingItem(
                        icon = Icons.Default.Person,
                        title = "Имя",
                        value = currentName,
                        onClick = {  showNameDialog = true  }
                    )

                    SettingItem(
                        icon = Icons.Default.DateRange,
                        title = "Группа",
                        value = currentGroup,
                        onClick = {showGroupDialog = true }
                    )

                    SettingItem(
                        icon = Icons.Default.Email,
                        title = "Почта",
                        value = currentEmail,
                        onClick = { showEmailDialog = true  }
                    )
                }
            }
        }

        if (showAvatarPicker) {
            AvatarPickerDialog(
                onDismiss = { showAvatarPicker = false },
                onCameraSelected = {
                    if (checkCameraPermission()) {
                        val file = imagePickerManager.createImageFile()
                        tempFile = file

                        val photoUri = imagePickerManager.getUriForFile(file)

                        cameraLauncher.launch(photoUri)
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                onGallerySelected = {
                    galleryLauncher.launch("image/*")
                }
            )
        }

        if (showCameraPermissionDialog) {
            AlertDialog(
                onDismissRequest = { showCameraPermissionDialog = false },
                title = {
                    Text(
                        text = "Разрешение камеры",
                        color = white,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "Для использования камеры необходимо предоставить разрешение. Вы можете предоставить его в настройках приложения.",
                        color = white,
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { showCameraPermissionDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.customColors.searchBar
                        )
                    ) {
                        Text("OK", color = white)
                    }
                },
                containerColor = MaterialTheme.customColors.dialogCont,
                textContentColor = white,
                titleContentColor = white
            )
        }

        if (showNameDialog) {
            EditDialog(
                title = "Изменить имя",
                currentValue = currentName,
                onDismiss = { showNameDialog = false },
                onConfirm = { newName ->
                    AppState.setUserName(newName)
                    showNameDialog = false
                }
            )
        }

        if (showGroupDialog) {
            EditDialog(
                title = "Изменить группу",
                currentValue = currentGroup,
                onDismiss = { showGroupDialog = false },
                onConfirm = { newGroup ->
                    AppState.setUserGroup(newGroup)
                    showGroupDialog = false
                }
            )
        }

        if (showEmailDialog) {
            EditDialog(
                title = "Изменить почту",
                currentValue = currentEmail,
                onDismiss = { showEmailDialog = false },
                onConfirm = { newEmail ->
                    AppState.setUserEmail(newEmail)
                    showEmailDialog = false
                }
            )
        }
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = white,
            modifier = Modifier.size(24.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(
                text = title,
                color = white,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = value,
                color = white,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Изменить",
            tint = white,
            modifier = Modifier.size(20.dp)
        )
    }
}

fun saveImageFromUri(context: Context, uri: Uri): String {
    val file = File(context.getExternalFilesDir(null), "avatar_${System.currentTimeMillis()}.jpg")

    try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        file.createNewFile()
    }

    return file.absolutePath
}

@Preview(
    name = "Light Theme Settings",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun UserSettingsScreenLightPreview() {
    ScheduleAppTheme(darkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            UserSettingsScreen(
                onNavigateBack = {}
            )
        }
    }
}

@Preview(
    name = "Dark Theme Settings",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun UserSettingsScreenDarkPreview() {
    ScheduleAppTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            UserSettingsScreen(
                onNavigateBack = {}
            )
        }
    }
}