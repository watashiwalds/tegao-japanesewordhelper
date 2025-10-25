package com.tegaoteam.application.tegao.domain.passage

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.data.database.SQLiteDatabase
import com.tegaoteam.application.tegao.domain.model.SearchHistory

object SearchHistoryPassage {
    private val historyDb = SQLiteDatabase.getInstance(TegaoApplication.instance.applicationContext).searchHistoryDAO

    fun getSearchedWords() = historyDb.getSearchedWords().map { it.map { entity -> entity.toDomainSearchHistory() } }
    fun getSearchedKanjis() = historyDb.getSearchedKanjis().map { it.map { entity -> entity.toDomainSearchHistory() } }
    fun upsertHistory(entry: SearchHistory) { historyDb.upsertHistory(entry) }
}