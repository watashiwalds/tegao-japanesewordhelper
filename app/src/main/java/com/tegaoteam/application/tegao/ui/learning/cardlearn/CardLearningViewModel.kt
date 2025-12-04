package com.tegaoteam.application.tegao.ui.learning.cardlearn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tegaoteam.application.tegao.data.model.asFlow
import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.domain.model.CardRepeat
import com.tegaoteam.application.tegao.domain.repo.LearningRepo
import com.tegaoteam.application.tegao.ui.learning.cardlearn.model.LearnCardInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

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

    //region Learning config finishing its work and other related things
    var newCards = 0
        private set
    var dueCards = 0
        private set
    var noRatingMode = false
        private set
    fun submitConfigs(newCards: Int, dueCards: Int, noRatingMode: Boolean) {
        this.newCards = newCards
        this.dueCards = dueCards
        this.noRatingMode = noRatingMode
    }
    fun streakCheckIn() {
        viewModelScope.launch{
            _learningRepo.streakCheckIn()
        }
    }
    //endregion

    //region Make a card deck of this learning session
    private val _sessionCards = MutableLiveData<List<CardEntry>>()
    val sessionCards: LiveData<List<CardEntry>> = _sessionCards
    private val _sessionRepeats = MutableLiveData<List<CardRepeat>>()
    val sessionRepeats: LiveData<List<CardRepeat>> = _sessionRepeats
    fun fetchSessionData() {
        viewModelScope.launch {
            val newCardIds = _learnableCardsStatus.value!!.filter { it.status == LearnCardInfo.STATUS_NEW }.map { it.cardId }
            val dueCardIds = _learnableCardsStatus.value!!.filter { it.status == LearnCardInfo.STATUS_DUE }.map { it.cardId }
            val randNew = newCardIds.shuffled().take(newCards)
            val randDue = dueCardIds.shuffled().take(dueCards)
            val learnCardIds = mutableListOf<Long>().apply {
                addAll(randNew)
                addAll(randDue)
                shuffled()
            }
            val learnCardEntries = mutableListOf<CardEntry>()
            learnCardIds.forEach {
                learnCardEntries.add(_learningRepo.getCardByCardId(it).asFlow().first())
            }
            withContext(Dispatchers.Main) {
                _sessionRepeats.value = _learnableCardRepeats.value?.filter { it.cardId in learnCardIds }
                _sessionCards.value = learnCardEntries
            }
        }
    }
    //endregion

    //region Update repeat on request of learningRun
    fun updateRepeatToDatabase(rpt: CardRepeat) {
        _sessionRepeats.value?.find { it.cardId == rpt.cardId }?.let {
            viewModelScope.launch {
                _learningRepo.updateRepeatTime(rpt)
            }
        }
    }
    //endregion

    //region Values for learning session metrics to use for visualizing (from learningRun)
    var repeatedReview: Int = 0
    var rememberedReview: Int = 0
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