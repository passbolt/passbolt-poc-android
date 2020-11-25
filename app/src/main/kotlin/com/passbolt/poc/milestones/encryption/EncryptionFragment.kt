package com.passbolt.poc.milestones.encryption

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

class EncryptionFragment : ListFragment() {

  private val encryptionFeatures = listOf(
      EncryptionFeature(
          string.encryption_encrypt_decrypt,
          R.id.action_encryptionFragment_to_encryptDecryptFragment
      ),
      EncryptionFeature(
          string.encryption_sign_verify,
          R.id.action_encryptionFragment_to_signVerifyFragment
      )
  )

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    val featureNames = encryptionFeatures.map { getString(it.nameId) }
    listAdapter = ArrayAdapter(requireContext(), layout.simple_list_item_1, featureNames)
  }

  override fun onListItemClick(
    l: ListView,
    v: View,
    position: Int,
    id: Long
  ) {
    super.onListItemClick(l, v, position, id)
    val actionId = encryptionFeatures[position].actionId
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
