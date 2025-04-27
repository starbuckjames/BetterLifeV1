// CalendarAppointmentDisplay.kt

package com.tutorial.betterlife.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.core.CalendarDay
import com.tutorial.betterlife.Appointment
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun Day(
    day: CalendarDay,
    appointmentsByDate: Map<LocalDate, List<Appointment>>,
    onClick: (LocalDate) -> Unit
) {
    val appts = appointmentsByDate[day.date].orEmpty().sortedBy { it.rawApptTime }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clickable { onClick(day.date) }
            .drawBehind {
                val lineHeight = 4.dp.toPx()
                val padding = 2.dp.toPx()
                appts.take(3).forEachIndexed { index, appt ->
                    val top = size.height - ((index + 1) * (lineHeight + padding))
                    drawRect(
                        color = Color.Blue.copy(alpha = 0.85f),
                        topLeft = Offset(0f, top),
                        size = androidx.compose.ui.geometry.Size(size.width, lineHeight)
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = if (appts.isNotEmpty()) Color.Black else Color.Gray
        )
    }
}

@Composable
fun AppointmentDetailsForDate(
    date: LocalDate?,
    appointments: List<Appointment>
) {
    if (date == null || appointments.isEmpty()) return

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Text(
            text = "Appointments on ${date.toString()}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(appointments.sortedBy { it.rawApptTime }) { appt ->
                val timeStr = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(appt.rawApptTime),
                    ZoneId.systemDefault()
                ).format(DateTimeFormatter.ofPattern("HH:mm"))

                Text(
                    text = "• $timeStr - ${appt.title}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
