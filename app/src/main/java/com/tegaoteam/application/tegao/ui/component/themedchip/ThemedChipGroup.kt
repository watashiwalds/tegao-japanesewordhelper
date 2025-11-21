package com.tegaoteam.application.tegao.ui.component.themedchip

import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.tegaoteam.application.tegao.ui.component.generics.ViewExpandControl

data class ThemedChipGroup(
    val id: String,
    val label: String,
    val manager: ThemedChipManager,
    val listAdapter: ThemedChipListAdapter<*>,
    val layoutManager: LayoutManager? = null,
    val expandControl: ViewExpandControl = ViewExpandControl(true),
    val allowQAB: Boolean = false
)