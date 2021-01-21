package com.passbolt.poc.robots

import com.passbolt.poc.R

fun mainActivity(func: MainActivityRobot.() -> Unit) = MainActivityRobot().apply { func() }

class MainActivityRobot: BaseRobot() {

  // ID resources
  private val actionBar = R.id.action_bar

  // String resources
  private val encryptionLibraryButton = R.string.milestone_encryption

  fun checkIfActionBarIsDisplayed() = checkIfElementIsDisplayed(actionBar)

  fun clickOnEncryptionLibraryButton()  = clickOnButtonWithStringResource(encryptionLibraryButton)
}
