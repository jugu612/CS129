package com.amadeus.ting
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

data class FoodIntakeInfo(
    var foodIntakeInfoNumber: Int,
    var timeIntervalHours : Int,
    val timeIntervalMinutes : Int,
    val timeIntervalMeridiem : String)



class FoodIntakeAdapter(private val foodIntakeSchedule : ArrayList<FoodIntakeInfo>) : RecyclerView.Adapter<FoodIntakeAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.view_food_intake_schedule,
            parent,false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = foodIntakeSchedule[position]

        holder.intervalNumber.text = currentItem.foodIntakeInfoNumber.toString()
        //holder.eatAt.text = "EAT AT"
        holder.timeToEatHour.text = currentItem.timeIntervalHours.toString()
        holder.timeToEatMinute.text = currentItem.timeIntervalMinutes.toString()
        //holder.timeToEatColon.text = ":"
        holder.timeToEatMeridiem.text = currentItem.timeIntervalMeridiem.toString()

        // Set up click listener for edit button
        holder.editTime.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Edit button clicked!", Toast.LENGTH_SHORT).show()
        }

        // Set up click listener for eat button
        holder.eatButton.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Eat button clicked!", Toast.LENGTH_SHORT).show()
        }

    }

    // tells the adapter how many lists are there in the recycler view
    override fun getItemCount(): Int {
        return foodIntakeSchedule.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val editTime : ShapeableImageView = itemView.findViewById(R.id.edit_time)
        val cyanLine : View = itemView.findViewById(R.id.cyan_line)
        val eatButton : Button = itemView.findViewById(R.id.eat_button)

        val eatAt : TextView = itemView.findViewById(R.id.eat_at)
        val timeToEatHour : TextView = itemView.findViewById(R.id.time_to_eat_hour)
        val timeToEatColon : TextView = itemView.findViewById(R.id.time_to_eat_colon)
        val timeToEatMinute : TextView = itemView.findViewById(R.id.time_to_eat_minute)
        val timeToEatMeridiem : TextView = itemView.findViewById(R.id.time_to_eat_meridiem)

        val intervalNumber : TextView = itemView.findViewById(R.id.interval_number)

    }

}