package com.tegaoteam.application.tegao.data.config

import androidx.datastore.preferences.core.booleanPreferencesKey
import com.tegaoteam.application.tegao.TegaoApplication
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

object SettingConfig {
    private val app = TegaoApplication.instance

    //default setting values
    const val DEF_USE_HEPBURN_CONVERTER = true

    //setting preference keys
    private val USE_HEPBURN_CONVERTER = booleanPreferencesKey("use_hepburn_converter")

    val enableHepburnConverter = app.settingDataStore.data.map { prefs -> prefs[USE_HEPBURN_CONVERTER] ?: DEF_USE_HEPBURN_CONVERTER }
    val mainNavbarItemIds = listOf("lookup")

}