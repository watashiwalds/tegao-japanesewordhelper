package com.tegaoteam.application.tegao.ui.setting.addon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.model.asFlow
import com.tegaoteam.application.tegao.domain.repo.AddonRepo
import com.tegaoteam.application.tegao.domain.repo.SettingRepo
import com.tegaoteam.application.tegao.ui.setting.model.ConfigEntryItem
import kotlinx.coroutines.launch

class SettingAddonViewModel(private val _settingRepo: SettingRepo, private val _addonRepo: AddonRepo) : ViewModel() {
    val addonSettings = listOf(
        ConfigEntryItem(
            labelResId = R.string.setting_addon_label_section_toggle,
            descriptionResId = 0,
            type = ConfigEntryItem.Companion.Type.DECORATIVE_LABEL
        ),
        ConfigEntryItem(
            labelResId = R.string.setting_addon_label_handwriting,
            descriptionResId = R.string.setting_addon_detail_handwriting_toggle,
            type = ConfigEntryItem.Companion.Type.BOOLEAN,
            liveData = if (_addonRepo.isHandwritingAvailable()) _settingRepo.isHandwritingAddonEnable().asFlow().asLiveData() else null,
            clickListener = { if (_addonRepo.isHandwritingAvailable()) { viewModelScope.launch { _settingRepo.toggleHandwritingAddon() } } else null }
        ),
        ConfigEntryItem(
            labelResId = R.string.setting_addon_label_section_status,
            descriptionResId = 0,
            type = ConfigEntryItem.Companion.Type.DECORATIVE_LABEL
        ),
        ConfigEntryItem(
            labelResId = R.string.setting_addon_label_handwriting,
            descriptionResId = if (_addonRepo.isHandwritingAvailable()) R.string.setting_addon_status_isInstalled else R.string.setting_addon_status_notInstalled,
            type = ConfigEntryItem.Companion.Type.PENDING_INTENT,
            clickListener = null //todo: external link when not installed, setting intent when installed
        )
    )

    companion object {
        class ViewModelFactory(
            private val settingRepo: SettingRepo,
            private val addonRepo: AddonRepo
        ) : ViewModelProvider.Factory {
            @Suppress("unchecked_cast")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SettingAddonViewModel::class.java)) {
                    return SettingAddonViewModel(settingRepo, addonRepo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}