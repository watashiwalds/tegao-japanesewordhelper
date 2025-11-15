package com.tegaoteam.application.tegao.domain.repo

import com.tegaoteam.application.tegao.domain.independency.Stream

interface SettingRepo {
    fun isHepburnConverterEnable(): Stream<Boolean>
    suspend fun toggleHepburnConverter()


    fun isHandwritingAddonEnable(): Stream<Boolean>
    suspend fun toggleHandwritingAddon()

    fun getMainNavbarItemIds(): List<String>
}