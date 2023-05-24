package com.amadeus.ting


import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.imageview.ShapeableImageView
import java.text.SimpleDateFormat
import java.util.*
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.amadeus.ting.databinding.ActivitySleepBinding
import java.time.LocalDate
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

    private lateinit var sharedPreferences: SharedPreferences
    private var selectedSleepTime: String? = null
    private var selectedWakeTime: String? = null
    private var sleepingInterval: String? = null
    private var sleepingLeft: Int? = 0
    private var wakingLeft: String? = null
    private var countdownTimer: CountDownTimer? = null
    private var sleepTimeChecked: Boolean = false
    private var wakeTimeChecked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sleep)

        binding = ActivitySleepBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpAdapter()
        setUpClickListener()
        setUpCalendar()

        sharedPreferences = getSharedPreferences("SleepData", Context.MODE_PRIVATE)

        selectedSleepTime = sharedPreferences.getString("SleepTime", null)
        selectedWakeTime = sharedPreferences.getString("WakeTime", null)
        sleepingInterval = calculateSleepingHours(selectedSleepTime, selectedWakeTime)

        Toast.makeText(this, "$selectedSleepTime $selectedWakeTime", Toast.LENGTH_SHORT).show()
        cardsValues()

        val backButton: ShapeableImageView = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            val goToHomePage = Intent(this, HomePage::class.java)
            startActivity(goToHomePage)
        }
        val editButton: ShapeableImageView = findViewById(R.id.edit_button)
        editButton.setOnClickListener {
            showCustomDialog(this, R.layout.edit_sleep)
        }

        val checkSleepTimeButton: Button = findViewById(R.id.check_sleep_time)
        checkSleepTimeButton.setOnClickListener {
            if (selectedSleepTime != null && !sleepTimeChecked) {
                sleepTimeChecked = true
                val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                findViewById<TextView>(R.id.sleep_time).text = currentTime
                findViewById<View>(R.id.upper_half).setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                findViewById<TextView>(R.id.sleep_time).text = currentTime
                with(sharedPreferences.edit()) {
                    putInt("SleepingLeft", 1)
                    apply()
                }
            }
        }

        val checkWakeTimeButton: Button = findViewById(R.id.check_wake_time)
        checkWakeTimeButton.setOnClickListener {
            if (selectedWakeTime != null && !wakeTimeChecked) {
                wakeTimeChecked = true
                val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                findViewById<TextView>(R.id.wakeup_time).text = currentTime
                findViewById<View>(R.id.upper_half).setBackgroundColor(ContextCompat.getColor(this, R.color.cyan))
                countdownTimer = countdownTime(selectedWakeTime!!, findViewById(R.id.wakeup_left)) // Start the countdown timer
                sleepingLeft = sharedPreferences.getInt("SleepLeft", 0)
            }
        }


    }

    private fun showCustomDialog(context: Context, popupLayout: Int) {
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

        val sleepButton = dialogLayout.findViewById<Button>(R.id.sleeping_time_button)
        val wakeButton = dialogLayout.findViewById<Button>(R.id.waking_time_button)
        val sleepingHoursTextView = dialogLayout.findViewById<TextView>(R.id.sw_hours_button)

        sleepButton.text = selectedSleepTime ?: "0:00"
        wakeButton.text = selectedWakeTime ?: "0:00"
        sleepingHoursTextView.text = sleepingInterval

        sleepButton.setOnClickListener {
            showTimePickerDialog(context, { selectedSleepTime2: String ->
                sleepButton.text = selectedSleepTime2
                selectedSleepTime = selectedSleepTime2 // Update the selectedSleepTime variable
                sleepingHoursTextView.text = calculateSleepingHours(selectedSleepTime, selectedWakeTime)
            }, true)
        }

        wakeButton.setOnClickListener {
            showTimePickerDialog(context, { selectedWakeTime2: String ->
                wakeButton.text = selectedWakeTime2
                selectedWakeTime = selectedWakeTime2// Update the selectedWakeTime variable
                sleepingHoursTextView.text = calculateSleepingHours(selectedSleepTime, selectedWakeTime)
            }, false)
        }

        val saveButton = dialogLayout.findViewById<Button>(R.id.save_button)
        saveButton.setOnClickListener {
            with(sharedPreferences.edit()) {
                putString("SleepTime", selectedSleepTime)
                putString("WakeTime", selectedWakeTime)
                putString("SleepingInterval", sleepingInterval)
                apply()
            }
            sleepButton.text = selectedSleepTime ?: "0:00"
            wakeButton.text = selectedWakeTime ?: "0:00"

            finish()
            val goToSleepSection = Intent(this, SleepSection::class.java)
            startActivity(goToSleepSection)
            overridePendingTransition(0, 0)
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun showTimePickerDialog(context: Context, callback: (String) -> Unit, isSleepingTime: Boolean) {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            context,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)

                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val selectedTime = timeFormat.format(calendar.time)

                if (isSleepingTime) {
                    selectedSleepTime = selectedTime
                } else {
                    selectedWakeTime = selectedTime
                }

                callback(selectedTime)
            },
            hour,
            minute,
            false
        )
        timePickerDialog.show()
    }

    private fun cardsValues() {
        val sleepingTime: TextView = findViewById(R.id.sleep_time)
        val sleepingTimeLeft: TextView = findViewById(R.id.sleep_left)
        sleepingTime.text = selectedSleepTime ?: "0:00"
        selectedSleepTime?.let { countdownTime(it, sleepingTimeLeft) }

        val wakingTime: TextView = findViewById(R.id.wakeup_time)
        val wakingTimeLeft: TextView = findViewById(R.id.wakeup_left)
        wakingTime.text = selectedWakeTime ?: "0:00"
        selectedWakeTime?.let { countdownTime(it, wakingTimeLeft) }

        val sleepingHoursInterval: TextView = findViewById(R.id.sleeptime_left)
        sleepingHoursInterval.text = sleepingInterval

    }


    private fun calculateSleepingHours(sleepTime: String?, wakeTime: String?): String {
        if (sleepTime == null || wakeTime == null) {
            return ""
        }
        val sleepFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val sleepCalendar = Calendar.getInstance()
        sleepCalendar.time = sleepFormat.parse(sleepTime)

        val wakeCalendar = Calendar.getInstance()
        wakeCalendar.time = sleepFormat.parse(wakeTime)

        if (wakeCalendar.before(sleepCalendar)) {
            wakeCalendar.add(Calendar.DATE, 1)
        }

        val diffInMillis = wakeCalendar.timeInMillis - sleepCalendar.timeInMillis

        val hours = (diffInMillis / (1000 * 60 * 60)).toInt()
        val minutes = ((diffInMillis % (1000 * 60 * 60)) / (1000 * 60)).toInt()

        val strhourText = when (hours) {
            0 -> ""
            1 -> "$hours hr"
            else -> "$hours hrs"
        }

        val strminText = when (minutes) {
            0 -> ""
            1 -> "$minutes min"
            else -> "$minutes mins"
        }

        sleepingInterval = if (diffInMillis == 0L) {
            "24 hrs"
        } else {
            "$strhourText $strminText"
        }
        return sleepingInterval as String
    }


    private fun setSleepAndWake(
        textView: TextView,
        sleepItemCard: View,
        sleepTimeText: TextView,
        countdownText: String,
    ) {
        val context: Context = applicationContext

        sleepItemCard.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow))
        textView.text = countdownText
        textView.setTextColor(ContextCompat.getColor(context, R.color.yellow))
        sleepTimeText.setTextColor(ContextCompat.getColor(context, R.color.yellow))

        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    textView.text = countdownText
                    textView.setTextColor(ContextCompat.getColor(context, R.color.red))
                    sleepItemCard.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
                    sleepTimeText.setTextColor(ContextCompat.getColor(context, R.color.red))
                }
            }
        }, 60000) // 60 seconds
    }

    private fun countdownTime(targetTime: String, textView: TextView): CountDownTimer {
        countdownTimer?.cancel()

        if (sleepingLeft == 1) {
            textView.text = ""
            finish()
        }
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val targetCalendar = Calendar.getInstance()
        targetCalendar.time = timeFormat.parse(targetTime)

        val currentCalendar = Calendar.getInstance()

        val currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentCalendar.get(Calendar.MINUTE)
        val currentSecond = currentCalendar.get(Calendar.SECOND)

        val targetHour = targetCalendar.get(Calendar.HOUR_OF_DAY)
        val targetMinute = targetCalendar.get(Calendar.MINUTE)
        val targetSecond = 0

        var diffInMillis = (targetHour - currentHour) * 60 * 60 * 1000
        diffInMillis += (targetMinute - currentMinute) * 60 * 1000
        diffInMillis += (targetSecond - currentSecond) * 1000

        if (diffInMillis < 0) {
            diffInMillis += 24 * 60 * 60 * 1000
        }

        return object : CountDownTimer(diffInMillis.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000).toInt()
                val minutes = (seconds / 60) % 60
                val hours = (seconds / (60 * 60)) % 24

                val strhourText = when (hours) {
                    0 -> ""
                    1 -> "$hours hr "
                    else -> "$hours hrs "
                }

                val strminText = when (minutes) {
                    0 -> ""
                    1 -> "$minutes min "
                    else -> "$minutes mins "
                }

                val strsecText = when (seconds % 60) {
                    0 -> ""
                    1 -> "${seconds % 60} sec"
                    else -> "${seconds % 60} secs"
                }

                if (textView.id == R.id.sleep_left && strhourText.isEmpty() && strminText.isEmpty() && strsecText.isEmpty()) {
                    setSleepAndWake(
                        findViewById(R.id.sleep_left),
                        findViewById(R.id.upper_half),
                        findViewById(R.id.sleep_time),
                        "Time to Sleep!"
                    )
                } else if (textView.id == R.id.wakeup_left && strhourText.isEmpty() && strminText.isEmpty() && strsecText.isEmpty()) {
                    setSleepAndWake(
                        findViewById(R.id.wakeup_left),
                        findViewById(R.id.wupper_half),
                        findViewById(R.id.wakeup_time),
                        "Wake Up!"
                    )
                } else {
                    textView.text = "Ring in $strhourText$strminText$strsecText"
                }
            }

            override fun onFinish() {
                // Handle countdown finished event
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countdownTimer?.cancel()
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