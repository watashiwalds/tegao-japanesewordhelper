package com.tegaoteam.application.tegao.ui.learning.cardmanage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tegaoteam.application.tegao.data.model.asFlow
import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.domain.model.CardGroup
import com.tegaoteam.application.tegao.domain.repo.LearningRepo
import com.tegaoteam.application.tegao.utils.EventBeacon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CardManageActivityViewModel(private val _learningRepo: LearningRepo): ViewModel() {
    val cardGroups = _learningRepo.getCardGroups().asFlow().asLiveData()

    //region Cards by groupId data fetching
    private val _cardsOfGroup = mutableMapOf<Long, LiveData<List<CardEntry>>>()
//    val cardsOfGroup: Map<Long, LiveData<List<CardEntry>>> = _cardsOfGroup
    val eventCardsOfGroupUpdated = EventBeacon()
    fun fetchCardsOfGroupLiveData(groupId: Long): LiveData<List<CardEntry>> {
        if (_cardsOfGroup[groupId] != null) return _cardsOfGroup[groupId]!!
        val liveData = _learningRepo.getCardsByGroupId(groupId).asFlow().asLiveData()
        _cardsOfGroup[groupId] = liveData
        eventCardsOfGroupUpdated.ignite()
        return liveData
    }
    //endregion

    //region suspend C_UD function of LearningRepo
    private val stateDelete = MutableLiveData<Int>(null)
    fun deleteGroup(groupId: Long) {
        if (stateDelete.value == STATUS_PROCESSING) return
        stateDelete.value = STATUS_PROCESSING
        viewModelScope.launch {
            val res = _learningRepo.deleteCardGroupById(groupId)
            withContext(Dispatchers.Main) {
                stateDelete.value = res
            }
        }
    }
    fun addNewCardGroup(groupName: String) {
        if (groupName.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            _learningRepo.upsertCardGroup(CardGroup(0, groupName))
        }
    }
    fun updateCardGroup(cardGroup: CardGroup) {
        if (cardGroups.value?.firstOrNull{ it.groupId == cardGroup.groupId } == null) return
        viewModelScope.launch(Dispatchers.Default) {
            _learningRepo.upsertCardGroup(cardGroup)
        }
    }
    fun deleteCard(cardId: Long) {
        if (stateDelete.value == STATUS_PROCESSING) return
        stateDelete.value = STATUS_PROCESSING
        viewModelScope.launch {
            val res = _learningRepo.deleteCardById(cardId)
            withContext(Dispatchers.Main) {
                stateDelete.value = res
            }
        }
    }
    //endregion

    companion object {
        const val STATUS_PROCESSING = 0
        const val STATUS_SUCCESS = 1
        const val STATUS_FAILURE = -1

        class ViewModelFactory(
            private val learningRepo: LearningRepo
        ) : ViewModelProvider.Factory {
            @Suppress("unchecked_cast")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CardManageActivityViewModel::class.java)) {
                    return CardManageActivityViewModel(learningRepo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}