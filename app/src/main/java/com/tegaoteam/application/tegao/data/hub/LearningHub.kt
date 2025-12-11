package com.tegaoteam.application.tegao.data.hub

import com.tegaoteam.application.tegao.data.config.datastore.LearningConfig
import com.tegaoteam.application.tegao.data.database.SQLiteDatabase
import com.tegaoteam.application.tegao.data.database.learningcard.CardEntity
import com.tegaoteam.application.tegao.data.database.learningcard.CardGroupEntity
import com.tegaoteam.application.tegao.data.database.learningcard.CardRepeatEntity
import com.tegaoteam.application.tegao.data.model.FlowStream
import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.domain.model.CardGroup
import com.tegaoteam.application.tegao.domain.model.CardRepeat
import com.tegaoteam.application.tegao.domain.repo.LearningRepo
import kotlinx.coroutines.flow.map

class LearningHub: LearningRepo {
    private val _srsDb = SQLiteDatabase.getInstance().learningCardDAO
    //region card crud(s)
    override fun getCardGroups(): FlowStream<List<CardGroup>>
            = FlowStream(_srsDb.getCardGroups().map { it.map { item -> CardGroupEntity.toDomainCardGroup(item) } })
    override fun getCardGroupByGroupId(groupId: Long): FlowStream<CardGroup>
            = FlowStream(_srsDb.getCardGroupByGroupId(groupId).map { item -> CardGroupEntity.toDomainCardGroup(item) })
    override fun getCardsByGroupId(groupId: Long): FlowStream<List<CardEntry>>
        = FlowStream(_srsDb.getCardsByGroupId(groupId).map { it.map { item -> CardEntity.toDomainCardEntry(item) } })
    override fun getCardByCardId(cardId: Long): FlowStream<CardEntry>
        = FlowStream(_srsDb.getCardByCardId(cardId).map { CardEntity.toDomainCardEntry(it) })
    override fun getCardRepeatsByCardIds(cardIds: List<Long>): FlowStream<List<CardRepeat>>
        = FlowStream(_srsDb.getCardRepeatsByCardIds(cardIds).map { it.map { item -> CardRepeatEntity.toDomainCardRepeat(item) } })
    override fun getCardRepeatsByGroupId(groupId: Long): FlowStream<List<CardRepeat>>
        = FlowStream(_srsDb.getCardRepeatsByGroupId(groupId).map { it.map { item -> CardRepeatEntity.toDomainCardRepeat(item) } })

    override suspend fun upsertCardGroup(newGroup: CardGroup): Long
        = _srsDb.upsertCardGroup(CardGroupEntity.fromDomainCardGroup(newGroup))
    override suspend fun upsertCard(newCard: CardEntry): Long
        = _srsDb.upsertCard(CardEntity.fromDomainCardEntry(newCard))
    override suspend fun deleteCardGroupById(groupId: Long): Int
        = _srsDb.deleteCardGroupById(groupId)
    override suspend fun deleteCardById(cardId: Long): Int
        = _srsDb.deleteCardById(cardId)

    override suspend fun updateRepeatTime(cardRepeat: CardRepeat): Long
        = _srsDb.upsertCardRepeat(CardRepeatEntity.fromDomainCardRepeat(cardRepeat))
    override fun getTodayDueCardIds(nowDate: String): FlowStream<List<Long>>
        = FlowStream(_srsDb.getTodayDueCardIds(nowDate))
    //endregion

    private val _learningConfig = LearningConfig
    //region streak manager
    override fun currentStreak(): FlowStream<Long> = FlowStream(_learningConfig.currentStreak)
    override fun highestStreak(): FlowStream<Long> = FlowStream(_learningConfig.highestStreak)
    override suspend fun streakCheckIn() { _learningConfig.streakCheckIn() }
    override suspend fun streakLaunchCheck() { _learningConfig.streakLaunchCheck() }

    //endregion
}