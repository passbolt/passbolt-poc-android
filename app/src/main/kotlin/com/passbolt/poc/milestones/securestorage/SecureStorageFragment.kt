package com.passbolt.poc.milestones.securestorage

import android.app.KeyguardManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.passbolt.poc.R
import com.passbolt.poc.R.string
import com.passbolt.poc.util.Keys
import com.passbolt.poc.util.SecureStorage
import com.passbolt.poc.util.doAfterAuth
import com.passbolt.poc.util.showError
import kotlinx.coroutines.launch

class SecureStorageFragment : Fragment(R.layout.fragment_securestorage) {

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    val keyguardManager = requireContext().getSystemService(KeyguardManager::class.java)
    if (!keyguardManager.isDeviceSecure) {
      requireActivity().showError(getString(string.secure_storage_device_not_secure_message))
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
          requireActivity().doAfterAuth {
            lifecycleScope.launch {
              val keyData = SecureStorage.getKeyData(requireContext()) ?: return@launch
              publicKeyEditText.setText(keyData.public_key)
              privateKeyEditText.setText(keyData.private_key)
              passwordKeyEditText.setText(keyData.password)
            }
          }
        }

    view.findViewById<Button>(R.id.button_read_keys_from_prefs_no_auth)
        .setOnClickListener {
          lifecycleScope.launch {
            try {
              val keyData = SecureStorage.getKeyData(requireContext()) ?: return@launch
              publicKeyEditText.setText(keyData.public_key)
              privateKeyEditText.setText(keyData.private_key)
              passwordKeyEditText.setText(keyData.password)
            } catch (e: Exception) {
              requireActivity().showError(getString(string.securestorage_auth_required))
            }
          }
        }

    view.findViewById<Button>(R.id.button_save_keys_in_prefs_auth)
        .setOnClickListener {
          requireActivity().doAfterAuth {
            lifecycleScope.launch {
              val publicKey = publicKeyEditText.text.toString()
              val privateKey = privateKeyEditText.text.toString()
              val password = passwordKeyEditText.text.toString()
              SecureStorage.saveKeyDataIfAbsent(requireContext(), publicKey, privateKey, password)
            }
          }
        }

    view.findViewById<Button>(R.id.button_save_keys_in_prefs_no_auth)
        .setOnClickListener {
          lifecycleScope.launch {
            try {
              val publicKey = publicKeyEditText.text.toString()
              val privateKey = privateKeyEditText.text.toString()
              val password = passwordKeyEditText.text.toString()
              SecureStorage.saveKeyDataIfAbsent(requireContext(), publicKey, privateKey, password)
            } catch (e: Exception) {
              requireActivity().showError(getString(string.securestorage_auth_required))
            }
          }
        }

    view.findViewById<Button>(R.id.button_clear_prefs)
        .setOnClickListener {
          requireActivity().doAfterAuth {
            lifecycleScope.launch {
              SecureStorage.clear(requireContext())
            }
          }
        }
  }
}
