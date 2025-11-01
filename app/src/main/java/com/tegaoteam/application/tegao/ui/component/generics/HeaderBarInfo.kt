package com.tegaoteam.application.tegao.ui.component.generics

data class HeaderBarInfo(
    val label: String,
    val backOnClickListener: (() -> Unit)? = null
)