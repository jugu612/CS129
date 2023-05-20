package com.amadeus.ting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SleepSection : AppCompatActivity() {
    private lateinit var sleepadapter: SleepAdapter
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sleep)

        recyclerView = findViewById(R.id.sleepwake_time)
        sleepadapter = SleepAdapter(sleepDataList)
        recyclerView.adapter = sleepadapter
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

    }
}