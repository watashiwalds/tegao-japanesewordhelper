package com.tegaoteam.application.tegao.ui.component.handwriting

import android.graphics.Bitmap
import android.view.View
import android.widget.EditText
import androidx.databinding.ViewDataBinding
import com.tegaoteam.application.tegao.databinding.ViewWritingBoardFullBinding
import com.tegaoteam.application.tegao.utils.toggleVisibility
import androidx.core.view.isVisible

class WritingViewController(
    private val writingView: WritingView,
    private val writingBinding: ViewDataBinding,
    private val editText: EditText? = null,
    onStrokeFinished: ((Bitmap?) -> Unit)? = null,
    private val onEnterKeyPressed: (() -> Unit)? = null
) {
    var isWritingEnabled: Boolean = false
        private set

    init {
        onStrokeFinished?.let { bindingWriteOutputFunction(it) }
        writingBinding.let {
            if (writingBinding is ViewWritingBoardFullBinding) {
                if (editText != null)
                    linkViewToBinding(BINDING_FULL)
                else
                    linkViewToBinding(BINDING_WRITE)
            }
            writingBinding.root.visibility = View.GONE
            writingBinding.executePendingBindings()
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
        editText.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            //TODO: Write show/hide function of writing keyboard here (remember: check for the enable state of writing mode)
            if (hasFocus && isWritingEnabled) showWritingView()
            else showWritingView(false)
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
        if (!isWritingEnabled) {
            if (writingBinding.root.isVisible) {
                writingBinding.root.visibility = View.GONE
            }
            return
        }
        editText?.let {
            writingBinding.root.toggleVisibility(really && editText.isFocused)
            return
        }
        writingBinding.root.toggleVisibility(really)
    }

    fun isWritingViewEqual(v: View?) = writingView == v
    fun isWritingBindingEqual(b: ViewDataBinding?) = writingBinding == b
    fun isEditTextEqual(v: View?) = editText == v

    companion object {
        private const val BINDING_FULL = 0
        private const val BINDING_WRITE = 1
    }
}