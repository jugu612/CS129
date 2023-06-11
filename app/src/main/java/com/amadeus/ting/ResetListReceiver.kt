package com.amadeus.ting

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.util.*

class ResetListReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        println("=========================================== resetListReceiver")
        val currentTime = Calendar.getInstance()

        // Get WakeTime Data
        val sleepSharedPref = context.getSharedPreferences("SleepData", Context.MODE_PRIVATE)
        val wakeTimeFromSleep = sleepSharedPref.getString("WakeTime", null)

        val wakeTimeParts = wakeTimeFromSleep?.split(":")
        val wakeHour = wakeTimeParts?.get(0)?.toIntOrNull()
        val wakeMinute = wakeTimeParts?.get(1)?.substringBefore(" ")?.toIntOrNull()
        val wakeAMPM = wakeTimeParts?.get(1)?.substringAfter(" ")?.trim()

        // Check if all the necessary values are not null
        if (wakeHour != null && wakeMinute != null && wakeAMPM != null) {

            if (currentTime.get(Calendar.HOUR) == wakeHour && currentTime.get(Calendar.MINUTE) == wakeMinute
                && currentTime.get(Calendar.AM_PM) == getAMPMValue(wakeAMPM)) {

                val foodIntakeActivity = FoodIntake.getInstance()
                val prefs = context.getSharedPreferences("FoodIntakePrefFile", Context.MODE_PRIVATE)

                val checkVisibility = Array(FoodIntake.mealsPerDay) { false }
                val timeToEatHours  = Array(FoodIntake.mealsPerDay) {0}
                val timeToEatMinutes  = Array(FoodIntake.mealsPerDay) {0}
                val timeToEatMeridiem = Array(FoodIntake.mealsPerDay) {""}
                val databaseTing = TingDatabase(context)

                // Current Day-Year-Month
                val year = currentTime.get(Calendar.YEAR)
                val month = currentTime.get(Calendar.MONTH)
                val dayOfMonth = currentTime.get(Calendar.DAY_OF_MONTH)
                for (i in 0 until FoodIntake.mealsPerDay) { // Save to Database
                    checkVisibility[i] = prefs.getBoolean("checkVisible_${i}", false)
                    timeToEatHours[i] = prefs.getInt("timeToEatHour_${i}", 0)
                    timeToEatMinutes[i] = prefs.getInt("timeToEatMinute_${i}", 0)
                    timeToEatMeridiem[i] = prefs.getString("timeToEatMeridiem_${i}", "").toString()

                    val mealTimeModel = MealTimeModel(i + 1, "$month ${dayOfMonth - 1} $year", "${timeToEatHours[i]} ${timeToEatMinutes[i]} ${timeToEatMeridiem[i]}", checkVisibility[i])
                    databaseTing.addMealtimeData(mealTimeModel)
                }

                // Perform the reset operation
                FoodIntake.foodScheduleList = arrayListOf()
                for (i in 0 until FoodIntake.mealsPerDay) {
                    // Resets the visibility of the buttons and the variable pointing to the button to be clicked
                    with(prefs.edit()) {
                        putInt(com.amadeus.ting.FoodIntake.BUTTON_TO_BE_CLICKED_KEY, 1)
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
                FoodIntake.newRecyclerView.adapter = FoodIntakeAdapter(FoodIntake.foodScheduleList)

                // Display a toast message
                val toastMessage = "Wake-up time reached! List reset."
                Toast.makeText(FoodIntake.appContext, toastMessage, Toast.LENGTH_SHORT).show()

            }
        }

    }

    private fun getAMPMValue(ampm: String): Int {
        return if (ampm.equals("PM", ignoreCase = true)) { Calendar.PM } else { Calendar.AM }
    }

}