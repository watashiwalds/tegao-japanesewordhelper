package com.tegaoteam.application.tegao.domain.repo

import com.tegaoteam.application.tegao.domain.interf.DictionaryNetworkApi
import com.tegaoteam.application.tegao.domain.model.Dictionary

interface ExtensionRepo {
    fun getAvailableDictionariesList(): List<Dictionary>
    fun getAvailableDictionaryNetworkApis(): List<DictionaryNetworkApi>
}