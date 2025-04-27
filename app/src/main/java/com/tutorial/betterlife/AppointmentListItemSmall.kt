package com.tutorial.betterlife

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tutorial.betterlife.Appointment

@Composable
fun AppointmentListItemSmall(
    appointment: Appointment,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = appointment.title, style = MaterialTheme.typography.titleSmall)
                Text(text = appointment.description, style = MaterialTheme.typography.bodySmall)
                Text(text = "Date: ${appointment.month}/${appointment.day}/${appointment.year}", style = MaterialTheme.typography.bodySmall)
                Text(text = "Time: ${String.format("%02d:%02d", appointment.hour, appointment.minute)}", style = MaterialTheme.typography.bodySmall)
                Text(text = "Notification: ${if (appointment.notification) "Enabled" else "Disabled"}", style = MaterialTheme.typography.bodySmall)
                Text(text = "Notification Time: ${formatEpochToHourMinute(appointment.notificationTime)}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

// Import this from Screens.kt or define here if necessary
/*
fun formatEpochToHourMinute(epochMillis: Long): String {
    val localDateTime = java.time.Instant.ofEpochMilli(epochMillis)
        .atZone(java.time.ZoneOffset.UTC)
        .toLocalDateTime()
    return String.format("%02d:%02d", localDateTime.hour, localDateTime.minute)
}
*/
