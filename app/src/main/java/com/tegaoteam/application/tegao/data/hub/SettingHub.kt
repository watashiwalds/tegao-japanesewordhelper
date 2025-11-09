package com.tegaoteam.application.tegao.data.hub

import com.tegaoteam.application.tegao.data.config.setting.SettingConfig
import com.tegaoteam.application.tegao.data.model.FlowStream
import com.tegaoteam.application.tegao.domain.repo.SettingRepo
import timber.log.Timber

class SettingHub: SettingRepo {
    //function settings (mainly boolean or int value)
    override fun isHepburnConverterEnable() = FlowStream(SettingConfig.enableHepburnConverter)
    override suspend fun toggleHepburnConverter() {
        SettingConfig.toggleHepburnConverter()
        Timber.i("Hepburn togged")
    }

    //personalization setting (mainly listOf)
    override fun getMainNavbarItemIds(): List<String> = SettingConfig.mainNavbarItemIds
}