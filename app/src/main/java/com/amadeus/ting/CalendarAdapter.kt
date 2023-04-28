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

class CalendarAdapter(private val listener: (calendarDateModel: CalendarDateModel, position: Int) -> Unit) :
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {
    private val list = ArrayList<CalendarDateModel>()
    private lateinit var dbHelper:TaskDatabase
    private lateinit var dialogLayout:View

    val current = LocalDate.now()
    inner class CalendarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(calendarDateModel: CalendarDateModel, taskList:List<TaskModel>) {


            val calendarDay = itemView.findViewById<TextView>(R.id.tv_calendar_day)
            val calendarDate = itemView.findViewById<TextView>(R.id.tv_calendar_date)
            val cardView = itemView.findViewById<LinearLayout>(R.id.circle_cal)
            val dateStatus = itemView.findViewById<LinearLayout>(R.id.test_calstatus)
            val dateStatus2 = itemView.findViewById<LinearLayout>(R.id.test_calstatus2)
            val curDate = current.dayOfMonth

            var curCalModel:CalendarDateModel




            //Database instance to grab the date and convert it to a usable string
            if(calendarDateModel.calendarDate == curDate.toString()){
                calendarDateModel.isSelected = true
            }

            if (calendarDateModel.isSelected) {
                curCalModel = calendarDateModel
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
                        R.color.red
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
                // reset the isSelected flag of all models
                list.forEach { it.isSelected = false }

                // set the isSelected flag of the clicked model to true
                calendarDateModel.isSelected = true

                // invoke the click listener with the clicked model and its position
                listener.invoke(calendarDateModel, adapterPosition)

                // notify the adapter that the data set has changed
                notifyDataSetChanged()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_calendar_date, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val dbHelper = TaskDatabase(holder.itemView.context)
        val taskList = dbHelper.getAllTasks()
        holder.bind(list[position], taskList)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(calendarList: ArrayList<CalendarDateModel>) {
        list.clear()
        list.addAll(calendarList)
        notifyDataSetChanged()
    }

    fun showTasks(){
        // Shows tasks on selected date
    }

}
