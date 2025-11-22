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
import com.tegaoteam.application.tegao.ui.learning.cardcreate.model.CardPlaceholder
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
    var selectedFronts: List<Pair<String, Int>>? = null
        private set
    fun submitSelectedFront(frontContent: List<Pair<String, Int>>) {
        selectedFronts = frontContent.ifEmpty { null }
    }
    //endregion

    //region [SetAnswer]
    var selectedAnswer: String? = null
        private set
    fun submitSelectedAnswer(answer: String?) {
        if (selectedType == LearningCardConst.Type.TYPE_ANSWERCARD.id) selectedAnswer = answer
    }
    //endregion

    //region [SetBack]
    var selectedBacks: List<Pair<String, Int>>? = null
        private set
    fun submitSelectedBack(backContent: List<Pair<String, Int>>) {
        selectedBacks = backContent.ifEmpty { null }
    }
    //endregion

    var parsedCardPlaceholder: CardPlaceholder? = null
    fun parsingCardPlaceholder() {
        parsedCardPlaceholder = CardPlaceholder.parseFromSelectedMaterials(
            materials = cardMaterial.value?: CardMaterial(mapOf()),
            groupIds = selectedGroupIds,
            type = selectedType?: LearningCardConst.Type.TYPE_FLASHCARD.id,
            frontKeys = selectedFronts?: listOf(),
            answer = selectedAnswer,
            backKeys = selectedBacks?: listOf()
        )
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