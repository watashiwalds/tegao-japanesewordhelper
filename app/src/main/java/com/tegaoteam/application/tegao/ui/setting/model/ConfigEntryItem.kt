package com.tegaoteam.application.tegao.ui.setting.model

import androidx.lifecycle.LiveData

data class ConfigEntryItem(
    val labelResId: Int,
    val descriptionResId: Int,
    val type: Type,
    var liveData: LiveData<Any>? = null,
    var clickListener: (() -> Unit)? = null
) {
    companion object {
        enum class Type {
            BOOLEAN,
            CONFIRMATION,
            PENDING_INTENT,
            DECORATIVE_LABEL,
        }
    }
}