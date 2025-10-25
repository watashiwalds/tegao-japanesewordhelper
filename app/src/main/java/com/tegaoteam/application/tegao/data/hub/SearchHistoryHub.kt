package com.tegaoteam.application.tegao.data.hub

import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.data.database.SQLiteDatabase
import com.tegaoteam.application.tegao.data.database.searchhistory.SearchHistoryEntity

object SearchHistoryHub {
    private val historyDb = SQLiteDatabase.getInstance(TegaoApplication.instance.applicationContext).searchHistoryDAO

    fun getSearchedWords() = historyDb.getSearchedWords()
    fun getSearchedKanjis() = historyDb.getSearchedKanjis()
    fun upsertEntity(entity: SearchHistoryEntity) {
        historyDb.upsert(entity)
    }
}