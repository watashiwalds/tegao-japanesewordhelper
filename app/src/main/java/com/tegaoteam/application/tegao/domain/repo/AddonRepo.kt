package com.tegaoteam.application.tegao.domain.repo

import com.tegaoteam.application.tegao.domain.interf.AlternativeInputApi
import com.tegaoteam.application.tegao.domain.interf.OfflineDictionaryApi

interface AddonRepo {
    fun isHandwritingAvailable(): Boolean
    val handwritingAddonApi: AlternativeInputApi?

    fun isOfflineDictionaryAvailable(): Boolean
    val offlineDictionaryAddonApi: OfflineDictionaryApi?
}