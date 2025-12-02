package com.tegaoteam.application.tegao.ui.learning.cardlearn.fragment

import androidx.lifecycle.ViewModel
import com.tegaoteam.application.tegao.domain.model.CardEntry

class LearningSessionRunViewModel: ViewModel() {
    var sessionDeck = mutableListOf<CardEntry>()
}