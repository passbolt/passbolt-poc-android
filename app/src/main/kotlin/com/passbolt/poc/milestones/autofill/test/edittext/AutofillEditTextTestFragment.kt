package com.passbolt.poc.milestones.autofill.test.edittext

import android.os.Bundle
import android.view.View
import android.view.autofill.AutofillManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.passbolt.poc.R

class AutofillEditTextTestFragment : Fragment(R.layout.fragment_autofill_edittext_test) {
  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    setupEditText(view)
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

  private fun isValidCredentials(
    username: String?,
    password: String?
  ): Boolean {
    return username != null && password != null && username.equals(password, ignoreCase = true)
  }
}
