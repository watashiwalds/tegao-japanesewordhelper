package com.tegaoteam.application.tegao.ui.learning.cardcreate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.tegaoteam.application.tegao.data.model.asFlow
import com.tegaoteam.application.tegao.domain.repo.LearningRepo
import com.tegaoteam.application.tegao.ui.learning.cardcreate.model.CardMaterial

class CardCreateActivityViewModel(private val learningRepo: LearningRepo): ViewModel() {
    private val _cardMaterial = MutableLiveData<CardMaterial>().apply { value = null }
    val cardMaterial: LiveData<CardMaterial> = _cardMaterial
    fun postCardContentMaterial(contentMaterial: CardMaterial?) {
        if (_cardMaterial.value == null) _cardMaterial.value = contentMaterial
    }

    val cardGroups = learningRepo.getCardGroups().asFlow().asLiveData()

    companion object {
        class ViewModelFactory(
            private val learningRepo: LearningRepo
        ) : ViewModelProvider.Factory {
            @Suppress("unchecked_cast")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CardCreateActivityViewModel::class.java)) {
                    return CardCreateActivityViewModel(learningRepo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}