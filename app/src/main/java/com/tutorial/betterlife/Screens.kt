package com.tutorial.betterlife

import android.R.attr.enabled
import android.R.attr.label
import android.R.attr.maxLines
import android.R.attr.onClick
import androidx.compose.foundation.clickable
import android.R.attr.text
import android.R.attr.y
import android.R.id.input
import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Debug
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.key.Key.Companion.Calendar
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.*
import com.kizitonwose.calendar.core.yearMonth
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.Week
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.Month
import androidx.compose.ui.draw.drawBehind
import java.nio.file.Files.size
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tutorial.betterlife.DatePickerStateMapper
import kotlinx.coroutines.selects.select
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.TemporalQueries.localDate
import kotlin.math.absoluteValue
import com.tutorial.betterlife.EditAppointmentDialog
import com.tutorial.betterlife.calendar.*

/*@Composable fun DashboardScreen() { Text("🏠 Dashboard Screen") }*/

@Composable
fun CalendarScreen(viewModel: AppointmentViewModel) {
    Text("📅 Calendar Screen")

    //Add appointment button
    CalendarMainScreen()




    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() } // Available from the library

    val appointments = viewModel.allAppointments.collectAsState(emptyList()).value
    val appointmentsByDate = remember(appointments) {
        appointments.groupBy {
            Instant.ofEpochMilli(it.rawApptTime).atZone(ZoneId.systemDefault()).toLocalDate()
        }
    }

    var showEditDialog by remember { mutableStateOf(false) }
    var appointmentToEdit by remember { mutableStateOf<Appointment?>(null) }


    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }



    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )
