package com.example.scheduleapp.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduleapp.data.EmptySchedule
import com.example.scheduleapp.data.ScheduleItem
import com.example.scheduleapp.ui.theme.ScheduleAppTheme
import com.example.scheduleapp.ui.theme.customColors
import java.time.LocalDate


@Composable
fun EmptyScheduleItemCompact(
    scheduleItem: ScheduleItem,
    modifier: Modifier = Modifier
) {
    val lessonNumber = EmptySchedule.getLessonNumber(scheduleItem)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.customColors.searchItem.copy(0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${scheduleItem.formattedStartTime}-${scheduleItem.formattedEndTime}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )

            Text(
                text = "$lessonNumber пара", // Вместо "○ Окно" теперь "1 пара", "2 пара" и т.д.
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}


@Preview(
    name = "Empty Item Compact - Light Theme",
    showBackground = true
)
@Composable
fun EmptyScheduleItemCompactPreviewLight() {
    ScheduleAppTheme(darkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                EmptyScheduleItemCompact(
                    scheduleItem = EmptySchedule.createEmptyItem(1, LocalDate.now())
                )
                EmptyScheduleItemCompact(
                    scheduleItem = EmptySchedule.createEmptyItem(2, LocalDate.now())
                )
                EmptyScheduleItemCompact(
                    scheduleItem = EmptySchedule.createEmptyItem(3, LocalDate.now())
                )
                EmptyScheduleItemCompact(
                    scheduleItem = EmptySchedule.createEmptyItem(4, LocalDate.now())
                )
                EmptyScheduleItemCompact(
                    scheduleItem = EmptySchedule.createEmptyItem(5, LocalDate.now())
                )
                EmptyScheduleItemCompact(
                    scheduleItem = EmptySchedule.createEmptyItem(6, LocalDate.now())
                )
                EmptyScheduleItemCompact(
                    scheduleItem = EmptySchedule.createEmptyItem(7, LocalDate.now())
                )
            }
        }
    }
}

@Preview(
    name = "Empty Item Compact - Dark Theme",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun EmptyScheduleItemCompactPreviewDark() {
    ScheduleAppTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                EmptyScheduleItemCompact(
                    scheduleItem = EmptySchedule.createEmptyItem(1, LocalDate.now())
                )
                EmptyScheduleItemCompact(
                    scheduleItem = EmptySchedule.createEmptyItem(2, LocalDate.now())
                )
                EmptyScheduleItemCompact(
                    scheduleItem = EmptySchedule.createEmptyItem(3, LocalDate.now())
                )
                EmptyScheduleItemCompact(
                    scheduleItem = EmptySchedule.createEmptyItem(4, LocalDate.now())
                )
                EmptyScheduleItemCompact(
                    scheduleItem = EmptySchedule.createEmptyItem(5, LocalDate.now())
                )
                EmptyScheduleItemCompact(
                    scheduleItem = EmptySchedule.createEmptyItem(6, LocalDate.now())
                )
                EmptyScheduleItemCompact(
                    scheduleItem = EmptySchedule.createEmptyItem(7, LocalDate.now())
                )
            }
        }
    }
}