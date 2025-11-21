package com.tegaoteam.application.tegao.ui.component.themedchip

import com.tegaoteam.application.tegao.ui.component.generics.ViewExpandControl

data class ThemedChipGroupInfo(
    val label: String,
    val themedChipManager: ThemedChipManager,
    val expandControl: ViewExpandControl = ViewExpandControl(true),
    val allowQAB: Boolean = false
)