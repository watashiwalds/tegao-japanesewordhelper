package com.tegaoteam.application.tegao.data.hub

import com.tegaoteam.application.tegao.data.config.DictionaryConfig
import com.tegaoteam.application.tegao.domain.interf.DictionaryNetworkApi
import com.tegaoteam.application.tegao.domain.model.Dictionary
import com.tegaoteam.application.tegao.domain.repo.ExtensionRepo

class ExtensionHub: ExtensionRepo {
    override fun getAvailableDictionariesList(): List<Dictionary> = DictionaryConfig.getDictionariesList()
    override fun getAvailableDictionaryNetworkApis(): List<DictionaryNetworkApi> = DictionaryConfig.getDictionariesApi()
}