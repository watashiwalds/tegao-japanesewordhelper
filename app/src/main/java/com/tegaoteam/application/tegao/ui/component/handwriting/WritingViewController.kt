package com.tegaoteam.application.tegao.ui.component.handwriting

import android.graphics.Bitmap
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.core.view.isGone
import androidx.databinding.ViewDataBinding
import com.tegaoteam.application.tegao.databinding.ViewWritingBoardFullBinding
import androidx.core.view.isVisible
import com.tegaoteam.application.tegao.R

class WritingViewController(
    private val writingView: WritingView,
    private val writingBinding: ViewDataBinding,
    private val editText: EditText? = null,
    onStrokeFinished: ((Bitmap?) -> Unit)? = null,
    private val onEnterKeyPressed: (() -> Unit)? = null
) {
    var isWritingEnabled: Boolean = false
        private set

    private lateinit var _inAnim: Animation
    private lateinit var _outAnim: Animation
    private val animatingView = writingBinding.root

    init {
        onStrokeFinished?.let { bindingWriteOutputFunction(it) }
        writingBinding.let {
            if (writingBinding is ViewWritingBoardFullBinding) {
                if (editText != null) {
                    setAnimation(BINDING_FULL)
                    linkViewToBinding(BINDING_FULL)
                }
                else {
                    setAnimation(BINDING_WRITE)
                    linkViewToBinding(BINDING_WRITE)
                }
            }
            writingBinding.root.visibility = View.GONE
            writingBinding.executePendingBindings()
        }
    }

    private fun setAnimation(mode: Int) {
        when (mode) {
            BINDING_FULL -> {
                _inAnim = AnimationUtils.loadAnimation(animatingView.context, R.anim.slidein_bottomup_easein).apply {
                    setAnimationListener(object: Animation.AnimationListener {
                        override fun onAnimationEnd(p0: Animation?) {}
                        override fun onAnimationRepeat(p0: Animation?) {}
                        override fun onAnimationStart(p0: Animation?) { animatingView.visibility = View.VISIBLE }
                    })
                }
                _outAnim = AnimationUtils.loadAnimation(animatingView.context, R.anim.slideout_topdown_easeout).apply {
                    setAnimationListener(object: Animation.AnimationListener {
                        override fun onAnimationEnd(p0: Animation?) { animatingView.visibility = View.GONE }
                        override fun onAnimationRepeat(p0: Animation?) {}
                        override fun onAnimationStart(p0: Animation?) {}
                    })
                }
            }
            else -> { //todo: change to something better than instant cause I can't think straight at 11pm
                _inAnim = AnimationUtils.loadAnimation(animatingView.context, R.anim.instant).apply {
                    setAnimationListener(object: Animation.AnimationListener {
                        override fun onAnimationEnd(p0: Animation?) {}
                        override fun onAnimationRepeat(p0: Animation?) {}
                        override fun onAnimationStart(p0: Animation?) { animatingView.visibility = View.VISIBLE }
                    })
                }
                _outAnim = AnimationUtils.loadAnimation(animatingView.context, R.anim.instant).apply {
                    setAnimationListener(object: Animation.AnimationListener {
                        override fun onAnimationEnd(p0: Animation?) { animatingView.visibility = View.GONE }
                        override fun onAnimationRepeat(p0: Animation?) {}
                        override fun onAnimationStart(p0: Animation?) {}
                    })
                }
            }
        }
    }

    private fun linkViewToBinding(mode: Int) {
        when (mode) {
            BINDING_FULL -> {
                bindingEditTextControlFunctions(writingBinding as ViewWritingBoardFullBinding, editText!!)
                bindingWriteHelperFunctions(writingBinding)
            }
            BINDING_WRITE -> {
                bindingWriteHelperFunctions(writingBinding as ViewWritingBoardFullBinding)
            }
            else -> return
        }
    }

    private fun bindingEditTextControlFunctions(binding: ViewWritingBoardFullBinding, editText: EditText) {
        // retain original onFocus function then inject new codes into it
        val originalOnFocusChangedListener = editText.onFocusChangeListener
        editText.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            // codes for show/hide the writing board
            if (hasFocus && isWritingEnabled) showWritingView()
            else showWritingView(false)
            originalOnFocusChangedListener.onFocusChange(view, hasFocus)
        }

        binding.cursorToLeftBtn.setOnClickListener {
            editText.setSelection(
                (editText.selectionStart - 1).let {
                    if (it < 0) 0 else it
                }
            )
        }
        binding.cursorToRightBtn.setOnClickListener {
            editText.setSelection(
                (editText.selectionEnd + 1).let {
                    if (it > editText.text.length) editText.text.length else it
                }
            )
        }
        binding.backspaceCharBtn.setOnClickListener {
            editText.apply {
                if (selectionEnd - selectionStart == 0) text.delete(
                    if (selectionStart == 0) 0 else selectionStart - 1,
                    selectionStart
                ) else text.delete(
                    selectionStart,
                    selectionEnd
                )
            }
        }
        binding.enterBtn.setOnClickListener {
            //TODO: Think about what the enter button should do...
            onEnterKeyPressed?.invoke()
            editText.clearFocus()
        }
    }

    private fun bindingWriteHelperFunctions(binding: ViewWritingBoardFullBinding) {
        binding.clearStrokesBtn.setOnClickListener { writingView.clearStrokes() }
        binding.undoStrokeBtn.setOnClickListener { writingView.undoStroke() }
    }

    private fun bindingWriteOutputFunction(onStrokeFinished: ((Bitmap?) -> Unit)) {
        writingView.onStrokeFinished = onStrokeFinished
    }

    // handle writing mode toggle and prevent edittext to call for softKeyboard when focus
    fun toggleWritingMode(value: Boolean? = null) {
        //todo: smoother writing keyboard hiding/showing
        isWritingEnabled = value?: !isWritingEnabled
        editText?.apply{ showSoftInputOnFocus = !isWritingEnabled }
        showWritingView(isWritingEnabled)
    }

    fun showWritingView(really: Boolean = true) {
        animatingView.apply {
            if (!isWritingEnabled && isVisible) {
                startAnimation(_outAnim)
                return
            }
            editText?.let {
                val showing = (really && editText.isFocused)
                if (showing && isGone)
                    startAnimation(_inAnim)
                if (!showing && isVisible)
                    startAnimation(_outAnim)
                return
            }
            if (really)
                startAnimation(_inAnim)
            else
                startAnimation(_outAnim)
        }
    }

    fun isWritingViewEqual(v: View?) = writingView == v
    fun isWritingBindingEqual(b: ViewDataBinding?) = writingBinding == b
    fun isEditTextEqual(v: View?) = editText == v

    companion object {
        private const val BINDING_FULL = 0
        private const val BINDING_WRITE = 1
    }
}