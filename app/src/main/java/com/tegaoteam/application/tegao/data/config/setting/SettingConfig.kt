package com.tegaoteam.application.tegao.data.config.setting

import androidx.datastore.preferences.core.booleanPreferencesKey
import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.data.config.settingDataStore
import kotlinx.coroutines.flow.map

@Suppress("KotlinConstantConditions")
object SettingConfig {
    private val app = TegaoApplication.Companion.instance

    //setting preference keys
    private val USE_HEPBURN_CONVERTER = booleanPreferencesKey("use_hepburn_converter")

    val enableHepburnConverter = app.settingDataStore.data.map { prefs -> prefs[USE_HEPBURN_CONVERTER] ?: DefaultSettingValue.USE_HEPBURN_CONVERTER }
    val mainNavbarItemIds = listOf("lookup")
}