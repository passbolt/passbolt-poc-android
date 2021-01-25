package com.passbolt.poc.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

  suspend fun saveDatabasePassword(password: String) = withContext(Dispatchers.IO) {
    preferences.edit(commit = true) {
      putString(DB_PASSWORD_PREFERENCE_NAME, password)
    }
  }

  suspend fun getDatabasePassword(): String = withContext(Dispatchers.IO) {
    preferences.getString(DB_PASSWORD_PREFERENCE_NAME, null) ?: ""
  }

  suspend fun clear() = withContext(Dispatchers.IO) {
    preferences.edit(commit = true) {
      clear()
    }
  }

  companion object {
    private const val PREFS_FILE_NAME = "passbolt_encrypted_prefs"

    private const val DB_PASSWORD_PREFERENCE_NAME = "passbolt_db_password_preference"
  }
}
