package com.amadeus.ting;

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.amadeus.ting.databinding.ActivityFoodIntakeBinding
import com.amadeus.ting.databinding.ActivityPlannerBinding
import com.google.android.material.imageview.ShapeableImageView
import java.time.LocalDate
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FoodIntake : AppCompatActivity(), CalendarAdapter.OnDateClickListener {
    // Initializing horizontal calendar
    private lateinit var binding: ActivityFoodIntakeBinding
    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val cal = Calendar.getInstance(Locale.ENGLISH)
    private val currentDate = Calendar.getInstance(Locale.ENGLISH)
    private val dates = java.util.ArrayList<Date>()
    private lateinit var calendarAdapter: CalendarAdapter
    private val calendarList2 = java.util.ArrayList<CalendarDateModel>()
    private lateinit var recyclerView: RecyclerView
    private var taskadapter: TaskAdapter? = null
    private lateinit var tskList: List<TaskModel>
    private var sortedTaskList: List<TaskModel> = emptyList()

    companion object {

        const val PREFS_NAME = "FoodIntakePrefFile"  // created a SharedReferences file that can store the data even if the file is exited
        const val BUTTON_TO_BE_CLICKED_KEY = "buttonToBeClicked"

        var eatingIntervalHours: Int = 1
        var eatingIntervalMinutes: Int = 0

        lateinit var foodScheduleList : ArrayList<FoodIntakeInfo>

        var buttonToBeClicked = 1

    }

    private lateinit var newRecyclerView : RecyclerView


    // Variables from food intake input (connect with the user's choice muna)
    // temp placeholder
    private var mealsPerDay: Int = 6


    private var firstReminderHours: Int = 0
    private var firstReminderMinutes: Int = 30

    // Button Visibility Variables
    private var editTimeVisibility = Array(mealsPerDay) { false }
    private var eatButtonVisibility = Array(mealsPerDay) { false }
    private var checkVisibility = Array(mealsPerDay) { false }

    // Food Intake Information Data
    private var timeToEatHours : Array<Int> = Array(mealsPerDay) {0}
    private var timeToEatColons : Array<String> = Array(mealsPerDay) {""}
    private var timeToEatMinutes : Array<Int>  = Array(mealsPerDay) {0}
    private var timeToEatMeridiem : Array<String> = Array(mealsPerDay) {""}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_intake)

        // Reads the values from SharedPreferences
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        buttonToBeClicked = prefs.getInt(BUTTON_TO_BE_CLICKED_KEY, 1)
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
            val goToFoodIntakeInput = Intent(this, FoodIntakeInput::class.java)
            startActivity(goToFoodIntakeInput)
        }

        onClick<ShapeableImageView>(R.id.create_button){
            val goToFoodIntakeInput = Intent(this, FoodIntakeInput::class.java)
            startActivity(goToFoodIntakeInput)
        }

        // Resets the user's food intake info
        onClick<ShapeableImageView>(R.id.delete_button){
            resetList()
            Toast.makeText(this, "Reset My Day Clicked!", Toast.LENGTH_SHORT).show()
        }

    }

    private fun resetList() {

        foodScheduleList = arrayListOf<FoodIntakeInfo>()

        for (i in 0 until mealsPerDay) {
            val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

            // Resets the visibility of the buttons and the variable pointing to the button to be clicked
            with(prefs.edit()) {
                putInt(BUTTON_TO_BE_CLICKED_KEY, 1)
                putInt("timeToEatHour_$i", 0)
                putInt("timeToEatMinute_$i", 0)
                putString("timeToEatMeridiem_$i", "")
                // putString("timeToEatColon_$i", ":")
                putBoolean("editTimeVisible_${i - 1}", true)
                putBoolean("eatButtonVisible_${i - 1}", true)
                putBoolean("checkVisible_${i - 1}", false)
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

        for (i in 1..mealsPerDay) {
            getSharedPreferencesValues(prefs, i)
            val intakeInfo = FoodIntakeInfo(i,  timeToEatHours[i - 1], timeToEatColons[i - 1],  timeToEatMinutes[i - 1], timeToEatMeridiem[i - 1])
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
        editTimeVisibility[i - 1] = prefs.getBoolean("editTimeVisible_${i - 1}", true)
        eatButtonVisibility[i - 1] = prefs.getBoolean("eatButtonVisible_${i - 1}", true)
        checkVisibility[i - 1] = prefs.getBoolean("checkVisible_${i - 1}", false)
        timeToEatHours[i - 1] = prefs.getInt("timeToEatHour_${i - 1}", 0)
        timeToEatColons[i - 1] = prefs.getString("timeToEatColon_${i - 1}", "").toString()
        timeToEatMinutes[i - 1] = prefs.getInt("timeToEatMinute_${i - 1}", 0)
        timeToEatMeridiem[i - 1] = prefs.getString("timeToEatMeridiem_${i - 1}", "").toString()
    }
    //Setting up the calendar adapter
    private fun setUpClickListener() {
        binding.ivCalendarNext.setOnClickListener {
            cal.add(Calendar.MONTH, 1)
            setUpCalendar()
        }
        binding.ivCalendarPrevious.setOnClickListener {
            cal.add(Calendar.MONTH, -1)
            if (cal == currentDate)
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
            calendarList2.forEachIndexed { index, calendarModel ->
                calendarModel.isSelected = index == position
            }
            calendarAdapter.setData(calendarList2)
        }, this)

        binding.calendarRecycler.adapter = calendarAdapter
        binding.calendarRecycler.scrollToPosition(defPos)
        // Line below requires debugging, need to check why it doesn't function
    }
    private fun setUpCalendar() {
        val calendarList = java.util.ArrayList<CalendarDateModel>()
        binding.tvDateMonth.text = sdf.format(cal.time)
        val monthCalendar = cal.clone() as Calendar
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        dates.clear()
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        while (dates.size < maxDaysInMonth) {
            dates.add(monthCalendar.time)
            calendarList.add(CalendarDateModel(monthCalendar.time))
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        calendarList2.clear()
        calendarList2.addAll(calendarList)
        calendarAdapter.setData(calendarList)
    }

    override fun onDateClick(position: Int) {
        //Add the date here
        val dateModel = calendarAdapter.getItem(position)
        taskadapter?.addList(tskList, dateModel)

    }

}