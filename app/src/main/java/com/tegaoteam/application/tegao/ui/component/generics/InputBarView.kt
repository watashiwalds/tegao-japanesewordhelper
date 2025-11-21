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
    private val _context: Context,
    private val _lifecycleOwner: LifecycleOwner,
    private val _addonRepo: AddonRepo? = null,
    private val _writingBoardLocatedViewGroup: ViewGroup? = null
) {
    private val _binding = ViewInputBarBinding.inflate(LayoutInflater.from(_context))
    val view = _binding.root

    //region Normal, basic input method
    val enableClearSearchString = MutableLiveData<Boolean>().apply { value = false }

    init {
        _binding.inputFieldEdt.doOnTextChanged { text, start, end, count ->
            enableClearSearchString.value = text?.isNotEmpty()?: false
        }
        _binding.inputClearBtn.setOnClickListener {
            _binding.inputFieldEdt.apply {
                clearFocus()
                text?.clear()
            }
        }
        _binding.lifecycleOwner = _lifecycleOwner
        _binding.executePendingBindings()
    }

    fun getInputValue() = _binding.inputFieldEdt.text.toString()
    //endregion

    // Handwriting handling if enabled
    //todo: bind handwriting also when using this bar view
    val isHandwritingEnabled = _addonRepo?.isHandwritingAvailable()?: false
    init {
        if (isHandwritingEnabled) {
            _binding.switchHandwritingModeIcl.switchInfo = SwitchButtonInfo(
                iconResId = R.drawable.ftc_round_handwriting_128,
                switchState = MutableLiveData<Boolean>().apply { value = false }
            )
        }
    }
}