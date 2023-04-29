package com.amadeus.ting

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.hamcrest.CoreMatchers.*
import org.junit.Rule
import org.junit.Test


class FoodIntakeTest {

    @Rule
    @JvmField
    val activityRule = ActivityScenarioRule(FoodIntakeInput::class.java)

    @Test
    fun foodIntakeInput() {
        onView(withId(R.id.meals_per_day_button)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("3 meals"))).perform(click())
        onView(withId(R.id.meals_per_day_button)).check(matches(withSpinnerText(containsString("3 meals"))))

        onView(withId(R.id.eating_intervals_button)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("4 hrs and 1 min"))).perform(click())
        onView(withId(R.id.eating_intervals_button)).check(matches(withSpinnerText(containsString("1 min"))))

        onView(withId(R.id.first_reminder_button)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("3 hrs and 12 mins before"))).perform(click())
        onView(withId(R.id.first_reminder_button)).check(matches(withSpinnerText(containsString("12 mins before"))))
    }


}