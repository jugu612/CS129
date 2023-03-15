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
import android.widget.PopupWindow

class Planner : AppCompatActivity() {

    private lateinit var popupWindow: PopupWindow
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planner)

        // Label
        onClick<ShapeableImageView>(R.id.label_button) {

            val alertDialog = MyAlertDialog()
            alertDialog.showCustomDialog(this)



        }

        // Create
        onClick<ShapeableImageView>(R.id.create_button) {
            val goToProgress = Intent(this, ProgressReport::class.java)
            startActivity(goToProgress)
        }

        // Sort
        onClick<ShapeableImageView>(R.id.sort_button) {
            val goToProgress = Intent(this, ProgressReport::class.java)
            startActivity(goToProgress)
        }
    }

    private inline fun <reified T : View> Activity.onClick(id: Int, crossinline action: (T) -> Unit) {
        findViewById<T>(id)?.setOnClickListener {
            action(it as T)
        }
    }

}

//class PopupWindowManager(private val context: Context) {
//    fun showPopupWindow() {
//        // Inflate the XML layout file
//
//
//
//        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val childView = inflater.inflate(R.layout.popupwindow, R.layout.activity_planner, false)
//
//        // Create the popup window
//        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//
//        // Set the content view of the popup window to the inflated XML layout
//        popupWindow.contentView = popupView
//
//        // Show the popup window
//        val view
//        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
//    }
//
//}


//class PopUpDialog2(private val context: Context) {
//    fun showMessageDialog(title: String, message: String) {
//
//        val builder = AlertDialog.Builder(context)
//
//        // popupWindow = PopupWindow(this)
//
//        // Inflate the layout of the popup window
//        //val layoutInflater = getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val inflater = LayoutInflater
//        val plannerLayout = findViewById<ConstraintLayout>(R.id.plannerLayout)
//
//        // val layoutInflater = LayoutInflater.from(inflater)
//        val popupView = layoutInflater.inflate(R.layout.popupwindow, plannerLayout, false)
//
//        // Set the inflated layout as the content view of the popup window
//        plannerLayout.addView(popupView)
//        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
//
//        builder.setTitle(title)
//        builder.setMessage(message)
//
//        builder.setPositiveButton("OK") { dialog, which ->
//            // do something when the "OK" button is clicked
//        }
//        builder.setNegativeButton("Cancel") { dialog, which ->
//            // do something when the "Cancel" button is clicked
//        }
//        builder.show()
//    }
//}

class MyAlertDialog {
    fun showCustomDialog(context: Context) {
        val builder = AlertDialog.Builder(context, R.style.MyDialogStyle)
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(R.layout.popupwindow, null)

        val customButton = dialogLayout.findViewById<Button>(R.id.cancel_button)
        builder.setView(dialogLayout)

        // Set cancelable to false
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        customButton.setOnClickListener {

            dialog.dismiss()
        }

        dialog.show()
    }
}
