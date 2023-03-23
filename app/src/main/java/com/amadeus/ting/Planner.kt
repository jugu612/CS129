package com.amadeus.ting


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.amadeus.ting.databinding.ActivityPlannerBinding
import com.google.android.material.imageview.ShapeableImageView
import java.text.SimpleDateFormat
import java.util.*
import com.amadeus.ting.TaskDatabase



class Planner : AppCompatActivity(){
    // Initializing horizontal calendar
    private lateinit var binding: ActivityPlannerBinding
    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val cal = Calendar.getInstance(Locale.ENGLISH)
    private val currentDate = Calendar.getInstance(Locale.ENGLISH)
    private val dates = ArrayList<Date>()
    private lateinit var adapter: CalendarAdapter
    private val calendarList2 = ArrayList<CalendarDateModel>()
    private lateinit var recyclerView: RecyclerView
    private var taskadapter: TaskAdapter? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planner)
        binding = ActivityPlannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpAdapter()
        setUpClickListener()
        setUpCalendar()
        initRecyclerView()
        val dbHelper = TaskDatabase(applicationContext)

        val tskList = dbHelper.getAllTasks()
        taskadapter?.addList(tskList)

        // Label -> Myka
        onClick<ShapeableImageView>(R.id.label_button) {
            val labelAlertDialog = MyAlertDialog()
            labelAlertDialog.showCustomDialog(this, R.layout.view_labels, R.layout.add_label, R.id.label_sample)
        }

        // Create -> Jugu
        onClick<ShapeableImageView>(R.id.create_button) {
            val labelAlert = MyAlertDialog()

            labelAlert.showCustomDialog(this, R.layout.create_popupwindow, -1, -1, 1)
            taskadapter?.addList(tskList)
            updateTaskList()
        }

        // Sort -> Dust
        onClick<ShapeableImageView>(R.id.sort_button) {
            val labelAlert = MyAlertDialog()

            labelAlert.showCustomDialog(this, R.layout.sort_popupwindow, R.layout.sort_nestedpopupwindow, R.id.text_label)

        }

    }

    fun updateTaskList() {
        val dbHelper = TaskDatabase(applicationContext)
        val tskList = dbHelper.getAllTasks()
        taskadapter?.addList(tskList)
    }
    private fun initRecyclerView(){
        recyclerView = findViewById<RecyclerView>(R.id.Tasklist)
        recyclerView.layoutManager = LinearLayoutManager(this)
        taskadapter  = TaskAdapter()
        recyclerView.adapter = taskadapter

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
    private var taskadapter: TaskAdapter? = null


    fun showCustomDialog(context: Context, popupLayout: Int, nestedPopupLayout: Int = -1, buttonToPress: Int = -1, create: Int = -1) {
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

        if (create != -1){
            val dbHelper = TaskDatabase(context)
            val editTitle = dialogLayout.findViewById<EditText>(R.id.edit_title)
            val editDetails = dialogLayout.findViewById<EditText>(R.id.edit_details)
            val dateButton = dialogLayout.findViewById<Button>(R.id.dateButton)
            val labelSpinner = dialogLayout.findViewById<Spinner>(R.id.task_spinner)
            val dateOpt= DatePick(dateButton)
            dateOpt.DefaultDate()
            dateOpt.pickDate()



            // Create a new task with the input data

            val saveButton = dialogLayout.findViewById<Button>(R.id.save_button)
            saveButton.setOnClickListener {
                val title = editTitle.text.toString()
                val details = editDetails.text.toString()
                val date = dateButton.text.toString()
                val label = labelSpinner.selectedItem.toString()
                val task = TaskModel(0, taskTitle = title, taskDetails = details, taskDate = date, taskLabel = label)
                dbHelper.addTask(task) // Add the task to the database
                val gd = dbHelper.getAllTasks()
                taskadapter?.addList(gd)
                dialog.dismiss() // Close the dialog
            }

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