//Main Column where stuff will be placed
    Column(
        modifier = Modifier
            .padding(MenuDefaults.DropdownMenuItemContentPadding)
            .windowInsetsPadding(WindowInsets.statusBars)
            .fillMaxSize()
            .padding(16.dp)
    )
    {
        HorizontalCalendar(
            state = state,
            dayContent = { day ->
                Day(day, appointmentsByDate) { selectedDate = it }
            },
            monthHeader = { Month(currentMonth) },
            modifier = Modifier



        )
//box where appointment data will go
        Box(
            modifier = Modifier

                .fillMaxWidth()

        )
        {
            /*Text(text = "Appointments")*/
            val selectedAppointments = remember(selectedDate, appointmentsByDate) {
                appointmentsByDate[selectedDate].orEmpty().sortedBy { it.rawApptTime }
            }

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(selectedAppointments, key = { it.id ?: 0 }) { appointment ->
                    AppointmentListItemSmall(
                        appointment = appointment,
                        onDeleteClick = { viewModel.delete(appointment) },
                        onClick = {
                            appointmentToEdit = appointment
                            showEditDialog = true
                            Log.d("EDIT_CLICK", "Clicked - showEditDialog is $showEditDialog")
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
}

@Composable
fun ListViewScreen(viewModel: AppointmentViewModel) { Text("📋 List View Screen")

    var showEditDialog by remember { mutableStateOf(false) }
    var appointmentToEdit by remember { mutableStateOf<Appointment?>(null) }



    Column(modifier = Modifier
        .fillMaxHeight()
        .padding(16.dp)
    ){
        Text(text = "Appointments", style = MaterialTheme.typography.headlineMedium)
        val appointmentsFlow = viewModel.allAppointments.collectAsState(emptyList())
        val appointments = viewModel.filteredAppointments.collectAsState(emptyList()).value
        val searchQuery by viewModel.searchQuery.collectAsState()
        TextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            label = { Text("Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxHeight()
        ){
                items(items = appointments, key = {it.id ?: 0}) { appointment ->
                AppointmentListItem(
                    appointment = appointment,
                    onDeleteClick = {
                        viewModel.delete(appointment)
                    }, onClick = {
                        appointmentToEdit = appointment
                        showEditDialog = true
                        Log.d("EDIT_CLICK", "Clicked - showEditDialog is $showEditDialog")

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

@Composable
fun AppointmentListItem(
    appointment: Appointment,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable{onClick()  },
        elevation = CardDefaults.cardElevation(4.dp),

    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = appointment.title, style = MaterialTheme.typography.titleLarge)
                Text(text = appointment.description, style = MaterialTheme.typography.bodyLarge)
                Text(text = "Date: " + appointment.month + "/" + appointment.day + "/" + appointment.year)
                Text(text = "Time: ${String.format("%02d:%02d", appointment.hour, appointment.minute)}")
                Text(text = "Notification: ${if (appointment.notification) "Enabled" else "Disabled"}")
                Text(text = "Notification Time: " + formatEpochToHourMinute(appointment.notificationTime))
                // To show the date, convert the 'Long' timestamp to a readable string.
                // e.g., SimpleDateFormat in real usage.
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }


}




/*
@Composable fun SettingsScreen(viewModel: AppointmentViewModel) {
    Text("⚙️ Settings Screen")

}
*/


//old code
/*@Composable
fun Day(day: CalendarDay, appointmentsByDate: Map<LocalDate, List<Appointment>>, onClick: (LocalDate) -> Unit) {
    val currentMonthDay = remember { YearMonth.now() }
    val apptsForday = appointmentsByDate(day.date).orEmpty()
    Log.d("DRAW_DEBUG", "Rendering day: ${day.date}")

    *//* Canvas(modifier = Modifier.size(120.dp)) {
         drawCircle(
             color = Color.Magenta,
             center = center,
             radius = size.minDimension / 2
         )*//*

    Box(
        modifier = Modifier
            .size(70.dp)
            .clickable()
            {
                Log.d("TAG", "Clicked on ${day.date}")

            }
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RectangleShape
            )
            .background(Color.LightGray, RectangleShape)
            .drawBehind {
                apptsForDay.take(3).forEachIndexed { index, appt ->
                    Log.d("DRAW_DEBUG", "Drawing appointment line for ${appt.title} on ${day.date}")
                    drawAppointmentLine(index)
                }

            }
    )


    {
        if (day.date.month == currentMonthDay.month) {
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,





                )
        } else {
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }

        // Text(text = day.date.dayOfMonth.toString(), style = MaterialTheme.typography.bodyLarge)
    }
}*/
//For Calendar
@Composable
fun Month(month: YearMonth) {
    val formattedMonth = month.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))
    Text(
        text = formattedMonth.toString().replaceFirstChar { it.uppercaseChar()}, // Capitalize the first letter
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier
            .padding(16.dp)
            .background(Color.LightGray, CircleShape)
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
    )
}

//Draws lines in days for Calendar for appointments
fun DrawScope.drawAppointmentLine(index: Int)
{
    val lineHeight = 6.dp.toPx()
    val spacing = 4.dp.toPx()

    val topOffset = 20.dp.toPx() + index * (lineHeight + spacing)

    drawRoundRect(
        color = Color.Blue.copy(alpha = 0.3f),
        topLeft = Offset(x = 8.dp.toPx(), y = topOffset),
        size = Size(width = size.width - 16.dp.toPx(), height = lineHeight),
        cornerRadius = CornerRadius(4.dp.toPx())

    )
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CalendarMainScreen() {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Appointment"
                )
            }
        }
    ) {
        Text(text = "Add Appointment Window", modifier = Modifier.padding(16.dp))

        if (showDialog) {
            AddAppointmentDialog(viewModel = viewModel(),
                onDismiss = { showDialog = false }
                // You can pass onSave = { appointment -> ... } later
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppointmentDialog(viewModel: AppointmentViewModel,
    onDismiss: () -> Unit
    // onSave: (AppointmentData) -> Unit
) {
    val titleState = remember { mutableStateOf("") }
    val descState = remember { mutableStateOf("") }
    val dayState = remember { mutableStateOf(0) }
    val monthState = remember { mutableStateOf(0) }
    val yearState = remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()

    var showDatePicker = remember{ mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var selectedDate by remember {mutableStateOf< LocalDate?>(null)}

    var selectedTime by remember { mutableStateOf<String?>(null) }
    var selectedHour by remember { mutableStateOf<Int?>(null) }
    var selectedMinute by remember { mutableStateOf<Int?>(null) }
    var selectedRawTime by remember { mutableStateOf<Long?>(null) }

    var shouldNotify by remember { mutableStateOf(false) }
    var notificationTime by remember { mutableStateOf<Long?>(null) }
    var notificationOffset by remember { mutableStateOf(0) }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Appointment") },
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
                    onValueChange = { descState.value = it},
                    label = {Text("Description")},
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState),
                    minLines = 3,
                    maxLines = 5
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    datePickerState.selectedDateMillis?.let{
                        selectedDate = Instant.ofEpochMilli(it)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                    }

                    selectedDate?.let{
                        Text(
                            text = "Selected date: ${it.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))}",
                        )
                    }
                    }

                Button(onClick = { showDatePicker.value = true }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Select Date")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    selectedTime?.let {
                        Text(
                            text = "Selected Time: ${String.format("%02d:%02d", selectedHour, selectedMinute)}"
                        )
                    }
                }
                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    TimePickerButton(onTimeSelected = { hours, minute ->
                        selectedTime = String.format("%02d:%02d", hours, minute)
                        selectedHour = hours
                        selectedMinute = minute
                        selectedRawTime = toTimeOnlyEpochMillis(hours, minute)
                    })
                }
                    if (showDatePicker.value == true )
                        {
                            DatePickerDialog(
                                onDismissRequest = { showDatePicker.value = false },
                                confirmButton = {
                                    TextButton(onClick = {

                                        // Call onSave(...) here when ready
                                        showDatePicker.value = false
                                    }) {
                                        Text("OK")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDatePicker.value = false }) {
                                        Text("Cancel")
                                    }
                                }
                            ) {
                                DatePicker(state = datePickerState)
                            }
                        }

                // Example field:
                // OutlinedTextField(value = ..., onValueChange = ...)


                // Add more fields as needed
                Row() {
                    Checkbox(
                        checked = shouldNotify,
                        onCheckedChange = { shouldNotify = it },
                        modifier = Modifier.padding(8.dp)
                    )
                    Text(text = "Enable Notification")
                    TextField(
                        value = notificationOffset.toString(),
                        onValueChange = {input ->
                            notificationOffset = input.toIntOrNull() ?: 0 },
                        label = {Text("Notification Time (minutes before)")},

                    )


                }

            }
        },
        confirmButton = {
            Button(onClick = {
                val newAppointment = Appointment(
                    title = titleState.value,
                    description = descState.value,
                    day = datePickerState.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate().dayOfMonth
                    } ?: 0,
                    month = datePickerState.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate().monthValue
                    } ?: 0,
                    year = datePickerState.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate().year
                    } ?: 0,
                    date = selectedTime ?: "",
                    hour = selectedHour ?: 0,
                    minute = selectedMinute ?: 0,
                    notification = shouldNotify ?: false,
                    rawApptTime = combineDateAndTime(datePickerState.selectedDateMillis ?: 0L ,selectedHour ?: 0, selectedMinute ?: 0),
                    notificationTime = getReminderNotificationTime(combineDateAndTime(datePickerState.selectedDateMillis ?: 0L, selectedHour ?: 0, selectedMinute ?: 0), notificationOffset)

                )
                Log.d("DEBUG_NOTIF_TIME", "Now: ${System.currentTimeMillis()}")
                Log.d("DEBUG_NOTIF_TIME", "Appointment time: ${newAppointment.rawApptTime}")
                Log.d("DEBUG_NOTIF_TIME", "Notification time: ${newAppointment.notificationTime}")

                viewModel.insert(newAppointment)
                // Call onSave(...) here when ready
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TimePickerButton(
    label: String = "Select Time",
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
) {
    val context = LocalContext.current
    val calendar = remember { java.util.Calendar.getInstance() }

    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            { _: TimePicker, hour: Int, minute: Int ->
                onTimeSelected(hour, minute)
            },
            calendar.get(java.util.Calendar.HOUR_OF_DAY),
            calendar.get(java.util.Calendar.MINUTE),
            true // is24HourView
        )
    }

    Button(onClick = { timePickerDialog.show() }) {
        Text(text = label)
    }
}

//This combines the Day/Month/Year taken from the Day picker and combines it with the hour/minute from the time picker.
//This was necessary because Jetpack Compose, as of 4/22/2025, does not yet have a built-in DateTime picker.
fun combineDateAndTime(
    dateEpochMillis: Long,
    hour: Int,
    minute: Int,
    zoneId: ZoneId = ZoneId.systemDefault()
): Long {
    // Step 1: Convert date to LocalDate
    val date = Instant.ofEpochMilli(dateEpochMillis).atZone(ZoneOffset.UTC).toLocalDate()

    // Step 2: Convert time to LocalTime
    val time = LocalTime.of(hour, minute)

    // Step 3: Combine into ZonedDateTime
    val dateTime = ZonedDateTime.of(date, time, zoneId)

    // Step 4: Convert back to epoch millis
    return dateTime.toInstant().toEpochMilli()
}

fun toTimeOnlyEpochMillis(hour: Int, minute: Int, zoneId: ZoneId = ZoneId.systemDefault()): Long {
    val time = LocalTime.of(hour, minute)
    val date = LocalDate.of(1970, 1, 1)
    return ZonedDateTime.of(date, time, zoneId).toInstant().toEpochMilli()
}

fun getReminderNotificationTime(
    appointmentTimeMillis: Long,
    reminderMinutesBefore: Int
): Long {
    return appointmentTimeMillis - (reminderMinutesBefore * 60_000L)
}

fun formatEpochToHourMinute(epochMillis: Long): String {
    val localDateTime = Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneOffset.UTC)
        .toLocalDateTime()
    return String.format("%02d:%02d", localDateTime.hour, localDateTime.minute)
}

