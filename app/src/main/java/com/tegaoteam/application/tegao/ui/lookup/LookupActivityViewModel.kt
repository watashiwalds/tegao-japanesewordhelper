package com.tegaoteam.application.tegao.ui.lookup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map

class LookupActivityViewModel(app: Application): AndroidViewModel(app) {
    private var _userSearchString = MutableLiveData<String>()

    private var _evClearSearchString = MutableLiveData<Boolean>()
    val evClearSearchString: LiveData<Boolean>
        get() = _evClearSearchString

    val enableClearSearchString = _userSearchString.map { !it.isNullOrBlank() }

    fun setSearchString(s: String) {
        _userSearchString.value = s
    }

    fun startClearSearchString() { _evClearSearchString.value = true }
    fun finClearSearchString() { _evClearSearchString.value = false }
}