package com.amadeus.ting

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test
import java.util.regex.Matcher


class PlannerTest {

    object MyViewAction {
        fun clickChildViewWithId(id: Int): ViewAction {
            return object : ViewAction {
                override fun getConstraints(): org.hamcrest.Matcher<View>? {
                    return null
                }

                override fun getDescription(): String {
                    return "Click on a child view with specified id."
                }
                override fun perform(uiController: UiController?, view: View) {
                    val v: View = view.findViewById(id)
                    v.performClick()
                }
            }
        }
    }

    @Rule
    @JvmField
    public var activityRule = ActivityScenarioRule(Planner::class.java)
    // var calendarView : RecyclerView.ViewHolder

    /* Calendar Test Cases */

    //Tapping dates
    @Test
    fun calendarTest(){
        Espresso.onView((withId(R.id.calendar_recycler))).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(2,click()))
        Espresso.onView((withId(R.id.calendar_recycler))).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(3,click()))
        Espresso.onView((withId(R.id.calendar_recycler))).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(4,click()))
    }
    //Calendar scrolling

    @Test
    fun createTaskTest(){
        Espresso.onView((withId(R.id.create_button))).perform(click())
        Espresso.onView(withId(R.id.edit_title)).perform(typeText("Test Task"))
        Espresso.onView(withId(R.id.edit_details)).perform(typeText("Hello World!"), pressBack())
        Espresso.onView((withId(R.id.save_button))).perform(click())
    }

    @Test
    fun sortTaskTest(){
        Espresso.onView(withId(R.id.sort_button)).perform(click())
        Espresso.onView(withId(R.id.text_alphabetical)).perform(click())
        Espresso.onView(withId(R.id.arrow)).perform(click())
        Espresso.onView(withId(R.id.text_deadline)).perform(click())
        Espresso.onView(withId(R.id.arrow2)).perform(click())
        Espresso.onView(withId(R.id.text_label)).perform(click())
        Espresso.onView(withId(R.id.clearButton)).perform(click())
        Espresso.onView(withId(R.id.text_alphabetical)).perform(click())
        Espresso.onView(withId(R.id.text_deadline)).perform(click())
        Espresso.onView(withId(R.id.text_label)).perform(click())

        Espresso.onView(withId(R.id.save_button)).perform(click())
    }

    @Test
    fun deleteTaskTest(){
        Espresso.onView(ViewMatchers.withId(R.id.Tasklist)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, MyViewAction.clickChildViewWithId(R.id.btnDeleteTask)))
    }
}