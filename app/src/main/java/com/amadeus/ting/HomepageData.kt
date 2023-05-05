package com.amadeus.ting

data class HomepageData(
    val title: String,
    val imageResId: Int,
    val colorCode: Int
)
val cardDataList = listOf(
    HomepageData("Planner", R.drawable.sectionlogo_planer, R.color.red),
    HomepageData("Mealtime", R.drawable.mealtime_logo, R.color.cyan),
    HomepageData("Water", R.drawable.water_logo, R.color.cyan),
    HomepageData("Sleeping", R.drawable.sleep_logo, R.color.cyan),
    HomepageData("Medicine", R.drawable.medicine_logo, R.color.cyan),
    HomepageData("Focus", R.drawable.sectionlogo_focus, R.color.yellow)
)
