package com.tegaoteam.application.tegao.ui.lookup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.tegaoteam.application.tegao.utils.EventBeacon

class LookupActivityViewModel(app: Application): AndroidViewModel(app) {
    private var _userSearchString = MutableLiveData<String>()

    val evClearSearchString = EventBeacon()

    val enableClearSearchString = _userSearchString.map { !it.isNullOrBlank() }

    fun setSearchString(s: String) {
        _userSearchString.value = s
    }
}