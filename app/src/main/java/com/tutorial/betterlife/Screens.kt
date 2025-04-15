package com.tutorial.betterlife

import android.R.attr.enabled
import android.R.attr.onClick
import android.R.attr.y
import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.os.Debug
import android.util.Log
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType


@Composable fun DashboardScreen() { Text("🏠 Dashboard Screen") }

@Composable
fun CalendarScreen() {
    Text("📅 Calendar Screen")

    //Add appointment button
    CalendarMainScreen()




    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() } // Available from the library


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
            dayContent = { Day(it) },
            monthHeader = { Month(currentMonth) },
            modifier = Modifier



        )
//box where appointment data will go
        Box(
            modifier = Modifier

                .fillMaxWidth()

        )
        {
            Text(text = "Appointments")
        }
    }
}

@Composable fun ListViewScreen() { Text("📋 List View Screen") }

@Composable fun SettingsScreen() { Text("⚙️ Settings Screen") }

@Composable
fun Day(day: CalendarDay) {
    val currentMonthDay = remember { YearMonth.now() }
    Log.d("DRAW_DEBUG", "Rendering day: ${day.date}")

    /* Canvas(modifier = Modifier.size(120.dp)) {
         drawCircle(
             color = Color.Magenta,
             center = center,
             radius = size.minDimension / 2
         )*/

    Box(
        modifier = Modifier
            .size(70.dp)
            .clickable
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
                drawAppointmentLine(5)
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
}
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
            AddAppointmentDialog(
                onDismiss = { showDialog = false }
                // You can pass onSave = { appointment -> ... } later
            )
        }
    }
}



@Composable
fun AddAppointmentDialog(
    onDismiss: () -> Unit
    // onSave: (AppointmentData) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Appointment") },
        text = {
            Column {
                Text(text = "Form goes here (title, time, etc.)")
                // Example field:
                // OutlinedTextField(value = ..., onValueChange = ...)
            }
        },
        confirmButton = {
            Button(onClick = {
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
