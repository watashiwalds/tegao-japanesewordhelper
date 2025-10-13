package com.tegaoteam.application.tegao.ui.lookup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.tegaoteam.application.tegao.domain.passing.DictionaryRelated
import com.tegaoteam.application.tegao.utils.EventBeacon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

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

    //Dictionary available
//    val availableDicts = DictionaryRelated.getDictionariesList()
    val sources = DictionaryRelated.getSupportedApi()

    private var _indevRetrofitResult = MutableLiveData<String>()
    val retrofitResult: LiveData<String> = _indevRetrofitResult

    //Start search on selected source
    val evStartSearch = EventBeacon()
    fun searchKeyword() {
        _indevRetrofitResult.value = "testing"
        //TODO
//        ioScope.launch {
//
//        }
    }

    //when viewModel being cleared (activity dismiss)
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}