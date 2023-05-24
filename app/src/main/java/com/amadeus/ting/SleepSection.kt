package com.amadeus.ting

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.amadeus.ting.databinding.ActivitySleepBinding
import com.google.android.material.imageview.ShapeableImageView
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class SleepSection : AppCompatActivity(), CalendarAdapter.OnDateClickListener {

    private lateinit var binding: ActivitySleepBinding
    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val cal = Calendar.getInstance(Locale.ENGLISH)
    private val currentDate = Calendar.getInstance(Locale.ENGLISH)
    private val dates = java.util.ArrayList<Date>()
    private lateinit var calendarAdapter: CalendarAdapter
    private val calendarList2 = ArrayList<CalendarDateModel>()
    private lateinit var recyclerView: RecyclerView
    private var taskadapter: TaskAdapter? = null
    private lateinit var tskList: List<TaskModel>
    private var sortedTaskList: List<TaskModel> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sleep)

        binding = ActivitySleepBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpAdapter()
        setUpClickListener()
        setUpCalendar()

        val backButton: ShapeableImageView = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            val goToHomePage = Intent(this, HomePage::class.java)
            startActivity(goToHomePage)
        }

        val editButton: ShapeableImageView = findViewById(R.id.edit_button)
        editButton.setOnClickListener {
            showCustomDialog(this, R.layout.edit_sleep)
        }


    }

    fun showCustomDialog(context: Context, popupLayout: Int) {
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

        val saveButton = dialogLayout.findViewById<Button>(R.id.save_button)
        saveButton.setOnClickListener {
            dialog.dismiss() // Close the dialog
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
    //Setting up the calendar adapter
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

    override fun onDateClick(position: Int) {
        //Add the date here
        val dateModel = calendarAdapter.getItem(position)
        taskadapter?.addList(tskList, dateModel)

    }
}