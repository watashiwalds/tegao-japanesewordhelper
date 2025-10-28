package com.tegaoteam.application.tegao.data.hub

import com.tegaoteam.application.tegao.data.config.AddonConfig
import com.tegaoteam.application.tegao.domain.repo.AddonRepo

class AddonHub: AddonRepo {
    override fun isHandwritingAvailable(): Boolean = AddonConfig.isHandwritingAvailable
}