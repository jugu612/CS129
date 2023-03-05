package com.amadeus.ting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class RegLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg_login)

        val googleloginButton = findViewById<Button>(R.id.google_login_button)
        val signupButton = findViewById<TextView>(R.id.signup_button)
        val loginButton = findViewById<Button>(R.id.login_button)
        signupButton.setOnClickListener{
            val Intent = Intent(this,SignUp::class.java)
            startActivity(Intent)
        }
        loginButton.setOnClickListener{
            val Intent = Intent(this,Login::class.java)
            startActivity(Intent)
        }

    }
}