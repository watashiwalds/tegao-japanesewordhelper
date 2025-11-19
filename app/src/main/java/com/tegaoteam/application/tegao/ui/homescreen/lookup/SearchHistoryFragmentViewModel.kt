package com.tegaoteam.application.tegao.ui.homescreen.lookup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.tegaoteam.application.tegao.data.model.asFlow
import com.tegaoteam.application.tegao.domain.repo.SearchHistoryRepo
import com.tegaoteam.application.tegao.ui.homescreen.lookup.searchhistory.SearchHistoryItem
import com.tegaoteam.application.tegao.ui.shared.GlobalState
import com.tegaoteam.application.tegao.utils.EventBeacon
import kotlinx.coroutines.flow.map

class SearchHistoryFragmentViewModel(seachHistoryRepo: SearchHistoryRepo): ViewModel() {
    val evNavigateToLookupActivity = EventBeacon()

    val lookupMode = GlobalState.lookupMode.asLiveData()
    val evChangeToWordMode = EventBeacon()
    val evChangeToKanjiMode = EventBeacon()

    val wordSearchHistories = seachHistoryRepo.getSearchedWords()
        .asFlow()
        .map { it.map { entry -> SearchHistoryItem.fromDomainSearchHistory(entry) } }
        .asLiveData()
    val kanjiSearchHistories = seachHistoryRepo.getSearchedKanjis()
        .asFlow()
        .map { it.map { entry -> SearchHistoryItem.fromDomainSearchHistory(entry) } }
        .asLiveData()

    companion object {
        class ViewModelFactory(
            private val searchHistoryRepo: SearchHistoryRepo
        ) : ViewModelProvider.Factory {
            @Suppress("unchecked_cast")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SearchHistoryFragmentViewModel::class.java)) {
                    return SearchHistoryFragmentViewModel(searchHistoryRepo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}