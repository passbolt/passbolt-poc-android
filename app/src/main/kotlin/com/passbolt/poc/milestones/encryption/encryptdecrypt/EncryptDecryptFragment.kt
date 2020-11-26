package com.passbolt.poc.milestones.encryption.encryptdecrypt

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.passbolt.poc.R
import com.passbolt.poc.util.Keys
import helper.Helper

class EncryptDecryptFragment : Fragment(R.layout.fragment_encrypt_decrypt) {

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    val messageEditText = view.findViewById<EditText>(R.id.message_edit_text)
    val passwordEditText = view.findViewById<EditText>(R.id.password_edit_text)
    val keyEditText = view.findViewById<EditText>(R.id.key_edit_text)
    val resultEditText = view.findViewById<EditText>(R.id.result_edit_text)

    view.findViewById<Button>(R.id.button_public_key)
        .setOnClickListener {
          passwordEditText.text.clear()
          keyEditText.setText(Keys.PUBLIC_KEY1)
        }

    view.findViewById<Button>(R.id.button_private_key)
        .setOnClickListener {
          passwordEditText.setText(Keys.PRIVATE_KEY1_PASSWORD)
          keyEditText.setText(Keys.PRIVATE_KEY1)
        }

    view.findViewById<Button>(R.id.button_encrypt)
        .setOnClickListener {
          try {
            val message = messageEditText.text.toString()
            val key = keyEditText.text.toString()
            val encrypted = Helper.encryptMessageArmored(key, message)
            resultEditText.setText(encrypted)
          } catch (e: Exception) {
            showError(e.message)
          }
        }

    view.findViewById<Button>(R.id.button_decrypt)
        .setOnClickListener {
          try {
            val message = messageEditText.text.toString()
            val key = keyEditText.text.toString()
            val password = passwordEditText.text.toString()
                .toByteArray()
            val decrypted = Helper.decryptMessageArmored(key, password, message)
            resultEditText.setText(decrypted)
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
