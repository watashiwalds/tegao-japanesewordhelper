package com.tegaoteam.application.tegao.data.hub

import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.data.database.SQLiteDatabase
import com.tegaoteam.application.tegao.data.database.searchhistory.SearchHistoryEntity
import com.tegaoteam.application.tegao.data.model.FlowStream
import com.tegaoteam.application.tegao.domain.repo.SearchHistoryRepo
import com.tegaoteam.application.tegao.domain.model.SearchHistory
import kotlinx.coroutines.flow.map
import timber.log.Timber

class SearchHistoryHub: SearchHistoryRepo {
    private val historyDb = SQLiteDatabase.getInstance(TegaoApplication.instance.applicationContext).searchHistoryDAO

    override fun getSearchedWords(): FlowStream<List<SearchHistory>> {
        val flow = historyDb.getSearchedWords().map { it.map { entity -> entity.toDomainSearchHistory() } }
        Timber.i("Repo return Flow<Word History>")
        return FlowStream(flow)
    }
    override fun getSearchedKanjis(): FlowStream<List<SearchHistory>> {
        val flow = historyDb.getSearchedKanjis().map { it.map { entity -> entity.toDomainSearchHistory() } }
        Timber.i("Repo return Flow<Kanji History>")
        return FlowStream(flow)
    }
    override suspend fun logSearch(entry: SearchHistory) {
        Timber.i("Perform Room history logging $entry")
        if (entry.keyword.isBlank() || entry.searchDate.isBlank()) return
        historyDb.upsert(SearchHistoryEntity.fromDomainSearchHistory(entry))
    }
}