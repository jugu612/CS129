package com.amadeus.ting

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.google.android.material.imageview.ShapeableImageView
import kotlin.properties.Delegates

class WaterIntake : AppCompatActivity() {

    private lateinit var seekBar: SeekBar
    private lateinit var valueTextView: TextView
    private lateinit var alertDialog : WaterAlertDialog

    private val PREFS_NAME = "WaterIntakePrefsFile" // Shared Preferences File

    private var milliliterGoal : Int = 0
    private var milliliterLeft : Int = 0
    private var totalMilliliter : Int = 0


    private var seekBarInput : Int = 0
    private var currentPercentage : Float = 0.0f

    @SuppressLint("MissingInflatedId", "UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_intake)
        alertDialog = WaterAlertDialog()


        // Opens Shared Preferences
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Retrieves number of milliliters to drink value
        milliliterGoal = prefs.getInt("milliliterGoal", 0)
        val drinkTextView: TextView = findViewById(R.id.drink)
        drinkTextView.text = "Drink $milliliterGoal ml"

        // Retrieves Percentage progress
        currentPercentage = prefs.getFloat("currentPercentage", 0F)
        totalMilliliter = prefs.getInt("totalMilliliter", 0)

        val numberPercent: TextView = findViewById(R.id.number_percent)
        numberPercent.text = "${currentPercentage.toInt()}%"

        val progressBar: ProgressBar = findViewById(R.id.progress_bar)
        progressBar.progress = currentPercentage.toInt()


        // Retrieves number of ml left
        milliliterLeft = prefs.getInt("milliliterLeft", 0)
        val numberMLLeft: TextView = findViewById(R.id.number_ml)
        numberMLLeft.text = "$milliliterLeft ml left"


        seekBar = findViewById(R.id.seekBar)

        // Clicking the Today's Goal TextView
        val goalText: TextView = findViewById(R.id.goal_text)
        goalText.setOnClickListener {
            alertDialog.intakeGoalDialog(this, R.layout.water_popupwindow) { userMilliliterInput ->
                if (userMilliliterInput != null) {
                    milliliterGoal = userMilliliterInput
                    milliliterLeft = userMilliliterInput
                    seekBar.max = milliliterGoal

                    drinkTextView.text = "Drink $milliliterGoal ml" // Updates the number of milliliter value
                    numberMLLeft.text = "$milliliterGoal ml left"

                    val editor = prefs.edit()
                    editor.putInt("milliliterGoal", milliliterGoal)
                    editor.putInt("milliliterLeft", milliliterGoal)
                    editor.apply()

                }
            }
        }

        valueTextView = findViewById(R.id.textView_progress)

        // Seekbar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                valueTextView.text = "$progress ml"
                seekBarInput = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })


        // Drink Water Button Clicked
        milliliterLeft = milliliterGoal
        val drinkButton: View = findViewById(R.id.drink_button)
        drinkButton.setOnClickListener {

            // Updates the percentage value
            totalMilliliter += seekBarInput
            currentPercentage = (((totalMilliliter.toFloat() / milliliterGoal.toFloat()) * 100))
            numberPercent.text = "${currentPercentage.toInt()}%"

            // Updates the number of ml left
            milliliterLeft -= seekBarInput
            numberMLLeft.text = "$milliliterLeft ml left"

            // Updates the Progress Bar
            progressBar.progress = currentPercentage.toInt()
            seekBar.max = milliliterLeft

            Toast.makeText(this, "Drank water successfully!", Toast.LENGTH_SHORT).show()

            // Stores it inside shared preferences
            val editor = prefs.edit()
            editor.putFloat("currentPercentage", currentPercentage)
            editor.putInt("milliliterLeft", milliliterLeft)
            editor.putInt("totalMilliliter", totalMilliliter)
            editor.apply()

        }

        // Records Button Clicked
        val recordsButton: View = findViewById(R.id.records_button)
        recordsButton.setOnClickListener {
            alertDialog.recordsDialog(this, R.layout.water_intake_records, R.layout.water_intake_history)

            // Resets the progress (DEBUGGING PURPOSES)
            val editor = prefs.edit()
            editor.clear()
            editor.apply()
            progressBar.progress = 0
            numberPercent.text = "0%"
            numberMLLeft.text = "0 ml left"
            drinkTextView.text = "Drink 0 ml"
            totalMilliliter = 0
            milliliterGoal = 0
            currentPercentage = 0.0F
        }

    }

    private inline fun <reified T : View> Activity.onClick(id: Int, crossinline action: (T) -> Unit) {
        findViewById<T>(id)?.apply {
            setOnClickListener {
                action(this)
            }
        }
    }

}







