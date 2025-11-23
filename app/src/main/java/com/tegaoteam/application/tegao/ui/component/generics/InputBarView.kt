package com.tegaoteam.application.tegao.ui.component.generics

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.ViewInputBarBinding
import com.tegaoteam.application.tegao.domain.repo.AddonRepo

class InputBarView(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    addonRepo: AddonRepo? = null
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

    fun getInputValue() = _binding.unvInputFieldEdt.text.toString()
    fun getEditTextView() = _binding.unvInputFieldEdt
    fun getSwitchButton() = _binding.switchHandwritingModeIcl
    //endregion

    // Handwriting handling if enabled
    //todo: bind handwriting also when using this bar view
    val isHandwritingEnabled = addonRepo?.isHandwritingAvailable()?: false
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