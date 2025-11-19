package com.tegaoteam.application.tegao.domain.repo

import com.tegaoteam.application.tegao.domain.independency.Stream
import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.domain.model.CardGroup
import com.tegaoteam.application.tegao.domain.model.CardRepeat

interface LearningRepo {
    fun getCardGroups(): Stream<List<CardGroup>>
    fun getCardsByGroupId(groupId: Long): Stream<List<CardEntry>>
    fun getCardRepeatsByCardIds(cardIds: List<Long>): Stream<List<CardRepeat>>

    suspend fun addCardGroup(newGroup: CardGroup): Long
    suspend fun addCard(newCard: CardEntry): Long

    suspend fun updateRepeatTime(cardRepeat: CardRepeat): Long

    suspend fun deleteCardGroupById(groupId: Long): Int
    suspend fun deleteCardById(cardId: Long): Int
}