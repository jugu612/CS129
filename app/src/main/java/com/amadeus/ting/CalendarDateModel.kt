package com.amadeus.ting

import java.text.SimpleDateFormat
import java.util.*

data class CalendarDateModel(var data: Date, var isSelected: Boolean = false) {

    val calendarDay: String
        get() = SimpleDateFormat("EE", Locale.getDefault()).format(data)

    val calendarDate: String
        get() {
            val cal = Calendar.getInstance()
            cal.time = data
            return cal[Calendar.DAY_OF_MONTH].toString()
        }
    val calendarDatefull: String
        get() {
            val cal = Calendar.getInstance()
            cal.time = data
            val dayOfMonth = cal[Calendar.DAY_OF_MONTH]
            val month = cal[Calendar.MONTH] + 1 // Month starts from 0, so add 1
            val year = cal[Calendar.YEAR]
            return String.format("%d/%d/%04d", month, dayOfMonth, year)
        }
}