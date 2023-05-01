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
    private var selectedPosition = -1

    val current = LocalDate.now()
    inner class CalendarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(calendarDateModel: CalendarDateModel, taskList:List<TaskModel>) {


            val calendarDay = itemView.findViewById<TextView>(R.id.tv_calendar_day)
            val calendarDate = itemView.findViewById<TextView>(R.id.tv_calendar_date)
            val cardView = itemView.findViewById<LinearLayout>(R.id.circle_cal)
            val dateStatus = itemView.findViewById<RecyclerView>(R.id.date_status)
            val curDate = current.dayOfMonth




            //Database instance to grab the date and convert it to a usable string
            if(calendarDateModel.calendarDate == curDate.toString()){
                calendarDateModel.isSelected = true
            }

            if (calendarDateModel.isSelected) {
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
                selectedPosition = adapterPosition

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

        val filteredTasks = taskList.filter { taskModel ->
            taskModel.taskDate == list[position].calendarDate
        }

        holder.bind(list[position], filteredTasks)

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

    fun showTasks(){
        // Shows tasks on selected date
    }

}
