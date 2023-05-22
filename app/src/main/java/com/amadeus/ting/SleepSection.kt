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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class SleepSection : AppCompatActivity() {
    private lateinit var sleepadapter: SleepAdapter
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sleep)


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
}