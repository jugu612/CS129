package com.amadeus.ting
import android.annotation.SuppressLint
import android.app.AlertDialog
import java.util.*
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView


data class FoodIntakeInfo(
    var foodIntakeInfoNumber: Int,
    var timeIntervalHours : Int,
    var timeIntervalColon : String,
    var timeIntervalMinutes : Int,
    var timeIntervalMeridiem : String)



// A custom RecyclerView adapter that takes an ArrayList of FoodIntakeInfo objects as input and binds them to a layout
class FoodIntakeAdapter(private val foodIntakeSchedule : ArrayList<FoodIntakeInfo>) : RecyclerView.Adapter<FoodIntakeAdapter.MyViewHolder>() {


    // called when a new view holder is created for the first time
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_food_intake_schedule, parent, false)
        val holder = MyViewHolder(itemView)

        // Set the RecyclerView to have a fixed size to disable infinite scrolling
        holder.itemView.isClickable = true
        holder.itemView.isFocusable = true
        holder.itemView.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)

        return holder
    }
    // called to update the contents of a view holder when the data at a particular position changes
    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        // Get the FoodIntakeInfo object at the current position in the ArrayList
        val currentItem = foodIntakeSchedule[position]

        // Set the text of various TextViews in the ViewHolder to the corresponding properties of the FoodIntakeInfo object
        holder.intervalNumber.text = currentItem.foodIntakeInfoNumber.toString()
        holder.timeToEatHour.text = currentItem.timeIntervalHours.toString()
        holder.timeToEatMinute.text = String.format("%02d", currentItem.timeIntervalMinutes)
        holder.timeToEatMeridiem.text = currentItem.timeIntervalMeridiem
        holder.timeToEatColon.text = ":"

        // Restore the visibility settings of the buttons from SharedPreferences
        val prefs = holder.itemView.context.getSharedPreferences(FoodIntake.PREFS_NAME, Context.MODE_PRIVATE)
        holder.editTime.visibility = if (prefs.getBoolean("editTimeVisible_$position", true)) View.VISIBLE else View.GONE
        holder.eatButton.visibility = if (prefs.getBoolean("eatButtonVisible_$position", true)) View.VISIBLE else View.GONE
        holder.check.visibility = if (prefs.getBoolean("checkVisible_$position", false)) View.VISIBLE else View.GONE

        // Condition if the food intake is reset and start of the day (outputs blank in recycler view)
        if (currentItem.timeIntervalHours.toString() == "0" && currentItem.timeIntervalMinutes.toString() == "0"
            && currentItem.timeIntervalMeridiem == "") {
            holder.timeToEatHour.text = ""
            holder.timeToEatMinute.text = "      -"
            holder.timeToEatMeridiem.text = ""
            holder.timeToEatColon.text = ""
        }

        // Edit Button
        holder.editTime.setOnClickListener {
            // Create an AlertDialog.Builder object
            val builder = AlertDialog.Builder(holder.itemView.context)

            // Create a TimePicker view
            val timePicker = TimePicker(holder.itemView.context)

            // Set the TimePicker view as the dialog view
            builder.setView(timePicker)
            timePicker.setIs24HourView(false)


            timePicker.hour = currentItem.timeIntervalHours
            timePicker.minute = currentItem.timeIntervalMinutes

            // Set the positive button text and action
            builder.setPositiveButton("OK") { dialog, _ ->
                // Handle positive button click
                val hour = ((timePicker.hour + 11) % 12 + 1).toString()
                val minute = String.format("%02d", timePicker.minute)
                val amPm = if (timePicker.hour < 12) "AM" else "PM"

                // sets the new clock time
                holder.timeToEatHour.text = hour
                holder.timeToEatColon.text = ":"
                holder.timeToEatMinute.text = minute
                holder.timeToEatMeridiem.text = amPm

                // Save the updated value of buttonToBeClicked to SharedPreferences
                saveDataPreferencesAfterClick(prefs, position, hour.toInt(), minute.toInt(), amPm, 1)

                // Updates the other food intake schedule below it
                updateOtherFoodIntake(prefs, position)
                notifyDataSetChanged() // Refreshes the recycler view to show the updated values immediately

            }

            // Set the negative button text and action
            builder.setNegativeButton("Cancel") { dialog, _ ->
            }

            val dialog = builder.create()
            dialog.show()

        }

        // Eat Button
        holder.eatButton.setOnClickListener {

            // Checks validity of the eat button clicked (must be an appropriate FoodIntakeSchedule)
            if (FoodIntake.buttonToBeClicked == currentItem.foodIntakeInfoNumber) {

                // edits visibility of the views affected
                holder.editTime.visibility =  View.GONE
                holder.eatButton.visibility = View.GONE
                holder.check.visibility = View.VISIBLE

                // gets the current time of the device and outputs it
                val calendar = Calendar.getInstance()
                val hours = calendar.get(Calendar.HOUR)
                val minutes = calendar.get(Calendar.MINUTE)
                val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"

                // sets the new edited time
                holder.timeToEatHour.text = hours.toString()
                holder.timeToEatMinute.text = String.format("%02d", minutes)
                holder.timeToEatMeridiem.text = amPm
                holder.timeToEatColon.text = ":"

                // Save the updated value of buttonToBeClicked to SharedPreferences
                saveDataPreferencesAfterClick(prefs, position, hours, minutes, amPm, 0)

                // Updates the other food intake schedule below it
                updateOtherFoodIntake(prefs, FoodIntake.buttonToBeClicked - 1)
                notifyDataSetChanged() // Refreshes the recycler view to show the updated values immediately

                FoodIntake.buttonToBeClicked += 1 // moves to the next button to be clicked
                Toast.makeText(holder.itemView.context, "Eat Completed!!\nGood Job!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(holder.itemView.context, "Eat failed.. Complete Interval #${FoodIntake.buttonToBeClicked} first.", Toast.LENGTH_SHORT).show()
            }

        }
    }


    // getItemCount returns the number of items in the ArrayList
    override fun getItemCount(): Int {
        return foodIntakeSchedule.size
    }

    private fun updateOtherFoodIntake(prefs : SharedPreferences, buttonToBeClickedNumber : Int) {

        // get data of the previous food intake interval from shared preferences
        val previousFoodInterval = buttonToBeClickedNumber
        var previousFoodIntakeHours = prefs.getInt("timeToEatHour_${previousFoodInterval}", 0)
        var previousFoodIntakeMinutes = prefs.getInt("timeToEatMinute_${previousFoodInterval}", 0)
        var previousFoodIntakeMeridiem: String = prefs.getString("timeToEatMeridiem_${previousFoodInterval}", "") ?: ""

        // fixes the visual bug: the time clicked of the first FoodIntake Schedule is not shown
        FoodIntake.foodScheduleList[previousFoodInterval].timeIntervalHours = previousFoodIntakeHours
        FoodIntake.foodScheduleList[previousFoodInterval].timeIntervalMinutes = previousFoodIntakeMinutes
        FoodIntake.foodScheduleList[previousFoodInterval].timeIntervalColon = ":"
        FoodIntake.foodScheduleList[previousFoodInterval].timeIntervalMeridiem = previousFoodIntakeMeridiem


        var changeMeridiem = ""
        var didChangeMeridiem = false
        // iterates through the next food intake schedules and calculates the time
        for (i in buttonToBeClickedNumber + 1 until itemCount) {

            var newFoodIntakeHours = previousFoodIntakeHours + FoodIntake.eatingIntervalHours
            var newFoodIntakeMinutes = previousFoodIntakeMinutes + FoodIntake.eatingIntervalMinutes

            // Calculates Minutes
            if (newFoodIntakeMinutes >= 60) {
                newFoodIntakeMinutes -= 60
                newFoodIntakeHours += 1
            }

            // Calculates Hours
            if (newFoodIntakeHours > 12) {
                newFoodIntakeHours -= 12
                FoodIntake.foodScheduleList[i].timeIntervalMeridiem = if (previousFoodIntakeMeridiem == "AM") { "PM" } else { "AM" }
                changeMeridiem = FoodIntake.foodScheduleList[i].timeIntervalMeridiem
                didChangeMeridiem = true
            } else if (newFoodIntakeHours == 12) {
                FoodIntake.foodScheduleList[i].timeIntervalMeridiem = if (previousFoodIntakeMeridiem == "AM") { "PM" } else { "AM" }
                changeMeridiem = FoodIntake.foodScheduleList[i].timeIntervalMeridiem
                didChangeMeridiem = true
            }

            // Store the new values of minutes, hours, colon, and meridiem
            FoodIntake.foodScheduleList[i].timeIntervalHours = newFoodIntakeHours
            FoodIntake.foodScheduleList[i].timeIntervalMinutes = newFoodIntakeMinutes
            FoodIntake.foodScheduleList[i].timeIntervalColon = ":"

            if (!didChangeMeridiem) {
                FoodIntake.foodScheduleList[i].timeIntervalMeridiem = previousFoodIntakeMeridiem
            } else {
                FoodIntake.foodScheduleList[i].timeIntervalMeridiem = changeMeridiem
            }


            // save the updated data in shared preferences
            with(prefs.edit()) {
                putInt("timeToEatHour_$i", newFoodIntakeHours)
                putInt("timeToEatMinute_$i", newFoodIntakeMinutes)
                putString("timeToEatMeridiem_$i", FoodIntake.foodScheduleList[i].timeIntervalMeridiem)
                putString("timeToEatColon_$i", ":")
                apply()
            }

            previousFoodIntakeHours = newFoodIntakeHours
            previousFoodIntakeMinutes = newFoodIntakeMinutes

        }
    }

    // save the data in the shared preferences so that the data will be retained even if the app is closed
    private fun saveDataPreferencesAfterClick(prefs : SharedPreferences, position : Int, hours : Int, minutes : Int, amPm : String, isEdit : Int) {

        with(prefs.edit()) {
            putInt(FoodIntake.BUTTON_TO_BE_CLICKED_KEY, FoodIntake.buttonToBeClicked)

            // doesn't change the button visibility if inside the edit button
            if (isEdit == 0) {
                putBoolean("editTimeVisible_$position", false)
                putBoolean("eatButtonVisible_$position", false)
                putBoolean("checkVisible_$position", true)
            }

            putInt("timeToEatHour_$position", hours)
            putInt("timeToEatMinute_$position", minutes)
            putString("timeToEatMeridiem_$position", amPm)
            putString("timeToEatColon_$position", "")
            apply()
        }
    }

    //  inner class that extends RecyclerView.ViewHolder and holds references to views in the layout
    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val editTime : ShapeableImageView = itemView.findViewById(R.id.edit_time)
        val eatButton : Button = itemView.findViewById(R.id.eat_button)
        val check : ImageView = itemView.findViewById(R.id.food_intake_check)
        val timeToEatHour : TextView = itemView.findViewById(R.id.time_to_eat_hour)
        val timeToEatMinute : TextView = itemView.findViewById(R.id.time_to_eat_minute)
        val timeToEatMeridiem : TextView = itemView.findViewById(R.id.time_to_eat_meridiem)
        val intervalNumber : TextView = itemView.findViewById(R.id.interval_number)
        val timeToEatColon : TextView = itemView.findViewById(R.id.time_to_eat_colon)
    }

}