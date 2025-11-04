package com.tegaoteam.application.tegao.domain.repo

import kotlinx.coroutines.flow.Flow

interface SettingRepo {
    fun isHepburnConverterEnable(): Flow<Boolean>
    suspend fun toggleHepburnConverter()

    fun getMainNavbarItemIds(): List<String>
}