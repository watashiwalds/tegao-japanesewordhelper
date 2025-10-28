package com.tegaoteam.application.tegao.data.hub

import com.tegaoteam.application.tegao.data.config.SettingConfig
import com.tegaoteam.application.tegao.domain.repo.SettingRepo

class SettingHub: SettingRepo {
    override fun isHepburnConverterEnable() = SettingConfig.enableHepburnConverter
}