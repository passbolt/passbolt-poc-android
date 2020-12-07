package com.passbolt.poc.milestones.autofill.parse

import android.app.assist.AssistStructure
import android.app.assist.AssistStructure.ViewNode
import android.view.View

class AssistStructureParser(
  private val structure: AssistStructure
) {

  fun parse(): AssistStructureParsingResult {
    val webDomainStringBuilder = StringBuilder()

    val parsedAssistStructures = parseWithProcessor { node ->
      val webDomain = node.webDomain
      if (webDomain != null) {
        if (webDomainStringBuilder.isNotEmpty()) {
          if (webDomain != webDomainStringBuilder.toString()) {
            throw SecurityException(
                "Found multiple web domains: valid= " +
                    webDomainStringBuilder + ", child=" + webDomain
            )
          }
        } else {
          webDomainStringBuilder.append(webDomain)
        }
      }

      val id = node.autofillId ?: return@parseWithProcessor null
      val autoFillHints = node.autofillHints?.toMutableList() ?: mutableListOf()
      if (autoFillHints.isEmpty()) {
        // check if hint contains username or password
        val textHint = node.hint ?: ""
        val contentDescription = node.contentDescription ?: ""

        when {
          textHint.contains(AUTOFILL_HINT_EMAIL, ignoreCase = true) -> {
            autoFillHints.add(View.AUTOFILL_HINT_USERNAME)
          }
          textHint.contains(View.AUTOFILL_HINT_USERNAME, ignoreCase = true) -> {
            autoFillHints.add(View.AUTOFILL_HINT_USERNAME)
          }
          textHint.contains(View.AUTOFILL_HINT_PASSWORD, ignoreCase = true) -> {
            autoFillHints.add(View.AUTOFILL_HINT_PASSWORD)
          }
          contentDescription.contains(AUTOFILL_HINT_EMAIL, ignoreCase = true) -> {
            autoFillHints.add(View.AUTOFILL_HINT_USERNAME)
          }
          contentDescription.contains(View.AUTOFILL_HINT_USERNAME, ignoreCase = true) -> {
            autoFillHints.add(View.AUTOFILL_HINT_USERNAME)
          }
          contentDescription.contains(View.AUTOFILL_HINT_PASSWORD, ignoreCase = true) -> {
            autoFillHints.add(View.AUTOFILL_HINT_PASSWORD)
          }
          else -> {
            return@parseWithProcessor null
          }
        }
      }
      val type = node.autofillType

      ParsedAssistStructure(id, autoFillHints, type)
    }

    return AssistStructureParsingResult(parsedAssistStructures, webDomainStringBuilder.toString())
  }

  private fun parseWithProcessor(
    processor: (ViewNode) -> ParsedAssistStructure?
  ): Set<ParsedAssistStructure> {
    val result = mutableSetOf<ParsedAssistStructure>()
    for (i in 0 until structure.windowNodeCount) {
      val viewNode = structure.getWindowNodeAt(i).rootViewNode
      traverseRoot(viewNode, result, processor)
    }
    return result
  }

  private fun traverseRoot(
    viewNode: ViewNode,
    result: MutableSet<ParsedAssistStructure>,
    processor: (ViewNode) -> ParsedAssistStructure?
  ) {
    processor(viewNode)?.let {
      result.add(it)
    }
    val childrenSize = viewNode.childCount
    for (i in 0 until childrenSize) {
      traverseRoot(viewNode.getChildAt(i), result, processor)
    }
  }

  companion object {
    private const val AUTOFILL_HINT_EMAIL = "email"
  }
}
