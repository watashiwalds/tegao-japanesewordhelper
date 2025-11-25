package com.tegaoteam.application.tegao.data.config

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.utils.Time
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

object LearningConfig {
    private val dataStore = TegaoApplication.instance.learningDataStore

    //region Learning streak manager
    private val LEARNING_STREAK_LAST_CHECKIN = stringPreferencesKey("learning_streak_last_checkin")
    val streakLastCheckin by lazy { dataStore.data.map { it[LEARNING_STREAK_LAST_CHECKIN]?: Time.getTodayMidnightTimestamp().toString() } }
    suspend fun streakCheckin() {
        when (Time.absoluteTimeDifferenceBetween(streakLastCheckin, Time.getTodayMidnightTimestamp(), Time.DIFF_DAY)) {
            0L -> { return }
            1L -> { updateCurrentStreak() }
            else -> { updateCurrentStreak(resetToZero = true) }
        }
        dataStore.edit { it[LEARNING_STREAK_LAST_CHECKIN] = Time.getTodayMidnightTimestamp().toString() }
    }

    private val LEARNING_STREAK_CURRENT = longPreferencesKey("learning_streak_current")
    val currentStreak by lazy { dataStore.data.map { it[LEARNING_STREAK_CURRENT]?: 0 } }
    private suspend fun updateCurrentStreak(resetToZero: Boolean = false) {
        val finRes = if (resetToZero) 0 else currentStreak.first() + 1
        updateHighestStreak(finRes)
        dataStore.edit { it[LEARNING_STREAK_CURRENT] = finRes }
    }

    private val LEARNING_STREAK_HIGHEST = longPreferencesKey("learning_streak_highest")
    val highestStreak by lazy { dataStore.data.map { it[LEARNING_STREAK_HIGHEST]?: 0 } }
    private suspend fun updateHighestStreak(newStreak: Long) {
        val finRes = highestStreak.first().takeUnless { it < newStreak }?: newStreak
        dataStore.edit { it[LEARNING_STREAK_CURRENT] = finRes }
    }
    //endregion
}