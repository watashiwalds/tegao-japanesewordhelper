package com.tegaoteam.application.tegao.domain.interf

import com.tegaoteam.application.tegao.domain.model.SearchHistory
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepo {
    fun getSearchedWords(): Flow<List<SearchHistory>>
    fun getSearchedKanjis(): Flow<List<SearchHistory>>
    fun logSearch(entry: SearchHistory)
}