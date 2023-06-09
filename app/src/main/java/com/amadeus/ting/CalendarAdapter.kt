package com.amadeus.ting
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity

class CalendarAdapter(private val listener: (calendarDateModel: CalendarDateModel, position: Int) -> Unit,
                      private var dateListener: OnDateClickListener) :
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {
    private val list = ArrayList<CalendarDateModel>()
    private var selectedPosition = -1

    init{
        this.dateListener = dateListener
    }
    //private var taskAdapter:TaskAdapter? = null

    val current = LocalDate.now()


    //Interface extended by the Planner activity
    interface OnDateClickListener{
        fun onDateClick(position: Int)
    }
    //Inner viewHolder class
    inner class CalendarViewHolder(view: View, private var dateListener: OnDateClickListener) : RecyclerView.ViewHolder(view){
        //internal dateListener constructor for the viewholder (crucial step for setting up the onClickListener)
        init{
            this.dateListener = dateListener
        }


        fun bind(calendarDateModel: CalendarDateModel, color: String) {

            val calendarDay = itemView.findViewById<TextView>(R.id.tv_calendar_day)
            val calendarDate = itemView.findViewById<TextView>(R.id.tv_calendar_date)
            val cardView = itemView.findViewById<LinearLayout>(R.id.circle_cal)
            val curDate = current.dayOfMonth

            var onPressColor = R.color.red

            if(color == "Blue"){
                onPressColor = R.color.cyan
            }


            if(calendarDateModel.isSelected && calendarDateModel.calendarDate == curDate.toString()){
                calendarDay.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.white
                    )
                )
                calendarDate.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.white
                    )
                )
                cardView.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.gray
                    )
                )
            }

            else if (calendarDateModel.isSelected) {
                calendarDay.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.white
                    )
                )
                calendarDate.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.white
                    )
                )
                cardView.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        onPressColor
                    )
                )
            }


            else {
                calendarDay.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.black
                    )
                )
                calendarDate.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.black
                    )
                )
                cardView.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.white
                    )
                )
            }

            calendarDay.text = calendarDateModel.calendarDay
            calendarDate.text = calendarDateModel.calendarDate
            cardView.setOnClickListener {
                selectedPosition = adapterPosition

                // invoke the click listener with the clicked model and its position
                listener.invoke(calendarDateModel, adapterPosition)
                dateListener.onDateClick(selectedPosition)


                // notify the adapter that the data set has changed
                notifyDataSetChanged()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_calendar_date, parent, false)
        return CalendarViewHolder(view, dateListener)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val color = when (holder.itemView.context) {
            is FoodIntake, is SleepSection -> "Blue"
            is Planner -> "Red"
            is WaterIntake -> "Blue"
            else -> null
        }
        if (color != null) {
            holder.bind(list[position], color)
        }

        list[position].isSelected = selectedPosition == position
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(calendarList: ArrayList<CalendarDateModel>) {
        list.clear()
        list.addAll(calendarList)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): CalendarDateModel{
        return list[position]
    }

}
