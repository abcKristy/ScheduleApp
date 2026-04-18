package com.example.scheduleapp.screens.master.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduleapp.R
import com.example.scheduleapp.ui.theme.customColors
import com.example.scheduleapp.util.SemesterUtils


@Composable
fun SemesterHeader(
    semester: String,
    isOutdated: Boolean = false,
    modifier: Modifier = Modifier
) {
    val displayName = SemesterUtils.getDisplayName(semester)
    val customColors = MaterialTheme.customColors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = if (isOutdated) {
                    customColors.searchItem.copy(alpha = 0.4f)
                } else {
                    customColors.searchItem.copy(alpha = 0.2f)
                },
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_calendar_month),
            contentDescription = "Семестр",
            tint = if (isOutdated) {
                MaterialTheme.customColors.shiny
            } else {
                MaterialTheme.customColors.title.copy(alpha = 0.7f)
            },
            modifier = Modifier.size(16.dp)
        )

        Text(
            text = displayName,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (isOutdated) {
                MaterialTheme.customColors.shiny
            } else {
                MaterialTheme.customColors.title.copy(alpha = 0.7f)
            }
        )

        if (isOutdated) {
            Text(
                text = "• Устарело",
                fontSize = 12.sp,
                color = MaterialTheme.customColors.shiny.copy(alpha = 0.8f)
            )
        }
    }
}