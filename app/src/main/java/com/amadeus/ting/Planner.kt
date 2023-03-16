package com.amadeus.ting

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
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
import android.widget.PopupWindow
import android.widget.TextView

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

        // Label -> Myka
        onClick<ShapeableImageView>(R.id.label_button) {
            val labelAlertDialog = MyAlertDialog()
            labelAlertDialog.showCustomDialog(this, R.layout.view_labels, R.layout.add_label, R.id.label_sample)

        }

        // Create -> Jugu
        onClick<ShapeableImageView>(R.id.create_button) {
            val goToProgress = Intent(this, ProgressReport::class.java)
            startActivity(goToProgress)
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
        binding.recyclerView.addItemDecoration(HorizontalItemDecoration(spacingInPixels))
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerView)
        adapter = CalendarAdapter { calendarDateModel: CalendarDateModel, position: Int ->
            calendarList2.forEachIndexed { index, calendarModel ->
                calendarModel.isSelected = index == position
            }
            adapter.setData(calendarList2)
        }
        binding.recyclerView.adapter = adapter
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
    fun showCustomDialog(context: Context, popupLayout: Int, nestedPopupLayout: Int = -1, buttonToPress: Int) {
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
