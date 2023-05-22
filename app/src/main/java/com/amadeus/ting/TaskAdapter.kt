package com.amadeus.ting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Color
import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.widget.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList


class TaskAdapter(private val context: Context) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private val dbHelper = TingDatabase(context)
    public var taskList: List<TaskModel> = ArrayList()
    private var onClickItem: ((TaskModel) ->Unit)?=null
    private var selectedDate: String? = null

    //Takes the date attribute from the current calendarDateModel and performs a filter on the list of tasks
    fun addList(lists: List<TaskModel>, calendarDateModel: CalendarDateModel?=null) {
        //Clicking on a date in the horizontal calendar filters the tasks by the selected date
        if(calendarDateModel != null){
            val filteredList = lists.filter {it.taskDate.substringBefore("  |") == calendarDateModel.calendarDatefull}
            this.taskList = filteredList
        }
        //Shows all tasks by default
        else{
            this.taskList = lists
        }
        notifyDataSetChanged()
    }

    fun clearList() {
        this.taskList = ArrayList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        var itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_tasks, parent, false)
        return TaskViewHolder(itemView)
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleTextView: TextView = itemView.findViewById(R.id.view_title)
        var detailsTextView: TextView = itemView.findViewById(R.id.view_details)
        var dateTextView: TextView = itemView.findViewById(R.id.view_date)
        var labelTextView: TextView = itemView.findViewById(R.id.view_label)
        var layout = itemView.findViewById<LinearLayout>(R.id.task_layout)
        var btnDeleteTask = itemView.findViewById<Button>(R.id.btnDeleteTask)
        var btnEditTask = itemView.findViewById<Button>(R.id.btnEditTask)


    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        var currentItem = taskList[position]
        //Glide.with(context).load(currentItem.image).into(holder.)
        holder.titleTextView.text = currentItem.taskTitle
        holder.detailsTextView.text = currentItem.taskDetails
        holder.dateTextView.text = currentItem.taskDate
        holder.labelTextView.text = currentItem.taskLabel
        holder.itemView.setOnClickListener{onClickItem?.invoke(currentItem)}
        holder.btnDeleteTask.setOnClickListener {

            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Delete Task")
            builder.setMessage("Are you sure you want to delete this task?")
            builder.setPositiveButton("Yes") { _, _ ->
                dbHelper.deleteTask(currentItem)
                taskList = taskList.filter { it.taskId != currentItem.taskId }
                notifyDataSetChanged()
            }
            builder.setNegativeButton("No") { _, _ -> }
            builder.show()
        }
        holder.btnEditTask.setOnClickListener {
            edit_task(currentItem, holder.itemView)
        }

        when (currentItem.taskLabel) {
            "☆ Label" -> holder.layout.setBackgroundColor(Color.parseColor("#C3B1E1"))
            "♡ Personal" -> holder.layout.setBackgroundColor(Color.parseColor("#AEC6CF"))
            "\uD83C\uDF82 Birthday" -> holder.layout.setBackgroundColor(Color.parseColor("#F4949E"))
            else -> holder.layout.setBackgroundColor(Color.parseColor("#00917C"))
        }
    }


    private fun edit_task(currentItem: TaskModel, itemView: View) {
        val inflater = LayoutInflater.from(itemView.context)
        val dialogLayout = inflater.inflate(R.layout.create_popupwindow, itemView as ViewGroup, false)
        val editTitle = dialogLayout.findViewById<EditText>(R.id.edit_title)
        val editDetails = dialogLayout.findViewById<EditText>(R.id.edit_details)
        val dateButton = dialogLayout.findViewById<Button>(R.id.dateButton)
        val labelSpinner = dialogLayout.findViewById<Spinner>(R.id.task_spinner)

        editTitle.setText(currentItem.taskTitle)
        editDetails.setText(currentItem.taskDetails)
        dateButton.text = currentItem.taskDate
        val dateOpt = DatePick(dateButton)
        dateOpt.DefaultDate()
        dateOpt.pickDate()
        val labelIndex = getLabelIndex(currentItem.taskLabel)
        labelSpinner.setSelection(labelIndex)

        val builder = AlertDialog.Builder(itemView.context)

        builder.setView(dialogLayout)
        builder.setCancelable(false)
        val cancelButton = dialogLayout.findViewById<Button>(R.id.cancel_button)
        val dialog = builder.create()
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        val saveButton = dialogLayout.findViewById<Button>(R.id.save_button)
        saveButton.setOnClickListener {
            val title = editTitle?.text.toString()
            val details = editDetails?.text.toString()
            val date = dateButton?.text.toString()
            val label = labelSpinner?.selectedItem.toString()


            val updatedTask = TaskModel(currentItem.taskId, title, details, date, label)
            dbHelper.updateTask(updatedTask)
            val index = taskList.indexOf(currentItem)
            taskList = taskList.toMutableList().also { it[index] = updatedTask }
            notifyDataSetChanged()
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }




    private fun getLabelIndex(label: String): Int {
        return when (label) {
            "☆ Label" -> 0
            "♡ Personal" -> 1
            "\uD83C\uDF82 Birthday" -> 2
            else -> 3
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

}
