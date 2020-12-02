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
import com.passbolt.poc.milestones.autofill.auth.AutofillAuthActivity
import com.passbolt.poc.milestones.autofill.parse.AssistStructureParser
import com.passbolt.poc.milestones.autofill.parse.ParsedAssistStructure

class PassboltPocAutofillService : AutofillService() {

  override fun onFillRequest(
    request: FillRequest,
    cancellationSignal: CancellationSignal,
    callback: FillCallback
  ) {
    val structure: AssistStructure = request.fillContexts.last().structure

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
      callback.onSuccess(null)
      return
    }

    // Build the presentation of the datasets
    val usernamePresentation = preparePresentation("Unencrypted username dataset")
    val passwordPresentation = preparePresentation("Unencrypted password dataset")
    val authPresentation = preparePresentation("Authenticate to use this dataset")

    val intent = Intent(applicationContext, AutofillAuthActivity::class.java)
    val sender = PendingIntent.getActivity(
        applicationContext, 0, intent,
        PendingIntent.FLAG_CANCEL_CURRENT
    ).intentSender

    val fillResponse: FillResponse = FillResponse.Builder()
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

    callback.onSuccess(fillResponse)
  }

  override fun onSaveRequest(
    request: SaveRequest,
    callback: SaveCallback
  ) {
  }

  private fun preparePresentation(text: String): RemoteViews {
    return RemoteViews(packageName, layout.simple_list_item_1).apply {
      setTextViewText(android.R.id.text1, text)
    }
  }
}
