package com.tutorial.betterlife

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlin.random.Random

class NotificationHelper : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Reminder"
        val description = intent.getStringExtra("description") ?: "You have an appointment."

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(context).notify(Random.nextInt(), notification)

    }

    companion object {
        private const val CHANNEL_ID = "appointment_channel"

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Appointment Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Channel for appointment notifications"
                }
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.createNotificationChannel(channel)
            }
        }

        fun scheduleNotification(context: Context, appointment: Appointment) {
            if (!appointment.notification) return

            val intent = Intent(context, NotificationHelper::class.java).apply {
                putExtra("title", appointment.title)
                putExtra("description", appointment.description)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                appointment.id ?: Random.nextInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                appointment.notificationTime,
                pendingIntent
            )
        }

        fun cancelNotification(context: Context, requestCode: Int) {
            val intent = Intent(context, NotificationHelper::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(pendingIntent)
            }
        }

        fun refreshAllNotifications(context: Context, appointments: List<Appointment>) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            appointments.forEach { appointment ->
                // Cancel old alarm if it exists
                appointment.id?.let { id ->
                    val intent = Intent(context, NotificationHelper::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        id,
                        intent,
                        PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                    )
                    if (pendingIntent != null) {
                        alarmManager.cancel(pendingIntent)
                    }
                }

                // Schedule new alarm if enabled
                if (appointment.notification) {
                    scheduleNotification(context, appointment)
                }
            }
        }

        fun checkAndRequestPermissions(activity: Activity) {
            val permissionsToRequest = mutableListOf<String>()

            // POST_NOTIFICATIONS – Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (activity.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            // SCHEDULE_EXACT_ALARM – Android 12+, open system settings manually
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (!alarmManager.canScheduleExactAlarms()) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    activity.startActivity(intent)
                }
            }

            // Request permissions if needed
            if (permissionsToRequest.isNotEmpty()) {
                activity.requestPermissions(permissionsToRequest.toTypedArray(), 1001)
            }
        }
    }
}
