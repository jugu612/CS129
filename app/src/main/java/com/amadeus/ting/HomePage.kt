package com.amadeus.ting

import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class HomePage : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth //Firebase authentication instance
    private lateinit var homeadapter: HomepageAdapter
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        val currentTime = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila")).time

        auth = FirebaseAuth.getInstance() //Initializing the Firebase authentication instance

        val email = intent.getStringExtra("email") //Extracting the email from the intent
        val displayName = intent.getStringExtra("name") //Extracting the name from the intent

        recyclerView = findViewById(R.id.homepage_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        homeadapter = HomepageAdapter(cardDataList)
        recyclerView.adapter = homeadapter


        onClick<ShapeableImageView>(R.id.user_image) {
            val goToProgress = Intent(this, ProgressReport::class.java)
            startActivity(goToProgress)
        }

        //onClick<ShapeableImageView>(R.id.logout_button) {
        ///    val goToRegLogin = Intent(this, RegLogin::class.java)
        //    startActivity(goToRegLogin)
        //    Toast.makeText(this, "Successfully logged out.", Toast.LENGTH_SHORT).show()
        //}

        changeMessage(currentTime)

    }

    fun changeMessage(currentTime: Date) {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila"))
        calendar.time = currentTime
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val t: TextView = findViewById(R.id.textView_Username)
        val d: TextView = findViewById(R.id.textView_date)
        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentTime).toUpperCase(Locale.getDefault())
        d.text = formattedDate

        when {
            hour in 4..11 -> t.text = "Good Morning! ⛅"
            hour in 12..17 -> t.text = "Good Afternoon! ☀"
            hour in 18..23 -> t.text = "Good Evening! \uD83C\uDF19"
            else -> t.text = "Hello! \uD83D\uDE0A"
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // disables the back button
    }

    //Inline function that sets an onClickListener for a given view ID
    private inline fun <reified T : View> Activity.onClick(id: Int, crossinline action: (T) -> Unit) {
        findViewById<T>(id)?.apply {
            setOnClickListener {
                action(this)
            }
        }
    }
}