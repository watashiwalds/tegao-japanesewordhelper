package com.tegaoteam.application.tegao.ui.learning.cardlearn.learnrun

import androidx.lifecycle.ViewModel
import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.domain.model.CardRepeat

class LearningSessionRunViewModel: ViewModel() {
    var sessionMode: Int = -1

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
    fun popNextCardOrNull(): CardEntry? {
        val res = if (_sessionCards.isNotEmpty()) _sessionCards.removeAt(0) else null
        return res
    }
}