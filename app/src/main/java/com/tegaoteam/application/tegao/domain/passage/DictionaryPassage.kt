package com.tegaoteam.application.tegao.domain.passage

import com.tegaoteam.application.tegao.data.config.DictionaryConfig

class DictionaryPassage {
    companion object {
        fun getDictionariesList() = DictionaryConfig.getDictionariesList()
        fun getDictionariesApi() = DictionaryConfig.getDictionariesApi()
    }
}