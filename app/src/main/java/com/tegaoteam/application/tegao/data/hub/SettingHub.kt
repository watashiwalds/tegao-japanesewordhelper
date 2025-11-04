package com.tegaoteam.application.tegao.data.hub

import com.tegaoteam.application.tegao.data.config.setting.SettingConfig
import com.tegaoteam.application.tegao.domain.repo.SettingRepo
import com.tegaoteam.application.tegao.utils.AppToast

class SettingHub: SettingRepo {
    //function settings (mainly boolean or int value)
    override fun isHepburnConverterEnable() = SettingConfig.enableHepburnConverter
    override suspend fun toggleHepburnConverter() {
        SettingConfig.toggleHepburnConverter()
        AppToast.show("Hepburn toggled", AppToast.LENGTH_SHORT)
    }

    //personalization setting (mainly listOf)
    override fun getMainNavbarItemIds(): List<String> = SettingConfig.mainNavbarItemIds
}