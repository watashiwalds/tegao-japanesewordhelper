package com.tegaoteam.application.tegao.data.config.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val DATASTORE_SETTINGS = "settings"
private const val DATASTORE_LEARNING = "learning_configs"
val Context.settingDataStore: DataStore<Preferences> by preferencesDataStore(DATASTORE_SETTINGS)
val Context.learningDataStore: DataStore<Preferences> by preferencesDataStore(DATASTORE_LEARNING)