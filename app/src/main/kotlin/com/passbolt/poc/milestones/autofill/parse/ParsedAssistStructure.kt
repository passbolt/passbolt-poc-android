package com.passbolt.poc.milestones.autofill.parse

import android.view.autofill.AutofillId

data class ParsedAssistStructure(
  val id: AutofillId,
  val hints: List<String>,
  val type: Int
)
