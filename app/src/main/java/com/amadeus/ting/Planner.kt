package com.amadeus.ting

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.imageview.ShapeableImageView
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Button
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.amadeus.ting.databinding.ActivityPlannerBinding

class Planner : AppCompatActivity() {
    // Initializing horizontal calendar
    private lateinit var binding: ActivityPlannerBinding
    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val cal = Calendar.getInstance(Locale.ENGLISH)
    private val currentDate = Calendar.getInstance(Locale.ENGLISH)
    private val dates = ArrayList<Date>()
    private lateinit var adapter: CalendarAdapter
    private val calendarList2 = ArrayList<CalendarDateModel>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planner)
        binding = ActivityPlannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpAdapter()
        setUpClickListener()
        setUpCalendar()

        // Create an instance of the SQLite class
        val dbHelper = TaskDatabase(applicationContext)
        // Create three tasks
        val task1 = TaskModel(1, "Go Home", "Milk, bread, eggs", "2023-03-22", "Chores")
        val task2 = TaskModel(2, "Take a sleep", "sheesh", "2023-03-24", "Me Time")
        val task3 = TaskModel(3, "Testing one two three....", "So what?", "2023-03-26", "Chores")
        val task4 = TaskModel(3, "ano baaaaaaa", "So what?", "2023-04-26", "Chores")
        val task5 = TaskModel(3, "aaaaaaa testing one two", "edi wag?", "2023-05-21", "Chores")

        // Insert the tasks into the database
        dbHelper.addTask(task1)
        dbHelper.addTask(task2)
        dbHelper.addTask(task3)
        dbHelper.addTask(task4)
        dbHelper.addTask(task5)

        // Retrieve all tasks from the database and print them to the console
        val allTasksAlphabetical = dbHelper.getAllTasksSortedAlphabetically()
        allTasksAlphabetical.forEach { task ->
            println("Tasks Alphabetical: ${task.taskName} (${task.deadline})")
        }

        val allTasksLabel = dbHelper.getTasksByLabel("Me Time")
        allTasksLabel.forEach { task ->
            println("Tasks Label: ${task.taskName} (${task.deadline})")
        }

        val allTasksDeadline = dbHelper.sortByDeadline()
        allTasksDeadline.forEach { task ->
            println("Tasks by Deadline: ${task.taskName} (${task.deadline})")
        }

        // Label -> Myka
        onClick<ShapeableImageView>(R.id.label_button) {
            val labelAlertDialog = MyAlertDialog()
            labelAlertDialog.showCustomDialog(this, R.layout.view_labels, R.layout.add_label, R.id.label_sample)
        }

        // Create -> Jugu
        onClick<ShapeableImageView>(R.id.create_button) {
            val labelAlert = MyAlertDialog()

            labelAlert.showCustomDialog(this, R.layout.create_popupwindow)

        }

        // Sort -> Dust
        onClick<ShapeableImageView>(R.id.sort_button) {
            val labelAlert = MyAlertDialog()

            labelAlert.showCustomDialog(this, R.layout.sort_popupwindow, R.layout.sort_nestedpopupwindow, R.id.text_label)

        }

    }
    private fun setUpClickListener() {
        binding.ivCalendarNext.setOnClickListener {
            cal.add(Calendar.MONTH, 1)
            setUpCalendar()
        }
        binding.ivCalendarPrevious.setOnClickListener {
            cal.add(Calendar.MONTH, -1)
            if (cal == currentDate)
                setUpCalendar()
            else
                setUpCalendar()
        }
    }

    private fun setUpAdapter() {
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.single_calendar_margin)
        binding.calendarRecycler.addItemDecoration(HorizontalItemDecoration(spacingInPixels))
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.calendarRecycler)
        adapter = CalendarAdapter { calendarDateModel: CalendarDateModel, position: Int ->
            calendarList2.forEachIndexed { index, calendarModel ->
                calendarModel.isSelected = index == position
            }
            adapter.setData(calendarList2)
        }
        binding.calendarRecycler.adapter = adapter
    }

    private fun setUpCalendar() {
        val calendarList = ArrayList<CalendarDateModel>()
        binding.tvDateMonth.text = sdf.format(cal.time)
        val monthCalendar = cal.clone() as Calendar
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        dates.clear()
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        while (dates.size < maxDaysInMonth) {
            dates.add(monthCalendar.time)
            calendarList.add(CalendarDateModel(monthCalendar.time))
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        calendarList2.clear()
        calendarList2.addAll(calendarList)
        adapter.setData(calendarList)
    }
    private inline fun <reified T : View> Activity.onClick(id: Int, crossinline action: (T) -> Unit) {
        findViewById<T>(id)?.setOnClickListener {
            action(it as T)
        }
    }

}

class MyAlertDialog {
    fun showCustomDialog(context: Context, popupLayout: Int, nestedPopupLayout: Int = -1, buttonToPress: Int = -1) {
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(popupLayout, null)

        val builder = AlertDialog.Builder(context, R.style.MyDialogStyle)
        builder.setView(dialogLayout)
        builder.setCancelable(false)

        val cancelButton = dialogLayout.findViewById<Button>(R.id.cancel_button)
        val dialog = builder.create()

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        // Nested Dialog: -1 if there is no need for nested dialog
        var nestedDialog: AlertDialog? = null
        if (nestedPopupLayout != -1) {

            val showNestedDialogButton = dialogLayout.findViewById<Button>(buttonToPress)

            showNestedDialogButton.setOnClickListener {
                val nestedDialogLayout = inflater.inflate(nestedPopupLayout, null)
                val nestedBuilder = AlertDialog.Builder(context, R.style.MyDialogStyle)
                nestedBuilder.setView(nestedDialogLayout)
                nestedBuilder.setCancelable(false)

                nestedDialog = nestedBuilder.create()
                nestedDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                nestedDialog?.show()

                val cancelButtonNested = nestedDialogLayout.findViewById<Button>(R.id.cancel_button)
                cancelButtonNested.setOnClickListener {
                    nestedDialog?.dismiss()
                }
            }
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

}
