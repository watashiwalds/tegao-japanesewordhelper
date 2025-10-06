package com.tegaoteam.application.tegao.ui.homescreen.lookup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class LookupFragmentViewModel(app: Application): AndroidViewModel(app) {
    private var _navigateToLookup = MutableLiveData<Boolean>()
    val navigateToLookup: LiveData<Boolean>
        get() = _navigateToLookup

    fun startNavigateToLookup() { _navigateToLookup.value = true }
    fun finishNavigateToLookup() { _navigateToLookup.value = false }
}