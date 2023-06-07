package com.amadeus.ting

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.amadeus.ting.FoodIntake.Companion.PREFS_NAME
import com.amadeus.ting.FoodIntake.Companion.newRecyclerView

class FoodIntakeInput {
    // SharedPreferences instance
    private lateinit var sharedPreferences: SharedPreferences

    // Keys for SharedPreferences
    companion object {
        val PREF_MEALS_PER_DAY = "pref_meals_per_day"
        val PREF_EATING_INTERVAL_HOURS = "pref_eating_interval_hours"
        val PREF_EATING_INTERVAL_MINUTES = "pref_eating_interval_minutes"
        val PREF_FIRST_REMINDER_HOURS = "pref_first_reminder_hours"
        val PREF_FIRST_REMINDER_MINUTES = "pref_first_reminder_minutes"
    }

    // Store the user's choice in these variables
    private var mealsPerDay: Int = 1
    private var eatingIntervalHours: Int = 0
    private var eatingIntervalMinutes: Int = 0
    private var firstReminderHours: Int = 0
    private var firstReminderMinutes: Int = 0


    fun editMealtimeDialog(context: Context, layout: Int) {


        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(layout, null)

        val builder = AlertDialog.Builder(context, R.style.MyDialogStyle)
        builder.setView(dialogLayout)
        builder.setCancelable(false)

        // Create and show the AlertDialog
        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        val cancelButton = dialogLayout.findViewById<Button>(R.id.cancel_button)
        val saveButton = dialogLayout.findViewById<Button>(R.id.save_button)


        // Initialize SharedPreferences
        sharedPreferences = context.getSharedPreferences("FoodIntakePrefs", Context.MODE_PRIVATE)


        // Restore saved values from SharedPreferences
        //showToast(context)
        getSavedValues(dialogLayout)


        // Meal Frequency options:
        val mealFrequencyList = mutableListOf<String>()
        for (i in 1..8) {
            val mealfreq = if (i == 1) "$i meal" else "$i meals"
            mealFrequencyList.add(mealfreq)
        }
        val mealFrequencyOptions = mealFrequencyList.toTypedArray()
        setupFoodSelectionSpinner(dialogLayout, R.id.meals_per_day_button, mealFrequencyOptions) { selectedPosition ->
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
                    "$hours & $minutes"
                }
                intervalOptions.add(interval)
            }
        }
        val eatingIntervalOptions = intervalOptions.toTypedArray()

        setupFoodSelectionSpinner(dialogLayout, R.id.eating_intervals_button, eatingIntervalOptions) { selectedPosition ->
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
                    "$minutes before"
                } else if (m == 0) {
                    "$hours before"
                } else {
                    "$hours & $minutes before"
                }
                firstReminderList.add(interval)
            }
        }
        val firstReminderOptions = firstReminderList.toTypedArray()

        setupFoodSelectionSpinner(dialogLayout, R.id.first_reminder_button, firstReminderOptions) { selectedPosition ->
            firstReminderHours = selectedPosition / 12 // Each hour has 12 options
            firstReminderMinutes = (selectedPosition % 12) * 5 // Each 5 minutes has 1 option

        }

        cancelButton?.setOnClickListener {
            alertDialog.dismiss()
        }

        // Save button
        saveButton?.setOnClickListener {
            resetListInput(context)
            saveValuesToFoodIntake()
            saveValuesToSharedPreferences() // Save the values to SharedPreferences
            showToast(context)
            if (context is Activity) {
                context.finish()
                val goToFoodSection = Intent(context, FoodIntake::class.java)
                context.startActivity(goToFoodSection)
                context.overridePendingTransition(0, 0)
            }

        }

    }


    private fun getSavedValues( dialogLayout: View) {
        mealsPerDay = sharedPreferences.getInt(PREF_MEALS_PER_DAY, 1)
        eatingIntervalHours = sharedPreferences.getInt(PREF_EATING_INTERVAL_HOURS, 0)
        eatingIntervalMinutes = sharedPreferences.getInt(PREF_EATING_INTERVAL_MINUTES, 0)
        firstReminderHours = sharedPreferences.getInt(PREF_FIRST_REMINDER_HOURS, 0)
        firstReminderMinutes = sharedPreferences.getInt(PREF_FIRST_REMINDER_MINUTES, 0)


        val mealsPerDayButton = dialogLayout.findViewById<Spinner>(R.id.meals_per_day_button)
        val eatingIntervalsButton = dialogLayout.findViewById<Spinner>(R.id.eating_intervals_button)
        val firstReminderButton = dialogLayout.findViewById<Spinner>(R.id.first_reminder_button)

        mealsPerDayButton.post {
            mealsPerDayButton.setSelection(mealsPerDay - 1, false) // Adjust for 0-based index
        }

        eatingIntervalsButton.post {
            eatingIntervalsButton.setSelection(calculateIntervalPosition(eatingIntervalHours, eatingIntervalMinutes), false)
        }

        firstReminderButton.post {
            firstReminderButton.setSelection(calculateIntervalPosition(firstReminderHours, firstReminderMinutes), false)
        }
    }


    private fun calculateIntervalPosition(hours: Int, minutes: Int): Int {
        val position = (hours * 12) + (minutes / 5)
        return position
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

    // Save inputted values to food intake
    private fun saveValuesToFoodIntake() {
        FoodIntake.eatingIntervalMinutes = eatingIntervalMinutes
        FoodIntake.eatingIntervalHours = eatingIntervalHours
        FoodIntake.mealsPerDay = mealsPerDay
        FoodIntake.firstReminderHours = firstReminderHours
        FoodIntake.firstReminderMinutes = firstReminderMinutes
    }

    // Assuming this code is inside an Activity class
    private fun showToast(context: Context) {
        mealsPerDay = sharedPreferences.getInt(PREF_MEALS_PER_DAY, 1)
        eatingIntervalHours = sharedPreferences.getInt(PREF_EATING_INTERVAL_HOURS, 0)
        eatingIntervalMinutes = sharedPreferences.getInt(PREF_EATING_INTERVAL_MINUTES, 0)
        firstReminderHours = sharedPreferences.getInt(PREF_FIRST_REMINDER_HOURS, 0)
        firstReminderMinutes = sharedPreferences.getInt(PREF_FIRST_REMINDER_MINUTES, 0)

        val message = "Meals per day: $mealsPerDay\n" +
                "Eating interval: $eatingIntervalHours hours $eatingIntervalMinutes minutes\n" +
                "First reminder: $firstReminderHours hours $firstReminderMinutes minutes"

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    fun setupFoodSelectionSpinner(
        dialogLayout: View,
        spinnerId: Int,
        spinnerOptions: Array<out Any>,
        callback: (Int) -> Unit // Add callback parameter
    ) {
        val mealOption = dialogLayout.findViewById<Spinner>(spinnerId)
        val adapterMF = ArrayAdapter(
            dialogLayout.context,
            R.layout.spinner_item_color,
            spinnerOptions.map { it.toString() }
        )
        adapterMF.setDropDownViewResource(R.layout.spinner_item_color)
        mealOption.adapter = adapterMF
        mealOption.dropDownVerticalOffset = mealOption.height

        mealOption.setOnTouchListener { _, _ ->
            if (mealOption.isPressed) {
                mealOption.background = dialogLayout.context.resources.getDrawable(R.drawable.food_intake_spinner_down)
            } else {
                mealOption.background = dialogLayout.context.resources.getDrawable(R.drawable.food_intake_spinner_down)
            }
            false
        }

        mealOption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedPosition = parent.selectedItemPosition
                if (selectedPosition == position) {
                    mealOption.background = dialogLayout.context.resources.getDrawable(R.drawable.food_intake_spinner_up)
                } else {
                    mealOption.background = dialogLayout.context.resources.getDrawable(R.drawable.food_intake_spinner_up)
                }
                // Call the callback with the selected position
                callback(selectedPosition)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                mealOption.background = dialogLayout.context.resources.getDrawable(R.drawable.food_intake_spinner_up)
            }
        }
    }



    fun resetListInput(context: Context) {
        FoodIntake.foodScheduleList = arrayListOf<FoodIntakeInfo>()

        for (i in 0 until FoodIntake.mealsPerDay) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

            // Resets the visibility of the buttons and the variable pointing to the button to be clicked
            with(prefs.edit()) {
                putInt(FoodIntake.BUTTON_TO_BE_CLICKED_KEY, 1)
                putInt("timeToEatHour_$i", 0)
                putInt("timeToEatMinute_$i", 0)
                putString("timeToEatMeridiem_$i", "")

                putBoolean("editTimeVisible_$i", true)
                putBoolean("eatButtonVisible_$i", true)
                putBoolean("checkVisible_$i", false)
                apply()
            }

            FoodIntake.buttonToBeClicked = 1
            val intakeInfo = FoodIntakeInfo(i + 1, 0, "", 0, "")
            FoodIntake.foodScheduleList.add(intakeInfo)
        }
        newRecyclerView.adapter = FoodIntakeAdapter(FoodIntake.foodScheduleList)
    }

}

