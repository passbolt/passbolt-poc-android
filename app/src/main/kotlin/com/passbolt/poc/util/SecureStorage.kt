package com.passbolt.poc.util

import android.content.Context
import com.passbolt.poc.Database
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import compassboltpoc.KeyData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.security.SecureRandom

object SecureStorage {

  private const val DB_PASSWORD_LENGTH = 128

  private lateinit var driver: SqlDriver
  private lateinit var database: Database
  private lateinit var preferences: EncryptedPreferences

  // open database only once and keep the connection
  private suspend fun initIfNeeded(context: Context) = withContext(Dispatchers.IO) {
    if (::driver.isInitialized && ::database.isInitialized) {
      return@withContext
    }
    preferences = EncryptedPreferences(context)
    var dbPassword = preferences.getDatabasePassword()
    if (dbPassword.isEmpty()) {
      dbPassword = generateRandomPassword(DB_PASSWORD_LENGTH)
      preferences.saveDatabasePassword(dbPassword)
    }
    val factory = SupportFactory(SQLiteDatabase.getBytes(dbPassword.toCharArray()))
    driver = AndroidSqliteDriver(Database.Schema, context, "secure_storage.db", factory)
    database = Database(driver)
  }

  // generates a random password with a given length
  private fun generateRandomPassword(length: Int): String {
    val alphabet = "0123456789abcdefghijklmnopqrstuvwxyz-_ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val sr = SecureRandom()

    return buildString {
      repeat(length) {
        append(alphabet[sr.nextInt(alphabet.length)])
      }
    }
  }

  suspend fun saveKeyDataIfAbsent(
    context: Context,
    publicKey: String,
    privateKey: String,
    password: String
  ) = withContext(Dispatchers.IO) {
    initIfNeeded(context)
    database.secureStorageQueries.insert(KeyData(publicKey, privateKey, password))
  }

  suspend fun getKeyData(context: Context): KeyData? = withContext(Dispatchers.IO) {
    initIfNeeded(context)
    database.secureStorageQueries.selectAll()
        .executeAsOneOrNull()
  }

  suspend fun clear(context: Context) = withContext(Dispatchers.IO) {
    initIfNeeded(context)
    database.secureStorageQueries.deleteAll()
    preferences.clear()
  }
}
