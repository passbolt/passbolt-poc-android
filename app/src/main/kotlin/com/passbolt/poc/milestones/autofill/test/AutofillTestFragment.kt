package com.passbolt.poc.milestones.autofill.test

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.autofill.AutofillManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.passbolt.poc.R

class AutofillTestFragment : Fragment(R.layout.fragment_autofill_test) {

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    setupEditText(view)
    setupWebView(view.findViewById(R.id.webview))
  }

  private fun setupEditText(view: View) {
    val usernameEditText = view.findViewById<EditText>(R.id.username_edit_text)
    val passwordEditText = view.findViewById<EditText>(R.id.password_edit_text)

    view.findViewById<Button>(R.id.button_login)
        .setOnClickListener {
          val username = usernameEditText.text.toString()
          val password = passwordEditText.text.toString()
          if (isValidCredentials(username, password)) {
            Toast.makeText(requireContext(), "Authentication success.", Toast.LENGTH_SHORT)
                .show()
          } else {
            Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT)
                .show()
          }
        }
    view.findViewById<Button>(R.id.button_clear)
        .setOnClickListener {
          requireContext().getSystemService(AutofillManager::class.java)
              .cancel()
          usernameEditText.text.clear()
          passwordEditText.text.clear()
        }
  }

  @SuppressLint("SetJavaScriptEnabled")
  private fun setupWebView(webView: WebView) {
    webView.apply {
      webViewClient = WebViewClient()
      settings.javaScriptEnabled = true

      clearHistory()
      clearFormData()
      clearCache(true)
      loadUrl("file:///android_res/raw/webview_sample.html")
    }
  }

  private fun isValidCredentials(
    username: String?,
    password: String?
  ): Boolean {
    return username != null && password != null && username.equals(password, ignoreCase = true)
  }
}
