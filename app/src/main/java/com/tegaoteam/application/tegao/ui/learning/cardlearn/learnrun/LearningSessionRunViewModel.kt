package com.tegaoteam.application.tegao.ui.learning.cardlearn.learnrun

import androidx.lifecycle.ViewModel
import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.domain.model.CardRepeat

class LearningSessionRunViewModel: ViewModel() {
    var sessionMode: Int = -1

    //region Init and non-editorial functions to session data (entry and repeat)
    private val _sessionCards = mutableListOf<CardEntry>()
    val sessionCards: List<CardEntry> = _sessionCards
    private val _sessionRepeats = mutableMapOf<Long, CardRepeat>()
    val sessionRepeats: Map<Long, CardRepeat> = _sessionRepeats

    fun submitSessionData(cards: List<CardEntry>, repeatList: List<CardRepeat>) {
        if (_sessionCards.size + _sessionRepeats.size == 0) {
            _sessionCards.addAll(cards)
            repeatList.forEach { _sessionRepeats[it.cardId] = it }
        }
    }
    fun getRepeatByCardId(cardId: Long): CardRepeat? = _sessionRepeats[cardId]
    //endregion

    //region During learning editorial functions
    fun nextCardOrNull(): CardEntry? = _sessionCards.firstOrNull()
    fun popTopCard(): CardEntry? {
        val res = if (_sessionCards.isNotEmpty()) _sessionCards.removeAt(0) else null
        return res
    }

    fun updateRepeat(rpt: CardRepeat) {
        _sessionRepeats[rpt.cardId] = rpt
    }
    //endregion

    //region Information text during learning (counter, etc...)

    //endregion
}