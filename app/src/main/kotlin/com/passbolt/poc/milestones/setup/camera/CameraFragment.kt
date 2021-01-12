package com.passbolt.poc.milestones.setup.camera

import android.os.Bundle
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.mlkit.common.MlKitException
import com.passbolt.poc.R
import com.passbolt.poc.R.string
import com.passbolt.poc.milestones.setup.camera.processor.BarcodeScannerProcessor
import com.passbolt.poc.util.GraphicOverlay
import com.passbolt.poc.util.showError

class CameraFragment : Fragment(R.layout.fragment_camera) {

  private lateinit var imageProcessor: BarcodeScannerProcessor
  private var updateOverlaySourceInfo = true

  @ExperimentalGetImage
  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    val cameraPreview = view.findViewById<PreviewView>(R.id.camera_preview)
    val graphicOverlay = view.findViewById<GraphicOverlay>(R.id.graphic_overlay)

    val mainExecutor = ContextCompat.getMainExecutor(requireContext())

    imageProcessor = BarcodeScannerProcessor(requireContext())

    val cameraProviderFuture =
      ProcessCameraProvider.getInstance(requireContext().applicationContext)

    cameraProviderFuture.addListener(
        {
          val cameraProvider = cameraProviderFuture.get()
          val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

          val preview = Preview.Builder()
              .build()
              .also {
                it.setSurfaceProvider(cameraPreview.surfaceProvider)
              }

          val analysis = ImageAnalysis.Builder()
              .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
              .build()
              .also {
                it.setAnalyzer(mainExecutor) { imageProxy: ImageProxy ->
                  if (updateOverlaySourceInfo) {
                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                    if (rotationDegrees == 0 || rotationDegrees == 180) {
                      graphicOverlay.setImageSourceInfo(imageProxy.width, imageProxy.height)
                    } else {
                      graphicOverlay.setImageSourceInfo(imageProxy.height, imageProxy.width)
                    }
                    updateOverlaySourceInfo = false
                  }

                  try {
                    imageProcessor.processImageProxy(imageProxy, graphicOverlay)
                  } catch (e: MlKitException) {
                    requireActivity().showError(getString(string.setup_image_processing_failed))
                  }
                }
              }

          try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, analysis)
          } catch (e: Exception) {
            requireActivity().showError(getString(string.setup_camera_setup_failed))
          }
        },
        mainExecutor
    )
  }
}
