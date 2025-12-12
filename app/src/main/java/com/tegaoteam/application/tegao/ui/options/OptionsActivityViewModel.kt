package com.tegaoteam.application.tegao.ui.options

import androidx.lifecycle.ViewModel
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.ui.component.generics.listnavigation.ListNavigationItemInfo

class OptionsActivityViewModel: ViewModel() {
    val navigationList = listOf(
        ListNavigationItemInfo(
            labelResId = R.string.options_setting_label,
            directionId = R.id.settingListFragment,
            detailResId = R.string.options_setting_description,
            iconResId = R.drawable.ftc_round_setting_128
        ),
        ListNavigationItemInfo(
            labelResId = R.string.options_info_label,
            directionId = 0,
            detailResId = R.string.options_info_description,
            iconResId = R.drawable.ftc_round_info_128
        )
    )
}