package com.passbolt.poc.milestones.autofill

import android.R.layout
import android.app.PendingIntent
import android.app.assist.AssistStructure
import android.content.Intent
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import android.view.View
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import com.passbolt.poc.milestones.autofill.applinkverifier.AppLinkVerifier
import com.passbolt.poc.milestones.autofill.auth.AutofillAuthActivity
import com.passbolt.poc.milestones.autofill.fingerprintverifier.FingerprintVerifier
import com.passbolt.poc.milestones.autofill.manualfill.ManualFillActivity
import com.passbolt.poc.milestones.autofill.parse.AssistStructureParser
import com.passbolt.poc.milestones.autofill.parse.ParsedAssistStructure
import com.passbolt.poc.util.TrustedAppsInfo

class PassboltPocAutofillService : AutofillService() {

  override fun onFillRequest(
    request: FillRequest,
    cancellationSignal: CancellationSignal,
    callback: FillCallback
  ) {
    val structure: AssistStructure = request.fillContexts.last().structure
    val manualFillRequested = request.flags and FillRequest.FLAG_MANUAL_REQUEST != 0

    val packageName = structure.activityComponent.packageName

    val appLinkVerifier = AppLinkVerifier()
    val fingerprintVerifier = FingerprintVerifier(packageManager)

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
      callback.onSuccess(null)
      return
    }

    if (manualFillRequested) {
      callback.onSuccess(
          getManualFillResponse(usernameParsedAssistStructure, passwordParsedAssistStructure)
      )
    } else {

      // Verify app signature. For the purpose of this POC we support filling only
      // Passbolt POC, Instagram, Facebook and Google Chrome apps
      if (!fingerprintVerifier.verifySignatures(packageName)) {
        callback.onFailure("Incorrect signatures.")
      }

      // Special handling for Instagram - POC imitates App Link verification
      if (packageName == TrustedAppsInfo.INSTAGRAM_PACKAGE ||
          (
              webDomain.isNotEmpty() && TrustedAppsInfo.INSTAGRAM_WEB_ADDRESS.contains(
                  webDomain, ignoreCase = true
              )
              )
      ) {
        appLinkVerifier.verify(packageName, webDomain) { success ->
          if (success) {
            callback.onSuccess(
                getInstagramFillResponse(
                    usernameParsedAssistStructure, passwordParsedAssistStructure, packageName
                )
            )
          } else {
            callback.onFailure("Couldn't verify App Link.")
          }
        }
      } else {
        callback.onSuccess(
            getPOCFillResponse(usernameParsedAssistStructure, passwordParsedAssistStructure)
        )
      }
    }
  }

  override fun onSaveRequest(
    request: SaveRequest,
    callback: SaveCallback
  ) {
    /* not supported in POC */
  }

  private fun preparePresentation(text: String): RemoteViews {
    return RemoteViews(packageName, layout.simple_list_item_1).apply {
      setTextViewText(android.R.id.text1, text)
    }
  }

  private fun getInstagramFillResponse(
    usernameParsedAssistStructure: ParsedAssistStructure,
    passwordParsedAssistStructure: ParsedAssistStructure,
    packageName: String
  ): FillResponse {
    // Build the presentation of the datasets
    val instagramPresentation = preparePresentation("Instagram credentials")

    val intent = Intent(applicationContext, AutofillAuthActivity::class.java)
    intent.putExtra(AutofillAuthActivity.EXTRA_PACKAGE_NAME, packageName)
    val sender = PendingIntent.getActivity(
        applicationContext, 0, intent,
        PendingIntent.FLAG_CANCEL_CURRENT
    ).intentSender

    return FillResponse.Builder()
        .addDataset(
            Dataset.Builder()
                .setAuthentication(sender)
                .setValue(
                    usernameParsedAssistStructure.id,
                    AutofillValue.forText(null),
                    instagramPresentation
                )
                .setValue(
                    passwordParsedAssistStructure.id,
                    AutofillValue.forText(null),
                    instagramPresentation
                )
                .build()
        )
        .build()
  }

  private fun getPOCFillResponse(
    usernameParsedAssistStructure: ParsedAssistStructure,
    passwordParsedAssistStructure: ParsedAssistStructure
  ): FillResponse {
    // Build the presentation of the datasets
    val usernamePresentation = preparePresentation("Unencrypted username dataset")
    val passwordPresentation = preparePresentation("Unencrypted password dataset")
    val authPresentation = preparePresentation("Authenticate to use this dataset")

    val intent = Intent(applicationContext, AutofillAuthActivity::class.java)
    val sender = PendingIntent.getActivity(
        applicationContext, 0, intent,
        PendingIntent.FLAG_CANCEL_CURRENT
    ).intentSender

    return FillResponse.Builder()
        .addDataset(
            Dataset.Builder()
                .setAuthentication(sender)
                .setValue(
                    usernameParsedAssistStructure.id,
                    AutofillValue.forText(null),
                    authPresentation
                )
                .setValue(
                    passwordParsedAssistStructure.id,
                    AutofillValue.forText(null),
                    authPresentation
                )
                .build()
        )
        .addDataset(
            Dataset.Builder()
                .setValue(
                    usernameParsedAssistStructure.id,
                    AutofillValue.forText("Unencrypted credentials"),
                    usernamePresentation
                )
                .setValue(
                    passwordParsedAssistStructure.id,
                    AutofillValue.forText("Unencrypted credentials"),
                    passwordPresentation
                )
                .build()
        )
        .build()
  }

  private fun getManualFillResponse(
    usernameParsedAssistStructure: ParsedAssistStructure,
    passwordParsedAssistStructure: ParsedAssistStructure
  ): FillResponse {
    // Build the presentation of the datasets
    val manualPresentation = preparePresentation("Tap to manually select data")

    val intent = Intent(applicationContext, ManualFillActivity::class.java)
    val sender = PendingIntent.getActivity(
        applicationContext, 0, intent,
        PendingIntent.FLAG_CANCEL_CURRENT
    ).intentSender

    return FillResponse.Builder()
        .addDataset(
            Dataset.Builder()
                .setAuthentication(sender)
                .setValue(
                    usernameParsedAssistStructure.id,
                    AutofillValue.forText(null),
                    manualPresentation
                )
                .setValue(
                    passwordParsedAssistStructure.id,
                    AutofillValue.forText(null),
                    manualPresentation
                )
                .build()
        )
        .build()
  }
}
