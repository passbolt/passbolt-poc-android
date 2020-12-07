package com.passbolt.poc.milestones.autofill.parse

data class AssistStructureParsingResult(
  val parsedAssistStructures: Set<ParsedAssistStructure>,
  val webDomain: String
)
