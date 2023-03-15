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
import android.widget.TextView

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
            labelAlert.showCustomDialog(this, R.layout.sort_popupwindow, R.layout.popupwindow)
        }

    }

    private inline fun <reified T : View> Activity.onClick(id: Int, crossinline action: (T) -> Unit) {
        findViewById<T>(id)?.setOnClickListener {
            action(it as T)
        }
    }

}

class MyAlertDialog {

    fun showCustomDialog(context: Context, popupLayout: Int, nestedPopupLayout: Int = -1) {
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
            val showNestedDialogButton = dialogLayout.findViewById<Button>(R.id.text_alphabetical)

            showNestedDialogButton.setOnClickListener {

                val nestedDialogLayout = inflater.inflate(nestedPopupLayout, null)
                val nestedBuilder = AlertDialog.Builder(context, R.style.MyDialogStyle)
                nestedBuilder.setView(nestedDialogLayout)
                builder.setCancelable(false)

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

