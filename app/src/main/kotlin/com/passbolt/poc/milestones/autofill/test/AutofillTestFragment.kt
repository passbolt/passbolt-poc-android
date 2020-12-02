package com.passbolt.poc.milestones.autofill.test

import android.R.layout
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.ListFragment
import androidx.navigation.fragment.findNavController
import com.passbolt.poc.R
import com.passbolt.poc.R.string

class AutofillTestFragment : ListFragment() {

  private val autofillTestFeatures = listOf(
      AutofillTestFeature(
          string.autofill_edittext_header,
          R.id.action_autofillTestFragment_to_autofillEditTextTestFragment
      ),
      AutofillTestFeature(
          string.autofill_webview_header,
          R.id.action_autofillTestFragment_to_autofillWebViewTestFragment
      )
  )

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    val featureNames = autofillTestFeatures.map { getString(it.nameId) }
    listAdapter = ArrayAdapter(requireContext(), layout.simple_list_item_1, featureNames)
  }

  override fun onListItemClick(
    l: ListView,
    v: View,
    position: Int,
    id: Long
  ) {
    super.onListItemClick(l, v, position, id)
    val actionId = autofillTestFeatures[position].actionId
    if (actionId == View.NO_ID) {
      Toast.makeText(
          requireContext(),
          getString(R.string.not_implemented), Toast.LENGTH_SHORT
      )
          .show()
    } else {
      findNavController().navigate(actionId)
    }
  }
}
