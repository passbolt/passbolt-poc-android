package com.passbolt.poc.milestones.autofill.auth

import android.R.layout
import android.app.Activity
import android.app.assist.AssistStructure
import android.content.Intent
import android.os.Bundle
import android.service.autofill.Dataset
import android.view.View
import android.view.autofill.AutofillManager.EXTRA_ASSIST_STRUCTURE
import android.view.autofill.AutofillManager.EXTRA_AUTHENTICATION_RESULT
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt.AuthenticationResult
import androidx.biometric.auth.AuthPromptCallback
import androidx.biometric.auth.startClass3BiometricOrCredentialAuthentication
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.passbolt.poc.R.string
import com.passbolt.poc.milestones.autofill.parse.AssistStructureParser
import com.passbolt.poc.milestones.autofill.parse.ParsedAssistStructure
import com.passbolt.poc.util.EncryptedPreferences
import com.passbolt.poc.util.TrustedAppsInfo
import helper.Helper

class AutofillAuthActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    startClass3BiometricOrCredentialAuthentication(
        null,
        getString(string.biometric_prompt_title),
        getString(string.biometric_prompt_subtitle),
        callback = object : AuthPromptCallback() {
          override fun onAuthenticationError(
            activity: FragmentActivity?,
            errorCode: Int,
            errString: CharSequence
          ) {
            super.onAuthenticationError(activity, errorCode, errString)
            Toast.makeText(
                applicationContext,
                "Authentication error: $errString", Toast.LENGTH_SHORT
            )
                .show()
            finish()
          }

          override fun onAuthenticationSucceeded(
            activity: FragmentActivity?,
            result: AuthenticationResult
          ) {
            super.onAuthenticationSucceeded(activity, result)
            val keyAndPasswordPair = getKeyAndPasswordFromSecureStorage()

            if (keyAndPasswordPair == null) {
              showError(getString(string.securestorage_error_reading_from_storage))
            } else {
              val (privateKey, password) = keyAndPasswordPair

              if (privateKey.isEmpty() || password.isEmpty()) {
                showError(
                    getString(string.autofill_key_not_present_in_secure_storage)
                )
              } else {
                fillRequest(privateKey, password)
                finish()
              }
            }
          }

          override fun onAuthenticationFailed(activity: FragmentActivity?) {
            super.onAuthenticationFailed(activity)
            Toast.makeText(
                applicationContext, "Authentication failed",
                Toast.LENGTH_SHORT
            )
                .show()
          }
        }
    )
  }

  private fun getKeyAndPasswordFromSecureStorage(): Pair<String, String>? {
    return try {
      val encryptedPreferences = EncryptedPreferences(applicationContext)
      Pair(
          encryptedPreferences.getPrivateKey(),
          encryptedPreferences.getPassword()
      )
    } catch (e: Exception) {
      null
    }
  }

  private fun fillRequest(
    privateKey: String,
    password: String
  ) {
    val structure = intent.getParcelableExtra<AssistStructure>(EXTRA_ASSIST_STRUCTURE)
    val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)
    if (structure == null) {
      setResult(RESULT_CANCELED)
      return
    }

    val assistStructureParsingResult = AssistStructureParser(structure).parse()
    val parsedAssistStructures = assistStructureParsingResult.parsedAssistStructures
    val webDomain = assistStructureParsingResult.webDomain

    // For the purpose of this POC we support filling only username and password
    val usernameParsedAssistStructure = parsedAssistStructures.firstOrNull { parsedStructure ->
      parsedStructure.hints.any { hint ->
        hint.contains(
            View.AUTOFILL_HINT_USERNAME, ignoreCase = true
        )
      }
    }

    val passwordParsedAssistStructure = parsedAssistStructures.firstOrNull { parsedStructure ->
      parsedStructure.hints.any { hint ->
        hint.contains(
            View.AUTOFILL_HINT_PASSWORD, ignoreCase = true
        )
      }
    }

    if (usernameParsedAssistStructure == null || passwordParsedAssistStructure == null) {
      setResult(RESULT_CANCELED)
      return
    }

    // Special handling for Instagram - POC imitates App Link verification
    val dataset = if (packageName == TrustedAppsInfo.INSTAGRAM_PACKAGE ||
        (
            webDomain.isNotEmpty() && TrustedAppsInfo.INSTAGRAM_WEB_ADDRESS.contains(
                webDomain, ignoreCase = true
            )
            )
    ) {
      getInstagramDataset(usernameParsedAssistStructure, passwordParsedAssistStructure)
    } else {
      getPOCDataset(
          privateKey,
          password,
          usernameParsedAssistStructure,
          passwordParsedAssistStructure
      )
    }

    val replyIntent = Intent().apply {
      putExtra(EXTRA_AUTHENTICATION_RESULT, dataset)
    }

    setResult(Activity.RESULT_OK, replyIntent)
  }

  private fun preparePresentation(text: String): RemoteViews {
    return RemoteViews(packageName, layout.simple_list_item_1).apply {
      setTextViewText(android.R.id.text1, text)
    }
  }

  private fun getInstagramDataset(
    usernameParsedAssistStructure: ParsedAssistStructure,
    passwordParsedAssistStructure: ParsedAssistStructure
  ): Dataset {
    val usernamePresentation = preparePresentation("Instagram username dataset")
    val passwordPresentation = preparePresentation("Instagram password dataset")

    return Dataset.Builder()
        .setValue(
            usernameParsedAssistStructure.id,
            AutofillValue.forText("instagram username"),
            usernamePresentation
        )
        .setValue(
            passwordParsedAssistStructure.id,
            AutofillValue.forText("instagram password"),
            passwordPresentation
        )
        .build()
  }

  private fun getPOCDataset(
    privateKey: String,
    password: String,
    usernameParsedAssistStructure: ParsedAssistStructure,
    passwordParsedAssistStructure: ParsedAssistStructure
  ): Dataset {

    val usernamePresentation = preparePresentation("Encrypted username dataset")
    val passwordPresentation = preparePresentation("Encrypted password dataset")

    val decryptedCredentials = Helper.decryptMessageArmored(
        privateKey,
        password.toByteArray(),
        ENCRYPTED_CREDENTIALS
    )

    return Dataset.Builder()
        .setValue(
            usernameParsedAssistStructure.id,
            AutofillValue.forText(decryptedCredentials),
            usernamePresentation
        )
        .setValue(
            passwordParsedAssistStructure.id,
            AutofillValue.forText(decryptedCredentials),
            passwordPresentation
        )
        .build()
  }

  private fun showError(message: String?) {
    MaterialAlertDialogBuilder(this)
        .setTitle(string.error)
        .setMessage(message ?: getString(string.unknown_error))
        .setPositiveButton(string.ok) { dialog, _ ->
          dialog.dismiss()
          finish()
        }
        .show()
  }

  companion object {
    private val ENCRYPTED_CREDENTIALS = """
      -----BEGIN PGP MESSAGE-----
      Version: GopenPGP 2.1.0
      Comment: https://gopenpgp.org
      
      wcDMA3tBIduonoTEAQv+PvQmbRIdLCf0+pxStDJV/Jl2YfZsx1m2TlQZyMen/RoV
      HRVWMEzd+nGx7bnVTSjxGtVWVXTEv+DO9bkzIfYIpz3+kzyGVTeauVMFd+DEyfyL
      5ZW/eU010MeTzDSEKZT0xLq/CA4i+yHaAEc9XxGMSAJlZhH4dY4vXW+iwXMWxXAw
      LWppFQBWjq3Ay3GKcck/fjRTmIEzTbpOVOet9Bm2MeJCfWsH6M1psZz1/CfNDNwn
      orGNhjBKd4zHr3brQey0tmHNJ+cD2U1lZJSYBP6sAgWSaF8+V5Exnlb91ao/7/eX
      COR/4HgYjnthVwOo+BskkSlMMcgqIeMcF609HjC39FdMuY+N8OLfHXFWRgXt3Mnd
      32V5e96MyjNIL5lOowayL5oA1PYK/hBPgQeDR0P55S7eMU0af9B8xcdf2t8M2hA2
      mcXqCfRL+vmKaOATeYtLiyzy5Ju7UL3eKJeZxEb0da1+xydUyaNDP0EBTMcs80+X
      xxDa1h3f65or0fW3OV/c0kYBMOI//sh1z1i2ImmpF1EOHWrGcCcYTGS54qDSXDxZ
      80XPPbF/Y4unb+AfekC+Zrk1GahNwILI9FVYz2Mmjip/eFW67QDu
      =22NN
      -----END PGP MESSAGE-----
    """.trimIndent()

    const val EXTRA_PACKAGE_NAME = "EXTRA_PACKAGE_NAME"
  }
}
