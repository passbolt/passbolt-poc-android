package com.passbolt.poc.milestones.setup.camera.processor

import android.content.Context
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.passbolt.poc.util.GraphicOverlay

// modified version of https://github.com/googlesamples/mlkit/tree/master/android/vision-quickstart
class BarcodeScannerProcessor(
  private val context: Context
) : VisionProcessorBase<List<Barcode>>(context) {

  private val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(
      BarcodeScannerOptions.Builder()
          .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
          .build()
  )

  override fun stop() {
    super.stop()
    barcodeScanner.close()
  }

  override fun detectInImage(image: InputImage): Task<List<Barcode>> {
    return barcodeScanner.process(image)
  }

  override fun onSuccess(
    results: List<Barcode>,
    graphicOverlay: GraphicOverlay
  ) {
    if (results.isEmpty()) {
      return
    }

    results.forEach { barcode ->
      val graphic = BarcodeGraphic(graphicOverlay, barcode)
      graphicOverlay.add(graphic)
    }
  }

  override fun onFailure(e: Exception) {
    Toast.makeText(
        context,
        "Failed to process.\nError: " +
            e.localizedMessage +
            "\nCause: " +
            e.cause,
        Toast.LENGTH_LONG
    )
        .show()
  }
}
