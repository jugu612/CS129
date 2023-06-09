package com.amadeus.ting;

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.amadeus.ting.databinding.ActivityFoodIntakeBinding
import com.google.android.material.imageview.ShapeableImageView
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class FoodIntake : AppCompatActivity(), CalendarAdapter.OnDateClickListener {
    private lateinit var binding: ActivityFoodIntakeBinding
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var tskList: List<TaskModel>

    private var taskadapter: TaskAdapter? = null
    private val calendarData = CalendarData()
    private var sortedTaskList: List<TaskModel> = emptyList()

    companion object {
        const val PREFS_NAME = "FoodIntakePrefFile"  // created a SharedReferences file that can store the data even if the file is exited
        const val BUTTON_TO_BE_CLICKED_KEY = "buttonToBeClicked"

        var eatingIntervalHours: Int = 0
        var eatingIntervalMinutes: Int = 0
        var mealsPerDay: Int = 0

        var firstReminderHours: Int = 0
        var firstReminderMinutes: Int = 0

        lateinit var foodScheduleList : ArrayList<FoodIntakeInfo>
        var buttonToBeClicked = 1

        lateinit var newRecyclerView : RecyclerView

    }



    // Button Visibility Variables
    private lateinit var editTimeVisibility : Array<Boolean>
    private lateinit  var eatButtonVisibility : Array<Boolean>
    private lateinit var checkVisibility : Array<Boolean>

    // Food Intake Information Data
    private lateinit var timeToEatHours : Array<Int>
    private lateinit var timeToEatColons : Array<String>
    private lateinit var timeToEatMinutes : Array<Int>
    private lateinit var timeToEatMeridiem : Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_intake)

        // Reads the values from SharedPreferences
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        buttonToBeClicked = prefs.getInt(BUTTON_TO_BE_CLICKED_KEY, 1)
        assignValuesFromSharedPreferences()

        binding = ActivityFoodIntakeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpAdapter()
        setUpClickListener()
        setUpCalendar()

        // Initializes the Recycler View
        newRecyclerView = findViewById(R.id.eating_time_rv)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)

        // Displays the food intake data of the app
        getListData(prefs)

        // Behavior of each buttons in the app
        onClick<ShapeableImageView>(R.id.back_button){
            val goToHomePage = Intent(this, HomePage::class.java)
            startActivity(goToHomePage)
        }

        onClick<ShapeableImageView>(R.id.edit_button){
            val labelAlert = FoodIntakeInput()
            labelAlert.editMealtimeDialog(this, R.layout.edit_mealtime)
        }

        // Resets the user's food intake info
        onClick<ShapeableImageView>(R.id.delete_button){
            resetList()
            Toast.makeText(this, "Reset My Day Clicked!", Toast.LENGTH_SHORT).show()
        }

        resetListWakeup(SleepSection.selectedWakeTime)
    }

   fun resetList() {

        foodScheduleList = arrayListOf<FoodIntakeInfo>()

        for (i in 0 until mealsPerDay) {
            val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

            // Resets the visibility of the buttons and the variable pointing to the button to be clicked
            with(prefs.edit()) {
                putInt(BUTTON_TO_BE_CLICKED_KEY, 1)
                putInt("timeToEatHour_$i", 0)
                putInt("timeToEatMinute_$i", 0)
                putString("timeToEatMeridiem_$i", "")

                putBoolean("editTimeVisible_$i", true)
                putBoolean("eatButtonVisible_$i", true)
                putBoolean("checkVisible_$i", false)
                apply()
            }

            buttonToBeClicked = 1
            val intakeInfo = FoodIntakeInfo(i + 1, 0, "", 0, "")
            foodScheduleList.add(intakeInfo)
        }

        newRecyclerView.adapter = FoodIntakeAdapter(foodScheduleList)

    }

    // gets the data from the shared preferences and put it in the recycler view
    private fun getListData(prefs : SharedPreferences) {

        foodScheduleList = arrayListOf<FoodIntakeInfo>()

        editTimeVisibility = Array(mealsPerDay) { false }
        eatButtonVisibility = Array(mealsPerDay) { false }
        checkVisibility = Array(mealsPerDay) { false }

        timeToEatHours  = Array(mealsPerDay) {0}
        timeToEatColons  = Array(mealsPerDay) {""}
        timeToEatMinutes  = Array(mealsPerDay) {0}
        timeToEatMeridiem = Array(mealsPerDay) {""}

        for (i in 0 until mealsPerDay) {

            getSharedPreferencesValues(prefs, i)

            val intakeInfo = FoodIntakeInfo(i + 1,  timeToEatHours[i], timeToEatColons[i],  timeToEatMinutes[i], timeToEatMeridiem[i])
            foodScheduleList.add(intakeInfo)
        }
        newRecyclerView.adapter = FoodIntakeAdapter(foodScheduleList)
    }


    private inline fun <reified T : View> Activity.onClick(id: Int, crossinline action: (T) -> Unit) {
        findViewById<T>(id)?.setOnClickListener {
            action(it as T)
        }
    }

    // retrieves the values stored in the shared preferences
    private fun getSharedPreferencesValues(prefs : SharedPreferences, i : Int) {
        editTimeVisibility[i] = prefs.getBoolean("editTimeVisible_${i}", true)
        eatButtonVisibility[i] = prefs.getBoolean("eatButtonVisible_${i}", true)
        checkVisibility[i] = prefs.getBoolean("checkVisible_${i}", false)
        timeToEatHours[i] = prefs.getInt("timeToEatHour_${i}", 0)
        timeToEatColons[i] = prefs.getString("timeToEatColon_${i}", "").toString()
        timeToEatMinutes[i] = prefs.getInt("timeToEatMinute_${i}", 0)
        timeToEatMeridiem[i] = prefs.getString("timeToEatMeridiem_${i}", "").toString()
    }
    //Setting up the calendar adapter
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
        taskadapter?.addList(tskList, dateModel)

    }

    private fun assignValuesFromSharedPreferences() {
        val otherPrefs = getSharedPreferences("FoodIntakePrefs", Context.MODE_PRIVATE)

        eatingIntervalHours = otherPrefs.getInt(FoodIntakeInput.PREF_EATING_INTERVAL_HOURS, 0)
        eatingIntervalMinutes = otherPrefs.getInt(FoodIntakeInput.PREF_EATING_INTERVAL_MINUTES, 0)
        mealsPerDay = otherPrefs.getInt(FoodIntakeInput.PREF_MEALS_PER_DAY, 0)
        firstReminderHours = otherPrefs.getInt(FoodIntakeInput.PREF_FIRST_REMINDER_HOURS, 0)
        firstReminderMinutes = otherPrefs.getInt(FoodIntakeInput.PREF_FIRST_REMINDER_MINUTES, 0)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this, HomePage::class.java)
        startActivity(intent)
        finish()
    }

    private fun resetListWakeup(selectedWakeTime: String?) {
        // Check if it is already the wake-up time
        val currentTime = Calendar.getInstance()
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentTime.get(Calendar.MINUTE)

        val wakeTimeParts = selectedWakeTime?.split(":")
        val wakeHour = wakeTimeParts?.get(0)?.toIntOrNull()
        val wakeMinute = wakeTimeParts?.get(1)?.substringBefore(" ")?.toIntOrNull()

        if (wakeHour != null && wakeMinute != null)  {
                if (wakeHour <= currentHour && wakeMinute <= currentMinute) {
                    // Perform the reset operation
                    foodScheduleList = arrayListOf<FoodIntakeInfo>()

                    for (i in 0 until mealsPerDay) {
                        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

                        // Resets the visibility of the buttons and the variable pointing to the button to be clicked
                        with(prefs.edit()) {
                            putInt(BUTTON_TO_BE_CLICKED_KEY, 1)
                            putInt("timeToEatHour_$i", 0)
                            putInt("timeToEatMinute_$i", 0)
                            putString("timeToEatMeridiem_$i", "")

                            putBoolean("editTimeVisible_$i", true)
                            putBoolean("eatButtonVisible_$i", true)
                            putBoolean("checkVisible_$i", false)
                            apply()
                        }

                        buttonToBeClicked = 1
                        val intakeInfo = FoodIntakeInfo(i + 1, 0, "", 0, "")
                        foodScheduleList.add(intakeInfo)
                    }

                    newRecyclerView.adapter = FoodIntakeAdapter(foodScheduleList)

                    // Display a toast message
                    val toastMessage = "Wake-up time reached! List reset."
                    Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

