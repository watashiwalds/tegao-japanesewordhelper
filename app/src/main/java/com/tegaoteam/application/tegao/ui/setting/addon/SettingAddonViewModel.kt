package com.tegaoteam.application.tegao.ui.setting.addon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tegaoteam.application.tegao.domain.repo.SettingRepo

class SettingAddonViewModel(private val _settingRepo: SettingRepo) : ViewModel() {
    // TODO: Implement the ViewModel

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