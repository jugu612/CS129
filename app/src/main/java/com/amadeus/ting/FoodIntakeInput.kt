package com.amadeus.ting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.imageview.ShapeableImageView

class FoodIntakeInput : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_food_intake)

        onClick<ShapeableImageView>(R.id.back_button){
            val goToFoodIntake = Intent(this, FoodIntake::class.java)
            startActivity(goToFoodIntake)
    }
}

    private inline fun <reified T : View> Activity.onClick(id: Int, crossinline action: (T) -> Unit) {
        findViewById<T>(id)?.setOnClickListener {
            action(it as T)
        }
    }
}