package com.tegaoteam.application.tegao.data.database.learningcard

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningCardDAO {
    //region [Normal GET functions]
    @Query("""select * from ${CardGroupEntity.TABLE_NAME}""")
    fun getCardGroups(): Flow<List<CardGroupEntity>>

    @Query("""select * from ${CardEntity.TABLE_NAME} where ${CardGroupEntity.COL_ID} = :groupId""")
    fun getCardsByGroupId(groupId: Long): Flow<List<CardEntity>>

    @Query("""select * from ${CardRepeatEntity.TABLE_NAME} where ${CardEntity.COL_ID} in (:cardIds)""")
    fun getCardRepeatsByCardIds(cardIds: List<Long>): Flow<List<CardRepeatEntity>>
    //endregion

    //region [Normal INSERT/UPSERT functions]
    @Upsert
    suspend fun upsertCardGroup(newGroup: CardGroupEntity): Long

    @Upsert
    suspend fun upsertCard(newCard: CardEntity): Long

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
}