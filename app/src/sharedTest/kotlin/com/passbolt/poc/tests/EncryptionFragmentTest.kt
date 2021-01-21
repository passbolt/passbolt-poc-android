package com.passbolt.poc.tests

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.passbolt.poc.R
import com.passbolt.poc.milestones.encryption.encryptdecrypt.EncryptDecryptFragment
import com.passbolt.poc.robots.encryptDecrypt
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EncryptionFragmentTest {

  @Before
  fun setup() {
    launchFragmentInContainer<EncryptDecryptFragment>(themeResId = R.style.Theme_MaterialComponents)
  }

  @Test
  @LargeTest
  fun `Key edit text displayed`() {
    encryptDecrypt {
      checkIfKeyEditTextIsDisplayed()
    }
  }
}
