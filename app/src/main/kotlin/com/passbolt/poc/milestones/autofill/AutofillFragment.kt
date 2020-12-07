package com.passbolt.poc.milestones.autofill

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
import com.passbolt.poc.util.FeatureElement

class AutofillFragment : ListFragment() {

  private val autofillFeatures = listOf(
      FeatureElement(
          string.autofill_settings,
          R.id.action_autofillFragment_to_autofillSettingsActivity
      ),
      FeatureElement(
          string.autofill_test,
          R.id.action_autofillFragment_to_autofillTestFragment
      )
  )

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    val featureNames = autofillFeatures.map { getString(it.nameId) }
    listAdapter = ArrayAdapter(requireContext(), layout.simple_list_item_1, featureNames)
  }

  override fun onListItemClick(
    l: ListView,
    v: View,
    position: Int,
    id: Long
  ) {
    super.onListItemClick(l, v, position, id)
    val actionId = autofillFeatures[position].actionId
    if (actionId == View.NO_ID) {
      Toast.makeText(
          requireContext(),
          getString(string.not_implemented), Toast.LENGTH_SHORT
      )
          .show()
    } else {
      findNavController().navigate(actionId)
    }
  }
}
