package com.tegaoteam.application.tegao.ui.component.themedchip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Holder class for themed_chip_item.xml View
 *
 * Require manual selecting control logic to correctly display (hold on to MutableLiveData<Boolean> or ```selected``` references)
 */
class ThemedChipItem(
    val id: String,
    val label: String,
    private val _isSelected: MutableLiveData<Boolean>
) {
    init {
        _isSelected.value = false
    }

    private var listener: () -> Unit = {}
    fun setOnClickListener(listener: () -> Unit) { this.listener = listener }
    fun onClick() { listener.invoke() }

    val isSelected: LiveData<Boolean> = _isSelected
    fun setSelectedState(b: Boolean) {
        _isSelected.value = b
    }
}