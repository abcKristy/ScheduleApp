package com.example.scheduleapp.screens.master

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scheduleapp.R
import com.example.scheduleapp.items.Calendar

@Composable
fun ScreenList() {
    Box {
        Column() {
            Spacer(modifier = Modifier.height(20.dp))
            Calendar()
            LazyColumn {

            }
            Spacer(modifier = Modifier.height(110.dp))
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