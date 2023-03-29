package com.amadeus.ting

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.app.TimePickerDialog
import android.widget.Button
import android.widget.DatePicker
import android.widget.TimePicker
import java.util.*

class DatePick(private val dateButton: Button) : DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0

    private var savedDay = 0
    private var savedMonth = 0
    private var savedYear = 0
    private var savedHour = 0
    private var savedMinute = 0

    init {
        pickDate()
    }

    fun DefaultDate(){
        val cal: Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR_OF_DAY)
        minute = cal.get(Calendar.MINUTE)

        // Set the initial values to the current date
        savedDay = day
        savedMonth = month + 1
        savedYear = year

        // Format the current date as a string and set it as the text for the Button
        val currentDate = "$savedMonth/${savedDay+1}/$savedYear  | ☀All Day"
        dateButton.text = currentDate
        println(dateButton.text.toString())
    }
    private fun getDateTimeCalendar() {
        val cal: Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR_OF_DAY)
        minute = cal.get(Calendar.MINUTE)
    }

    fun pickDate() {
        dateButton.setOnClickListener {
            getDateTimeCalendar()

            DatePickerDialog(dateButton.context, this, year, month, day).show()
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month +1
        savedYear = year

        getDateTimeCalendar()

        TimePickerDialog(dateButton.context, this, hour, minute, false).apply {
            setButton(DialogInterface.BUTTON_NEUTRAL, "All day") { _, _ ->
                dateButton.text = "$savedMonth/$savedDay/$savedYear  | ☀All Day"
            }
        }.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute

        val suffix: String = if (savedHour < 12) "AM" else "PM"
        savedHour = if (savedHour > 12) savedHour - 12 else savedHour
        savedHour = if (savedHour == 0) 12 else savedHour

        dateButton.text = "$savedMonth/$savedDay/$savedYear  | ⏰$savedHour:${String.format("%02d", savedMinute)} $suffix"
    }
}
