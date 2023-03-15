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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planner)

        // Label -> Myka
        onClick<ShapeableImageView>(R.id.label_button) {


        }

        // Create -> Jugu
        onClick<ShapeableImageView>(R.id.create_button) {
            val goToProgress = Intent(this, ProgressReport::class.java)
            startActivity(goToProgress)
        }

        // Sort -> Dust
        onClick<ShapeableImageView>(R.id.sort_button) {
            val labelAlert = MyAlertDialog()
            labelAlert.showCustomDialog(this, R.layout.sort_popupwindow)

            onClick<Button>(R.id.create_button) {
                // code here
            }

        }
    }

    private inline fun <reified T : View> Activity.onClick(id: Int, crossinline action: (T) -> Unit) {
        findViewById<T>(id)?.setOnClickListener {
            action(it as T)
        }
    }

}

class MyAlertDialog {
    fun showCustomDialog(context: Context, popupLayout: Int) {
        val builder = AlertDialog.Builder(context, R.style.MyDialogStyle)
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(popupLayout, null)

        // Cancel Button
        val cancelButton = dialogLayout.findViewById<Button>(R.id.cancel_button)
        builder.setView(dialogLayout)

        builder.setCancelable(false) // Set cancelable to false
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
