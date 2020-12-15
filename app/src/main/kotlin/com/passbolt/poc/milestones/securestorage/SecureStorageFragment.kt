package com.passbolt.poc.milestones.securestorage

import android.app.KeyguardManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.biometric.BiometricPrompt.AuthenticationResult
import androidx.biometric.auth.AuthPromptCallback
import androidx.biometric.auth.startClass3BiometricOrCredentialAuthentication
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.passbolt.poc.R
import com.passbolt.poc.R.string
import com.passbolt.poc.util.EncryptedPreferences
import com.passbolt.poc.util.Keys

class SecureStorageFragment : Fragment(R.layout.fragment_securestorage) {

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    val keyguardManager = requireContext().getSystemService(KeyguardManager::class.java)
    if (!keyguardManager.isDeviceSecure) {
      showError(getString(string.secure_storage_device_not_secure_message))
      return
    }

    val publicKeyEditText = view.findViewById<EditText>(R.id.public_key_edit_text)
    val privateKeyEditText = view.findViewById<EditText>(R.id.private_key_edit_text)
    val passwordKeyEditText = view.findViewById<EditText>(R.id.password_edit_text)

    view.findViewById<Button>(R.id.button_key_pair1)
        .setOnClickListener {
          publicKeyEditText.setText(Keys.PUBLIC_KEY1)
          privateKeyEditText.setText(Keys.PRIVATE_KEY1)
          passwordKeyEditText.setText(Keys.PRIVATE_KEY1_PASSWORD)
        }

    view.findViewById<Button>(R.id.button_key_pair2)
        .setOnClickListener {
          publicKeyEditText.setText(Keys.PUBLIC_KEY2)
          privateKeyEditText.setText(Keys.PRIVATE_KEY2)
          passwordKeyEditText.setText(Keys.PRIVATE_KEY2_PASSWORD)
        }

    view.findViewById<Button>(R.id.button_read_keys_from_prefs_auth)
        .setOnClickListener {
          doAfterAuth {
            val encryptedPreferences = EncryptedPreferences(requireContext().applicationContext)
            publicKeyEditText.setText(encryptedPreferences.getPublicKey())
            privateKeyEditText.setText(encryptedPreferences.getPrivateKey())
            passwordKeyEditText.setText(encryptedPreferences.getPassword())
          }
        }

    view.findViewById<Button>(R.id.button_read_keys_from_prefs_no_auth)
        .setOnClickListener {
          try {
            val encryptedPreferences = EncryptedPreferences(requireContext().applicationContext)
            publicKeyEditText.setText(encryptedPreferences.getPublicKey())
            privateKeyEditText.setText(encryptedPreferences.getPrivateKey())
            passwordKeyEditText.setText(encryptedPreferences.getPassword())
          } catch (e: Exception) {
            showError(getString(string.securestorage_auth_required))
          }
        }

    view.findViewById<Button>(R.id.button_save_keys_in_prefs_auth)
        .setOnClickListener {
          doAfterAuth {
            val encryptedPreferences = EncryptedPreferences(requireContext().applicationContext)
            val publicKey = publicKeyEditText.text.toString()
            val privateKey = privateKeyEditText.text.toString()
            val password = passwordKeyEditText.text.toString()
            encryptedPreferences.saveKeyDataIfAbsent(publicKey, privateKey, password)
          }
        }

    view.findViewById<Button>(R.id.button_save_keys_in_prefs_no_auth)
        .setOnClickListener {
          try {
            val encryptedPreferences = EncryptedPreferences(requireContext().applicationContext)
            val publicKey = publicKeyEditText.text.toString()
            val privateKey = privateKeyEditText.text.toString()
            val password = passwordKeyEditText.text.toString()
            encryptedPreferences.saveKeyDataIfAbsent(publicKey, privateKey, password)
          } catch (e: Exception) {
            showError(getString(string.securestorage_auth_required))
          }
        }

    view.findViewById<Button>(R.id.button_clear_prefs)
        .setOnClickListener {
          doAfterAuth {
            val encryptedPreferences = EncryptedPreferences(requireContext().applicationContext)
            encryptedPreferences.clear()
          }
        }
  }

  private fun doAfterAuth(action: () -> Unit) {
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
                requireContext().applicationContext,
                "Authentication error: $errString", Toast.LENGTH_SHORT
            )
                .show()
          }

          override fun onAuthenticationSucceeded(
            activity: FragmentActivity?,
            result: AuthenticationResult
          ) {
            super.onAuthenticationSucceeded(activity, result)
            action()
          }

          override fun onAuthenticationFailed(activity: FragmentActivity?) {
            super.onAuthenticationFailed(activity)
            Toast.makeText(
                requireContext().applicationContext, "Authentication failed",
                Toast.LENGTH_SHORT
            )
                .show()
          }
        }
    )
  }

  private fun showError(message: String?) {
    MaterialAlertDialogBuilder(requireContext())
        .setTitle(string.error)
        .setMessage(message ?: getString(string.unknown_error))
        .setPositiveButton(string.ok) { dialog, _ ->
          dialog.dismiss()
        }
        .show()
  }
}
