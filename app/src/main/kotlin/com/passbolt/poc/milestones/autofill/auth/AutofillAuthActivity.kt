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
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.passbolt.poc.R.string
import com.passbolt.poc.milestones.autofill.parse.AssistStructureParser
import com.passbolt.poc.milestones.autofill.parse.ParsedAssistStructure

class AutofillAuthActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val info = BiometricPrompt.PromptInfo.Builder()
        .setTitle(getString(string.biometric_prompt_title))
        .setSubtitle(getString(string.biometric_prompt_subtitle))
        .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
        .build()

    val prompt = BiometricPrompt(
        this, ContextCompat.getMainExecutor(this),
        object : BiometricPrompt.AuthenticationCallback() {
          override fun onAuthenticationError(
            errorCode: Int,
            errString: CharSequence
          ) {
            super.onAuthenticationError(errorCode, errString)
            Toast.makeText(
                applicationContext,
                "Authentication error: $errString", Toast.LENGTH_SHORT
            )
                .show()
            finish()
          }

          override fun onAuthenticationSucceeded(
            result: BiometricPrompt.AuthenticationResult
          ) {
            super.onAuthenticationSucceeded(result)

            fillRequest()
            finish()
          }

          override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            Toast.makeText(
                applicationContext, "Authentication failed",
                Toast.LENGTH_SHORT
            )
                .show()
          }
        }
    )

    prompt.authenticate(info)
  }

  private fun fillRequest() {
    val structure = intent.getParcelableExtra<AssistStructure>(EXTRA_ASSIST_STRUCTURE)
    if (structure == null) {
      setResult(RESULT_CANCELED)
      return
    }

    val parser = AssistStructureParser(structure)

    val parsedAssistStructures = parser.parse { node ->
      val id = node.autofillId ?: return@parse null
      val hints = node.autofillHints?.asList() ?: listOf()
      if (hints.isEmpty()) {
        return@parse null
      }
      val type = node.autofillType

      ParsedAssistStructure(id, hints, type)
    }

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

    val usernamePresentation = preparePresentation("Encrypted username dataset")
    val passwordPresentation = preparePresentation("Encrypted password dataset")

    val dataset = Dataset.Builder()
        .setValue(
            usernameParsedAssistStructure.id,
            AutofillValue.forText("Decrypted credentials"),
            usernamePresentation
        )
        .setValue(
            passwordParsedAssistStructure.id,
            AutofillValue.forText("Decrypted credentials"),
            passwordPresentation
        )
        .build()

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
}
