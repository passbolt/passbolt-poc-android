package com.passbolt.poc.util

import crypto.Crypto
import helper.Helper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// A coroutine wrapper for Gopenpgp library
object Gopenpgp {

  suspend fun encryptMessageArmored(
    key: String,
    message: String
  ): String = withContext(Dispatchers.Default) {
    Helper.encryptMessageArmored(key, message)
  }

  suspend fun decryptMessageArmored(
    key: String,
    password: ByteArray,
    message: String
  ): String = withContext(Dispatchers.Default) {
    Helper.decryptMessageArmored(key, password, message)
  }

  suspend fun signCleartextMessageArmored(
    key: String,
    password: ByteArray,
    message: String
  ): String = withContext(Dispatchers.Default) {
    Helper.signCleartextMessageArmored(key, password, message)
  }

  suspend fun verifyCleartextMessageArmored(
    key: String,
    message: String
  ): String = withContext(Dispatchers.Default) {
    Helper.verifyCleartextMessageArmored(key, message, Crypto.getUnixTime())
  }
}
