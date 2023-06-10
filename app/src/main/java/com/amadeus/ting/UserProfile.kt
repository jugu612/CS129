package com.amadeus.ting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UserProfile : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        window.statusBarColor = ContextCompat.getColor(this, R.color.dark_green)

        auth = FirebaseAuth.getInstance()

        val user: FirebaseUser? = auth.currentUser
        val email = user?.email
        val displayName = user?.displayName
        val profileImageUrl = user?.photoUrl.toString()

        findViewById<TextView>(R.id.textView_full_name).text = displayName
        findViewById<TextView>(R.id.textView_email).text = email
        val imageViewProfile: ImageView = findViewById(R.id.imageViewProfile)

        Glide.with(this)
            .load(profileImageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageViewProfile)

        onClick<Button>(R.id.logout_button) {
            val goToRegLogin = Intent(this, RegLogin::class.java)
            startActivity(goToRegLogin)
            Toast.makeText(this, "Successfully logged out.", Toast.LENGTH_SHORT).show()
        }

    }

    private inline fun <reified T : View> Activity.onClick(id: Int, crossinline action: (T) -> Unit) {
        findViewById<T>(id)?.apply {
            setOnClickListener {
                action(this)
            }
        }
    }

}
