package com.tutorial.betterlife

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tutorial.betterlife.AppointmentListItemSmall
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun DashboardScreen(viewModel: AppointmentViewModel = viewModel()) {
    val allAppointments by viewModel.allAppointments.collectAsState(initial = emptyList())
    val today = LocalDate.now()
    val thisWeek = today.datesUntil(today.plusDays(7)).toList()

    val todayAppointments = remember(allAppointments) {
        allAppointments.filter {
            val apptDate = Instant.ofEpochMilli(it.rawApptTime).atZone(ZoneId.systemDefault()).toLocalDate()
            apptDate == today
        }.sortedBy { it.rawApptTime }
    }

    val weekAppointments = remember(allAppointments) {
        allAppointments.filter {
            val apptDate = Instant.ofEpochMilli(it.rawApptTime).atZone(ZoneId.systemDefault()).toLocalDate()
            apptDate in thisWeek && apptDate != today
        }.sortedBy { it.rawApptTime }
    }

    var showEditDialog by remember { mutableStateOf(false) }
    var appointmentToEdit by remember { mutableStateOf<Appointment?>(null) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(text = "Today's Appointments", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(todayAppointments, key = { it.id ?: 0 }) { appointment ->
                AppointmentListItemSmall(
                    appointment = appointment,
                    onDeleteClick = { viewModel.delete(appointment) },
                    onClick = {
                        appointmentToEdit = appointment
                        showEditDialog = true
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Upcoming Week's Appointments", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(weekAppointments, key = { it.id ?: 0 }) { appointment ->
                AppointmentListItemSmall(
                    appointment = appointment,
                    onDeleteClick = { viewModel.delete(appointment) },
                    onClick = {
                        appointmentToEdit = appointment
                        showEditDialog = true
                    }
                )
            }
        }

        if (showEditDialog && appointmentToEdit != null) {
            EditAppointmentDialog(
                appointment = appointmentToEdit!!,
                viewModel = viewModel,
                onDismiss = {
                    showEditDialog = false
                    appointmentToEdit = null
                }
            )
        }
    }
}
