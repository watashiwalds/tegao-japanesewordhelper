package com.tegaoteam.application.tegao.ui.component.generics

import androidx.lifecycle.MutableLiveData

data class ViewExpandControl(
    val expandable: Boolean,
    val isExpanding: MutableLiveData<Boolean>? = null
) {
    fun toggleExpand(b: Boolean? = null) {
        isExpanding?.let {
            if (b != null) isExpanding.value = b
            else isExpanding.apply { if (value != null) value = !value!! }
        }
    }
}