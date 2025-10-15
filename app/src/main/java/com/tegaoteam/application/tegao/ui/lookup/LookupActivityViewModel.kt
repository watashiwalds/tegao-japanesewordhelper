package com.tegaoteam.application.tegao.ui.lookup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.tegaoteam.application.tegao.domain.passage.DictionaryPassage
import com.tegaoteam.application.tegao.domain.model.RepoResult
import com.tegaoteam.application.tegao.ui.shared.GlobalState
import com.tegaoteam.application.tegao.utils.EventBeacon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LookupActivityViewModel(app: Application): AndroidViewModel(app) {
    //Coroutine stuff
    private var viewModelJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    //Search string using when tap Search button
    private var _userSearchString = MutableLiveData<String>()
    fun setSearchString(s: String) {
        _userSearchString.value = s
    }

    //Clear search string QOL function
    val evClearSearchString = EventBeacon()
    val enableClearSearchString = _userSearchString.map { !it.isNullOrBlank() }

    //Mode changing handling
    val lookupMode = GlobalState.lookupMode
    val evChangeToWordMode = EventBeacon()
    val evChangeToKanjiMode = EventBeacon()

    //Dictionary available
    val availableDictionariesList = DictionaryPassage.getDictionariesList()
    var selectedDictionaryId: String = ""

    private var _indevRetrofitResult = MutableLiveData<String>()
    val indevRetrofitResult: LiveData<String> = _indevRetrofitResult

    //Start search on selected source
    val dictionaryHub = DictionaryPassage.getDictionaryHub()
    val evStartSearch = EventBeacon()
    fun searchKeyword() {
        if (_userSearchString.value.isNullOrBlank()) return
        //for the test of online api, default to indev
        if (!selectedDictionaryId.isEmpty()) {
            _indevRetrofitResult.value = "Now searching..."
            ioScope.launch {
                //TODO: Word and Kanji mode respectively
                val result = dictionaryHub.devTest(_userSearchString.value!!, selectedDictionaryId)
                withContext(Dispatchers.Main) {
                    _indevRetrofitResult.value = when (result) {
                        is RepoResult.Error<*> -> "ErrorCode: ${result.code}, Reason: ${result.message}"
                        is RepoResult.Success<*> -> "${result.data}"
                    }
                }
            }
            return
        }
    }

    //when viewModel being cleared (activity dismiss)
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}