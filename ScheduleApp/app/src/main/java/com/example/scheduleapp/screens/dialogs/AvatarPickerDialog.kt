package com.example.scheduleapp.screens.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalDrawer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.scheduleapp.R
import com.example.scheduleapp.ui.theme.ScheduleAppTheme
import com.example.scheduleapp.ui.theme.blue
import com.example.scheduleapp.ui.theme.customColors
import com.example.scheduleapp.ui.theme.deepGreen
import com.example.scheduleapp.ui.theme.lightGreen
import com.example.scheduleapp.ui.theme.white

@Composable
fun AvatarPickerDialog(
    onDismiss: () -> Unit,
    onCameraSelected: () -> Unit,
    onGallerySelected: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.customColors.dialogCont,
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(18.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.size(24.dp))
                Text(
                    text = "Выберите источник",
                    color = white,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Закрыть",
                        tint = white
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Откуда хотите загрузить фото?",
                color = white,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp),
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = onCameraSelected,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(3.dp, lightGreen),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(horizontal = 0.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical =2.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_camera),
                            contentDescription = "Камера",
                            tint = white,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "Камера",
                            color = white,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Gallery button
                OutlinedButton(
                    onClick = onGallerySelected,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(3.dp, lightGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(horizontal = 0.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_galery),
                            contentDescription = "Галерея",
                            tint = white,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "Галерея",
                            color = white,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    name = "Light Theme",
    showBackground = true
)
@Composable
fun AvatarPickerDialogPreviewLight() {
    ScheduleAppTheme {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            AvatarPickerDialog(
                onDismiss = { },
                onCameraSelected = { },
                onGallerySelected = { }
            )
        }
    }
}

@Preview(
    name = "Dark Theme",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AvatarPickerDialogPreviewDark() {
    ScheduleAppTheme {
        Column(
            modifier = Modifier
                .background(Color.DarkGray)
                .padding(16.dp)
        ) {
            AvatarPickerDialog(
                onDismiss = { },
                onCameraSelected = { },
                onGallerySelected = { }
            )
        }
    }
}

@Preview(
    name = "Light Theme with Background",
    showBackground = true
)
@Composable
fun AvatarPickerDialogPreviewLightWithBackground() {
    ScheduleAppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            AvatarPickerDialog(
                onDismiss = { },
                onCameraSelected = { },
                onGallerySelected = { }
            )
        }
    }
}

@Preview(
    name = "Dark Theme with Background",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AvatarPickerDialogPreviewDarkWithBackground() {
    ScheduleAppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            AvatarPickerDialog(
                onDismiss = { },
                onCameraSelected = { },
                onGallerySelected = { }
            )
        }
    }
}