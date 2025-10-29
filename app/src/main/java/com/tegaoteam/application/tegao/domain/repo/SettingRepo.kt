package com.tegaoteam.application.tegao.domain.repo

interface SettingRepo {
    fun isHepburnConverterEnable(): Boolean

    fun getMainNavbarItemIds(): List<String>
}