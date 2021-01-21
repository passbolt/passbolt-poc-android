package com.passbolt.poc.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.passbolt.poc.robots.mainActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest: BaseTest() {

  @Test
  @LargeTest
  fun `Action bar displayed`() {
    mainActivity {
      checkIfActionBarIsDisplayed()
    }
  }
}
