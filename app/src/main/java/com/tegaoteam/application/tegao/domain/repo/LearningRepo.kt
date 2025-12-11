package com.tegaoteam.application.tegao.domain.repo

import com.tegaoteam.application.tegao.domain.independency.Stream
import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.domain.model.CardGroup
import com.tegaoteam.application.tegao.domain.model.CardRepeat

interface LearningRepo {
    //region card crud(s)
    fun getCardGroups(): Stream<List<CardGroup>>
    fun getCardGroupByGroupId(groupId: Long): Stream<CardGroup>
    fun getCardsByGroupId(groupId: Long): Stream<List<CardEntry>>
    fun getCardByCardId(cardId: Long): Stream<CardEntry>
    fun getCardRepeatsByCardIds(cardIds: List<Long>): Stream<List<CardRepeat>>
    fun getCardRepeatsByGroupId(groupId: Long): Stream<List<CardRepeat>>

    suspend fun upsertCardGroup(newGroup: CardGroup): Long
    suspend fun upsertCard(newCard: CardEntry): Long
    suspend fun deleteCardGroupById(groupId: Long): Int
    suspend fun deleteCardById(cardId: Long): Int

    suspend fun updateRepeatTime(cardRepeat: CardRepeat): Long
    fun getTodayDueCardIds(nowDate: String): Stream<List<Long>>
    //endregion

    //region streak manager
    fun currentStreak(): Stream<Long>
    fun highestStreak(): Stream<Long>
    suspend fun streakCheckIn()
    suspend fun streakLaunchCheck()
    //endregion
}