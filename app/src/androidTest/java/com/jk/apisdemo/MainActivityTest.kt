package com.jk.apisdemo

import android.os.SystemClock
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.jk.apisdemo.main.MainActivity
import org.junit.Rule
import org.junit.Test


class MainActivityTest {
    companion object {
        private const val INTERVAL = 2000L
    }

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testOpenMenu() {
        onView(withText(R.string.title)).check(matches(isDisplayed()))
        onView(withId(R.id.action_history)).perform(click())
        SystemClock.sleep(INTERVAL);
        onView(withText(R.string.history)).check(matches(isDisplayed()))
    }

}