package com.codelabs.customview.minipaint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import androidx.core.content.res.ResourcesCompat

/**
 * to setup the stroke width.
 */
private const val STROKE_WIDTH = 12f

class MyCanvasView(context: Context) : View(context) {

    /**
     * these variables is used for caching what has been drawn before.
     */
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    /**
     * to setup background color of the canvas.
     */
    private val backgroundColor =
        ResourcesCompat.getColor(resources, R.color.colorBackground, null)

    /**
     * To holding the color to draw, and initialize it with the colorPaint.
     */
    private val drawColor =
        ResourcesCompat.getColor(resources, R.color.colorPaint, null)

    /**
     * setup the paint with which to draw.
     */
    private val paint = Paint().apply {
        color = drawColor

        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true

        // Dithering affects how colors with higher-precision than the device
        // are down-sampled.
        isDither = true

        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }

    private var path = Path()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // recycle extraBitmap before creating the next one by add this code, to handle
        // a memory leak.
        if (::extraBitmap.isInitialized) extraBitmap.recycle()

        // Instance of bitmap with the new width and height, which are the screen size, and
        // assign it to extraBitmap.
        // The third arguments is the bitmap color configuration.
        extraBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        // Specify the background color in which to fill extraCanvas.
        extraCanvas.drawColor(backgroundColor)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
    }


}