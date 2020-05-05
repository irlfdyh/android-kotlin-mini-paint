package com.codelabs.customview.minipaint

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import kotlin.math.abs

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

    /**
     * variables for caching the x and y coordinates of the current touch event
     */
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    /**
     * Variables to cache the latest x and y values. After the user stops
     * moving and lifts their touch, these are the starting point for the
     * next path (the next segment of the line to draw).
     */
    private var currentX = 0f
    private var currentY = 0f

    private var touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private lateinit var frame: Rect

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

        // Calculate a rectangular frame around the picture.
        val insert = 40
        frame = Rect(insert, insert, w - insert, h - insert)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)

        // Draw a frame around the canvas.
        canvas.drawRect(frame, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }

    private fun touchStart() {
        // Reset the path.
        path.reset()
        // Move to the x-y coordinates of the touch event.
        path.moveTo(motionTouchEventX, motionTouchEventY)
        // Assign this property value.
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchMove() {
        val dx = abs(motionTouchEventX - currentX)
        val dy = abs(motionTouchEventY - currentY)
        if (dx >= touchTolerance || dy >= touchTolerance) {
            // QuadTo() adds a quadratic bezier from the last point,
            // approaching control point (x1, y1), and ending at (x2, y2).
            path.quadTo(
                currentX, currentY,
                (motionTouchEventX + currentX) / 2,
                (motionTouchEventY + currentY) / 2)
            currentX = motionTouchEventX
            currentY = motionTouchEventY
            // Draw the path in the extra bitmap to cache it.
            extraCanvas.drawPath(path, paint)
        }
        invalidate()
    }

    private fun touchUp() {
        // Reset the path so it doesn't get drawn again.
        path.reset()
    }

}