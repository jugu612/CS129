package com.amadeus.ting

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.imageview.ShapeableImageView
import android.widget.ArrayAdapter

class FoodIntakeInput : AppCompatActivity() {

    // SharedPreferences instance
    private lateinit var sharedPreferences: SharedPreferences

    // Keys for SharedPreferences
    private val PREF_MEALS_PER_DAY = "pref_meals_per_day"
    private val PREF_EATING_INTERVAL_HOURS = "pref_eating_interval_hours"
    private val PREF_EATING_INTERVAL_MINUTES = "pref_eating_interval_minutes"
    private val PREF_FIRST_REMINDER_HOURS = "pref_first_reminder_hours"
    private val PREF_FIRST_REMINDER_MINUTES = "pref_first_reminder_minutes"

    // Store the user's choice in these variables
    private var mealsPerDay: Int = 1
    private var eatingIntervalHours: Int = 0
    private var eatingIntervalMinutes: Int = 0
    private var firstReminderHours: Int = 0
    private var firstReminderMinutes: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_food_intake)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("FoodIntakePrefs", Context.MODE_PRIVATE)

        // Restore saved values from SharedPreferences
        restoreSavedValues()

        onClick<ShapeableImageView>(R.id.back_button) {
            val goToFoodIntake = Intent(this, FoodIntake::class.java)
            startActivity(goToFoodIntake)
        }

        // Meal Frequency options:
        val mealFrequencyList = mutableListOf<String>()
        for (i in 1..8) {
            val mealfreq = if (i == 1) "$i meal" else "$i meals"
            mealFrequencyList.add(mealfreq)
        }
        val mealFrequencyOptions = mealFrequencyList.toTypedArray()
        setupFoodSelectionSpinner(this, R.id.meals_per_day_button, mealFrequencyOptions) { selectedPosition ->
            mealsPerDay = selectedPosition + 1 // Save selected value (1-based index)
        }

        // Eating Interval Options
        val intervalOptions = mutableListOf<String>()
        for (h in 0..6) {
            for (m in 0..59 step 5) {
                val hours = if (h == 1) "$h hr" else "$h hrs"
                val minutes = if (m == 1) "$m min" else "$m mins"
                val interval = if (h == 0) {
                    "$minutes"
                } else if (m == 0) {
                    "$hours"
                } else {
                    "$hours and $minutes"
                }
                intervalOptions.add(interval)
            }
        }
        val eatingIntervalOptions = intervalOptions.toTypedArray()

        setupFoodSelectionSpinner(this, R.id.eating_intervals_button, eatingIntervalOptions) { selectedPosition ->
            // Calculate and save eating interval hours and minutes based on the selected position
            val hours = selectedPosition / 12 // Each hour has 12 options
            val minutes = (selectedPosition % 12) * 5 // Each 5 minutes has 1 option
            eatingIntervalHours = hours
            eatingIntervalMinutes = minutes
        }

        // First Notification Reminder Options
        val firstReminderList = mutableListOf<String>()
        for (h in 0..6) {
            for (m in 0..59 step 5) {
                val hours = if (h == 1) "$h hr" else "$h hrs"
                val minutes = if (m == 1) "$m min" else "$m mins"
                val interval = if (h == 0) {
                    "$minutes"
                } else if (m == 0) {
                    "$hours"
                } else {
                    "$hours and $minutes"
                }
                firstReminderList.add(interval)
            }
        }
        val firstReminderOptions = firstReminderList.toTypedArray()

        setupFoodSelectionSpinner(this, R.id.first_reminder_button, firstReminderOptions) { selectedPosition ->
            // Calculate and save first reminder hours and minutes based on the selected position
            if (selectedPosition == 0) {
                firstReminderHours = 0
                firstReminderMinutes = 0
            } else {
                val timeBefore = selectedPosition - 1 // Subtract 1 for the "On time" option
                firstReminderHours = timeBefore / 12 // Each hour has 12 options
                firstReminderMinutes = (timeBefore % 12) * 5 // Each 5 minutes has 1 option
            }
        }

        // Save button
        onClick<View>(R.id.save_button) {
            saveValuesToSharedPreferences() // Save the values to SharedPreferences
            showToast()
            val goToFoodIntake = Intent(this, FoodIntake::class.java)
            startActivity(goToFoodIntake)
        }
        // Cancel button
        onClick<View>(R.id.cancel_button) {
            val goToFoodIntake = Intent(this, FoodIntake::class.java)
            startActivity(goToFoodIntake)
        }
    }
    override fun onResume() {
        super.onResume()

        // Restore saved values from SharedPreferences
        restoreSavedValues()
    }

    private fun restoreSavedValues() {
        // Restore mealsPerDay value from SharedPreferences
        mealsPerDay = sharedPreferences.getInt(PREF_MEALS_PER_DAY, 1)

        // Restore eatingIntervalHours and eatingIntervalMinutes values from SharedPreferences
        eatingIntervalHours = sharedPreferences.getInt(PREF_EATING_INTERVAL_HOURS, 0)
        eatingIntervalMinutes = sharedPreferences.getInt(PREF_EATING_INTERVAL_MINUTES, 0)

        // Restore firstReminderHours and firstReminderMinutes values from SharedPreferences
        firstReminderHours = sharedPreferences.getInt(PREF_FIRST_REMINDER_HOURS, 0)
        firstReminderMinutes = sharedPreferences.getInt(PREF_FIRST_REMINDER_MINUTES, 0)

        // Update the spinners with the restored values
        val mealsPerDaySpinner = findViewById<Spinner>(R.id.meals_per_day_button)
        mealsPerDaySpinner.setSelection(mealsPerDay - 1) // Subtract 1 to convert to 0-based index

        val eatingIntervalSpinner = findViewById<Spinner>(R.id.eating_intervals_button)
        val eatingIntervalPosition = eatingIntervalHours * 12 + eatingIntervalMinutes / 5
        eatingIntervalSpinner.setSelection(eatingIntervalPosition)

        val firstReminderSpinner = findViewById<Spinner>(R.id.first_reminder_button)
        val firstReminderPosition = if (firstReminderHours == 0 && firstReminderMinutes == 0) {
            0 // "On time" option
        } else {
            (firstReminderHours * 12 + firstReminderMinutes / 5) + 1 // Add 1 for the "On time" option
        }
        firstReminderSpinner.setSelection(firstReminderPosition)
    }


    private fun saveValuesToSharedPreferences() {
        val editor = sharedPreferences.edit()
        editor.putInt(PREF_MEALS_PER_DAY, mealsPerDay)
        editor.putInt(PREF_EATING_INTERVAL_HOURS, eatingIntervalHours)
        editor.putInt(PREF_EATING_INTERVAL_MINUTES, eatingIntervalMinutes)
        editor.putInt(PREF_FIRST_REMINDER_HOURS, firstReminderHours)
        editor.putInt(PREF_FIRST_REMINDER_MINUTES, firstReminderMinutes)
        editor.apply()
    }

    private fun showToast() {
        val mealsPerDay = sharedPreferences.getInt(PREF_MEALS_PER_DAY, 1)
        val eatingIntervalHours = sharedPreferences.getInt(PREF_EATING_INTERVAL_HOURS, 0)
        val eatingIntervalMinutes = sharedPreferences.getInt(PREF_EATING_INTERVAL_MINUTES, 0)
        val firstReminderHours = sharedPreferences.getInt(PREF_FIRST_REMINDER_HOURS, 0)
        val firstReminderMinutes = sharedPreferences.getInt(PREF_FIRST_REMINDER_MINUTES, 0)

        val message = "Meals per day: $mealsPerDay\n" +
                "Eating interval: $eatingIntervalHours hours $eatingIntervalMinutes minutes\n" +
                "First reminder: $firstReminderHours hours $firstReminderMinutes minutes"

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }





    fun setupFoodSelectionSpinner(
        activity: Activity,
        spinnerId: Int,
        spinnerOptions: Array<out Any>,
        callback: (Int) -> Unit // Add callback parameter
    ) {
        val mealOption = activity.findViewById<Spinner>(spinnerId)
        val adapterMF = ArrayAdapter(
            activity,
            R.layout.spinner_item_color,
            spinnerOptions.map { it.toString() }
        )
        adapterMF.setDropDownViewResource(R.layout.spinner_item_color)
        mealOption.adapter = adapterMF
        mealOption.dropDownVerticalOffset = mealOption.height

        mealOption.setOnTouchListener { _, _ ->
            if (mealOption.isPressed) {
                mealOption.background = activity.resources.getDrawable(R.drawable.food_intake_spinner_down)
            } else {
                mealOption.background = activity.resources.getDrawable(R.drawable.food_intake_spinner_down)
            }
            false
        }

        mealOption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedPosition = parent.selectedItemPosition
                if (selectedPosition == position) {
                    mealOption.background = activity.resources.getDrawable(R.drawable.food_intake_spinner_up)
                } else {
                    mealOption.background = activity.resources.getDrawable(R.drawable.food_intake_spinner_up)
                }
                // Call the callback with the selected position
                callback(selectedPosition)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                mealOption.background = activity.resources.getDrawable(R.drawable.food_intake_spinner_up)
            }
        }
    }

    private inline fun <reified T : View> Activity.onClick(id: Int, crossinline action: (T) -> Unit) {
        findViewById<T>(id)?.setOnClickListener {
            action(it as T)
        }
    }
}

