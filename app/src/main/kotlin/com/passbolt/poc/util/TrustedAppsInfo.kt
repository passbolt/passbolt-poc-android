package com.passbolt.poc.util

object TrustedAppsInfo {

  private const val PASSBOLT_POC_SIGNATURE =
    "73:30:F1:F9:81:E0:CD:1C:94:A4:52:9D:DD:6E:C2:F8:75:3E:7E:E8:A0:9E:27:73:7D:FD:F9:BD:F0:FF:8C:3D"
  private const val INSTAGRAM_SIGNATURE =
    "5F:3E:50:F4:35:58:3C:9A:E6:26:30:2A:71:F7:34:00:44:08:7A:7E:2C:60:AD:AC:FC:25:42:05:A9:93:E3:05"
  private const val FACEBOOK_SIGNATURE =
    "E3:F9:E1:E0:CF:99:D0:E5:6A:05:5B:A6:5E:24:1B:33:99:F7:CE:A5:24:32:6B:0C:DD:6E:C1:32:7E:D0:FD:C1"
  private const val GOOGLE_CHROME_SIGNATURE =
    "F0:FD:6C:5B:41:0F:25:CB:25:C3:B5:33:46:C8:97:2F:AE:30:F8:EE:74:11:DF:91:04:80:AD:6B:2D:60:DB:83"

  const val INSTAGRAM_PACKAGE = "com.instagram.android"

  const val INSTAGRAM_WEB_ADDRESS = "https://www.instagram.com"

  val trustedSignatures = setOf(
      PASSBOLT_POC_SIGNATURE,
      INSTAGRAM_SIGNATURE,
      FACEBOOK_SIGNATURE,
      GOOGLE_CHROME_SIGNATURE
  )
}
