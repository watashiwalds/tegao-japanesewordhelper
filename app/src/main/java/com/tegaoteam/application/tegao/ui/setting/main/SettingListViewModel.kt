package com.tegaoteam.application.tegao.ui.setting.main

import androidx.lifecycle.ViewModel
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.ui.component.generics.ListNavigationItemInfo

class SettingListViewModel: ViewModel() {
    val settingNavigations = listOf(
        ListNavigationItemInfo(
            labelResId = R.string.setting_lookup_label,
            directionId = SettingListFragmentDirections.actionSettingListFragmentToSettingLookupFragment().actionId,
            detailResId = R.string.setting_lookup_detail,
            iconResId = R.drawable.ftc_round_search_128
        ),
        ListNavigationItemInfo(
            labelResId = R.string.setting_history_label,
            directionId = SettingListFragmentDirections.actionSettingListFragmentToSettingHistoryFragment().actionId,
            detailResId = R.string.setting_history_detail,
            iconResId = R.drawable.ftc_round_history_128
        ),
        ListNavigationItemInfo(
            labelResId = R.string.setting_addon_label,
            directionId = SettingListFragmentDirections.actionSettingListFragmentToSettingAddonFragment().actionId,
            detailResId = R.string.setting_addon_detail,
            iconResId = R.drawable.ftc_round_addon_128
        ),
//        ListNavigationItemInfo(
//            labelResId = R.string.dev_setting_label,
//            directionId = SettingListFragmentDirections.actionSettingListFragmentToDevPlaygroundFragment().actionId,
//            detailResId = R.string.dev_setting_detail,
//            iconResId = R.drawable.ftc_bold_lab_128
//        ),
    )
}