package com.tegaoteam.application.tegao.ui.learning.cardcreate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tegaoteam.application.tegao.domain.repo.LearningRepo
import com.tegaoteam.application.tegao.ui.learning.cardcreate.model.CardContentMaterial

class CardCreateActivityViewModel(private val learningRepo: LearningRepo): ViewModel() {
    private val _cardContentMaterial = MutableLiveData<CardContentMaterial>().apply { value = null }
    val cardContentMaterial: LiveData<CardContentMaterial> = _cardContentMaterial
    fun postCardContentMaterial(contentMaterial: CardContentMaterial?) {
        if (_cardContentMaterial.value == null) _cardContentMaterial.value = contentMaterial
    }

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