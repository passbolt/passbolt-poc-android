package com.passbolt.poc.milestones.setup.camera.processor

import android.app.ActivityManager
import android.content.Context
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.mlkit.vision.common.InputImage
import com.passbolt.poc.util.GraphicOverlay
import com.passbolt.poc.util.ScopedExecutor

// modified version of https://github.com/googlesamples/mlkit/tree/master/android/vision-quickstart
abstract class VisionProcessorBase<T>(context: Context) {

  private val activityManager =
    context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

  private val executor = ScopedExecutor(TaskExecutors.MAIN_THREAD)

  private var isShutdown = false

  @ExperimentalGetImage
  fun processImageProxy(
    imageProxy: ImageProxy,
    graphicOverlay: GraphicOverlay
  ) {
    if (isShutdown) {
      return
    }

    requestDetectInImage(
        InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees),
        graphicOverlay
    ).addOnCompleteListener { imageProxy.close() }
  }

  private fun requestDetectInImage(
    image: InputImage,
    graphicOverlay: GraphicOverlay
  ): Task<T> {
    return detectInImage(image).addOnSuccessListener(executor) { results: T ->
      graphicOverlay.clear()
      this@VisionProcessorBase.onSuccess(results, graphicOverlay)
      graphicOverlay.postInvalidate()
    }
        .addOnFailureListener(executor) { e: Exception ->
          graphicOverlay.clear()
          graphicOverlay.postInvalidate()
          this@VisionProcessorBase.onFailure(e)
        }
  }

  open fun stop() {
    executor.shutdown()
    isShutdown = true
  }

  protected abstract fun detectInImage(image: InputImage): Task<T>
  protected abstract fun onSuccess(
    results: T,
    graphicOverlay: GraphicOverlay
  )

  protected abstract fun onFailure(e: Exception)
}
