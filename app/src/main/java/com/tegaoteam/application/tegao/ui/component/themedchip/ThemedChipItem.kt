package com.tegaoteam.application.tegao.ui.component.themedchip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tegaoteam.application.tegao.domain.model.Dictionary
import com.tegaoteam.application.tegao.domain.model.Kanji

/**
 * Holder class for themed_chip_item.xml View
 *
 * Require manual selecting control logic to correctly display (hold on to MutableLiveData<Boolean> or ```selected``` references)
 *
 * Require manual binding onClick() in item's XML to activate onClick() function of chip
 */
class ThemedChipItem(
    val id: String,
    val label: String,
    private val _isSelected: MutableLiveData<Boolean>,
    val iconResId: Int? = null
) {
    init {
        _isSelected.value = false
    }

    var manager: ThemedChipManager? = null
        private set
    fun assignController(controller: ThemedChipManager) { if (this.manager == null) this.manager = controller}

    var onSelectedListener: ((ThemedChipItem) -> Unit)? = null
    var onUnselectedListener: ((ThemedChipItem) -> Unit)? = null

    val isSelected: LiveData<Boolean> = _isSelected
    fun setSelectedState(b: Boolean) {
        _isSelected.value = b
        onSelectedStateChanged()
    }
    fun toggleSelectedState() {
        _isSelected.value = !_isSelected.value!!
        onSelectedStateChanged()
    }
    fun nowSelected() {
        _isSelected.value = true
        onSelectedStateChanged()
    }
    fun nowUnselected() {
        _isSelected.value = false
        onSelectedStateChanged()
    }

    private fun onSelectedStateChanged() {
        when (_isSelected.value) {
            true -> {
                manager?.onSelected(this)
                onSelectedListener?.invoke(this)
            }
            false -> {
                manager?.onUnselected(this)
                onUnselectedListener?.invoke(this)
            }
            null -> {}
        }
    }

    companion object {
        fun fromDictionary(dict: Dictionary): ThemedChipItem {
            val chip = ThemedChipItem(
                dict.id,
                dict.displayName,
                MutableLiveData<Boolean>()
            )
            return chip
        }
        fun fromKanji(kanji: Kanji): ThemedChipItem {
            val chip = ThemedChipItem(
                kanji.character,
                kanji.character,
                MutableLiveData<Boolean>()
            )
            return chip
        }
    }
}