package com.tegaoteam.application.tegao.ui.homescreen.lookup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tegaoteam.application.tegao.domain.repo.SearchHistoryRepo
import com.tegaoteam.application.tegao.ui.homescreen.lookup.searchhistory.SearchHistoryItem
import com.tegaoteam.application.tegao.ui.shared.GlobalState
import com.tegaoteam.application.tegao.utils.EventBeacon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

class LookupFragmentViewModel(seachHistoryRepo: SearchHistoryRepo): ViewModel() {
    val evNavigateToLookupActivity = EventBeacon()

    val lookupMode = GlobalState.lookupMode
    val evChangeToWordMode = EventBeacon()
    val evChangeToKanjiMode = EventBeacon()

    val wordSearchHistories = seachHistoryRepo.getSearchedWords()
        .map { it.map { entry -> SearchHistoryItem.fromDomainSearchHistory(entry) } }
        .asLiveData()
    val kanjiSearchHistories = seachHistoryRepo.getSearchedKanjis()
        .map { it.map { entry -> SearchHistoryItem.fromDomainSearchHistory(entry) } }
        .asLiveData()

    companion object {
        class ViewModelFactory(
            private val searchHistoryRepo: SearchHistoryRepo
        ) : ViewModelProvider.Factory {
            @Suppress("unchecked_cast")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(LookupFragmentViewModel::class.java)) {
                    return LookupFragmentViewModel(searchHistoryRepo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}