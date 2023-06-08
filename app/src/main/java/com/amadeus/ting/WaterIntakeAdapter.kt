package com.amadeus.ting

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.TimePicker
import androidx.recyclerview.widget.RecyclerView
import com.amadeus.ting.SleepSection.Companion.selectedWakeTime
import com.google.android.material.imageview.ShapeableImageView
import java.util.ArrayList

data class WaterIntakeInfo(
    var time : String,
    var numberOfLiters : String)


class WaterIntakeAdapter(private val waterIntakeData : ArrayList<WaterIntakeInfo>) : RecyclerView.Adapter<WaterIntakeAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.water_intake_rv, parent, false)
        val holder = MyViewHolder(itemView)

        // Set the RecyclerView to have a fixed size to disable infinite scrolling
        holder.itemView.isClickable = true
        holder.itemView.isFocusable = true
        holder.itemView.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)

        return holder
    }

    override fun getItemCount(): Int {
        return waterIntakeData.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = waterIntakeData[position]

        val wakeTimeParts = currentItem.time.split(":")
        val wakeHour = wakeTimeParts[0].toIntOrNull()
        val wakeMinute = wakeTimeParts[1].substringBefore(" ").toIntOrNull()
        val amPm = wakeTimeParts[1].substringAfterLast(" ")

        holder.timeInfo.text = String.format("%02d:%02d %s", wakeHour, wakeMinute, amPm)
        holder.numberMlData.text = currentItem.numberOfLiters
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val waterGlass : ImageView = itemView.findViewById(R.id.water_glass)
        val timeInfo : TextView = itemView.findViewById(R.id.time_info)
        val numberMlData : TextView = itemView.findViewById(R.id.number_ml_data)
    }

}