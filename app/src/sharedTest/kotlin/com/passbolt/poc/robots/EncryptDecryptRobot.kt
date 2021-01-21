package com.passbolt.poc.robots

import com.passbolt.poc.R

fun encryptDecrypt(func: EncryptDecryptRobot.() -> Unit) = EncryptDecryptRobot().apply { func() }

class EncryptDecryptRobot: BaseRobot() {

  // ID resources
  private val messageEditText = R.id.message_edit_text
  private val keyEditText = R.id.key_edit_text

  fun checkIfMessageEditTextIsDisplayed() = checkIfElementIsDisplayed(messageEditText)

  fun checkIfKeyEditTextIsDisplayed() = checkIfElementIsDisplayed(keyEditText)

}
