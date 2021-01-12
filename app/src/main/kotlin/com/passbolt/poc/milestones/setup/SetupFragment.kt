package com.passbolt.poc.milestones.setup

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.passbolt.poc.R
import com.passbolt.poc.R.string
import com.passbolt.poc.util.showError

class SetupFragment : Fragment(R.layout.fragment_setup) {

  private val requestPermissionLauncher =
    registerForActivityResult(RequestPermission()) { isGranted: Boolean ->
      if (isGranted) {
        onPermissionGranted()
      } else {
        requireActivity().showError(getString(string.setup_camera_permission_required))
      }
    }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    view.findViewById<Button>(R.id.button_start_setup)
        .setOnClickListener {
          when {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
              onPermissionGranted()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
              showExplanationDialog()
            }
            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
          }
        }
  }

  private fun showExplanationDialog() {
    MaterialAlertDialogBuilder(requireActivity())
        .setTitle(string.error)
        .setMessage(getString(string.setup_camera_permission_required))
        .setPositiveButton(getString(string.go_to_settings)) { dialog, _ ->
          dialog.dismiss()
          startActivity(
              Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("$PACKAGE_PREFIX${requireActivity().packageName}")
              }
          )
        }
        .setNegativeButton(getString(string.cancel)) { dialog, _ ->
          dialog.dismiss()
        }
        .show()
  }

  private fun onPermissionGranted() {
    findNavController().navigate(R.id.action_setupFragment_to_setupCameraFragment)
  }

  companion object {
    private const val PACKAGE_PREFIX = "package:"
  }
}
