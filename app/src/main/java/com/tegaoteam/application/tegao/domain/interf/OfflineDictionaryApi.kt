package com.tegaoteam.application.tegao.domain.interf

interface OfflineDictionaryApi: DictionaryLookupApi {
    fun registerCallback(callback: (String) -> Unit)
}