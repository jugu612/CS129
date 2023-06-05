package com.amadeus.ting

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amadeus.ting.FoodIntake.Companion.newRecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class WaterAlertDialog {

    private lateinit var dialogLayout: View
    private lateinit var nestedDialogLayout: View


    companion object {
        lateinit var userMilliliterInput : String
    }

    fun intakeGoalDialog(context: Context, layout: Int, callback: (Int?) -> Unit) {
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(layout, null)

        val builder = AlertDialog.Builder(context, R.style.MyDialogStyle)
        builder.setView(dialogLayout)
        builder.setCancelable(false)

        // Create and show the AlertDialog
        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        val cancelButton = dialogLayout.findViewById<Button>(R.id.cancel_button)
        val saveButton = dialogLayout.findViewById<Button>(R.id.save_button)

        val milliliterInputLayout = dialogLayout.findViewById<TextInputLayout>(R.id.milliliter_input_layout)
        val milliliterInput: TextInputEditText = milliliterInputLayout.findViewById(R.id.milliliter_input)

        // Cancel and Save Button Behavior
        cancelButton?.setOnClickListener {
            alertDialog.dismiss()
            callback(null) // Invoke the callback with a null value when cancel button is clicked
        }
        saveButton?.setOnClickListener {
            val userInput: String = milliliterInput.text.toString()
            val userMilliliterInput: Int? = userInput.toIntOrNull()
            alertDialog.dismiss()
            callback(userMilliliterInput) // Invoke the callback with the user's input value when save button is clicked
        }
    }

//    fun recordsDialog(context: Context, popupLayout: Int, nestedPopupLayout: Int = -1) {
//
//        val alertDialogBuilder = AlertDialog.Builder(context)
//        val dialogView = LayoutInflater.from(context).inflate(popupLayout, null)
//
//        // Initialize Recycler View
//        recyclerViewWater = dialogView.findViewById(R.id.water_intake_records_rv)
//        recyclerViewWater.layoutManager = LinearLayoutManager(context)
//        recyclerViewWater.setHasFixedSize(true)
//        //builder.setCancelable(false)
//
//        alertDialogBuilder.setView(dialogView)
//        val dialog = alertDialogBuilder.create()
//        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog.show()
//    }



}