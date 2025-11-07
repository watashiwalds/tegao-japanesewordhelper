package com.tegaoteam.application.tegao.ui.component.handwriting

import android.graphics.Bitmap
import android.widget.EditText
import androidx.databinding.ViewDataBinding
import com.tegaoteam.application.tegao.databinding.ViewWritingBoardFullBinding

class WritingViewController(
    private val writingView: WritingView,
    onStrokeFinished: ((Bitmap?) -> Unit)? = null,
    private val binding: ViewDataBinding? = null,
    private val editText: EditText? = null
) {
    init {
        onStrokeFinished?.let { bindingWriteOutputFunction(it) }
        binding?.let { bindingNotNull ->
            if (binding is ViewWritingBoardFullBinding) {
                if (editText != null)
                    linkViewToBinding(BINDING_FULL)
                else
                    linkViewToBinding(BINDING_WRITE)
            }
            binding.executePendingBindings()
        }
    }

    private fun linkViewToBinding(mode: Int) {
        when (mode) {
            BINDING_FULL -> {
                bindingEditTextControlFunctions(binding as ViewWritingBoardFullBinding, editText!!)
                bindingWriteHelperFunctions(binding)
            }
            BINDING_WRITE -> {
                bindingWriteHelperFunctions(binding as ViewWritingBoardFullBinding)
            }
            else -> return
        }
    }

    private fun bindingEditTextControlFunctions(binding: ViewWritingBoardFullBinding, editText: EditText) {
        //TODO: Do binding when EditText is presented
        //include: enterBtn, backspaceBtn, next/prevBtn
    }

    private fun bindingWriteHelperFunctions(binding: ViewWritingBoardFullBinding) {
        binding.clearStrokesBtn.setOnClickListener { writingView.clearStrokes() }
        binding.undoStrokeBtn.setOnClickListener { writingView.undoStroke() }
    }

    private fun bindingWriteOutputFunction(onStrokeFinished: ((Bitmap?) -> Unit)) {
        writingView.onStrokeFinished = onStrokeFinished
    }

    companion object {
        private const val BINDING_FULL = 0
        private const val BINDING_WRITE = 1
    }
}