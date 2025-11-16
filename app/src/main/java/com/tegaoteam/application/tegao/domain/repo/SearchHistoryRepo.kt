package com.tegaoteam.application.tegao.domain.repo

import com.tegaoteam.application.tegao.domain.independency.Stream
import com.tegaoteam.application.tegao.domain.model.SearchHistory

interface SearchHistoryRepo {
    fun getSearchedWords(): Stream<List<SearchHistory>>
    fun getSearchedKanjis(): Stream<List<SearchHistory>>
    suspend fun logSearch(entry: SearchHistory)
    suspend fun deleteAll(): Int
}