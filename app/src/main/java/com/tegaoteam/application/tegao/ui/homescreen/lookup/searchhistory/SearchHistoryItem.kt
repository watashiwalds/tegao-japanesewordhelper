package com.tegaoteam.application.tegao.ui.homescreen.lookup.searchhistory

import com.tegaoteam.application.tegao.domain.model.SearchHistory

data class SearchHistoryItem(
    val type: Int,
    val keyword: String,
    val searchDate: String
) {
    fun toDomainSearchHistory() = SearchHistory(type, keyword, searchDate)
    companion object {
        fun fromDomainSearchHistory(entry: SearchHistory) = SearchHistoryItem(entry.type, entry.keyword, entry.searchDate)
    }
}