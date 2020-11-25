package com.passbolt.poc.milestones

import androidx.annotation.IdRes
import androidx.annotation.StringRes

data class MilestoneFeature(
  @StringRes val nameId: Int,
  @IdRes val actionId: Int
)
