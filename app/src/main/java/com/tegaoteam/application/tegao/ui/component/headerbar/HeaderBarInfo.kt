package com.tegaoteam.application.tegao.ui.component.headerbar

data class HeaderBarInfo(
    val label: String,
    val backOnClickListener: (() -> Unit)? = null
)