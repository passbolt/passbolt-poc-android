package com.passbolt.poc.util

import android.content.Context
import android.content.pm.PackageManager
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.MasterKey

object EncryptionKeyManager {

  private const val AUTHENTICATION_TIMEOUT_SECONDS = 10

  fun getMasterKey(context: Context): MasterKey {
    val spec = KeyGenParameterSpec.Builder(
        MasterKey.DEFAULT_MASTER_KEY_ALIAS,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
        .apply {
          setBlockModes(KeyProperties.BLOCK_MODE_GCM)
          setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
          setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
          setUserAuthenticationRequired(true)
          setInvalidatedByBiometricEnrollment(false)
          setUserAuthenticationParameters(
              AUTHENTICATION_TIMEOUT_SECONDS,
              KeyProperties.AUTH_DEVICE_CREDENTIAL or KeyProperties.AUTH_BIOMETRIC_STRONG
          )
          setUnlockedDeviceRequired(true)

          // use StrongBox if supported
          if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)) {
            setIsStrongBoxBacked(true)
          }
        }
        .build()

    return MasterKey.Builder(context)
        .setKeyGenParameterSpec(spec)
        .build()
  }
}
