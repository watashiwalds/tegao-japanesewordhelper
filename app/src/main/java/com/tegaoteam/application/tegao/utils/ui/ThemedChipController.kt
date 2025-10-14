package com.tegaoteam.application.tegao.utils.ui

import timber.log.Timber

class ThemedChipController(
    val chips: List<ThemedChipItem>,
    val mode: Int
) {
    companion object {
        const val MODE_SINGLE = 0
    }

    var currentSelected: ThemedChipItem? = null
        private set

    private fun onSelectedChipChanged(selectedChip: ThemedChipItem) {
        when (mode) {
            MODE_SINGLE -> {
                Timber.i("Set selected")
                currentSelected?.let {
                    if (currentSelected == selectedChip) return
                    it.setSelectedState(false)
                    selectedChip.setSelectedState(true)
                    currentSelected = selectedChip
                    return
                }
                selectedChip.setSelectedState(true)
                currentSelected = selectedChip
            }
        }
    }

    fun setSelected(chip: ThemedChipItem) {
        onSelectedChipChanged(chip)
    }
    fun setSelected(index: Int) {
        if (index in (0..<chips.size)) {
            onSelectedChipChanged(chips[index])
        }
    }
    fun selectFirst() {
        setSelected(0)
    }
}