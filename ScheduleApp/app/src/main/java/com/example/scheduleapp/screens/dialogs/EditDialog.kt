package com.example.scheduleapp.screens.dialogs

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduleapp.ui.theme.ScheduleAppTheme
import com.example.scheduleapp.ui.theme.customColors
import com.example.scheduleapp.ui.theme.white

@Composable
fun EditDialog(
    title: String,
    currentValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var editedValue by remember { mutableStateOf(currentValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                color = white,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        },
        text = {
            Column {
                BasicTextField(
                    value = editedValue,
                    onValueChange = { editedValue = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    textStyle = TextStyle(
                        color = white,
                        fontSize = 16.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.wrapContentSize().padding(end = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.customColors.searchBar
                    )
                ) {
                    Text("Отмена", color = white)
                }

                Button(
                    onClick = { onConfirm(editedValue) },
                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.customColors.searchBar
                    )
                ) {
                    Text("Сохранить", color = white)
                }
            }
        },
        dismissButton = {
        },
        containerColor = MaterialTheme.customColors.dialogCont.copy(alpha = 0.9f)
    )
}

@Preview(
    name = "Edit Dialog Preview",
    showBackground = true
)
@Composable
fun EditDialogPreview() {
    ScheduleAppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            EditDialog(
                title = "Изменить имя",
                currentValue = "Кристина",
                onDismiss = { },
                onConfirm = { }
            )
        }
    }
}

@Preview(
    name = "Dark Dialog Preview",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun EditDialogPreviewN() {
    ScheduleAppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            EditDialog(
                title = "Изменить имя",
                currentValue = "Кристина",
                onDismiss = { },
                onConfirm = { }
            )
        }
    }
}