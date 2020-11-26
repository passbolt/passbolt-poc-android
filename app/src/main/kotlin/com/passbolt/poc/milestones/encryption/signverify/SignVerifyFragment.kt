package com.passbolt.poc.milestones.encryption.signverify

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.passbolt.poc.R
import com.passbolt.poc.util.Keys
import crypto.Crypto
import helper.Helper

class SignVerifyFragment : Fragment(R.layout.fragment_sign_verify) {

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    val messageEditText = view.findViewById<EditText>(R.id.message_edit_text)
    val passwordEditText = view.findViewById<EditText>(R.id.password_edit_text)
    val keyEditText = view.findViewById<EditText>(R.id.key_edit_text)
    val resultEditText = view.findViewById<EditText>(R.id.result_edit_text)
    val key1RadioButton = view.findViewById<RadioButton>(R.id.radio_key1)
    val key2RadioButton = view.findViewById<RadioButton>(R.id.radio_key2)

    view.findViewById<Button>(R.id.button_public_key)
        .setOnClickListener {
          passwordEditText.text.clear()
          val key = when {
            key1RadioButton.isChecked -> Keys.PUBLIC_KEY1
            key2RadioButton.isChecked -> Keys.PUBLIC_KEY2
            else -> ""
          }
          keyEditText.setText(key)
        }

    view.findViewById<Button>(R.id.button_private_key)
        .setOnClickListener {
          val (key, password) = when {
            key1RadioButton.isChecked -> Pair(Keys.PRIVATE_KEY1, Keys.PRIVATE_KEY1_PASSWORD)
            key2RadioButton.isChecked -> Pair(Keys.PRIVATE_KEY2, Keys.PRIVATE_KEY2_PASSWORD)
            else -> Pair("", "")
          }
          passwordEditText.setText(password)
          keyEditText.setText(key)
        }

    view.findViewById<Button>(R.id.button_sign)
        .setOnClickListener {
          try {
            val message = messageEditText.text.toString()
            val key = keyEditText.text.toString()
            val password = passwordEditText.text.toString()
                .toByteArray()
            val signed = Helper.signCleartextMessageArmored(key, password, message)
            resultEditText.setText(signed)
          } catch (e: Exception) {
            showError(e.message)
          }
        }

    view.findViewById<Button>(R.id.button_verify)
        .setOnClickListener {
          try {
            val message = messageEditText.text.toString()
            val key = keyEditText.text.toString()
            val verified = Helper.verifyCleartextMessageArmored(key, message, Crypto.getUnixTime())
            resultEditText.setText(verified)
          } catch (e: Exception) {
            showError(e.message)
          }
        }
  }

  private fun showError(message: String?) {
    MaterialAlertDialogBuilder(requireContext())
        .setTitle(R.string.error)
        .setMessage(message ?: getString(R.string.unknown_error))
        .setPositiveButton(R.string.ok) { dialog, _ ->
          dialog.dismiss()
        }
        .show()
  }
}
