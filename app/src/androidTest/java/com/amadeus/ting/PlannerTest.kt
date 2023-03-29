package com.amadeus.ting

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PlannerTest {

    @Rule
    @JvmField
    public var activityRule = ActivityScenarioRule(Planner::class.java)
    // var calendarView : RecyclerView.ViewHolder

    /* Calendar Test Cases */

    //Tapping dates
    @Test
    fun recycleViewTest(){
        Espresso.onView((withId(R.id.calendar_recycler))).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(2,click()))
        Espresso.onView((withId(R.id.calendar_recycler))).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(3,click()))
        Espresso.onView((withId(R.id.calendar_recycler))).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(4,click()))
    }

    //Calendar scrolling
    @Test
    fun calendarScrollTest(){

    }
}