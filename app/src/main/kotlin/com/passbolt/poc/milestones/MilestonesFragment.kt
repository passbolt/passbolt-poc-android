package com.passbolt.poc.milestones

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.ListFragment
import androidx.navigation.fragment.findNavController
import com.passbolt.poc.R
import com.passbolt.poc.util.FeatureElement

class MilestonesFragment : ListFragment() {

  private val milestoneFeatures = listOf(
      FeatureElement(
          R.string.milestone_encryption,
          R.id.action_milestonesFragment_to_encryptionFragment
      ),
      FeatureElement(
          R.string.milestone_autofill,
          R.id.action_milestonesFragment_to_autofillFragment
      ),
      FeatureElement(
          R.string.milestone_secure_storage,
          R.id.action_milestonesFragment_to_securestorageFragment
      ),
      FeatureElement(
          R.string.milestone_setup,
          View.NO_ID
      )
  )

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    val milestoneNames = milestoneFeatures.map { getString(it.nameId) }
    listAdapter =
      ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, milestoneNames)
  }

  override fun onListItemClick(
    l: ListView,
    v: View,
    position: Int,
    id: Long
  ) {
    super.onListItemClick(l, v, position, id)
    val actionId = milestoneFeatures[position].actionId
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
