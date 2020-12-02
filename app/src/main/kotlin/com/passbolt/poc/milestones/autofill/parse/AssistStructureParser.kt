package com.passbolt.poc.milestones.autofill.parse

import android.app.assist.AssistStructure
import android.app.assist.AssistStructure.ViewNode

class AssistStructureParser(
  private val structure: AssistStructure
) {
  fun parse(processor: (ViewNode) -> ParsedAssistStructure?): Set<ParsedAssistStructure> {
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
}
