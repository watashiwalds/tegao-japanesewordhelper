package com.tegaoteam.application.tegao.ui.component.themedchip

class ThemedChipManager(
    val chips: List<ThemedChipItem>,
    val mode: Int
) {
    companion object {
        const val MODE_SINGLE = 0
        const val MODE_MULTI = 1
    }

    init {
        chips.forEach { it.assignController(this) }
    }

    private val _selectedChips = mutableListOf<ThemedChipItem>()
    val selectedChips: List<ThemedChipItem> = _selectedChips

    private fun onChipSelectStateChanged(callerChip: ThemedChipItem, selectedState: Boolean) {
        when (mode) {
            MODE_SINGLE -> {
                if (_selectedChips.isEmpty()) {
                    if (selectedState) _selectedChips.add(callerChip)
                    return
                }
                if (_selectedChips[0] == callerChip) {
                    if (!selectedState) {
                        _selectedChips.clear()
                    }
                } else {
                    _selectedChips[0].nowUnselected()
                }
            }
            MODE_MULTI -> {
                when (selectedState) {
                    true -> {
                        _selectedChips.add(callerChip)
                    }
                    false -> {
                        _selectedChips.remove(callerChip)
                    }
                }
            }
        }
    }

    fun onSelected(chip: ThemedChipItem) {
        if (chip in chips) onChipSelectStateChanged(chip, true)
    }
    fun onUnselected(chip: ThemedChipItem) {
        if (chip in chips) onChipSelectStateChanged(chip, false)
    }

    fun selectFirst() {
        if (chips.isNotEmpty()) {
            chips[0].nowSelected()
        }
    }
}