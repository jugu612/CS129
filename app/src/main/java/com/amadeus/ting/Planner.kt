package com.amadeus.ting


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.transition.TransitionManager
import android.transition.AutoTransition
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.amadeus.ting.databinding.ActivityPlannerBinding
import com.google.android.material.imageview.ShapeableImageView
import java.text.SimpleDateFormat
import java.util.*
import java.time.LocalDate
import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView


class Planner : AppCompatActivity(), CalendarAdapter.OnDateClickListener{

    private lateinit var binding: ActivityPlannerBinding
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var tskList: List<TaskModel>

    private var taskadapter: TaskAdapter? = null
    private var sortedTaskList: List<TaskModel> = emptyList()
    private var checkedTaskList: List<TaskModel> = emptyList()
    private var isDoneTasksVisible = false


    private val calendarData = CalendarData()
    private val notificationSystem:NotificationSystem = NotificationSystem(this)
    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){
            isGranted: Boolean ->
        if(isGranted){
            notificationSystem.showNotification()
        }
        else{
            Toast.makeText(this, "Notifications currently disabled.", Toast.LENGTH_LONG).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planner)
        window.statusBarColor = ContextCompat.getColor(this, R.color.red)

        binding = ActivityPlannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val dbHelper = TingDatabase(applicationContext)

        setUpAdapter()
        setUpClickListener()
        setUpCalendar()
        initRecyclerView()

        val notifButton = findViewById<ShapeableImageView>(R.id.notifreq)
        notifButton.setOnClickListener{
            when{
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED ->{
                    notificationSystem.showNotification()
                }
                else->{
                    requestPermissionsLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                }
            }
        }


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
            dbHelper.getAllChecks()
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

        val textViewDone = findViewById<ToggleButton>(R.id.textView_Done)

        textViewDone.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkedTaskList = dbHelper.getAllCheckedTasks()
                taskadapter?.addList(checkedTaskList)
                isDoneTasksVisible = true
                val color = ContextCompat.getColor(this, R.color.red)
                textViewDone.backgroundTintList = ColorStateList.valueOf(color) // Set the desired color when isChecked is false
            } else {
                tskList = dbHelper.getAllTasks()
                taskadapter?.addList(tskList)
                isDoneTasksVisible = false
                val color = ContextCompat.getColor(this, R.color.black)
                textViewDone.backgroundTintList = ColorStateList.valueOf(color) // Set the desired color when isChecked is true
            }
        }


        onClick<ShapeableImageView>(R.id.back_button){
            val goToHomePage = Intent(this, HomePage::class.java)
            startActivity(goToHomePage)
        }


    }

    private fun initRecyclerView(){
        recyclerView = findViewById<RecyclerView>(R.id.Tasklist)
        recyclerView.layoutManager = LinearLayoutManager(this)
        taskadapter  = TaskAdapter(this)
        recyclerView.adapter = taskadapter
    }


    //Setting up the calendar adapter
    private fun setUpClickListener() {
        val currentDate = Calendar.getInstance(Locale.ENGLISH)
        binding.ivCalendarNext.setOnClickListener {
            calendarData.currentDate.add(Calendar.MONTH, 1)
            setUpCalendar()
        }
        binding.ivCalendarPrevious.setOnClickListener {
            calendarData.currentDate.add(Calendar.MONTH, -1)
            if (calendarData.currentDate == currentDate)
                setUpCalendar()
            else
                setUpCalendar()
        }
    }
    private fun setUpAdapter() {
        //For positioning the recyclerview
        val curDate = LocalDate.now()
        val defPos = curDate.dayOfMonth-3

        // Horizontal spacing for each date in the calendar
        val dateSpacing = resources.getDimensionPixelSize(R.dimen.single_calendar_margin)
        binding.calendarRecycler.addItemDecoration(HorizontalItemDecoration(dateSpacing))

        // Center snap for scrolling
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.calendarRecycler)



        calendarAdapter = CalendarAdapter({ calendarDateModel: CalendarDateModel, position ->
            calendarData.calendarList.forEachIndexed { index, calendarModel ->
                calendarModel.isSelected = index == position
            }
            calendarAdapter.setData(calendarData.calendarList)
        }, this)

        binding.calendarRecycler.adapter = calendarAdapter
        binding.calendarRecycler.scrollToPosition(defPos)
        // Line below requires debugging, need to check why it doesn't function
    }
    private fun setUpCalendar() {
        val calendarList = java.util.ArrayList<CalendarDateModel>()
        binding.tvDateMonth.text = calendarData.dateFormat.format(calendarData.currentDate.time)
        val monthCalendar = calendarData.currentDate.clone() as Calendar
        val maxDaysInMonth = calendarData.currentDate.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendarData.dates.clear()
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        while (calendarData.dates.size < maxDaysInMonth) {
            calendarData.dates.add(monthCalendar.time)
            calendarList.add(CalendarDateModel(monthCalendar.time))
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        calendarData.calendarList.clear()
        calendarData.calendarList.addAll(calendarList)
        calendarAdapter.setData(calendarList)
    }

    private inline fun <reified T : View> Activity.onClick(id: Int, crossinline action: (T) -> Unit) {
        findViewById<T>(id)?.setOnClickListener {
            action(it as T)
        }
    }

    //Method called by the adapter to display the tasks for each date
    override fun onDateClick(position: Int) {
        //Add the date here
        val dateModel = calendarAdapter.getItem(position)
        taskadapter?.addList(tskList, dateModel)

    }

}