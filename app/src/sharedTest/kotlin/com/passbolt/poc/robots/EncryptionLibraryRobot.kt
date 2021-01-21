package com.passbolt.poc.robots

import com.passbolt.poc.R

fun encryptionLibrary(func: EncryptionLibraryRobot.() -> Unit) = EncryptionLibraryRobot().apply { func() }

class EncryptionLibraryRobot: BaseRobot() {

  // String resources
  private val encryptDecryptButton = R.string.encryption_encrypt_decrypt

  fun clickOnEncryptDecryptButton()  = clickOnButtonWithStringResource(encryptDecryptButton)
}
