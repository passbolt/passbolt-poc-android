package com.passbolt.poc.milestones.autofill.test

import androidx.annotation.IdRes
import androidx.annotation.StringRes

data class AutofillTestFeature(
  @StringRes val nameId: Int,
  @IdRes val actionId: Int
)
