package com.tutorial.betterlife

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun SettingsScreen(viewModel: AppointmentViewModel) {
    val context = LocalContext.current
    val allAppointments by viewModel.allAppointments.collectAsState(initial = emptyList())
    val switchState = allAppointments.all { it.notification }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Enable Notifications for All")
            Switch(
                checked = switchState,
                onCheckedChange = {
                    viewModel.setGlobalNotifications(it)
                }
            )
        }
    }
}


/*
@Composable
fun SettingsScreen(viewModel: AppointmentViewModel) {
    val context = LocalContext.current
    val allAppointments by viewModel.allAppointments.collectAsState(initial = emptyList())
    val allEnabled = allAppointments.all { it.notification }

    var switchState by remember { mutableStateOf(allEnabled) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Enable Notifications for All")
            Switch(
                checked = switchState,
                onCheckedChange = {
                    switchState = it
                    viewModel.setGlobalNotifications(it)
                }
            )
        }
    }
}
*/
