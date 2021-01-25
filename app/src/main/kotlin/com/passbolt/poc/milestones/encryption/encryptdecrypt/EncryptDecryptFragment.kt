package com.passbolt.poc.milestones.encryption.encryptdecrypt

import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.passbolt.poc.R
import com.passbolt.poc.util.Gopenpgp
import com.passbolt.poc.util.Keys
import com.passbolt.poc.util.showError
import kotlinx.coroutines.launch

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
          lifecycleScope.launch {
            try {
              val message = messageEditText.text.toString()
              val key = keyEditText.text.toString()
              val start = SystemClock.uptimeMillis()
              val encrypted = Gopenpgp.encryptMessageArmored(key, message)
              val elapsed = SystemClock.uptimeMillis() - start
              Toast.makeText(
                  requireContext(), getString(R.string.operation_time, elapsed), Toast.LENGTH_SHORT
              )
                  .show()
              resultEditText.setText(encrypted)
            } catch (e: Exception) {
              requireActivity().showError(e.message)
            }
          }
        }

    view.findViewById<Button>(R.id.button_decrypt)
        .setOnClickListener {
          lifecycleScope.launch {
            try {
              val message = messageEditText.text.toString()
              val key = keyEditText.text.toString()
              val password = passwordEditText.text.toString()
                  .toByteArray()
              val start = SystemClock.uptimeMillis()
              val decrypted = Gopenpgp.decryptMessageArmored(key, password, message)
              val elapsed = SystemClock.uptimeMillis() - start
              Toast.makeText(
                  requireContext(), getString(R.string.operation_time, elapsed), Toast.LENGTH_SHORT
              )
                  .show()
              resultEditText.setText(decrypted)
            } catch (e: Exception) {
              requireActivity().showError(e.message)
            }
          }
        }
  }
}
