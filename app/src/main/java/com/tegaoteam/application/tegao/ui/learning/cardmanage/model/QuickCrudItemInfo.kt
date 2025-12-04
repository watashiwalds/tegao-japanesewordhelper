package com.tegaoteam.application.tegao.ui.learning.cardmanage.model

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

data class QuickCrudItemInfo(
    val id: Long,
    val label: String,
    val quickInfo: LiveData<String>,
    var onEditQabClickListener: ((Long) -> Unit)? = null,
    var onDeleteQabClickListener: ((Long) -> Unit)? = null,
    var onItemClickListener: ((Long) -> Unit)? = null,
    val lifecycleOwner: LifecycleOwner? = null
)