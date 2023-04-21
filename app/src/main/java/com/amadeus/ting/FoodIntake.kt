package com.amadeus.ting;

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.imageview.ShapeableImageView

class FoodIntake : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_intake)

        onClick<ShapeableImageView>(R.id.back_button){
            val goToHomePage = Intent(this, HomePage::class.java)
            startActivity(goToHomePage)
        }

        onClick<ShapeableImageView>(R.id.edit_button){
            val goToFoodIntakeInput = Intent(this, FoodIntakeInput::class.java)
            startActivity(goToFoodIntakeInput)
        }

        onClick<ShapeableImageView>(R.id.create_button){
            val goToFoodIntakeInput = Intent(this, FoodIntakeInput::class.java)
            startActivity(goToFoodIntakeInput)
        }

        onClick<ShapeableImageView>(R.id.delete_button){

        }

    }
    private inline fun <reified T : View> Activity.onClick(id: Int, crossinline action: (T) -> Unit) {
        findViewById<T>(id)?.setOnClickListener {
            action(it as T)
        }
    }
}