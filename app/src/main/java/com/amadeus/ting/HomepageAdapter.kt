package com.amadeus.ting

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random


class HomepageAdapter(private val cardDataList: List<HomepageData>) :
    RecyclerView.Adapter<HomepageAdapter.CardViewHolder>() {

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.homepage_but)
        private val cardImageView: ImageView = itemView.findViewById(R.id.card_image)
        private val cardView: CardView = itemView.findViewById(R.id.homepage_layout)

        fun bind(cardData: HomepageData) {
            titleTextView.text = cardData.title
            cardImageView.setImageResource(cardData.imageResId)
            cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context, cardData.colorCode))

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = when (adapterPosition) {
                    0 -> Intent(context, Planner::class.java)
                    1 -> Intent(context, FoodIntake::class.java)
                    2 -> Intent(context, WaterIntake::class.java)
                    3 -> Intent(context, SleepSection::class.java)
                    4 -> Intent(context, HealthAndWellness::class.java)
                    5 -> Intent(context, FocusMode::class.java)
                    else -> null
                }
                intent?.let { context.startActivity(it) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.homepage_buttons, parent, false)
        return CardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val currentCardData = cardDataList[position]
        holder.bind(currentCardData)
    }

    override fun getItemCount() = cardDataList.size
}




