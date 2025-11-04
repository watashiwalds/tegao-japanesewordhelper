package com.tegaoteam.application.tegao.ui.setting.main

import androidx.lifecycle.ViewModel
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.ui.component.generics.ListNavigationItemInfo

class SettingListViewModel: ViewModel() {
    val settingNavigations = listOf(
        ListNavigationItemInfo(
            labelResId = R.string.setting_search_label,
            directionId = SettingListFragmentDirections.actionSettingListFragmentToSettingLookupFragment().actionId,
            detailResId = R.string.setting_search_detail,
            iconResId = R.drawable.ftc_round_search_128
        )
    )
}