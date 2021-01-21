package com.passbolt.poc.robots

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matcher

open class BaseRobot {

  fun checkIfElementIsDisplayed(resId: Int): ViewInteraction =
    checkIfElementIsDisplayed(withId(resId))

  fun clickOnButton(resId: Int): ViewInteraction =
    clickOnButton(withId(resId))

  fun clickOnButtonWithStringResource(resId: Int): ViewInteraction =
    clickOnButton(withText(resId))

  private fun checkIfElementIsDisplayed(matcher: Matcher<View>): ViewInteraction =
    onView(matcher).check(matches(isDisplayed()))

  private fun clickOnButton(matcher: Matcher<View>): ViewInteraction =
    onView(matcher).perform(click())
}
