package com.tegaoteam.application.tegao.data.config

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore

private const val DATASTORE_SETTINGS = "settings"
val Context.settingDataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(DATASTORE_SETTINGS)