package com.tegaoteam.application.tegao.ui.learning.cardlearn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tegaoteam.application.tegao.domain.repo.LearningRepo
import timber.log.Timber

class CardLearningViewModel(private val _learningRepo: LearningRepo): ViewModel() {
    fun fetchLearnableCardsByGroupId(groupId: Long) {
        // all group (groupId == 0)
        if (groupId == 0L) {
            Timber.i("Card learn receive 0 (All groups) as fetch input")
        }
        // specific group (groupId > 0)
        else {
            Timber.i("Card learn receive !0 ($groupId) as fetch input")
        }
    }

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