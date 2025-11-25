package com.tegaoteam.application.tegao.data.database.learningcard

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningCardDAO {
    //region [GET functions]
    @Query("""select * from ${CardGroupEntity.TABLE_NAME}""")
    fun getCardGroups(): Flow<List<CardGroupEntity>>

    @Query("""select * from ${CardEntity.TABLE_NAME} where ${CardGroupEntity.COL_ID} = :groupId""")
    fun getCardsByGroupId(groupId: Long): Flow<List<CardEntity>>

    @Query("""select * from ${CardEntity.TABLE_NAME} where ${CardEntity.COL_ID} = :cardId""")
    fun getCardByCardId(cardId: Long): Flow<CardEntity>

    @Query("""select * from ${CardRepeatEntity.TABLE_NAME} where ${CardEntity.COL_ID} in (:cardIds)""")
    fun getCardRepeatsByCardIds(cardIds: List<Long>): Flow<List<CardRepeatEntity>>
    //endregion

    //region [INSERT/UPSERT functions]
    @Upsert
    suspend fun upsertCardGroup(newGroup: CardGroupEntity): Long

    @Upsert
    suspend fun rawUpsertCard(newCard: CardEntity): Long
    @Transaction
    suspend fun upsertCard(newCard: CardEntity): Long {
        val cardUpsertRes = rawUpsertCard(newCard)
        if (cardUpsertRes != -1L) upsertCardRepeat(CardRepeatEntity(newCard.cardId))
        return cardUpsertRes
    }

    @Upsert
    suspend fun upsertCardRepeat(cardRepeat: CardRepeatEntity): Long
    //endregion

    //region [Raw functions to form TRANSACTION for respective DELETE functions]
    @Query("""delete from ${CardGroupEntity.TABLE_NAME} where ${CardGroupEntity.COL_ID} = :groupId""")
    suspend fun rawDeleteCardGroupById(groupId: Long): Int
    @Query("""select ${CardEntity.COL_ID} from ${CardEntity.TABLE_NAME} where ${CardGroupEntity.COL_ID} = :groupId""")
    fun rawGetCardIdsByGroupId(groupId: Long): List<Long>
    @Query("""delete from ${CardRepeatEntity.TABLE_NAME} where ${CardEntity.COL_ID} in (:cardIds)""")
    suspend fun rawDeleteCardRepeatsByCardIds(cardIds: List<Long>): Int
    @Query("""delete from ${CardEntity.TABLE_NAME} where ${CardEntity.COL_ID} in (:cardIds)""")
    suspend fun rawDeleteCardsByIds(cardIds: List<Long>): Int

    @Transaction
    suspend fun deleteCardGroupById(groupId: Long): Int {
        val relatedCardIds = rawGetCardIdsByGroupId(groupId)
        if (relatedCardIds.isEmpty()) return rawDeleteCardGroupById(groupId)
        rawDeleteCardRepeatsByCardIds(relatedCardIds)
        return rawDeleteCardsByIds(relatedCardIds) + 1
    }

    @Transaction
    suspend fun deleteCardById(cardId: Long): Int {
        rawDeleteCardRepeatsByCardIds(listOf(cardId))
        return rawDeleteCardsByIds(listOf(cardId))
    }
    //endregion

    //region [Complementary functions to serve various niche query of UI]
    @Query("""
        with CardIds as (
            select ${CardEntity.COL_ID} as related from ${CardEntity.TABLE_NAME}
            where ${CardGroupEntity.COL_ID} = :groupId
        )
        select * from ${CardRepeatEntity.TABLE_NAME}
        inner join CardIds on ${CardEntity.COL_ID} = related
    """)
    fun getCardRepeatsByGroupId(groupId: Long): Flow<List<CardRepeatEntity>>

    @Query("""
        select ${CardEntity.COL_ID} from ${CardRepeatEntity.TABLE_NAME}
        where ${CardRepeatEntity.COL_NEXTREPEAT} not null and ${CardRepeatEntity.COL_NEXTREPEAT} < :nowDate 
    """)
    fun getTodayDueCardIds(nowDate: String): Flow<List<Long>>
    //endregion
}