package com.amadeus.ting

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.SnapHelper
import com.amadeus.ting.databinding.ActivityWaterIntakeBinding
import com.google.android.material.imageview.ShapeableImageView
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class WaterIntake : AppCompatActivity(), CalendarAdapter.OnDateClickListener {
    private lateinit var binding: ActivityWaterIntakeBinding
    private lateinit var calendarAdapter: CalendarAdapter

    private lateinit var seekBar: SeekBar
    private lateinit var valueTextView: TextView
    private lateinit var alertDialog : WaterAlertDialog
    private lateinit var prefs : SharedPreferences

    private val PREFS_NAME = "WaterIntakePrefsFile" // Shared Preferences File

    private var milliliterGoal : Int = 0
    private var milliliterLeft : Int = 0
    private var totalMilliliter : Int = 0

    private var seekBarInput : Int = 0
    private var currentPercentage : Int = 0

    private lateinit var waterIntakeInformation : ArrayList<WaterIntakeInfo>
    private var index : Int = 0



    private var timeArray: Array<String> = Array(10000) { "" }
    private var numberOfLitersArray: Array<String> = Array(10000) { "" }
    private lateinit var recyclerViewWater : RecyclerView

    private lateinit var alertDialogBuilder : AlertDialog.Builder
    private lateinit var dialogView : View

    private val calendarData = CalendarData()

    @SuppressLint("MissingInflatedId", "UseCompatLoadingForDrawables", "SetTextI18n",
        "InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_intake)

        binding = ActivityWaterIntakeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpAdapter()
        setUpClickListener()
        setUpCalendar()

        alertDialogBuilder = AlertDialog.Builder(this)
        dialogView = LayoutInflater.from(this).inflate(R.layout.water_intake_records, null)

        // Initialize Recycler View
        recyclerViewWater = dialogView.findViewById(R.id.water_intake_records_rv)
        recyclerViewWater.layoutManager = LinearLayoutManager(this)
        recyclerViewWater.setHasFixedSize(true)


        alertDialog = WaterAlertDialog() // Instance of Water Intake Dialog

        // Opens Shared Preferences
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        restoreElementsInWaterRecordArray()
        restoreStoredPrefData()

        // Initialize Values
        val drinkTextView: TextView = findViewById(R.id.drink)
        drinkTextView.text = "Drink $milliliterGoal ml"

        val numberPercent: TextView = findViewById(R.id.number_percent)
        numberPercent.text = "${currentPercentage}%"

        val progressBar: ProgressBar = findViewById(R.id.progress_bar)
        progressBar.progress = currentPercentage

        val numberMLLeft: TextView = findViewById(R.id.number_ml)
        numberMLLeft.text = "$milliliterLeft ml left"

        seekBar = findViewById(R.id.seekBar)
        seekBar.max = milliliterLeft

        // Today's Goal -> (Inputs number of ml)
        val goalText: TextView = findViewById(R.id.goal_text)
        goalText.setOnClickListener {
            alertDialog.intakeGoalDialog(this, R.layout.water_popupwindow) { userMilliliterInput ->
                if (userMilliliterInput != null) {
                    milliliterGoal = userMilliliterInput
                    milliliterLeft = userMilliliterInput
                    seekBar.max = milliliterGoal

                    drinkTextView.text = "Drink $milliliterGoal ml" // Updates the number of milliliter value
                    numberMLLeft.text = "$milliliterGoal ml left"

                    val editor = prefs.edit()
                    editor.putInt("milliliterGoal", milliliterGoal)
                    editor.putInt("milliliterLeft", milliliterGoal)
                    editor.apply()
                }
            }
        }
        valueTextView = findViewById(R.id.textView_progress) // Seekbar Value

        // Seekbar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                valueTextView.text = "$progress ml"
                seekBarInput = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })


        val drinkButton: View = findViewById(R.id.drink_button)
        drinkButton.setOnClickListener {

            val temp = seekBarInput
            if (milliliterLeft == 0) {
                Toast.makeText(this, "No more water left to drink!", Toast.LENGTH_SHORT).show()
            } else if (seekBarInput == 0) {
                Toast.makeText(this, "Input number of ml to drink.", Toast.LENGTH_SHORT).show()
            } else {
                    // Updates the percentage value
                    totalMilliliter += seekBarInput
                    currentPercentage = (((totalMilliliter.toFloat() / milliliterGoal.toFloat()) * 100)).toInt()
                    numberPercent.text = "${currentPercentage}%"

                    // Updates the number of ml left
                    milliliterLeft -= seekBarInput
                    numberMLLeft.text = "$milliliterLeft ml left"

                    // Updates the Progress Bar
                    progressBar.progress = currentPercentage
                    seekBar.max = milliliterLeft

                    Toast.makeText(this, "Drank water successfully!", Toast.LENGTH_SHORT).show()


                    val calendar = Calendar.getInstance()
                    var hours = 0
                    hours = if (calendar.get(Calendar.HOUR) == 0) {12} else { calendar.get(Calendar.HOUR) }
                    val minutes = calendar.get(Calendar.MINUTE)
                    val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"

                    val editor = prefs.edit()
                    // Stores Information in the waterIntakeInformation array
                    if (timeArray.isEmpty() && numberOfLitersArray.isEmpty()) {
                        timeArray[0] = String.format("%02d:%02d %s", hours, minutes, amPm)
                        numberOfLitersArray[0]= "$temp ml"

                        editor.putString("waterIntakeInfoTime_0", "$hours:$minutes $amPm")
                        editor.putString("waterIntakeInfoNumberOfLiters_0", "$temp ml")
                        index++
                    } else {
                        timeArray[index] = String.format("%02d:%02d %s", hours, minutes, amPm)
                        numberOfLitersArray[index]= "$temp ml"

                        editor.putString("waterIntakeInfoTime_$index", "$hours:$minutes $amPm")
                        editor.putString("waterIntakeInfoNumberOfLiters_$index", "$temp ml")
                        index++
                    }

                    editor.putInt("currentPercentage", currentPercentage)
                    editor.putInt("milliliterLeft", milliliterLeft)
                    editor.putInt("totalMilliliter", totalMilliliter)
                    editor.putInt("index", index)
                    editor.apply()
            }
        }

        // Records Button Clicked (Alert Dialog Popup)
        val recordsButton: View = findViewById(R.id.records_button)
        recordsButton.setOnClickListener {
            restoreElementsInWaterRecordArray()
            recordsDialog()
        }

        // Reset Button Clicked
        val resetButton: View = findViewById(R.id.reset_button)
        resetButton.setOnClickListener {
            val editor = prefs.edit()
            editor.clear()

            progressBar.progress = 0
            numberPercent.text = "0%"
            currentPercentage = 0
            totalMilliliter = 0
            index = 0
            milliliterLeft = milliliterGoal
            seekBar.max = milliliterGoal
            numberMLLeft.text = "$milliliterLeft ml left"

            editor.putInt("milliliterGoal", milliliterGoal)
            editor.putInt("milliliterLeft", milliliterGoal)
            editor.apply()

            Toast.makeText(this, "Reset successful!", Toast.LENGTH_SHORT).show()
        }
    }

    private inline fun <reified T : View> Activity.onClick(id: Int, crossinline action: (T) -> Unit) {
        findViewById<T>(id)?.apply {
            setOnClickListener {
                action(this)
            }
        }
    }


    private fun restoreStoredPrefData() {
        milliliterGoal = prefs.getInt("milliliterGoal", 0)
        currentPercentage = prefs.getInt("currentPercentage", 0)
        totalMilliliter = prefs.getInt("totalMilliliter", 0)
        milliliterLeft = prefs.getInt("milliliterLeft", 0)
        index = prefs.getInt("index", 0)
    }

    private fun restoreElementsInWaterRecordArray() {

        waterIntakeInformation  = arrayListOf<WaterIntakeInfo>()

        for (i in 0 until index) {
            timeArray[i] = prefs.getString("waterIntakeInfoTime_$i", "").toString()
            numberOfLitersArray[i] = prefs.getString("waterIntakeInfoNumberOfLiters_$i", "").toString()

            val waterIntakeInfo = WaterIntakeInfo(timeArray[i],  numberOfLitersArray[i])
            waterIntakeInformation.add(waterIntakeInfo)
        }
        recyclerViewWater.adapter = WaterIntakeAdapter(waterIntakeInformation)
    }

    private fun recordsDialog() {
        alertDialogBuilder = AlertDialog.Builder(this)
        dialogView = LayoutInflater.from(this).inflate(R.layout.water_intake_records, null)

        // Remove existing parent view if it exists
        val parentView = dialogView.parent as? ViewGroup
        parentView?.removeView(dialogView)

        recyclerViewWater = dialogView.findViewById(R.id.water_intake_records_rv)
        recyclerViewWater.layoutManager = LinearLayoutManager(this)
        recyclerViewWater.setHasFixedSize(true)

        val adapter = WaterIntakeAdapter(waterIntakeInformation)
        recyclerViewWater.adapter = adapter

        alertDialogBuilder.setView(dialogView)
        val dialog = alertDialogBuilder.create()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun setUpClickListener() {
        val currentDate = Calendar.getInstance(Locale.ENGLISH)
        binding.ivCalendarNext.setOnClickListener {
            calendarData.currentDate.add(Calendar.MONTH, 1)
            setUpCalendar()
        }
        binding.ivCalendarPrevious.setOnClickListener {
            calendarData.currentDate.add(Calendar.MONTH, -1)
            if (calendarData.currentDate == currentDate)
                setUpCalendar()
            else
                setUpCalendar()
        }
    }
    private fun setUpAdapter() {
        //For positioning the recyclerview
        val curDate = LocalDate.now()
        val defPos = curDate.dayOfMonth-3

        // Horizontal spacing for each date in the calendar
        val dateSpacing = resources.getDimensionPixelSize(R.dimen.single_calendar_margin)
        binding.calendarRecycler.addItemDecoration(HorizontalItemDecoration(dateSpacing))

        // Center snap for scrolling
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.calendarRecycler)



        calendarAdapter = CalendarAdapter({ calendarDateModel: CalendarDateModel, position ->
            calendarData.calendarList.forEachIndexed { index, calendarModel ->
                calendarModel.isSelected = index == position
            }
            calendarAdapter.setData(calendarData.calendarList)
        }, this)

        binding.calendarRecycler.adapter = calendarAdapter
        binding.calendarRecycler.scrollToPosition(defPos)
        // Line below requires debugging, need to check why it doesn't function
    }
    private fun setUpCalendar() {
        val calendarList = java.util.ArrayList<CalendarDateModel>()
        binding.tvDateMonth.text = calendarData.dateFormat.format(calendarData.currentDate.time)
        val monthCalendar = calendarData.currentDate.clone() as Calendar
        val maxDaysInMonth = calendarData.currentDate.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendarData.dates.clear()
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        while (calendarData.dates.size < maxDaysInMonth) {
            calendarData.dates.add(monthCalendar.time)
            calendarList.add(CalendarDateModel(monthCalendar.time))
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        calendarData.calendarList.clear()
        calendarData.calendarList.addAll(calendarList)
        calendarAdapter.setData(calendarList)
    }
    override fun onDateClick(position: Int) {
        //Add the date here
        val dateModel = calendarAdapter.getItem(position)

    }


}







