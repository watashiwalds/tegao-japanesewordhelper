package com.tegaoteam.application.tegao.data.hub

import com.tegaoteam.application.tegao.data.addon.HandwritingAddonApi
import com.tegaoteam.application.tegao.data.config.AddonConfig
import com.tegaoteam.application.tegao.domain.interf.AlternativeInputApi
import com.tegaoteam.application.tegao.domain.repo.AddonRepo

class AddonHub : AddonRepo {
    override fun isHandwritingAvailable(): Boolean = AddonConfig.isHandwritingAvailable
    override val handwritingAddonApi = if (isHandwritingAvailable()) HandwritingAddonApi.instance else null
}