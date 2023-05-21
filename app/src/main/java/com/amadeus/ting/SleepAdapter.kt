package com.amadeus.ting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SleepAdapter(private val sleepDataList: List<SleepData>) :
    RecyclerView.Adapter<SleepAdapter.SleepDataViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SleepDataViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_sleep, parent, false)
        return SleepDataViewHolder(view)
    }

    override fun onBindViewHolder(holder: SleepDataViewHolder, position: Int) {
        val sleepData = sleepDataList[position]
        holder.bind(sleepData)
    }

    override fun getItemCount(): Int {
        return sleepDataList.size
    }

    inner class SleepDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.sleep_title)
        private val timeTextView: TextView = itemView.findViewById(R.id.sleep_time)
        private val timeLeftTextView: TextView = itemView.findViewById(R.id.sleep_left)

        fun bind(sleepData: SleepData) {
            titleTextView.text = sleepData.title
            timeTextView.text = sleepData.time.toString()
            timeLeftTextView.text = sleepData.timeLeft
        }
    }
}
