package com.passbolt.poc.milestones.autofill.applinkverifier

import com.passbolt.poc.util.TrustedAppsInfo

class AppLinkVerifier {

  fun verify(
    packageName: String,
    domain: String,
    callback: (Boolean) -> Unit
  ) {
    // verify that it's an instagram app or instagram website
    // in real app we'll be using App Link REST API to find out matching packages and websites
    callback(
        TrustedAppsInfo.INSTAGRAM_PACKAGE == packageName ||
            TrustedAppsInfo.INSTAGRAM_WEB_ADDRESS.contains(domain, ignoreCase = true)
    )
  }
}
