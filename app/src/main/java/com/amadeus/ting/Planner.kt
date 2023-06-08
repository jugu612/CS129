package com.amadeus.ting


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.TransitionManager
import android.transition.AutoTransition
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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


class Planner : AppCompatActivity(), CalendarAdapter.OnDateClickListener{

    // Initializing horizontal calendar
    private lateinit var binding: ActivityPlannerBinding
    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val cal = Calendar.getInstance(Locale.ENGLISH)
    private val currentDate = Calendar.getInstance(Locale.ENGLISH)
    private val dates = ArrayList<Date>()
    private lateinit var calendarAdapter: CalendarAdapter
    private val calendarList2 = ArrayList<CalendarDateModel>()
    private lateinit var recyclerView: RecyclerView
    private var taskadapter: TaskAdapter? = null
    private lateinit var tskList: List<TaskModel>
    private var sortedTaskList: List<TaskModel> = emptyList()
    // Using a helper for the data class
    private lateinit var horizontalCalendar: HorizontalCalendar


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
        binding = ActivityPlannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val dbHelper = TingDatabase(applicationContext)

        horizontalCalendar = HorizontalCalendar()

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

        val textViewDone = findViewById<TextView>(R.id.textView_Done)

        textViewDone.setOnClickListener {
            val checkedTasks = dbHelper.getAllCheckedTasks()
            // Do something with the checked tasks, such as updating the UI
            // For example, you can log the task titles:
            checkedTasks.forEach { task ->
                Log.d("CheckedTask", task.taskTitle)
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
    /* Calendar Data Binding */
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
            calendarList2.forEachIndexed { index, calendarModel ->
                calendarModel.isSelected = index == position
            }
            calendarAdapter.setData(calendarList2)
        }, this)

        binding.calendarRecycler.adapter = calendarAdapter
        binding.calendarRecycler.scrollToPosition(defPos)
        // Line below requires debugging, need to check why it doesn't function
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