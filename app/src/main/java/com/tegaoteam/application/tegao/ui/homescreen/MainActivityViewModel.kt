package com.tegaoteam.application.tegao.ui.homescreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {

    //fix non-sync visual of navBar with current displayed fragment (my bad I was using my own themedChip for this, ok?)
    private val _fragmentChangeId = MutableLiveData<String>()
    val fragmentChangeId: LiveData<String> = _fragmentChangeId
    fun fragmentChanged(fragId: String) { _fragmentChangeId.value = fragId }
}