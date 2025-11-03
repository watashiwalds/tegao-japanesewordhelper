package com.tegaoteam.application.tegao.data.hub

import com.tegaoteam.application.tegao.data.config.setting.SettingConfig
import com.tegaoteam.application.tegao.domain.repo.SettingRepo

class SettingHub: SettingRepo {
    //function settings (mainly boolean or int value)
    override fun isHepburnConverterEnable() = SettingConfig.enableHepburnConverter

    //personalization setting (mainly listOf)
    override fun getMainNavbarItemIds(): List<String> = SettingConfig.mainNavbarItemIds
}