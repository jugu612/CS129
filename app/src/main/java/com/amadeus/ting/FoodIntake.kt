package com.amadeus.ting;

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class FoodIntake : AppCompatActivity() {

    private lateinit var newRecyclerView : RecyclerView
    private lateinit var foodScheduleList : ArrayList<FoodIntakeInfo>

    // temporary Variables (display starting from 8:30 am with 2 hour interval)
    private lateinit var foodIntakeNumbers : Array<Int>
    private lateinit var timeIntervalHours : Array<Int>
    private lateinit var timeIntervalMinutes : Array<Int>
    private lateinit var timeIntervalMeridiem : Array<String>
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

        // temporary lang muna
        foodIntakeNumbers = arrayOf(1, 2, 3, 4, 5, 6)
        timeIntervalHours = arrayOf(8, 10, 12, 2, 4, 6)
        timeIntervalMinutes = arrayOf(30, 30, 30, 30, 30, 30)
        timeIntervalMeridiem = arrayOf("AM", "AM", "PM", "PM", "PM", "PM")

        newRecyclerView = findViewById(R.id.eating_time_rv)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)


        foodScheduleList = arrayListOf<FoodIntakeInfo>()
        getListData()
    }
    private inline fun <reified T : View> Activity.onClick(id: Int, crossinline action: (T) -> Unit) {
        findViewById<T>(id)?.setOnClickListener {
            action(it as T)
        }
    }

    private fun getListData() {

        for (i in foodIntakeNumbers.indices) {

            val intakeInfo = FoodIntakeInfo(foodIntakeNumbers[i], timeIntervalHours[i], timeIntervalMinutes[i], timeIntervalMeridiem[i])
            foodScheduleList.add(intakeInfo)
        }

        newRecyclerView.adapter = FoodIntakeAdapter(foodScheduleList)
    }

}