package com.amadeus.ting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class RegLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg_login)

        val googleLoginButton = findViewById<Button>(R.id.google_login_button)

        googleLoginButton.setOnClickListener{
            val Intent = Intent(this,Login::class.java)
            startActivity(Intent)
        }

    }
}