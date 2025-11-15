package com.tegaoteam.application.tegao.ui.setting.addon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.domain.repo.SettingRepo
import com.tegaoteam.application.tegao.ui.setting.model.ConfigEntryItem

class SettingAddonViewModel(private val _settingRepo: SettingRepo) : ViewModel() {
    val addonSettings = listOf(
        ConfigEntryItem(
            labelResId = R.string.setting_addon_label_section_toggle,
            descriptionResId = 0,
            type = ConfigEntryItem.Companion.Type.NON_CONTROL
        ),
        ConfigEntryItem(
            labelResId = R.string.setting_addon_label_section_status,
            descriptionResId = 0,
            type = ConfigEntryItem.Companion.Type.NON_CONTROL
        )
    )

    companion object {
        class ViewModelFactory(
            private val settingRepo: SettingRepo
        ) : ViewModelProvider.Factory {
            @Suppress("unchecked_cast")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SettingAddonViewModel::class.java)) {
                    return SettingAddonViewModel(settingRepo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}