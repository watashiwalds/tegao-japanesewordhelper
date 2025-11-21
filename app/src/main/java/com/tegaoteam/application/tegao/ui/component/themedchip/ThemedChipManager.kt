package com.tegaoteam.application.tegao.ui.component.themedchip

class ThemedChipManager(
    val mode: Int,
    private var _chips: MutableList<ThemedChipItem> = mutableListOf()
) {
    companion object {
        const val MODE_SINGLE = 0
        const val MODE_MULTI = 1
    }

    init {
        assigningSelfToItems()
    }

    val chips: List<ThemedChipItem> = _chips

    private fun assigningSelfToItems() {
        _chips.forEach { it.assignController(this) }
    }

    private val _selectedChips = mutableListOf<ThemedChipItem>()
    val selectedChips: List<ThemedChipItem> = _selectedChips

    private fun onChipSelectStateChanged(callerChip: ThemedChipItem, selectedState: Boolean) {
//        Timber.i("START Caller [${callerChip.label}] calling for its state [${selectedState}]. Selected chip: ${selectedChips.joinToString { it.label }}")
        when (mode) {
            MODE_SINGLE -> {
                if (_selectedChips.isEmpty()) {
                    if (selectedState) _selectedChips.add(callerChip)
//                    Timber.i("Caller [${callerChip.label}] finished call. Selected chip: ${selectedChips.joinToString { it.label }}")
                    return
                }
                if (_selectedChips[0] == callerChip) {
                    if (!selectedState) {
                        _selectedChips.clear()
                    }
                } else {
                    _selectedChips[0].nowUnselected()
                    if (selectedState) {
                        if (_selectedChips.isEmpty()) _selectedChips.add(callerChip)
                        else _selectedChips[0] = callerChip
                    }
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
//        Timber.i("Caller [${callerChip.label}] finished call. Selected chip: ${selectedChips.joinToString { it.label }}")
    }

    fun onSelected(chip: ThemedChipItem) {
        if (chip in _chips) onChipSelectStateChanged(chip, true)
    }
    fun onUnselected(chip: ThemedChipItem) {
        if (chip in _chips) onChipSelectStateChanged(chip, false)
    }

    fun selectFirst() {
        if (_chips.isNotEmpty()) {
            _chips[0].nowSelected()
        }
    }

    fun selectAll() {
        if (mode == MODE_MULTI) {
            _selectedChips.clear()
            _chips.forEach { it.nowSelected() }
        }
    }

    fun unselectAll() {
        while (!_selectedChips.isEmpty()) _selectedChips.firstOrNull()?.nowUnselected()
    }

    fun isAllSelected() = (_selectedChips.size == _chips.size) && _chips.isNotEmpty()

    private var chipSelectedListener: ((ThemedChipItem) -> Unit)? = null
    private var chipUnselectedListener: ((ThemedChipItem) -> Unit)? = null
    fun setChipsOnSelectedListener(listener: ((ThemedChipItem) -> Unit)?) {
        _chips.forEach { it.onSelectedListener = listener }
        chipSelectedListener = listener
    }
    fun setChipsOnUnselectedListener(listener: ((ThemedChipItem) -> Unit)?) {
        _chips.forEach { it.onUnselectedListener = listener }
        chipUnselectedListener = listener
    }

    fun submitChipList(submitChips: List<ThemedChipItem>?) {
        _chips.apply {
            clear()
            addAll(submitChips?: emptyList())
        }
        assigningSelfToItems()
        chipSelectedListener?.let { setChipsOnSelectedListener { chipSelectedListener } }
        chipUnselectedListener?.let { setChipsOnUnselectedListener { chipUnselectedListener } }
    }
}