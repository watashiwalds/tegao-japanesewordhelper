package com.tegaoteam.application.tegao.domain.repo

import com.tegaoteam.application.tegao.domain.independency.Stream

interface SettingRepo {
    fun isHepburnConverterEnable(): Stream<Boolean>
    suspend fun toggleHepburnConverter()

    fun getMainNavbarItemIds(): List<String>
}