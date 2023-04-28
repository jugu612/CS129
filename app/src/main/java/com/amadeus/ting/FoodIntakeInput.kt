package com.amadeus.ting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.imageview.ShapeableImageView
import android.widget.ArrayAdapter

class FoodIntakeInput : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_food_intake)

        onClick<ShapeableImageView>(R.id.back_button) {
            val goToFoodIntake = Intent(this, FoodIntake::class.java)
            startActivity(goToFoodIntake)
        }
        //Meal Frequency options:
        val mealFrequencyList = mutableListOf<String>()
        for (i in 1..10) {
            val mealfreq = if (i == 1) "$i meal" else "$i meals"
            mealFrequencyList.add(mealfreq)
        }
        val mealFrequencyOptions= mealFrequencyList.toTypedArray()
        setupFoodSelectionSpinner(this, R.id.meals_per_day_button, mealFrequencyOptions)


        //Eating Interval Options
        val intervalOptions = mutableListOf<String>()
        for (h in 0..23) {
            for (m in 0..59 step 1) {
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

        setupFoodSelectionSpinner(this, R.id.eating_intervals_button, eatingIntervalOptions)

        //First Notification Reminder Options
        val firstReminderList = mutableListOf<String>()
        firstReminderList.add("On time")
        for (h in 0..23) {
            for (m in 0..59 step 1) {
                val timeBefore = h * 60 + m
                if (timeBefore > 0) {
                    val hours = if (h == 1) "$h hr" else "$h hrs"
                    val minutes = if (m == 1) "$m min" else "$m mins"
                    val interval = if (h == 0) {
                        "$minutes before"
                    } else if (m == 0) {
                        "$hours before"
                    } else {
                        "$hours and $minutes before"
                    }
                    firstReminderList.add(interval)
                }
            }
        }
        val firstReminderOptions = firstReminderList.toTypedArray()

        setupFoodSelectionSpinner(this, R.id.first_reminder_button, firstReminderOptions)

    }



    fun setupFoodSelectionSpinner(activity: Activity, spinnerId: Int, spinnerOptions: Array<out Any>) {
        val mealOption = activity.findViewById<Spinner>(spinnerId)
        val adapterMF = ArrayAdapter(
            activity,
            R.layout.spinner_item_color,
            spinnerOptions.map { it.toString() }
        )
        adapterMF.setDropDownViewResource(R.layout.spinner_item_color)
        mealOption.adapter = adapterMF

        mealOption.setOnTouchListener { _, _ ->
            if (mealOption.isPressed) {
                mealOption.background = activity.resources.getDrawable(R.drawable.food_intake_spinner_down)
            } else {
                mealOption.background = activity.resources.getDrawable(R.drawable.food_intake_spinner_down)
            }
            false
        }

        mealOption.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedPosition = parent.selectedItemPosition
                if (selectedPosition == position) {
                    mealOption.background = activity.resources.getDrawable(R.drawable.food_intake_spinner_up)
                } else {
                    mealOption.background = activity.resources.getDrawable(R.drawable.food_intake_spinner_up)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                mealOption.background = activity.resources.getDrawable(R.drawable.food_intake_spinner_up)
            }
        })
    }



    private inline fun <reified T : View> Activity.onClick(id: Int, crossinline action: (T) -> Unit) {
        findViewById<T>(id)?.setOnClickListener {
            action(it as T)
        }
    }
}

