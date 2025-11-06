package com.example.scheduleapp.screens.master

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduleapp.R
import com.example.scheduleapp.items.Calendar
import com.example.scheduleapp.ui.theme.white
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ScreenList() {
    Box {
        Column(modifier = Modifier
            .padding(top = 60.dp)) {
            Calendar()
        }
    }

}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
@Preview
fun TestList() {
    Scaffold(
        containerColor = colorResource(id = R.color.gray)
    )
    {
        ScreenList()
    }
}