package com.tegaoteam.application.tegao.domain.repo

import com.tegaoteam.application.tegao.domain.interf.AlternativeInputApi

interface AddonRepo {
    fun isHandwritingAvailable(): Boolean
    val handwritingAddonApi: AlternativeInputApi?
}