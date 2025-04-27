package com.tutorial.betterlife

import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tutorial.betterlife.ui.theme.BetterLifeTheme
import androidx.navigation.NavController
import android.provider.Settings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BetterLifeTheme {
                checkAndRequestPermissions(this)
                NotificationHelper.createNotificationChannel(this)
                MainScreen()

                }
            }
        }
    }


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BetterLifeTheme {
        Greeting("Android")
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    Button(onClick = { navController.navigate("details") }, modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)) {
        Text("Go to Details")
    }
}

//Checks for required permissions for notifications, PUSH and SCHEDULE_EXACT_ALARM
fun checkAndRequestPermissions(activity: Activity) {
    val permissionsToRequest = mutableListOf<String>()

    // POST_NOTIFICATIONS – Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (activity.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // SCHEDULE_EXACT_ALARM – Android 12+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (!alarmManager.canScheduleExactAlarms()) {
            // Optional: guide user to allow it manually (no runtime prompt exists)
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            activity.startActivity(intent)
        }
    }

    // Request permissions if needed
    if (permissionsToRequest.isNotEmpty()) {
        activity.requestPermissions(permissionsToRequest.toTypedArray(), 1001)
    }
}
