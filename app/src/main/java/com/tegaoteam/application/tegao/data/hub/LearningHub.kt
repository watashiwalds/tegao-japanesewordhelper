package com.tegaoteam.application.tegao.data.hub

import com.tegaoteam.application.tegao.data.database.SQLiteDatabase
import com.tegaoteam.application.tegao.data.database.srscard.SRSCardEntity
import com.tegaoteam.application.tegao.data.database.srscard.SRSCardGroup
import com.tegaoteam.application.tegao.data.database.srscard.SRSCardRepeat
import com.tegaoteam.application.tegao.data.model.FlowStream
import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.domain.model.CardGroup
import com.tegaoteam.application.tegao.domain.model.CardRepeat
import com.tegaoteam.application.tegao.domain.repo.LearningRepo
import kotlinx.coroutines.flow.map

class LearningHub: LearningRepo {
    private val _srsDb = SQLiteDatabase.getInstance().srsCardDAO

    override fun getCardGroups(): FlowStream<List<CardGroup>>
        = FlowStream(_srsDb.getCardGroups().map { it.map { item -> SRSCardGroup.toDomainCardGroup(item) } })

    override fun getCardsByGroupId(groupId: Long): FlowStream<List<CardEntry>>
        = FlowStream(_srsDb.getCardsByGroupId(groupId).map { it.map { item -> SRSCardEntity.toDomainCardEntry(item) } })

    override fun getCardRepeatsByCardIds(cardIds: List<Long>): FlowStream<List<CardRepeat>>
        = FlowStream(_srsDb.getCardRepeatsByCardIds(cardIds).map { it.map { item -> SRSCardRepeat.toDomainCardRepeat(item) } })

    override suspend fun addCardGroup(newGroup: CardGroup): Long
        = _srsDb.upsertCardGroup(SRSCardGroup.fromDomainCardGroup(newGroup))

    override suspend fun addCard(newCard: CardEntry): Long
        = _srsDb.upsertCard(SRSCardEntity.fromDomainCardEntry(newCard))

    override suspend fun updateRepeatTime(cardRepeat: CardRepeat): Long
        = _srsDb.upsertCardRepeat(SRSCardRepeat.fromDomainCardRepeat(cardRepeat))

    override suspend fun deleteCardGroupById(groupId: Long): Int
        = _srsDb.deleteCardGroupById(groupId)

    override suspend fun deleteCardById(cardId: Long): Int
        = _srsDb.deleteCardById(cardId)
}