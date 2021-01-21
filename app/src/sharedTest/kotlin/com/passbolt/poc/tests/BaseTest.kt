package com.passbolt.poc.tests

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.passbolt.poc.MainActivity
import org.junit.Rule

open class BaseTest {

  @get:Rule
  open val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)
}
