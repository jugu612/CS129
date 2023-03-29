package com.amadeus.ting


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcelable
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
import com.amadeus.ting.MyAlertDialog
import com.google.android.gms.tasks.Task
import java.io.Serializable


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
    private lateinit var tskList: List<TaskModel>
    private var sortedTaskList: List<TaskModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planner)
        binding = ActivityPlannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val dbHelper = TaskDatabase(applicationContext)
        setUpAdapter()
        setUpClickListener()
        setUpCalendar()
        initRecyclerView()

        // Get the shared preferences object with the name "MyPreferences"
        val sharedPref = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        // Get the values stored in shared preferences for button press, alphabetical arrow and deadline arrow
        val buttonValue = sharedPref.getInt("buttonPressed", -1)
        val getAlphabeticalArrow = sharedPref.getInt("isAlphabeticalArrowUp", -1)
        val getDeadlineArrow = sharedPref.getInt("isDeadlineArrowUp", -1)

        // Initialize the task list based on the shared preferences values
        tskList = if (buttonValue != -1) {
            if (buttonValue == 1 && getAlphabeticalArrow != -1) {
                dbHelper.getAllTasks(1, getAlphabeticalArrow) // Get all tasks sorted by alphabetical order
            } else if (buttonValue == 2 && getDeadlineArrow != -1) {
                dbHelper.getAllTasks(2, getDeadlineArrow) // Get all tasks sorted by deadline
            } else {
                dbHelper.getAllTasks() // Get all tasks
            }
        } else {
            dbHelper.getAllTasks() // Get all tasks
        }

        // Add the task list to the adapter
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

        // Set an onClick listener for the sort button
        onClick<ShapeableImageView>(R.id.sort_button) {
            val sortLabelAlert = MyAlertDialog()
            sortLabelAlert.sortAlertDialog(this, R.layout.sort_popupwindow, R.layout.sort_nestedpopupwindow, R.id.text_label, sharedPref) {

                // Get the sorted task list from the alert dialog
                sortedTaskList = sortLabelAlert.getTaskList()

                // Add the sorted task list to the adapter and update the current task list
                taskadapter?.addList(sortedTaskList)
                tskList = sortedTaskList

                // Update the shared preferences with the button press and arrow direction states
                val editor = sharedPref.edit()
                editor.putInt("buttonPressed", sortLabelAlert.getButtonPressed())
                editor.putInt("isAlphabeticalArrowUp", sortLabelAlert.getAlphabeticalArrowState())
                editor.putInt("isDeadlineArrowUp", sortLabelAlert.getDeadlineArrowState())
                editor.apply()
            }
        }


        onClick<ShapeableImageView>(R.id.back_button){
            val goToHomePage = Intent(this, HomePage::class.java)
            startActivity(goToHomePage)
        }

    }

    private fun updateTaskList() {
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