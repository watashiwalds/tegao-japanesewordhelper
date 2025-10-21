package com.tegaoteam.application.tegao.ui.component.themedchip

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
                currentSelected?.let {
                    if (currentSelected == selectedChip) return
                    it.setSelectedState(false)
                }
                selectedChip.setSelectedState(true)
                currentSelected = selectedChip
            }
        }
    }

    fun setSelected(chip: ThemedChipItem?) {
        if (chip != null) onSelectedChipChanged(chip)
    }
    fun setSelected(index: Int) {
        if (index in (0..<chips.size)) {
            chips[index].onClick()
        }
    }
    fun selectFirst() {
        setSelected(0)
    }
}