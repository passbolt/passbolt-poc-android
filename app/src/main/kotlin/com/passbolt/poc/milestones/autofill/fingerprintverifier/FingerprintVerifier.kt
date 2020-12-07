package com.passbolt.poc.milestones.autofill.fingerprintverifier

import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.util.Log
import com.passbolt.poc.util.TrustedAppsInfo
import java.io.ByteArrayInputStream
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.Locale

class FingerprintVerifier(
  private val packageManager: PackageManager
) {

  fun verifySignatures(packageName: String): Boolean {
    return try {
      val fingerprint = getFingerprint(packageName)
      val result = TrustedAppsInfo.trustedSignatures.contains(fingerprint)
      if (!result) {
        Log.e("Error", "Not trusted signature: $fingerprint for package: $packageName")
      }
      result
    } catch (e: Exception) {
      Log.e("Error", "Error getting signature hash for $packageName: $e")
      false
    }
  }

  @Throws(
      NameNotFoundException::class,
      IOException::class,
      NoSuchAlgorithmException::class,
      CertificateException::class
  )
  fun getFingerprint(
    packageName: String
  ): String {
    val packageInfo =
      packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)

    val signatures = packageInfo.signingInfo.apkContentsSigners
    if (signatures.size != 1) {
      throw SecurityException(packageName + " has " + signatures.size + " signatures")
    }
    val cert = signatures[0].toByteArray()
    ByteArrayInputStream(cert).use { input ->
      val factory = CertificateFactory.getInstance("X509")
      val x509 = factory.generateCertificate(input) as X509Certificate
      val md = MessageDigest.getInstance("SHA256")
      val publicKey = md.digest(x509.encoded)
      return toHexFormat(publicKey)
    }
  }

  private fun toHexFormat(bytes: ByteArray): String {
    val builder = StringBuilder(bytes.size * 2)
    for (i in bytes.indices) {
      var hex = Integer.toHexString(bytes[i].toInt())
      val length = hex.length
      if (length == 1) {
        hex = "0$hex"
      }
      if (length > 2) {
        hex = hex.substring(length - 2, length)
      }
      builder.append(hex.toUpperCase(Locale.ROOT))
      if (i < bytes.size - 1) {
        builder.append(':')
      }
    }
    return builder.toString()
  }
}
