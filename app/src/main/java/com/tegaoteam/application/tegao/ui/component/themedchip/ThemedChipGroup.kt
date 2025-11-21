package com.tegaoteam.application.tegao.ui.component.themedchip

import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.tegaoteam.application.tegao.ui.component.generics.ViewExpandControl

/**
 * Class to manage ThemedChips to inflate to a designated XML design with custom action
 *
 * REMEMBER, ALWAYS SET THE MANAGER TO THE LIST_ADAPTER BEFORE SUBMIT THE CHIP LIST!
 *
 * nvm init{} did it for ya
 */
data class ThemedChipGroup(
    val id: String,
    val label: String,
    val manager: ThemedChipManager,
    val listAdapter: ThemedChipListAdapter<*>,
    val layoutManager: LayoutManager? = null,
    val expandControl: ViewExpandControl = ViewExpandControl(true),
    val allowQAB: Boolean = false
) {
    init {
        listAdapter.themedChipManager = manager
    }
    fun getSelectedChips() = manager.selectedChips
}