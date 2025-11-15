package com.tegaoteam.application.tegao.ui.setting.model

import androidx.lifecycle.LiveData

data class ConfigEntryItem(
    val labelResId: Int,
    val descriptionResId: Int,
    val type: Type,
    val liveData: LiveData<Any>? = null,
    val clickListener: (() -> Unit)? = null
) {
    companion object {
        enum class Type {
            BOOLEAN,
            NON_CONTROL
        }
    }
}