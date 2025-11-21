package com.tegaoteam.application.tegao.utils.preset

import android.view.View
import android.view.animation.DecelerateInterpolator

object AnimationPreset {
    private fun cancelCurrentAnimation(v: View) {
        v.animate().withEndAction(null)
        v.animate().cancel()
    }

    fun inSlideFromBottom(v: View) {
        cancelCurrentAnimation(v)
        v.apply {
            alpha = 0.3f
            translationY = height.toFloat()
            visibility = View.VISIBLE
        }
        v.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(200)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }
    fun outSlideToBottom(v: View) {
        cancelCurrentAnimation(v)
        v.apply {
            alpha = 1f
            translationY = 0f
            visibility = View.VISIBLE
        }
        v.animate()
            .translationY(v.height.toFloat())
            .alpha(0.3f)
            .setDuration(200)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction { v.visibility = View.GONE }
            .start()
    }

    fun inInstant(v: View) {
        v.apply {
            alpha = 1f
            translationY = 0f
            visibility = View.VISIBLE
        }
    }
    fun outInstant(v: View) {
        v.apply {
            alpha = 1f
            translationY = 0f
            visibility = View.GONE
        }
    }
}