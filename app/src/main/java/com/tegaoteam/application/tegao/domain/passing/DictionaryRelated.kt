package com.tegaoteam.application.tegao.domain.passing

import com.tegaoteam.application.tegao.data.config.DictionaryConfig

class DictionaryRelated {
    companion object {
        fun getDictionariesList() = DictionaryConfig.getDictionariesList()
    }
}