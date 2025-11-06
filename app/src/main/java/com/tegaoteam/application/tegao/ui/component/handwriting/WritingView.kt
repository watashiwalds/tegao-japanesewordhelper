package com.tegaoteam.application.tegao.ui.component.handwriting

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.tegaoteam.application.tegao.R
import androidx.core.graphics.createBitmap
import timber.log.Timber
import kotlin.math.min

/**
 * Handwriting region to handle user's handwriting gesture.
 * Many thanks to ChatGPT for assisting me with this view construction
 *
 * Is a square that allow user to use touch gesture to simulate pen writing (hence, handwriting) in a specified region (writeRegion)
 *
 * Able to produce Bitmap value of the handwriting **after** each stroke, ready to be sent to a character recognition of choice for text characters return
 */
class WritingView(context: Context, attrs: AttributeSet?): View(context, attrs) {
    // overhead prevention for attributes (default background, square display,...)
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val isBackgroundNotSet = (background as? ColorDrawable)?.color == Color.TRANSPARENT || background == null
        if (isBackgroundNotSet) setBackgroundResource(R.drawable.view_writing_pad)
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val squareSide = min(width, height)
        val squareSideMeasureSpec = MeasureSpec.makeMeasureSpec(squareSide, MeasureSpec.EXACTLY)
        super.onMeasure(squareSideMeasureSpec, squareSideMeasureSpec)
        setMeasuredDimension(squareSide, squareSide)
    }

    //define the "pen head" of the handwriting draw lines
    private val penPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.text_normal)
        style = Paint.Style.STROKE
        strokeWidth = 20f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isAntiAlias = true
    }
    // represent the current "stroke" the user is writing (from action_down to action_up and all movement between)
    private val currentStroke = Path()
    // all stroke that have been written store in here
    private val strokeStack = mutableListOf<Pair<Path, Paint>>()
    // define the allowed region to write
    private var writeRegion: RectF? = null
    // Canvas for drawing the stroke onto the display
    private lateinit var bufferCanvas: Canvas
    // Bitmap for storing the whole handwriting
    private lateinit var bitmapBuffer: Bitmap

    // qol, smooth writing w/o sudden teleport line drawing
    private var isInsideWriteRegionWhenWrite = false

    var onStrokeFinished: ((Bitmap) -> Unit)? = null

    // called when the View finalize it creation and have a value for it dimension on the screen
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmapBuffer = createBitmap(w, h)
        bufferCanvas = Canvas(bitmapBuffer) // make Canvas with size of bitmapBuffer
        setWriteRegion(RectF(0f, 0f, w.toFloat(), h.toFloat())) // set the write region
    }

    fun setWriteRegion(region: RectF) {
        writeRegion = region
        invalidate() // cause the writeRegion (potentially) changed it value, the View need to be re-draw
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        if (writeRegion?.contains(x, y) == false) isInsideWriteRegionWhenWrite = false

        when (event.action) {
            // the hand start it stroke by touching down the writeRegion
            MotionEvent.ACTION_DOWN -> {
                isInsideWriteRegionWhenWrite = true
                currentStroke.moveTo(x, y) // stroke start, teleport to the start point to tract the stroke
            }

            // hand moving while still touching the screen -> user is writing
            MotionEvent.ACTION_MOVE -> {
                if (isInsideWriteRegionWhenWrite)
                    currentStroke.lineTo(x, y) // drag the cursor to the now location of the hand (higher fps, smoother the line)
                else {
                    isInsideWriteRegionWhenWrite = true
                    currentStroke.moveTo(x, y) // teleport the cursor (without drawing) when going outside the writeRegion
                }
                invalidate() // redraw the View to show this new trail of currentStroke
            }

            // hand is leaving the screen, suggest a finish in this stroke
            MotionEvent.ACTION_UP -> {
                strokeStack.add(Pair(currentStroke, penPaint)) // add stroke to list
                bufferCanvas.drawPath(currentStroke, penPaint) // long-term draw the stroke onto Canvas
                currentStroke.reset() // clear out value for the next stroke (if have)
                onStrokeFinished?.invoke(bitmapBuffer) // send the current bitmap to listener
                // accessibility things. might look upon if have time
                performClick()
            }
        }
        return true
    }

    // get a copy of the current bitmap (with all the strokes draw on it)
    fun exportBitmap() = bitmapBuffer.copy(Bitmap.Config.ARGB_8888, false)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(bitmapBuffer, 0f, 0f, penPaint) // draw bitmapBuffer with 0 offset in both left and top with penPaint style
        canvas.drawPath(currentStroke, penPaint)
    }

    // undo stroke remove the latest stroke off strokeStack
    fun undoStroke() {
        strokeStack.removeLastOrNull()
        redrawBuffer()
        invalidate()
    }

    // clear strokes make all strokes cleared, return to a blank canvas
    fun clearStrokes() {
        strokeStack.clear()
        redrawBuffer()
        invalidate()
    }

    //
    private fun redrawBuffer() {
        bufferCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR) // clear the canvas of all long-term stroke
        strokeStack.forEach { (stroke, paint) -> bufferCanvas.drawPath(stroke, paint) }
    }

    // TODO: onTouchEvent scream about override performClick for accessibility. No idea of doing this
    override fun performClick(): Boolean {
        super.performClick()
        Timber.i("Stroke count: ${strokeStack.size}")
        return true
    }
}