package com.tegaoteam.application.tegao.ui.learning.cardlearn.learnrun

import androidx.lifecycle.ViewModel
import com.tegaoteam.application.tegao.domain.model.CardEntry

class LearningSessionRunViewModel: ViewModel() {
    var sessionMode: Int = -1
    var sessionDeck = mutableListOf<CardEntry>()
}