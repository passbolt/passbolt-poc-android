package com.passbolt.poc.milestones.autofill

import androidx.annotation.IdRes
import androidx.annotation.StringRes

data class AutofillFeature(
  @StringRes val nameId: Int,
  @IdRes val actionId: Int
)
