package com.example.scheduleapp.bottom_navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduleapp.R
import com.example.scheduleapp.ui.theme.darkGray
import com.example.scheduleapp.ui.theme.deepGreen
import com.example.scheduleapp.ui.theme.lightGreen

@Composable
fun ScreenProfile() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .padding(bottom = 110.dp)
                .fillMaxHeight(0.5f)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            colorResource(id = R.color.darkBlue)
                        )
                    )
                )
                .align(Alignment.BottomStart)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 100.dp,
                        shape = CircleShape,
                        spotColor = colorResource(id = R.color.lightGreen),
                        ambientColor = colorResource(id = R.color.lightGreen)
                    )
            ) {
                Image(
                    painter = painterResource(R.drawable.avatar),
                    contentDescription = "Glowing avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )
            }
            Text(
                text = "Kristina",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.lightGreen),
                modifier = Modifier.padding(bottom = 20.dp)

            )
            Card(
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 15.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = deepGreen
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Row(modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Major Group: ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.darkGray)
                    )
                    Text(
                        text = "IKBO_60-23",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.darkGray)
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 15.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = deepGreen
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Row(modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Email: ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.darkGray)
                    )
                    Text(
                        text = "ilicheva.k.o@edu.ru",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.darkGray)
                    )
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 15.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = deepGreen
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically){
                    Row(modifier = Modifier.padding(10.dp)
                        .fillMaxWidth(0.9f),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Theme: ",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.darkGray)
                        )
                        Text(
                            text = "Light",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.darkGray)
                        )
                    }

                    Icon(
                        painter = painterResource(R.drawable.ic_dark_mode),
                        contentDescription = "mode",
                        tint = darkGray,
                        modifier = Modifier
                            .size(30.dp)
                    )
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 15.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = deepGreen
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically){
                    Row(modifier = Modifier.padding(10.dp)
                        .fillMaxWidth(0.9f),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "CDO: ",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.darkGray)
                        )
                        Text(
                            text = "active url",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.darkGray)
                        )
                    }

                    Icon(
                        painter = painterResource(R.drawable.ic_copy),
                        contentDescription = "mode",
                        tint = darkGray,
                        modifier = Modifier
                            .size(25.dp)
                    )
                }
            }
        }


    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@Composable
fun testProfile(){
    Scaffold(
        containerColor = colorResource(id = R.color.gray))
    {
        ScreenProfile()
    }
}