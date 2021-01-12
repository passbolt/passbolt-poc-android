package com.passbolt.poc.milestones.setup.camera.processor

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.google.mlkit.vision.barcode.Barcode
import com.passbolt.poc.util.GraphicOverlay
import com.passbolt.poc.util.GraphicOverlay.Graphic

// modified version of https://github.com/googlesamples/mlkit/tree/master/android/vision-quickstart
class BarcodeGraphic constructor(
  overlay: GraphicOverlay,
  private val barcode: Barcode,
  private val message: String? = null
) : Graphic(overlay) {

  private val rectPaint: Paint = Paint().apply {
    color = Color.WHITE
    style = Paint.Style.STROKE
    strokeWidth = STROKE_WIDTH
    color = Color.GREEN
  }
  private val barcodePaint: Paint = Paint().apply {
    color = Color.BLACK
    textSize = TEXT_SIZE
  }
  private val labelPaint: Paint = Paint().apply {
    color = Color.WHITE
    style = Paint.Style.FILL
  }

  private val barcodeRect = RectF()

  override fun draw(canvas: Canvas) {
    val boundingBox = barcode.boundingBox ?: return
    barcodeRect.set(boundingBox)

    val x0 = translateX(barcodeRect.left)
    val x1 = translateX(barcodeRect.right)
    barcodeRect.left = x0.coerceAtMost(x1)
    barcodeRect.right = x0.coerceAtLeast(x1)
    barcodeRect.top = translateY(barcodeRect.top)
    barcodeRect.bottom = translateY(barcodeRect.bottom)
    canvas.drawRect(barcodeRect, rectPaint)

    if (message != null) {
      val lineHeight = TEXT_SIZE + 2 * STROKE_WIDTH
      val textWidth = barcodePaint.measureText(barcode.rawValue)

      canvas.drawRect(
          barcodeRect.left - STROKE_WIDTH,
          barcodeRect.top - lineHeight,
          barcodeRect.left + textWidth + 2 * STROKE_WIDTH,
          barcodeRect.top,
          labelPaint
      )

      canvas.drawText(
          message,
          barcodeRect.left,
          barcodeRect.top - STROKE_WIDTH,
          barcodePaint
      )
    }
  }

  companion object {
    private const val TEXT_SIZE = 54.0f
    private const val STROKE_WIDTH = 4.0f
  }
}
