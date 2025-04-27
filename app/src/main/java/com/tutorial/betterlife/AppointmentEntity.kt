package com.tutorial.betterlife

import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.room.Entity
import androidx.room.PrimaryKey

@OptIn(ExperimentalMaterial3Api::class)
@Entity(tableName = "appointments")
data class Appointment (
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    var title: String,
    var description: String,
    var day: Int,
    var month: Int,
    var year: Int,
    var date: String,
    var hour: Int,
    var minute: Int,
    var notificationTime: Long,
    var notification: Boolean,
    var rawApptTime: Long,

)