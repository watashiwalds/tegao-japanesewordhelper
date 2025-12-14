package com.tegaoteam.application.tegao.data.hub

import com.tegaoteam.application.tegao.data.addon.HandwritingAddonConnection
import com.tegaoteam.application.tegao.data.addon.offlinedict.OfflineDictionaryAddonConnection
import com.tegaoteam.application.tegao.data.config.AddonConfig
import com.tegaoteam.application.tegao.domain.repo.AddonRepo

class AddonHub : AddonRepo {
    override fun isHandwritingAvailable(): Boolean = AddonConfig.isHandwritingAvailable
    override val handwritingAddonApi = if (isHandwritingAvailable()) HandwritingAddonConnection.instance else null

    override fun isOfflineDictionaryAvailable(): Boolean = AddonConfig.isOfflineDictionaryAvailable
    override val offlineDictionaryAddonApi = if (isOfflineDictionaryAvailable()) OfflineDictionaryAddonConnection.instance else null
}