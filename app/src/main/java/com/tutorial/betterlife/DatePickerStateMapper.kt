package com.tutorial.betterlife

import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object DatePickerStateMapper {

    @OptIn(ExperimentalMaterial3Api::class)
    fun datePickerStateToYMD(state: DatePickerState): Triple<Int, Int, Int>? {
        val millis = state.selectedDateMillis ?: return null
        val localDate = Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        return Triple(localDate.monthValue, localDate.dayOfMonth, localDate.year)
    }

    fun ymdToEpochMillis(month: Int, day: Int, year: Int): Long {
        val localDate = LocalDate.of(year, month, day)
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}
