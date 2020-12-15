package com.passbolt.poc.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences

class EncryptedPreferences(
  context: Context
) {

  private val preferences: SharedPreferences

  init {
    preferences = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE_NAME,
        EncryptionKeyManager.getMasterKey(context),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
  }

  fun saveKeyDataIfAbsent(
    publicKey: String,
    privateKey: String,
    password: String
  ) {
    if (preferences.getString(PUBLIC_KEY_PREFERENCE_NAME, null) == null ||
        preferences.getString(PRIVATE_KEY_PREFERENCE_NAME, null) == null ||
        preferences.getString(PASSWORD_PREFERENCE_NAME, null) == null
    ) {
      with(preferences.edit()) {
        putString(PUBLIC_KEY_PREFERENCE_NAME, publicKey)
        putString(PRIVATE_KEY_PREFERENCE_NAME, privateKey)
        putString(PASSWORD_PREFERENCE_NAME, password)
        apply()
      }
    }
  }

  fun getPublicKey(): String {
    return preferences.getString(PUBLIC_KEY_PREFERENCE_NAME, null) ?: ""
  }

  fun getPrivateKey(): String {
    return preferences.getString(PRIVATE_KEY_PREFERENCE_NAME, null) ?: ""
  }

  fun getPassword(): String {
    return preferences.getString(PASSWORD_PREFERENCE_NAME, null) ?: ""
  }

  fun clear() {
    preferences.edit()
        .clear()
        .apply()
  }

  companion object {
    private const val PREFS_FILE_NAME = "passbolt_encrypted_prefs"

    private const val PUBLIC_KEY_PREFERENCE_NAME = "passbolt_public_key_preference"
    private const val PRIVATE_KEY_PREFERENCE_NAME = "passbolt_private_key_preference"
    private const val PASSWORD_PREFERENCE_NAME = "passbolt_password_preference"
  }
}
