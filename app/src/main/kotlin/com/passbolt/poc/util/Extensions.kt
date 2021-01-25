package com.passbolt.poc.util

import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricPrompt.AuthenticationResult
import androidx.biometric.auth.AuthPromptCallback
import androidx.biometric.auth.startClass3BiometricOrCredentialAuthentication
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.passbolt.poc.R.string

fun FragmentActivity.doAfterAuth(action: () -> Unit) {
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
          Log.e("Error", "Authentication failed")
        }
      }
  )
}

fun FragmentActivity.showError(
  message: String?,
  okAction: (() -> Unit)? = null
) {
  MaterialAlertDialogBuilder(this)
      .setTitle(string.error)
      .setMessage(message ?: getString(string.unknown_error))
      .setPositiveButton(string.ok) { dialog, _ ->
        okAction?.invoke()
        dialog.dismiss()
      }
      .show()
}
