package com.tegaoteam.application.tegao.ui.setting.model

import androidx.lifecycle.LiveData

data class ConfigEntryItem(
    val labelResId: Int,
    val descriptionResId: Int,
    val type: ConfigType,
    val liveData: LiveData<Any>,
    val clickListener: () -> Unit
)