package com.tutorial.betterlife

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import com.tutorial.betterlife.NotificationHelper
import kotlinx.coroutines.flow.map
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.State

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine


class AppointmentViewModel(application: Application) : AndroidViewModel(application) {
    private val appointmentDao: AppointmentDao =
        AppointmentDatabase.getDatabase(application).apopintmentDao()
    var allAppointments: Flow<List<Appointment>> = appointmentDao.getAllAppointments()
    val context = getApplication<Application>()

    //for search
    private var _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = _searchQuery
    val filteredAppointments: Flow<List<Appointment>> = combine(
        appointmentDao.getAllAppointments(), _searchQuery
    ) { list, query ->
        val lowerQuery = query.lowercase()
        if (lowerQuery.isBlank()) list
        else list.filter {
            it.title.contains(lowerQuery, ignoreCase = true) ||
                    it.description.contains(lowerQuery, ignoreCase = true) ||
                    "${it.month}/${it.day}/${it.year}".contains(lowerQuery) ||
                    String.format("%02d:%02d", it.hour, it.minute).contains(lowerQuery) ||
                    it.date.contains(lowerQuery, ignoreCase = true)
        }
    }



    /*    val filteredAppointments: Flow<List<Appointment>> = appointmentDao.getAllAppointments()
        .map { list ->
            val query = _searchQuery.value.lowercase()
            if (query.isBlank()) list else list.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true) ||
                        "${it.month}/${it.day}/${it.year}".contains(query) ||
                        String.format("%02d:%02d", it.hour, it.minute).contains(query) ||
                        it.date.contains(query, ignoreCase = true)
            }
        }*/
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query}

    fun insert(appointment: Appointment) {
        viewModelScope.launch {
            appointmentDao.insertAppointment(appointment)
            val appointments = appointmentDao.getAllAppointments().firstOrNull()
            appointments?.let {
                NotificationHelper.refreshAllNotifications(context, it)
            }
            }
        }

        fun delete(appointment: Appointment) {
            viewModelScope.launch {
                appointmentDao.deleteAppointment(appointment)
                val appointments = appointmentDao.getAllAppointments().firstOrNull()
                appointments?.let {
                    NotificationHelper.refreshAllNotifications(context, it)
                }
            }
        }

        fun update(appointment: Appointment) {
            viewModelScope.launch {
                appointmentDao.updateAppointment(appointment)
                val appointments = appointmentDao.getAllAppointments().firstOrNull()
                appointments?.let {
                    NotificationHelper.refreshAllNotifications(context, it)
                }
            }
        }

    fun setGlobalNotifications(enabled: Boolean) {
        viewModelScope.launch {
            val all = appointmentDao.getAllAppointments().firstOrNull() ?: return@launch
            val updated = all.map { it.copy(notification = enabled) }
            updated.forEach { appointmentDao.updateAppointment(it) }
            NotificationHelper.refreshAllNotifications(context, updated)
        }
    }


}

