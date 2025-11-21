package com.tegaoteam.application.tegao.ui.component.generics

import androidx.lifecycle.MutableLiveData

data class ViewExpandControl(
    val expandable: Boolean,
    val isExpanding: MutableLiveData<Boolean>? = null
) {
    fun toggleExpand() {
        isExpanding?.apply { if (value != null) value = !value!! }
    }
}