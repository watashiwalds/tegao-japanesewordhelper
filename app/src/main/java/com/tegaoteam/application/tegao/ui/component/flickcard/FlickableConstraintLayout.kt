package com.tegaoteam.application.tegao.ui.component.flickcard

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import com.tegaoteam.application.tegao.utils.dpToPixel
import com.tegaoteam.application.tegao.utils.toggleVisibility
import timber.log.Timber

class FlickableConstraintLayout(context: Context, attrs: AttributeSet?): ConstraintLayout(context, attrs) {
    //x and y is coordination inside the parent view

    var flickable = false
    var enableFlickAway = false
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
                if (collidingState != COLLIDING_NONE) {
                    onFinalCollide[collidingState]?.invoke()
                    Timber.i("Final colliding on $collidingState")
                }
                flickAway()
            }
        }
    }
    //endregion

    //region [Storing onCollide lambda and setters]
        //none, west, north, east, south
    private var onFinalCollide = mutableMapOf<Int, (() -> Unit)?>(COLLIDING_NONE to null, COLLIDING_WEST to null, COLLIDING_NORTH to null, COLLIDING_EAST to null, COLLIDING_SOUTH to null)
    private var onCollide = mutableMapOf<Int, (() -> Unit)?>(COLLIDING_NONE to null, COLLIDING_WEST to null, COLLIDING_NORTH to null, COLLIDING_EAST to null, COLLIDING_SOUTH to null)
    fun setOnCollideListener(vararg sides: Int, lambda: () -> Unit) {
        sides.forEach { onCollide[it] = lambda }
    }
    fun setOnFinalCollideListener(vararg sides: Int, lambda: () -> Unit) {
        sides.forEach { onFinalCollide[it] = lambda }
    }
    fun getOnCollideListener(side: Int) = onCollide.getOrDefault(side, null)
    fun getOnFinalCollideListener(side: Int) = onFinalCollide.getOrDefault(side, null)
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

    var collidingState = COLLIDING_NONE
        private set
    private fun updateCollidingState() {
        //pos - collide, neg - no collide
        val deltas = listOf(
            COLLIDING_WEST to (0 - x - dpToPixel(collideDpPadding)),
            COLLIDING_NORTH to (0 - y - 2*dpToPixel(collideDpPadding)),
            COLLIDING_EAST to (x + borderEast - dpToPixel(collideDpPadding)),
            COLLIDING_SOUTH to (y + borderSouth - 2*dpToPixel(collideDpPadding)))
        val nowColliding = run{
            val maxCollide = deltas.maxBy { it.second }
            if (maxCollide.second - dpToPixel(collideDpPadding) <= 0) COLLIDING_NONE else maxCollide.first
        }
        if (nowColliding != collidingState) {
            collidingState = nowColliding
            Timber.i("Colliding on $collidingState")
            onCollide[collidingState]?.invoke()
        }
    }
    //endregion

    //region small function for niche
    private fun flickAway() {
        if (!enableFlickAway || collidingState == COLLIDING_NONE) {
            animate()
                .translationX(0f)
                .translationY(0f)
                .setInterpolator(DecelerateInterpolator())
                .setDuration(200)
                .start()
        } else when (collidingState) {
            COLLIDING_WEST -> animate()
                .translationX(-width.toFloat())
                .translationY(0f)
                .setInterpolator(DecelerateInterpolator())
                .setDuration(200)
                .withEndAction {
                    toggleVisibility(false)
                    animate().translationX(0f).translationY(0f).start()
                }
                .start()
            COLLIDING_NORTH -> animate()
                .translationX(0f)
                .translationY(-height.toFloat())
                .setInterpolator(DecelerateInterpolator())
                .setDuration(200)
                .withEndAction {
                    toggleVisibility(false)
                    animate().translationX(0f).translationY(0f).start()
                }
                .start()
            COLLIDING_EAST -> animate()
                .translationX(width + borderEast)
                .translationY(0f)
                .setInterpolator(DecelerateInterpolator())
                .setDuration(200)
                .withEndAction {
                    toggleVisibility(false)
                    animate().translationX(0f).translationY(0f).start()
                }
                .start()
            COLLIDING_SOUTH -> animate()
                .translationX(0f)
                .translationY(height + borderSouth)
                .setInterpolator(DecelerateInterpolator())
                .setDuration(200)
                .withEndAction {
                    toggleVisibility(false)
                    animate().translationX(0f).translationY(0f).start()
                }
                .start()
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