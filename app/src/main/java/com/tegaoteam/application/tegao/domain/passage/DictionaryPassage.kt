package com.tegaoteam.application.tegao.domain.passage

import com.tegaoteam.application.tegao.data.config.DictionaryConfig
import com.tegaoteam.application.tegao.data.hub.DictionaryHub

class DictionaryPassage {
    companion object {
        fun getDictionariesList() = DictionaryConfig.getDictionariesList()
        fun getDictionaryHub() = DictionaryHub
    }
}