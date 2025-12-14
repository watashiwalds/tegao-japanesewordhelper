package com.tegaoteam.application.tegao.ui.shared

import androidx.lifecycle.MutableLiveData
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipItem
import com.tegaoteam.application.tegao.utils.getStringFromAppRes

object IdTranslator {
    const val MAINNAV_TOLOOKUP = "lookup"
    const val MAINNAV_TOTRANSLATE = "translate"
    const val MAINNAV_TOCHATBOT = "chatbot"
    const val MAINNAV_TOLEARNING = "learning"

    /**
     * Get ThemedChipItem for navigating in MainActivity's fragments
     *
     * Notice: id value of ThemedChipItem is the resId of the destination fragment, .toInt() before using
     */
    fun mainNavbarId(navId: String): ThemedChipItem? {
        when (navId) {
            MAINNAV_TOLOOKUP -> return ThemedChipItem(
                id = (R.id.main_searchHistoryFragment).toString(),
                label = getStringFromAppRes(R.string.title_label_lookup),
                _isSelected = MutableLiveData<Boolean>(),
                iconResId = R.drawable.ftc_round_search_128
            )
            MAINNAV_TOTRANSLATE -> return ThemedChipItem(
                id = (R.id.main_translateFragment).toString(),
                label = getStringFromAppRes(R.string.title_label_translate),
                _isSelected = MutableLiveData<Boolean>(),
                iconResId = R.drawable.ftc_round_translate_128
            )
            MAINNAV_TOCHATBOT -> return ThemedChipItem(
                id = (R.id.main_chatbotFragment).toString(),
                label = "Chatbot",
                _isSelected = MutableLiveData<Boolean>(),
                iconResId = R.drawable.ftc_round_chatbot_128
            )
            MAINNAV_TOLEARNING -> return ThemedChipItem(
                id = (R.id.main_learningDashboardFragment).toString(),
                label = getStringFromAppRes(R.string.title_label_learning),
                _isSelected = MutableLiveData(),
                iconResId = R.drawable.ftc_round_flashcard_128
            )
            else -> return null
        }
    }
}