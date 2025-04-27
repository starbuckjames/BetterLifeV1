package com.tutorial.betterlife

import android.R.attr.text
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAppointmentDialog(
    appointment: Appointment,
    viewModel: AppointmentViewModel,
    onDismiss: () -> Unit
) {
    val titleState = remember { mutableStateOf(appointment.title) }
    val descState = remember { mutableStateOf(appointment.description) }
    val scrollState = rememberScrollState()

    val dateMillis = remember { mutableStateOf(combineDateAndTime(appointment.rawApptTime, 0, 0)) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis.value)
    val selectedDate = rememberUpdatedState(
        newValue = Instant.ofEpochMilli(datePickerState.selectedDateMillis ?: appointment.rawApptTime)
            .atZone(ZoneOffset.UTC).toLocalDate()
    )

    val selectedHour = remember { mutableStateOf(appointment.hour) }
    val selectedMinute = remember { mutableStateOf(appointment.minute) }
    val selectedRawTime = remember { mutableStateOf(toTimeOnlyEpochMillis(appointment.hour, appointment.minute)) }

    val shouldNotify = remember { mutableStateOf(appointment.notification) }
    val notificationOffset = remember { mutableStateOf(0) } // Could extract from time diff if stored

    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Appointment") },
        text = {
            Column {
                TextField(
                    value = titleState.value,
                    onValueChange = { titleState.value = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = descState.value,
                    onValueChange = { descState.value = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState),
                    minLines = 3,
                    maxLines = 5
                )

                selectedDate.value?.let {
                    Text(text = "Selected Date: ${it.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))}",)

                }

                Button(onClick = { showDatePicker = true }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Change Date")
                }

                Text(
                    text = "Selected Time: ${String.format("%02d:%02d", selectedHour.value, selectedMinute.value)}",
                    modifier = Modifier.align(Alignment.Start)
                )

                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    TimePickerButton(onTimeSelected = { hour, minute ->
                        selectedHour.value = hour
                        selectedMinute.value = minute
                        selectedRawTime.value = toTimeOnlyEpochMillis(hour, minute)
                    })
                }

                Row() {
                    Checkbox(
                        checked = shouldNotify.value,
                        onCheckedChange = { shouldNotify.value = it },
                        modifier = Modifier.padding(8.dp)
                    )
                    Text("Enable Notification")
                    TextField(
                        value = notificationOffset.value.toString(),
                        onValueChange = { input ->
                            notificationOffset.value = input.toIntOrNull() ?: 0
                        },
                        label = { Text("Notify (minutes before)") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                }

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val updatedAppointment = appointment.copy(
                    title = titleState.value,
                    description = descState.value,
                    day = selectedDate.value?.dayOfMonth ?: appointment.day,
                    month = selectedDate.value?.monthValue ?: appointment.month,
                    year = selectedDate.value?.year ?: appointment.year,
                    hour = selectedHour.value,
                    minute = selectedMinute.value,
                    notification = shouldNotify.value,
                    rawApptTime = combineDateAndTime(
                        datePickerState.selectedDateMillis ?: appointment.rawApptTime,
                        selectedHour.value,
                        selectedMinute.value
                    ),
                    notificationTime = getReminderNotificationTime(
                        combineDateAndTime(
                            datePickerState.selectedDateMillis ?: appointment.rawApptTime,
                            selectedHour.value,
                            selectedMinute.value
                        ),
                        notificationOffset.value
                    )
                )
                viewModel.update(updatedAppointment)
                onDismiss()
            }) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
