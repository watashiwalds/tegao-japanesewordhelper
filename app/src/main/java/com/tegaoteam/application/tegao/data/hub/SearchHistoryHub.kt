package com.tegaoteam.application.tegao.data.hub

import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.data.database.SQLiteDatabase
import com.tegaoteam.application.tegao.domain.repo.SearchHistoryRepo
import com.tegaoteam.application.tegao.domain.model.SearchHistory
import kotlinx.coroutines.flow.map
import timber.log.Timber

class SearchHistoryHub: SearchHistoryRepo {
    private val historyDb = SQLiteDatabase.getInstance(TegaoApplication.instance.applicationContext).searchHistoryDAO

    override fun getSearchedWords() = historyDb.getSearchedWords().map { it.map { entity -> entity.toDomainSearchHistory() } }
    override fun getSearchedKanjis() = historyDb.getSearchedKanjis().map { it.map { entity -> entity.toDomainSearchHistory() } }
    override fun logSearch(entry: SearchHistory) {
        Timber.i("logSearch success with value $entry")
//        historyDb.upsert(SearchHistoryEntity.fromDomainSearchHistory(entry))
    }
}