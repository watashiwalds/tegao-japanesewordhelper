package com.tegaoteam.application.tegao.ui.component.generics

import android.content.Context
import android.view.LayoutInflater
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.ViewInputBarBinding
import com.tegaoteam.application.tegao.ui.shared.FetchedConfigs

class InputBarView(
    context: Context,
    lifecycleOwner: LifecycleOwner
) {
    private val _binding = ViewInputBarBinding.inflate(LayoutInflater.from(context))
    val view = _binding.root

    //region Normal, basic input method
    val enableClearInput = MutableLiveData<Boolean>().apply { value = false }

    init {
        _binding.unvInputFieldEdt.doOnTextChanged { text, start, end, count ->
            enableClearInput.value = text?.isNotEmpty()?: false
        }
        _binding.inputClearBtn.setOnClickListener {
            _binding.unvInputFieldEdt.apply {
                text?.clear()
            }
        }
    }

    fun clearInput() { _binding.unvInputFieldEdt.editableText.clear() }
    fun getInputValue() = _binding.unvInputFieldEdt.text.toString()
    fun getEditTextView() = _binding.unvInputFieldEdt
    fun getSwitchButton() = _binding.switchHandwritingModeIcl
    //endregion

    // Handwriting handling if enabled
    //todo: bind handwriting also when using this bar view
    val isHandwritingEnabled = FetchedConfigs.isHandwritingEnabled.value
    init {
        if (isHandwritingEnabled) {
            _binding.switchHandwritingModeIcl.switchInfo = SwitchButtonInfo(
                iconResId = R.drawable.ftc_round_handwriting_128,
                switchState = MutableLiveData<Boolean>().apply { value = false }
            )
        }
    }

    // Final init for binding this object and lifecycleOwner to run execute()
    init {
        _binding.inputBar = this
        _binding.lifecycleOwner = lifecycleOwner
        _binding.executePendingBindings()
    }
}