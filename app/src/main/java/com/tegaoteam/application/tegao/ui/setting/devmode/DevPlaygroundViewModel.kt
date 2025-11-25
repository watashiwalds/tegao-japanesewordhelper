package com.tegaoteam.application.tegao.ui.setting.devmode

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tegaoteam.application.tegao.data.hub.LearningHub
import com.tegaoteam.application.tegao.data.model.asFlow
import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.domain.model.CardRepeat
import com.tegaoteam.application.tegao.domain.repo.LearningRepo
import com.tegaoteam.application.tegao.utils.EventBeacon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class DevPlaygroundViewModel: ViewModel() {
    val learningRepo: LearningRepo = LearningHub()
    val groups = learningRepo.getCardGroups().asFlow().asLiveData()
    val queriedCardRepeat = MutableLiveData<List<CardRepeat>>()

    fun queryCardsByGroupId(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val cards = learningRepo.getCardRepeatsByGroupId(id).asFlow().first()
            withContext(Dispatchers.Main) {
                queriedCardRepeat.value = cards
            }
        }
    }

    init {
        Timber.i("Slaughter start")
    }

    val evMakePrint = EventBeacon()
}