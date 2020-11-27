package com.passbolt.poc.milestones.autofill.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.autofill.AutofillManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.passbolt.poc.R

class AutofillSettingsActivity : AppCompatActivity() {

  private lateinit var autoFillManager: AutofillManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_autofill_settings)

    autoFillManager = getSystemService(AutofillManager::class.java)

    findViewById<Button>(R.id.autofill_enable_button)
        .setOnClickListener {
          if (!autoFillManager.isAutofillSupported) {
            showDialog(getString(R.string.error), getString(R.string.autofill_not_supported))
          } else if (!autoFillManager.hasEnabledAutofillServices()) {
            startActivity(
                Intent(
                    Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE,
                    Uri.parse("package:$packageName")
                )
            )
          } else {
            showDialog(getString(R.string.info), getString(R.string.autofill_already_enabled))
          }
        }
  }

  private fun showDialog(
    title: String,
    message: String?
  ) {
    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(R.string.ok) { dialog, _ ->
          dialog.dismiss()
        }
        .show()
  }
}
