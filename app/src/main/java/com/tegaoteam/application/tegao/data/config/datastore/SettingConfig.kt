package com.tegaoteam.application.tegao.data.config.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.tegaoteam.application.tegao.TegaoApplication
import kotlinx.coroutines.flow.map

@Suppress("KotlinConstantConditions", "UnusedFlow")
object SettingConfig {
    private val app = TegaoApplication.Companion.instance

    //setting preference keys
    private val USE_HEPBURN_CONVERTER = booleanPreferencesKey("use_hepburn_converter")
    val useHepburnConverter by lazy { app.settingDataStore.data.map { prefs -> prefs[USE_HEPBURN_CONVERTER] ?: DefaultConfigs.USE_HEPBURN_CONVERTER } }
    suspend fun toggleHepburnConverter() {
        app.settingDataStore.edit { settings -> settings[USE_HEPBURN_CONVERTER] = !(settings[USE_HEPBURN_CONVERTER]?: true) }
    }

    private val ENABLE_HANDWRITING_ADDON = booleanPreferencesKey("enable_handwriting_addon")
    val enableHandwritingAddon by lazy { app.settingDataStore.data.map { prefs -> prefs[ENABLE_HANDWRITING_ADDON] ?: DefaultConfigs.ENABLE_HANDWRITING_ADDON } }
    suspend fun toggleHandwritingAddon() {
        app.settingDataStore.edit { settings -> settings[ENABLE_HANDWRITING_ADDON] = !(settings[ENABLE_HANDWRITING_ADDON]?: true) }
    }

    val mainNavbarItemIds = listOf("lookup", "translate", "chatbot", "learning")

    init {
        useHepburnConverter
        enableHandwritingAddon
    }
}