package com.passbolt.poc.milestones.encryption

import androidx.annotation.IdRes
import androidx.annotation.StringRes

data class EncryptionFeature(
  @StringRes val nameId: Int,
  @IdRes val actionId: Int
)
