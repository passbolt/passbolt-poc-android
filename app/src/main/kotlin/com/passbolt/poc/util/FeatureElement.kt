package com.passbolt.poc.util

import androidx.annotation.IdRes
import androidx.annotation.StringRes

data class FeatureElement(
  @StringRes val nameId: Int,
  @IdRes val actionId: Int
)
