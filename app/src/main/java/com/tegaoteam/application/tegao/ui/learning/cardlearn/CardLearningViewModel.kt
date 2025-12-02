package com.tegaoteam.application.tegao.ui.learning.cardlearn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tegaoteam.application.tegao.data.model.asFlow
import com.tegaoteam.application.tegao.domain.model.CardRepeat
import com.tegaoteam.application.tegao.domain.repo.LearningRepo
import com.tegaoteam.application.tegao.ui.learning.cardlearn.model.LearnCardInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CardLearningViewModel(private val _learningRepo: LearningRepo): ViewModel() {
    val cardGroups = _learningRepo.getCardGroups().asFlow().asLiveData()
    var learnCardGroupId = CardLearningActivityGate.GROUP_ALLGROUP

    //region Fetching card repeats and transform to card status by passed groupId (All | 1)
    private val _learnableCardRepeats = MutableLiveData<List<CardRepeat>>()
    val learnableCardRepeats: LiveData<List<CardRepeat>> = _learnableCardRepeats
    private val _learnableCardsStatus = MutableLiveData<List<LearnCardInfo>>()
    val learnableCardsStatus: LiveData<List<LearnCardInfo>> = _learnableCardsStatus
    fun fetchLearnableCards() {
        viewModelScope.launch {
            val learnable = mutableListOf<CardRepeat>()
            val queryGroups = if (learnCardGroupId == CardLearningActivityGate.GROUP_ALLGROUP) {
                cardGroups.value?.map { it.groupId }?: _learningRepo.getCardGroups().asFlow().first().map { it.groupId }
            } else {
                listOf(learnCardGroupId)
            }
            queryGroups.forEach { groupId ->
                learnable.addAll(_learningRepo.getCardRepeatsByGroupId(groupId).asFlow().first()) }
            val statuses = learnable.map { LearnCardInfo.fromCardRepeat(it) }
            withContext(Dispatchers.Main) {
                _learnableCardRepeats.value = learnable
                _learnableCardsStatus.value = statuses
            }
        }
    }
    //endregion

    //region Keep learning session config values
    var newCards = -1
        private set
    var dueCards = -1
        private set
    var noRatingMode = false
        private set
    fun submitConfigs(newCards: Int, dueCards: Int, noRatingMode: Boolean) {
        this.newCards = newCards
        this.dueCards = dueCards
        this.noRatingMode = noRatingMode
    }
    //endregion

    companion object {
        class ViewModelFactory(
            private val learningRepo: LearningRepo
        ) : ViewModelProvider.Factory {
            @Suppress("unchecked_cast")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CardLearningViewModel::class.java)) {
                    return CardLearningViewModel(learningRepo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}