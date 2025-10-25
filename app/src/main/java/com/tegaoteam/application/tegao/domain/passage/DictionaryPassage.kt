package com.tegaoteam.application.tegao.domain.passage

import com.tegaoteam.application.tegao.data.config.DictionaryConfig
import com.tegaoteam.application.tegao.data.hub.DictionaryHub

object DictionaryPassage {
    fun getDictionariesList() = DictionaryConfig.getDictionariesList()
    fun getDictionaryHub() = DictionaryHub
}