package com.tegaoteam.application.tegao.ui.component.handwriting

import android.graphics.Bitmap
import android.view.View
import android.widget.EditText
import androidx.core.view.isGone
import androidx.databinding.ViewDataBinding
import com.tegaoteam.application.tegao.databinding.ViewWritingBoardFullBinding
import androidx.core.view.isVisible
import com.tegaoteam.application.tegao.ui.shared.preset.AnimationPreset
import timber.log.Timber

class WritingViewController(
    private val writingView: WritingView,
    private val writingBinding: ViewDataBinding,
    private val onRequestRecognition: (Bitmap?) -> Unit,
    private val editText: EditText? = null,
    onStrokeFinished: ((Bitmap?) -> Unit)? = null,
    private val onEnterKeyPressed: (() -> Unit)? = null,
    private val onSuggestionSelected: ((String) -> Unit)? = null
) {
    var isWritingEnabled: Boolean = false
        private set

    private lateinit var _suggestionsListAdapter: WritingViewCharacterSuggestionsListAdapter

    private lateinit var _inAnim: () -> Unit
    private lateinit var _outAnim: () -> Unit
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
                _inAnim = { AnimationPreset.inSlideFromBottom(animatingView) }
                _outAnim = { AnimationPreset.outSlideToBottom(animatingView) }
            }
            else -> { //todo: change to something better than instant cause I can't think straight at 11pm
                _inAnim = { AnimationPreset.inInstant(animatingView) }
                _outAnim = { AnimationPreset.outInstant(animatingView) }
            }
        }
    }

    private fun linkViewToBinding(mode: Int) {
        when (mode) {
            BINDING_FULL -> {
                bindingEditTextControlFunctions(writingBinding as ViewWritingBoardFullBinding, editText!!)
                bindingWriteHelperFunctions(writingBinding)
                bindingSuggestionFunction(writingBinding)
            }
            BINDING_WRITE -> {
                bindingWriteHelperFunctions(writingBinding as ViewWritingBoardFullBinding)
            }
            else -> return
        }
    }

    private fun bindingSuggestionFunction(binding: ViewDataBinding) {
        when (binding) {
            is ViewWritingBoardFullBinding -> {
                _suggestionsListAdapter = WritingViewCharacterSuggestionsListAdapter { selected ->
                    editText?.let {
                        if (it.selectionStart >= 0) {
                            if (it.selectionEnd != it.selectionStart)
                                it.editableText.replace(it.selectionStart, it.selectionEnd-1, selected)
                            else
                                it.editableText.insert(it.selectionStart, selected)
                        }
                    }
                }.apply { submitList( listOf<String>() ) }
                binding.recognizedCharsRcy.adapter = _suggestionsListAdapter
            }
        }
    }

    private fun bindingEditTextControlFunctions(binding: ViewWritingBoardFullBinding, editText: EditText) {
        // retain original onFocus function then inject new codes into it
        val originalOnFocusChangedListener = editText.onFocusChangeListener
        editText.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            // codes for show/hide the writing board
            if (hasFocus && isWritingEnabled) showWritingView()
            else showWritingView(false)
            originalOnFocusChangedListener?.onFocusChange(view, hasFocus)
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
        binding.clearStrokesBtn.setOnClickListener {
            writingView.clearStrokes()
            updateSuggestionsList(listOf())
        }
        binding.undoStrokeBtn.setOnClickListener {
            writingView.undoStroke()
            if (writingView.getStrokeCount() > 0) requestSuggestions() else updateSuggestionsList(listOf())
        }
    }

    private fun bindingWriteOutputFunction(onStrokeFinished: ((Bitmap?) -> Unit)) {
        writingView.onStrokeFinished = onStrokeFinished
    }

    // handle writing mode toggle and prevent edittext to call for softKeyboard when focus
    fun toggleWritingMode(value: Boolean? = null) {
        isWritingEnabled = value?: !isWritingEnabled
        editText?.apply{ showSoftInputOnFocus = !isWritingEnabled }
        showWritingView(isWritingEnabled)
    }

    fun showWritingView(really: Boolean = true) {
        animatingView.apply {
            if (!isWritingEnabled && isVisible) {
                _outAnim.invoke()
                return
            }
            editText?.let {
                val showing = (really && editText.isFocused)
                if (showing && isGone)
                    _inAnim.invoke()
                if (!showing && isVisible)
                    _outAnim.invoke()
                return
            }
            if (really)
                _inAnim.invoke()
            else
                _outAnim.invoke()
        }
    }

    /**
     * Request for suggestion list with current writing via defined onRequestRecognition lambda
     *
     * @param bitmap Can left blank, automatically pickup writing view's exportBitmap to use
     */
    fun requestSuggestions(bitmap: Bitmap? = null) {
        val inpBitmap = bitmap?: writingView.exportBitmap()
        onRequestRecognition.invoke(inpBitmap)
    }

    /**
     * Update the suggestion list manually (this is because of using Service as the recognition method)
     *
     * Just pass the List<String?>? and the suggestion list would update
     */
    fun updateSuggestionsList(suggestions: List<String?>?) {
        _suggestionsListAdapter.submitList(suggestions)
        Timber.i("Suggestion list updating: $suggestions")
    }

    fun isWritingViewEqual(v: View?) = writingView == v
    fun isWritingBindingEqual(b: ViewDataBinding?) = writingBinding == b
    fun isEditTextEqual(v: View?) = editText == v

    companion object {
        private const val BINDING_FULL = 0
        private const val BINDING_WRITE = 1
    }
}