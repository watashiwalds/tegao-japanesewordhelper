package com.tegaoteam.application.tegao.ui.lookup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.tegaoteam.application.tegao.data.configs.DictionaryConfig
import com.tegaoteam.application.tegao.utils.EventBeacon

class LookupActivityViewModel(app: Application): AndroidViewModel(app) {

    //Search string using when tap Search button
    private var _userSearchString = MutableLiveData<String>()

    fun setSearchString(s: String) {
        _userSearchString.value = s
    }

    //Clear search string QOL function
    val evClearSearchString = EventBeacon()

    val enableClearSearchString = _userSearchString.map { !it.isNullOrBlank() }

    //Dictionary available
    val availableDicts = DictionaryConfig.getDictionariesList()
}