package com.passbolt.poc

import androidx.test.filters.LargeTest
import com.passbolt.poc.robots.encryptDecrypt
import com.passbolt.poc.robots.encryptionLibrary
import com.passbolt.poc.robots.mainActivity
import com.passbolt.poc.tests.BaseTest
import org.junit.Test

@Suppress("IllegalIdentifier")
class EncryptionTest: BaseTest() {

  @Test
  @LargeTest
  fun `Message edit text displayed`() {
    mainActivity {
      clickOnEncryptionLibraryButton()
    }

    encryptionLibrary {
      clickOnEncryptDecryptButton()
    }

    encryptDecrypt {
      checkIfMessageEditTextIsDisplayed()
    }
  }

}