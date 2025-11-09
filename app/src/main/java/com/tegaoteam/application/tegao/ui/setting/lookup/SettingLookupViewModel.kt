package com.tegaoteam.application.tegao.ui.setting.lookup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.model.asFlow
import com.tegaoteam.application.tegao.domain.repo.SettingRepo
import com.tegaoteam.application.tegao.ui.setting.model.ConfigEntryItem
import com.tegaoteam.application.tegao.ui.setting.model.ConfigType
import kotlinx.coroutines.launch

class SettingLookupViewModel(private val settingRepo: SettingRepo): ViewModel() {
    val lookupSettings = listOf(
        ConfigEntryItem(
            labelResId = R.string.setting_lookup_label_hepburn_converter,
            descriptionResId = R.string.setting_lookup_description_hepburn_converter,
            type = ConfigType.BOOLEAN,
            liveData = settingRepo.isHepburnConverterEnable().asFlow().asLiveData(),
            clickListener = { viewModelScope.launch { settingRepo.toggleHepburnConverter() } }
        )
    )

    companion object {
        class ViewModelFactory(
            private val settingRepo: SettingRepo
        ) : ViewModelProvider.Factory {
            @Suppress("unchecked_cast")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SettingLookupViewModel::class.java)) {
                    return SettingLookupViewModel(settingRepo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}