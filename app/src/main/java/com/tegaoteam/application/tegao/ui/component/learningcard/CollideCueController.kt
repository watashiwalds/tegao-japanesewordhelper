package com.tegaoteam.application.tegao.ui.component.learningcard

import android.view.ViewPropertyAnimator
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.IncludeFlickcardCollideEffectBinding
import com.tegaoteam.application.tegao.utils.dpToPixel
import com.tegaoteam.application.tegao.utils.setBackgroundTintByResId

class CollideCueController(private val binding: IncludeFlickcardCollideEffectBinding) {
    private var borderCueSize = dpToPixel(16f)
    init {
        clearCue()
    }

    fun applyTint(mode: Int) {
        when (mode) {
            MODE_SRS -> {
                binding.apply {
                    westCue.setBackgroundTintByResId(R.color.srs_good)
                    northCue.setBackgroundTintByResId(R.color.srs_easy)
                    eastCue.setBackgroundTintByResId(R.color.srs_hard)
                    southCue.setBackgroundTintByResId(R.color.srs_forget)
                }
            }
            else -> {
                binding.apply { listOf(westCue, northCue, eastCue, southCue).forEach { it.setBackgroundTintByResId(0) } }
            }
        }
    }

    private var currentCue = COLLIDE_NONE
    private var currentAnim: ViewPropertyAnimator? = null
    private fun hideCue(side: Int) {
        when (side) {
            COLLIDE_WEST -> binding.westCue.translationX = (-borderCueSize)
            COLLIDE_NORTH -> binding.northCue.translationY = (-borderCueSize)
            COLLIDE_EAST -> binding.eastCue.translationX = (borderCueSize)
            COLLIDE_SOUTH -> binding.southCue.translationY = (borderCueSize)
        }
    }
    fun showCue(side: Int) {
        if (currentCue == side) return
        currentAnim?.cancel()
        hideCue(currentCue)
        currentCue = side
        currentAnim = when (currentCue) {
            COLLIDE_WEST -> binding.westCue.animate().translationX(0f).setDuration(100)
            COLLIDE_NORTH -> binding.northCue.animate().translationY(0f).setDuration(100)
            COLLIDE_EAST -> binding.eastCue.animate().translationX(0f).setDuration(100)
            COLLIDE_SOUTH -> binding.southCue.animate().translationY(0f).setDuration(100)
            else -> null
        }
        currentAnim?.start()
    }

    fun clearCue() {
        binding.apply {
            westCue.translationX = -borderCueSize
            northCue.translationY = -borderCueSize
            eastCue.translationX = borderCueSize
            southCue.translationY = borderCueSize
            textCue.alpha = 0f
        }
    }

    companion object {
        const val MODE_NEUTRAL = 0
        const val MODE_SRS = 1

        const val COLLIDE_NONE = FlickableConstraintLayout.COLLIDING_NONE
        const val COLLIDE_WEST = FlickableConstraintLayout.COLLIDING_WEST
        const val COLLIDE_NORTH = FlickableConstraintLayout.COLLIDING_NORTH
        const val COLLIDE_EAST = FlickableConstraintLayout.COLLIDING_EAST
        const val COLLIDE_SOUTH = FlickableConstraintLayout.COLLIDING_SOUTH
    }
}