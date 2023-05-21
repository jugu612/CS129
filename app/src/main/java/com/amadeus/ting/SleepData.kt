package com.amadeus.ting

data class SleepData(
    val title: String,
    val time: Int,
    val timeLeft: String
)

val sleepDataList = listOf(
    SleepData("Sleeping Time", 10, "Ring in 6 hrs and 7 mins"),
    SleepData("Wake-up Time", 10, "Ring in 8 hrs and 7 mins")
)
