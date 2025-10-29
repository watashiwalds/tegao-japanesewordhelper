package com.tegaoteam.application.tegao.ui.shared

import androidx.lifecycle.MutableLiveData
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipItem

object IdTranslator {
    const val MAINNAV_TOLOOKUP = "lookup"

    /**
     * Get ThemedChipItem for navigating in MainActivity's fragments
     *
     * Notice: id value of ThemedChipItem is the resId of the destination fragment, .toInt() before using
     */
    fun mainNavbarId(navId: String): ThemedChipItem? {
        when (navId) {
            MAINNAV_TOLOOKUP -> return ThemedChipItem(
                id = (R.id.lookupFragment).toString(),
                label = TegaoApplication.instance.getString(R.string.title_label_lookup),
                _isSelected = MutableLiveData<Boolean>(),
                iconResId = R.drawable.ftc_round_search_128
            )
            else -> return null
        }
    }
}