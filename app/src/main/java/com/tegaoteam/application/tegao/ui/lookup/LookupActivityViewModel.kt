package com.tegaoteam.application.tegao.ui.lookup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.model.asFlow
import com.tegaoteam.application.tegao.domain.repo.SearchHistoryRepo
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.tegaoteam.application.tegao.domain.model.SearchHistory
import com.tegaoteam.application.tegao.domain.model.Word
import com.tegaoteam.application.tegao.domain.repo.AddonRepo
import com.tegaoteam.application.tegao.domain.repo.DictionaryRepo
import com.tegaoteam.application.tegao.domain.repo.SettingRepo
import com.tegaoteam.application.tegao.ui.component.generics.SwitchButtonInfo
import com.tegaoteam.application.tegao.ui.shared.FetchedConfigs
import com.tegaoteam.application.tegao.ui.shared.GlobalState
import com.tegaoteam.application.tegao.utils.AppToast
import com.tegaoteam.application.tegao.utils.EventBeacon
import com.tegaoteam.application.tegao.utils.HepburnStringConvert
import com.tegaoteam.application.tegao.utils.Time.getCurrentTimestamp
import com.tegaoteam.application.tegao.utils.toSafeQueryString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class LookupActivityViewModel(private val dictionaryRepo: DictionaryRepo, private val searchHistoryRepo: SearchHistoryRepo): ViewModel() {
    //Coroutine stuff
    //old way (?)
//    private var viewModelJob = Job()
//    private val ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    //switch to use viewModelScope instead
    private var searchJob: Job? = null
    private var logJob: Job? = null

    //Search string using when tap Search button
    private val _userSearchString = MutableLiveData<String>().apply { value = "" }
    val userSearchString: LiveData<String> = _userSearchString

    //Clear search string QOL function
    val evClearSearchString = EventBeacon()
    val enableClearSearchString = _userSearchString.map { !it.isNullOrBlank() }

    //Mode changing handling
    val lookupMode = GlobalState.lookupMode
    val evChangeToWordMode = EventBeacon()
    val evChangeToKanjiMode = EventBeacon()

    //search result list value holder
    private var _searchResultList = MutableLiveData<List<Any>>()
    val searchResultList: LiveData<List<Any>> = _searchResultList

    //Dictionary available
    val availableDictionariesList = dictionaryRepo.getAvailableDictionariesList()
    var selectedDictionaryId: String = ""

    private var _nonResult = MutableLiveData<String>()
    val nonResult: LiveData<String> = _nonResult

    //Start search on selected source
    val evStartSearch = EventBeacon()

    //show devtest textview in case of
    val evIsRcyAdapterAvailable = MutableLiveData<Boolean>()

    //handwriting addon variables
    val isHandwritingEnabled = FetchedConfigs.isHandwritingEnabled

    //preference values
    private val _useHepburnConverter = FetchedConfigs.isHepburnConverterEnabled.value

    fun setSearchString(s: String) {
        var t = s.toSafeQueryString()
        if (_useHepburnConverter) t = HepburnStringConvert.toHiragana(s)
        _userSearchString.value = t
        Timber.i("Safe string query return ${_userSearchString.value}")
    }

    fun searchKeyword() {
        //no keyword to search? bye
        if (_userSearchString.value.isNullOrBlank()) return
        //drop every pending search to do this search instead
        if (searchJob != null) searchJob?.cancel()
        //no proper dictionary to search on? bye
        if (selectedDictionaryId.isEmpty()) return

        Timber.i("Start searching keyword [${_userSearchString.value}] on [$selectedDictionaryId] dictionary")

        //TODO: Proper displaying onSearch status in Activity, not by toasting text
        AppToast.show("Now searching...", AppToast.LENGTH_SHORT)
        _nonResult.value = "Now searching..."

        searchJob = viewModelScope.launch(Dispatchers.IO) {
            val result = when (lookupMode.value) {
                GlobalState.LookupMode.WORD -> dictionaryRepo.searchWord(_userSearchString.value!!, selectedDictionaryId)
                GlobalState.LookupMode.KANJI -> dictionaryRepo.searchKanji(_userSearchString.value!!, selectedDictionaryId)
            }
            result.collect { res ->
                withContext(Dispatchers.Main) {
                    when (res) {
                        is RepoResult.Error<*> -> _nonResult.value =
                            "Error [${res.code}] : ${res.message}"

                        is RepoResult.Success<*> -> {
                            //return search result to activity
                            val data = res.data
                            @Suppress("unchecked_cast")
                            if (data is List<*>) {
                                when (data.firstOrNull()) {
                                    is Word -> _searchResultList.value = data as List<Word>
                                    is Kanji -> _searchResultList.value = data as List<Kanji>
                                    else -> {}
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun logSearch(keyword: String) {
        logJob =
            when (lookupMode.value) {
                GlobalState.LookupMode.WORD -> viewModelScope.launch(Dispatchers.IO) { searchHistoryRepo.logSearch(SearchHistory(
                    type = SearchHistory.TYPE_WORD,
                    keyword = keyword,
                    searchDate = getCurrentTimestamp().toString()
                )) }
                GlobalState.LookupMode.KANJI -> viewModelScope.launch(Dispatchers.IO) { searchHistoryRepo.logSearch(SearchHistory(
                    type = SearchHistory.TYPE_KANJI,
                    keyword = keyword,
                    searchDate = getCurrentTimestamp().toString()
                )) }
            }
    }

    //when viewModel being cleared (activity dismiss)
    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }

    companion object {
        class ViewModelFactory(
            private val dictionaryRepo: DictionaryRepo,
            private val searchHistoryRepo: SearchHistoryRepo
        ) : ViewModelProvider.Factory {
            @Suppress("unchecked_cast")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(LookupActivityViewModel::class.java)) {
                    return LookupActivityViewModel(dictionaryRepo, searchHistoryRepo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}