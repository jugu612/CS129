package com.amadeus.ting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Color
import android.widget.LinearLayout


class TaskAdapter: RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private var taskList: List<TaskModel> = ArrayList()
    private var onClickItem: ((TaskModel) ->Unit)?=null

    fun addList(lists: List<TaskModel>){
        this.taskList = lists
        notifyDataSetChanged()
    }

    fun setOnClickItem(callback: (TaskModel)->Unit){
        this.onClickItem = callback
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


    }
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        var currentItem = taskList[position]
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
                val dbHelper = TaskDatabase(holder.itemView.context)
                dbHelper.deleteTask(currentItem)
                taskList = taskList.filter { it.taskId != currentItem.taskId }
                notifyDataSetChanged()
            }
            builder.setNegativeButton("No") { _, _ -> }
            builder.show()
        }
        when (currentItem.taskLabel) {
            "☆ Label" -> holder.layout.setBackgroundColor(Color.parseColor("#C3B1E1"))
            "♡ Personal" -> holder.layout.setBackgroundColor(Color.parseColor("#AEC6CF"))
            "\uD83C\uDF82 Birthday" -> holder.layout.setBackgroundColor(Color.parseColor("#F4949E"))
            else -> holder.layout.setBackgroundColor(Color.parseColor("#00917C"))
        }
    }

    override fun getItemCount() = taskList.size
}
