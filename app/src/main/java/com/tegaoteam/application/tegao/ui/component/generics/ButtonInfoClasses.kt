package com.tegaoteam.application.tegao.ui.component.generics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

open class ButtonInfo(
    val labelResId: Int = 0,
    val iconResId: Int = 0,
    protected val onClickListener: (() -> Unit)? = null
) {
    open fun push() {
        onClickListener?.invoke()
    }
}

class SwitchButtonInfo(
    labelResId: Int = 0,
    iconResId: Int = 0,
    private val onStateChangedListener: ((state: Boolean) -> Unit)? = null,
    private val switchState: MutableLiveData<Boolean>
): ButtonInfo(labelResId, iconResId) {
    val stateLiveData: LiveData<Boolean> = switchState
    override fun push() {
        if (switchState.value == null)
            switchState.value = true
        else
            switchState.value = !(switchState.value!!)
        onStateChangedListener?.invoke(stateLiveData.value?: false)
    }
}