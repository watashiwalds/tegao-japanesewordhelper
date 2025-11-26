package com.tegaoteam.application.tegao.ui.learning.cardmanage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tegaoteam.application.tegao.domain.repo.LearningRepo

class CardManageActivityViewModel(private val _learningRepo: LearningRepo): ViewModel() {

    companion object {
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