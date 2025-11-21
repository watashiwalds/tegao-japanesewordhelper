package com.tegaoteam.application.tegao.ui.learning.cardcreate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tegaoteam.application.tegao.data.model.asFlow
import com.tegaoteam.application.tegao.domain.model.CardGroup
import com.tegaoteam.application.tegao.domain.repo.LearningRepo
import com.tegaoteam.application.tegao.ui.learning.LearningCardConst
import com.tegaoteam.application.tegao.ui.learning.cardcreate.model.CardMaterial
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CardCreateActivityViewModel(private val learningRepo: LearningRepo): ViewModel() {
    //region [Pre-start, fetch materials for card creation from Word/Kanji passed into]
    private val _cardMaterial = MutableLiveData<CardMaterial>().apply { value = null }
    val cardMaterial: LiveData<CardMaterial> = _cardMaterial
    fun postCardContentMaterial(contentMaterial: CardMaterial?) {
        if (_cardMaterial.value == null) _cardMaterial.value = contentMaterial
    }
    //endregion

    //region [SetGroup]
    val cardGroups = learningRepo.getCardGroups().asFlow().asLiveData()
    fun addNewCardGroup(groupName: String) {
        if (groupName.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            learningRepo.addCardGroup(CardGroup(0, groupName))
        }
    }
    private val _selectedGroupIds = mutableListOf<Long>()
    val selectedGroupIds: List<Long> = _selectedGroupIds
    fun submitSelectedGroupIds(groupIds: List<Long>) {
        _selectedGroupIds.apply {
            clear()
            addAll(groupIds)
        }
    }
    //endregion

    //region [SetType]
    val cardTypeChipItems = LearningCardConst.Type.entries.map { it.id to it.display }
    var selectedType: Int? = null
        private set
    fun submitSelectedType(type: Int) {
        selectedType = if (type in cardTypeChipItems.map { it.first }) type else null
    }
    //endregion

    //region [SetFront]
    var selectedFronts: List<Pair<String, String>>? = null
        private set
    fun submitSelectedFront(frontContent: List<Pair<String, String>>) {
        selectedFronts = frontContent.ifEmpty { null }
    }
    //endregion

    //region [SetAnswer]

    //endregion

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