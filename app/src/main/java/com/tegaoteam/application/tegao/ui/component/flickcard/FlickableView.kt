package com.tegaoteam.application.tegao.ui.component.flickcard

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import com.tegaoteam.application.tegao.utils.dpToPixel
import timber.log.Timber

class FlickableView(context: Context, attrs: AttributeSet?): ConstraintLayout(context, attrs) {
    //x and y is coordination inside the parent view

    var flickable = false
    var collideDpPadding = 0f


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        setBorderValues()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (flickable) flickableTouchEvent(event)
        return true
    }

    //region [Dragging animation control]
    private var thumbStartX: Float = 0f
    private var thumbStartY: Float = 0f
    private fun flickableTouchEvent(event: MotionEvent) {
        val eventX = event.rawX
        val eventY = event.rawY
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                thumbStartX = eventX
                thumbStartY = eventY
            }
            MotionEvent.ACTION_MOVE -> {
                translationX = eventX - thumbStartX
                translationY = eventY - thumbStartY
                updateCollidingState()
//                Timber.i("dragCoordinate [$x; $y]")
            }
            MotionEvent.ACTION_UP -> {
                if (collidingState != COLLIDING_NONE) return //todo: animating out with correct direction and GONE
                animate()
                    .translationX(0f)
                    .translationY(0f)
                    .setInterpolator(DecelerateInterpolator())
                    .setDuration(200)
                    .start()
            }
        }
    }
    //endregion

    //region [Detect border's collide]
    private var borderEast: Float = 0f
    private var borderSouth: Float = 0f
    private fun setBorderValues() {
        val parent = (parent as View)
        borderEast = parent.width.toFloat()
        borderSouth = parent.height.toFloat()
        Timber.i("Border's set! [0; 0] [$borderEast; $borderSouth]")
        Timber.i("View's collide value calculate as: [0; 0] [x + $width; y + $height]")

        // pre-calculate view's size - parent's size
        borderEast = width - borderEast
        borderSouth = height - borderSouth
    }

    var whenCollideWest = { Timber.i("Left collide!") }
    var whenCollideNorth = { Timber.i("Top collide!") }
    var whenCollideEast = { Timber.i("Right collide!") }
    var whenCollideSouth = { Timber.i("Bottom collide!") }
    var collidingState = COLLIDING_NONE
        private set
    private fun updateCollidingState() {
        //pos - collide, neg - no collide
        //[west, north, east, south]
        val deltas = listOf(0 - x, 0 - y, x + borderEast, y + borderSouth)
        val nowColliding = run{
            val maxIndex = deltas.withIndex().maxBy { it.value }.index
            if (deltas[maxIndex] - dpToPixel(collideDpPadding) <= 0) -1 else maxIndex
        }
        if (nowColliding != collidingState) {
            collidingState = nowColliding
            when (collidingState) {
                COLLIDING_WEST -> whenCollideWest.invoke()
                COLLIDING_NORTH -> whenCollideNorth.invoke()
                COLLIDING_EAST -> whenCollideEast.invoke()
                COLLIDING_SOUTH -> whenCollideSouth.invoke()
                COLLIDING_NONE -> Timber.i("No collide")
            }
        }
    }
    //endregion

    companion object {
        const val COLLIDING_NONE = -1
        const val COLLIDING_WEST = 0
        const val COLLIDING_NORTH = 1
        const val COLLIDING_EAST = 2
        const val COLLIDING_SOUTH = 3
    }
}