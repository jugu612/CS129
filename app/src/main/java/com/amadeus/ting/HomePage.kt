package com.amadeus.ting

import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth

class HomePage : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth //Firebase authentication instance
    private lateinit var googleSignInClient: GoogleSignInClient //Google Sign In client instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        auth = FirebaseAuth.getInstance() //Initializing the Firebase authentication instance

        val email = intent.getStringExtra("email") //Extracting the email from the intent
        val displayName = intent.getStringExtra("name") //Extracting the name from the intent

        //Setting up the onClickListeners for the different sections of the home page
        onClick<ConstraintLayout>(R.id.linearLayout_HealthSection) {
            val goToHealthWellness = Intent(this, HealthAndWellness::class.java)
            startActivity(goToHealthWellness)
        }

        onClick<ConstraintLayout>(R.id.linearLayout_PlannerSection) {
            val goToPlanner = Intent(this, Planner::class.java)
            startActivity(goToPlanner)
        }

        onClick<ConstraintLayout>(R.id.linearLayout_FocusSection) {
            val goToFocus = Intent(this, FocusMode::class.java)
            startActivity(goToFocus)
        }

        onClick<ShapeableImageView>(R.id.user_image) {
            val goToProgress = Intent(this, ProgressReport::class.java)
            startActivity(goToProgress)
        }

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